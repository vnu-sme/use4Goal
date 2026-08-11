// Generated from Bpmn2.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Bpmn2Parser extends Parser {
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
		T__38=39, T__39=40, T__40=41, IDENT=42, STRING=43, STATE_CLAUSE=44, WS=45, 
		LINE_COMMENT=46, BLOCK_COMMENT=47;
	public static final int
		RULE_model = 0, RULE_pool = 1, RULE_laneDecl = 2, RULE_topElement = 3, 
		RULE_eventDecl = 4, RULE_eventTypeProperty = 5, RULE_eventKind = 6, RULE_activityDecl = 7, 
		RULE_activityTypeProperty = 8, RULE_activityType = 9, RULE_gatewayDecl = 10, 
		RULE_gatewayTypeProperty = 11, RULE_gatewayFlow = 12, RULE_gatewayFlowCondition = 13, 
		RULE_laneProperty = 14, RULE_flowProperty = 15, RULE_message = 16, RULE_messageFlow = 17, 
		RULE_nameProperty = 18, RULE_triggerProperty = 19, RULE_directionProperty = 20, 
		RULE_messageProperty = 21, RULE_preProperty = 22, RULE_postProperty = 23, 
		RULE_effectProperty = 24, RULE_stateClause = 25, RULE_eventType = 26, 
		RULE_eventDir = 27, RULE_gwType = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "pool", "laneDecl", "topElement", "eventDecl", "eventTypeProperty", 
			"eventKind", "activityDecl", "activityTypeProperty", "activityType", 
			"gatewayDecl", "gatewayTypeProperty", "gatewayFlow", "gatewayFlowCondition", 
			"laneProperty", "flowProperty", "message", "messageFlow", "nameProperty", 
			"triggerProperty", "directionProperty", "messageProperty", "preProperty", 
			"postProperty", "effectProperty", "stateClause", "eventType", "eventDir", 
			"gwType"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'model'", "'{'", "'}'", "'pool'", "'lane'", "';'", "'event'", 
			"'type'", "'start'", "'end'", "'intermediate'", "'activity'", "'task'", 
			"'call-activity'", "'subprocess'", "'gateway'", "'flow'", "'when'", "'default'", 
			"'message'", "'message-flow'", "'->'", "'name'", "'trigger'", "'direction'", 
			"'pre'", "'post'", "'effect'", "'none'", "'timer'", "'error'", "'signal'", 
			"'terminate'", "'compensation'", "'conditional'", "'catching'", "'throwing'", 
			"'xor'", "'and'", "'or'", "'event-based'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "IDENT", "STRING", "STATE_CLAUSE", 
			"WS", "LINE_COMMENT", "BLOCK_COMMENT"
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
	public String getGrammarFileName() { return "Bpmn2.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Bpmn2Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public TerminalNode EOF() { return getToken(Bpmn2Parser.EOF, 0); }
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitModel(this);
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
			setState(58);
			match(T__0);
			setState(59);
			match(IDENT);
			setState(60);
			match(T__1);
			setState(62); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(61);
				pool();
				}
				}
				setState(64); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__3 );
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__19) {
				{
				{
				setState(66);
				message();
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__20) {
				{
				{
				setState(72);
				messageFlow();
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__6) | (1L << T__11) | (1L << T__15))) != 0)) {
				{
				{
				setState(78);
				topElement();
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(84);
			match(T__2);
			setState(85);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterPool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitPool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitPool(this);
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
			setState(87);
			match(T__3);
			setState(88);
			match(IDENT);
			setState(89);
			match(T__1);
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(90);
				nameProperty();
				}
			}

			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(93);
				laneDecl();
				}
				}
				setState(98);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(99);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public LaneDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_laneDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterLaneDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitLaneDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitLaneDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaneDeclContext laneDecl() throws RecognitionException {
		LaneDeclContext _localctx = new LaneDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_laneDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__4);
			setState(102);
			match(IDENT);
			setState(103);
			match(T__5);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterTopElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitTopElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitTopElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopElementContext topElement() throws RecognitionException {
		TopElementContext _localctx = new TopElementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_topElement);
		try {
			setState(108);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				eventDecl();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				activityDecl();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
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

	public static class EventDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public EventTypePropertyContext eventTypeProperty() {
			return getRuleContext(EventTypePropertyContext.class,0);
		}
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEventDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEventDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEventDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventDeclContext eventDecl() throws RecognitionException {
		EventDeclContext _localctx = new EventDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_eventDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(T__6);
			setState(111);
			match(IDENT);
			setState(112);
			match(T__1);
			setState(113);
			eventTypeProperty();
			setState(114);
			laneProperty();
			setState(115);
			triggerProperty();
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__24) {
				{
				setState(116);
				directionProperty();
				}
			}

			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(119);
				flowProperty();
				}
			}

			setState(122);
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

	public static class EventTypePropertyContext extends ParserRuleContext {
		public EventKindContext eventKind() {
			return getRuleContext(EventKindContext.class,0);
		}
		public EventTypePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventTypeProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEventTypeProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEventTypeProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEventTypeProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventTypePropertyContext eventTypeProperty() throws RecognitionException {
		EventTypePropertyContext _localctx = new EventTypePropertyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_eventTypeProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			match(T__7);
			setState(125);
			eventKind();
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

	public static class EventKindContext extends ParserRuleContext {
		public EventKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eventKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEventKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEventKind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEventKind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventKindContext eventKind() throws RecognitionException {
		EventKindContext _localctx = new EventKindContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_eventKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__10))) != 0)) ) {
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

	public static class ActivityDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
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
		public EffectPropertyContext effectProperty() {
			return getRuleContext(EffectPropertyContext.class,0);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterActivityDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitActivityDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitActivityDecl(this);
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
			setState(129);
			match(T__11);
			setState(130);
			match(IDENT);
			setState(131);
			match(T__1);
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__22) {
				{
				setState(132);
				nameProperty();
				}
			}

			setState(135);
			activityTypeProperty();
			setState(136);
			laneProperty();
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__25) {
				{
				setState(137);
				preProperty();
				}
			}

			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__27) {
				{
				setState(140);
				effectProperty();
				}
			}

			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(143);
				postProperty();
				}
			}

			setState(147);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(146);
				flowProperty();
				}
			}

			setState(149);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterActivityTypeProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitActivityTypeProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitActivityTypeProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActivityTypePropertyContext activityTypeProperty() throws RecognitionException {
		ActivityTypePropertyContext _localctx = new ActivityTypePropertyContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_activityTypeProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(T__7);
			setState(152);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterActivityType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitActivityType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitActivityType(this);
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
			setState(154);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterGatewayDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitGatewayDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitGatewayDecl(this);
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
			setState(156);
			match(T__15);
			setState(157);
			match(IDENT);
			setState(158);
			match(T__1);
			setState(159);
			laneProperty();
			setState(160);
			gatewayTypeProperty();
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__25) {
				{
				setState(161);
				preProperty();
				}
			}

			setState(165); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(164);
				gatewayFlow();
				}
				}
				setState(167); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__16 );
			setState(169);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterGatewayTypeProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitGatewayTypeProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitGatewayTypeProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayTypePropertyContext gatewayTypeProperty() throws RecognitionException {
		GatewayTypePropertyContext _localctx = new GatewayTypePropertyContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_gatewayTypeProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			match(T__7);
			setState(172);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public GatewayFlowConditionContext gatewayFlowCondition() {
			return getRuleContext(GatewayFlowConditionContext.class,0);
		}
		public GatewayFlowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gatewayFlow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterGatewayFlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitGatewayFlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitGatewayFlow(this);
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
			setState(174);
			match(T__16);
			setState(175);
			match(IDENT);
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17 || _la==T__18) {
				{
				setState(176);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterGatewayFlowCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitGatewayFlowCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitGatewayFlowCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GatewayFlowConditionContext gatewayFlowCondition() throws RecognitionException {
		GatewayFlowConditionContext _localctx = new GatewayFlowConditionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_gatewayFlowCondition);
		try {
			setState(182);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(179);
				match(T__17);
				setState(180);
				stateClause();
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 2);
				{
				setState(181);
				match(T__18);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public LanePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_laneProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterLaneProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitLaneProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitLaneProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LanePropertyContext laneProperty() throws RecognitionException {
		LanePropertyContext _localctx = new LanePropertyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_laneProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184);
			match(T__4);
			setState(185);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public FlowPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_flowProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterFlowProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitFlowProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitFlowProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FlowPropertyContext flowProperty() throws RecognitionException {
		FlowPropertyContext _localctx = new FlowPropertyContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_flowProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			match(T__16);
			setState(188);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public NamePropertyContext nameProperty() {
			return getRuleContext(NamePropertyContext.class,0);
		}
		public MessageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_message; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterMessage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitMessage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitMessage(this);
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
			setState(190);
			match(T__19);
			setState(191);
			match(IDENT);
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(192);
				match(T__1);
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__22) {
					{
					setState(193);
					nameProperty();
					}
				}

				setState(196);
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
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2Parser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2Parser.IDENT, i);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterMessageFlow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitMessageFlow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitMessageFlow(this);
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
			setState(199);
			match(T__20);
			setState(200);
			match(IDENT);
			setState(201);
			match(T__21);
			setState(202);
			match(IDENT);
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(203);
				match(T__1);
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__19) {
					{
					setState(204);
					messageProperty();
					}
				}

				setState(207);
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
		public TerminalNode STRING() { return getToken(Bpmn2Parser.STRING, 0); }
		public NamePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterNameProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitNameProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitNameProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NamePropertyContext nameProperty() throws RecognitionException {
		NamePropertyContext _localctx = new NamePropertyContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_nameProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(T__22);
			setState(211);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterTriggerProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitTriggerProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitTriggerProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TriggerPropertyContext triggerProperty() throws RecognitionException {
		TriggerPropertyContext _localctx = new TriggerPropertyContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_triggerProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(T__23);
			setState(214);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterDirectionProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitDirectionProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitDirectionProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectionPropertyContext directionProperty() throws RecognitionException {
		DirectionPropertyContext _localctx = new DirectionPropertyContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_directionProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			match(T__24);
			setState(217);
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
		public TerminalNode IDENT() { return getToken(Bpmn2Parser.IDENT, 0); }
		public MessagePropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_messageProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterMessageProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitMessageProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitMessageProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MessagePropertyContext messageProperty() throws RecognitionException {
		MessagePropertyContext _localctx = new MessagePropertyContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_messageProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(T__19);
			setState(220);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterPreProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitPreProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitPreProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrePropertyContext preProperty() throws RecognitionException {
		PrePropertyContext _localctx = new PrePropertyContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_preProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(T__25);
			setState(223);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterPostProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitPostProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitPostProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PostPropertyContext postProperty() throws RecognitionException {
		PostPropertyContext _localctx = new PostPropertyContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_postProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			match(T__26);
			setState(226);
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

	public static class EffectPropertyContext extends ParserRuleContext {
		public StateClauseContext stateClause() {
			return getRuleContext(StateClauseContext.class,0);
		}
		public EffectPropertyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_effectProperty; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEffectProperty(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEffectProperty(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEffectProperty(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EffectPropertyContext effectProperty() throws RecognitionException {
		EffectPropertyContext _localctx = new EffectPropertyContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_effectProperty);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(T__27);
			setState(229);
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
		public TerminalNode STATE_CLAUSE() { return getToken(Bpmn2Parser.STATE_CLAUSE, 0); }
		public StateClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stateClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterStateClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitStateClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitStateClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StateClauseContext stateClause() throws RecognitionException {
		StateClauseContext _localctx = new StateClauseContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_stateClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEventType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEventType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEventType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventTypeContext eventType() throws RecognitionException {
		EventTypeContext _localctx = new EventTypeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_eventType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__19) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33) | (1L << T__34))) != 0)) ) {
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterEventDir(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitEventDir(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitEventDir(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EventDirContext eventDir() throws RecognitionException {
		EventDirContext _localctx = new EventDirContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_eventDir);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_la = _input.LA(1);
			if ( !(_la==T__35 || _la==T__36) ) {
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
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).enterGwType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2Listener ) ((Bpmn2Listener)listener).exitGwType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2Visitor ) return ((Bpmn2Visitor<? extends T>)visitor).visitGwType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GwTypeContext gwType() throws RecognitionException {
		GwTypeContext _localctx = new GwTypeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_gwType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40))) != 0)) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\61\u00f2\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\2\3\2\6\2"+
		"A\n\2\r\2\16\2B\3\2\7\2F\n\2\f\2\16\2I\13\2\3\2\7\2L\n\2\f\2\16\2O\13"+
		"\2\3\2\7\2R\n\2\f\2\16\2U\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3^\n\3\3"+
		"\3\7\3a\n\3\f\3\16\3d\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\5\5o\n"+
		"\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6x\n\6\3\6\5\6{\n\6\3\6\3\6\3\7\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\t\3\t\5\t\u0088\n\t\3\t\3\t\3\t\5\t\u008d\n\t\3\t"+
		"\5\t\u0090\n\t\3\t\5\t\u0093\n\t\3\t\5\t\u0096\n\t\3\t\3\t\3\n\3\n\3\n"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\5\f\u00a5\n\f\3\f\6\f\u00a8\n\f\r\f"+
		"\16\f\u00a9\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\5\16\u00b4\n\16\3\17\3"+
		"\17\3\17\5\17\u00b9\n\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22"+
		"\3\22\5\22\u00c5\n\22\3\22\5\22\u00c8\n\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\5\23\u00d0\n\23\3\23\5\23\u00d3\n\23\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\2\2\37\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:\2\7\3\2\13\r\3\2\17"+
		"\21\4\2\26\26\37%\3\2&\'\3\2(+\2\u00eb\2<\3\2\2\2\4Y\3\2\2\2\6g\3\2\2"+
		"\2\bn\3\2\2\2\np\3\2\2\2\f~\3\2\2\2\16\u0081\3\2\2\2\20\u0083\3\2\2\2"+
		"\22\u0099\3\2\2\2\24\u009c\3\2\2\2\26\u009e\3\2\2\2\30\u00ad\3\2\2\2\32"+
		"\u00b0\3\2\2\2\34\u00b8\3\2\2\2\36\u00ba\3\2\2\2 \u00bd\3\2\2\2\"\u00c0"+
		"\3\2\2\2$\u00c9\3\2\2\2&\u00d4\3\2\2\2(\u00d7\3\2\2\2*\u00da\3\2\2\2,"+
		"\u00dd\3\2\2\2.\u00e0\3\2\2\2\60\u00e3\3\2\2\2\62\u00e6\3\2\2\2\64\u00e9"+
		"\3\2\2\2\66\u00eb\3\2\2\28\u00ed\3\2\2\2:\u00ef\3\2\2\2<=\7\3\2\2=>\7"+
		",\2\2>@\7\4\2\2?A\5\4\3\2@?\3\2\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CG\3"+
		"\2\2\2DF\5\"\22\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HM\3\2\2\2IG"+
		"\3\2\2\2JL\5$\23\2KJ\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NS\3\2\2\2O"+
		"M\3\2\2\2PR\5\b\5\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TV\3\2\2\2"+
		"US\3\2\2\2VW\7\5\2\2WX\7\2\2\3X\3\3\2\2\2YZ\7\6\2\2Z[\7,\2\2[]\7\4\2\2"+
		"\\^\5&\24\2]\\\3\2\2\2]^\3\2\2\2^b\3\2\2\2_a\5\6\4\2`_\3\2\2\2ad\3\2\2"+
		"\2b`\3\2\2\2bc\3\2\2\2ce\3\2\2\2db\3\2\2\2ef\7\5\2\2f\5\3\2\2\2gh\7\7"+
		"\2\2hi\7,\2\2ij\7\b\2\2j\7\3\2\2\2ko\5\n\6\2lo\5\20\t\2mo\5\26\f\2nk\3"+
		"\2\2\2nl\3\2\2\2nm\3\2\2\2o\t\3\2\2\2pq\7\t\2\2qr\7,\2\2rs\7\4\2\2st\5"+
		"\f\7\2tu\5\36\20\2uw\5(\25\2vx\5*\26\2wv\3\2\2\2wx\3\2\2\2xz\3\2\2\2y"+
		"{\5 \21\2zy\3\2\2\2z{\3\2\2\2{|\3\2\2\2|}\7\5\2\2}\13\3\2\2\2~\177\7\n"+
		"\2\2\177\u0080\5\16\b\2\u0080\r\3\2\2\2\u0081\u0082\t\2\2\2\u0082\17\3"+
		"\2\2\2\u0083\u0084\7\16\2\2\u0084\u0085\7,\2\2\u0085\u0087\7\4\2\2\u0086"+
		"\u0088\5&\24\2\u0087\u0086\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2"+
		"\2\2\u0089\u008a\5\22\n\2\u008a\u008c\5\36\20\2\u008b\u008d\5.\30\2\u008c"+
		"\u008b\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e\u0090\5\62"+
		"\32\2\u008f\u008e\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2\2\2\u0091"+
		"\u0093\5\60\31\2\u0092\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3"+
		"\2\2\2\u0094\u0096\5 \21\2\u0095\u0094\3\2\2\2\u0095\u0096\3\2\2\2\u0096"+
		"\u0097\3\2\2\2\u0097\u0098\7\5\2\2\u0098\21\3\2\2\2\u0099\u009a\7\n\2"+
		"\2\u009a\u009b\5\24\13\2\u009b\23\3\2\2\2\u009c\u009d\t\3\2\2\u009d\25"+
		"\3\2\2\2\u009e\u009f\7\22\2\2\u009f\u00a0\7,\2\2\u00a0\u00a1\7\4\2\2\u00a1"+
		"\u00a2\5\36\20\2\u00a2\u00a4\5\30\r\2\u00a3\u00a5\5.\30\2\u00a4\u00a3"+
		"\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a8\5\32\16\2"+
		"\u00a7\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ac\7\5\2\2\u00ac\27\3\2\2\2\u00ad"+
		"\u00ae\7\n\2\2\u00ae\u00af\5:\36\2\u00af\31\3\2\2\2\u00b0\u00b1\7\23\2"+
		"\2\u00b1\u00b3\7,\2\2\u00b2\u00b4\5\34\17\2\u00b3\u00b2\3\2\2\2\u00b3"+
		"\u00b4\3\2\2\2\u00b4\33\3\2\2\2\u00b5\u00b6\7\24\2\2\u00b6\u00b9\5\64"+
		"\33\2\u00b7\u00b9\7\25\2\2\u00b8\u00b5\3\2\2\2\u00b8\u00b7\3\2\2\2\u00b9"+
		"\35\3\2\2\2\u00ba\u00bb\7\7\2\2\u00bb\u00bc\7,\2\2\u00bc\37\3\2\2\2\u00bd"+
		"\u00be\7\23\2\2\u00be\u00bf\7,\2\2\u00bf!\3\2\2\2\u00c0\u00c1\7\26\2\2"+
		"\u00c1\u00c7\7,\2\2\u00c2\u00c4\7\4\2\2\u00c3\u00c5\5&\24\2\u00c4\u00c3"+
		"\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c8\7\5\2\2\u00c7"+
		"\u00c2\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8#\3\2\2\2\u00c9\u00ca\7\27\2\2"+
		"\u00ca\u00cb\7,\2\2\u00cb\u00cc\7\30\2\2\u00cc\u00d2\7,\2\2\u00cd\u00cf"+
		"\7\4\2\2\u00ce\u00d0\5,\27\2\u00cf\u00ce\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0"+
		"\u00d1\3\2\2\2\u00d1\u00d3\7\5\2\2\u00d2\u00cd\3\2\2\2\u00d2\u00d3\3\2"+
		"\2\2\u00d3%\3\2\2\2\u00d4\u00d5\7\31\2\2\u00d5\u00d6\7-\2\2\u00d6\'\3"+
		"\2\2\2\u00d7\u00d8\7\32\2\2\u00d8\u00d9\5\66\34\2\u00d9)\3\2\2\2\u00da"+
		"\u00db\7\33\2\2\u00db\u00dc\58\35\2\u00dc+\3\2\2\2\u00dd\u00de\7\26\2"+
		"\2\u00de\u00df\7,\2\2\u00df-\3\2\2\2\u00e0\u00e1\7\34\2\2\u00e1\u00e2"+
		"\5\64\33\2\u00e2/\3\2\2\2\u00e3\u00e4\7\35\2\2\u00e4\u00e5\5\64\33\2\u00e5"+
		"\61\3\2\2\2\u00e6\u00e7\7\36\2\2\u00e7\u00e8\5\64\33\2\u00e8\63\3\2\2"+
		"\2\u00e9\u00ea\7.\2\2\u00ea\65\3\2\2\2\u00eb\u00ec\t\4\2\2\u00ec\67\3"+
		"\2\2\2\u00ed\u00ee\t\5\2\2\u00ee9\3\2\2\2\u00ef\u00f0\t\6\2\2\u00f0;\3"+
		"\2\2\2\30BGMS]bnwz\u0087\u008c\u008f\u0092\u0095\u00a4\u00a9\u00b3\u00b8"+
		"\u00c4\u00c7\u00cf\u00d2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}