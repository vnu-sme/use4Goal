package org.vnu.sme.goal.analysis.mapping;

import java.util.List;

/** Mapping-specific Rodin theorem guards and ProB properties generated for an Event-B project. */
public record MappingVerificationPlan(List<Obligation> obligations) {
    public MappingVerificationPlan { obligations = List.copyOf(obligations); }

    public enum GenerationStatus { GENERATED, UNSUPPORTED }

    public record Obligation(String mappingId, GenerationStatus status, String rodinProofObligation,
                             String probPropertyId, String predicate, String explanation) {}

    public static MappingVerificationPlan empty() { return new MappingVerificationPlan(List.of()); }
    public Obligation forMapping(String mappingId) {
        return obligations.stream().filter(x -> x.mappingId().equals(mappingId)).findFirst().orElse(null);
    }
}
