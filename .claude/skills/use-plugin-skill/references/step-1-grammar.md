# Bước 1 — Thiết kế Grammar ANTLR4

## Vị trí file

```
src/main/resources/grammars/<LANG>.g4
```

Sau khi viết xong, chạy `mvn generate-sources` để sinh ra các class vào:
```
target/generated-sources/antlr4/
    <LANG>Lexer.java
    <LANG>Parser.java
    <LANG>Visitor.java          ← dùng ở Bước 5
    <LANG>BaseVisitor.java
    <LANG>Listener.java
    <LANG>BaseListener.java
```

> **KHÔNG chỉnh sửa file generated** — chỉ sửa `.g4` rồi regenerate.

---

## pom.xml: khai báo ANTLR4 plugin

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.antlr</groupId>
      <artifactId>antlr4-maven-plugin</artifactId>
      <version>4.13.1</version>
      <executions>
        <execution>
          <goals><goal>antlr4</goal></goals>
        </execution>
      </executions>
      <configuration>
        <sourceDirectory>${basedir}/src/main/resources/grammars</sourceDirectory>
        <outputDirectory>${project.build.directory}/generated-sources/antlr4</outputDirectory>
        <visitor>true</visitor>
        <listener>false</listener>
      </configuration>
    </plugin>
  </plugins>
</build>

<dependencies>
  <dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.13.1</version>
  </dependency>
</dependencies>
```

---

## Cấu trúc grammar chuẩn cho USE plugin

Mẫu dưới đây phản ánh cấu trúc của `MAXGoal.g4` và `GOAL.g4` trong project:

```antlr
grammar <LANG>;

// ─── Entry point ──────────────────────────────────────────────────────
model
    : 'model' IDENT '{' actorDef* dependBlock? '}' EOF
    ;

// ─── Actor (container cho intentional elements) ───────────────────────
actorDef
    : actorKind IDENT '{' intentional* '}'
    ;

actorKind
    : 'agent' | 'role' | 'position'
    ;

// ─── Intentional elements ─────────────────────────────────────────────
intentional
    : goalDecl
    | taskDecl
    | resourceDecl
    ;

goalDecl
    : 'goal' IDENT '{' goalAttr* '}'
    ;

goalAttr
    : 'achieve'  ':' condition                      # gaAchieve
    | 'maintain' ':' condition                      # gaMaintain
    | 'avoid'    ':' condition                      # gaAvoid
    | 'refine'   ':' refineSpec                     # gaRefine
    ;

taskDecl
    : 'task' IDENT '{' taskAttr* '}'
    ;

taskAttr
    : 'pre'    ':' condition                        # taPre
    | 'post'   ':' condition                        # taPost
    | 'refine' ':' refineSpec                       # taRefine
    | 'needby' ':' IDENT                            # taNeedby
    ;

resourceDecl
    : resourceKind IDENT ';'
    ;

resourceKind
    : 'data' | 'service' | 'physical'
    ;

// ─── Dependency block ─────────────────────────────────────────────────
dependBlock
    : 'depend' '{' depStmt* '}'
    ;

depStmt
    : IDENT '->' IDENT ';'
    ;

// ─── Refinement spec (sealed hierarchy trong MM) ──────────────────────
refineSpec
    : 'SEQ'  '[' IDENT (',' IDENT)* ']'             # seqRefine
    | 'PAR'  '[' IDENT (',' IDENT)* ']'             # parRefine
    | 'XOR'  '[' guardedChild (',' guardedChild)* ']' # xorRefine
    | 'IOR'  '[' guardedChild (',' guardedChild)* ']' # iorRefine
    | 'ITER' '[' IDENT (',' IDENT)* ']' 'until' condition # iterRefine
    ;

guardedChild
    : '(' condition ',' IDENT ')'
    ;

// ─── Condition (OCL expression hoặc raw text) ─────────────────────────
condition
    : '(' condInner ')'
    | condText
    ;

// Capture raw text để chuyển sang OCL sau
condText
    : ~(';' | '[' | ']' | ',' | '{' | '}')+
    ;

// ─── Tokens ───────────────────────────────────────────────────────────
IDENT       : [a-zA-Z_][a-zA-Z0-9_]* ;
INT         : [0-9]+ ;
REAL        : [0-9]+ '.' [0-9]+ ;
STRING      : '"' (~'"')* '"' ;
WS          : [ \t\r\n]+ -> skip ;
COMMENT     : '//' ~[\r\n]* -> skip ;
ML_COMMENT  : '/*' .*? '*/' -> skip ;
```

---

## Kỹ thuật capture raw text cho condition/expression

Trong `MAXGoalBuildingVisitor`, condition được capture bằng `Interval`:

```java
private static String cond(MAXGoalParser.ConditionContext ctx) {
    if (ctx == null) return "";
    Interval i = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
    return ctx.start.getInputStream().getText(i);
}
```

Đây là pattern đúng khi OCL expression phức tạp (không cần parse thành AST ngay — chỉ giữ text thô).

---

## Error Listener pattern (dùng trong Compiler)

```java
List<String> errors = new ArrayList<>();

ANTLRErrorListener errListener = new BaseErrorListener() {
    @Override
    public void syntaxError(Recognizer<?,?> rec, Object sym,
                            int line, int col,
                            String msg, RecognitionException e) {
        errors.add("line " + line + ":" + col + " " + msg);
    }
};

<LANG>Lexer  lexer  = new <LANG>Lexer(chars);
<LANG>Parser parser = new <LANG>Parser(new CommonTokenStream(lexer));

lexer.removeErrorListeners();
parser.removeErrorListeners();
lexer.addErrorListener(errListener);
parser.addErrorListener(errListener);

<LANG>Parser.ModelContext tree = parser.model();
if (!errors.isEmpty()) return new Result(null, errors);
```

> **Quan trọng**: Luôn `removeErrorListeners()` trước khi thêm listener riêng,
> không thì ANTLR vẫn in lỗi ra stderr song song.

---

## Template grammar theo loại ngôn ngữ

### Ngôn ngữ Goal/Actor (như MAXGoal)
Entry: `model → actorDef* → intentional* → goalDecl/taskDecl`
Xem `MAXGoal.g4` (trong `goal/target/classes/grammars/`) làm reference đầy đủ.

### Ngôn ngữ Process/BPMN
Entry: `model → process* → lane* → node* → flow*`
Xem `BPMN.g4` trong cùng thư mục.

### Ngôn ngữ Structural/Class
Entry: `model → classDecl* → attribute* → operation* → assoc*`
Tham khảo USE `use.g` trong `use-parser`.

---

## Labeled alternatives (`# label`)

Dùng khi một rule có nhiều alternative cần xử lý khác nhau trong Visitor:

```antlr
refineSpec
    : 'SEQ' '[' IDENT+ ']'   # seqRefine    // → visitSeqRefine()
    | 'PAR' '[' IDENT+ ']'   # parRefine    // → visitParRefine()
    | 'XOR' '[' ...  ']'     # xorRefine    // → visitXorRefine()
    ;
```

Nếu không có label, Visitor chỉ tạo `visitRefineSpec()` — không phân biệt được case.

---

## Checklist bước 1

- [ ] Grammar tên file khớp với tên grammar (`grammar <LANG>`)
- [ ] Rule đầu tiên là entry point `model`, kết thúc bằng `EOF`
- [ ] Mỗi concept quan trọng có rule riêng (không gộp vào 1 rule lớn)
- [ ] Tất cả keyword là literal string (`'goal'`, `'task'`), không phải token
- [ ] Dùng labeled alternatives (`# label`) cho mọi rule có 2+ alternative
- [ ] Có skip rule cho whitespace và comment (cả `//` và `/* */`)
- [ ] `mvn generate-sources` chạy thành công — 0 errors
- [ ] Generated classes nằm trong `target/generated-sources/antlr4/`
- [ ] `pom.xml` bật `<visitor>true</visitor>`

## Lỗi thường gặp

| Lỗi | Nguyên nhân | Sửa |
|-----|-------------|-----|
| `no viable alternative at input` | Rule ambiguous hoặc thiếu case | Dùng labeled alternatives |
| Token xung đột keyword vs IDENT | Keyword không phải literal | Đặt keyword literal `'goal'` thay vì token `GOAL` |
| `mvn` không sinh code | Thiếu antlr4-maven-plugin | Thêm đúng groupId `org.antlr` |
| Visitor chỉ có `visitRefineSpec` | Thiếu `# label` | Thêm `# seqRefine`, `# parRefine`, ... |
| ANTLR in lỗi ra stderr | Chưa removeErrorListeners | Gọi `removeErrorListeners()` trước khi add listener riêng |
