import fs from 'node:fs';
import crypto from 'node:crypto';

const here = new URL('.', import.meta.url);

function uuid(key) {
  const h = crypto.createHash('sha256').update(`sales-forecast:${key}`).digest('hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-4${h.slice(13, 16)}-a${h.slice(17, 20)}-${h.slice(20, 32)}`;
}

// -------------------------------------------------------------------------
// piStar 2.1.0 — pedagogical SR model
// -------------------------------------------------------------------------

const actors = [];
const links = [];
const dependencies = [];
const display = {};
const refs = new Map();

function actor(key, text, x, y, width, height) {
  const value = { id: uuid(`actor:${key}`), text, type: 'istar.Role', x, y, nodes: [], customProperties: {} };
  actors.push(value);
  refs.set(key, value);
  display[value.id] = { width, height };
  return value;
}

function node(owner, key, text, type, x, y, width = 230) {
  const value = { id: uuid(`node:${key}`), text, type: `istar.${type}`, x, y, customProperties: {} };
  owner.nodes.push(value);
  refs.set(key, value);
  display[value.id] = { width, height: 58 };
  return value;
}

// piStar stores custom-property values as strings.  Each goal therefore gets
// one `condition` attribute containing the OCL state predicate that denotes
// when that goal is satisfied.
function condition(key, ocl) {
  const element = refs.get(key);
  if (!element) throw new Error(`Unknown intentional element: ${key}`);
  element.customProperties.condition = ocl.replace(/\s+/g, ' ').trim();
}

function link(type, source, target, label) {
  const value = {
    id: uuid(`link:${type}:${source}:${target}:${label ?? ''}`),
    type: `istar.${type}Link`,
    source: refs.get(source).id,
    target: refs.get(target).id
  };
  if (label) value.label = label;
  links.push(value);
}

function dependency(key, text, type, source, target, x, y) {
  const value = {
    id: uuid(`dependum:${key}`), text, type: `istar.${type}`, x, y,
    customProperties: {}, reinforcedBy: [], directionFlow: 'inbound',
    source: refs.get(source).id, target: refs.get(target).id
  };
  dependencies.push(value);
  refs.set(key, value);
  display[value.id] = { width: 220, height: 58 };
  const first = {
    id: uuid(`dependency-link:${key}:in`), type: 'istar.DependencyLink',
    source: refs.get(source).id, target: value.id
  };
  const second = {
    id: uuid(`dependency-link:${key}:out`), type: 'istar.DependencyLink',
    source: value.id, target: refs.get(target).id
  };
  links.push(first, second);
}

const management = actor('management', 'Management', 60, 50, 760, 600);
node(management, 'governance', 'Governed sales proposal completed', 'Goal', 290, 105, 300);
node(management, 'approvedOnly', 'Authorized proposal delivered', 'Goal', 105, 225, 285);
node(management, 'profitability', 'Profitability assessment completed', 'Goal', 420, 225, 285);
node(management, 'financialRisk', 'Required risk clearances obtained', 'Goal', 105, 350, 300);
node(management, 'salesVisible', 'Expected sales information recorded', 'Goal', 430, 350, 300);
node(management, 'reliableSales', 'Reliable and profitable sales', 'Quality', 270, 485, 300);

link('AndRefinement', 'approvedOnly', 'governance');
link('AndRefinement', 'profitability', 'governance');
link('AndRefinement', 'financialRisk', 'governance');
link('AndRefinement', 'salesVisible', 'governance');
link('Contribution', 'governance', 'reliableSales', 'make');

condition('approvedOnly', `
  self.group.proposalSent
  and self.group.approvalStatus = #approved
  and self.group.sentRevision = self.group.approvedRevision
  and self.group.sentRevision = self.group.currentRevision
`);
condition('profitability', `
  self.group.profitabilityAssessmentCompleted
  and (self.group.approvalStatus = #approved or self.group.approvalStatus = #rejected)
`);
condition('financialRisk', `
  (not self.group.dealAmountOver250000NIS or self.group.financialControlApproved)
  and (not self.group.creditReviewRequired or self.group.creditControlApproved)
`);
condition('salesVisible', `self.group.expectedSalesInformationRecorded`);
condition('governance', `
  self.group.proposalSent
  and self.group.approvalStatus = #approved
  and self.group.sentRevision = self.group.approvedRevision
  and self.group.sentRevision = self.group.currentRevision
  and self.group.profitabilityAssessmentCompleted
  and (not self.group.dealAmountOver250000NIS or self.group.financialControlApproved)
  and (not self.group.creditReviewRequired or self.group.creditControlApproved)
  and self.group.expectedSalesInformationRecorded
`);

const customerManager = actor('customerManager', 'Customer Manager', 900, 50, 1120, 870);
node(customerManager, 'progressProposal', 'Price proposal progressed', 'Goal', 1280, 105, 310);
node(customerManager, 'proposalPrepared', 'Proposal prepared', 'Goal', 970, 225);
node(customerManager, 'approvalObtained', 'Commercial approval obtained', 'Goal', 1295, 225, 270);
node(customerManager, 'proposalDelivered', 'Proposal delivered to customer', 'Goal', 1620, 225, 270);
node(customerManager, 'recordDeal', 'Record customer and deal', 'Task', 940, 355);
node(customerManager, 'requestTechnical', 'Request technical package', 'Task', 1220, 355);
node(customerManager, 'validateService', 'Validate service terms', 'Task', 1500, 355);
node(customerManager, 'submitApproval', 'Submit proposal for approval', 'Task', 940, 485, 260);
node(customerManager, 'sendProposal', 'Send approved price proposal', 'Task', 1240, 485);
node(customerManager, 'transferOrder', 'Transfer approved deal to order', 'Task', 1520, 485, 280);
node(customerManager, 'respondQuickly', 'Respond quickly and flexibly', 'Quality', 990, 645, 285);
node(customerManager, 'satisfyCustomer', 'Satisfy customer', 'Quality', 1345, 645);
node(customerManager, 'achieveSales', 'Achieve sales targets', 'Quality', 1655, 645);
node(customerManager, 'outsideSystem', 'Prepare and send proposal outside IS', 'Task', 1260, 770, 330);

link('AndRefinement', 'proposalPrepared', 'progressProposal');
link('AndRefinement', 'approvalObtained', 'progressProposal');
link('AndRefinement', 'proposalDelivered', 'progressProposal');
link('AndRefinement', 'recordDeal', 'proposalPrepared');
link('AndRefinement', 'requestTechnical', 'proposalPrepared');
link('AndRefinement', 'validateService', 'proposalPrepared');
link('AndRefinement', 'submitApproval', 'approvalObtained');
link('AndRefinement', 'sendProposal', 'proposalDelivered');
link('Contribution', 'sendProposal', 'satisfyCustomer', 'make');
link('Contribution', 'sendProposal', 'achieveSales', 'help');
link('Contribution', 'transferOrder', 'achieveSales', 'make');
link('Contribution', 'outsideSystem', 'respondQuickly', 'make');
link('Contribution', 'outsideSystem', 'satisfyCustomer', 'help');

condition('proposalPrepared', `self.group.proposalPrepared`);
condition('approvalObtained', `
  self.group.approvalStatus = #approved
  and self.group.approvedRevision = self.group.currentRevision
`);
condition('proposalDelivered', `self.group.proposalSent`);
condition('progressProposal', `
  self.group.proposalPrepared
  and self.group.approvalStatus = #approved
  and self.group.approvedRevision = self.group.currentRevision
  and self.group.proposalSent
  and self.group.sentRevision = self.group.currentRevision
`);

const presale = actor('presale', 'Presale Engineer', 60, 760, 700, 410);
node(presale, 'technicalPackage', 'Technical package prepared', 'Goal', 270, 815, 280);
node(presale, 'createBom', 'Create bill of materials', 'Task', 105, 950);
node(presale, 'createHld', 'Create high-level design', 'Task', 405, 950);
link('AndRefinement', 'createBom', 'technicalPackage');
link('AndRefinement', 'createHld', 'technicalPackage');
condition('technicalPackage', `
  not self.group.requiresPresaleEngineer
  or self.group.technicalPackagePrepared
`);

const service = actor('service', 'Service Agreements', 830, 990, 650, 390);
node(service, 'serviceTerms', 'Service terms validated', 'Goal', 1020, 1045, 270);
node(service, 'reviewService', 'Review service agreement', 'Task', 865, 1175, 260);
node(service, 'approveService', 'Approve service terms', 'Task', 1160, 1175);
link('AndRefinement', 'reviewService', 'serviceTerms');
link('AndRefinement', 'approveService', 'serviceTerms');
condition('serviceTerms', `
  self.group.serviceFeeAtLeast7Percent or self.group.serviceTermsValidated
`);

const approvers = actor('approvers', 'Commercial Approvers', 2120, 50, 780, 600);
node(approvers, 'decisionMade', 'Commercial decision made', 'Goal', 2350, 105, 300);
node(approvers, 'assessProfitability', 'Assess profitability', 'Task', 2170, 235, 260);
node(approvers, 'approveProposal', 'Approve proposal', 'Task', 2480, 235);
node(approvers, 'rejectProposal', 'Reject or request revision', 'Task', 2170, 365, 270);
node(approvers, 'maintainProfit', 'Maintain profitability', 'Quality', 2490, 365);
node(approvers, 'completeRound', 'Complete approval round', 'Goal', 2330, 495, 280);
link('AndRefinement', 'assessProfitability', 'decisionMade');
link('OrRefinement', 'approveProposal', 'decisionMade');
link('OrRefinement', 'rejectProposal', 'decisionMade');
link('Contribution', 'assessProfitability', 'maintainProfit', 'make');
condition('decisionMade', `
  self.group.approvalStatus = #approved or self.group.approvalStatus = #rejected
`);
condition('completeRound', `
  self.group.reviewCompleted
  and (self.group.approvalStatus = #approved or self.group.approvalStatus = #rejected)
`);

const controllers = actor('controllers', 'Financial and Credit Control', 1550, 990, 820, 390);
node(controllers, 'riskReviewed', 'Financial and credit risks reviewed', 'Goal', 1790, 1045, 310);
node(controllers, 'financialReview', 'Review high-value deal', 'Task', 1590, 1175, 260);
node(controllers, 'creditReview', 'Review customer credit', 'Task', 1900, 1175, 260);
link('AndRefinement', 'financialReview', 'riskReviewed');
link('AndRefinement', 'creditReview', 'riskReviewed');
condition('riskReviewed', `
  (not self.group.dealAmountOver250000NIS or self.group.financialControlApproved)
  and (not self.group.creditReviewRequired or self.group.creditControlApproved)
`);

const customer = actor('customer', 'Customer', 2460, 810, 530, 360);
node(customer, 'receiveProposal', 'Receive price proposal', 'Goal', 2590, 875, 270);
node(customer, 'usableProposal', 'Proposal is timely and usable', 'Quality', 2580, 1000, 285);
condition('receiveProposal', `self.group.proposalSent`);

dependency('depTechnical', 'Technical package', 'Resource', 'requestTechnical', 'technicalPackage', 790, 790);
dependency('depService', 'Validated service terms', 'Goal', 'validateService', 'serviceTerms', 1200, 945);
dependency('depApproval', 'Approved price proposal', 'Goal', 'approvalObtained', 'completeRound', 2040, 370);
dependency('depProfitability', 'Profitability assessment', 'Goal', 'profitability', 'assessProfitability', 2020, 200);
dependency('depRisk', 'Financial and credit clearance', 'Goal', 'financialRisk', 'riskReviewed', 1450, 1070);
dependency('depApprovedOnly', 'Authorized proposal delivery', 'Goal', 'approvedOnly', 'sendProposal', 850, 520);
dependency('depDelivery', 'Price proposal', 'Resource', 'receiveProposal', 'sendProposal', 2340, 770);
dependency('depSalesData', 'Expected sales information', 'Resource', 'salesVisible', 'recordDeal', 850, 290);

condition('depService', `self.group.serviceFeeAtLeast7Percent or self.group.serviceTermsValidated`);
condition('depApproval', `
  self.group.approvalStatus = #approved
  and self.group.approvedRevision = self.group.currentRevision
`);
condition('depProfitability', `self.group.profitabilityAssessmentCompleted`);
condition('depRisk', `
  (not self.group.dealAmountOver250000NIS or self.group.financialControlApproved)
  and (not self.group.creditReviewRequired or self.group.creditControlApproved)
`);
condition('depApprovedOnly', `
  self.group.proposalSent
  and self.group.approvalStatus = #approved
  and self.group.sentRevision = self.group.approvedRevision
  and self.group.sentRevision = self.group.currentRevision
`);

const pistar = {
  datastructures: [], concepts: [], reinforcements: [], actors, orphans: [], dependencies, links, display,
  tool: 'pistar.2.1.0', istar: '2.0', saveDate: new Date(0).toUTCString(),
  diagram: {
    width: 3100, height: 1450, name: 'Sales Forecast — readable case-study model',
    customProperties: {
      Description: 'Pedagogical reconstruction of the Sales Forecast case. Every goal has a string-valued condition attribute containing its OCL satisfaction predicate over the shared SalesCase state.'
    }
  }
};

fs.writeFileSync(new URL('sales_forecast_full_sr_pistar.txt', here), `${JSON.stringify(pistar, null, 2)}\n`);

// -------------------------------------------------------------------------
// bpmn.io — prescribed process + observed workaround in one collaboration
// -------------------------------------------------------------------------

const esc = value => String(value).replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;');

const lanes = [
  ['CustomerManager', 'Customer Manager'],
  ['PresaleEngineer', 'Presale Engineer'],
  ['ServiceAgreements', 'Service Agreements'],
  ['CommercialApprover', 'Commercial Approver'],
  ['FinancialControl', 'Financial Control'],
  ['CreditControl', 'Credit Control'],
  ['OrderOperations', 'Order Operations']
];

const nodes = [];
function bnode(id, type, name, lane, x, y, extra = {}) {
  nodes.push({ id, type, name, lane, x, y, ...extra });
}

// The overview intentionally exposes only the hand-offs between departments.
// Conditional checks, revisions, and local rejection paths live inside the
// corresponding collapsed subprocess instead of becoming top-level gateways.
bnode('OfficialStart', 'startEvent', 'Price proposal requested', 'CustomerManager', 500, 112);
bnode('RecordCustomerDeal', 'task', 'Record customer and deal', 'CustomerManager', 575, 95, {
  documentation: 'Resulting state: self.customerDealRecorded and self.expectedSalesInformationRecorded.'
});
bnode('PrepareTechnicalPackage', 'subProcess', 'Prepare required technical package', 'PresaleEngineer', 775, 235, {
  documentation: 'Presale determines whether BoM and HLD are required and prepares the applicable documents. When required, the resulting state satisfies self.technicalPackagePrepared.'
});
bnode('ValidateServiceTerms', 'subProcess', 'Validate service terms', 'ServiceAgreements', 975, 375, {
  documentation: 'Service Agreements checks the service fee and coordinates corrections. When a review is required, the resulting state satisfies self.serviceTermsValidated.'
});
bnode('SubmitApproval', 'sendTask', 'Submit proposal for approval', 'CustomerManager', 1175, 95, {
  documentation: 'Resulting state: self.proposalPrepared and self.approvalRequested and self.approvalStatus = #requested.'
});
bnode('ObtainCommercialApproval', 'subProcess', 'Obtain commercial approval', 'CommercialApprover', 1375, 515, {
  documentation: 'The approver assesses profitability and may approve, reject, or request revision. The returned state records self.profitabilityAssessmentCompleted and the final approvalStatus.'
});
bnode('ObtainFinancialClearance', 'subProcess', 'Obtain financial clearance if required', 'FinancialControl', 1575, 655, {
  documentation: 'Financial Control determines whether the deal-value threshold applies and performs the corresponding review when necessary.'
});
bnode('ObtainCreditClearance', 'subProcess', 'Obtain credit clearance if required', 'CreditControl', 1775, 795, {
  documentation: 'Credit Control determines whether a credit review is required and returns the applicable clearance result.'
});
bnode('AllApprovalsGranted', 'exclusiveGateway', 'All required approvals granted?', 'CustomerManager', 1980, 105);
bnode('RejectedEnd', 'endEvent', 'Proposal rejected', 'CustomerManager', 2085, 68, { endKind: 'terminate' });
bnode('SendApprovedProposal', 'sendTask', 'Send approved price proposal', 'CustomerManager', 2180, 95, {
  documentation: 'Pre-state requires the current revision to be approved. Resulting state: self.proposalSent and self.sentRevision = self.currentRevision.'
});
bnode('CompleteKit', 'task', 'Complete kit components', 'OrderOperations', 2380, 935);
bnode('CreateOrder', 'task', 'Create purchase order', 'OrderOperations', 2560, 935, {
  documentation: 'Resulting state: self.processCompleted.'
});
bnode('OfficialEnd', 'endEvent', 'Order created', 'OrderOperations', 2740, 952);

const flows = [];
function flow(id, source, target, name = '', condition = '', isDefault = false, waypoints) {
  flows.push({ id, source, target, name, condition, isDefault, waypoints });
}

flow('F01', 'OfficialStart', 'RecordCustomerDeal');
flow('F02', 'RecordCustomerDeal', 'PrepareTechnicalPackage');
flow('F03', 'PrepareTechnicalPackage', 'ValidateServiceTerms');
flow('F04', 'ValidateServiceTerms', 'SubmitApproval');
flow('F05', 'SubmitApproval', 'ObtainCommercialApproval');
flow('F06', 'ObtainCommercialApproval', 'ObtainFinancialClearance');
flow('F07', 'ObtainFinancialClearance', 'ObtainCreditClearance');
flow('F08', 'ObtainCreditClearance', 'AllApprovalsGranted');
flow('F09', 'AllApprovalsGranted', 'SendApprovedProposal', 'Yes', '${self.approvalStatus = #approved and self.approvedRevision = self.currentRevision and (not self.dealAmountOver250000NIS or self.financialControlApproved) and (not self.creditReviewRequired or self.creditControlApproved)}');
flow('F10', 'AllApprovalsGranted', 'RejectedEnd', 'No', '', true);
flow('F11', 'SendApprovedProposal', 'CompleteKit');
flow('F12', 'CompleteKit', 'CreateOrder');
flow('F13', 'CreateOrder', 'OfficialEnd');

const laneNotes = [
  ['CustomerManager', 'Customer Manager activities:\n• Record customer and deal\n• Submit proposal for approval\n• Check the final approval outcome\n• Send the approved price proposal', 'RecordCustomerDeal'],
  ['PresaleEngineer', 'Presale Engineer activity:\n• Prepare the required technical package (BoM/HLD)', 'PrepareTechnicalPackage'],
  ['ServiceAgreements', 'Service Agreements activity:\n• Validate service terms and coordinate corrections', 'ValidateServiceTerms'],
  ['CommercialApprover', 'Commercial Approver activity:\n• Assess profitability and return a commercial decision', 'ObtainCommercialApproval'],
  ['FinancialControl', 'Financial Control activity:\n• Provide financial clearance when the deal requires it', 'ObtainFinancialClearance'],
  ['CreditControl', 'Credit Control activity:\n• Provide credit clearance when the customer requires it', 'ObtainCreditClearance'],
  ['OrderOperations', 'Order Operations activities:\n• Complete kit components\n• Create the purchase order', 'CompleteKit']
].map(([lane, text, target], index) => ({
  id: `Note_${lane}`, lane, text, target, x: 100, y: 72 + index * 140, width: 335, height: 108
}));

const workaroundNodes = [
  { id: 'WorkaroundStart', type: 'startEvent', name: 'Urgent customer request', x: 500, y: 1170 },
  { id: 'PrepareOutsideIS', type: 'task', name: 'Prepare proposal outside IS', x: 590, y: 1153 },
  { id: 'SendUnapprovedProposal', type: 'sendTask', name: 'Send proposal before approval', x: 790, y: 1153 },
  { id: 'WorkaroundEnd', type: 'endEvent', name: 'Proposal sent', x: 990, y: 1170 }
];
const workaroundNote = {
  id: 'Note_WorkaroundCustomerManager',
  text: 'Customer Manager activities:\n• Prepare a proposal outside the official IS\n• Send it directly to the customer',
  x: 100, y: 1128, width: 335, height: 105
};
const workaroundFlows = [
  { id: 'W01', source: 'WorkaroundStart', target: 'PrepareOutsideIS' },
  { id: 'W02', source: 'PrepareOutsideIS', target: 'SendUnapprovedProposal' },
  { id: 'W03', source: 'SendUnapprovedProposal', target: 'WorkaroundEnd' }
];

const allNodes = [...nodes, ...workaroundNodes];
const nodeById = new Map(allNodes.map(n => [n.id, n]));
const allFlows = [...flows, ...workaroundFlows];
const incoming = id => allFlows.filter(f => f.target === id).map(f => f.id);
const outgoing = id => allFlows.filter(f => f.source === id).map(f => f.id);
const defaultByGateway = new Map(flows.filter(f => f.isDefault).map(f => [f.source, f.id]));

function nodeXml(n) {
  const attrs = [`id="${n.id}"`];
  if (n.name) attrs.push(`name="${esc(n.name)}"`);
  if (defaultByGateway.has(n.id)) attrs.push(`default="${defaultByGateway.get(n.id)}"`);
  const inside = [
    ...(n.documentation ? [`      <bpmn:documentation>${esc(n.documentation)}</bpmn:documentation>`] : []),
    ...incoming(n.id).map(id => `      <bpmn:incoming>${id}</bpmn:incoming>`),
    ...outgoing(n.id).map(id => `      <bpmn:outgoing>${id}</bpmn:outgoing>`)
  ];
  if (n.type === 'endEvent' && n.endKind === 'terminate') inside.push(`      <bpmn:terminateEventDefinition id="Terminate_${n.id}"/>`);
  return `    <bpmn:${n.type} ${attrs.join(' ')}>\n${inside.join('\n')}\n    </bpmn:${n.type}>`;
}

function noteXml(note) {
  return `    <bpmn:textAnnotation id="${note.id}">\n      <bpmn:text>${esc(note.text)}</bpmn:text>\n    </bpmn:textAnnotation>`;
}

function flowXml(f) {
  const attrs = [`id="${f.id}"`, `sourceRef="${f.source}"`, `targetRef="${f.target}"`];
  if (f.name) attrs.push(`name="${esc(f.name)}"`);
  if (!f.condition) return `    <bpmn:sequenceFlow ${attrs.join(' ')}/>`;
  return `    <bpmn:sequenceFlow ${attrs.join(' ')}>\n      <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression"><![CDATA[${f.condition}]]></bpmn:conditionExpression>\n    </bpmn:sequenceFlow>`;
}

function size(n) {
  if (n.type.includes('Gateway')) return [50, 50];
  if (n.type.includes('Event')) return [36, 36];
  return [125, 70];
}

function center(n) {
  const [w, h] = size(n);
  return [n.x + w / 2, n.y + h / 2];
}

function edgePoints(f) {
  if (f.waypoints) return f.waypoints;
  const s = nodeById.get(f.source), t = nodeById.get(f.target);
  const [sw, sh] = size(s), [tw, th] = size(t);
  const sc = center(s), tc = center(t);
  if (Math.abs(sc[1] - tc[1]) < 5) return [[s.x + sw, sc[1]], [t.x, tc[1]]];
  const sx = s.x + sw / 2;
  const tx = t.x + tw / 2;
  const midY = (sc[1] + tc[1]) / 2;
  return [[sx, s.y + sh], [sx, midY], [tx, midY], [tx, t.y]];
}

const laneHeight = 140;
const officialY = 60;
const officialHeight = laneHeight * lanes.length;
const participantWidth = 2850;

const laneXml = lanes.map(([id, name]) => {
  const refs = nodes.filter(n => n.lane === id).map(n => `        <bpmn:flowNodeRef>${n.id}</bpmn:flowNodeRef>`).join('\n');
  return `      <bpmn:lane id="Lane_${id}" name="${esc(name)}">\n${refs}\n      </bpmn:lane>`;
}).join('\n');

const officialProcess = `  <bpmn:process id="Process_Official" name="Prescribed Sales Forecast process" isExecutable="false">
    <bpmn:documentation>The official process prepares technical and service information, obtains commercial, financial and credit approval when required, sends the approved proposal, and creates an order.</bpmn:documentation>
    <bpmn:laneSet id="LaneSet_Official">
${laneXml}
    </bpmn:laneSet>
${nodes.map(nodeXml).join('\n')}
${flows.map(flowXml).join('\n')}
${laneNotes.map(noteXml).join('\n')}
  </bpmn:process>`;

const workaroundProcess = `  <bpmn:process id="Process_Workaround" name="Observed direct-send workaround" isExecutable="false">
    <bpmn:documentation>This is an observed deviation, not an allowed branch of the prescribed process. The Customer Manager prepares a proposal outside the information system and sends it before approval.</bpmn:documentation>
    <bpmn:laneSet id="LaneSet_Workaround">
      <bpmn:lane id="Lane_WorkaroundCustomerManager" name="Customer Manager">
${workaroundNodes.map(n => `        <bpmn:flowNodeRef>${n.id}</bpmn:flowNodeRef>`).join('\n')}
      </bpmn:lane>
    </bpmn:laneSet>
${workaroundNodes.map(nodeXml).join('\n')}
${workaroundFlows.map(flowXml).join('\n')}
${noteXml(workaroundNote)}
  </bpmn:process>`;

const shapeXml = [
  `      <bpmndi:BPMNShape id="Participant_Official_di" bpmnElement="Participant_Official" isHorizontal="true"><dc:Bounds x="40" y="${officialY}" width="${participantWidth}" height="${officialHeight}"/></bpmndi:BPMNShape>`,
  ...lanes.map(([id], index) => `      <bpmndi:BPMNShape id="Lane_${id}_di" bpmnElement="Lane_${id}" isHorizontal="true"><dc:Bounds x="70" y="${officialY + index * laneHeight}" width="${participantWidth - 30}" height="${laneHeight}"/></bpmndi:BPMNShape>`),
  ...laneNotes.map(note => `      <bpmndi:BPMNShape id="${note.id}_di" bpmnElement="${note.id}"><dc:Bounds x="${note.x}" y="${note.y}" width="${note.width}" height="${note.height}"/></bpmndi:BPMNShape>`),
  ...nodes.map(n => {
    const [w, h] = size(n);
    const expanded = n.type === 'subProcess' ? ' isExpanded="false"' : '';
    return `      <bpmndi:BPMNShape id="${n.id}_di" bpmnElement="${n.id}"${expanded}><dc:Bounds x="${n.x}" y="${n.y}" width="${w}" height="${h}"/></bpmndi:BPMNShape>`;
  }),
  `      <bpmndi:BPMNShape id="Participant_Workaround_di" bpmnElement="Participant_Workaround" isHorizontal="true"><dc:Bounds x="40" y="1100" width="1100" height="180"/></bpmndi:BPMNShape>`,
  `      <bpmndi:BPMNShape id="Lane_WorkaroundCustomerManager_di" bpmnElement="Lane_WorkaroundCustomerManager" isHorizontal="true"><dc:Bounds x="70" y="1100" width="1070" height="180"/></bpmndi:BPMNShape>`,
  `      <bpmndi:BPMNShape id="${workaroundNote.id}_di" bpmnElement="${workaroundNote.id}"><dc:Bounds x="${workaroundNote.x}" y="${workaroundNote.y}" width="${workaroundNote.width}" height="${workaroundNote.height}"/></bpmndi:BPMNShape>`,
  ...workaroundNodes.map(n => { const [w, h] = size(n); return `      <bpmndi:BPMNShape id="${n.id}_di" bpmnElement="${n.id}"><dc:Bounds x="${n.x}" y="${n.y}" width="${w}" height="${h}"/></bpmndi:BPMNShape>`; }),
  `      <bpmndi:BPMNShape id="Participant_Customer_di" bpmnElement="Participant_Customer" isHorizontal="true"><dc:Bounds x="1200" y="1100" width="650" height="180"/></bpmndi:BPMNShape>`
].join('\n');

const edgeXml = allFlows.map(f => `      <bpmndi:BPMNEdge id="${f.id}_di" bpmnElement="${f.id}">\n${edgePoints(f).map(([x,y]) => `        <di:waypoint x="${x}" y="${y}"/>`).join('\n')}\n      </bpmndi:BPMNEdge>`).join('\n');

const messageEdges = `      <bpmndi:BPMNEdge id="Message_OfficialToCustomer_di" bpmnElement="Message_OfficialToCustomer"><di:waypoint x="2243" y="165"/><di:waypoint x="2243" y="1070"/><di:waypoint x="1525" y="1070"/><di:waypoint x="1525" y="1100"/></bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge id="Message_WorkaroundToCustomer_di" bpmnElement="Message_WorkaroundToCustomer"><di:waypoint x="915" y="1188"/><di:waypoint x="1200" y="1188"/></bpmndi:BPMNEdge>`;

const bpmn = `<?xml version="1.0" encoding="UTF-8"?>
<bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Definitions_SalesForecastReadable" targetNamespace="http://bpmn.io/schema/bpmn">
  <bpmn:collaboration id="Collaboration_SalesForecast">
    <bpmn:participant id="Participant_Official" name="PRESCRIBED PROCESS" processRef="Process_Official"/>
    <bpmn:participant id="Participant_Workaround" name="OBSERVED WORKAROUND (not an official branch)" processRef="Process_Workaround"/>
    <bpmn:participant id="Participant_Customer" name="Customer"/>
    <bpmn:messageFlow id="Message_OfficialToCustomer" name="Approved proposal" sourceRef="SendApprovedProposal" targetRef="Participant_Customer"/>
    <bpmn:messageFlow id="Message_WorkaroundToCustomer" name="Unapproved proposal" sourceRef="SendUnapprovedProposal" targetRef="Participant_Customer"/>
  </bpmn:collaboration>
${officialProcess}
${workaroundProcess}
  <bpmndi:BPMNDiagram id="BPMNDiagram_SalesForecastReadable">
    <bpmndi:BPMNPlane id="BPMNPlane_SalesForecastReadable" bpmnElement="Collaboration_SalesForecast">
${shapeXml}
${edgeXml}
${messageEdges}
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</bpmn:definitions>
`;

fs.writeFileSync(new URL('sales_forecast_full_process.bpmn', here), bpmn);
