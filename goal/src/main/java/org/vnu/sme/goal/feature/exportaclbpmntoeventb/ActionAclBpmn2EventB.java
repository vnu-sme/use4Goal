package org.vnu.sme.goal.feature.exportaclbpmntoeventb;

import java.nio.file.Path;
import java.util.Objects;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.runtime.gui.*;
import org.vnu.sme.goal.translate.aclbpmn2eventb.*;

public final class ActionAclBpmn2EventB implements IPluginActionDelegate {
    @Override public void performAction(IPluginAction action) {
        Objects.requireNonNull(action); Runnable work=()->run(action.getParent());
        if(SwingUtilities.isEventDispatchThread()) work.run(); else SwingUtilities.invokeLater(work);
    }
    private static void run(MainWindow parent) {
        Path acl=choose(parent,"Select ACL model","ACL (*.acl)","acl"); if(acl==null)return;
        Path bpmn=choose(parent,"Select BPMN model","BPMN (*.bpmn2)","bpmn2"); if(bpmn==null)return;
        Path output=directory(parent,acl); if(output==null)return;
        String name=JOptionPane.showInputDialog(parent,"Rodin project name",base(acl)+"BpmnEventB");
        if(name==null||name.isBlank())return;
        var result=new AclBpmn2EventBService().export(new AclBpmn2EventBRequest(acl,bpmn,output,name.trim()));
        show(parent,"aclBpmn2eventB",result);
    }
    private static Path choose(MainWindow p,String title,String description,String extension){JFileChooser c=new JFileChooser();c.setDialogTitle(title);c.setFileFilter(new FileNameExtensionFilter(description,extension));return c.showOpenDialog(p)==JFileChooser.APPROVE_OPTION?c.getSelectedFile().toPath():null;}
    private static Path directory(MainWindow p,Path near){JFileChooser c=new JFileChooser(near.toAbsolutePath().getParent().toFile());c.setDialogTitle("Select Rodin output directory");c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);return c.showOpenDialog(p)==JFileChooser.APPROVE_OPTION?c.getSelectedFile().toPath():null;}
    private static String base(Path p){String n=p.getFileName().toString();int dot=n.lastIndexOf('.');return dot>0?n.substring(0,dot):n;}
    private static void show(MainWindow p,String title,org.vnu.sme.goal.translate.aclistarbpmn2eventb.EventBExportResult r){JOptionPane.showMessageDialog(p,r.success()?"Generated Rodin project:\n"+r.projectDirectory()+(r.diagnostics().isEmpty()?"":"\n\nWarnings:\n"+String.join("\n",r.diagnostics())):String.join("\n",r.diagnostics()),title,r.success()?JOptionPane.INFORMATION_MESSAGE:JOptionPane.ERROR_MESSAGE);}
}
