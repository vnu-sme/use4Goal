package org.vnu.sme.goal.feature.exportacltoeventb;

import java.nio.file.Path;
import java.util.Objects;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.vnu.sme.goal.translate.acl2eventb.AclToEventBRequest;
import org.vnu.sme.goal.translate.acl2eventb.AclToEventBService;

/** USE action named acl2eventB: one ACL file in, one Rodin project out. */
public final class ActionAcl2EventB implements IPluginActionDelegate {
    @Override public void performAction(IPluginAction action) {
        Objects.requireNonNull(action,"action");
        Runnable work=()->run(action.getParent());
        if(SwingUtilities.isEventDispatchThread()) work.run(); else SwingUtilities.invokeLater(work);
    }

    private static void run(MainWindow parent) {
        JFileChooser source=new JFileChooser(); source.setDialogTitle("Select ACL model");
        source.setFileFilter(new FileNameExtensionFilter("ACL files (*.acl)","acl"));
        if(source.showOpenDialog(parent)!=JFileChooser.APPROVE_OPTION) return;
        Path acl=source.getSelectedFile().toPath();

        JFileChooser output=new JFileChooser(acl.toAbsolutePath().getParent().toFile());
        output.setDialogTitle("Select Rodin workspace/output directory");
        output.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(output.showOpenDialog(parent)!=JFileChooser.APPROVE_OPTION) return;

        String defaultName=base(acl.getFileName().toString())+"AclEventB";
        String projectName=JOptionPane.showInputDialog(parent,"Rodin project name",defaultName);
        if(projectName==null||projectName.isBlank()) return;
        var result=new AclToEventBService().export(new AclToEventBRequest(
                acl,output.getSelectedFile().toPath(),projectName.trim()));
        if(result.success()) JOptionPane.showMessageDialog(parent,
                "Generated ACL-only Rodin project:\n"+result.projectDirectory(),
                "acl2eventB",JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(parent,String.join("\n",result.diagnostics()),
                "acl2eventB failed",JOptionPane.ERROR_MESSAGE);
    }
    private static String base(String name){int dot=name.lastIndexOf('.');return dot>0?name.substring(0,dot):name;}
}
