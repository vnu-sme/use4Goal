package org.vnu.sme.goal.feature.istartrace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.vnu.sme.goal.verify.conformance.semantics.GoalTaskStatus;
import org.vnu.sme.goal.dsl.istar.mm.Goal;
import org.vnu.sme.goal.dsl.istar.mm.IntentionalElement;
import org.vnu.sme.goal.dsl.istar.mm.Task;
import org.vnu.sme.goal.trace.istartrace.IStarTraceStepper;

/** Rows are instantiated Goal/Task occurrences; columns are observable ACL states. */
@SuppressWarnings("serial")
public final class IStarTraceTableModel extends AbstractTableModel {
    public record Row(String occurrenceId, String kind, String label) {}

    private final List<IStarTraceStepper.Step> steps;
    private final List<Row> rows;

    public IStarTraceTableModel(IStarTraceStepper.Result result) {
        this.steps = result == null ? List.of() : result.steps();
        Map<String, Row> occurrences = new LinkedHashMap<>();
        for (IStarTraceStepper.Step step : steps) {
            for (Map.Entry<String, IntentionalElement> entry
                    : step.instantiation().instanceModel().allElements().entrySet()) {
                String kind = switch (entry.getValue()) {
                    case Goal ignored -> "Goal";
                    case Task ignored -> "Task";
                    default -> null;
                };
                if (kind == null) continue;
                String id = entry.getKey();
                String label = step.instantiation().nodeLabels().getOrDefault(id, id);
                occurrences.putIfAbsent(id, new Row(id, kind, label));
            }
        }
        List<Row> sorted = new ArrayList<>(occurrences.values());
        sorted.sort(Comparator.comparing(Row::kind).thenComparing(Row::label)
                .thenComparing(Row::occurrenceId));
        this.rows = List.copyOf(sorted);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return steps.size() + 1; }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? "Goal / Task occurrence" : "s" + (column - 1);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? String.class : GoalTaskStatus.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = rows.get(rowIndex);
        if (columnIndex == 0) return row.kind() + "  " + row.label();
        return steps.get(columnIndex - 1).instantiation().instanceMarking()
                .goalTaskStatus(row.occurrenceId());
    }

    public Row row(int rowIndex) { return rows.get(rowIndex); }

    public String statementAt(int modelColumn) {
        if (modelColumn <= 0 || modelColumn > steps.size()) return "";
        return steps.get(modelColumn - 1).checkpoint().soilLine();
    }
}
