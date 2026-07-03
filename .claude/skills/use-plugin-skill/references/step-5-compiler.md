# Bước 5 — Action, Form, Compiler, Visitor, Factory

## Vị trí

```
src/main/java/org/vnu/sme/<plugin>/
├── action/
│   └── ActionOpen<LANG>.java        # Entry point từ USE menu
├── gui2/   (hoặc gui/)
│   └── <Lang>ModelForm.java         # JDialog file picker + log + view
└── parser/   (hoặc <lang>/)
    ├── <LANG>Compiler.java          # Pure Java: file/string → MM
    └── <Lang>BuildingVisitor.java   # ParseTree → MM (hoặc → AST)
```

---

## 5.1 ActionOpen<LANG>

Entry point khi người dùng click menu trong USE. Implement `IPluginActionDelegate`.

**Pattern thực tế từ `ActionOpenMAXGoal`**:

```java
package org.vnu.sme.<plugin>.action;

import javax.swing.SwingUtilities;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.<plugin>.gui2.<Lang>ModelForm;

public final class ActionOpen<LANG> implements IPluginActionDelegate {

    @Override
    public void performAction(IPluginAction pluginAction) {
        Session    session    = pluginAction.getSession();    // USE session
        MainWindow mainWindow = pluginAction.getParent();     // USE main window

        // Luôn mở dialog trên EDT
        SwingUtilities.invokeLater(() -> {
            <Lang>ModelForm form = new <Lang>ModelForm(session, mainWindow);
            form.setResizable(true);
            form.setVisible(true);
        });
    }
}
```

> **Quan trọng**: `IPluginAction` cung cấp cả `getSession()` VÀ `getParent()`.
> Luôn lấy cả hai — Session cần thiết nếu sau này cần tích hợp với USE model.

---

## 5.2 <Lang>ModelForm (JDialog)

Form standalone mở 1 lần, không block USE main window (non-modal).

**Pattern thực tế từ `MAXGoalForm`**:

```java
package org.vnu.sme.<plugin>.gui2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.vnu.sme.<plugin>.mm.<Lang>Model;
import org.vnu.sme.<plugin>.parser.<LANG>Compiler;
import org.vnu.sme.<plugin>.view.<Lang>View;

public final class <Lang>ModelForm extends JDialog {

    // ── Dark palette (konsisten với MAXGoalForm) ───────────────────
    private static final Color C_BG      = new Color(30, 30, 30);
    private static final Color C_PANEL   = new Color(37, 37, 38);
    private static final Color C_FG      = new Color(212, 212, 212);
    private static final Color C_ACCENT  = new Color(0, 122, 204);
    private static final Color C_SUCCESS = new Color(78, 201, 176);
    private static final Color C_ERROR   = new Color(244, 71, 71);
    private static final Color C_FIELD   = new Color(50, 50, 50);
    private static final Color C_BTN     = new Color(0, 122, 204);
    private static final Color C_BTN2    = new Color(55, 55, 55);

    private static final Font FONT_MONO  = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    private static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private static final Font FONT_LABEL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    // ── State ──────────────────────────────────────────────────────
    private final Session    session;
    private final MainWindow mainWindow;
    private <Lang>Model      currentModel;

    // ── Widgets ────────────────────────────────────────────────────
    private JTextField  pathField;
    private JTextArea   logArea;
    private JLabel      statusLabel;
    private <Lang>View  modelView;

    public <Lang>ModelForm(Session session, MainWindow mainWindow) {
        super(mainWindow, "<Lang> Compiler", false);  // false = non-modal
        this.session    = session;
        this.mainWindow = mainWindow;
        buildUI();
        setSize(1200, 750);
        setLocationRelativeTo(mainWindow);
    }

    private void buildUI() {
        getContentPane().setBackground(C_BG);
        setLayout(new BorderLayout(8, 8));
        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildCentrePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildTopPanel() {
        JPanel p = dark(new JPanel(new BorderLayout(8, 0)));
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        JLabel title = new JLabel("<Lang> Compiler");
        title.setFont(FONT_TITLE); title.setForeground(C_FG);
        p.add(title, BorderLayout.WEST);

        JPanel row = dark(new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0)));
        pathField = new JTextField(30);
        styleField(pathField);
        JButton btnBrowse  = btn("Browse…", C_BTN2, e -> chooseFile());
        JButton btnCompile = btn("Compile",  C_BTN,  e -> doCompile());

        row.add(label("File:")); row.add(pathField);
        row.add(btnBrowse); row.add(btnCompile);
        p.add(row, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCentrePanel() {
        JPanel p = dark(new JPanel(new BorderLayout()));
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        modelView = new <Lang>View();
        JScrollPane scroll = new JScrollPane(modelView);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60,60,60)),
                "<Lang> Diagram", 0, 0, FONT_LABEL, C_FG));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomPanel() {
        JPanel p = dark(new JPanel(new BorderLayout(0, 4)));
        p.setBorder(BorderFactory.createEmptyBorder(4, 12, 10, 12));

        logArea = new JTextArea(5, 80);
        logArea.setFont(FONT_MONO); logArea.setBackground(C_FIELD);
        logArea.setForeground(C_FG); logArea.setEditable(false);
        logArea.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(FONT_LABEL); statusLabel.setForeground(C_FG);

        p.add(new JScrollPane(logArea), BorderLayout.CENTER);
        p.add(statusLabel,              BorderLayout.SOUTH);
        return p;
    }

    // ── Actions ────────────────────────────────────────────────────

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(
                "<Lang> files (*.<ext>)", "<ext>"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            pathField.setText(fc.getSelectedFile().getAbsolutePath());
    }

    private void doCompile() {
        String path = pathField.getText().trim();
        if (path.isEmpty()) { status("No file selected.", C_ERROR); return; }
        try {
            <LANG>Compiler.Result r = <LANG>Compiler.compile(Path.of(path));
            if (!r.ok()) {
                log("Compile errors:\n" + String.join("\n", r.errors()));
                status("Compile FAILED — " + r.errors().size() + " error(s).", C_ERROR);
                return;
            }
            currentModel = r.model();
            modelView.setModel(currentModel);
            log("Compiled OK: " + currentModel.getActors().size() + " actors.");
            status("Compiled successfully.", C_SUCCESS);
        } catch (IOException ex) {
            log("IO error: " + ex.getMessage());
            status("IO error.", C_ERROR);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void status(String msg, Color c) {
        statusLabel.setText(msg); statusLabel.setForeground(c);
    }

    private static JPanel dark(JPanel p) { p.setBackground(new Color(30,30,30)); return p; }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(C_FG); l.setFont(FONT_LABEL);
        return l;
    }

    private static void styleField(JTextField f) {
        f.setBackground(new Color(50,50,50)); f.setForeground(new Color(212,212,212));
        f.setCaretColor(new Color(212,212,212));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70,70,70)),
                BorderFactory.createEmptyBorder(2,6,2,6)));
        f.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private static JButton btn(String label, Color bg, ActionListener al) {
        JButton b = new JButton(label);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(al);
        return b;
    }
}
```

---

## 5.3 <LANG>Compiler

Pure Java, không có Swing. Trả về `Result` record.

**Pattern thực tế từ `MAXGoalCompiler`**:

```java
package org.vnu.sme.<plugin>.parser;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.antlr.v4.runtime.*;
import org.vnu.sme.<plugin>.mm.<Lang>Model;

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

        // Custom error listener — thay thế default stderr
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        ANTLRErrorListener errListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?,?> rec, Object sym,
                                    int line, int col,
                                    String msg, RecognitionException e) {
                errors.add("line " + line + ":" + col + " " + msg);
            }
        };
        lexer.addErrorListener(errListener);
        parser.addErrorListener(errListener);

        <LANG>Parser.ModelContext tree = parser.model();
        if (!errors.isEmpty()) return new Result(null, errors);

        // Visitor → MM (nếu ngôn ngữ phức tạp thì qua AST trước)
        <Lang>Model model = <Lang>BuildingVisitor.build(tree);
        return new Result(model, Collections.emptyList());
    }
}
```

---

## 5.4 <Lang>BuildingVisitor

Visitor ANTLR → MM (hoặc → AST rồi Factory → MM).

**Pattern thực tế từ `MAXGoalBuildingVisitor`**:

```java
public final class <Lang>BuildingVisitor extends <LANG>BaseVisitor<Object> {

    private <Lang>Model model;

    public static <Lang>Model build(<LANG>Parser.ModelContext ctx) {
        <Lang>BuildingVisitor v = new <Lang>BuildingVisitor();
        v.visitModel(ctx);
        return v.model;
    }

    @Override
    public Object visitModel(<LANG>Parser.ModelContext ctx) {
        model = new <Lang>Model(ctx.IDENT().getText());
        for (var actorCtx : ctx.actorDef())
            model.addActor((Actor) visitActorDef(actorCtx));
        if (ctx.dependBlock() != null)
            visitDependBlock(ctx.dependBlock());
        return model;
    }

    @Override
    public Object visitActorDef(<LANG>Parser.ActorDefContext ctx) {
        String    name = ctx.IDENT().getText();
        ActorKind kind = ActorKind.from(ctx.actorKind().getText());
        List<Intentional> items = new ArrayList<>();
        for (var ic : ctx.intentional()) {
            Intentional item = (Intentional) visit(ic);
            if (item != null) items.add(item);
        }
        return new Actor(name, kind, items);
    }

    @Override
    public Object visitGoalDecl(<LANG>Parser.GoalDeclContext ctx) {
        String actorName  = currentActorName(ctx);
        String goalName   = ctx.IDENT().getText();
        String clause = null, expr = null;
        RefineSpec refine = null;

        for (var attr : ctx.goalAttr()) {
            if (attr instanceof <LANG>Parser.GaAchieveContext ac) {
                clause = "achieve"; expr = cond(ac.condition());
            } else if (attr instanceof <LANG>Parser.GaMaintainContext mc) {
                clause = "maintain"; expr = cond(mc.condition());
            } else if (attr instanceof <LANG>Parser.GaAvoidContext av) {
                clause = "avoid"; expr = cond(av.condition());
            } else if (attr instanceof <LANG>Parser.GaRefineContext rf) {
                refine = (RefineSpec) visit(rf.refineSpec());
            }
        }
        return new GoalDef(goalName, actorName, clause, expr, refine);
    }

    @Override
    public Object visitSeqRefine(<LANG>Parser.SeqRefineContext ctx) {
        return RefineSpec.SeqRefine.of(identList(ctx.IDENT()));
    }

    @Override
    public Object visitParRefine(<LANG>Parser.ParRefineContext ctx) {
        return new RefineSpec.ParRefine(identList(ctx.IDENT()));
    }

    @Override
    public Object visitXorRefine(<LANG>Parser.XorRefineContext ctx) {
        return new RefineSpec.XorRefine(guardedChildren(ctx.guardedChild()));
    }

    @Override
    public Object visitIterRefine(<LANG>Parser.IterRefineContext ctx) {
        return new RefineSpec.IterRefine(identList(ctx.IDENT()), cond(ctx.condition()));
    }

    // ── Helpers ────────────────────────────────────────────────────

    private String currentActorName(org.antlr.v4.runtime.ParserRuleContext ctx) {
        var p = ctx.getParent();
        while (p != null) {
            if (p instanceof <LANG>Parser.ActorDefContext a)
                return a.IDENT().getText();
            p = p.getParent();
        }
        return "";
    }

    private List<String> identList(
            List<? extends org.antlr.v4.runtime.tree.TerminalNode> nodes) {
        return nodes.stream().map(t -> t.getText()).collect(Collectors.toList());
    }

    private List<GuardedChild> guardedChildren(
            List<<LANG>Parser.GuardedChildContext> list) {
        return list.stream()
                .map(g -> new GuardedChild(cond(g.condition()), g.IDENT().getText()))
                .collect(Collectors.toList());
    }

    private static String cond(<LANG>Parser.ConditionContext ctx) {
        if (ctx == null) return "";
        Interval i = new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        return ctx.start.getInputStream().getText(i);
    }
}
```

---

## useplugin.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plugin>
  <name><Lang> Plugin</name>
  <version>1.0</version>
  <vendor>VNU-SME</vendor>

  <actions>
    <action id="open<Lang>"
            label="Open <Lang>..."
            class="org.vnu.sme.<plugin>.action.ActionOpen<LANG>"
            icon="images/<lang>.png"
            tooltip="Load and visualise a <Lang> model"
            menuPath="Plugins/<Lang>"/>
  </actions>
</plugin>
```

---

## Checklist bước 5

- [ ] `ActionOpen<LANG>` implement `IPluginActionDelegate`
- [ ] Action lấy cả `session` và `mainWindow` từ `pluginAction`
- [ ] Action gọi `SwingUtilities.invokeLater` trước khi tạo Form
- [ ] Form extends `JDialog` (không phải JFrame)
- [ ] Form là non-modal (`super(mainWindow, "...", false)`)
- [ ] Compiler là `final class` không có Swing
- [ ] Compiler trả về `Result` record với `model` + `errors`
- [ ] Compiler gọi `removeErrorListeners()` trước khi add custom listener
- [ ] Visitor dùng `instanceof` check cho labeled alternatives
- [ ] `cond()` helper dùng `Interval` để capture raw text từ InputStream
- [ ] `useplugin.xml` class attribute khớp với fully qualified class name

## Lỗi thường gặp

| Lỗi | Sửa |
|-----|-----|
| UI freeze khi compile | Compile chạy trực tiếp trong `doCompile()` — nếu file lớn, wrap trong `SwingWorker` |
| NPE trong Visitor | ANTLR trả `null` cho optional rule — luôn null-check trước `ctx.xxx()` |
| `getSession()` NPE | Session có thể null nếu không có USE model mở — null-check khi dùng |
| ANTLR in lỗi ra stderr | Quên `removeErrorListeners()` |
| `instanceof` check fail | Cần labeled alternatives `# label` trong grammar (Bước 1) |
| Form không hiện | Kiểm tra `setVisible(true)` và `SwingUtilities.invokeLater` |
