# State-Aware ACL and iStar Development Models

This directory contains the experimental 3.0 state-aware ACL and iStar
metamodels. Use it to inspect how stable runtime identities, changing system
states, intentional models, and goal evaluations are represented together.

This package is developed bottom-up from M0 examples. It is a candidate design
and does not replace the canonical 2.0 metamodels or the current Java compiler.

## Files and recommended viewing order

| Order | File | What it represents |
|---|---|---|
| 1 | `acl-state.ecore` | ACL schema plus the system-history runtime metamodel |
| 2 | `istar-state.ecore` | Intentional specification plus the goal-trace metamodel |
| 3 | `mtg-development.acl.xmi` | Meeting Scheduler ACL model (M1) |
| 4 | `mtg-development.istar.xmi` | iStar model whose actor views reference ACL actors |
| 5 | `mtg-development.state.xmi` | Runtime identities and system states `s0` and `s1` |
| 6 | `mtg-development.goaltrace.xmi` | Complete iStar reevaluations for `s0` and `s1` |

The examples contain cross-file references. Keep all files together when
opening or moving this package.

## Import into Eclipse

The development directory belongs to the Eclipse project in its parent
directory. Follow the import instructions in [`../README.md`](../README.md)
and then expand the project's `development` folder.

Required Eclipse components are EMF, the Sample Ecore Model Editor, Sirius,
EcoreTools, and Classic Eclipse OCL.

## View the development metamodels

There are no prebuilt `.aird` sessions for these candidate metamodels. You can
inspect them immediately as trees or create local EcoreTools diagrams.

### Tree view

1. Right-click `acl-state.ecore` or `istar-state.ecore`.
2. Select **Open With > Sample Ecore Model Editor**.
3. Expand the `EPackage`, **EClassifiers**, and individual EClasses.
4. Select an element and use the **Properties** view to inspect its type,
   multiplicity, containment, opposite, and annotations.

### Class diagram view

1. Switch to the **Design** perspective.
2. Right-click an `.ecore` file and choose **Viewpoints Selection** or
   **Initialize Ecore Diagram**, depending on the EcoreTools version.
3. Enable the **Design** viewpoint.
4. Create a new **Entities** representation for the root EPackage.
5. Open the representation from the resulting `.aird` session.
6. Use **Refresh** if the first canvas is empty, then arrange and save the
   diagram.

Creating a diagram may add a local `.aird` file or update the Eclipse project
nature. Review those generated changes before committing them. Do not use the
palette's **Class** tool only to make existing classes appear; that tool changes
the `.ecore` metamodel.

## Explore the runtime and goal views

Open each `.xmi` file with **Open With > Sample Ecore Model Editor**. Expand
the nodes and keep the **Properties** view visible.

### ACL specification view

Open `mtg-development.acl.xmi` to inspect classifiers, attributes,
relationships, and the Actor classifiers used by the iStar model.

### iStar intentional view

Open `mtg-development.istar.xmi` and expand `actorViews`. Each view points to
an Actor declared in the ACL model and contains that actor's goals, tasks,
resources, qualities, refinements, and dependencies.

### System history view

Open `mtg-development.state.xmi` and inspect:

- `instances` for stable runtime identities;
- the `s0` and `s1` checkpoints for state-specific values and links; and
- references from each occurrence to its classifier in the ACL model.

Comparing `s0` with `s1` shows what changed without changing the identity of
the runtime objects.

### Goal trace view

Open `mtg-development.goaltrace.xmi` and expand the snapshots associated with
`s0` and `s1`. Within each snapshot, inspect the markings to see the evaluated
status, evidence, intentional element, and runtime subject. This view records a
complete reevaluation at each checkpoint rather than copying a goal value from
the preceding checkpoint.

When following a reference, use **Navigate > Show In** or the editor's
reference navigation action if available. If a target appears unresolved,
confirm that all four `.xmi` files and both `.ecore` files remain in the same
directory, then close and reopen the editor.

## Validate the examples

For each `.xmi` file:

1. Open it with the **Sample Ecore Model Editor**.
2. Select the root object.
3. Right-click and select **Validate**.
4. Review the validation dialog and **Problems** view.

Validate the files in the recommended order so that referenced resources have
already been loaded. Validating an `.ecore` file checks the metamodel; validating
an `.xmi` root evaluates the instance and its semantic invariants.

## Compatibility boundary

The development metamodels use separate namespaces:

```text
https://vnu.edu.vn/sme/goal/acl/state/3.0
https://vnu.edu.vn/sme/goal/istar/state/3.0
```

The separate namespaces prevent Eclipse from interpreting development models
as canonical 2.0 models. Parser or compiler migration should begin only after
the invariants and M0 examples have been accepted.

## Semantic documentation

- [ACL state model](../../docs/semantics/development/acl-state-model.md)
- [iStar state model](../../docs/semantics/development/istar-state-model.md)
- [Meeting Scheduler stateful example](../../docs/semantics/development/mtg-stateful-example.md)
