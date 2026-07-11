Contents lists available at ScienceDirect
Information and Software Technology
journal homepage: www.elsevier.com/locate/infsof
Aligning processes with high-level requirements: Goal-model-based
compliance checking✩
Juanita Caballero-Villalobos a ,∗
, Hubert Baumeister a
, Elda Paja b
, Olga Kokoulina c
,
Hugo A. López a
a Department of Applied Mathematics and Computer Science, Technical University of Denmark, Anker Engelunds Vej 1, Kongens Lyngby, 2800, Denmark
b
IT University of Copenhagen, Rued Langgaards Vej 7, Copenhagen, 2300, Denmark
c Faculty of Law, University of Copenhagen, Karen Blixens Plads 16, Copenhagen, 2300, Denmark
A R T I C L E I N F O
Keywords:
Compliance checking
Business process compliance
Goal modeling
Requirements engineering
A B S T R A C T
Context: Process compliance refers to the alignment between business processes and regulatory requirements.
Compliance is challenging because it requires expressing the intent and possible interpretations of laws into
formal models, aligning models with foreseeable executions, and inspecting whether these executions could
generate violations. While business process compliance has been studied through conformance checking techniques, most regulatory requirements are defined in subjective and high-level terms, limiting the application
of rule- and alignment-checking algorithms to restricted cases where requirements are formally specified.
Objective: This paper investigates how compliance can be assessed against high-level and non-functional
requirements rather than low-level process events. We aim to raise the abstraction level of compliance checking
from specific task execution to the satisfaction of high-level business goals and subjective qualities.
Method: We propose a framework that links process models with goal models to capture functional decomposition, non-functional requirements, and contribution links, developed iteratively with legal practitioners.
Business processes (imperative and declarative) are represented as labeled transition systems (LTS), while
goals and qualities are modeled using iStar models. A mapping function synchronizes process activities with
goal elements. Compliance is then checked through state reachability, where all qualities are satisfied, with
computational support from the Kogi tool. We refine compliance into weak, strong, and monotonic quality
satisfaction, also called stability.
Results: From a technical view, the framework has demonstrated expressiveness, feasibility, and modularity
by distinguishing strong, weak, and monotonic compliance. From a legal perspective, it has shown potential
to enhance transparency in evidence-based decision-making and supports the traceability of knowledge bases
underpinning system development, as illustrated by the use case from Regulation EC 261/2004.
Conclusions: The proposed approach enables compliance checking against high-level and non-functional
requirements, showing potential to distinguish among compliant behaviors and increase goal satisfaction
traceability compared to rule-based conformance methods.
1. Introduction
Business processes are considered the heart of organizations.
Through processes, companies achieve objectives, coordinate and optimize resources, and comply with regulatory requirements. Legal
compliance became a substantial task for all business organizations.
✩ This article is part of a Special issue entitled: ‘RegCompliance in SE’ published in Information and Software Technology.
∗ Corresponding author.
E-mail addresses: jcavi@dtu.dk (J. Caballero-Villalobos), huba@dtu.dk (H. Baumeister), elpa@itu.dk (E. Paja), olga.kokoulina@jur.ku.dk (O. Kokoulina),
hulo@dtu.dk (H.A. López).
Yet, the majority of organizations treat law as an exogenous force, and
compliance is mapped and measured rather than explained [1]. The
management of business processes requires traceability between highlevel requirements (for instance, laws) and traces in an information
system. Regulatory compliance is a major driver of process mining
https://doi.org/10.1016/j.infsof.2026.108146
Received 23 September 2025; Received in revised form 1 April 2026; Accepted 8 April 2026
Information and Software Technology 196 (2026) 108146
Available online 20 April 2026
0950-5849/© 2026 The Authors. Published by Elsevier B.V. This is an open access article under the CC BY license (http://creativecommons.org/licenses/by/4.0/).
J. Caballero-Villalobos et al.
adoption in the industry [2]. Yet, there is still a large gap between regulations and business processes formalized via process models (PM). In
particular, there is a nontrivial interpretative factor: a legal paragraph
may have multiple interpretations by design [3], and its disambiguation
may therefore require human support. This contrasts with the intention
of formalizing policies with a single, mathematical, and unequivocal
semantics.
Regulatory texts and guidelines are often written at a high level of
abstraction. Breaux and Antón [4] demonstrate this through their analysis of privacy regulations, showing that legal provisions frequently use
broad terms requiring interpretation before operationalization. As a result, they can be used to elicit high-level business requirements (HLBRs)
that define organizational objectives or compliance expectations in
terms that do not have an immediate, agreed operational meaning.
Siena et al. [5] illustrate this through their goal-oriented approach
to extracting compliance requirements from regulations, where the
same legal provision may admit different operational interpretations
depending on stakeholder perspectives. HLBRs differ from functional
requirements, which prescribe specific system behaviors, and from
non-functional requirements, which prescribe measurable quality attributes such as performance or security [6]. In regulatory settings,
HLBRs are frequent because the normative text uses evaluative concepts
whose application depends on context and institutional interpretation.
Franceschetti et al. [3] argue that this ambiguity is often intentional,
allowing regulations to remain applicable across diverse situations. This
interpretive flexibility creates a fundamental challenge for automated
compliance checking: the same regulatory text may be operationalized
differently by different organizations, making it essential to document and trace the interpretive choices that underpin compliance
assessments.
To illustrate this challenge, we introduce a running example that is
used throughout the paper.
Running Example
Consider a simplified construction permit approval process in
a municipality, specified as follows.
(i) If a construction permit application is registered, the
application must eventually be assessed.
(ii) If additional documentation is required, the application
may be updated.
(iii) A decision (approve or deny) can only be made if an
application has been registered and assessed.
(iv) Once a decision is made, the application is closed and
cannot be decided again.
The municipality has established the following business goal:
(i) City business growth is supported
That is, the process consists of four actions: 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋, 𝖠𝗌𝗌𝖾𝗌𝗌,
𝖠𝗉𝗉𝗋𝗈𝗏𝖾, and 𝖣𝖾𝗇𝗒. The municipality pursues the business
goal City business growth is supported, which we interpret as a
preference for issuing 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 decisions whenever the application satisfies the required conditions. Although both 𝖠𝗉𝗉𝗋𝗈𝗏𝖾
and 𝖣𝖾𝗇𝗒 comply with the process specification, they do not
contribute equally to this goal: 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 supports it, whereas
𝖣𝖾𝗇𝗒 does not. The requirements of this process can be captured from different perspectives, including the operational
and intentional perspectives, as shown in Figs. 2, 4, and 5
From a process perspective, both approval and denial are valid outcomes once registration and assessment have occurred. However, from
the perspective of high-level business requirements, these outcomes
differ: the municipality aims to support city business growth, which
is better aligned with approving applications that meet the required
conditions. This illustrates the central challenge discussed earlier: compliance cannot be assessed solely based on process execution, but must
also account for how actions contribute to high-level goals.
However, modeling HLBRs and non-functional requirements is challenging because the relevant interpretation must be agreed and documented by experts in both the process and legal domains [7]. If experts
are not involved throughout later assessments, the rationale behind
each interpretation can be lost, which weakens traceability and the
justification of compliance outcomes. Multiple techniques have been
applied to compliance analysis. For instance, Governatori et al. [8]
combine imperative process models with deontic and temporal logics to formalize normative requirements; Burattin et al. [9] apply
conformance checking techniques to verify process executions against
prescribed behaviors; and López et al. [10] use declarative process
models such as Dynamic Condition Response (DCR) graphs to capture flexible compliance constraints (see [11] for a recent review).
However, these works typically operationalize low-level requirements,
that is, requirements whose satisfaction conditions can be captured
directly by process events and control-flow or declarative constraint
relations (for example, ‘‘activity 𝐴 must occur before 𝐵’’, or ‘‘if 𝐶
occurs then 𝐷 must eventually occur’’). This limits their applicability to
regulatory compliance, where many requirements appear as HLBRs and
require interpretive choices before they can be checked. For instance,
consider the requirement: ‘‘the company must provide a clear delivery
confirmation’’. Existing approaches may verify that a confirmation was
sent; however, they cannot assess whether the confirmation was clear
without an explicit operationalization (for example, a documented set
of required content elements) agreed by experts.
To address this gap, we explore how goal-modeling frameworks
can support the alignment of high-level requirements with business
processes. Goal models have been used for over three decades in
requirements engineering to capture stakeholder intentions, alternative
means of achieving objectives, and quality concerns [12,13]. They
provide a natural representation for HLBRs because they distinguish
between what stakeholders want to achieve (goals) and how they might
achieve it (tasks), and they make explicit the qualities that stakeholders
value. However, integrating goal models with process models raises
several challenges.
First, goal models and process models serve different purposes and
evolve independently. Goal models capture stakeholder intentions and
are typically used during requirements elicitation and analysis, whereas
process models capture operational workflows and are used during
design, implementation, and monitoring. As noted by Amyot et al. [12],
aligning these models requires explicit mappings that document how
process activities contribute to goal satisfaction. Without such mappings, it is unclear whether executing a process achieves the intended
goals, and changes to either model may break the alignment.
Second, goal models do not prescribe execution order or control
flow. They represent desired states of affairs, not sequences of actions.
In contrast, process models explicitly define which activities can occur
in which order. This mismatch means that reasoning about compliance
cannot be performed solely on the goal model or solely on the process model; it requires a synchronized view that respects the process
semantics while tracking goal satisfaction.
Third, goal satisfaction is not static. As noted by Giannakopoulou
et al. [14] and Meroni et al. [15], goals may be satisfied, violated,
and re-satisfied during process execution. For example, a quality such
as ‘‘customer satisfaction is maintained’’ may hold after an approval
action but be violated by a subsequent denial action. Existing goaloriented reasoning approaches [16,17] often treat satisfaction as a
one-time check rather than a property that evolves. This limits their
ability to reason about processes where later actions invalidate earlier
achievements.
Information and Software Technology 196 (2026) 108146
2
J. Caballero-Villalobos et al.
Fig. 1. Goal-oriented compliance checking at design-time framework.
Finally, process models come in different styles. Imperative models
such as Petri nets and workflow nets [18] prescribe explicit control flow
and are well-suited to structured, predictable processes. Declarative
models such as DCR graphs [19] specify constraints on allowed executions and are well-suited to flexible, knowledge-intensive processes. A
compliance framework that works only for one modeling style limits its
applicability. However, integrating goal models with both imperative
and declarative process models requires a common semantic foundation
that abstracts over the differences between modeling languages.
This work explores how goal-modeling frameworks can help define
compliance checking techniques that align high-level requirements and
business processes. Fig. 1 provides an overview of the framework that
makes expert operationalization of high-level business requirements
explicit and analyzable at design time by combining goal modeling with
operational process semantics via labeled transition systems. We represent both the goal model and the process model as labeled transition
systems and evaluate compliance on their synchronized executions. The
product system progresses only through enabled process actions, while
a mapping provides a correspondence relation that determines which
intentional element is updated by each executed action. This preserves
the semantics of the chosen process notation and makes explicit how
process behavior affects goal satisfaction over time, including cases in
which later actions invalidate earlier contributions.
The unique contribution of our approach is, that it can be instantiated with any process formalism that admits a labeled transition system
semantics. As examples, we choose workflow nets and DCR graphs,
because they cover two common modeling styles. Workflow nets [18]
capture imperative control flow explicitly and have a well-established
behavioral semantics, which suits structured operational processes.
DCR graphs [19] capture declarative constraints and suit settings where
behavior is flexible and compliance is primarily expressed through
constraints on allowed executions. For requirements, we use iStar goal
models [20] because they support stakeholder-oriented modeling of
goals and tasks together with qualities, and they provide a natural
representation for the operationalization of HLBRs. Building on earlier
work on goal model specialization [16] and monitoring of goal satisfaction [21,22], we define an operational semantics for iStar goal models
that tracks satisfaction over time and supports reasoning about conflicts
between contributions.
In this setting, compliance becomes a reachability-style property on
the product transition system: whether executions can (or must) reach
configurations in which all qualities that represent HLBRs satisfaction
are fulfilled. This makes it possible to distinguish executions that are
compliant with the process semantics from executions that satisfy the
stakeholder-oriented requirements captured in the goal model. By distinguishing strong compliance (where qualities, once satisfied, remain
satisfied) from weak compliance (where qualities may be temporarily
violated but can be recovered), the framework enables organizations
to compare alternative process designs based on how robustly they
achieve compliance.
Contributions. This paper extends our earlier work [23], which formulated compliance as a reachability problem by encoding a workflow net
and an iStar goal model as labeled transition systems and analyzing
their synchronous product. The analysis returned a binary outcome
(compliant/non-compliant) by checking whether, from every reachable
product configuration, some configuration in which all modeled highlevel business requirements (represented as qualities) are satisfied is
reachable. The main extensions are:
1. Generalization beyond a single process modeling notation by
allowing any process model that can be translated into a labeled
transition system, making the compliance analysis independent
of the modeling language (Section 4).
2. Instantiation of this generalization with Dynamic Condition Response (DCR) graphs alongside workflow nets, demonstrating
applicability to both imperative and declarative process models
(Section 4).
3. Refinement of the compliance criteria by distinguishing strong
compliance, weak compliance, and quality satisfaction monotonicity, together with formal guarantees (correctness, completeness, and complexity) for the compliance checking and stability
algorithms (Sections 4.3 and 5).
4. A use case derived from EU Air Passenger Rights (Regulation EC
261/2004), developed with legal practitioners, to reflect expert
interpretation and evidentiary requirements and to provide an
initial assessment of feasibility and ease of application for legal
practitioners (Section 7).
5. Extension of the Kogi tool [24] with outcome categories aligned
with these criteria, support for DCR graphs, and counterexample
generation for diagnosing non-compliance (Section 6).
Information and Software Technology 196 (2026) 108146
3
J. Caballero-Villalobos et al.
Limitations and threats to validity. The main threats to validity are
(i) the manual and interpretive nature of model construction, which
introduces subjectivity and requires expert collaboration; (ii) scalability
concerns for large or frequently evolving models, though empirical
evidence suggests the product LTS size is often manageable; (iii) limited
empirical validation, as the framework has been demonstrated in a
single regulatory domain with one legal practitioner; and (iv) expressiveness limitations, as the framework does not yet support multi-agent,
data-aware, or deontic reasoning. These limitations are discussed in
detail in Section 7.
Overview. The remainder of the paper is organized as follows. Section 2
introduces the required preliminaries on process models (Workflow
nets and DCR graphs), goal models, and labeled transition systems. Section 3 defines the goal model operational semantics and the mapping
between process actions and intentional elements. Section 4 introduces
the synchronous product and the compliance assessment for both imperative and declarative process models, and defines the compliance
criteria. Section 5 presents the compliance-checking and stability algorithms with their guarantees. Section 6 describes tool support. Section 7
reports the proof-of-concept validation. Section 8 discusses related
work. Section 9 concludes.
2. Preliminaries
In this section, we provide the formal definitions of iStar (goal
model), Workflow nets, and Dynamic Condition Response (DCR)
graphs.
2.1. Process models
Process models capture executable workflow steps and their ordering constraints. Traditional process modeling approaches are often
imperative, for instance, Petri nets and their variants, where the model
prescribes the admissible control flow by encoding the order in which
tasks may occur. While this approach suits fixed-order workflows. It
restricts processes that require flexibility because multiple execution
orders are valid, and deviations from a single prescribed path may
occur. Declarative formalisms address this limitation by specifying
constraints that define the permitted executions without prescribing
a single task order. For declarative modeling, we choose Dynamic
Condition Response (DCR) graphs [19]. DCR bears similarities to other
declarative modeling languages, such as Declare [25] and CMMN [26],
in that they are processes designed for flexibility, defining explicit
constraints that all executions should satisfy. In this sense, the flows
perceived in an imperative notation are implicit rather than explicit
in the formalism. Our choice of DCR is not incidental: first, compared
to CMMN and Declare, DCR graphs are perceived with higher ease
of use and perceived usefulness by process modelers [27]; second,
DCR graphs have been adopted by commercial vendors and municipal
organizations in their case management and compliance tasks [28], and
third, DCR graphs have evolved from a basic (yet expressive formalism)
to a large modeling language supporting extensions with time [29],
data [30], choreographies and orchestrations [31], object-centricity
and relational data [32], as well as with multiple commercial and
open-source implementations of the language [33].
Below, we introduce the notation used to model system behavior and to formalize the specifications of the running example (Section 1) using Workflow nets as an imperative formalism and Dynamic
Condition Response graphs as a declarative formalism.
2.1.1. System’s behavior
Before introducing any process model language, we define a labeled
transition system. In our case, we consider process modeling notations whose semantics can be expressed as an operational semantics,
endowed with a Labeled Transition System (LTS).
Definition 2.1 (Labeled Transition System [34]). Given a finite set 𝐴 of
(action) labels, a labeled transition system (LTS) over 𝐴 is a tuple
𝛤𝐴 = ⟨𝑆, 𝐴, 𝑠0
, →⟩,
where 𝑆 is the (possibly) infinite set of states, 𝑠0
is the initial state, and
→ ⊆ 𝑆 × 𝐴 × 𝑆 is the transition relation.
In what follows, we write 𝑠
𝑎
←←←←←→ 𝑠
′
for (𝑠, 𝑎, 𝑠′
) ∈→ . As customary, the
reflexive transitive closure of → is denoted by →*
. We write 𝑠 →*
𝑠
′
if
there exists a finite sequence of transitions from 𝑠 to 𝑠
′
.
2.1.2. Imperative process modeling
In this section, we adapt the formalizations of Workflow nets (WFnet) presented by van der Werf et al. [35] to a WF-net without weights.
Let 𝑆 and 𝑇 be sets. The powerset of 𝑆 is denoted by (𝑆) = {𝑆
′
∣ 𝑆
′ ⊆
𝑆} and |𝑆| denotes the cardinality of 𝑆. A multiset over a set 𝑆 is a
function 𝑚 ∶ 𝑆 → N, where N = {0, 1, 2,…} denotes the set of natural
numbers. For 𝑠 ∈ 𝑆, 𝑚(𝑠) ∈ N denotes the number of times 𝑠 appears in
the multiset. For 𝑥 ∉ 𝑆, 𝑚(𝑥) = 0. We write 𝑠
𝑛
if 𝑚(𝑠) = 𝑛. We use 𝑆
⊕ to
denote the set of all finite multisets over 𝑆 and ∅ to denote the empty
multiset. We also write |𝑚| =
∑
𝑠∈𝑆 𝑚(𝑠) to denote the cardinality of 𝑚.
A sequence over 𝑆 of length 𝑛 ∈ N is a function 𝜎 ∶ {1, … , 𝑛} → 𝑆. If
𝑛 > 0 and 𝜎(𝑖) = 𝑎𝑖
, for 1 ≤ 𝑖 ≤ 𝑛, we write 𝜎 = ⟨𝑎1
,… , 𝑎𝑛
⟩. The length of
𝜎 is denoted by |𝜎| and is equal to 𝑛. The sequence of length 0 is called
the empty sequence, and is denoted by 𝜖. The set of all finite sequences
over 𝑆 is denoted by 𝑆
*
. We write 𝑎 ∈ 𝜎 if there is 1 ≤ 𝑖 ≤ |𝜎| such
that 𝜎(𝑖) = 𝑎.
Definition 2.2 (Petri Net [35]). A Petri netis a 3-tuple (𝑃 , 𝑇 , 𝐹)where 𝑃
and 𝑇 are two disjoint finite sets of places and transitions, respectively,
and 𝐹 ⊆ (𝑃 × 𝑇 ) ∪ (𝑇 × 𝑃 ) is the flow relation.
For 𝑥 ∈ 𝑃 ∪ 𝑇 , we write ∙𝑥 = {𝑦 ∣ (𝑦, 𝑥) ∈ 𝐹} to denote the preset of
𝑥 and 𝑥
∙ = {𝑦 ∣ (𝑥, 𝑦) ∈ 𝐹} to denote the postset of 𝑥.
A marking of 𝑁 is a multiset 𝑚 ∈ 𝑃
⊕, where 𝑚(𝑝) denotes the
number of tokens in place 𝑝 ∈ 𝑃 . If 𝑚(𝑝) > 0, place 𝑝 is called marked
in marking 𝑚.
A marked Petri net is a tuple 𝑀𝑃 𝑁 = (𝑁, 𝑚) where 𝑁 is a Petri net
and 𝑚 is a marking. A transition 𝑡 ∈ 𝑇 is enabled in (𝑁, 𝑚), denoted by
𝑁 ⊢ 𝑚
𝑡
←←→ 𝑚′
, iff (𝑝, 𝑡) ≤ 𝑚(𝑝) for all 𝑝 ∈
∙
𝑡. An enabled transition can
fire, resulting in marking 𝑚′
iff 𝑚′
(𝑝) + (𝑝, 𝑡) = 𝑚(𝑝) + (𝑡, 𝑝), for all 𝑝 ∈ 𝑃 ,
and (𝑁, 𝑚)
𝑡
←←→ (𝑁, 𝑚′
).
We lift the notation of firings to sequences. A sequence 𝜎 ∈ 𝑇
*
is a firing sequence of (𝑁, 𝑚0
) iff 𝜎 = 𝜖, or markings 𝑚0
, …, 𝑚𝑛
exist
such that (𝑁, 𝑚0
)
𝜎(𝑖)
←←←←←←←←←←←←→ (𝑁, 𝑚𝑖
) for 1 ≤ 𝑖 ≤ |𝜎| = 𝑛, and is denoted by
(𝑁, 𝑚0
)
𝜎
←←←←←←→ (𝑁, 𝑚𝑛
). If the context is clear, we omit 𝑁, and just write
𝑚0
𝜎
←←←←←←→ 𝑚𝑛
.
The set of reachable markings of (𝑁, 𝑚) is defined by (𝑁, 𝑚) =
{𝑚′
∣ ∃𝜎 ∈ 𝑇
* ∶ (𝑁, 𝑚0
)
𝜎
←←←←←←→ (𝑁, 𝑚𝑛
)}.
The semantics of a marked Petri net 𝑀𝑃 𝑁 = (𝑁, 𝑚0
) with 𝑁 =
(𝑃 , 𝑇 , 𝐹) is defined by the LTS
𝑁,𝑚0
= (𝑃
⊕, 𝑇 , 𝑚0
, →)
with (𝑚, 𝑡, 𝑚′
) ∈→ iff 𝑚
𝑡
←←→ 𝑚′
.
Definition 2.3 (Workflow Net [35]). A workflow net (WF-net) is a tuple
𝑁 = (𝑃 , 𝑇 , 𝐹 , 𝑖𝑛, 𝑜𝑢𝑡) such that:
(i) (𝑃 , 𝑇 , 𝐹) is a Petri net.
(ii) {𝑖𝑛, 𝑜𝑢𝑡} ⊆ 𝑃 , and ∙𝑖𝑛 = 𝑜𝑢𝑡∙
.
(iii) every node 𝑛 ∈ 𝑃 ∪ 𝑇 is on a directed path from 𝑖𝑛 to 𝑜𝑢𝑡.
Information and Software Technology 196 (2026) 108146
4
J. Caballero-Villalobos et al.
Fig. 2. A workflow net with seven places and eight transitions.
𝑁 is called sound for some 𝑘 ∈ 𝙽 iff
(i) it is proper completing, i.e., for all reachable markings 𝑘 ∈ N,
𝑚 ∈ (𝑁, [𝑖𝑛]
𝑘
), if [𝑜𝑢𝑡]
𝑘 ≤ 𝑚, then 𝑚 = [𝑜𝑢𝑡]
𝑘
;
(ii) it is weakly terminating, i.e., for any reachable marking 𝑚 ∈
(𝑁, [𝑖𝑛]
𝑘
), the final marking is reachable, i.e., [𝑜𝑢𝑡]
𝑘 ∈ (𝑁, 𝑚);
and
(iii) it is quasi-live, i.e., for all transitions 𝑡 ∈ 𝑇 , there is a marking
𝑚 ∈ (𝑁, [𝑖𝑛]) such that (𝑁, 𝑚)
𝑡
←←→ (𝑁, 𝑚′
).
𝑁 is called safe iff for all reachable markings 𝑚 ∈ (𝑁, [𝑖𝑛]) and
for all places 𝑝 ∈ 𝑃 , it holds that 𝑚(𝑝) ≤ 1.
WF-net example. Fig. 2 shows a workflow net (𝑁) for a construction
permit approval process (Section 1). Starting from the source place
(𝑖𝑛), the case is registered (𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋) and assessed (𝖠𝗌𝗌𝖾𝗌𝗌). From the
assessment place, the net allows three alternatives: 𝖠𝗉𝗉𝗋𝗈𝗏𝖾, 𝖣𝖾𝗇𝗒, or
request additional information. The latter triggers a loop that returns
the case to 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋, modeling an updated submission of the same
application followed by re-assessment. If no additional information
is required, the case proceeds to either approval or denial, and then
reaches the sink place (𝑜𝑢𝑡).
Starting with an initial marking 𝑚0 = [𝑝0
] that is, one token in 𝑝0
and zero tokens in all other places, the net is safe. The preset and postset
are read from the flow relation 𝐹. For instance, ∙
𝑡2 = {𝑝1
} and 𝑡
∙
2
= {𝑝2
},
because 𝐹 contains the arcs (𝑝1
, 𝑡2
) and (𝑡2
, 𝑝2
).
Moreover, the execution of the net is expressed using the transition
relation of the induced LTS (𝑁,𝑚0
). From 𝑚0 = [𝑝0
], transition 𝑡1
is
enabled because there exists a marking 𝑚1
such that 𝑁 ⊢ 𝑚0
𝑡1
←←←←←←←←→ 𝑚1
.
In this net, firing 𝑡1
consumes the token from 𝑝0 and produces a token
in 𝑝1
, hence [𝑝0
]
𝑡1
←←←←←←←←→ [𝑝1
]. Therefore, [𝑝1
] is a reachable marking from
the initial place, ([𝑝1
] ∈ (𝑁, [𝑖𝑛])), and [𝑝0
]
𝑡1
←←←←←←←←→ [𝑝1
] belongs to the
transition relation, that is ([𝑝0
], 𝑡1
, [𝑝1
]) ∈→.
A further reachable marking is obtained by continuing with the
labeled steps [𝑝1
]
𝑡2
←←←←←←←←→ [𝑝2
]
𝑡3
←←←←←←←←→ [𝑝3
]. From [𝑝3
], the net allows two
successor markings, depending on which transition fires next. For
example, [𝑝3
]
𝑡5
←←←←←←←←→ [𝑝5
]
𝑡7
←←←←←←←←→ [𝑝6
], which reaches the sink place. The loop
for requesting additional information is represented by [𝑝3
]
𝑡4
←←←←←←←←→ [𝑝1
],
after which the case is routed back to 𝑝1
for another cycle. All the
previous step sequences belong to 𝑇
*
, as a set of finite sequences
over the transition set 𝑇 . For this net, 𝜎 = ⟨𝑡1
, 𝑡2
, 𝑡3
, 𝑡5
, 𝑡7
⟩ ∈ 𝑇
*
is
a firing sequence because there exist markings 𝑚0
, …, 𝑚6
such that
𝑚0
𝑡1
←←←←←←←←→ 𝑚1
𝑡2
←←←←←←←←→ 𝑚2
𝑡3
←←←←←←←←→ 𝑚3
𝑡5
←←←←←←←←→ 𝑚4
𝑡6
←←←←←←←←→ 𝑚5
𝑡8
←←←←←←←←→ 𝑚6
, with 𝑚0 = [𝑝0
] and
𝑚6 = [𝑝6
]. In other words, from the initial marking we can eventually
reach 𝑚5
; formally expressed as 𝑚0
←←→* 𝑚6
In this example, the net is sound. First, it is proper completing since
whenever 𝑜𝑢𝑡 is reached, the marking contains only the token in 𝑜𝑢𝑡.
Second, it is weakly terminating because from every reachable marking
there exists a firing sequence that reaches 𝑜𝑢𝑡. Finally, it is quasi-live
since each transition 𝑡 ∈ 𝑇 is enabled in at least one reachable marking,
so no transition is dead.
2.1.3. Declarative process modeling
This section introduces Dynamic Condition Response (DCR) graphs;
here we restrict our expressive power to the set of constraints presented
in the classical definition for the graph in [19]. In our presentation, we
will use a process term semantics introduced in [10]. We assume a fixed
universe of events  ranged over 𝑒, 𝑓, and labels . Let 𝜆 ∶  →  be
the labeling function from events to labels. We assume that event labels
are unique; that is, for all 𝑒, 𝑓 ∈ 𝖿𝖾(𝑇 ), 𝜆(𝑒) ≠ 𝜆(𝑓) ∨ 𝑒 = 𝑓.
Definition 2.4 (Dynamic Condition Response (DCR) Graphs [10]). A
DCR process, written 𝑃 = [𝖬] 𝜆 𝑇 comprises a term 𝑇 , the labeling
function 𝜆, and the marking, 𝖬. We often write a DCR process P as
[𝖬] 𝑇 , omitting the labeling function since it rarely changes.
A DCR process1 defines process behavior as finite or infinite sequences over events. The marking 𝖬, is a finite map from events 𝑒 ∈ 
to triples (ℎ, 𝑖, 𝑝), called the event state (𝛷), where ℎ, 𝑖, and 𝑝 range
over boolean values {𝖿, 𝗍}. The marking indicates whether an event has
happened (ℎ), is included (𝑖), and is pending (𝑝). We write 𝖽𝗈𝗆(𝖬) for
the domain of 𝖬 and take 𝖽𝗈𝗆([𝖬] 𝑇 ) = 𝖽𝗈𝗆(𝖬). We represent 𝖬 as a
finite list of event–state pairs 𝖬 = 𝑒1 ∶ 𝛷1
,… , 𝑒𝑘 ∶ 𝛷𝑘
. Adding a new
pair (𝑒 ∶ 𝛷) to 𝖬, written 𝖬, 𝑒 ∶ 𝛷, is defined only if 𝑒 ∉ 𝖽𝗈𝗆(𝖬). The
set of free events of 𝑇 , written 𝖿𝖾(𝑇 ) ⊆ , is simply the set of events
appearing in it.
A term 𝑇 represents a parallel composition of constraint and effect
relations between events, generated by the grammar
𝑇 ∶∶= 0 ∣ 𝑒 →∙ 𝑒
′
∣ 𝑒 ∙→ 𝑒
′
∣ 𝑒 →+𝑒
′
∣ 𝑒 →%𝑒
′
∣ 𝑇 ∥ 𝑇 ,
where 0 denotes the empty term. The relation types are:
1. A condition relation 𝑒 →∙ 𝑒
′
, say if 𝑒 is included, then 𝑒 must have
been executed before 𝑒
′
can be executed;
2. A response relation 𝑒 ∙→ 𝑒
′
, say if 𝑒 is executed eventually 𝑒
′ must
be executed or excluded, we called 𝑒
′ a pending event;
3. An inclusion relation 𝑒 →+𝑒
′
, say execute 𝑒 includes 𝑒
′
; and
4. An exclusion relation 𝑒 →%𝑒
′
, say execute 𝑒 excludes 𝑒
′
.
In our representation, we consider the events in the term 𝑇 as
actions of the system. Fig. 4 shows a DCR process [𝖬] 𝑇 for a construction permit approval process (Section 1). Recalling the description
of the specification after an application is registered, the authority
performs an assessment to determine whether additional information
is required. If additional information is needed, the registration details
may be updated, and the assessment can be repeated. If no further
information is required, the application proceeds to a final decision,
that is, approval or rejection. Once a decision has been made, it cannot
be revised; therefore, approve and deny can occur at most once and
only after the assessment.
1 We use the terms DCR process and DCR graph interchangeably throughout
this paper.
Information and Software Technology 196 (2026) 108146
5
J. Caballero-Villalobos et al.
Fig. 3. Enabledness and effects. We write _ as a shorthand for a value in {𝗍,𝖿}.
Definition 2.5 (Enabledness and Local Effects [10]). Enabledness and
local effects judgment, written [𝑀] 𝑇 ⊢ 𝑒 ∶ 𝛿, where 𝛿 is an effect, is
ruled by the specifications in Fig. 3.
[𝖬] 𝑇 ⊢ 𝑒 ∶ 𝛿 should be read as in marking 𝖬, the term 𝑇 allows
𝑒 to happen with the effect 𝛿 = (𝖤𝗑𝖼, 𝖨𝗇𝖼, 𝖯𝖾𝗇), of excluding a set of
events (𝖤𝗑𝖼), including a set of events (𝖨𝗇𝖼), and put as pending a set
of events (𝖯𝖾𝗇). Overall, a DCR event is enabled in a given marking if it
is included and all the prerequisites have been executed. For example,
in Fig. 4 the initial marking, only 𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 is enabled because it has
no prerequisites; graphically, it is shown with a solid border to indicate
inclusion, whereas 𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌, 𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾, and 𝖾𝟦∶ 𝖣𝖾𝗇𝗒 are shown with
dashed borders to indicate they are excluded.
Fig. 3 shows the enabledness and effects. For instance, rule (3)
implies that for all the events 𝑒 that have a condition relation with 𝑓,
𝑒 →∙ 𝑓, all the events 𝑒 that are currently included need to be executed
before 𝑓 can be enabled (𝑖 ⇒ ℎ). Moreover, recalling the for of a DCR
process [𝖬] 𝑇 the conclusion of the rule means that in a marking 𝖬
to events 𝑒 ∶ (ℎ, 𝑖,_) where h and i must be 𝗍, that is included and
has happened, and _ we do not care about the value, therefore can
be either {𝗍,𝖿}. If all the events 𝑒 that have a condition relation with
𝑓, 𝑓 is enabled with an effect 𝛿 = (𝖤𝗑𝖼, 𝖨𝗇𝖼, 𝖯𝖾𝗇), as a condition only
evaluates prerequisites, which do not trigger any changes in the set of
excluded, included, or pending events, written 𝑓 ∶ {∅, ∅, ∅}. Therefore,
rules presented in Fig. 3 should be read as follows:
1. Rule (1): for an unconstrained process 0, an event 𝑒 can occur
iff 𝑒 is currently included.
2. Rule (2): a relation permits any included event 𝑒 to occur with no
effects whenever 𝑒 is not the relation’s right-hand side (target)
event.
3. Rule (3): if 𝑒 is a condition for 𝑓, then 𝑓 can occur only if 𝑓 is
included and, whenever 𝑒 is included, 𝑒 has previously occurred.
4. Rule (4): if 𝑓 is a response to 𝑒, then 𝑒 can occur with the effect
of making 𝑓 pending.
5. Rule (5): if 𝑓 is included by 𝑒 and 𝑒 is included, then 𝑒 can occur
with the effect of including 𝑓.
6. Rule (6): if 𝑓 is excluded by 𝑒 and 𝑒 is included, then 𝑒 can occur
with the effect of excluding 𝑓.
7. Rule (7): if both terms 𝑇1 and 𝑇2 allow an event to execute, then
the event’s effect is the union of the effects induced by 𝑇1 and
𝑇2
.
For example, in Fig. 4 there is an exclusion relation 𝖾𝟥 →% 𝑒𝟦
. By
applying rule (6), it means that if 𝖾𝟥
is executed, 𝖾𝟦 will be excluded.
The execution of an enabled event updates the marking in two steps.
The first step updates only the executed event. The event update on a
marking 𝖬 is defined inductively by:
𝑒⟨𝖬⟩ =
⎧
⎪
⎨
⎪
⎩
𝜖 if 𝖬 = 𝜖,
𝑒⟨𝖬′
⟩, 𝑓 ∶ (𝗍, 𝑖,𝖿) if 𝖬 = 𝖬′
, 𝑓 ∶ (ℎ, 𝑖, 𝑝) ∧ 𝑓 = 𝑒,
𝑒⟨𝖬′
⟩, 𝑓 ∶ (ℎ, 𝑖, 𝑝) if 𝖬 = 𝖬′
, 𝑓 ∶ (ℎ, 𝑖, 𝑝) ∧ 𝑓 ≠ 𝑒.
The second step applies the induced effect 𝛿 to the resulting marking.
The effect application on a marking 𝖬 is defined by:
𝛿⟨𝖬⟩ =
{
𝜖 if 𝖬 = 𝜖,
𝛿⟨𝖬′
⟩, 𝑓 ∶ (ℎ, 𝑖′
, 𝑝′
) if 𝖬 = 𝖬′
, 𝑓 ∶ (ℎ, 𝑖, 𝑝),
where
𝑖
′ =
(
(𝑖 ∧ 𝑓 ∉ 𝖤𝗑𝖼) ∨ 𝑓 ∈ 𝖨𝗇𝖼)
𝑝
′ =
(
𝑝 ∨ 𝑓 ∈ 𝖯𝖾𝗇)
.
The combined update is denoted by 𝛿⟨𝑒⟨𝖬⟩⟩.
2 Therefore, execute an
enabled event 𝑒 has the implications of (i) add 𝑒 to the set of executed
events, and update the set of pending events (𝖯𝖾𝗇) by removing 𝑒,
written 𝑒⟨𝖬⟩; (ii) add any pending events triggered by 𝑒; and update
the set of including events, by first removing all the ones excluded by
𝑒, and then add all the ones included by 𝑒, written 𝛿⟨𝖬⟩.
A process execution of a DCR process is defined by the following
transition rule:
[𝖬] 𝑇 ⊢ 𝑒 ∶ 𝛿
𝑇 ⊢ 𝖬
𝑒
←←←→ 𝛿⟨𝑒⟨𝖬⟩⟩
[EVENT]
Rule [EVENT] defines a successor marking 𝖬′ = 𝛿⟨𝑒⟨𝖬⟩⟩ whenever
e is enabled [𝖬] 𝑇 ⊢ 𝑒 ∶ 𝛿. We write this step as 𝑇 ⊢ 𝖬
𝑒
←←←→ 𝖬′
. We write
←←→*
for the reflexive transitive closure of ←←→.
Definition 2.6 (LTS DCR Graphs). Let ([𝖬]𝑇 ) be the set of reachable
configurations of the process, defined as
([𝖬′
]𝑇 ) = {[𝖬′
]𝑇 ∣ 𝑇 ⊢ 𝖬 ←←→* 𝖬′
}.
The semantics of a DCR process [𝖬] 𝑇 is given as a labeled transition
system, written
𝐺 = ⟨([𝖬] 𝑇 ), , ←←→, [𝖬] 𝑇 ,, 𝜆⟩.
2 We use 𝛿⟨𝑒⟨𝖬⟩⟩ and 𝖬′
interchangeably throughout this paper.
Information and Software Technology 196 (2026) 108146
6
J. Caballero-Villalobos et al.
Fig. 4. A DCR graph with four events and four relation types.
Definition 2.7 (Runs, Accepting Runs, and Traces [10]). A run of 𝐺 is
a finite or infinite sequence of transitions
[𝖬] 𝑇 = [𝖬0
] 𝑇
𝑒0
←←←←←←←←←→ [𝖬1
] 𝑇
𝑒1
←←←←←←←←←→ ⋯ .
A run is accepting if for every state [𝖬𝑖
] 𝑇 , whenever
𝖬𝑖
(𝑒) = (_, 𝗍, 𝗍),
there exists 𝑗 ≥ 𝑖 such that either [𝖬𝑗
] 𝑇
𝑒
←←←→ [𝖬𝑗+1
] 𝑇 , or 𝖬𝑗
(𝑒) = (_,𝖿,_).
A trace of [𝖬] 𝑇 is a finite or infinite string 𝑠 = (𝑠𝑖
)
𝑖∈𝖨𝗇𝖼 such that
[𝖬] 𝑇 has an accepting run
[𝖬𝑖
] 𝑇
𝑒𝑖
←←←←←←→ [𝖬𝑖+1
] 𝑇
with 𝑠𝑖 = 𝜆(𝑒𝑖
).
The language of [𝖬] 𝑇 is defined as
𝗅𝖺𝗇𝗀([𝖬] 𝑇 ) = {𝑠 ∣ 𝑠 is a trace of [𝖬] 𝑇 }.
The DCR LTS (𝐺) represents the process as a set of reachable markings connected by enabled event executions. The acceptance condition
means that pending obligations cannot be ignored forever. Whenever
an event is both pending and included at some point during a run, the
run must eventually either execute that event or exclude it. Since labels
are omitted, a trace is simply the sequence of executed events along
an accepting run, and the language is the set of all such traces. For
instance, ⟨𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋, 𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌, 𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋, 𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌, 𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾⟩
it is an accepting trace of the graph in Fig. 4. Table 1 presents a
snapshot of the state space (reachable markings) for the DCR process
presented in Fig. 4, where each row represents a reachable marking.
For easier reading of changes in the tuple (ℎ, 𝑖, 𝑝), we write 𝖤𝗑𝖾𝖼𝗎𝗍𝖾𝖽 for
tuples of the form (𝗍,_,_), where _ indicates that the values of the other
components are irrelevant in that context. The intuition is that only the
fact that the event has happened is used, independently of whether it
is included or pending. We use the same convention for the included
and pending components, writing (_, 𝗍,_) and (_,_, 𝗍), respectively.
For instance, in Fig. 4, starting from the initial marking (𝖬0
), only
𝖾𝟣
is included, so the only enabled step is 𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋. Executing 𝖾𝟣
moves the process to 𝖬1
, where 𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 becomes pending (reflecting the obligation to perform an assessment) and the decision events
𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 and 𝖾𝟦∶ 𝖣𝖾𝗇𝗒 become included; however, only 𝖾𝟤
is enabled
because approval and denial require the assessment to have occurred.
After executing 𝖾𝟤
, the process reaches 𝖬2
, where the pending set is
empty and 𝖾𝟣
, 𝖾𝟥
, and 𝖾𝟦 are enabled, meaning the process may either
repeat registration (update information) or proceed to a decision. As
another example, consider 𝖬5
, which shows the effect of deciding by
Table 1
Excerpt of reachable markings for the running example.
(h,i,p) (𝗍,_,_) (_, 𝗍,_) (_,_, 𝗍) [𝖬] 𝑇 ⊢ 𝑒 ∶ 𝛿
Executed 𝖬 𝖤𝗑𝖾𝖼𝗎𝗍𝖾𝖽 𝖨𝗇𝖼𝗅𝗎𝖽𝖾𝖽 𝖯𝖾𝗇𝖽𝗂𝗇𝗀 𝖾𝗇𝖺𝖻𝗅𝖾𝖽?
∅ 𝖬𝟢 ∅ 𝖾𝟣 ∅ 𝖾𝟣
𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝖬𝟣
𝖾𝟣
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟤
𝖾𝟤
𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝖬𝟤
𝖾𝟣
, 𝖾𝟤
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦 ∅ 𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝖬𝟥
𝖾𝟣
, 𝖾𝟤
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟤
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝖬𝟤
𝖾𝟣
, 𝖾𝟤
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦 ∅ 𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 𝖬𝟦
𝖾𝟣
, 𝖾𝟤
, 𝖾𝟥
𝖾𝟣 ∅ 𝖾𝟣
executing 𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾: the decision is irreversible and disables further
decision-making, which is reflected by the absence of 𝖾𝟥 and 𝖾𝟦
from
the enabled set.
Behavior. A DCR process 𝑃 = [𝖬] 𝑇 , exhibits non-empty behavior if
∣ 𝗅𝖺𝗇𝗀(𝑃 ) ∣ ≥ 0, it has no dead activities if every event that occurs in 𝑇
can occur in at least one run, that is, ∀𝑒 ∈ 𝖿𝖾(𝑃 ), 𝑇 ⊢ 𝖬
𝑒
←←←→
*
𝖬′
, and
it is deadlock-free if for all reachable markings 𝖬′
such that 𝖬 ←←→* 𝖬′
,
if there exists an event 𝑒 with 𝖬′
(𝑒) = (_, 𝗍, 𝗍) (i.e., 𝑒 is both included
and pending), then there exists an event 𝑒
′ ∈ 𝖿𝖾(𝑃 ) and a marking 𝖬′′
such that 𝖬′
𝑒
′
←←←←←←→ 𝖬′′ [29]. The DCR graphs in Fig. 4 exhibit non-empty
behavior, no dead activities, and are deadlock-free.
2.2. Goal models
Goal models support the representation of stakeholder objectives,
alternative courses of action, and quality concerns that guide decisionmaking. There are multiple goal modeling languages, such as Tropos [36], Goal Requirements Language [37], KAOS [38], iStar [39].
We chose iStar because it is sufficient to capture the modeling needs.
First, iStar provides sufficient expressiveness to model goals (what
should hold), tasks (alternative ways of acting), and qualities (nonfunctional/high-level concerns), together with contribution links that
make the impact on qualities observable, which is central to our
compliance analysis. Second, iStar is widely used in requirements
engineering [40] and supported by robust modeling conventions and
tooling [20], which facilitates communication and replicability of the
research. In this work, we adopted a snapshot of the elements introduced in the latest iStar version [20]. The formalization of the subset
of elements is an adaptation of the work done by Giorgini et al. [21],
for a goal graph referred to in this paper as goal model.
Information and Software Technology 196 (2026) 108146
7
J. Caballero-Villalobos et al.
Fig. 5. Goal model - Running example (Section 1).
Definition 2.8 (Goal Model). A goal model is a tuple
𝐺𝑀 ∶= (𝐼𝐸, 𝐿),
where 𝐼𝐸 is a finite set of intentional elements and 𝐿 is a finite set of
links. The set of intentional elements is partitioned as
𝐼𝐸 ∶= 𝐺 ⊎ 𝑇 ⊎ 𝑄,
where
• 𝑇 = {𝑡1
,… , 𝑡𝑛
} is a finite set of tasks.
• 𝐺 = {𝑔1
, …, 𝑔𝑚} is a finite set of goals.
• 𝑄 = {𝑞1
, …, 𝑞𝑘
} is a finite set of qualities.
For convenience, given 𝐺𝑀, we define the projection functions
𝖦𝗈𝖳(𝐺𝑀) ∶= 𝐺 ⊎ 𝑇 and 𝖰𝗎𝖺𝗅(𝐺𝑀) ∶= 𝑄.
A goal model model contains a finite set of elements (goals, tasks,
and qualities) and two kinds of links between them: refinement links
explain how goals or tasks are structured into sub-goals or sub-tasks
(either all are needed (𝖠𝖭𝖣) or one option is enough (𝖮𝖱), and contribution links record whether pursuing a goal or performing a task
supports (𝖬𝖺𝗄𝖾) or harms (𝖡𝗋𝖾𝖺𝗄) a quality. Compared with the original
version [20], which provides two complementary views: the strategic
dependency view, which captures inter-actor dependencies, and the
strategic Rationale view, which captures an actor’s internal reasoning.
We adopted the Strategic Rationale perspective and focused on a single
actor at a time to make explicit the goals, tasks, and elements relations
relevant to the analysis. We therefore do not model other actors.
Therefore, inter-actor dependencies are outside the scope of this study.
Goal model - example. Fig. 5 shows the goal model based to represent
the specification and municipality’s goal (Section 1) with one quality
( ), three goals ( ), four tasks ( ), refinements of type 𝖠𝖭𝖣
( ) and 𝖮𝖱 ( ), and contribution links 𝖬𝖺𝗄𝖾 and 𝖡𝗋𝖾𝖺𝗄. Notice that
we do not enforce any precedence or execution order in the goal model
(i.e., assess the application can only occur after register application).
Ordering constraints are captured by the process models. Here, we
only represent stakeholders’ goals and how tasks contribute to their
satisfaction.
3. Goal model reasoning and mapping
This section introduces the operational semantics used to evaluate
goal satisfaction during compliance checking, based on a marking that
captures the satisfaction status of goals, tasks, and qualities. It also
defines a mapping that establishes a correspondence relation between
process action and the intentional elements, Goal model reasoning
has been studied using techniques such as satisfiability-based semantics [16], where all the constraints are evaluated at once to determine
the satisfaction of the goals and state-based semantics [21,22], where
satisfaction is represented as evidence-pairs (evolving states) that can
be updated over time. In our framework, we adopt a state-based
(marking) semantics.
Definition 3.1 (Goal Model Marking). A goal model marking is set of
pairs of intentional elements an their status
m̂ ⊆ 𝐼𝐸 × 𝐸,
where 𝐼𝐸 is the set of intentional elements and 𝐸 is the set of satisfaction values defined as
𝐸 ∶= 𝛥 ⊎ (𝛥 × 𝛥), where 𝛥 = {⊥, ⊤, ?}
is the set of values (‘‘true’’, ‘‘false’’, and ‘‘unknown’’).
A goal marking (m̂ ) satisfies: A goal marking (m̂ ) satisfies:
(i) ∀𝑒 ∈ 𝐼𝐸. ∃! 𝑣 ∈ 𝐸 ∣ (𝑒, 𝑣) ∈ m̂ ;
(ii) 𝑒 ∈ 𝖦𝗈𝖳(𝐺𝑀) ∧ (𝑒, 𝑣) ∈ m̂ ⇒ 𝑣 ∈ {(⊤, ⊤), (⊤, ⊥), (?, ?)};
(iii) 𝑒 ∈ 𝖰𝗎𝖺𝗅(𝐺𝑀) ∧ (𝑒, 𝑣) ∈ m̂ ⇒ 𝑣 ∈ {⊤, ⊥, ?}.
For all the intentional elements in the goal model the initial marking
is
m̂0 = {(𝑒, (?, ?)) ∣ 𝑒 ∈ 𝖦𝗈𝖳(𝐺𝑀)} ∪ {(𝑒, ?) ∣ 𝑒 ∈ 𝖰𝗎𝖺𝗅(𝐺𝑀)}.
A goal marking update is defined by
m[ ̂ 𝑒 ↦ 𝑣] ∶= (
m̂ ⧵ {(𝑒, 𝑤) ∣ (𝑒, 𝑤) ∈ m}̂
)
∪ {(𝑒, 𝑣)},
where (𝑒, 𝑤) is the previous marking.
The set of marking values should be read as follows: Goals and Tasks:
(i) (𝑒, (?, ?)) means the achievement of 𝑒 unknown; (ii) (𝑒, (⊤, ⊥)) means
the achievement of 𝑒 fulfilled; (iii) (𝑒, (⊤, ⊤)) means the achievement of
𝑒 is pending (𝑒 must be done again). That is, 𝑒 has been fulfilled at some
earlier point, but that fulfillment is no longer valid in the current state
(i.e., a later event breaks a quality that was previously achieved by a
contribution of 𝑒). Qualities: (i) (𝑞, ?) means the status of 𝑞 unknown;
(ii) (𝑞, ⊤) fulfilled means the status of 𝑞 is fulfilled ; (iii) (𝑞, ⊥) means
the status of 𝑞 is denied. The marking update m[ ̂ 𝑒 ↦ 𝑣] removes the old
pair (𝑒, 𝑤) and adds the new pair (𝑒, 𝑣). For instance, an excerpt of the
initial marking of the graph in Fig. 5 is m̂0 = {(𝗍𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾, (?, ?)), …,
(𝗀𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝗋𝖺𝗍𝗂𝗈𝗇 𝖢𝗈𝗆𝗉𝗅𝖾𝗍𝖾𝖽, (?, ?)), … , (𝗊𝟣∶ 𝖨𝗇𝖼𝗋𝖾𝖺𝗌𝖾 𝖼𝗂𝗍𝗒 𝖻𝗎𝗌𝗂𝗇𝖾𝗌𝗌 𝗀𝗋𝗈𝗐𝗍𝗁, ?)}
To clarify the relations among intentional elements, we introduce
the notion of parenthood.
Definition 3.2 (Refinement, Parents, and Leaf Nodes). Let 𝐺𝑀 = (𝐼𝐸, 𝐿)
be a goal model. For any 𝑒 ∈ 𝖦𝗈𝖳(𝐺𝑀), the sets of parents (Par(𝑒)) and
children (Ch(𝑒)) of an intentional element 𝑒 are defined by
Par(𝑒) ∶= {𝑒
′ ∈ 𝖦𝗈𝖳(𝐺𝑀) ∣ (𝑒, 𝑒′
) ∈ 𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀)}
Information and Software Technology 196 (2026) 108146
8
J. Caballero-Villalobos et al.
Ch(𝑒) ∶= {𝑒
′ ∈ 𝖦𝗈𝖳(𝐺𝑀) ∣ (𝑒
′
, 𝑒) ∈ 𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀)},
An element 𝑒 ∈ 𝖦𝗈𝖳(𝐺𝑀) is called leaf node iff
Ch(𝑒) = ∅.
A refinement link means that a child element refines (breaks down)
a parent element: the parent is refined by its children. Any refinement
link (𝑒, 𝑒′
) ∈ 𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀) represents a link from 𝑒 to 𝑒
′
, where 𝑒 is
the child and 𝑒
′
is the parent. When the parenthood concept makes
explanations more comprehensible, we write (𝑐, 𝑝) to represent the same
relation, with 𝑐 the child and 𝑝 the parent. The set of parents lists what
an element contributes to, while the set of children lists what refines
an element, and a leaf node is an element that has no children. For
instance, in Fig. 5 𝗀𝟣 its the parent in the refinement link (𝗍𝟣
, 𝗀𝟣
).
Definition 3.3 (Top-down Reachability Relation). Let 𝐺𝑀 = (𝐼𝐸, 𝐿) be
a goal model. The top-down reachability relation
↪ ⊆ 𝐼𝐸 × 𝐼𝐸
is the smallest relation such that
(i) (𝑐, 𝑝) ∈ 𝐿 ⇒ 𝑝 ↪ 𝑐; and
(ii) 𝑝 ↪ 𝑐 ∧ 𝑐 ↪ 𝑐
′ ⇒ 𝑝 ↪ 𝑐
′
.
Its reflexive and transitive closure ↪*
is defined by
(i) ∀𝑝 ∈ 𝐼𝐸 ∶ 𝑝 ↪* 𝑝; and
(ii) 𝑝 ↪*
𝑐 ∧ 𝑐 ↪*
𝑐
′ ⇒ 𝑝 ↪*
𝑐
′
.
The top-down reachability relation describes how to move downward in the goal model: from a parent to its children. The relation
↪ is used to obtain, for any given parent element, the set of all
elements that contribute to or refine it along one or more link steps.
For example, in Fig. 5, the goal 𝗀𝟦 ∶ 𝖠𝗉𝗉𝗅𝗂𝖼𝖺𝗍𝗂𝗈𝗇 𝖼𝗅𝗈𝗌𝖾𝖽 (𝑝) is refined
through 𝗀𝟤 ∶ 𝖠𝗉𝗉𝗅𝗂𝖼𝖺𝗍𝗂𝗈𝗇 𝖠𝗌𝗌𝖾𝗌𝗌𝖾𝖽 (𝑐), which includes a tasks such as
𝖠𝗌𝗌𝖾𝗌𝗌 𝖠𝗉𝗉𝗅𝗂𝖼𝖺𝗍𝗂𝗈𝗇 (𝑐
′
); thus, 𝑝 ↪*
𝑐
′
.
Definition 3.4 (Well-formed Goal Model). Let 𝐺𝑀 = (𝐼𝐸, 𝐿) be a goal
model. 𝐺𝑀is called well-formed iff:
(i) 𝐺𝑀 is Directed Acyclic Graph
(ii) |𝐺𝑜𝑇 | ≥ 1, |𝑄| ≥ 1, and ∃𝑒 ∈ 𝐺𝑜𝑇 , 𝑞 ∈ 𝑄 ∶ 𝐶(𝑒, 𝑞) = 𝖬𝖺𝗄𝖾;
(iii) ∀𝑒1
, 𝑒2
, 𝑒 ∈ 𝐺𝑜𝑇 ∣
(
(𝑒1
, 𝑒) ∈ 𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀) ∧ (𝑒2
, 𝑒) ∈
𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀)
)
⇒ 𝑅(𝑒1
, 𝑒) = 𝑅(𝑒2
, 𝑒)
(iv) ∀𝑞, 𝑒1
, 𝑒2
∣ 𝐶(𝑒1
, 𝑞) ≠ 𝐶(𝑒2
, 𝑞) ⇒
(
{𝑐 ∣ 𝑒1 ↪*
𝑐} ∩ {𝑐 ∣ 𝑒2 ↪*
𝑐} =
∅
)
.
Fig. 5 depicts a well-formed goal model, satisfying conditions
(i)–(iv).
1. The goal model contains no refinement cycles: no sequence of
refinement links starts from an intentional element and returns
to it. In particular, no pair of distinct intentional elements refines
each other, and refinement links are oriented from children
to parents only, which ensures acyclicity as required by (i).
Hence, the acyclicity of the graph also implies asymmetry of
the links. For any 𝑒, 𝑒′
, if (𝑒, 𝑒′
) ∈ 𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀) then (𝑒
′
, 𝑒) ∉
𝖱𝖾𝖿𝖫𝗂𝗇𝗄𝗌(𝐺𝑀); analogously, if (𝑒, 𝑒′
) ∈ 𝖢𝗈𝗇𝗍𝗋𝗂𝖻𝖫𝗂𝗇𝗄𝗌(𝐺𝑀) then
(𝑒
′
, 𝑒) ∉ 𝖢𝗈𝗇𝗍𝗋𝗂𝖻𝖫𝗂𝗇𝗄𝗌(𝐺𝑀).
2. The model meets the minimal goal model elements in (ii): it
includes at least one intentional element and at least one quality
node that receives at least one positive contribution.
3. For every parent, all of its children are connected through the
same refinement type, that is, each refinement set is consistently
𝖠𝖭𝖣-refined or 𝖮𝖱-refined.
4. The contribution structure respects the non-conflict constraint:
elements with different contribution types to the same quality
do not share children. This prevents conflicting labels on the
(a) Conflicting contribution.
(b) No conflicting contribution.
Fig. 6. Examples of contributions: (a) conflicting and (b) non-conflicting.
same quality from appearing within a single refinement branch
(Fig. 6a). As a result, conflicts can only arise between alternative
branches (Fig. 6(b)), thereby satisfying (iv).
The semantics of a goal model 𝐺𝑀 is given by its induced labeled
transition system.
Definition 3.5 (Labeled Transition System - Goal Model). Let 𝐺𝑀 =
(𝐼𝐸, 𝐿) be a goal model, an element 𝑒 ∈ 𝐼𝐸 is enabled in a marking
m̂ , written 𝐺𝑀 ⊢ m̂
𝑒
←←←→ m̂
′
iff ∃ 𝑣 ∈ 𝐸. (𝑒, 𝑣) ∈ m̂ .
An enabled element can be executed, resulting in marking m̂
′
iff
(m̂ , 𝑒, m̂
′
) ∈←←→ .
The transition relation ←←→⊆ M × 𝐼𝐸 × M is governed by the rules in
Fig. 7. We use ←←→*
for the transitive closure of ←←→.
The set of reachable markings from the initial marking is defined
by
M = {m̂
′
∣ m̂0
←←→* m̂
′
}.
The labeled transition system of a 𝐺𝑀 is written
𝐺𝑀 ∶= ⟨M, 𝐼𝐸, →, m̂0
⟩.
A run of 𝐺𝑀 a finite or infinite sequence of transitions
m̂0
𝑒0
←←←←←←←←←→ m̂
𝑖
𝑒𝑖
←←←←←←→ … .
A run is accepting iff ∀𝑞 ∈ 𝑄. (𝑞, ?) ∉ m̂ .
A trace (𝜎) of 𝐺𝑀 is a finite or infinite string 𝑠 = (𝑠𝑖
)
𝑖∈𝐼𝐸 such that
𝐺𝑀 has an accepting run m̂
𝑖
𝑒𝑖
←←←←←←→ m̂
𝑖+1 with 𝑠𝑖 = 𝑒𝑖
.
𝐺𝑀 is a state machine for a goal model. Each state is a snapshot of the current markings of the intentional elements, and each
action represents one allowed update, that is, an intentional element
contained in the goal model. The transition relation specifies which
updates can move the model from one snapshot to the next. A run of
𝐺𝑀 is accepting if all qualities are no longer unknown: each has been
decided as either fulfilled or denied. For readability, we write the goal
model as GM instead of 𝐺𝑀 = (𝐼𝐸, 𝐿).
Executing an event 𝑒 may change the status of the corresponding
intentional element and can enable further updates along refinement
relations (for example, from a child to its parent). Therefore, after firing
𝑒, we repeatedly apply the rules in Fig. 7 to propagate the change
through the goal model. This iterative propagation terminates when
Information and Software Technology 196 (2026) 108146
9
J. Caballero-Villalobos et al.
Fig. 7. Operational semantics for 𝐺𝑀 .
no rule is enabled; the obtained marking is the outcome of executing
𝑒. Rule ([𝑃ie]) marks a leaf node as satisfied directly, since it has no
children.
• Rule ([𝑃𝖠𝖭𝖣]) propagates satisfaction to a parent refined by 𝖠𝖭𝖣
only when all of its children are already satisfied, whereas ([𝑃𝖮𝖱])
propagates satisfaction to a parent refined by 𝖮𝖱 as soon as at
least one child is satisfied.
• Once a goal is satisfied, ([𝑃𝖬𝖺𝗄𝖾]) and ([𝑃𝖡𝗋𝖾𝖺𝗄]) update the associated quality when its value is still unknown: a 𝖬𝖺𝗄𝖾 contribution
sets the quality to true, while a 𝖡𝗋𝖾𝖺𝗄 contribution sets it to false.
• When the quality already has a value ([𝐵𝑃fulfill]) and ([𝐵𝑃deny]),
manage the update by allowing the quality to switch to the
value supported by the newly satisfied goal. At the same time,
they temporarily put in pending the satisfied goals that support
the opposite value, to prevent the quality from flipping back
(conflicts) immediately.
Notice that the operational semantics only allow a value to move
from unknown to a decided status. For goals and tasks, once something
becomes fulfilled (⊤, ⊥), it may later switch back and forth between
pending (⊤, ⊤) and fulfilled (⊤, ⊥). For qualities, once the value is no
longer unknown, it may first become fulfilled (⊤) or denied (⊥), and
then it can switch back and forth between fulfilled and denied.
Table 2 illustrates some of these reachable markings for the goal
model in Fig. 5 (running example), reflecting the forward and backward propagation and backtracking effects defined in the operational
semantics. For instance, after the execution of 𝑡1
(applying [P𝑖𝑒]), 𝑔1
is
also achieved by the application of [P𝖮𝖱].
This leads to the following definition:
Definition 3.6 (
𝑒
⇐⇐⇐⇐⇐⇒
*
). For a labeled transition system 𝐺𝑀 ∶=
⟨M, 𝐼𝐸, →, m̂0
⟩, we define the relation
𝑒
⇐⇐⇐⇐⇐⇒
*
⊆ M × 𝐼𝐸 × M for an event
𝑒 ∈ 𝐼𝐸 as follows: A transition m̂
𝑒
⇐⇐⇐⇐⇐⇒
*
m̂
′ holds if and only if there exists
a sequence of states m̂ 0
, m̂ 1
, … , m̂ 𝑛
in M such that:
Table 2
Excerpt of reachable markings for the goal model depicted in Fig. 5.
𝐼𝐸 𝖤𝗑𝖾𝖼𝗎𝗍𝖾𝖽 ↓ 𝖦𝗈𝖳(𝐺𝑀) 𝖰𝗎𝖺𝗅(𝐺𝑀)
𝖲𝗍𝖺𝗍𝗎𝗌 𝗏𝖺𝗅𝗎𝖾 (𝐸) → (?, ?) (⊤, ⊥) (⊤, ⊤) ? ⊤ ⊥
∅ 𝑡1
, 𝑡2
, 𝑡3
, 𝑡4
𝑔1
, 𝑔2
, 𝑔3
, 𝑔4
∅ ∅ 𝑞1 ∅ ∅
𝗍𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝑡2
, 𝑡3
, 𝑡4
, 𝑔2
𝑔3
, 𝑔4
𝑡1
, 𝑔1 ∅ 𝑞1 ∅ ∅
𝗍𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝑡3
, 𝑡4
, 𝑔3
, 𝑔4
𝑡1
, 𝑔1
, 𝑡2
, 𝑔2 ∅ 𝑞1 ∅ ∅
𝗍𝟦∶𝖣𝖾𝗇𝗒 𝑡3
𝑡1
, 𝑔1
, 𝑡2
, 𝑔2
𝑡4
, 𝑔3
, 𝑔4
∅ ∅ ∅ 𝑞1
𝗍𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝑡3
𝑡1
, 𝑔1
, 𝑡2
, 𝑔2
,
𝑡4
, 𝑔3
, 𝑔4
∅ ∅ ∅ 𝑞1
𝗍𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 ∅ 𝑡1
, 𝑔1
, 𝑡2
, 𝑔2
𝑔3
, 𝑔4
, 𝑡3
𝑡4 ∅ 𝑞1 ∅
1. m =̂ m̂ 0 and m̂
′ = m̂ 𝑛
.
2. For each 𝑖 ∈ {0, … , 𝑛 − 1}, there exists a transition m̂
𝑖
𝑒𝑖
←←←←←←→ m̂
𝑖+1
where 𝑒𝑖 ∈ 𝑃 𝑎𝑟(𝑒𝑖−1
) and 𝑒0 = 𝑒.
3. The final state, m̂
′
, is a terminal state with respect to 𝑒. This
means either 𝑛 > 0 and there is no state m̂
′′ ∈ 𝑆𝐺𝑀 and no event
𝑒
′′ ∈ 𝑃 𝑎𝑟(𝑒𝑛−1
) such that a transition m̂
′
𝑒
′′
←←←←←←←←←←→ m̂
′′ exists. Or 𝑛 = 0
and there does not exist a transition m̂ 0
𝑒0
←←←←←←←←←→ m̂
′
for some state
m ∈̂ 𝑆𝐺𝑀 and where 𝑒0 = 𝑒.
𝐺𝑀 does not designate final states. Instead, we identify a set of
success states, (M✓).
Definition 3.7 (Success States of a Goal Model). Let 𝐺𝑀 = (𝐼𝐸, 𝐿)
be a goal model, and 𝐺𝑀 ∶= ⟨M, 𝐼𝐸, →, m̂0
⟩ be its labeled transition
system, the set of success states M✓ is defined by:
M✓ ∶= { 𝑠 ∈ M ∣ ∀𝑞 ∈ 𝖰𝗎𝖺𝗅(𝐺𝑀) ⇒ m̂ n = (𝑞, ⊤) }.
Information and Software Technology 196 (2026) 108146
10
J. Caballero-Villalobos et al.
Table 3
Process action to intentional-element mapping, where 𝑁 row, corresponds to
WF-net transitions in Fig. 2, [𝖬] 𝑇 row, refers the DCR events in Fig. 4, and
each 𝚖𝚊𝚙 shows the intentional element 𝐼𝐸 assigned to a process action.
𝖭 Elements
𝗍𝟣
𝗍𝟤∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝗍𝟥∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝗍𝟦
𝗍𝟧
𝗍𝟨∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 𝗍𝟩∶ 𝖣𝖾𝗇𝗒 𝗍𝟪
𝗆𝖺𝗉 𝜖 𝗍𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝗍𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝜖 𝜖 𝗍𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 𝗍𝟦∶ 𝖣𝖾𝗇𝗒 𝜖
[𝖬] 𝖳 𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 𝖾𝟤∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝖾𝟥∶ 𝖠𝗉𝗉𝗋𝗈𝗏𝖾 𝖾𝟦∶ 𝖣𝖾𝗇𝗒
In other words, success states are those 𝑛-states (goal model markings) in which every quality is satisfied.
3.1. Mapping
This section defines the mapping between process actions and intentional elements of the goal model. The mapping serves as a correspondence relation only: it links each executed transition (or event) to
the goal or task whose satisfaction status is affected by that execution.
Importantly, the mapping does not impose the control-flow or precedence constraints of the process model on the goal model. Precedence
is handled exclusively by the process semantics during synchronization.
As we will explain in Section 4, synchronous moves are triggered only
by enabled process actions; therefore, only actions that are enabled
in the process model can be executed and, consequently, can induce
updates in the goal model via the mapping.
Definition 3.8 (Mapping between Process Actions and Intentional Elements). Let 𝙿𝙼 ∈ {[𝖬] 𝑇 , 𝑁} denote the process model type. Let 𝐺𝑀
be a goal model. The mapping from process activities to intentional
elements is defined as
𝗆𝖺𝗉 ∶= {
𝑇 → 𝖦𝗈𝖳(𝐺𝑀) ∪ {𝜖} if 𝙿𝙼 = 𝑁,
 → 𝖦𝗈𝖳(𝐺𝑀) if 𝙿𝙼 = [𝖬] 𝑇 ,
with
𝑚𝑎𝑝(𝑡) = 𝜖 ⟺ 𝑡 ∈ 𝑇 is a silent ,
where 𝑇 is the set of WF-net transitions,  the set of DCR events, 𝐺𝑜𝑇
the set of goals and tasks, and 𝜖 denotes an unmapped activity.
The mapping assigns each process action to the leaf node it operationally affects. Silent Workflow-net transitions have no business
meaning, so they may be left unmapped (𝜖). For instance, consider the
goal model in Fig. 5 and the Workflow net in Fig. 2. The transition
𝑡2
is mapped to the intentional element (task) 𝗍𝟤 ∶ 𝖠𝗌𝗌𝖾𝗌𝗌 𝖺𝗉𝗉𝗅𝗂𝖼𝖺𝗍𝗂𝗈𝗇,
while 𝑡1
is unmapped (𝜖). Table 3 shows the mapping for the remaining
elements of the WF-net and presents the mapping for the DCR events.
For instance, the event 𝖾𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 is mapped to the task 𝗍𝟣∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 in
the goal model.
4. Compliance assessment
This section introduces a goal-oriented compliance checking method
at design-time. The method evaluates whether the execution of process
actions can lead to system states in which all the high-level requirements modeled as qualities are fulfilled. To support this analysis, we
introduce the synchronous product of the labeled transition systems of
the process model and the goal model together with the corresponding
compliance criterion.
Definition 4.1 (Product Labeled Transition System). Let
𝙿𝙼 = (𝑆𝑃
, 𝐴𝑃
, 𝑠𝑃
0
, →𝑃
)
be a labeled transition system for a process model, like 𝐺 for a DCR
graph or 𝑁,𝑚0
for a WF-net. Let
𝐺𝑀 = ⟨M, 𝐼𝐸, →, m̂0
⟩
be an LTS of a goal model 𝐺𝑀.
Let 𝗆𝖺𝗉 be the mapping function such that
𝗆𝖺𝗉 ∶ 𝐴𝑃 → 𝐺𝑜𝑇 (𝐺𝑀) ∪ {𝜖}.
The synchronous product of 𝐺𝑀 and 𝙿𝙼 is defined as
𝐶 ∶= ⟨𝑆𝐶, 𝐴𝑃
, →𝐶, 𝑠𝐶
0
, 𝐹𝐶⟩,
with 𝑆𝐶 = M × 𝑆𝑃
, 𝑠
𝐶
0
= (m̂ 0
, 𝑠𝑃
0
), and ((m̂ , 𝑠), 𝑎, (m̂
′
, 𝑠′
)) ∈ →𝐶 if and
only if rules (1) and (2) apply.
We write
𝐹𝐶 = {⟨m̂ , 𝑠⟩ ∈ 𝑆𝐶 ∣ m ∈̂ M✓ ∧ 𝑠 ∈ 𝑆𝑝
}
for the set of final states.
𝑎 ∈ 𝐴𝑃
𝑒 ∈ 𝐼𝐸 𝗆𝖺𝗉(𝑎) = 𝑒 𝗆𝖺𝗉(𝑎) ≠ 𝜖
𝐺𝑀 ⊢ m̂
𝑒
⇐⇐⇐⇐⇐⇒
*
m̂
′ 𝙿𝙼 ⊢ 𝑠
𝑎
←←←←←→𝑃
𝑠
′
⟨
𝐺𝑀, 𝙿𝙼 ⟩
⊢ (m̂ , 𝑠)
𝑎
←←←←←→𝐶 (m̂
′
, 𝑠′
)
(1)
𝑎 ∈ 𝐴𝑃 𝗆𝖺𝗉(𝑎) = 𝜖 𝙿𝙼 ⊢ 𝑠
𝑎
←←←←←→𝑃
𝑠
′
⟨
𝐺𝑀, 𝙿𝙼 ⟩
⊢ (m̂ , 𝑠)
𝑎
←←←←←→𝐶 (m̂ , 𝑠′
)
(2)
Note that the relation
𝑒
⇐⇐⇐⇐⇐⇒
*
is defined in Definition 3.6.
The transitions of the product LTS are governed entirely by the
transitions of the process model. A product transition is enabled only if
the process model can execute an action 𝑎 according to its operational
semantics. For each such transition 𝑠
𝑎
←←←←←→𝑃
𝑠
′
, if 𝗆𝖺𝗉(𝑎) = 𝑒 ≠ 𝜖,
the corresponding intentional action 𝑒 is executed in the goal model,
updating its state from m̂ to a uniquely determined successor state m̂
′
.
Since the goal model exhibits no non-determinism – i.e., each goal
model state m̂ and action 𝑒 lead to exactly one successor state – there
exists exactly one corresponding product transition (m̂ , 𝑠)
𝑎
←←←←←→𝐶 (m̂
′
, 𝑠′
)
for every enabled process transition. If 𝗆𝖺𝗉(𝑎) = 𝜖 then m̂
′ = m̂ . Hence,
the goal model does not determine which actions are taken or in which
order; it merely reacts to the execution of the process model through the
mapping. Consequently, all product runs respect the semantics of the
process model, while the goal model reflects the updates along those
runs.
The construction of the product LTS and the definitions of stability,
weak compliance, and strong compliance are generic wrt. the process
model notation chosen. As an example, we have provided labeled
transition system semantics for workflow Petri nets (Definition 2.2)
and DCR graphs (Definition 2.6), but any process model notation can
be used, such as BPMN [41] or LTLf [42], whose semantics can be
expressed as a labeled transition system.
4.1. Goal-oriented design time compliance using workflow nets
In this section, we look at the instantiation of the product labeled
transition system 𝐶 for WF-nets graphs, i.e., where 𝙿𝙼 = 𝑁,𝑚0
=
(𝑃
⊕, 𝑇 , 𝑚0
, →), and present an excerpt of the synchronous moves instantiated for the running example. In this case, the mapping function
is 𝗆𝖺𝗉 ∶ 𝑇 → 𝐺𝑜𝑇 (𝐺𝑀) ∪ {𝜖}
Table 4 shows an execution fragment of the product labeled transition system built from the goal model in Fig. 5, the Workflow net
in Fig. 2, and the mapping in Table 3. After firing the transition 𝑡2 ∶
𝑅𝑒𝑔𝑖𝑠𝑡𝑒𝑟, the status of the goal model elements is updated. Specifically,
𝗍𝟣 ∶ 𝖱𝖾𝗀𝗂𝗌𝗍𝖾𝗋 and 𝗀𝟣 ∶ 𝖱𝖾𝗊𝗎𝗂𝗋𝖾𝖽 𝖽𝗈𝖼𝗎𝗆𝖾𝗇𝗍𝖺𝗍𝗂𝗈𝗇 𝗋𝖾𝗀𝗂𝗌𝗍𝖾𝗋𝖾𝖽 become achieved
(⊤, ⊥). Moreover, after firing transition 𝑡5 with a marking 𝑚 = [𝑝4
], both
𝑡6 and 𝑡7 are enabled. This is because 𝑡6 and 𝑡7 belong to an exclusiveor branching in 𝑁. If the execution continues with 𝑡6
from 𝑚 = [𝑝4
],
the resulting state satisfies the quality 𝗊𝟣∶ 𝖨𝗇𝖼𝗋𝖾𝖺𝗌𝖾 𝖢𝗂𝗍𝗒 𝖡𝗎𝗌𝗂𝗇𝖾𝗌𝗌 𝖦𝗋𝗈𝗐𝗍𝗁
(⊤). Therefore, that states the set of final states 𝐹𝐶. Finally, since 𝑡8
is
enabled after either 𝑡6 or 𝑡7
, the last column in Table 4 contains (*) to
indicate that the value of an intentional element depends on the branch
taken. For example, the path [𝑝5
]
𝑡6
←←←←←←←←→ [𝑝6
] results in 𝗊𝟣 being satisfied,
whereas [𝑝5
]
𝑡7
←←←←←←←←→ [𝑝6
] results in 𝗊𝟣 being denied (⊥).
Information and Software Technology 196 (2026) 108146
11
J. Caballero-Villalobos et al.
Table 4
Excerpt of reachable markings of product labeled transition system 𝑁,𝑚0
× 𝐺𝑀 .
𝖤𝗇𝖺𝖻𝗅𝖾𝖽? 𝑡1
𝑡2
𝑡3
𝑡4
, 𝑡5
𝑡2
𝑡6
, 𝑡7
𝑡8
𝑡8 ∅
𝗆𝖺𝗉( t ) 𝜖 𝗍𝟣
𝗍𝟤
𝜖 𝜖 𝗍𝟥
𝗍𝟥
𝜖
Reachable markings of N - firing transition 𝑡 lead to marking [𝑝𝑛
]
Fired ( t )
𝑚
[𝑝0
]
∅
[𝑝1
]
𝑡1
[𝑝2
]
𝑡2
[𝑝3
]
𝑡3
[𝑝1
]
𝑡4
[𝑝4
]
𝑡5
[𝑝5
]
𝑡6
[𝑝5
]
𝑡7
[𝑝6
]
𝑡8
Intentional elements
𝗍𝟣
(?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗍𝟤
(?, ?) (?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗍𝟥
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) (?, ?) *
𝗍𝟦
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) *
𝗀𝟣
(?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗀𝟤
(?, ?) (?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗀𝟥
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) (?, ?) *
𝗀𝟦
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) *
𝗊𝟣
? ? ? ? ? ? ⊤ ⊥ *
Table 5
Excerpt of reachable markings of product labeled transition system 𝐺 × 𝐺𝑀 .
𝖬𝟢 𝖬𝟣 𝖬𝟤 𝖬𝟥 𝖬𝟤 𝖬𝟦 𝖬𝟧
𝖤𝗑𝖾𝖼𝗎𝗍𝖾𝖽 (𝗍,_,_) ∅ 𝖾𝟣
𝖾𝟣
, 𝖾𝟤
𝖾𝟣
, 𝖾𝟤
𝖾𝟣
, 𝖾𝟤
𝖾𝟣
, 𝖾𝟤
, 𝖾𝟥
𝖾𝟣
, 𝖾𝟤
, 𝖾𝟦
𝖯𝖾𝗇𝖽𝗂𝗇𝗀 (_,_, 𝗍) ∅ 𝖾𝟤 ∅ 𝖾𝟤 ∅ ∅ ∅
𝖨𝗇𝖼𝗅𝗎𝖽𝖾𝖽 (_,_, 𝗍) 𝖾𝟣
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣
𝖾𝟣
𝖾𝗇𝖺𝖻𝗅𝖾𝖽𝐺 𝖾𝟣
𝖾𝟤
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟤
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣
, 𝖾𝟥
, 𝖾𝟦
𝖾𝟣
𝖾𝟣
Executed ∅ 𝖾𝟣
𝖾𝟤
𝖾𝟣
𝖾𝟤
𝖾𝟥
𝖾𝟦
𝗆𝖺𝗉( 𝖾 ) 𝜖 𝗍𝟣
𝗍𝟤
𝗍𝟣
𝗍𝟤
𝗍𝟥
𝗍𝟦
Intentional elements
𝗍𝟣
(?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗍𝟤
(?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗍𝟥
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) (?, ?)
𝗍𝟦
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥)
𝗀𝟣
(?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗀𝟤
(?, ?) (?, ?) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥) (⊤, ⊥)
𝗀𝟥
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥) (?, ?)
𝗀𝟦
(?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (?, ?) (⊤, ⊥)
𝗊𝟣
? ? ? ? ? ⊤ ⊥
4.2. Goal-oriented design time compliance using DCR process
In this section, we look at the instantiation of the product labeled
transition system 𝐶, where the process model LTS is given by the LTS
of a DCR graph (𝙿𝙼 = 𝐺 = ⟨([𝖬] 𝑇 ), , ←←→, [𝖬] 𝑇 ,, 𝑙⟩), and provide an
excerpt of the synchronous moves instantiated in the running example.
In this case, the mapping function is 𝗆𝖺𝗉 ∶  → 𝖦𝗈𝖳(𝐺𝑀) ∪ {𝜖}.
As discussed in Section 3.1, we assume that for DCR graphs an event
𝑒 ∈  is never not mapped to an intentional element in the goal model,
i.e., it is never considered ‘‘silent’’ and thus never mapped to 𝜖.
As explained previously, all product runs adhere to the underlying
semantics of the process model. In contrast, the goal model only reflects
the execution of the elements triggered by those process executions.
Table 5 depicts some exemplary execution fragments of the labeled
transition system product for the goal model shown in Fig. 5 (𝐺𝑀)
and the DCR process ([𝖬] 𝑇 ) in Fig. 4, and the mapping in Table 3.
For conciseness, we refer to the event number and not directly to the
full name of each DCR event, as well as to the abbreviation of the
goal model elements (i.e., 𝑡1
). Consider the process model execution
⟨𝖾𝟣, 𝖾𝟤, 𝖾𝟦⟩ leads the product labeled transition system in a state in
which the quality is denied, showing that the process model [𝖬] 𝑇 is
non-compliant with the goal model 𝐺𝑀
4.3. Compliance criteria
Having defined the product labeled transition system 𝐶 based an
arbitrary process model 𝙿𝙼 whose semantics can be represented as a
labeled transitions system (𝙿𝙼), including Petri nets and DCR graphs,
this section introduces the corresponding compliance criteria. In what
follows, 𝐶 refers to the product labeled transition system for a given
goal model and process model. As customary, in this section we write
←←→*
𝐶
for the transitive closure of ←←→𝐶. Below, we introduce the categories
of compliance outcomes supported by our approach.
We define weak compliance as an existential completion property
over the product transition system: from every reachable state, at least
one continuation leads to a success state; if the system gets stuck (no
outgoing transitions), then that terminal state must already satisfy all
qualities. This follows the option-to-complete style criterion used for
workflow correctness in Petri nets. Intuitively, weak compliance rules
out unavoidable failure: even if an execution temporarily reaches a
state in which not all qualities hold, it must remain possible to reach a
success state again.
Definition 4.2 (Weak Compliance). Let 𝙿𝙼 be a process model whose
semantics is expressed as a labeled transition system 𝙿𝙼, let 𝐺𝑀 be a
goal model, and let 𝐶 = (𝑆𝐶, 𝐴𝐶, 𝑠𝐶
0
, →𝐶, 𝐹𝐶) be the product labeled
transition system between the LTS of the process model and the goal
model. A process model 𝙿𝙼 is weakly compliant with a goal model 𝐺𝑀
iff
∀𝑠𝑖 ∈ 𝑆𝐶 ∶ (𝑠0 →*
𝐶
𝑠𝑖
) ⇒
(
∃𝑠𝑛 ∈ 𝑆𝐶 ∶ 𝑠𝑖 →*
𝐶
𝑠𝑛 ∧ 𝑠
𝑛
∈ 𝐹𝐶
)
∨
Information and Software Technology 196 (2026) 108146
12
J. Caballero-Villalobos et al.
(
¬∃𝑎 ∈ 𝐴𝐶, ∃𝑠
′ ∈ 𝑆𝐶 ∶ 𝑠𝑖
𝑎
←←←←←→𝐶 𝑠
′ ∧ 𝑠
𝑖
∈ 𝐹𝐶
)
.
Necessary criteria for weak compliance.
1. ∀𝑞 ∈ 𝑄 ∃𝑒 ∈ 𝐼𝐸 ∶ 𝐶(𝑒, 𝑞) = 𝖬𝖺𝗄𝖾.
2. ∀𝑞 ∈ 𝑄 ∶ 𝐼𝐸𝑞 = { 𝑒 ∈ 𝐺𝑜𝑇 ∣ ∃𝑝 ∈ 𝐺𝑜𝑇 ∶ 𝐶ℎ(𝑒) = ∅ ∧ 𝑝 ↪*
𝑒 ∧ 𝐶(𝑝, 𝑞) = 𝖬𝖺𝗄𝖾 } and ∀𝑞 ∈ 𝑄 ∀𝑒 ∈ 𝐼𝐸𝑞 ∃𝑎 ∈ 𝐴𝑐𝑡 ∶ 𝗆𝖺𝗉(𝑎) = 𝑒.
To have weak compliance, we require: (1) Every quality must have
at least one goal element that can make it true, so the quality is actually
achievable in the goal model. (2) Each quality has a subset of elements
that contribute to making a quality true 𝐼𝐸𝑞
. Each of the elements
𝑒 ∈ 𝐼𝐸𝑞 must exist a process action such as the mapping exists, that
is, the executable behavior of the process model would represent the
achievement of at least each quality.
Let us consider the running example from Section 1 with the goal
model depicted in Fig. 5. First, we look at weak compliance with
respect to the WF-net depicted in Fig. 2. In this case, the WF-net is not
weakly compliant with respect to the goal model as the WF-net makes
a decision and either executes t6: - Approve, leading to the satisfaction
of the goal q1: Increase city business growth via the make link or t8: -
Deny, which leads to the dissatisfaction of goal q1 via the break link,
and then terminates. Thus there exists an execution of the WF-net, such
that quality q1 is not satisfied and there do not exist any transitions out
of the final state which could eventually lead to a satisfaction of q1.
Compare this to the DCR-graph in Fig. 4. Here, after executing event
e4: Deny, which breaks quality q1, e1: Register is still enabled and allows
to execute e1: Register, e2: Assess, and then e3: Approve. This means, that
it is always possible to get to a state where q1 is satisfied from a state,
where q1 was not satisfied, making the DCR-graph weakly compliant
with respect to the goal model.
To strengthen this notion, we now introduce the concept of a stable
system.
Definition 4.3 (Stable System). Let 𝐶 be a product labeled transition
system created from the labeled transition systems of a process model
𝙿𝙼 and a goal model 𝐺𝑀, let
𝑄𝐶(𝑞) = {⟨m̂ , 𝑠⟩ ∈ 𝑆𝐶 ∣ (𝑞, ⊤) ∈ m}̂
be the set of states of 𝑆𝐶, in which quality 𝑞 is satisfy. 𝐶 is stable iff:
∀𝑛 ∈ N ∀(𝑠𝑖
)
𝑛
𝑖=0
⊆ 𝑆𝐶 ∶
(
𝑠0
𝑎1
←←←←←←←←→𝐶 𝑠1
𝑎2
←←←←←←←←→𝐶 ⋯
𝑎𝑛
←←←←←←←←←→𝐶 𝑠𝑛
)
⇒ ∀𝑞 ∈ 𝑄 ∀ 0 ≤ 𝑖 < 𝑗 ≤ 𝑛 ∶
(
𝑠𝑖 ∈ 𝑄𝐶(𝑞)
)
⇒
(
𝑠𝑗 ∈ 𝑄𝐶(𝑞)
)
.
A stable system exhibits quality satisfaction monotonicity. Once
a quality is achieved at any point in a run, it remains true in all
subsequent states until the process terminates.
Necessary criteria for system stability.
1. Necessary conditions for weak compliance (See Section 4.3).
2. ∀𝑞 ∈ 𝑄 ∶ 𝐼𝐸*
𝑞
= { 𝑒 ∈ 𝐺𝑜𝑇 ∣ ∃𝑝 ∈ 𝐺𝑜𝑇 ∶ 𝐶ℎ(𝑒) = ∅ ∧ 𝑝 ↪*
𝑒 ∧ 𝐶(𝑝, 𝑞) = 𝖡𝗋𝖾𝖺𝗄 } and ∀𝑞 ∈ 𝑄 ∃𝑒 ∈ 𝐼𝐸*
𝑞
∄𝑎 ∈ 𝐴𝑐𝑡 ∶ 𝗆𝖺𝗉(𝑎) = 𝑒.
For system stability, all prerequisites for weak compliance must
hold. Moreover, for each quality 𝑞 ∈ 𝑄, let 𝐼𝐸*
𝑞
denote the set of
goal elements that contribute to breaking 𝑞. Stability requires that there
exists at least one 𝑒 ∈ 𝐼𝐸*
𝑞
that is not realizable by the process, that is,
there is no process action 𝑎 such that 𝗆𝖺𝗉(𝑎) = 𝑒.
Looking at the running example from Section 1 and the WF-net from
Fig. 2, we can see that the WF-net is stable with respect to the goal
model. What the WF-net has to ensure is, that if once quality q1 is
satisfied, it stays satisfied. Which is true, as after executing t6 - Approve
and t8, the process terminates. However, if the quality q1 was never
satisfied and its state is ?, then implication that if q1 was satisfied, q1
stays satisfied is trivially true.
In contrast, when we consider the DCR-graph from Fig. 4, then
we can always, after having executed e3: Approve, which makes q1
satisfied, execute e1: Register, e2: Assess and e4: Deny, which leads to
a dissatisfaction of q1. Thus, the property that once q1 is satisfied q1
stays satisfied is violated, and the DCR-graph is not stable with respect
to the goal model.
Definition 4.4 (Strong Compliance). A process model 𝙿𝙼 is strongly
compliant with a goal model 𝐺𝑀 iff: 𝙿𝙼 is weakly compliant with 𝐺𝑀
and 𝐶 is stable.
As we can see from the previous discussions for the running example, neither the WF-net is strongly compliant with the goal model, nor
the DCR-graph is strongly compliant with the goal model. For the WFnet, it is not weakly compliant, and for the DCR-graph, it is not stable
with respect to the goal model.
4.3.1. Design recommendations for meaningful results
Although our approach does not impose restrictions on the wellformedness of the model and returns a result for any uploaded triple
(goal model, process model, and mapping), the usefulness and interpretability of the compliance criteria for domain experts can be
improved if:
1. 𝙿𝙼 is a well-formed process model.
(a) If 𝙿𝙼 = 𝑁 the WF-net is sound and safe. (Definition 2.3)
(b) If 𝙿𝙼 = [𝖬] 𝑇 , the DCR process exhibits non-empty
behavior, no dead activities, and it is deadlock-free. (See
Definition 2.7)
2. 𝐺𝑀 is a well-formed goal model (Definition 3.4).
5. Goal-oriented compliance checking algorithms
This section provides the computational procedures for goaloriented compliance analysis on the product transition system 𝐶.
We present four algorithms: weak compliance checking (Algorithm
1), forward reachability via breadth-first search (Algorithm 2), backward reachability via breadth-first search (Algorithm 3), and stability
checking (Algorithm 4).
Algorithm 1: Weak Compliance Checking Algorithm
Require: product labeled Transition System 𝐶 based on a process
model 𝙿𝙼 and a goal model 𝐺𝑀, a mapping map, and a Set of
qualities 𝑄.
Ensure: True if 𝙿𝙼 is weakly compliant with respect to 𝐺𝑀, False
otherwise.
1: 𝑆𝑄 ← {𝑠 ∈ 𝐹𝐶}
2: 𝑆terminal ← {𝑠 ∈ 𝑆𝐶 ∣ ∀𝑠
′ ∈ 𝑆𝐶, (𝑠, 𝑎, 𝑠′
) ∉ →𝐶 for any 𝑎 ∈ 𝐴𝑐𝑡}
3: 𝑆reach_Q ← Backward_BFS(𝑆𝐶, →𝐶, 𝑆𝑄)
4: 𝑆disj ← 𝑆reach_Q ∪ (𝑆terminal ∩ 𝑆𝑄)
5: 𝑆reachable ← Forward_BFS(𝑆𝐶, →𝐶, 𝑠𝐶
0
)
6: if 𝑆reachable ⊆ 𝑆disj then
7: return True
8: else
9: return False
10: end if
Algorithm 1 expresses weak compliance as a reachability property
in 𝐶. It first identifies the set 𝑆𝑄 of states satisfying all qualities. It
then computes the set 𝑆reach_Q of states from which 𝑆𝑄 is reachable
using a backward search (Algorithm 3), and the set 𝑆reachable of states
reachable from the initial state using a forward search (Algorithm 2).
Weak compliance holds iff every state in 𝑆reachable can reach 𝑆𝑄, or is
a terminal state that already satisfies all qualities.
Algorithm 4 checks stability per quality 𝑞 ∈ 𝑄. A system is stable for
𝑞 if whenever 𝑞 holds at some reachable state, 𝑞 cannot become false
Information and Software Technology 196 (2026) 108146
13
J. Caballero-Villalobos et al.
Algorithm 2: Forward Breadth-First Search (Forward_BFS)
Require: Set of states 𝑆, transition relation →, initial state 𝑠0
.
Ensure: The set of all states reachable from 𝑠0
.
1: queue ← empty queue
2: visited ← empty set
3: add 𝑠0
to queue
4: add 𝑠0
to visited
5: while queue is not empty do
6: 𝑠 ← dequeue from queue
7: for each 𝑠
′
such that (𝑠, 𝑎, 𝑠′
) ∈→ for some 𝑎 do
8: if 𝑠
′ ∉ visited then
9: add 𝑠
′
to queue
10: add 𝑠
′
to visited
11: end if
12: end for
13: end while
14: return visited
Algorithm 3: Backward Breadth-First Search (Backward_BFS)
Require: Set of states 𝑆, transition relation →, target set of states
𝑆target.
Ensure: The set of all states from which a state in 𝑆target is
reachable.
1: queue ← empty queue
2: visited ← empty set
3: for each 𝑠target ∈ 𝑆target do
4: add 𝑠target to queue
5: add 𝑠target to visited
6: end for
7: while queue is not empty do
8: 𝑠 ← dequeue from queue
9: for each 𝑠
′
such that (𝑠
′
, 𝑎, 𝑠) ∈→ for some 𝑎 do
10: if 𝑠
′ ∉ visited then
11: add 𝑠
′
to queue
12: add 𝑠
′
to visited
13: end if
14: end for
15: end while
16: return visited
later along any continuation. Operationally, the algorithm computes
states from which ¬𝑞 is reachable (via Algorithm 3); states outside
this set cannot lead to a violation of 𝑞 once 𝑞 holds. Stability holds
iff all forward reachable states satisfy the resulting stability condition
for every quality.
5.1. Correctness, termination, and complexity
Below, we state termination and correctness guarantees for the main
procedures (Algorithms 1 and 4). Detailed proofs are deferred to the
appendix.
Theorem 5.1 (Time and Complexity of Algorithm 1). Let 𝐶 be a labeled
product system. Let 𝑚 = | →𝐶 | be the number of transitions, and 𝛥 =
max𝑠∈𝑆𝐶
|{(𝑎, 𝑠′
) ∣ 𝑠
𝑎
←←←←←→𝐶 𝑠
′}| as the maximum out-degree. Then Algorithm
1 runs in:
𝑇𝑐𝑐 (𝑆𝐶, →𝐶) = 𝛩(|𝑆𝐶| + 𝑚), 𝑆𝑝𝑎𝑐𝑒𝑐𝑐 (𝑆𝐶) = 𝑂(|𝑆𝐶|).
If 𝑚 ≤ 𝛥|𝑆𝐶| and 𝛥 = 𝑂(1) ⟹ 𝑇𝑐𝑐 (𝑆𝐶, →𝐶) = 𝑂(|𝑆𝐶|).
Proof. See A. □
Algorithm 4: Stability Checking Algorithm
Require: A product labeled Transition System 𝐶 based on a
process model 𝙿𝙼 and a goal model 𝐺𝑀, Set of qualities 𝑄.
Ensure: True if for all qualities 𝑞 ∈ 𝑄, once a quality becomes
satisfied, it is satisfied for the remaining runs, False otherwise.
1: 𝑆reachable ← Forward_BFS(𝑆𝐶, →, 𝑠0
)
2: for each 𝑞 ∈ 𝑄 do
3: 𝑆¬𝑞 ← {𝑠 ∈ 𝑆𝐶 ∣ (𝑞, ⊤) ∉ m}̂
4: 𝑆𝑟𝑒𝑎𝑐ℎ(¬𝑞) ← Backward_BFS(𝑆𝐶, →𝐶, 𝑆¬𝑞
)
5: 𝑆𝑎𝑙𝑙(𝑞) ← 𝑆𝐶 ⧵ 𝑆𝑟𝑒𝑎𝑐ℎ(¬𝑞)
6: 𝑆stable(q) ← 𝑆¬𝑞 ∪ 𝑆𝑎𝑙𝑙(𝑞)
7: if 𝑆reachable ⊈ 𝑆stable(q) then
8: return False
9: end if
10: end for
11: return True
Algorithm 1 runs breadth-first searches on 𝐶. A breadth-first search
visits each state once and scans each transition once, so the running
time is proportional to the number of states plus the number of transitions, so the running time is proportional to 𝛥|𝑆𝐶|. If 𝛥 is bounded by a
constant, the running time is proportional to |𝑆𝐶|. Since each combined
state pairs one process state with one goal state, the worst case is when
all pairs can occur.
Theorem 5.2 (Termination). The Compliance Checking Algorithm (Alg.
1) terminates.
Proof. See B. □
Theorem 5.3 (Correctness and Completeness of the Compliance Checking
and Stability Algorithms). Given a set of target qualities 𝑄 and a product
labeled transition system 𝐶 induced by: a process model (𝙿𝙼), a goal model
(𝐺𝑀), and a mapping (𝗆𝖺𝗉).
1. The compliance-checking algorithm (Algorithm 1) is correct and
complete with respect to the definition of a weak compliance (Definition 4.2).
2. The stability checking algorithm (Algorithm 4) is correct and complete with respect to the definition of a stable system (Definition
4.3).
Proof. See C for Theorem 5.3 part 1. □
Proof. See D for Theorem 5.3 part 2. □
Theorem 5.4 (Time and Space Complexity of Algorithm 4). Let 𝐶 =
(𝑆𝐶, →𝐶, 𝑠0
) be a product labeled transition system and 𝑄 be the set of
qualities. Let 𝑚 = | →𝐶 | denote the number of transitions and 𝛥 =
max𝑠∈𝑆𝐶
|{(𝑎, 𝑠′
) ∣ 𝑠
𝑎
←←←←←→𝐶 𝑠
′}| be the maximum out-degree. Then Algorithm
4 runs in:
𝑇𝑠𝑡𝑏(𝑄, 𝑆𝐶, →𝐶) = 𝑂(|𝑄| ⋅ (|𝑆𝐶| + 𝑚))
Space𝑠𝑡𝑏(𝑆𝐶, →𝐶) = 𝑂(|𝑆𝐶| + 𝑚)
Furthermore, if 𝑚 ≤ 𝛥|𝑆𝐶| and 𝛥 = 𝑂(1), then 𝑇𝑠𝑡𝑏 = 𝑂(|𝑄| ⋅ |𝑆𝐶|).
Proof. See E. □
6. Tool support
Kogi [24] is a Python tool that supports compliance evaluation
between process models and organizational goal models. Users upload
a process model, a goal-oriented requirements model, and a mapping
that relates process transitions to goal elements for analyzing the compliance of the process model with the goal model at design-time. In our
Information and Software Technology 196 (2026) 108146
14
J. Caballero-Villalobos et al.
previous work [23], Kogi supported compliance analysis only for Petri
nets and goal models. In that setting, compliance was formulated as a
reachability problem by encoding both the process model and the goal
model as labeled transition systems and analyzing their synchronous
product. Moreover, the tool reported a binary outcome (True/False):
it returned True if, from every reachable product state, a state in which
all qualities are satisfied was reachable; otherwise, it returned False.
In this work, Kogi3 was enhanced as follows:
1. We aligned the outcome categories with the compliance criteria introduced in Section 4.3, distinguishing strong compliance,
weak compliance, and quality satisfaction stability, which enables
comparisons across compliant executions.
2. We increased the expressiveness of the interactive notebook for
goal models and Petri nets by generating counterexamples when
non-compliance is detected, identifying the specific transition(s)
that lead to non-compliance. The notebook now supports three
modes:
(a) Batch mode, where the user uploads the goal model, the
process model, and the mapping, and receives an outcome (weak compliance and stability) (note that strong
compliance is weak compliance plus stability).
(b) Interactive mode based on a goal model, process model,
and mapping, where the user fires transitions step by step
and observes how goal satisfaction changes.
(c) What-if interactive scenario that allows the user to explore the reaction of the goal model to events (no process
model and mapping are needed)
3. We extended Kogi to support goal-oriented compliance analysis
for the declarative model language Dynamic Condition Response
(DCR) graphs combined with goal models, allowing users to
upload the required models and obtain an outcome; however,
stepwise execution of DCR events with interactive goal updates
is not yet supported.
4. We report the number of states and transitions that the LTS for
goal model, process model, and product system has to allow one
to make a scalability assessment
5. We updated the documentation and provided additional built-in
examples in the repository to facilitate replicability.
Fig. 8 illustrates the interactive checking for compliance and stability for the product LTS of the goal model depicted in Fig. 5 and
the WF-net in Fig. 2. In the goal model, an AND-decomposition is
represented by purple links, an OR-decomposition by yellow arrows,
make-links by green arrows, and break-links by red arrows. The interactive mode supports two modes. In the first mode, the user himself can
fire transitions from the Petri net by selecting the relevant transition
(event) and then executing that transition. After each transition, where
the Petri net has fired, the executed traces section is updated, and the
visualization of the goal model reflects the satisfaction status of the
corresponding elements. In the second mode, the system can be used
to automatically check for stability and weak compliance by pressing
the corresponding button. In case, the product system of process model
and goal model is stable or weak compliant, True is being shown. If not,
False is shown, and the drop-down list contains a list of places/states
of the product LTS, where the corresponding properties have failed.
By selecting one of those places, the state of the Petri net and the
goal model for that place/state are shown. In Fig. 8 weak compliance
(Definition 4.2) has failed. It is now possible to choose one of the failed
states, like 𝑝7
, and one can observe elements satisfied by the path that
are shown in green, denied elements in red, pending elements that
require re-execution in blue, and elements with unknown status that
3 https://github.com/jc4v1/Kogi-Python.
have not yet been triggered in white. In this case, one can see that
we have reached the end state of the Petri net, and quality 𝑞1 is not
satisfied, which means that the Petri net, together with the goal model,
is not weakly compliant.
Fig. 9 shows the batch use of Kogi with the DCR graph from Fig. 4
and the goal model from Fig. 5. Fig. 9 shows that the process is weakly
compliant with the goal model, as it is always able to reach a state in
which all qualities are satisfied. However, the system is not stable, as
even after the qualities are achieved, that status is not maintained over
time. An example of a trace where 𝑞1 is satisfied and then 𝑞1 is not
satisfied is: Register, Assess, Approve, Register, Deny.
Note that stability and compliance always depend on the combination of process model and goal model. In the running example, we see
that the Petri net process model together with the goal model is stable
but not weak compliant. In contrast, the DCR graph together with the
goal model is weak compliant, but not stable. The difference between
the Petri net and the DCR graph model is that the Petri net finishes after
a decision is made, but the DCR graph allows registration again after
a decision was made and then redo the decision. This means that the
DCR model is weak compliant, as it is always possible to perform an
Approve event and thus satisfy the quality q1. However, this also means
that the DCR model is not stable, as after an Approve is executed, the
DCR graph can always execute a Register and then a Deny. In contrast,
on the one hand, the Petri net is not weak compliant, because once a
decision is make, i.e., Deny, the Petri net terminates. And in case of a
Deny, the quality 𝑞1 is not satisfied. On the other hand, the Petri net is
stable, because stability means that if a quality becomes satisfied, it has
to stay satisfied. However, this also means that if the quality is never
satisfied, the process is stable.
Another option Kogi offers is an animation of the goal model independent of a concrete process model. This allows one to execute What-If
scenarios (i.e., Fig. 10), that is, to experiment with how executing
intentional elements changes the state of the goal model. Here, the
events can be executed in any order.
The idea of the What-If scenario is for the designer of the goal model
and process model to check what execution of intentional elements
will lead to a weakly-compliant or a stable system. This is helpful in
designing the process model to make sure, that one always can reach
a state where all goals are satisfied (i.e., weak compliance), and which
traces need to be excluded to make sure a process model together with
a goal model is stable.
For example, in Fig. 10, one can see that after executing t1: Register
application, t2: Assess application, t3: Approve, t1: Register application,
and t4: Deny, the quality q1: Increase city business growth is not
satisfied. However, if our process model always allows to execute t3:
Approve again, then the system would be weakly-compliant. Indeed,
this is how the DCR version of the process model is designed (cf. Fig.
4).
Similarly, we can see that, once we have executed t3: Approve, it
should not be possible to execute t4: Deny, if we want a stable system.
This means, that to make the Petri net process model stable, once t3:
Approve is executed, the process should finish (cf. Fig. 2).
Another advantage of the What-if model is to allow for checking of
counter examples in the non-interactive version of Kogi, where we do
not have an interactive animation of the goal model with the process
model available. Here, if weak-compliance or stability are violated, we
get a set of traces showing the violation. To better understand those
traces, they can be replayed using the What-if part of the Kogi tool.
For example, in Fig. 11, we see the result of the execution of the
first counter example for stability from Fig. 9. First, the corresponding
intentional elements for Register, Assess, Approve are executed, which
shows that the quality q1 is satisfied. However, if the execution continues with Register, and Deny, then q1 is not satisfied anymore, meaning
that the DCR graph is not stable wrt. the goal model.
The What-if animation is achieved by combining the goal model
with a labeled transition system for the process model, which consists
of only one state 𝑠 and a transition from 𝑠 to 𝑠 for each intentional
element that is a leaf, i.e., an intentional element that does not have
any children.
Information and Software Technology 196 (2026) 108146
15
J. Caballero-Villalobos et al.
Fig. 8. Kogi’s notebook - 𝐺𝑀 and WF-nets.
Fig. 9. Kogi’s notebook — non-interactive analysis of a DCR graph and a goal model from the running example.
7. Proof of concept - validation
We illustrate the application of the framework, grounding it in
the EU air passenger rights framework, as specified in Regulation
261/2004, and the Court of Justice of the European Union (CJEU)
judgments. In the EU, common rules on compensation and assistance
to passengers in cases of denied boarding, cancellation, or long flight
delays are laid down in Regulation 261/2004, which is currently under
Information and Software Technology 196 (2026) 108146
16
J. Caballero-Villalobos et al.
Fig. 10. Kogi’s notebook - interactive What-if analysis.
review. The revision has been prompted by divergences in interpretation and enforcement across Member States. As the Commission’s
Communication notes, these divergences have, in turn, generated numerous references for a preliminary ruling to the CJEU.4 The resulting
body of case law underscores the persistent indeterminacy in key
notions of the regulatory framework. In what follows, we focus on one
aspect that makes this indeterminacy and the challenge of knowledge
scoping concrete, presenting an example of an interdisciplinary reading
of a flight delay scenario culminating in a compensation claim.
Scenario. An airline validates a passenger compensation claim by computing the arrival delay from scheduled versus actual arrival time
and then deciding approval or exemption by assessing, classifying,
and documenting whether extraordinary circumstances apply under
Regulation (EC) No. 261/2004 (Recital 14).
Business goals. The company (an airline) has established the following
business goals:
1. The company shall record the arrival time through verifiable
evidence.
2. The company should keep an up-to-date list of extraordinary
circumstances.
3. The company shall evaluate every compensation claim and issue
a decision.
Within the Regulation’s text, ‘delay’ is framed at the point of departure. Thus, Art. 6 sets the departure delay thresholds defined both
in terms of hours and distance. Once the relevant threshold is met, the
air carrier shall provide care as per Art. 9, and, if the delay reaches
at least five hours, it shall also offer the reimbursement option under
Art. 8(1)(a). Essentially, the flight is delayed when it is operated as
planned but departs later than scheduled. Art. 6 does not itself explicitly
grant any right to compensation. However, as the Court pointed out,
the qualification of a ‘flight delay’ is inseparable from the passenger’s
‘loss of time’, measured at the final destination. Thus, in Sturgeon
case,5 national courts asked whether a very long delay, including a
case where such a delay was over 22 h, should still be treated as
4 Commission Notice — Interpretative Guidelines on Regulation (EC) No
261/2004 of the European Parliament and of the Council establishing common
rules on compensation and assistance to passengers in the event of denied
boarding and of cancellation or long delay of flights and on Council Regulation
(EC) No 2027/97 on air carrier liability in the event of accidents as amended
by Regulation (EC) No 889/2002 of the European Parliament and of the
Council C/2016/3502, OJ C 214.
5 Joined Cases C-402/07 and C-432/07 Christopher Sturgeon and Others
v Condor Flugdienst GmbH and Stefan Böck and Cornelia Lepuschitz V Air
France SA.
a ‘delay’ under Art 6 or as a cancellation, triggering compensation
under Art 7. Having interpreted the Regulation in light of its purpose
and the principle of equal treatment,6
the Court held that long delays
at the final destination place passengers in a situation comparable to
cancellation that involves Art. 7 compensation regime, subject to the
Art 5(3) defence.7
It follows that passengers reaching their destination
three hours or more after the scheduled arrival time are entitled to
compensation, unless the carrier can prove that such a delay is caused
by extraordinary circumstances within the meaning of Art 5(3). The
court’s interpretation thus effectively extends the scope of the delay
provision to recognize a compensation entitlement beyond the cases
explicitly mentioned in articles 4 and 5 concerning denied boarding and
flight cancellation. This long-delay scenario highlights both the value
of interdisciplinary collaboration in mapping the appropriate knowledge base and the relevant challenges in identifying and representing
different types of indeterminacies embedded in legal texts.
From the operating carrier’s perspective, compensation liability may
significantly affect revenue. Determining when compensation is owed,
therefore, becomes a critical task. To that end, the carrier must
1. Standardize the interpretation of terms triggering the compensation regime.
2. Record the decision criteria applied in each case.
3. Retain evidence supporting the subsequent claims handling.
(1) Terms Interpretation As discussed, the compensation under
consideration is contingent upon the interpretation of two key
concepts: ‘arrival time’ and ‘extraordinary circumstances
Arrival Time Even though the former can, in principle, be established from an event log, there is still a choice remaining as to
which event constitutes the decisive legal criterion. The Regulation itself does not define ‘arrival time’. However, as clarified
in the CJEU judgment Germanwings v Henning8
, the concept
ultimately refers to the moment at which at least one aircraft
door is opened, and passengers are allowed to disembark.9
In
reaching this decision, the CJEU, again, framed the choice of
the pertinent criterion around the notion of the ‘lost time’, as
an implication of the fact that passengers are asked to remain
confined in an enclosed space, ‘unable to achieve the objectives
which led them to go at the desired time to the destinations of
their choice’.10
Extraordinary Circumstances, as a notion, is not specifically defined in the Regulation. Art 5(3) refers to them as an exemption
from compensation where delays and cancellations are caused
by such circumstances, ‘which could not have been avoided
even if all reasonable measures had been taken’. Moreover,
Recital 14 offers a non-exhaustive list of examples, such as
political instability, severe weather, security risks, unexpected
flight safety shortcomings, and strikes that affect the operation
of an operating air carrier. Further clarifications were delivered
by the CJEU in the Wallentin-Hermann case.11 As the Court
pointed out, Art 5(3) is a derogation from the rule and therefore
should be interpreted strictly.12 It also emphasized that the list
of circumstances enlisted in Recital 14 is merely indicative:
6 Para 42–48 of the judgment.
7 Para 67, with the reference to Rec 15 as well.
8 Judgment of the Court, 4 September 2014. Germanwings GmbH v Ronny
Henning, Case C-452/13.
9 Para 25.
10 Para 20–21.
11 Case C-452/13, Germanwings GmbH v Henning, the European Court of
Justice (ECJ).
12 Para 20.
Information and Software Technology 196 (2026) 108146
17
J. Caballero-Villalobos et al.
Fig. 11. On the left, the state of the goal model is shown after executing t1: Register application, t2: Assess application, and t3: Approve application, and on the
right continuing with t1: Register application followed by t4: Deny.
the events do not automatically qualify as extraordinary, but
may produce such circumstances.13 The Court further set two
cumulative conditions for circumstances to be recognized as
‘extraordinary’: (i) the event in point shall ‘not be inherent in
the normal exercise of the activity of the air carrier concerned’
and (ii) should be ‘beyond the actual control of that carrier on
account of its nature or origin’14
(2) Reflection on the decision criteria applied in each case and
indeterminacy
Although a thorough discussion on the methods of interpretation
of indeterminacy lies outside the scope of this research, we
briefly note here that the examples above involve distinct forms
of indeterminacy and, in principle, demand different forms of
analytical assessment. This distinction matters significantly for
proposing the extent to which decision-making support can be
semi-automated. Thus, from a legal perspective, both notions
call for judgment. However, in the case of ‘arrival time’, this
judgment revolves around a choice of competing meanings. For
example, in Germanwings v Henning case,15 such alternatives
were framed as the time at which the aircraft touches down
on the runway of the destination airport; the time at which the
aircraft reaches its parking position and the parking brakes are
engaged or the shocks have been applied; the time at which
the aircraft door is opened; and a time defined by the parties
by common accord.16 Essentially, indeterminacy could be eliminated by referencing an agreed-upon meaning. By contrast, the
notion of extraordinary circumstances does not amount to a
choice of a distinct meaning, but rather requires a case-by-case
evaluation, engaging with open-ended standards describing, for
example, foreseeability, mitigation, control, etc. In this context,
indeterminacy is largely mitigated through defining boundaries
captured by the assessment of relevant factors and evidence.
(3) Evidence
In light of the discussion above, evidence supporting the subsequent claims handling could be distinct. For the arrival time,
evidence could be reduced to a reference to a single data point,
such as the arrival moment, defined in accordance with the
CJEU case law and recorded by a signed declaration of the
flight crew or handling agent.17 For assessment of extraordinary
circumstances, the evidentiary record could comprise a bundle
of materials demonstrating that the event was not inherent
in normal operations; was beyond the carrier’s actual control,
13 Para 22.
14 Para 23.
15 Case C-452/13, Germanwings GmbH v Henning, the European Court of
Justice (ECJ).
16 Para 12.
17 3.3.3. of interpretative Guidelines of the Commission.
and that all reasonable measures, technically and economically
viable, were taken18
Having established the legal considerations for interpreting the
terms of interest, we model the operational behavior as a process model
and the business objectives as a goal model. We then map process
transitions to intentional elements in the goal model (Fig. 12).
Process model. The process behavior described in paragraph (7) is modeled as a Workflow net. For readability, we use the task abbreviations
and the transition number as shown in Fig. 12. The process begins by
performing Retrieve departure time limit in the passenger booking (rtapb,
𝑡2
) and Retrieve the time the aircraft doors opened (rtdwo, 𝑡3
). These
activities may be executed in any order, but both are completed before
Compute the actual–scheduled time difference (cda, 𝑡4
). If the computed
time difference exceeds the legal threshold (three hours), the process
continues with the assessment of whether the case is excluded due to
extraordinary circumstances. At this point, the process either retrieves
additional evidence via Retrieve information from internal systems (riis,
𝑡5
) or directly records an exclusion justification via Record the justification for exclusion as an extraordinary circumstance (rjcnoec, 𝑡9
).
The latter path leads to Deny compensation (dc, 𝑡14) and terminates
with Document and save the case file (dsc, 𝑡15). If additional evidence
is retrieved, and no extraordinary circumstances are determined, the
process proceeds to Record the justification for exclusion of extraordinary circumstances (rjcnoec, 𝑡8
), Approve compensation (ac, 𝑡13), and
Document and save the case file (dsc, 𝑡15). Otherwise, if extraordinary
circumstances are confirmed, the process reaches a compensation decision either Approve compensation or Deny compensation and terminates
with Document and save the case file.
Goal model. The business goals introduced in paragraph 7 are modeled
as an iStar goal model. They are represented as goals ( ) and refined
into tasks ( ) that correspond to the activities in the process model
(Fig. 12). In this case study, the task set is derived from the process
description; however, the same goals could be operationalized by a
different process, or the tasks could originate from an existing goal
model.
The goal model uses 𝖠𝖭𝖣- and 𝖮𝖱-decompositions to capture required sub-goals and alternative means of satisfaction. Importantly, the
goal model does not encode execution order. It describes desired states
of affairs in an ideal setting, while ordering and feasibility are enforced
by the process semantics in the product system. As a consequence,
a goal task such as Approve compensation can only be considered in
executions where the corresponding prerequisite process behavior has
occurred.
18 Seem i.e., paras 40–42 of the Wallentin-Hermann to structure evidence
supporting the decision-making progression.
Information and Software Technology 196 (2026) 108146
18
J. Caballero-Villalobos et al.
Fig. 12. Process model, goal model, and mapping—EU air-passenger rights. Top: Well-formed workflow net (13 places, 15 transitions). Well-formed goal model
and mapping.
As discussed in the legal considerations, the open-text terms of interest in this case study are delay on arrival and extraordinary circumstances.
For delay on arrival, the model adopts the operational interpretation
used by the Court, namely that arrival time is the time when at least one
aircraft door is opened. This relies on a concrete, measurable task in the
goal model and supports automated checking against the corresponding
quality ( ).
For extraordinary circumstances, the regulation provides a nonexhaustive list and the classification remains context-dependent. Consequently, the model cannot fully automate the legal qualification.
Instead, it enforces that decisions are supported by recorded criteria
and evidence, enabling subsequent review and audit. For example,
a carrier may initially classify a delay caused by pilot sickness as
an extraordinary circumstance and deny compensation; however, on
appeal, a court may reject this classification. In such cases, the recorded
criteria and evidence provide the basis to assess the legal justification of
the original decision. We capture these requirements as qualities ( )
requiring appropriate measurement and documentation of the terms of
interest.
Mapping. The mapping from process transitions to goal-model elements should be defined and validated jointly by process and legal/-
domain experts, since it determines how observable process behavior
contributes to goal satisfaction. In this case study, we adopt a namebased mapping for simplicity: each labeled transition in the Workflow
net is mapped to the goal-model task with the same abbreviation. For
example, the transition 𝑡13 labeled Approve compensation is mapped to
the task ac in the goal model, written 𝑡13 ↦ 𝚊𝚌. This one-to-one correspondence is expected here because the goal model was constructed
from the same process description.
In general, the framework can be applied with externally developed
goal models; however, our compliance definition relies on the satisfaction of qualities, which limits the reuse of goal models that do not
explicitly represent such qualities. The mapping is defined over the leaf
tasks of the goal model.
Kogi’s evaluation. We uploaded the files19 for the goal model, the
process model, and the mapping into Kogi. Based on the compliance
criteria in Section 4.3, Kogi reports that the process model is strongly
compliant with the goal model. This means that weak compliance holds
and that the product system exhibits quality satisfaction monotonicity.
This result is expected because the goal model was built based on the
process description, with a 1-to-1 mapping based on name correspondence. In practice, this is not always the case. Different specialists may
design the models and the mapping. The proposed approach supports
modelers in specifying the right elements and in experimenting to
assess whether the specification captures the intended requirements.
To illustrate a less straightforward case, consider a variation of
the workflow net in Fig. 12 where the process modeler records the
criteria for extraordinary circumstances only when such circumstances
are identified, that is, transition (𝑡7
, rcjec), and omits the case that
records the criteria for non-extraordinary circumstances, that is, transition (𝑡8
, rcjnoec). If the mapping for the remaining transitions
is kept as in Fig. 12, Kogi determines that the process model is not
weakly compliant with the goal model. Omitting 𝑡8
implies that the
criteria for registering the exemption are never recorded. Consequently,
when no extraordinary circumstances apply, the quality Extraordinary
circumstances documented appropriately (𝑒𝑐𝑑𝑎𝑝) is not satisfied along
at least one possible execution path.
Framework’s evaluation. From a technical standpoint, the framework is
feasible, expressive, and modular. From a legal standpoint, we carried
19 https://github.com/jc4v1/Kogi-Python/tree/main/Data/air-passengerrights.
Information and Software Technology 196 (2026) 108146
19
J. Caballero-Villalobos et al.
out a formative evaluation through a semi-structured interview. The interview involved one legal practitioner (P1) with more than 15 years of
expertise at the intersection of law and technology and a research focus
on digital compliance. P1’s background combines interdisciplinary legal
research on computational compliance and the societal implications
of automated decision-making, including questions of accountability,
transparency, and explainability. P1 has also worked on regulatory
topics relevant to compliance engineering, including data protection
and the governance of algorithmic systems, drawing on perspectives
from law and economics as well as human-centered approaches. The
interview focused on (i) perceived support for day-to-day legal analysis
and compliance checking tasks, and (ii) ease of application, whether
the approach can be used without undue technical overhead. The
interview guide and anonymized transcript are available in the project
repository.20
Fig. 13 shows excerpts from the interview with P1 that support
the points reported in our evaluation. P1 (1) framed the framework
as support for the translation workflow from legal sources to operational artifacts, including source mapping, scoping the governing
knowledge base, and identifying ambiguous terms; (2) linked ambiguity to interpretive authority and accountability in legal practice
and stressed the need to make explicit where expert judgment enters the workflow, including in apparently straightforward cases; (3)
associated interpretability of the outputs with visual representations,
especially goal models, and connected this to locating where noncompliance arises; (4) argued that explainability requires an explicit
non-compliance threshold and an evidentiary basis, since adverse outcomes can trigger duties to justify decisions; (5) highlighted value
for large-scale handling: classification supports routing straightforward
cases while flagging those needing additional attention under high
claim volumes. Finally, as a limitation, P1 (6) stressed that operationalization should not rely only on the legal text and should incorporate
case law, decisions, and/or guidelines, as legal qualification depends
on those sources.
7.1. Limitations and threats to validity
In this section, we present some of the limitations and threats to
the validity of our approach. We organize these according to standard
validity categories and discuss mitigation strategies where applicable.
7.1.1. Construct validity
Manual modeling effort and interpretive subjectivity. The
framework requires manual construction of goal models, process models, and mappings between them. This introduces several risks: (i)
different modelers may interpret the same regulatory text differently,
leading to inconsistent models; (ii) modelers may omit relevant requirements or introduce spurious ones; (iii) the mapping from process
actions to goal elements depends on expert judgment and may not
accurately reflect the intended relationship. We mitigate this by suggesting design guidelines (Section 4.3.1) and by documenting modeling
decisions. However, the framework does not eliminate subjectivity—it
makes interpretive choices explicit and traceable.
Dependency on case law and interpretive guidance. Operationalizing regulatory requirements requires more than the text of a
regulation. As noted by P1 (Fig. 13, excerpt 6), legal qualification
depends on case law, administrative decisions, and interpretive guidelines. Our case study incorporated CJEU judgments to define ‘‘arrival
time’’ and ‘‘extraordinary circumstances’’, but the framework does not
20 https://github.com/jc4v1/Kogi-Python/tree/main/Data/Invterviews.
Fig. 13. Excerpts from the semi-structured interview with P1.
automate the extraction or integration of such sources. Models may be
incomplete or inaccurate if relevant case law is not considered, and
they require manual updates as case law evolves.
Limited scalability for large regulatory texts. Eliciting goal models from lengthy or complex regulations (i.e., GDPR, financial regulations) requires substantial manual effort. The framework does not
provide automated support for extracting goal models from regulatory
texts, and the effort required grows with the size and complexity of the
regulation, limiting practical applicability in domains with extensive
regulatory frameworks.
Information and Software Technology 196 (2026) 108146
20
J. Caballero-Villalobos et al.
Table 6
Reachable states and transitions for the LTS’ from the Goal model, Process model (Petri net and DCR), and the
Combined LTS.
Scenario GM states GM trans PM states PM trans Comb. states Comb. trans
Example Petri net 20 160 7 8 11 11
Example DCR 20 160 16 32 20 40
Example What-if 20 160 1 4 20 80
Air passenger Petri net 512 8704 14 17 22 22
Air passenger What-if 512 8704 1 9 512 4608
7.1.2. Internal validity
Well-formedness checking cost. The framework does not impose
well-formed inputs. However, to have meaningful discussions, we suggest some design criteria (Section 4.3.1). Therefore, we provide more
details to check the goal model’s well-formedness (Definition 3.4).
The cost for checking the well-formedness is 𝑂(|𝑅| + |𝖦𝗈𝖳(𝐺𝑀)|),
which is efficient for typical models. However, checking Workflow
net soundness (Definition 2.3) requires reachability analysis, which is
PSPACE-complete in the worst case [43]. For DCR graphs, checking
deadlock-freedom and liveness also requires state-space exploration. In
practice, these checks are feasible for models with tens of places/events
and hundreds of states, but may become challenging for models with
thousands of states or complex data dependencies.
Product state-space size. Compliance checking operates on the
product LTS 𝐶, whose worst-case size is |𝑆𝐶| = |𝑆𝑃
|×|𝑆𝐺𝑀 |. However,
in practice, the number of reachable states of 𝑆𝐶 is more often between
the sizes of reachable states of the goal model and the reachable states
of the process model. Table 6 shows empirical measurements for our
case studies. For the running example, we get 20 reachable states for
the goal model, 7 reachable states for the Petri net, and 11 reachable
states for the product LTS instead of the theoretical 140 reachable
states. If one uses the DCR graph as the process model, its LTS has 16
reachable states and the product LTS has 20 reachable states instead
of the theoretical 320. In both cases, the product size is closer to
the process model size than the theoretical worst case. The reason is,
that the transitions of the product LTS are governed entirely by the
transitions of the process model as discussed in Section 4. For a goal
model, its transitions are unique for a given goal model state and a
process model transition. However, in What-If scenarios where each
state in the process model can perform any action and all actions are
mapped to different intentional elements, the size of the product LTS
approaches the goal model size (e.g., 512 states for the air passenger
What-If scenario). The Kogi tool reports product sizes, allowing users
to assess scalability case-by-case.
Transformation assumptions for DCR graphs. When using DCR
graphs, the framework constructs the product LTS from the DCR operational semantics (Definition 2.4). This semantics assumes finite executions for compliance checking (accepting runs must eventually satisfy
all pending responses). Infinite executions or liveness properties are
not addressed, which may be relevant for long-running or reactive
processes.
7.1.3. External validity
Single-domain validation. Our empirical validation is based on
one case study (EU Air Passenger Rights, Regulation EC 261/2004) and
one formative interview with a legal practitioner (P1). The case study
demonstrates feasibility, but findings may not generalize to other regulatory domains (i.e., healthcare, finance, data protection) with different structural characteristics. For example, principle-based regulations
(i.e., GDPR’s ‘‘appropriate security measures’’) may require different
modeling strategies than rule-based regulations (i.e., EC 261/2004’s
three-hour delay threshold).
Limited practitioner evaluation. The framework was evaluated
by one legal expert (P1) with more than 15 years of experience in
law and technology. While P1 provided valuable insights (Fig. 13),
the evaluation is formative and exploratory. We have not conducted
controlled user studies or measured adoption effort, learning curve, or
inter-rater reliability for model construction. Future work will include
user studies with multiple practitioners from diverse backgrounds.
Model reuse and evolution not evaluated. The framework supports modular construction of goal and process models, but we have not
systematically evaluated the effort required to adapt models to related
regulations or to incorporate regulatory updates. The framework does
not include version management or change impact analysis, which are
necessary for long-term use in organizations where regulations and
processes evolve.
7.1.4. Expressiveness limitations
No multi-agent modeling. The framework models single-actor
processes and does not represent dependencies between multiple actors
(i.e., airline, passenger, regulator) or cross-organizational compliance
requirements. The goal model (Definition 2.8) focuses on a single actor’s intentional elements and does not include inter-actor dependency
links from the full iStar language [20].
No deontic modalities. The goal model captures satisfaction of
goals, tasks, and qualities, but does not represent obligations, prohibitions, or permissions. This limits reasoning about normative requirements (i.e., ‘‘the airline must provide compensation’’ vs. ‘‘the
airline may deny compensation if extraordinary circumstances apply’’).
Deontic logic extensions could address this limitation.
No data-aware compliance. The framework does not model data
values or data-dependent constraints. For example, it cannot verify
that a computed delay exceeds a specific threshold (i.e., 180 min)
or that a passenger’s booking class entitles them to compensation.
Process actions are abstract labels; their data inputs and outputs are not
represented. This limits applicability to compliance checks that depend
on data values.
No temporal operators. The goal model operational semantics
(Definition 3.5) does not include temporal logic operators (i.e., ‘‘eventually’’, ‘‘always’’, ‘‘until’’). This restricts reasoning about time-dependent
requirements (i.e., ‘‘compensation must be paid within 7 days’’) or liveness properties (i.e., ‘‘every claim must eventually receive a decision’’).
However, as the synchronous move of the product labeled transition
system is only triggered by process actions, temporal requirements are
captured by the process model.
7.1.5. Reliability
Tool correctness. The Kogi tool implements Algorithms 1 to 4.
We have proven correctness, completeness, and termination (Theorems
5.2 and 5.3), have automated tests for the algorithms, and manually
verified tool outputs for the case studies. However, the implementation
has not been independently tested or formally verified. Bugs could
lead to incorrect compliance verdicts. We mitigate this by providing
open-source code, test cases, and expected outputs for replication.
Reproducibility. All models, mappings, and tool configurations
for the case studies are available in the project repository. However,
the manual modeling steps (goal model elicitation, mapping construction) are not fully documented with step-by-step protocols, which may
hinder exact replication by other researchers.
Information and Software Technology 196 (2026) 108146
21
J. Caballero-Villalobos et al.
8. Related work
In this section, we presented the related work with our main contribution divided into four clusters: (i) Design-time business process
compliance, (ii) Compliance requirements from Software engineering,
(iii) Process-goal model interfaces and operational semantics, and (iv)
Regulatory compliance as requirements-process co-design.
8.1. Design-time business process compliance
Design-time compliance applies formal methods to detect requirement violations before deployment. The dominant line of work specifies constraints in temporal and rule-based logics, then applies model
checking or query-based verification to decide satisfaction over a process model [8,44–46]. These approaches support early detection of
control flow and data flow violations, and it supports repeatable audits when the property language has a clear semantics. A recurrent
pitfall is binary compliance. Many frameworks return a single outcome per model or per trace, and they treat all satisfying traces as
equivalent. Event calculus-based separation of compliance management from execution illustrates this pattern, because the evaluation
focuses on satisfaction of constraints rather than on comparative preference among satisfying traces [47]. Visual compliance checking for
clinical workflows also concentrates on violation detection and does
not define a preference relation among satisfying executions [45].
Binary results are insufficient when legal requirements admit multiple compliant behaviors and organizations need selection criteria
grounded in high-level business requirements, stakeholder preferences,
and non-functional requirements.
A second pitfall is traceability without operational meaning for
high-level terms. Annotation based approaches attach textual rules
or control objectives to process activities to connect regulations and
models [48,49]. This improves documentation, but open-textured terms
(HLBRs) remain unresolved because the annotation do not provide
a semantics that supports design time comparison among alternative
compliant designs. The interpretive task moves to later phases, and the
model does not record which operational choices justify compliance
under a given interpretation. Goal-oriented compliance addresses part
of this gap by using goal models to represent regulatory aims and
organizational objectives, and by supporting systematic derivation of
constraints from regulations [5,50]. The typical outcome is a set of
process-level constraints or controls that must hold. The pitfall is that
the goal model often remains upstream of verification, so the design
time check still returns a binary decision over the induced constraints,
rather than an analysis of how alternative process executions satisfy
high level business requirements.
Our approach differs in two aspects. First, it treats operationalization of high-level business requirements as a first-class modeling
step: the goal model records interpretations, and links from process
actions to intentional elements make this explicit. Second, we provide
an operational semantics and a compliance criterion distinguishing
strong, weak, and monotonic satisfaction over executions, enabling
comparison among compliant traces and alternative process designs
based on their contribution to goal satisfaction rather than constraint
satisfaction alone.
8.2. Requirements compliance from software engineering
Within software engineering, compliance has been primarily studied
from a requirements engineering perspective, ensuring that high-level
requirements are correctly interpreted, operationalized, and analyzable
at design time. In contrast to business process compliance, which
typically reasons over executions, this line of work starts from requirements artifacts and supports systematic refinement into models or
specifications that can be assessed for compliance before deployment.
A substantial body of research addresses the extraction and structuring of regulatory requirements. Anton and colleagues introduce
taxonomies for privacy requirements and demonstrate how goal mining can identify system requirements from policy documents [51–53].
Breaux et al. propose semantic parameterization techniques to extract and formalize rights and obligations from regulations such as
HIPAA, producing traceable and analyzable requirements models [54].
Maxwell and Anton show how production rule models support regulatory requirements elicitation and validation by improving communication between legal experts and requirements engineers [55]. These
approaches emphasize correctness of interpretation and traceability,
but they typically do not provide an operational semantics that supports
comparison among alternative compliant designs.
Recent work also explores NLP-based automation for regulatory
compliance, for example, by extracting obligations from legal texts
or checking document completeness against GDPR requirements [56–
59]. Such approaches are complementary to ours: they focus on deriving compliance-relevant requirements from unstructured sources,
whereas our work assumes that high-level requirements have already
been operationalized into an intentional model and addresses the semantic question of how alternative process executions realize these
requirements.
A central challenge in this literature is the operationalization of nonfunctional requirements (qualities) and other high-level constraints.
Frameworks such as non-functional requirements catalogs and quality
models support structured decomposition of abstract qualities into
more concrete criteria [6]. In this work, such qualities include
compliance-relevant concerns such as privacy (e.g., data minimization)
or security (e.g., separation of duties), which are often expressed in
regulations using open-textured language and admit multiple operational interpretations [6]. However, many qualities, such as usability
or user experience, resist precise validation at design time. By contrast,
security and privacy requirements are more frequently operationalized
and verified, partly due to established threat models, controls, and
analysis techniques [60,61]. This contrast highlights that difficulties
in compliance analysis often stem from missing explicit operational
interpretations of high-level requirements, rather than from the absence
of formal verification techniques.
Among established requirements modeling approaches, goal models
provide a structured way to represent high-level requirements, including qualities and stakeholder concerns. In this work, we treat the goal
model as an input artifact capturing the operationalized interpretation
of such requirements. We do not address elicitation or refinement;
instead, we provide a semantics-based connection to an executable process for design-time compliance assessment. Goal-oriented compliance
reasoning is discussed in Section 8.5.
8.3. Process–goal model interfaces and operational semantics
A distinct line of research focuses on defining explicit interfaces
between process models and goal models, with the aim of relating executable behavior to intentional abstractions. These approaches differ in
how mappings are defined, how execution order is handled, and how
goal satisfaction is evaluated.
A first family of approaches establishes structural correspondences
or transformations between process modeling languages and goal modeling languages. For example, Tropos-based approaches relate business
processes to iStar models to support alignment between organizational
objectives and operational workflows [36]. Similarly, several works
propose mappings between BPMN and goal-oriented notations such
as GRL to support traceability, design-time consistency checking and
compliant process redesign [62–64]. These approaches focus on model
alignment and consistency, but they do not define how goal satisfaction
evolves during execution, nor how different executions of the same
process affect intentional elements.
Information and Software Technology 196 (2026) 108146
22
J. Caballero-Villalobos et al.
A second family focuses on formal semantics for goal models independently of processes, often adopting satisfiability-style reasoning.
In this setting, goal models are interpreted as sets of logical constraints, and analysis determines whether there exists an assignment
that satisfies goals and contribution relations [65,66]. While these
semantics support rigorous reasoning about conflicts and trade-offs,
goal satisfaction is evaluated globally rather than incrementally, and
execution order is not represented explicitly.
A third family adopts evidence-based or marking-based semantics, in which goal satisfaction is computed from propagated labels
or accumulated evidence [12,67]. These approaches support partial
satisfaction and qualitative reasoning, but the connection to execution semantics is indirect: evidence is typically aggregated post hoc,
and process enabling conditions and precedence constraints are not
preserved as first-class semantic elements.
Across these families, mappings between processes and goals often
abstract execution order away or re-encode control-flow constraints
inside the goal model. This limits reasoning about how alternative executions contribute differently to high-level requirements, particularly
qualities and other non-functional requirements.
Our approach addresses this by defining an operational, stepwise
semantics for the composed process–goal system. We introduce a correspondence relation between process actions and intentional elements,
while ordering and dependencies are preserved by the process model
itself: composed transitions occur only when the corresponding process
action is enabled. This induces goal-state updates during execution,
enabling comparison of compliant traces by their impact on goal and
quality satisfaction.
8.4. Regulatory compliance as requirements–process co-design
A complementary body of work treats regulatory compliance as a
socio-technical design problem that spans requirements engineering,
organizational processes, and governance structures. In this perspective, compliance is not reduced to the satisfaction of formal constraints or goals, but is understood as the result of interpreting regulations, operationalizing them into requirements, and aligning these
interpretations with software and process artifacts across the system
lifecycle.
Several studies investigate regulatory compliance from a requirements engineering standpoint, emphasizing regulatory interpretation,
traceability, and knowledge management. Kosenkov et al. present a systematic mapping study of requirements engineering research related to
regulatory compliance, highlighting challenges in managing regulatory
knowledge, aligning requirements with organizational processes, and
supporting change over time [68]. This work characterizes compliance
as a continuous and context-dependent activity, rather than a one-time
verification task.
Focusing specifically on data protection regulations, Negri-Ribalta
et al. conduct a systematic mapping study of requirements engineering
approaches that address GDPR compliance [69]. Their analysis shows
that a wide range of RE artifacts and modeling techniques have been
proposed to support compliance, but also identifies a lack of operational mechanisms that connect high-level regulatory requirements to
concrete process behavior in a way that enables systematic analysis and
comparison.
Another line of work explores automated support for regulatory
compliance through natural language processing and artificial intelligence. Abualhaija et al. propose NLP-based techniques to automatically
assess the compliance of legal and contractual documents, such as
data processing agreements and privacy policies, against GDPR requirements [70,71]. These approaches demonstrate the feasibility of
automating parts of regulatory analysis, particularly at the level of
document completeness and consistency, but they do not address how
compliant interpretations are realized and compared at the level of
executable processes.
Across this body of work, compliance is mainly assessed in terms
of traceability, coverage, or binary satisfaction of regulatory requirements. While these approaches support regulatory interpretation, governance, and automation, they rarely distinguish among alternative
compliant designs or executions based on how they satisfy high-level
business requirements and qualities, and the operational consequences
of different interpretations are often not made explicit in process behavior.
Our work complements this literature by providing an operational
semantics that links regulatory requirements, represented as high-level
intentional models, to executable process behavior. Rather than assessing compliance solely via goal satisfaction or derived constraints,
we update quality satisfaction stepwise during execution, enabling
comparative analysis of compliant traces by how well they support
compliance-relevant qualities.
8.5. Goal-oriented compliance checking
The work most closely related to ours lies in goal-oriented compliance checking, where goal models are used not only for requirements
elicitation but also for compliance assessment. Horita et al. propose
verifying goal models against event logs using Linear Temporal Logic,
enabling conformance checking between observed behavior and goal
satisfaction conditions [72]. Ghasemi and Amyot introduce techniques
to enrich execution traces with goal satisfaction levels, allowing post
hoc analysis of how executions contribute to organizational objectives [73]. These approaches demonstrate that goal models can be
linked to execution data and used to reason about compliance beyond
simple rule satisfaction.
Other work in this space focuses on supporting decision-making under regulatory constraints. Ghanavati et al. use goal models to analyze
trade-offs among competing regulations and business objectives, helping organizations prioritize compliance strategies [50,74]. Siena et al.
propose the Nomos framework, which integrates goal modeling with
legal taxonomies to systematically derive compliant requirements from
regulations [5]. Badreddin et al. introduce measurable goal-oriented
models that transform prescriptive regulations into analyzable artifacts
for regulatory intelligence [75].
While these contributions establish the value of goal-oriented compliance reasoning, they typically treat goal satisfaction as the main
compliance criterion and abstract away from the operational mechanisms that produce it. Compliance is often assessed at the goalmodel level or via derived process constraints, yielding a largely binary
outcome over models or executions.
Our work extends this line by integrating goal-oriented compliance
checking with an operational semantics that links goal and quality satisfaction directly to process execution. Rather than assessing compliance
only by whether goals hold, we evaluate how executions contribute to
qualities and other non-functional requirements, enabling comparison
among compliant traces and analysis of satisfaction evolution over
time.
9. Conclusions
This paper addressed compliance assessment against high-level business requirements and non-functional requirements, rather than against
low-level process events. The framework combines an operational semantics for iStar goal models, a labeled transition system semantics
for process models, and a mapping that relates process actions to
goal model leaf elements. Compliance is evaluated on the synchronous
product of the process labeled transition system and the goal model
labeled transition system, where process executions induce changes
in goal satisfaction. This representation makes explicit the effect of
admissible process behavior on expert operationalization of high-level
business requirements expressed through qualities and contribution
links.
Information and Software Technology 196 (2026) 108146
23
J. Caballero-Villalobos et al.
The main technical contribution is a compliance notion for HLBRs
with two levels. Weak compliance is defined as an option to complete
the property. For every reachable product state, there exists a continuation to a success state in which all qualities hold, and every terminal
product state satisfies all qualities. Strong compliance is defined as
weak compliance plus quality satisfaction monotonicity, also called
stability. Quality satisfaction monotonicity requires that once a quality
is satisfied along a run, it remains satisfied in all subsequent states of
that run. This distinction supports analysis beyond a binary outcome
and supports comparison of process designs with respect to their ability
to preserve quality satisfaction. The paper provided algorithms for
weak compliance and for quality satisfaction monotonicity based on
forward and backward breadth first search on the product system, with
termination, correctness, completeness, and complexity guarantees.
The framework generalizes to any process notation that admits a
labeled transition system semantics. The paper instantiated this generalization with workflow nets and with dynamic condition response
graphs to cover imperative and declarative modeling styles. The instantiations illustrate how differences in behavioral flexibility affect
weak compliance and quality satisfaction monotonicity. Tool support
in Kogi was extended to report outcome categories aligned with weak
compliance, quality satisfaction monotonicity, and strong compliance,
to support dynamic condition response graphs in addition to workflow
nets, and to generate counterexamples that localize non compliance
and monotonicity violations in the product state space. A proof of
concept case study based on Regulation (EC) No 261/2004 showed
how the framework documents interpretive choices, such as an operational meaning of arrival time based on case law, and how it
supports evidentiary and documentation-related qualities for opentextured concepts, such as extraordinary circumstances. A formative
evaluation with a legal practitioner indicated that the approach supports traceability in the translation from legal sources to operational
artifacts and that explicit thresholds and evidence-oriented qualities
are relevant for explanation duties and review. The framework has
limitations. It depends on manual modeling and expert interpretation
when constructing goal models and mappings, so results depend on the
quality of these artifacts. Scalability is limited by the size of the product
state space, although the search procedures are linear in the explored
product graph. The evaluation covered one regulatory domain and one
practitioner interview. The current formalization does not cover multiactor settings, data-aware constraints, or explicit deontic reasoning,
including STIT logic and normative effects.
Future work will address broader empirical validation across domains and teams, incremental and modular analysis for evolving models and mappings, integration of data and evidence artifacts into the
product semantics, and extensions toward multi-agent goal models and
richer normative effects for legal compliance engineering.
CRediT authorship contribution statement
Juanita Caballero-Villalobos: Writing – review & editing, Writing
– original draft, Visualization, Validation, Software, Resources, Project
administration, Methodology, Investigation, Formal analysis, Conceptualization. Hubert Baumeister: Writing – review & editing, Validation,
Supervision, Software, Resources, Formal analysis. Elda Paja: Writing
– review & editing, Supervision, Conceptualization. Olga Kokoulina:
Writing – review & editing, Validation, Conceptualization. Hugo A.
López: Writing – review & editing, Supervision, Funding acquisition,
Formal analysis, Conceptualization.
Declaration of competing interest
The authors declare the following financial interests/personal relationships which may be considered as potential competing interests: Hugo A Lopez reports financial support was provided by Villum
Foundation. Not applicable reports financial support was provided by
Innovation Fund Denmark. If there are other authors, they declare
that they have no known competing financial interests or personal
relationships that could have appeared to influence the work reported
in this paper.
Acknowledgments
This work was supported by VILLUM FONDEN, Denmark (grant
VIL57420) through the Center for Digital Compliance (DICE), and
by the Innovation Fund Denmark project ‘‘Explainable Hybrid-AI for
Computational Law and Accurate Legal Chatbots’’ (XHAILe, grant 4355-
00018B). The authors thank Dr. Andrea Burattin for his valuable time
and insightful discussions during the early stages of this project. They
also thank Jonas Linder for his feedback and discussions on business
process compliance and the implications of goal models, which helped
improve this work.
Appendix A. Compliance checking algorithm - time and space
complexity
Proof of Theorem 5.1 (Time and Space Complexity). Algorithm 1 runs
in time 𝛩(|𝑆𝐶|+𝑚), where 𝑚 is the number of transitions in the product
labeled transition system 𝐶.
• Line 1 initializes 𝑆𝑄 by iterating over all states in 𝑆𝐶, taking
𝑂(|𝑆𝐶|) time.
• Line 2 identifies terminal states by checking outgoing transitions
for each state, taking 𝑂(|𝑆𝐶| × 𝛥), where 𝛥 is the maximum
out-degree.
• Line 3 calls Backward_BFS (Algorithm 3, lines 1–16), which runs
in 𝑂(|𝑆𝐶| + 𝑚).
• Line 4 performs set union and intersection in 𝑂(|𝑆𝐶|).
• Line 5 calls Forward_BFS (Algorithm 2, lines 1–14), also 𝑂(|𝑆𝐶|+
𝑚).
• Line 6 performs a subset check over sets of size |𝑆𝐶|, which is
𝑂(|𝑆𝐶|).
Since 𝑚 ≤ 𝛥 × |𝑆𝐶|, the total running time is 𝑂(𝛥 × |𝑆𝐶|). When 𝛥 is
bounded by a constant (i.e., due to a fixed number of enabled process
transitions), this time complexity reduces to 𝑂(|𝑆𝐶|).
Space complexity is dominated by storage of state sets and BFS
queues, each of size at most |𝑆𝐶|, resulting in 𝑂(|𝑆𝐶|) space. □
Appendix B. Compliance checking algorithm terminates
Proof of Theorem 5.2 (Termination). The Compliance Checking Algorithm (Algorithm 1) terminates because:
• The state space 𝑆𝐶 is finite, as it is the product of finite state sets
of the process model and goal model.
• Both Forward_BFS (lines 5–13) and Backward_BFS (lines 7–15)
traverse states without repetition and terminate once all reachable states are visited.
• All set operations (lines 1–6 in Algorithm 1) are performed on
finite sets.
• The algorithm returns a result at line 7 or 9, ensuring no infinite
loops.
Hence, the algorithm always terminates. □
Information and Software Technology 196 (2026) 108146
24
J. Caballero-Villalobos et al.
Appendix C. Correctness and completeness of the compliance
checking algorithm
Proof of Theorem 5.3 (Alg. 1 Correctness and Completeness). Let 𝑃 be
a process model, let 𝐺𝑀 be a goal model, and  be their product
labeled transition system; and let 𝑄 be a set of target qualities, we
prove the algorithm’s correctness and completeness with respect to
weak compliance.
Correctness: Suppose Algorithm 1 returns True (line 7). Then, by
line 6, all reachable states 𝑠 ∈ 𝑆reachable (line 5) satisfy 𝑠 ∈ 𝑆disj =
𝑆reach_Q ∪ (𝑆terminal ∩ 𝑆𝑄) (line 4). This means:
• Either 𝑠 can reach a state in 𝑆𝑄 (states where all qualities hold,
line 1) via backward BFS (line 3), or
• 𝑠 is a terminal state (line 2) where all qualities hold.
Hence, every reachable state can reach a compliant state or is itself
terminal and compliant, fulfilling the definition of weak compliance
(Definition 4.2).
Completeness: Assume the process model 𝑃𝑀 is weakly compliant
with respect to 𝐺𝑀. Then for every reachable state 𝑠, either 𝑠 ∈ 𝑆reach_Q
or 𝑠 ∈ 𝑆terminal ∩ 𝑆𝑄, implying 𝑠 ∈ 𝑆disj. Thus the subset condition on
line 6 holds, and the algorithm returns True (line 7).
If this condition does not hold, the algorithm returns False (line
9), indicating non-compliance. □
Appendix D. Correctness and completeness for stability checking
Proof of Theorem 5.3 (Alg. 4 Correctness and Completeness).
Correctness: We have to show that if the algorithm returns true, then
for all qualities on all reachable paths from 𝑠0
, once 𝑞 is satisfied by a
state 𝑠, then for all subsequent paths and states 𝑠
′
, 𝑠
′
satisfies 𝑞.
If the algorithm returns true, then for all reachable states 𝑠 ∈
𝑆reachable, we have that 𝑠 ∈ 𝑆stable(q) = 𝑆¬𝑞 ∪ 𝑆all(𝑞)
(line 5) for all
qualities 𝑞 ∈ 𝑄 (lines 7 and 11).
Case 1: 𝑠 ∈ 𝑆¬𝑞
: This means 𝑠 does not satisfy 𝑞 (line 2) and therefore
the implication is trivially satisfied.
Case 2: 𝑠 ∈ 𝑆all(q): Assume that there exists a state 𝑠𝑖
reachable from
𝑠 such that 𝑠𝑖 does not satisfy 𝑞. This means, that 𝑠𝑖 ∈ 𝑆¬𝑞
(line 2) and
thus 𝑠 ∈ 𝑆reach(¬𝑞)
(line 3), However, this leads to a contradiction, as
𝑠 ∈ 𝑆all(𝑞)
implies 𝑠 ∉ 𝑆reach(¬𝑞)
(line 4). Therefore, for all states 𝑠𝑖
reachable from 𝑠, satisfy 𝑞.
Completeness: Assume we have a quality 𝑞 ∈ 𝑄 and a state 𝑠 reachable
from 𝑠0
such that either 𝑠 satisfies 𝑞, or for each 𝑠𝑖
reachable from 𝑠,
𝑠𝑖
satisfies 𝑞. We have to show that the algorithm does not return false
for quality 𝑞.
Since 𝑠 is reachable from 𝑠0
, it is in the set 𝑆reachable (line 6). We
have to show that 𝑠 ∈ 𝑆stable(𝑞)
.
Case 1: 𝑠 does not satisfy 𝑞: This means 𝑠 ∈ 𝑆¬𝑞
(line 2) and thus
𝑠 ∈ 𝑆stable(𝑞)
(line 5).
Case 2: 𝑠 satisfies 𝑞: In this case, for 𝑠 ∈ 𝑆stable(𝑞)
to hold, we have
to show that 𝑠 ∈ 𝑆all(𝑞)
(line 5). Assume that 𝑠 ∉ 𝑆all(𝑞)
, which means
𝑠 ∈ 𝑆reach(¬𝑞)
(line 4). Thus, there exists a state 𝑠𝑖
reachable from 𝑠
and 𝑠𝑖 ∈ 𝑆¬𝑞
, which means 𝑠𝑖 does not satisfy 𝑞 (line 2). This is a
contraction with the definition of stability, where all states 𝑠𝑖
reachable
from 𝑠 satisfy quality 𝑞. Therefore, 𝑠 ∈ 𝑆all(𝑞) and thus 𝑠 ∈ 𝑆stable(𝑞)
. □
Appendix E. Proof of Theorem 5.4
Proof. The complexity is determined by the cost of reachability
analysis relative to the input size.
Time Complexity: The algorithm performs one initial Forward_BFS to
compute 𝑆𝑟𝑒𝑎𝑐ℎ𝑎𝑏𝑙𝑒 and |𝑄| subsequent iterations. Each iteration consists
of a Backward_BFS and several linear-time set operations (union, difference, and subset testing). Since a BFS on graph 𝐶 runs in 𝑂(|𝑆𝐶| + 𝑚),
the total time complexity is:
𝑇𝑠𝑡𝑏 = 𝑂(|𝑆𝐶| + 𝑚) + |𝑄| ⋅ 𝑂(|𝑆𝐶| + 𝑚) = 𝑂(|𝑄| ⋅ (|𝑆𝐶| + 𝑚))
Given 𝛥 = 𝑂(1), the number of transitions 𝑚 is 𝑂(|𝑆𝐶|), which simplifies
the bound to 𝑂(|𝑄| ⋅ |𝑆𝐶|).
Space Complexity: The primary memory overhead is the adjacency list
representation of 𝐶 and the storage of state sets. The adjacency list
requires 𝑂(|𝑆𝐶| + 𝑚) space. As the auxiliary sets for each quality 𝑞 are
reused or overwritten during each iteration, the total space complexity
is 𝑂(|𝑆𝐶| + 𝑚). □
References
[1] D. Monciardini, N. Bernaz, A. Andhov, The organizational dynamics of compliance with the UK modern slavery act in the food and tobacco sector, Bus. Soc.
60 (2021) http://dx.doi.org/10.1177/0007650319898195.
[2] T. Grisold, J. Mendling, M. Otto, J. vom Brocke, Adoption, use and management
of process mining in practice, Bus. Process. Manag. J. 27 (2021) http://dx.doi.
org/10.1108/BPMJ-03-2020-0112.
[3] M. Franceschetti, R. Seiger, H.A. López, A. Burattin, L. García-Bañuelos, B.
Weber, A characterisation of ambiguity in BPM, in: International Conference on
Conceptual Modeling, Springer, 2023, pp. 277–295, http://dx.doi.org/10.1007/
978-3-031-47262-6_15.
[4] T.D. Breaux, A.I. Antón, Analyzing regulatory rules for privacy and security
requirements, IEEE Trans. Softw. Eng. 34 (1) (2008) 5–20, http://dx.doi.org/
10.1109/TSE.2007.70746.
[5] A. Siena, J. Mylopoulos, A. Perini, A. Susi, Designing law-compliant software
requirements, in: Lecture Notes in Computer Science (Including Subseries Lecture
Notes in Artificial Intelligence and Lecture Notes in Bioinformatics), in: LNCS,
vol. 5829, Springer, 2009, pp. 472–486, http://dx.doi.org/10.1007/978-3-642-
04840-1_35.
[6] L. Chung, J.C.S.D.P. Leite, On non-functional requirements in software engineering, in: Lecture Notes in Computer Science (Including Subseries Lecture Notes in
Artificial Intelligence and Lecture Notes in Bioinformatics), in: LNCS, vol. 5600,
Springer, Berlin, Heidelberg, 2009, http://dx.doi.org/10.1007/978-3-642-02463-
4_19.
[7] H.A. López, Challenges in legal process discovery, in: CEUR Workshop
Proceedings, Vol. 2952, 2021, pp. 26–38.
[8] G. Governatori, The regorous approach to process compliance, in: Proceedings
of the 2015 IEEE 19th International Enterprise Distributed Object Computing
Conference Workshops and Demonstrations, EDOCW 2015, 2015, pp. 33–40,
http://dx.doi.org/10.1109/EDOCW.2015.28.
[9] A. Burattin, F.M. Maggi, A. Sperduti, Conformance checking based on multiperspective declarative process models, Expert Syst. Appl. 65 (2016) http://dx.
doi.org/10.1016/j.eswa.2016.08.040.
[10] H.A. López, S. Debois, T. Slaats, T.T. Hildebrandt, Business process compliance
using reference models of law, in: Lecture Notes in Computer Science, in: LNCS,
vol. 12076, 2020, pp. 378–399, http://dx.doi.org/10.1007/978-3-030-45234-
6_19.
[11] H.A. López, T.T. Hildebrandt, Three decades of formal methods in business
process compliance: A systematic literature review, 2024, arXiv:2410.10906.
[12] D. Amyot, S. Ghanavati, J. Horkoff, G. Mussbacher, L. Peyton, E. Yu, Evaluating
goal models within the goal-oriented requirement language, Int. J. Intell. Syst.
25 (2010) 841–877, http://dx.doi.org/10.1002/int.20433.
[13] J. Horkoff, F.B. Aydemir, E. Cardoso, T. Li, A. Maté, E. Paja, M. Salnitri, J.
Mylopoulos, P. Giorgini, Goal-oriented requirements engineering: An extended
systematic mapping study, Requir. Eng. 24 (2) (2019) 133–160, http://dx.doi.
org/10.1007/s00766-017-0280-z.
[14] D. Giannakopoulou, J. Magee, Fluent model checking for event-based systems,
in: Proceedings of the 9th European Software Engineering Conference Held
Jointly with 11th ACM SIGSOFT International Symposium on Foundations of
Software Engineering, ACM, 2003, pp. 257–266, http://dx.doi.org/10.1145/
949952.940106.
Information and Software Technology 196 (2026) 108146
25
J. Caballero-Villalobos et al.
[15] G. Meroni, R. Eshuis, Goal-oriented process monitoring: An artifact-driven
monitoring extension, in: International Conference on Advanced Information
Systems Engineering, in: LNBIP, vol. 557, Springer, 2025, pp. 119–127, http:
//dx.doi.org/10.1007/978-3-031-94590-8_15.
[16] L. López, X. Franch, J. Marco, Specialization in i* strategic rationale diagrams,
in: International Conference on Conceptual Modeling, Vol. 7532, Springer, 2018,
pp. 267–281, http://dx.doi.org/10.1007/978-3-642-34002-4_21.
[17] D. Amyot, O. Akhigbe, M. Baslyman, S. Ghanavati, M. Ghasemi, J. Hassine, L.
Lessard, G. Mussbacher, K. Shen, E. Yu, Combining goal modelling with business
process modelling: Two decades of experience with the user requirements
notation standard, Enterp. Model. Inf. Syst. Archit. 17 (2022).
[18] W. van Der Aalst, Process Mining: Data Science in Action, Springer, 2016,
http://dx.doi.org/10.1007/978-3-662-49851-4.
[19] T.T. Hildebrandt, R.R. Mukkamala, Declarative event-based workflow as distributed dynamic condition response graphs, Electron. Proc. Theor. Comput. Sci.
69 (2011) http://dx.doi.org/10.4204/eptcs.69.5.
[20] F. Dalpiaz, X. Franch, J. Horkoff, IStar 2.0 language guide, 2016, arXiv:1605.
07767.
[21] P. Giorgini, J. Mylopoulos, E. Nicchiarelli, R. Sebastiani, Formal reasoning
techniques for goal models, in: Lecture Notes in Computer Science (Including Subseries Lecture Notes in Artificial Intelligence and Lecture Notes in
Bioinformatics, Vol. 2800, 2003, http://dx.doi.org/10.1007/978-3-540-39733-
5_1.
[22] A.M. Grubb, M. Chechik, Formal reasoning for analyzing goal models that evolve
over time, Requir. Eng. 26 (2021) http://dx.doi.org/10.1007/s00766-021-00350-
8.
[23] J. Caballero-Villalobos, A. Burattin, H.A. López, High-level requirements-driven
business process compliance, in: Lecture Notes in Business Information Processing, in: LNBIP, vol. 564, Springer Nature Switzerland, 2026, pp. 23–39,
http://dx.doi.org/10.1007/978-3-032-02929-4_2.
[24] J. Caballero-Villalobos, H.A. López, Kogi: A tool for assessing high-level business
process compliance, in: CEUR Workshop Proceedings, Vol. 4032, 2025, pp.
216–223.
[25] M. Pesic, H. Schonenberg, W.M. Van der Aalst, DECLARE: Full support for
loosely-structured processes, in: Proceedings - IEEE International Enterprise
Distributed Object Computing Workshop, EDOC, IEEE, 2007, p. 287, http://dx.
doi.org/10.1109/EDOC.2007.4384001.
[26] Object Management Group, Case Management Model and Notation, Specification,
CMMN, Object Management Group, MA, USA, 2016, URL: https://www.omg.org/
spec/CMMN/1.1/About-CMMN.
[27] A. Jalali, Evaluating user acceptance of knowledge-intensive business process
modeling languages, Softw. Syst. Model. 22 (6) (2023) 1803–1826, http://dx.
doi.org/10.1007/s10270-023-01120-6.
[28] T.T. Hildebrandt, A.A. Andaloussi, L.R. Christensen, S. Debois, N.P. Healy, H.A.
López, M. Marquard, N.L. Møller, A.C. Petersen, T. Slaats, et al., Ecoknow:
Engineering effective, co-created and compliant adaptive case management
systems for knowledge workers, in: Proceedings - 2020 IEEE/ACM International
Conference on Software and System Processes, ICSSP 2020, 2020, pp. 155–164,
http://dx.doi.org/10.1145/3379177.3388908.
[29] T. Hildebrandt, R.R. Mukkamala, T. Slaats, F. Zanitti, Contracts for crossorganizational workflows as timed dynamic condition response graphs, J. Log.
Algebr. Program. 82 (5–7) (2013) 164–185, http://dx.doi.org/10.1016/j.jlap.
2013.05.005.
[30] R. Strømsted, H.A. López, S. Debois, M. Marquard, Dynamic evaluation forms
using declarative modeling, in: CEUR Workshop Proceedings, Vol. 2196, 2018,
pp. 172–179.
[31] T.T. Hildebrandt, H.A. López, T. Slaats, Declarative choreographies with time and
data, in: Lecture Notes in Business Information Processing, in: LNBIP, vol. 490,
Springer, 2023, pp. 73–89, http://dx.doi.org/10.1007/978-3-031-41623-1_5.
[32] T. Zuckmantel, H.-A. López-Acosta, Y. Zhou, B. Düdder, T. Hildebrandt, Data
integrity-by-design: Combining declarative object-centric choreographies and
entity relationship models, in: Proceedings of the 31st International Conference
on Cooperative Information Systems, CoopIS 2025, in: Lecture Notes in Computer
Science, vol. 15535, Springer, 2026, pp. 221–238, http://dx.doi.org/10.1007/
978-3-032-15538-2_13.
[33] A.K. Christfort, H.A. López, DCR-JS: An online environment for declarative process mining, in: 23rd International Conference on Business Process Management,
CEUR-WS, 2025, pp. 256–263.
[34] R.M. Keller, Formal verification of parallel programs, Commun. ACM 19 (1976)
http://dx.doi.org/10.1145/360248.360251.
[35] J.M.E. van Der Werf, A. Rivkin, M. Montali, A. Polyvyanyy, Correctness notions
for Petri nets with identifiers, Fund. Inform. 190 (2024) http://dx.doi.org/10.
3233/FI-242169.
[36] P. Bresciani, A. Perini, P. Giorgini, F. Giunchiglia, J. Mylopoulos, Tropos: An
agent-oriented software development methodology, Auton. Agents Multi-Agent
Syst. 8 (2004) http://dx.doi.org/10.1023/B:AGNT.0000018806.20944.ef.
[37] ITU-T, User Requirements Notation (URN) – Language Definition, Recommendation ITU-T Z.151, International Telecommunication Union, Geneva, Switzerland,
2008, URL: https://www.itu.int/rec/T-REC-Z.151-200811-S.
[38] A. Dardenne, A. van Lamsweerde, S. Fickas, Goal-directed requirements acquisition, Sci. Comput. Program. 20 (1993) 3–50, http://dx.doi.org/10.1016/0167-
6423(93)90021-G.
[39] E. Yu, Modelling Strategic Relationships for Process Reengineering (Ph.D. thesis),
University of Toronto, Toronto, Canada, 1995.
[40] X. Franch, J.C.S.D.P. Leite, G. Mussbacher, J. Mylopoulos, A. Perini, Social
Modeling Using the i* Framework, Springer, Cham, 2024, http://dx.doi.org/10.
1007/978-3-031-72107-6.
[41] Object Management Group, Business Process Model and Notation (BPMN) Version 2.0, Standard, Object Management Group, 2011, URL: https://www.omg.
org/spec/BPMN/2.0/.
[42] G.D. Giacomo, M.Y. Vardi, Linear temporal logic and linear dynamic logic on
finite traces, in: IJCAI International Joint Conference on Artificial Intelligence,
IJCAI/AAAI Press, 2013, pp. 854–860.
[43] J. Esparza, Decidability and complexity of Petri net problems — An introduction,
in: Lectures on Petri Nets I: Basic Models: Advances in Petri Nets, Springer,
Berlin, Heidelberg, 1998, pp. 374–428, http://dx.doi.org/10.1007/3-540-65306-
6_20, Chapter 9.
[44] A. Awad, M. Weidlich, M. Weske, Specification, verification and explanation of
violation for data aware compliance rules, in: Lecture Notes in Computer Science
(Including Subseries Lecture Notes in Artificial Intelligence and Lecture Notes in
Bioinformatics), in: LNCS, vol. 5900, 2009, pp. 500–515, http://dx.doi.org/10.
1007/978-3-642-10383-4_37.
[45] D. Knuplesch, L.T. Ly, S. Rinderle-Ma, H. Pfeifer, P. Dadam, On enabling dataaware compliance checking of business process models, Concept. Model. 6412
LNCS (2013) http://dx.doi.org/10.1007/978-3-642-16373-9_24.
[46] A. Elgammal, O. Turetken, W.-J. van den Heuvel, M. Papazoglou, Formalizing
and applying compliance patterns for business process compliance, Softw. Syst.
Model. 15 (1) (2016) 119–146, http://dx.doi.org/10.1007/s10270-014-0395-3.
[47] E. Ramezani, D. Fahland, W.M.P. van der Aalst, Where did I misbehave?
Diagnostic information in compliance checking, in: International Conference on
Business Process Management, in: LNCS, vol. 7481, Springer, 2012, pp. 262–278,
http://dx.doi.org/10.1007/978-3-642-32885-5_21.
[48] A. Ghose, G. Koliadis, Auditing business process compliance, in: International
Conference on Service-Oriented Computing, in: LNCS, vol. 4749, Springer, 2007,
pp. 169–180, http://dx.doi.org/10.1007/978-3-540-74974-5_14.
[49] S. Sadiq, G. Governatori, K. Namiri, Modeling control objectives for business
process compliance, in: Lecture Notes in Computer Science (Including Subseries
Lecture Notes in Artificial Intelligence and Lecture Notes in Bioinformatics), in:
LNCS, vol. 4714, Springer, 2007, pp. 149–164, http://dx.doi.org/10.1007/978-
3-540-75183-0_12.
[50] S. Ghanavati, A. Rifaut, E. Dubois, D. Amyot, Goal-oriented compliance with
multiple regulations, in: 2014 IEEE 22nd International Requirements Engineering
Conference, RE, IEEE, 2014, pp. 73–82, http://dx.doi.org/10.1109/RE.2009.42.
[51] A.I. Antón, J.B. Earp, A. Reese, Analyzing website privacy requirements using a
privacy goal taxonomy, in: Proceedings IEEE Joint International Conference on
Requirements Engineering, IEEE, 2002, pp. 23–31, http://dx.doi.org/10.1109/
ICRE.2002.1048502.
[52] A.I. Antón, D. Bolchini, Q. He, The use of goals to extract privacy and
security requirements from policy statements, in: Proceedings of the 26th IEEE
International Conference on Software Engineering, 2003.
[53] A.K. Massey, P.N. Otto, A.I. Antón, Aligning requirements with HIPAA in the
itrust system, in: Proceedings of the 16th IEEE International Requirements
Engineering Conference, RE’08, IEEE, 2008, pp. 335–336, http://dx.doi.org/10.
1109/RE.2008.53.
[54] T.D. Breaux, M.W. Vail, A.I. Anton, Towards regulatory compliance: Extracting
rights and obligations to align requirements with regulations, in: 14th IEEE
International Requirements Engineering Conference, RE’06, IEEE, 2006, pp.
49–58, http://dx.doi.org/10.1109/RE.2006.68.
[55] J.C. Maxwell, A.I. Antón, Developing production rule models to aid in acquiring
requirements from legal texts, in: Proceedings of the IEEE International Conference on Requirements Engineering, IEEE, 2009, pp. 101–110, http://dx.doi.org/
10.1109/RE.2009.21.
[56] D. Torre, M. Alferez, G. Soltana, M. Sabetzadeh, L. Briand, Modeling data
protection and privacy: Application and experience with GDPR, Softw. Syst.
Model. 20 (6) (2021) 2071–2087, http://dx.doi.org/10.1007/s10270-021-00935-
5.
[57] G. Morales, K. Pragyan, S. Jahan, M.B. Hosseini, R. Slavin, A large language model approach to code and privacy policy alignment, in: Proceedings
- 2024 IEEE International Conference on Software Analysis, Evolution and
Reengineering, SANER 2024, IEEE, 2024, pp. 79–90, http://dx.doi.org/10.1109/
SANER60148.2024.00016.
[58] D. Rodriguez, I. Yang, J.M. Del Alamo, N. Sadeh, Large language models: A
new approach for privacy policy analysis at scale, Computing 106 (12) (2024)
3879–3903, http://dx.doi.org/10.1007/s00607-024-01331-9.
[59] S. Abualhaija, M. Ceci, N. Sannier, D. Bianculli, S. Lannier, M. Siclari, O.
Voordeckers, S. Tosza, LLM-assisted extraction of regulatory requirements: A case
study on the GDPR, in: 2025 IEEE 33rd International Requirements Engineering
Conference, RE, 2025, pp. 142–154, http://dx.doi.org/10.1109/RE63999.2025.
00023.
Information and Software Technology 196 (2026) 108146
26
J. Caballero-Villalobos et al.
[60] D. Basin, M. Clavel, M. Egea, A decade of model-driven security, in: Proceedings
of ACM Symposium on Access Control Models and Technologies, SACMAT, 2011,
pp. 1–10, http://dx.doi.org/10.1145/1998441.1998443.
[61] S. Gürses, C. Troncoso, C. Diaz, Engineering privacy by design reloaded, in:
Amsterdam Privacy Conference, 2015, pp. 1–21.
[62] S. Abrahão, E. Insfrán, F. González-Ladrón-de-Guevara, M. Fernández-Diego,
C. Cano-Genoves, R.P. de Oliveira, Assessing the effectiveness of goal-oriented
modeling languages: A family of experiments, Inf. Softw. Technol. 116 (2019)
http://dx.doi.org/10.1016/j.infsof.2019.08.003.
[63] M. Salnitri, E. Paja, M. Poggianella, P. Giorgini, et al., STS-tool 3.0: Maintaining
security in socio-technical systems, in: CEUR Workshop Proceedings, Vol. 1367,
2015, pp. 205–212.
[64] H.A. López, F. Massacci, N. Zannone, Goal-equivalent secure business process
re-engineering, in: International Conference on Service-Oriented Computing, in:
LNCS, vol. 4907, Springer, 2007, pp. 212–223, http://dx.doi.org/10.1007/978-
3-540-93851-4_21.
[65] R. De Landtsheer, E. Letier, A. Van Lamsweerde, Deriving tabular event-based
specifications from goal-oriented requirements models, Requir. Eng. 9 (2) (2004)
104–120, http://dx.doi.org/10.1007/s00766-004-0189-1.
[66] A. van Lamsweerde, Goal-oriented requirements engineering: from system objectives to UML models to precise software specifications, in: 25th International
Conference on Software Engineering, 2003. Proceedings., 2003, pp. 744–745,
http://dx.doi.org/10.1109/ICSE.2003.1201266.
[67] J. Horkoff, E. Yu, Analyzing goal models: different approaches and how to choose
among them, in: Proceedings of the ACM Symposium on Applied Computing,
2011, pp. 675–682, http://dx.doi.org/10.1145/1982185.1982334.
[68] O. Kosenkov, P. Elahidoost, T. Gorschek, J. Fischbach, D. Mendez, M. Unterkalmsteiner, D. Fucci, R. Mohanani, Systematic mapping study on requirements
engineering for regulatory compliance of software systems, Inf. Softw. Technol.
178 (2025) 107622, http://dx.doi.org/10.1016/j.infsof.2024.107622.
[69] C. Negri-Ribalta, M. Lombard-Platet, C. Salinesi, Understanding the GDPR from a
requirements engineering perspective—a systematic mapping study on regulatory
data protection requirements, Requir. Eng. 29 (2024) 523–549, http://dx.doi.
org/10.1007/s00766-024-00423-4.
[70] O.A. Cejas, M.I. Azeem, S. Abualhaija, L.C. Briand, NLP-based automated compliance checking of data processing agreements against GDPR, IEEE Trans. Softw.
Eng. 49 (9) (2023) 4282–4303, http://dx.doi.org/10.1109/TSE.2023.3288901.
[71] O. Amaral, S. Abualhaija, D. Torre, M. Sabetzadeh, L.C. Briand, AI-enabled
automation for completeness checking of privacy policies, IEEE Trans. Softw.
Eng. 48 (11) (2021) 4647–4674, http://dx.doi.org/10.1109/TSE.2021.3124332.
[72] H. Horita, H. Hirayama, Y. Tahara, A. Ohsuga, Towards goal-oriented conformance checking, in: Proceedings of the International Conference on Software
Engineering and Knowledge Engineering, SEKE, Vol. 2015-January, 2015, pp.
484–489.
[73] M. Ghasemi, D. Amyot, From event logs to goals: A systematic literature review
of goal-oriented process mining, Requir. Eng. (2020) http://dx.doi.org/10.1007/
s00766-018-00308-3.
[74] S. Ghanavati, D. Amyot, L. Peyton, Compliance analysis based on a goal-oriented
requirement language evaluation methodology, in: 2009 17th IEEE International
Requirements Engineering Conference, 2009, pp. 133–142, http://dx.doi.org/10.
1109/RE.2009.42.
[75] O. Badreddin, G. Mussbacher, D. Amyot, S.A. Behnam, R. Rashidi-Tabrizi,
E. Braun, M. Alhaj, G. Richards, Regulation-based dimensional modeling for
regulatory intelligence, in: 2013 6th International Workshop on Requirements
Engineering and Law, RELAW, 2013, pp. 1–10, http://dx.doi.org/10.1109/
RELAW.2013.6671340.
Juanita Caballero-Villalobos is a second-year Ph.D. student at the Technical University of Denmark. She obtained
her Master’s degree in Artificial Intelligence and Master’s degree in Industrial Engineering, both from Javeriana
University in Colombia. Her research interests focus on
business process compliance in the context of European
Union data protection and privacy law. She is particularly interested in methods for aligning legal and process
terminology, modeling legal requirements, and visualizing compliance outcomes. Her PhD project is carried out
within the EuroTech Alliance in collaboration with the
Eindhoven University of Technology and draws on interdisciplinary perspectives from requirements engineering,
business intelligence, legal practice, and end users.
Dr. Ing. Hubert Baumeister is an Associate Professor
in the Department of Applied Mathematics and Computer
Science (DTU Compute) at the Technical University of
Denmark (DTU). He obtained his PhD (Dr.-Ing.) in Computer
Science from Saarland University while working at the
Max–Planck-Institute for Computer Science in Saarbrücken.
His current research interests include software engineering,
formal methods, UML and its semantics/extensions, serviceorientation, and agile software development processes such
as extreme programming. He has made significant contributions to the development of EASETECH, a life-cycle
assessment (LCA) modeling system for environmental technologies that handles complex material flows and supports
process-oriented LCA. He has published in international
conferences. He was the academic chair of XP2004 and
program co-chair of XP2005, XP2013, and XP2017.
Dr. Elda Paja is an Associate Professor at the IT University of Copenhagen (ITU), affiliated with the Software
Engineering Section and the Center for Information Security and Trust (CISAT). Her research focuses on security
requirements engineering, human and social aspects of security and privacy in critical domains, empirical software
engineering, including practices for AI/ML-based systems,
and the integration of security into agile development. She
obtained her PhD from the University of Trento, Italy,
and has been serving the requirements engineering and
conceptual modeling communities since 2011. She currently
serves as Associate Editor for Data & Knowledge Engineering
(DKE) and contributes, among others, to the International
Conceptual Modeling Conference (ER), REFSQ and the
IEEE International Requirements Engineering Conference in
program committee and various organizational roles.
Dr. Olga Kokoulina is an Assistant Professor at the Centre
for Private Governance, University of Copenhagen. Her research focuses on the governance of emerging technologies,
particularly regulatory compliance automation and the regulation of Big Tech. Previously, she was a Carlsberg-funded
Postdoctoral Researcher at the Centre for Information and
Innovation Law (CIIR), University of Copenhagen, where she
worked on accountability in algorithmic decision-making.
She holds degrees from the University of Oxford, University
of Copenhagen, Lund University, and the Higher School of
Economics. Her work engages interdisciplinary communities
across law, governance, and public policy.
Dr. Hugo A. López is an Associate Professor at the Technical University of Denmark, with a Ph.D. from the IT
University of Copenhagen. His research focuses on integrating formal methods and business process management,
with a particular emphasis on declarative process modeling,
compliance, and process mining. He has made contributions to the theory of communicating and concurrent
systems, including session-based concurrency, as well as
to the development of frameworks for ensuring regulatory
compliance in process-aware systems. More recently, his
work explores data-driven techniques for monitoring and
optimizing processes, including stream-based and process
mining approaches. He is actively involved in the BPM and
formal methods communities and has served on program
committees of major conferences such as BPM, ICPM, and
ER.
Information and Software Technology 196 (2026) 108146
27