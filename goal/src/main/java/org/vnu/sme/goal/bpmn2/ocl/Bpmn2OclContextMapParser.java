package org.vnu.sme.goal.bpmn2.ocl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the small line-oriented BPMN OCL context map format.
 *
 * <pre>
 *   # comment
 *   context &lt;bpmnElementId&gt; -&gt; &lt;UseClassName&gt;
 *   context &lt;sourceId&gt;::&lt;targetId&gt; -&gt; &lt;UseClassName&gt;
 * </pre>
 */
public final class Bpmn2OclContextMapParser {

    private Bpmn2OclContextMapParser() {}

    public static Map<String, String> parse(Path file) throws IOException {
        return parse(Files.readAllLines(file));
    }

    public static Map<String, String> parse(List<String> lines) {
        Map<String, String> result = new LinkedHashMap<>();

        for (String raw : lines) {
            String line = stripComment(raw).trim();
            if (line.isEmpty()) continue;

            String[] tok = line.split("\\s+");
            if (tok.length == 4 && tok[0].equals("context") && tok[2].equals("->")) {
                result.put(tok[1], tok[3]);
            } else {
                throw new IllegalArgumentException("cannot parse BPMN OCL context line: '" + raw + "'");
            }
        }

        return result;
    }

    private static String stripComment(String line) {
        int i = line.indexOf('#');
        return i < 0 ? line : line.substring(0, i);
    }
}
