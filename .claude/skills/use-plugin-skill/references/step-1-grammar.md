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
    <LANG>Visitor.java          ← dùng ở Bước 2 (AST)
    <LANG>BaseVisitor.java
    <LANG>Listener.java
    <LANG>BaseListener.java
```

> **KHÔNG chỉnh sửa file generated** — chỉ sửa `.g4` rồi regenerate.

---

## Vì sao ANTLR4 1-file-1-grammar (khác USE core)

USE core (mã nguồn thật trong `use/`) dùng **ANTLR3** và ghép grammar từ nhiều mảnh `.gpart`
(`use/use-core/src/main/resources/grammars/{base,use,ocl,soil,...}/*.gpart`) vì
`.use`/`.ocl`/`.soil` **nhúng lẫn nhau** trong cùng 1 file — invariant trong `.use` là biểu
thức OCL, thân operation là SOIL. Plugin trong `goal/` không có nhu cầu đó: một ngôn ngữ mới
độc lập, không nhúng ngôn ngữ khác vào giữa cú pháp của nó — nên **ANTLR4, 1 file `.g4` độc
lập cho mỗi ngôn ngữ** là lựa chọn đúng, không cần bắt chước kỹ thuật ghép fragment của USE
core. Chi tiết: `doc/use-core-design-rules.md` mục 2.1.

---

## pom.xml: khai báo ANTLR4 plugin

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.antlr</groupId>
      <artifactId>antlr4-maven-plugin</artifactId>
      <configuration>
        <visitor>true</visitor>
        <listener>true</listener>
        <sourceDirectory>${project.basedir}/src/main/resources/grammars</sourceDirectory>
        <outputDirectory>${project.build.directory}/generated-sources/antlr4</outputDirectory>
      </configuration>
    </plugin>
  </plugins>
</build>
```

`goal/pom.xml` quét **toàn bộ** `.g4` trong `src/main/resources/grammars/` — không cần khai
báo tên file riêng lẻ. Thêm 1 ngôn ngữ mới chỉ cần thả `.g4` vào thư mục đó.

---

## Cấu trúc grammar chuẩn — khuôn mẫu chung

Entry point thường có dạng phân tầng: `model → declaration* → relation*` — khai báo các thực
thể (actor/pool/class/...) trước, quan hệ giữa chúng (dependency/flow/association/...) sau.
Dùng **labeled alternatives** (`# label`) cho mọi rule có nhiều alternative, để Visitor ở
Bước 2 sinh ra 1 method `visit<Label>()` riêng cho từng nhánh thay vì phải tự phân biệt bằng
tay trong 1 method `visit<Rule>()` gộp chung. Ví dụ khuôn cho 1 rule "thân thực thể" có nhiều
loại khai báo trộn lẫn:

```antlr
entityBody
    : 'kindA' IDENT ';'                              # kindAElem
    | 'kindB' IDENT ';'                              # kindBElem
    | 'relatesTo' IDENT '->' IDENT ';'                # relationElem
    | 'refines'   IDENT '<-' IDENT (',' IDENT)* ';'   # refineElem
    ;
```

Với ngôn ngữ dạng process/flow (nhiều loại node nối bằng cạnh có hướng), áp dụng cùng nguyên
tắc: mỗi loại node (event/task/gateway/...) là 1 alternative riêng có label, cạnh nối
(sequence/message/...) là rule riêng tham chiếu id của node bằng `IDENT` (resolve thành object
thật ở Bước 3 Factory, không resolve ngay trong grammar).

---

## Kỹ thuật capture raw text cho condition/expression

Khi 1 rule cần giữ nguyên văn bản gốc (ví dụ biểu thức điều kiện chưa cần parse ngay), dùng
`Interval` để lấy text thô từ input stream thay vì ghép lại token:

```java
private static String rawText(ParserRuleContext ctx) {
    if (ctx == null) return "";
    Interval i = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
    return ctx.start.getInputStream().getText(i);
}
```

---

## Error Listener pattern (dùng trong Compiler ở Bước 4)

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
```

> **Quan trọng**: Luôn `removeErrorListeners()` trước khi thêm listener riêng,
> không thì ANTLR vẫn in lỗi ra stderr song song, làm log lẫn lộn giữa lỗi thật và lỗi trùng lặp.

---

## Labeled alternatives (`# label`)

Dùng khi một rule có nhiều alternative cần xử lý khác nhau ở Bước 2 (AST Visitor):

```antlr
elementBody
    : 'goal' IDENT ';'   # goalElem     // → visitGoalElem()
    | 'task' IDENT ';'   # taskElem     // → visitTaskElem()
    ;
```

Nếu không có label, Visitor chỉ tạo `visitElementBody()` — không phân biệt được case.

---

## Checklist bước 1

- [ ] Grammar tên file khớp với tên grammar (`grammar <LANG>`)
- [ ] Rule đầu tiên là entry point, kết thúc bằng `EOF`
- [ ] Mỗi concept quan trọng có rule riêng (không gộp vào 1 rule lớn)
- [ ] Tất cả keyword là literal string (`'goal'`, `'task'`), không phải token
- [ ] Dùng labeled alternatives (`# label`) cho mọi rule có 2+ alternative
- [ ] Có skip rule cho whitespace và comment (cả `//` và `/* */`)
- [ ] `mvn generate-sources` chạy thành công — 0 errors
- [ ] `pom.xml` bật `<visitor>true</visitor>`

## Lỗi thường gặp

| Lỗi | Nguyên nhân | Sửa |
|-----|-------------|-----|
| `no viable alternative at input` | Rule ambiguous hoặc thiếu case | Dùng labeled alternatives |
| Token xung đột keyword vs IDENT | Keyword không phải literal | Đặt keyword literal `'goal'` thay vì token `GOAL` |
| Visitor chỉ có `visitElementBody` | Thiếu `# label` | Thêm `# goalElem`, `# taskElem`, ... |
| ANTLR in lỗi ra stderr | Chưa `removeErrorListeners()` | Gọi trước khi add listener riêng |
