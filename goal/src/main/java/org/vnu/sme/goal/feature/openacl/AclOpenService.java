package org.vnu.sme.goal.feature.openacl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.tzi.use.gui.main.MainWindow;
import org.vnu.sme.goal.dsl.acl.parser.AclCompiler;
import org.vnu.sme.goal.dsl.acl.view.AclView;

/** Application boundary for the Open-ACL use case; Swing forms do not parse or build views. */
public final class AclOpenService {
    public enum Target { USE_DESKTOP, POPUP_WINDOW }
    public record Result(boolean opened, List<String> errors) {
        public Result { errors = List.copyOf(errors); }
    }

    private final MainWindow mainWindow;

    public AclOpenService(MainWindow mainWindow) {
        this.mainWindow = Objects.requireNonNull(mainWindow, "mainWindow");
    }

    public Result open(Path source, Target target) {
        try {
            AclCompiler.Result compiled = AclCompiler.compile(source);
            if (!compiled.ok()) return new Result(false, compiled.errors());
            if (target == Target.POPUP_WINDOW) {
                AclView.openPopupWindow(mainWindow, compiled.model(), source);
            } else {
                AclView.openUseDesktop(mainWindow, compiled.model(), source);
            }
            return new Result(true, List.of());
        } catch (IOException exception) {
            return new Result(false, List.of("cannot read '" + source + "': " + exception.getMessage()));
        }
    }
}
