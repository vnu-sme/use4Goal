# Bước 2 — Thiết kế AST (Abstract Syntax Tree)

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/ast/
```

## Vai trò của AST

AST (Concrete Syntax — CS) là lớp trung gian giữa ParseTree (ANTLR) và MM runtime. Nó:
- Ánh xạ gần 1-1 với cấu trúc grammar
- Lưu thông tin đủ để xây dựng MM (bao gồm source text thô của condition/expression)
- Không chứa logic semantic
- Là immutable sau khi Visitor build xong

> **Lưu ý codebase thực tế**: Plugin `MAXGoal` trong project này không có tầng AST riêng
> — `MAXGoalBuildingVisitor` xây MM trực tiếp từ ParseTree. Đây là pattern hợp lệ cho
> ngôn ngữ đơn giản. Tầng AST riêng chỉ cần khi: grammar phức tạp, nhiều ngôn ngữ nhúng
> (như OCL-in-GOAL), hoặc cần reuse AST cho nhiều mục đích khác nhau.

---

## Khi nào cần AST riêng vs không cần?

| Tình huống | Nên có AST riêng? |
|------------|-------------------|
| Ngôn ngữ đơn giản, 1 pipeline | Không cần — Visitor → MM thẳng |
| Có nhiều ngôn ngữ nhúng (OCL, condition) | Có — AST riêng cho từng sub-language |
| Cần multiple passes (validate rồi mới build MM) | Có |
| Cần serialize/deserialize parse result | Có |
| Grammar thay đổi thường xuyên | Có — cách ly MM khỏi grammar changes |

---

## Pattern 1: Không có AST riêng (MAXGoal pattern)

`MAXGoalBuildingVisitor` xây dựng MM objects trực tiếp:

```java
public final class MAXGoalBuildingVisitor extends MAXGoalBaseVisitor<Object> {

    private MAXGoalModel model;

    public static MAXGoalModel build(MAXGoalParser.ModelContext ctx) {
        MAXGoalBuildingVisitor v = new MAXGoalBuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    @Override
    public Object visitModel(MAXGoalParser.ModelContext ctx) {
        model = new MAXGoalModel(ctx.IDENT().getText());
        for (MAXGoalParser.ActorDefContext a : ctx.actorDef())
            model.addActor((Actor) visitActorDef(a));
        if (ctx.dependBlock() != null) visitDependBlock(ctx.dependBlock());
        return model;
    }

    @Override
    public Object visitGoalDecl(MAXGoalParser.GoalDeclContext ctx) {
        String actorName = currentActorName(ctx);
        String goalName  = ctx.IDENT().getText();
        String clause = null, expr = null;
        RefineSpec refine = null;

        for (MAXGoalParser.GoalAttrContext attr : ctx.goalAttr()) {
            if (attr instanceof MAXGoalParser.GaAchieveContext ac) {
                clause = "achieve"; expr = cond(ac.condition());
            } else if (attr instanceof MAXGoalParser.GaRefineContext rf) {
                refine = (RefineSpec) visit(rf.refineSpec());
            }
            // ... các case khác
        }
        return new GoalDef(goalName, actorName, clause, expr, refine);
    }

    // Capture raw text của condition expression
    private static String cond(MAXGoalParser.ConditionContext ctx) {
        if (ctx == null) return "";
        Interval i = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        return ctx.start.getInputStream().getText(i);
    }

    // Helper: tìm tên actor từ ancestor context
    private String currentActorName(org.antlr.v4.runtime.ParserRuleContext ctx) {
        org.antlr.v4.runtime.ParserRuleContext p = ctx.getParent();
        while (p != null) {
            if (p instanceof MAXGoalParser.ActorDefContext a) return a.IDENT().getText();
            p = p.getParent();
        }
        return "";
    }
}
```

---

## Pattern 2: Có AST riêng (GOAL plugin đầy đủ)

Khi cần tầng AST riêng, dùng classes với hậu tố `CS`:

### Root model

```java
package org.vnu.sme.<plugin>.ast;

public final class <Lang>ModelCS {
    private final String             name;
    private final List<ActorCS>      actors;
    private final List<DependencyCS> dependencies;

    public <Lang>ModelCS(String name, List<ActorCS> actors,
                         List<DependencyCS> dependencies) {
        this.name         = name;
        this.actors       = List.copyOf(actors);
        this.dependencies = List.copyOf(dependencies);
    }

    public String             name()         { return name; }
    public List<ActorCS>      actors()       { return actors; }
    public List<DependencyCS> dependencies() { return dependencies; }
}
```

### Actor CS

```java
public record ActorCS(
    String            name,
    String            kind,           // "agent"|"role"|"position"
    List<IntentCS>    intentionals
) {}
```

### Intentional element hierarchy (sealed)

```java
public sealed interface IntentCS
        permits GoalCS, TaskCS, ResourceCS {
    String name();
}

public record GoalCS(
    String   name,
    String   clause,    // "achieve"|"maintain"|"avoid"|null
    String   condText,  // raw text từ condition rule
    RefineCS refine     // null nếu không có
) implements IntentCS {}

public record TaskCS(
    String   name,
    String   preText,
    String   postText,
    String   needby,
    RefineCS refine
) implements IntentCS {}

public record ResourceCS(
    String name,
    String kind    // "data"|"service"|"physical"
) implements IntentCS {}
```

### Refinement CS

```java
public sealed interface RefineCS
        permits SeqRefineCS, ParRefineCS, XorRefineCS,
                IorRefineCS, IterRefineCS {
}

public record SeqRefineCS(List<String> children) implements RefineCS {}
public record ParRefineCS(List<String> children) implements RefineCS {}

public record GuardedChildCS(String condition, String childId) {}
public record XorRefineCS(List<GuardedChildCS> branches) implements RefineCS {}
public record IorRefineCS(List<GuardedChildCS> branches) implements RefineCS {}

public record IterRefineCS(
    List<String> body,
    String       until
) implements RefineCS {}
```

### Dependency CS

```java
public record DependencyCS(String from, String to) {}
```

---

## Visitor khi có AST riêng

```java
public final class <Lang>BuildingVisitor extends <LANG>BaseVisitor<<Lang>ModelCS> {

    @Override
    public <Lang>ModelCS visitModel(<LANG>Parser.ModelContext ctx) {
        List<ActorCS> actors = ctx.actorDef().stream()
            .map(this::visitActorDef)
            .collect(Collectors.toList());

        List<DependencyCS> deps = ctx.dependBlock() != null
            ? visitDependBlock(ctx.dependBlock())
            : Collections.emptyList();

        return new <Lang>ModelCS(ctx.IDENT().getText(), actors, deps);
    }

    public ActorCS visitActorDef(<LANG>Parser.ActorDefContext ctx) {
        String kind = ctx.actorKind().getText();
        List<IntentCS> items = ctx.intentional().stream()
            .map(i -> (IntentCS) visit(i))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        return new ActorCS(ctx.IDENT().getText(), kind, items);
    }

    @Override
    public GoalCS visitGoalDecl(<LANG>Parser.GoalDeclContext ctx) {
        // parse attributes
        String clause = null, condText = null;
        RefineCS refine = null;
        for (var attr : ctx.goalAttr()) {
            if (attr instanceof <LANG>Parser.GaAchieveContext ac) {
                clause = "achieve"; condText = rawText(ac.condition());
            } else if (attr instanceof <LANG>Parser.GaRefineContext rf) {
                refine = (RefineCS) visit(rf.refineSpec());
            }
        }
        return new GoalCS(ctx.IDENT().getText(), clause, condText, refine);
    }

    // ... các visitXxx khác

    private static String rawText(ParserRuleContext ctx) {
        if (ctx == null) return "";
        return ctx.start.getInputStream()
                   .getText(new Interval(ctx.start.getStartIndex(),
                                         ctx.stop.getStopIndex()));
    }
}
```

---

## Checklist bước 2

- [ ] Tất cả CS class/record nằm trong package `...ast`
- [ ] Tất cả tên kết thúc bằng `CS`
- [ ] Không có logic xử lý semantic trong CS (chỉ data)
- [ ] Dùng `List.copyOf()` hoặc `Collections.unmodifiableList()` để đảm bảo immutable
- [ ] Dùng Java record cho CS node đơn giản (không có behavior)
- [ ] Dùng sealed interface + record cho hierarchy (RefineCS, IntentCS)
- [ ] CS class không import gì từ `mm` package
- [ ] CS class không import Swing/AWT

## Lỗi thường gặp

| Lỗi | Nguyên nhân | Sửa |
|-----|-------------|-----|
| CS class có setter | CS phải immutable | Dùng record hoặc chỉ constructor + getter |
| CS class chứa logic | Sẽ làm khó test và bảo trì | Chuyển sang Factory (Bước 5) |
| Thiếu class CS cho rule | Visitor không biết map vào đâu | Thêm CS class tương ứng |
| CS import MM class | Vòng dependency | CS chỉ được import primitive/util |
| Record không có `List.copyOf` | List có thể bị mutate sau đó | Luôn `List.copyOf()` trong record constructor |
