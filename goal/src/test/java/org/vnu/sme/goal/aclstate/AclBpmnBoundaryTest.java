package org.vnu.sme.goal.aclstate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.vnu.sme.goal.verify.aclstate.AclBpmnBoundary;
import org.vnu.sme.goal.verify.aclstate.AclStateEvaluationSession;

class AclBpmnBoundaryTest {
    private static final Path CLASSROOM = Path.of("src/main/resources/examples/classroom");

    @Test
    void parsesTheClassroomSymbolicScope() throws Exception {
        var session = AclStateEvaluationSession.load(CLASSROOM.resolve("classroom.acl"));
        AclBpmnBoundary boundary = session.loadBoundary(CLASSROOM.resolve("classroom.aclboundary"));

        assertEquals(24, boundary.snapshots());
        assertEquals(3, boundary.loopBound());
        assertEquals(new AclBpmnBoundary.Scope(2, 2), boundary.objectScopes().get("Person"));
        assertEquals(new AclBpmnBoundary.Scope(1, 1),
                boundary.linkScopes().get("Classroom_contains_Student"));
    }

    @Test
    void rejectsABoundaryThatDoesNotScopeEveryAclClassifier(@TempDir Path temp) throws Exception {
        var session = AclStateEvaluationSession.load(CLASSROOM.resolve("classroom.acl"));
        Path incomplete = temp.resolve("incomplete.aclboundary");
        Files.writeString(incomplete, """
                acl-bpmn-boundary v1.0 Incomplete {
                  snapshots 4;
                  loop-bound 3;
                  integer -1..1;
                  objects Classroom 1;
                }
                """);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> session.loadBoundary(incomplete));
        assertTrue(error.getMessage().contains("missing objects scope for Person"), error::getMessage);
        assertTrue(error.getMessage().contains("missing objects scope for Teacher"), error::getMessage);
    }
}
