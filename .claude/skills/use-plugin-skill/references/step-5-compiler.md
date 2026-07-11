# Bước 4 — Action, Form, Compiler

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/<lang>/
├── action/
│   └── ActionOpen<Lang>.java        # Entry point từ USE menu
├── gui/
│   └── <Lang>Form.java              # JDialog file picker + log + view
└── parser/
    ├── <LANG>Compiler.java          # Pure Java: file/string → MM (qua AST+Factory)
    ├── <Lang>BuildingVisitor.java   # ParseTree → AST
    └── <Lang>ModelFactory.java      # AST → MM
```

---

## 4.1 ActionOpen\<Lang\> — hợp đồng bắt buộc với USE core

Đây là điểm móc nối **duy nhất** với USE core, đúng cơ chế Extension Point mô tả trong
`doc/use-core-design-rules.md` mục 1: class này được `PluginClassLoader` nạp lazy khi
người dùng bấm menu, không đụng gì tới code trong `use/`.

```java
package org.vnu.sme.goal.<lang>.action;

import javax.swing.SwingUtilities;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.<lang>.gui.<Lang>Form;

public final class ActionOpen<Lang> implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();    // → MSystem/MModel nếu cần
        MainWindow mainWindow = pluginAction.getParent();     // USE main window

        SwingUtilities.invokeLater(() -> {
            <Lang>Form form = new <Lang>Form(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
```

> Luôn lấy cả `getSession()` **và** `getParent()` — Session cần thiết nếu sau này ngôn ngữ
> cần đọc `MModel`/`MSystem` đang mở trong USE (ví dụ cross-reference với class diagram).

---

## 4.2 \<Lang\>Form (JDialog) — non-modal, độc lập với ViewFrame

Form mở như 1 dialog độc lập, **không** kế thừa `ViewFrame` của USE GUI (đó chỉ dùng cho
view built-in như ClassDiagram/ObjectDiagram của USE core).

```java
package org.vnu.sme.goal.<lang>.gui;

import javax.swing.*;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.goal.<lang>.mm.<Lang>Model;
import org.vnu.sme.goal.<lang>.parser.<LANG>Compiler;
import org.vnu.sme.goal.<lang>.view.<Lang>View;

public final class <Lang>Form extends JDialog {

    private final Session    session;
    private final MainWindow mainWindow;
    private <Lang>Model      currentModel;

    private JTextField pathField;
    private JTextArea  logArea;
    private JLabel     statusLabel;
    private <Lang>View view;

    public <Lang>Form(Session session, MainWindow mainWindow) {
        super(mainWindow, "<Lang> Viewer", false);   // false = non-modal — BẮT BUỘC
        this.session    = session;
        this.mainWindow = mainWindow;
        buildUI();
        setSize(1200, 800);
        setLocationRelativeTo(mainWindow);
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("<Lang> files (*.<ext>)", "<ext>"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
    }

    private void doLoad() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) { status("No file selected.", C_ERR); return; }
        try {
            <LANG>Compiler.Result r = <LANG>Compiler.compile(Path.of(path));
            if (!r.ok()) {
                log("Compile errors:\n" + String.join("\n", r.errors()));
                status("Compile FAILED — " + r.errors().size() + " error(s).", C_ERR);
                return;
            }
            currentModel = r.model();
            view.setModel(currentModel);
            status("Loaded successfully.", C_OK);
        } catch (IOException ex) {
            log("IO error: " + ex.getMessage());
        }
    }
}
```

Mẹo: dùng `java.util.prefs.Preferences` để nhớ đường dẫn file lần mở gần nhất
(`PREFS.get("<lang>.lastFile", "")`) — tiện lợi cho người dùng, không bắt buộc nhưng nên áp
dụng nếu ngôn ngữ mới cũng có luồng "mở file rồi load lại nhiều lần".

---

## 4.3 \<LANG\>Compiler — Pure Java, trả về Result record

Không có Swing. Chuỗi gọi: ANTLR parse → `<Lang>BuildingVisitor.build()` (→ AST) →
`<Lang>ModelFactory.build()` (→ MM). Đây là điểm khác biệt quan trọng với MAXGoal (đã xoá):
compiler KHÔNG được gọi thẳng 1 Visitor build MM — luôn phải đi qua AST rồi Factory (xem
`step-2-ast.md`, `step-3-mm.md`).

```java
package org.vnu.sme.goal.<lang>.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.vnu.sme.goal.<lang>.mm.<Lang>Model;

public final class <LANG>Compiler {

    public record Result(<Lang>Model model, List<String> errors) {
        public boolean ok() { return errors.isEmpty(); }
    }

    public static Result compile(Path file) throws IOException {
        return compileStream(CharStreams.fromPath(file));
    }

    public static Result compile(String source) {
        return compileStream(CharStreams.fromString(source));
    }

    private static Result compileStream(CharStream chars) {
        List<String> errors = new ArrayList<>();

        <LANG>Lexer  lexer  = new <LANG>Lexer(chars);
        <LANG>Parser parser = new <LANG>Parser(new CommonTokenStream(lexer));

        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?,?> rec, Object sym, int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        <LANG>Parser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        var cs    = <Lang>BuildingVisitor.build(tree);           // ParseTree → AST
        var model = <Lang>ModelFactory.build(cs);                // AST → MM
        return new Result(model, Collections.emptyList());
    }
}
```

---

## useplugin.xml — 1 dòng `<action>` mỗi ngôn ngữ

```xml
<plugin name="GoalModel Plugin" version="3.0">
    <actions>
        <action label="<Lang> Viewer"
                icon="images/goal.png"
                class="org.vnu.sme.goal.<lang>.action.ActionOpen<Lang>"
                tooltip="<Lang> Diagram Viewer"
                menu="GoalModel Plugin"
                menuitem="<Lang> Viewer"
                toolbaritem="<Lang> Viewer"
                id="org.vnu.sme.goal.<lang>.action.ActionOpen<Lang>">
        </action>
    </actions>
</plugin>
```

Đây là **toàn bộ** những gì cần khai báo để USE nạp 1 ngôn ngữ mới — không cần sửa gì khác
trong `use/`.

---

## Checklist bước 4

- [ ] `ActionOpen<Lang>` implement `IPluginActionDelegate`
- [ ] Action lấy cả `session` và `mainWindow` từ `pluginAction`
- [ ] Action gọi `SwingUtilities.invokeLater` trước khi tạo Form
- [ ] Form extends `JDialog` (không phải `JFrame`), non-modal (`super(..., false)`)
- [ ] Compiler là `final class` không có Swing
- [ ] Compiler đi qua **AST rồi Factory**, không build MM thẳng trong Visitor
- [ ] Compiler trả về `Result` record với `model` + `errors`
- [ ] Compiler gọi `removeErrorListeners()` trước khi add custom listener
- [ ] `useplugin.xml` `class=` khớp fully qualified class name

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| UI freeze khi compile file lớn | Wrap trong `SwingWorker` thay vì gọi trực tiếp trong action listener |
| `getSession()` NPE | Session có thể null nếu không có USE model mở — null-check khi dùng |
| ANTLR in lỗi ra stderr | Quên `removeErrorListeners()` |
| Compiler gọi thẳng Visitor build MM | Vi phạm tách AST/Factory — xem `step-2-ast.md`/`step-3-mm.md` |
| Form không hiện | Kiểm tra `setVisible(true)` và `SwingUtilities.invokeLater` |
