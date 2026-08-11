package org.vnu.sme.goal.feature.istartrace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.trace.istartrace.nativeacl.BpmnTraceIStarMonitor;

@SuppressWarnings("serial")
public final class BpmnTraceMarkingTableModel extends AbstractTableModel {
    public record Row(String id,String label){}
    private final List<BpmnTraceIStarMonitor.Frame> frames; private final List<Row> rows;
    public BpmnTraceMarkingTableModel(BpmnTraceIStarMonitor.ProcessResult process){
        frames=process==null?List.of():process.frames();Map<String,Row> found=new LinkedHashMap<>();
        for(var frame:frames)for(var e:frame.instanceModel().allElements().entrySet())
            if(e.getValue() instanceof Goal||e.getValue() instanceof Task)
                found.putIfAbsent(e.getKey(),new Row(e.getKey(),frame.nodeLabels().getOrDefault(e.getKey(),e.getKey())));
        List<Row> sorted=new ArrayList<>(found.values());sorted.sort(Comparator.comparing(Row::label));rows=List.copyOf(sorted);
    }
    public int getRowCount(){return rows.size();}public int getColumnCount(){return frames.size()+1;}
    public String getColumnName(int c){return c==0?"Goal / Task occurrence":"s"+(c-1);}
    public Class<?> getColumnClass(int c){return c==0?String.class:GoalTaskStatus.class;}
    public Object getValueAt(int r,int c){return c==0?rows.get(r).label():frames.get(c-1).marking().goalTaskStatus(rows.get(r).id());}
    public Row row(int r){return rows.get(r);}public BpmnTraceIStarMonitor.Frame frame(int c){return frames.get(c-1);}
}
