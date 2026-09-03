package org.vnu.sme.goal.dsl.acl.view;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;
import org.vnu.sme.goal.dsl.acl.mm.*;

/**
 * Presentation adapter from ACL semantics to USE's UML metamodel.
 *
 * <p>This is deliberately the only layer that knows both metamodels. Views consume
 * real {@link MClass}/{@link MAssociation} objects and therefore reuse USE's class
 * and association components; parser and ACL semantic classes remain GUI-free.</p>
 */
final class AclUseViewModel {
    final MModel model;
    final Map<String, MClass> classes;
    final Map<String, MAssociation> associations;

    private AclUseViewModel(MModel model, Map<String, MClass> classes,
                            Map<String, MAssociation> associations) {
        this.model = model;
        this.classes = Map.copyOf(classes);
        this.associations = Map.copyOf(associations);
    }

    static AclUseViewModel build(AclModel acl) {
        try {
            MModel useModel = new ModelFactory().createModel("__acl_view__");
            UseModelApi api = new UseModelApi(useModel);
            Map<String, MClass> classes = new LinkedHashMap<>();
            Map<String, MAssociation> associations = new LinkedHashMap<>();

            for (AclEnum value : acl.enums()) api.createEnumeration(value.name(), value.literals());
            for (AclEntity value : acl.entities()) classes.put(value.name(), api.createClass(value.name(), false));
            for (AclRole value : acl.roles()) classes.put(value.name(), api.createClass(value.name(), false));
            for (AclGroup value : acl.groups()) classes.put(value.name(), api.createClass(value.name(), false));

            acl.entities().forEach(value -> addAttributes(api, value.name(), value.attributes()));
            acl.roles().forEach(value -> addAttributes(api, value.name(), value.attributes()));
            acl.groups().forEach(value -> addAttributes(api, value.name(), value.attributes()));
            for (AclGeneralization value : acl.generalizations()) {
                if (acl.findRole(value.specific()).isPresent()) continue;
                api.createGeneralization(value.specific(), value.general());
            }
            for (AclRelation value : acl.relations()) {
                int aggregation = switch (value.kind()) {
                    case AGGREGATION -> MAggregationKind.AGGREGATION;
                    case COMPOSITION -> MAggregationKind.COMPOSITION;
                    default -> MAggregationKind.NONE;
                };
                String sourceRole = value.source().roleName().orElse("source_" + value.name());
                String targetRole = value.target().roleName().orElse("target_" + value.name());
                MAssociation association = api.createAssociation(value.name(),
                        value.source().type(), sourceRole,
                        multiplicity(value.source().multiplicity()), aggregation,
                        value.target().type(), targetRole,
                        multiplicity(value.target().multiplicity()), MAggregationKind.NONE);
                associations.put(value.name(), association);
            }
            return new AclUseViewModel(useModel, classes, associations);
        } catch (UseApiException exception) {
            String detail = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            throw new IllegalArgumentException(
                    "Cannot adapt ACL model to USE diagram metamodel: " + detail, exception);
        }
    }

    private static void addAttributes(UseModelApi api, String owner, java.util.List<AclAttribute> attributes) {
        for (AclAttribute attribute : attributes) {
            try {
                api.createAttribute(owner, attribute.name(), attribute.type().sourceName());
            } catch (UseApiException exception) {
                throw new IllegalArgumentException("Invalid ACL attribute '" + owner + "." + attribute.name() + "'", exception);
            }
        }
    }

    private static String multiplicity(AclCardinality value) {
        if (value.max().isPresent() && value.min() == value.max().getAsInt()) return Integer.toString(value.min());
        return value.min() + ".." + (value.max().isPresent() ? value.max().getAsInt() : "*");
    }
}
