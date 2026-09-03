// Generated from Bpmn.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.bpmn.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BpmnParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, IDENT=41, STRING=42, STATE_CLAUSE=43, WS=44, LINE_COMMENT=45, 
		BLOCK_COMMENT=46;
	public static final int
		RULE_model = 0, RULE_pool = 1, RULE_laneDecl = 2, RULE_topElement = 3, 
		RULE_startDecl = 4, RULE_endDecl = 5, RULE_eventDecl = 6, RULE_activityDecl = 7, 
		RULE_activityTypeProperty = 8, RULE_activityType = 9, RULE_gatewayDecl = 10, 
		RULE_gatewayTypeProperty = 11, RULE_gatewayFlow = 12, RULE_gatewayFlowCondition = 13, 
		RULE_laneProperty = 14, RULE_flowProperty = 15, RULE_message = 16, RULE_messageFlow = 17, 
		RULE_nameProperty = 18, RULE_triggerProperty = 19, RULE_directionProperty = 20, 
		RULE_messageProperty = 21, RULE_preProperty = 22, RULE_postProperty = 23, 
		RULE_stateClause = 24, RULE_eventType = 25, RULE_eventDir = 26, RULE_gwType = 27;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "pool", "laneDecl", "topElement", "startDecl", "endDecl", "eventDecl", 
			"activityDecl", "activityTypeProperty", "activityType", "gatewayDecl", 
			"gatewayTypeProperty", "gatewayFlow", "gatewayFlowCondition", "laneProperty", 
			"flowProperty", "message", "messageFlow", "nameProperty", "triggerProperty", 
			"directionProperty", "messageProperty", "preProperty", "postProperty", 
			"stateClause", "eventType", "eventDir", "gwType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'model'", "'{'", "'}'", "'pool'", "'for'", "'lane'", "';'", "'start'", 
			"'end'", "'event'", "'activity'", "'type'", "'task'", "'call-activity'", 
			"'subprocess'", "'gateway'", "'flow'", "'post'", "'when'", "'default'", 
			"'message'", "'message-flow'", "'->'", "'name'", "'trigger'", "'direction'", 
			"'pre'", "'none'", "'timer'", "'error'", "'signal'", "'terminate'", "'compensation'", 
			"'conditional'", "'catching'", "'throwing'", "'xor'", "'and'", "'or'", 
			"'event-based'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "IDENT", "STRING", "STATE_CLAUSE", "WS", 
			"LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Bpmn.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public BpmnParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public TerminalNode EOF() { return getToken(BpmnParser.EOF, 0); }
		public List<PoolContext> pool() {
			return getRuleContexts(PoolContext.class);
		}
		public PoolContext pool(int i) {
			return getRuleContext(PoolContext.class,i);
		}
		public List<MessageContext> message() {
			return getRuleContexts(MessageContext.class);
		}
		public MessageContext message(int i) {
			return getRuleContext(MessageContext.class,i);
		}
		public List<MessageFlowContext> messageFlow() {
			return getRuleContexts(MessageFlowContext.class);
		}
		public MessageFlowContext messageFlow(int i) {
			return getRuleContext(MessageFlowContext.class,i);
		}
		public List<TopElementContext> topElement() {
			return getRuleContexts(TopElementContext.class);
		}
		public TopElementContext topElement(int i) {
			return getRuleContext(TopElementContext.class,i);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitModel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelContext model() throws RecognitionException {
		ModelContext _localctx = new ModelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_model);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(T__0);
			setState(57);
			match(IDENT);
			setState(58);
			match(T__1);
			setState(60); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(59);
				pool();
				}
				}
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__3 );
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__20) {
				{
				{
				setState(64);
				message();
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__21) {
				{
				{
				setState(70);
				messageFlow();
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__15))) != 0)) {
				{
				{
				setState(76);
				topElement();
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(82);
			match(T__2);
			setState(83);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PoolContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(BpmnParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(BpmnParser.IDENT, i);
		}
		public NamePropertyContext nameProperty() {
			return getRuleContext(NamePropertyContext.class,0);
		}
		public List<LaneDeclContext> laneDecl() {
			return getRuleContexts(LaneDeclContext.class);
		}
		public LaneDeclContext laneDecl(int i) {
			return getRuleContext(LaneDeclContext.class,i);
		}
		public PoolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pool; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterPool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitPool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitPool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PoolContext pool() throws RecognitionException {
		PoolContext _localctx = new PoolContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pool);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(T__3);
			setState(86);
			match(IDENT);
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(87);
				match(T__4);
				setState(88);
				match(IDENT);
				}
			}

			setState(91);
			match(T__1);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__23) {
				{
				setState(92);
				nameProperty();
				}
			}

			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(95);
				laneDecl();
				}
				}
				setState(100);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(101);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LaneDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LaneDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_laneDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterLaneDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitLaneDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitLaneDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaneDeclContext laneDecl() throws RecognitionException {
		LaneDeclContext _localctx = new LaneDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_laneDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(T__5);
			setState(104);
			match(IDENT);
			setState(105);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TopElementContext extends ParserRuleContext {
		public StartDeclContext startDecl() {
			return getRuleContext(StartDeclContext.class,0);
		}
		public EndDeclContext endDecl() {
			return getRuleContext(EndDeclContext.class,0);
		}
		public EventDeclContext eventDecl() {
			return getRuleContext(EventDeclContext.class,0);
		}
		public ActivityDeclContext activityDecl() {
			return getRuleContext(ActivityDeclContext.class,0);
		}
		public GatewayDeclContext gatewayDecl() {
			return getRuleContext(GatewayDeclContext.class,0);
		}
		public TopElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterTopElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitTopElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitTopElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopElementContext topElement() throws RecognitionException {
		TopElementContext _localctx = new TopElementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_topElement);
		try {
			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				startDecl();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(108);
				endDecl();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(109);
				eventDecl();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(110);
				activityDecl();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 5);
				{
				setState(111);
				gatewayDecl();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StartDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LanePropertyContext laneProperty() {
			return getRuleContext(LanePropertyContext.class,0);
		}
		public TriggerPropertyContext triggerProperty() {
			return getRuleContext(TriggerPropertyContext.class,0);
		}
		public FlowPropertyContext flowProperty() {
			return getRuleContext(FlowPropertyContext.class,0);
		}
		public PrePropertyContext preProperty() {
			return getRuleContext(PrePropertyContext.class,0);
		}
		public StartDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_startDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterStartDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitStartDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitStartDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartDeclContext startDecl() throws RecognitionException {
		StartDeclContext _localctx = new StartDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_startDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(T__7);
			setState(115);
			match(IDENT);
			setState(116);
			match(T__1);
			setState(117);
			laneProperty();
			setState(118);
			triggerProperty();
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(119);
				preProperty();
				}
			}

			setState(122);
			flowProperty();
			setState(123);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EndDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LanePropertyContext laneProperty() {
			return getRuleContext(LanePropertyContext.class,0);
		}
		public TriggerPropertyContext triggerProperty() {
			return getRuleContext(TriggerPropertyContext.class,0);
		}
		public EndDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterEndDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitEndDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitEndDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndDeclContext endDecl() throws RecognitionException {
		EndDeclContext _localctx = new EndDeclContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_endDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			match(T__8);
			setState(126);
			match(IDENT);
			setState(127);
			match(T__1);
			setState(128);
			laneProperty();
			setState(129);
			triggerProperty();
			setState(130);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EventDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LanePropertyContext laneProperty() {
			return getRuleContext(LanePropertyContext.class,0);
		}
		public TriggerPropertyContext triggerProperty() {
			return getRuleContext(TriggerPropertyContext.class,0);
		}
		public DirectionPropertyContext directionProperty() {
			return getRuleContext(DirectionPropertyContext.class,0);
		}
		public FlowPropertyContext flowProperty() {
			return getRuleContext(FlowPropertyContext.class,0);
		}
		public EventDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterEventDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitEventDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitEventDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventDeclContext eventDecl() throws RecognitionException {
		EventDeclContext _localctx = new EventDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_eventDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			match(T__9);
			setState(133);
			match(IDENT);
			setState(134);
			match(T__1);
			setState(135);
			laneProperty();
			setState(136);
			triggerProperty();
			setState(137);
			directionProperty();
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(138);
				flowProperty();
				}
			}

			setState(141);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActivityDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public ActivityTypePropertyContext activityTypeProperty() {
			return getRuleContext(ActivityTypePropertyContext.class,0);
		}
		public LanePropertyContext laneProperty() {
			return getRuleContext(LanePropertyContext.class,0);
		}
		public NamePropertyContext nameProperty() {
			return getRuleContext(NamePropertyContext.class,0);
		}
		public PrePropertyContext preProperty() {
			return getRuleContext(PrePropertyContext.class,0);
		}
		public PostPropertyContext postProperty() {
			return getRuleContext(PostPropertyContext.class,0);
		}
		public FlowPropertyContext flowProperty() {
			return getRuleContext(FlowPropertyContext.class,0);
		}
		public ActivityDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_activityDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterActivityDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitActivityDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitActivityDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActivityDeclContext activityDecl() throws RecognitionException {
		ActivityDeclContext _localctx = new ActivityDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_activityDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(T__10);
			setState(144);
			match(IDENT);
			setState(145);
			match(T__1);
			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__23) {
				{
				setState(146);
				nameProperty();
				}
			}

			setState(149);
			activityTypeProperty();
			setState(150);
			laneProperty();
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(151);
				preProperty();
				}
			}

			setState(155);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(154);
				postProperty();
				}
			}

			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(157);
				flowProperty();
				}
			}

			setState(160);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActivityTypePropertyContext extends ParserRuleContext {
		public ActivityTypeContext activityType() {
			return getRuleContext(ActivityTypeContext.class,0);
		}
		public ActivityTypePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_activityTypeProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterActivityTypeProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitActivityTypeProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitActivityTypeProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActivityTypePropertyContext activityTypeProperty() throws RecognitionException {
		ActivityTypePropertyContext _localctx = new ActivityTypePropertyContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_activityTypeProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			match(T__11);
			setState(163);
			activityType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActivityTypeContext extends ParserRuleContext {
		public ActivityTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_activityType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterActivityType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitActivityType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitActivityType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActivityTypeContext activityType() throws RecognitionException {
		ActivityTypeContext _localctx = new ActivityTypeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_activityType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__12) | (1L << T__13) | (1L << T__14))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GatewayDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LanePropertyContext laneProperty() {
			return getRuleContext(LanePropertyContext.class,0);
		}
		public GatewayTypePropertyContext gatewayTypeProperty() {
			return getRuleContext(GatewayTypePropertyContext.class,0);
		}
		public PrePropertyContext preProperty() {
			return getRuleContext(PrePropertyContext.class,0);
		}
		public List<GatewayFlowContext> gatewayFlow() {
			return getRuleContexts(GatewayFlowContext.class);
		}
		public GatewayFlowContext gatewayFlow(int i) {
			return getRuleContext(GatewayFlowContext.class,i);
		}
		public GatewayDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gatewayDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterGatewayDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitGatewayDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitGatewayDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayDeclContext gatewayDecl() throws RecognitionException {
		GatewayDeclContext _localctx = new GatewayDeclContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_gatewayDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(T__15);
			setState(168);
			match(IDENT);
			setState(169);
			match(T__1);
			setState(170);
			laneProperty();
			setState(171);
			gatewayTypeProperty();
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(172);
				preProperty();
				}
			}

			setState(176); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(175);
				gatewayFlow();
				}
				}
				setState(178); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__16 );
			setState(180);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GatewayTypePropertyContext extends ParserRuleContext {
		public GwTypeContext gwType() {
			return getRuleContext(GwTypeContext.class,0);
		}
		public GatewayTypePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gatewayTypeProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterGatewayTypeProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitGatewayTypeProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitGatewayTypeProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayTypePropertyContext gatewayTypeProperty() throws RecognitionException {
		GatewayTypePropertyContext _localctx = new GatewayTypePropertyContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_gatewayTypeProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(T__11);
			setState(183);
			gwType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GatewayFlowContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public GatewayFlowConditionContext gatewayFlowCondition() {
			return getRuleContext(GatewayFlowConditionContext.class,0);
		}
		public GatewayFlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gatewayFlow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterGatewayFlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitGatewayFlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitGatewayFlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayFlowContext gatewayFlow() throws RecognitionException {
		GatewayFlowContext _localctx = new GatewayFlowContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_gatewayFlow);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			match(T__16);
			setState(186);
			match(IDENT);
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__18) | (1L << T__19))) != 0)) {
				{
				setState(187);
				gatewayFlowCondition();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GatewayFlowConditionContext extends ParserRuleContext {
		public StateClauseContext stateClause() {
			return getRuleContext(StateClauseContext.class,0);
		}
		public GatewayFlowConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gatewayFlowCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterGatewayFlowCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitGatewayFlowCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitGatewayFlowCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayFlowConditionContext gatewayFlowCondition() throws RecognitionException {
		GatewayFlowConditionContext _localctx = new GatewayFlowConditionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_gatewayFlowCondition);
		try {
			setState(195);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				match(T__17);
				setState(191);
				stateClause();
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 2);
				{
				setState(192);
				match(T__18);
				setState(193);
				stateClause();
				}
				break;
			case T__19:
				enterOuterAlt(_localctx, 3);
				{
				setState(194);
				match(T__19);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LanePropertyContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public LanePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_laneProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterLaneProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitLaneProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitLaneProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LanePropertyContext laneProperty() throws RecognitionException {
		LanePropertyContext _localctx = new LanePropertyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_laneProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(197);
			match(T__5);
			setState(198);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FlowPropertyContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public FlowPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flowProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterFlowProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitFlowProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitFlowProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlowPropertyContext flowProperty() throws RecognitionException {
		FlowPropertyContext _localctx = new FlowPropertyContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_flowProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(T__16);
			setState(201);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessageContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public NamePropertyContext nameProperty() {
			return getRuleContext(NamePropertyContext.class,0);
		}
		public MessageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterMessage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitMessage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitMessage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageContext message() throws RecognitionException {
		MessageContext _localctx = new MessageContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_message);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(203);
			match(T__20);
			setState(204);
			match(IDENT);
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(205);
				match(T__1);
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__23) {
					{
					setState(206);
					nameProperty();
					}
				}

				setState(209);
				match(T__2);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessageFlowContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(BpmnParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(BpmnParser.IDENT, i);
		}
		public MessagePropertyContext messageProperty() {
			return getRuleContext(MessagePropertyContext.class,0);
		}
		public MessageFlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageFlow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterMessageFlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitMessageFlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitMessageFlow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessageFlowContext messageFlow() throws RecognitionException {
		MessageFlowContext _localctx = new MessageFlowContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_messageFlow);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
			match(T__21);
			setState(213);
			match(IDENT);
			setState(214);
			match(T__22);
			setState(215);
			match(IDENT);
			setState(221);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(216);
				match(T__1);
				setState(218);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__20) {
					{
					setState(217);
					messageProperty();
					}
				}

				setState(220);
				match(T__2);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NamePropertyContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(BpmnParser.STRING, 0); }
		public NamePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterNameProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitNameProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitNameProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamePropertyContext nameProperty() throws RecognitionException {
		NamePropertyContext _localctx = new NamePropertyContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_nameProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			match(T__23);
			setState(224);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TriggerPropertyContext extends ParserRuleContext {
		public EventTypeContext eventType() {
			return getRuleContext(EventTypeContext.class,0);
		}
		public TriggerPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_triggerProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterTriggerProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitTriggerProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitTriggerProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TriggerPropertyContext triggerProperty() throws RecognitionException {
		TriggerPropertyContext _localctx = new TriggerPropertyContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_triggerProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(T__24);
			setState(227);
			eventType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DirectionPropertyContext extends ParserRuleContext {
		public EventDirContext eventDir() {
			return getRuleContext(EventDirContext.class,0);
		}
		public DirectionPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directionProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterDirectionProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitDirectionProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitDirectionProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectionPropertyContext directionProperty() throws RecognitionException {
		DirectionPropertyContext _localctx = new DirectionPropertyContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_directionProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			match(T__25);
			setState(230);
			eventDir();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MessagePropertyContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(BpmnParser.IDENT, 0); }
		public MessagePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterMessageProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitMessageProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitMessageProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessagePropertyContext messageProperty() throws RecognitionException {
		MessagePropertyContext _localctx = new MessagePropertyContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_messageProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			match(T__20);
			setState(233);
			match(IDENT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrePropertyContext extends ParserRuleContext {
		public StateClauseContext stateClause() {
			return getRuleContext(StateClauseContext.class,0);
		}
		public PrePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_preProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterPreProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitPreProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitPreProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrePropertyContext preProperty() throws RecognitionException {
		PrePropertyContext _localctx = new PrePropertyContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_preProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(T__26);
			setState(236);
			stateClause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PostPropertyContext extends ParserRuleContext {
		public StateClauseContext stateClause() {
			return getRuleContext(StateClauseContext.class,0);
		}
		public PostPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_postProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterPostProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitPostProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitPostProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PostPropertyContext postProperty() throws RecognitionException {
		PostPropertyContext _localctx = new PostPropertyContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_postProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			match(T__17);
			setState(239);
			stateClause();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StateClauseContext extends ParserRuleContext {
		public TerminalNode STATE_CLAUSE() { return getToken(BpmnParser.STATE_CLAUSE, 0); }
		public StateClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterStateClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitStateClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitStateClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateClauseContext stateClause() throws RecognitionException {
		StateClauseContext _localctx = new StateClauseContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_stateClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			match(STATE_CLAUSE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EventTypeContext extends ParserRuleContext {
		public EventTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterEventType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitEventType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitEventType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventTypeContext eventType() throws RecognitionException {
		EventTypeContext _localctx = new EventTypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_eventType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__20) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EventDirContext extends ParserRuleContext {
		public EventDirContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventDir; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterEventDir(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitEventDir(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitEventDir(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventDirContext eventDir() throws RecognitionException {
		EventDirContext _localctx = new EventDirContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_eventDir);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			_la = _input.LA(1);
			if ( !(_la==T__34 || _la==T__35) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GwTypeContext extends ParserRuleContext {
		public GwTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gwType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).enterGwType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof BpmnListener ) ((BpmnListener)listener).exitGwType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof BpmnVisitor ) return ((BpmnVisitor<? extends T>)visitor).visitGwType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GwTypeContext gwType() throws RecognitionException {
		GwTypeContext _localctx = new GwTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_gwType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\60\u00fc\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\3\2\6\2?\n\2\r\2"+
		"\16\2@\3\2\7\2D\n\2\f\2\16\2G\13\2\3\2\7\2J\n\2\f\2\16\2M\13\2\3\2\7\2"+
		"P\n\2\f\2\16\2S\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3\\\n\3\3\3\3\3\5\3"+
		"`\n\3\3\3\7\3c\n\3\f\3\16\3f\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\3\5\5\5s\n\5\3\6\3\6\3\6\3\6\3\6\3\6\5\6{\n\6\3\6\3\6\3\6\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u008e\n\b\3\b\3\b"+
		"\3\t\3\t\3\t\3\t\5\t\u0096\n\t\3\t\3\t\3\t\5\t\u009b\n\t\3\t\5\t\u009e"+
		"\n\t\3\t\5\t\u00a1\n\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\f\5\f\u00b0\n\f\3\f\6\f\u00b3\n\f\r\f\16\f\u00b4\3\f\3\f\3\r\3\r"+
		"\3\r\3\16\3\16\3\16\5\16\u00bf\n\16\3\17\3\17\3\17\3\17\3\17\5\17\u00c6"+
		"\n\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\3\22\5\22\u00d2\n\22"+
		"\3\22\5\22\u00d5\n\22\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00dd\n\23\3"+
		"\23\5\23\u00e0\n\23\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27"+
		"\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34"+
		"\3\35\3\35\3\35\2\2\36\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,"+
		".\60\62\64\668\2\6\3\2\17\21\4\2\27\27\36$\3\2%&\3\2\'*\2\u00f9\2:\3\2"+
		"\2\2\4W\3\2\2\2\6i\3\2\2\2\br\3\2\2\2\nt\3\2\2\2\f\177\3\2\2\2\16\u0086"+
		"\3\2\2\2\20\u0091\3\2\2\2\22\u00a4\3\2\2\2\24\u00a7\3\2\2\2\26\u00a9\3"+
		"\2\2\2\30\u00b8\3\2\2\2\32\u00bb\3\2\2\2\34\u00c5\3\2\2\2\36\u00c7\3\2"+
		"\2\2 \u00ca\3\2\2\2\"\u00cd\3\2\2\2$\u00d6\3\2\2\2&\u00e1\3\2\2\2(\u00e4"+
		"\3\2\2\2*\u00e7\3\2\2\2,\u00ea\3\2\2\2.\u00ed\3\2\2\2\60\u00f0\3\2\2\2"+
		"\62\u00f3\3\2\2\2\64\u00f5\3\2\2\2\66\u00f7\3\2\2\28\u00f9\3\2\2\2:;\7"+
		"\3\2\2;<\7+\2\2<>\7\4\2\2=?\5\4\3\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3"+
		"\2\2\2AE\3\2\2\2BD\5\"\22\2CB\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FK"+
		"\3\2\2\2GE\3\2\2\2HJ\5$\23\2IH\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2L"+
		"Q\3\2\2\2MK\3\2\2\2NP\5\b\5\2ON\3\2\2\2PS\3\2\2\2QO\3\2\2\2QR\3\2\2\2"+
		"RT\3\2\2\2SQ\3\2\2\2TU\7\5\2\2UV\7\2\2\3V\3\3\2\2\2WX\7\6\2\2X[\7+\2\2"+
		"YZ\7\7\2\2Z\\\7+\2\2[Y\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]_\7\4\2\2^`\5&\24"+
		"\2_^\3\2\2\2_`\3\2\2\2`d\3\2\2\2ac\5\6\4\2ba\3\2\2\2cf\3\2\2\2db\3\2\2"+
		"\2de\3\2\2\2eg\3\2\2\2fd\3\2\2\2gh\7\5\2\2h\5\3\2\2\2ij\7\b\2\2jk\7+\2"+
		"\2kl\7\t\2\2l\7\3\2\2\2ms\5\n\6\2ns\5\f\7\2os\5\16\b\2ps\5\20\t\2qs\5"+
		"\26\f\2rm\3\2\2\2rn\3\2\2\2ro\3\2\2\2rp\3\2\2\2rq\3\2\2\2s\t\3\2\2\2t"+
		"u\7\n\2\2uv\7+\2\2vw\7\4\2\2wx\5\36\20\2xz\5(\25\2y{\5.\30\2zy\3\2\2\2"+
		"z{\3\2\2\2{|\3\2\2\2|}\5 \21\2}~\7\5\2\2~\13\3\2\2\2\177\u0080\7\13\2"+
		"\2\u0080\u0081\7+\2\2\u0081\u0082\7\4\2\2\u0082\u0083\5\36\20\2\u0083"+
		"\u0084\5(\25\2\u0084\u0085\7\5\2\2\u0085\r\3\2\2\2\u0086\u0087\7\f\2\2"+
		"\u0087\u0088\7+\2\2\u0088\u0089\7\4\2\2\u0089\u008a\5\36\20\2\u008a\u008b"+
		"\5(\25\2\u008b\u008d\5*\26\2\u008c\u008e\5 \21\2\u008d\u008c\3\2\2\2\u008d"+
		"\u008e\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0090\7\5\2\2\u0090\17\3\2\2"+
		"\2\u0091\u0092\7\r\2\2\u0092\u0093\7+\2\2\u0093\u0095\7\4\2\2\u0094\u0096"+
		"\5&\24\2\u0095\u0094\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0097\3\2\2\2\u0097"+
		"\u0098\5\22\n\2\u0098\u009a\5\36\20\2\u0099\u009b\5.\30\2\u009a\u0099"+
		"\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009e\5\60\31\2"+
		"\u009d\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u00a0\3\2\2\2\u009f\u00a1"+
		"\5 \21\2\u00a0\u009f\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\u00a3\7\5\2\2\u00a3\21\3\2\2\2\u00a4\u00a5\7\16\2\2\u00a5\u00a6\5\24"+
		"\13\2\u00a6\23\3\2\2\2\u00a7\u00a8\t\2\2\2\u00a8\25\3\2\2\2\u00a9\u00aa"+
		"\7\22\2\2\u00aa\u00ab\7+\2\2\u00ab\u00ac\7\4\2\2\u00ac\u00ad\5\36\20\2"+
		"\u00ad\u00af\5\30\r\2\u00ae\u00b0\5.\30\2\u00af\u00ae\3\2\2\2\u00af\u00b0"+
		"\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1\u00b3\5\32\16\2\u00b2\u00b1\3\2\2\2"+
		"\u00b3\u00b4\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b6"+
		"\3\2\2\2\u00b6\u00b7\7\5\2\2\u00b7\27\3\2\2\2\u00b8\u00b9\7\16\2\2\u00b9"+
		"\u00ba\58\35\2\u00ba\31\3\2\2\2\u00bb\u00bc\7\23\2\2\u00bc\u00be\7+\2"+
		"\2\u00bd\u00bf\5\34\17\2\u00be\u00bd\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		"\33\3\2\2\2\u00c0\u00c1\7\24\2\2\u00c1\u00c6\5\62\32\2\u00c2\u00c3\7\25"+
		"\2\2\u00c3\u00c6\5\62\32\2\u00c4\u00c6\7\26\2\2\u00c5\u00c0\3\2\2\2\u00c5"+
		"\u00c2\3\2\2\2\u00c5\u00c4\3\2\2\2\u00c6\35\3\2\2\2\u00c7\u00c8\7\b\2"+
		"\2\u00c8\u00c9\7+\2\2\u00c9\37\3\2\2\2\u00ca\u00cb\7\23\2\2\u00cb\u00cc"+
		"\7+\2\2\u00cc!\3\2\2\2\u00cd\u00ce\7\27\2\2\u00ce\u00d4\7+\2\2\u00cf\u00d1"+
		"\7\4\2\2\u00d0\u00d2\5&\24\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2"+
		"\u00d3\3\2\2\2\u00d3\u00d5\7\5\2\2\u00d4\u00cf\3\2\2\2\u00d4\u00d5\3\2"+
		"\2\2\u00d5#\3\2\2\2\u00d6\u00d7\7\30\2\2\u00d7\u00d8\7+\2\2\u00d8\u00d9"+
		"\7\31\2\2\u00d9\u00df\7+\2\2\u00da\u00dc\7\4\2\2\u00db\u00dd\5,\27\2\u00dc"+
		"\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00e0\7\5"+
		"\2\2\u00df\u00da\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0%\3\2\2\2\u00e1\u00e2"+
		"\7\32\2\2\u00e2\u00e3\7,\2\2\u00e3\'\3\2\2\2\u00e4\u00e5\7\33\2\2\u00e5"+
		"\u00e6\5\64\33\2\u00e6)\3\2\2\2\u00e7\u00e8\7\34\2\2\u00e8\u00e9\5\66"+
		"\34\2\u00e9+\3\2\2\2\u00ea\u00eb\7\27\2\2\u00eb\u00ec\7+\2\2\u00ec-\3"+
		"\2\2\2\u00ed\u00ee\7\35\2\2\u00ee\u00ef\5\62\32\2\u00ef/\3\2\2\2\u00f0"+
		"\u00f1\7\24\2\2\u00f1\u00f2\5\62\32\2\u00f2\61\3\2\2\2\u00f3\u00f4\7-"+
		"\2\2\u00f4\63\3\2\2\2\u00f5\u00f6\t\3\2\2\u00f6\65\3\2\2\2\u00f7\u00f8"+
		"\t\4\2\2\u00f8\67\3\2\2\2\u00f9\u00fa\t\5\2\2\u00fa9\3\2\2\2\30@EKQ[_"+
		"drz\u008d\u0095\u009a\u009d\u00a0\u00af\u00b4\u00be\u00c5\u00d1\u00d4"+
		"\u00dc\u00df";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}