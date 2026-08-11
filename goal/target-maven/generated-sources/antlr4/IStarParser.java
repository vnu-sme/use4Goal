// Generated from IStar.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.istar.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IStarParser extends Parser {
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
		T__38=39, T__39=40, T__40=41, OCL_CLAUSE=42, OCL_BLOCK=43, IDENT=44, WS=45, 
		LINE_COMMENT=46, BLOCK_COMMENT=47;
	public static final int
		RULE_model = 0, RULE_actorDef = 1, RULE_actorKind = 2, RULE_actorBody = 3, 
		RULE_goalType = 4, RULE_goalTypeName = 5, RULE_obstacleType = 6, RULE_obstacleTypeName = 7, 
		RULE_rel = 8, RULE_dependency = 9, RULE_dependumRef = 10, RULE_dependumKind = 11, 
		RULE_depEnd = 12, RULE_contribType = 13, RULE_oclCondition = 14, RULE_goalCondition = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "actorDef", "actorKind", "actorBody", "goalType", "goalTypeName", 
			"obstacleType", "obstacleTypeName", "rel", "dependency", "dependumRef", 
			"dependumKind", "depEnd", "contribType", "oclCondition", "goalCondition"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'istar'", "'{'", "'}'", "'role'", "'agent'", "'goal'", "'task'", 
			"'resource'", "'quality'", "'obstacle'", "'is-a'", "'participates-in'", 
			"':'", "'Achieve'", "'Maintain'", "'Avoid'", "'Prevention'", "'Restoration'", 
			"'Mitigation'", "'>'", "'or'", "'forall'", "'pick'", "'qualifies'", "'needed-by'", 
			"'obstructs'", "'resolves'", "'depend'", "'->'", "'.'", "'make'", "'help'", 
			"'hurt'", "'break'", "'pre'", "'post'", "'trigger'", "'satisfy'", "'while'", 
			"'ensure'", "'release'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "OCL_CLAUSE", "OCL_BLOCK", "IDENT", 
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
	public String getGrammarFileName() { return "IStar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public IStarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public TerminalNode EOF() { return getToken(IStarParser.EOF, 0); }
		public List<ActorDefContext> actorDef() {
			return getRuleContexts(ActorDefContext.class);
		}
		public ActorDefContext actorDef(int i) {
			return getRuleContext(ActorDefContext.class,i);
		}
		public List<DependencyContext> dependency() {
			return getRuleContexts(DependencyContext.class);
		}
		public DependencyContext dependency(int i) {
			return getRuleContext(DependencyContext.class,i);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitModel(this);
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
			setState(32);
			match(T__0);
			setState(33);
			match(IDENT);
			setState(34);
			match(T__1);
			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__4) {
				{
				{
				setState(35);
				actorDef();
				}
				}
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__27) {
				{
				{
				setState(41);
				dependency();
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47);
			match(T__2);
			setState(48);
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

	public static class ActorDefContext extends ParserRuleContext {
		public ActorKindContext actorKind() {
			return getRuleContext(ActorKindContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public List<ActorBodyContext> actorBody() {
			return getRuleContexts(ActorBodyContext.class);
		}
		public ActorBodyContext actorBody(int i) {
			return getRuleContext(ActorBodyContext.class,i);
		}
		public ActorDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actorDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterActorDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitActorDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitActorDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorDefContext actorDef() throws RecognitionException {
		ActorDefContext _localctx = new ActorDefContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_actorDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(50);
			actorKind();
			setState(51);
			match(IDENT);
			setState(52);
			match(T__1);
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << IDENT))) != 0)) {
				{
				{
				setState(53);
				actorBody();
				}
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
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

	public static class ActorKindContext extends ParserRuleContext {
		public ActorKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actorKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterActorKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitActorKind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitActorKind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorKindContext actorKind() throws RecognitionException {
		ActorKindContext _localctx = new ActorKindContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_actorKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_la = _input.LA(1);
			if ( !(_la==T__3 || _la==T__4) ) {
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

	public static class ActorBodyContext extends ParserRuleContext {
		public ActorBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actorBody; }
	 
		public ActorBodyContext() { }
		public void copyFrom(ActorBodyContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BodyIsAContext extends ActorBodyContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarParser.IDENT, i);
		}
		public BodyIsAContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyIsA(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyIsA(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyIsA(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyObstacleContext extends ActorBodyContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public ObstacleTypeContext obstacleType() {
			return getRuleContext(ObstacleTypeContext.class,0);
		}
		public List<RelContext> rel() {
			return getRuleContexts(RelContext.class);
		}
		public RelContext rel(int i) {
			return getRuleContext(RelContext.class,i);
		}
		public BodyObstacleContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyObstacle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyObstacle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyObstacle(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyParticipatesContext extends ActorBodyContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarParser.IDENT, i);
		}
		public BodyParticipatesContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyParticipates(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyParticipates(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyParticipates(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyGoalContext extends ActorBodyContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public GoalTypeContext goalType() {
			return getRuleContext(GoalTypeContext.class,0);
		}
		public List<RelContext> rel() {
			return getRuleContexts(RelContext.class);
		}
		public RelContext rel(int i) {
			return getRuleContext(RelContext.class,i);
		}
		public List<GoalConditionContext> goalCondition() {
			return getRuleContexts(GoalConditionContext.class);
		}
		public GoalConditionContext goalCondition(int i) {
			return getRuleContext(GoalConditionContext.class,i);
		}
		public BodyGoalContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyGoal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyGoal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyGoal(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyResourceContext extends ActorBodyContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public List<RelContext> rel() {
			return getRuleContexts(RelContext.class);
		}
		public RelContext rel(int i) {
			return getRuleContext(RelContext.class,i);
		}
		public BodyResourceContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyResource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyResource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyResource(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyQualityContext extends ActorBodyContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public List<RelContext> rel() {
			return getRuleContexts(RelContext.class);
		}
		public RelContext rel(int i) {
			return getRuleContext(RelContext.class,i);
		}
		public BodyQualityContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyQuality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyQuality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyQuality(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BodyTaskContext extends ActorBodyContext {
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public List<RelContext> rel() {
			return getRuleContexts(RelContext.class);
		}
		public RelContext rel(int i) {
			return getRuleContext(RelContext.class,i);
		}
		public List<OclConditionContext> oclCondition() {
			return getRuleContexts(OclConditionContext.class);
		}
		public OclConditionContext oclCondition(int i) {
			return getRuleContext(OclConditionContext.class,i);
		}
		public BodyTaskContext(ActorBodyContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterBodyTask(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitBodyTask(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitBodyTask(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorBodyContext actorBody() throws RecognitionException {
		ActorBodyContext _localctx = new ActorBodyContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_actorBody);
		int _la;
		try {
			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				_localctx = new BodyGoalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(63);
				match(T__5);
				setState(64);
				match(IDENT);
				setState(66);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__12) {
					{
					setState(65);
					goalType();
					}
				}

				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19) {
					{
					{
					setState(68);
					rel();
					}
					}
					setState(73);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__35) | (1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40) | (1L << OCL_CLAUSE))) != 0)) {
					{
					{
					setState(74);
					goalCondition();
					}
					}
					setState(79);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new BodyTaskContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(80);
				match(T__6);
				setState(81);
				match(IDENT);
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19) {
					{
					{
					setState(82);
					rel();
					}
					}
					setState(87);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__34) | (1L << T__35) | (1L << OCL_CLAUSE))) != 0)) {
					{
					{
					setState(88);
					oclCondition();
					}
					}
					setState(93);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				_localctx = new BodyResourceContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(94);
				match(T__7);
				setState(95);
				match(IDENT);
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19) {
					{
					{
					setState(96);
					rel();
					}
					}
					setState(101);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 4:
				_localctx = new BodyQualityContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(102);
				match(T__8);
				setState(103);
				match(IDENT);
				setState(107);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19) {
					{
					{
					setState(104);
					rel();
					}
					}
					setState(109);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 5:
				_localctx = new BodyObstacleContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(110);
				match(T__9);
				setState(111);
				match(IDENT);
				setState(113);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__12) {
					{
					setState(112);
					obstacleType();
					}
				}

				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19) {
					{
					{
					setState(115);
					rel();
					}
					}
					setState(120);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 6:
				_localctx = new BodyIsAContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(121);
				match(IDENT);
				setState(122);
				match(T__10);
				setState(123);
				match(IDENT);
				}
				break;
			case 7:
				_localctx = new BodyParticipatesContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(124);
				match(IDENT);
				setState(125);
				match(T__11);
				setState(126);
				match(IDENT);
				}
				break;
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

	public static class GoalTypeContext extends ParserRuleContext {
		public GoalTypeNameContext goalTypeName() {
			return getRuleContext(GoalTypeNameContext.class,0);
		}
		public GoalTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goalType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterGoalType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitGoalType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitGoalType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalTypeContext goalType() throws RecognitionException {
		GoalTypeContext _localctx = new GoalTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_goalType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(T__12);
			setState(130);
			goalTypeName();
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

	public static class GoalTypeNameContext extends ParserRuleContext {
		public GoalTypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goalTypeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterGoalTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitGoalTypeName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitGoalTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalTypeNameContext goalTypeName() throws RecognitionException {
		GoalTypeNameContext _localctx = new GoalTypeNameContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_goalTypeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__15))) != 0)) ) {
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

	public static class ObstacleTypeContext extends ParserRuleContext {
		public ObstacleTypeNameContext obstacleTypeName() {
			return getRuleContext(ObstacleTypeNameContext.class,0);
		}
		public ObstacleTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obstacleType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterObstacleType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitObstacleType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitObstacleType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObstacleTypeContext obstacleType() throws RecognitionException {
		ObstacleTypeContext _localctx = new ObstacleTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_obstacleType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(T__12);
			setState(135);
			obstacleTypeName();
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

	public static class ObstacleTypeNameContext extends ParserRuleContext {
		public ObstacleTypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_obstacleTypeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterObstacleTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitObstacleTypeName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitObstacleTypeName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ObstacleTypeNameContext obstacleTypeName() throws RecognitionException {
		ObstacleTypeNameContext _localctx = new ObstacleTypeNameContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_obstacleTypeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) ) {
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

	public static class RelContext extends ParserRuleContext {
		public RelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rel; }
	 
		public RelContext() { }
		public void copyFrom(RelContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelResolvesContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelResolvesContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelResolves(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelResolves(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelResolves(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelQualifiesContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelQualifiesContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelQualifies(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelQualifies(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelQualifies(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelForAllContext extends RelContext {
		public Token actorType;
		public Token target;
		public List<TerminalNode> IDENT() { return getTokens(IStarParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarParser.IDENT, i);
		}
		public RelForAllContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelForAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelForAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelForAll(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelAndContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelAndContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelAnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelAnd(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelContributeContext extends RelContext {
		public Token target;
		public ContribTypeContext contribType() {
			return getRuleContext(ContribTypeContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelContributeContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelContribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelContribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelContribute(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelOrContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelOrContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelOr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelOr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelPickContext extends RelContext {
		public Token actorType;
		public Token target;
		public List<TerminalNode> IDENT() { return getTokens(IStarParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarParser.IDENT, i);
		}
		public RelPickContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelPick(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelPick(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelPick(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelObstructsContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelObstructsContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelObstructs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelObstructs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelObstructs(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelNeededByContext extends RelContext {
		public Token target;
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public RelNeededByContext(RelContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterRelNeededBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitRelNeededBy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitRelNeededBy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelContext rel() throws RecognitionException {
		RelContext _localctx = new RelContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_rel);
		try {
			setState(168);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				_localctx = new RelAndContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				match(T__19);
				setState(140);
				((RelAndContext)_localctx).target = match(IDENT);
				}
				break;
			case 2:
				_localctx = new RelOrContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(141);
				match(T__19);
				setState(142);
				match(T__20);
				setState(143);
				((RelOrContext)_localctx).target = match(IDENT);
				}
				break;
			case 3:
				_localctx = new RelForAllContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(144);
				match(T__19);
				setState(145);
				match(T__21);
				setState(146);
				((RelForAllContext)_localctx).actorType = match(IDENT);
				setState(147);
				((RelForAllContext)_localctx).target = match(IDENT);
				}
				break;
			case 4:
				_localctx = new RelPickContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(148);
				match(T__19);
				setState(149);
				match(T__22);
				setState(150);
				((RelPickContext)_localctx).actorType = match(IDENT);
				setState(151);
				((RelPickContext)_localctx).target = match(IDENT);
				}
				break;
			case 5:
				_localctx = new RelContributeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(152);
				match(T__19);
				setState(153);
				contribType();
				setState(154);
				((RelContributeContext)_localctx).target = match(IDENT);
				}
				break;
			case 6:
				_localctx = new RelQualifiesContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(156);
				match(T__19);
				setState(157);
				match(T__23);
				setState(158);
				((RelQualifiesContext)_localctx).target = match(IDENT);
				}
				break;
			case 7:
				_localctx = new RelNeededByContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(159);
				match(T__19);
				setState(160);
				match(T__24);
				setState(161);
				((RelNeededByContext)_localctx).target = match(IDENT);
				}
				break;
			case 8:
				_localctx = new RelObstructsContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(162);
				match(T__19);
				setState(163);
				match(T__25);
				setState(164);
				((RelObstructsContext)_localctx).target = match(IDENT);
				}
				break;
			case 9:
				_localctx = new RelResolvesContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(165);
				match(T__19);
				setState(166);
				match(T__26);
				setState(167);
				((RelResolvesContext)_localctx).target = match(IDENT);
				}
				break;
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

	public static class DependencyContext extends ParserRuleContext {
		public List<DepEndContext> depEnd() {
			return getRuleContexts(DepEndContext.class);
		}
		public DepEndContext depEnd(int i) {
			return getRuleContext(DepEndContext.class,i);
		}
		public DependumRefContext dependumRef() {
			return getRuleContext(DependumRefContext.class,0);
		}
		public DependencyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependency; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterDependency(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitDependency(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitDependency(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DependencyContext dependency() throws RecognitionException {
		DependencyContext _localctx = new DependencyContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_dependency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(T__27);
			setState(171);
			depEnd();
			setState(172);
			match(T__28);
			setState(173);
			dependumRef();
			setState(174);
			match(T__28);
			setState(175);
			depEnd();
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

	public static class DependumRefContext extends ParserRuleContext {
		public DependumKindContext dependumKind() {
			return getRuleContext(DependumKindContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(IStarParser.IDENT, 0); }
		public DependumRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependumRef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterDependumRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitDependumRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitDependumRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DependumRefContext dependumRef() throws RecognitionException {
		DependumRefContext _localctx = new DependumRefContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_dependumRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			dependumKind();
			setState(178);
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

	public static class DependumKindContext extends ParserRuleContext {
		public DependumKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dependumKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterDependumKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitDependumKind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitDependumKind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DependumKindContext dependumKind() throws RecognitionException {
		DependumKindContext _localctx = new DependumKindContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_dependumKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8))) != 0)) ) {
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

	public static class DepEndContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarParser.IDENT, i);
		}
		public DepEndContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_depEnd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterDepEnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitDepEnd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitDepEnd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DepEndContext depEnd() throws RecognitionException {
		DepEndContext _localctx = new DepEndContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_depEnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(IDENT);
			setState(185);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__29) {
				{
				setState(183);
				match(T__29);
				setState(184);
				match(IDENT);
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

	public static class ContribTypeContext extends ParserRuleContext {
		public ContribTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_contribType; }
	 
		public ContribTypeContext() { }
		public void copyFrom(ContribTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CtHurtContext extends ContribTypeContext {
		public CtHurtContext(ContribTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterCtHurt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitCtHurt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitCtHurt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CtHelpContext extends ContribTypeContext {
		public CtHelpContext(ContribTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterCtHelp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitCtHelp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitCtHelp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CtMakeContext extends ContribTypeContext {
		public CtMakeContext(ContribTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterCtMake(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitCtMake(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitCtMake(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CtBreakContext extends ContribTypeContext {
		public CtBreakContext(ContribTypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterCtBreak(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitCtBreak(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitCtBreak(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ContribTypeContext contribType() throws RecognitionException {
		ContribTypeContext _localctx = new ContribTypeContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_contribType);
		try {
			setState(191);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__30:
				_localctx = new CtMakeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(187);
				match(T__30);
				}
				break;
			case T__31:
				_localctx = new CtHelpContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(188);
				match(T__31);
				}
				break;
			case T__32:
				_localctx = new CtHurtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(189);
				match(T__32);
				}
				break;
			case T__33:
				_localctx = new CtBreakContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(190);
				match(T__33);
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

	public static class OclConditionContext extends ParserRuleContext {
		public TerminalNode OCL_BLOCK() { return getToken(IStarParser.OCL_BLOCK, 0); }
		public TerminalNode OCL_CLAUSE() { return getToken(IStarParser.OCL_CLAUSE, 0); }
		public OclConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oclCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterOclCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitOclCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitOclCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OclConditionContext oclCondition() throws RecognitionException {
		OclConditionContext _localctx = new OclConditionContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_oclCondition);
		int _la;
		try {
			setState(196);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__34:
			case T__35:
				enterOuterAlt(_localctx, 1);
				{
				setState(193);
				_la = _input.LA(1);
				if ( !(_la==T__34 || _la==T__35) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(194);
				match(OCL_BLOCK);
				}
				break;
			case OCL_CLAUSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(195);
				match(OCL_CLAUSE);
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

	public static class GoalConditionContext extends ParserRuleContext {
		public TerminalNode OCL_BLOCK() { return getToken(IStarParser.OCL_BLOCK, 0); }
		public OclConditionContext oclCondition() {
			return getRuleContext(OclConditionContext.class,0);
		}
		public GoalConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goalCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).enterGoalCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarListener ) ((IStarListener)listener).exitGoalCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarVisitor ) return ((IStarVisitor<? extends T>)visitor).visitGoalCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalConditionContext goalCondition() throws RecognitionException {
		GoalConditionContext _localctx = new GoalConditionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_goalCondition);
		int _la;
		try {
			setState(201);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__36:
			case T__37:
			case T__38:
			case T__39:
			case T__40:
				enterOuterAlt(_localctx, 1);
				{
				setState(198);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__36) | (1L << T__37) | (1L << T__38) | (1L << T__39) | (1L << T__40))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(199);
				match(OCL_BLOCK);
				}
				break;
			case T__34:
			case T__35:
			case OCL_CLAUSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
				oclCondition();
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\61\u00ce\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\3\2\3\2"+
		"\3\2\3\2\7\2\'\n\2\f\2\16\2*\13\2\3\2\7\2-\n\2\f\2\16\2\60\13\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\7\39\n\3\f\3\16\3<\13\3\3\3\3\3\3\4\3\4\3\5\3\5"+
		"\3\5\5\5E\n\5\3\5\7\5H\n\5\f\5\16\5K\13\5\3\5\7\5N\n\5\f\5\16\5Q\13\5"+
		"\3\5\3\5\3\5\7\5V\n\5\f\5\16\5Y\13\5\3\5\7\5\\\n\5\f\5\16\5_\13\5\3\5"+
		"\3\5\3\5\7\5d\n\5\f\5\16\5g\13\5\3\5\3\5\3\5\7\5l\n\5\f\5\16\5o\13\5\3"+
		"\5\3\5\3\5\5\5t\n\5\3\5\7\5w\n\5\f\5\16\5z\13\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\5\5\u0082\n\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00ab\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\5\16\u00bc\n\16\3\17\3\17"+
		"\3\17\3\17\5\17\u00c2\n\17\3\20\3\20\3\20\5\20\u00c7\n\20\3\21\3\21\3"+
		"\21\5\21\u00cc\n\21\3\21\2\2\22\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		" \2\b\3\2\6\7\3\2\20\22\3\2\23\25\3\2\b\13\3\2%&\3\2\'+\2\u00dd\2\"\3"+
		"\2\2\2\4\64\3\2\2\2\6?\3\2\2\2\b\u0081\3\2\2\2\n\u0083\3\2\2\2\f\u0086"+
		"\3\2\2\2\16\u0088\3\2\2\2\20\u008b\3\2\2\2\22\u00aa\3\2\2\2\24\u00ac\3"+
		"\2\2\2\26\u00b3\3\2\2\2\30\u00b6\3\2\2\2\32\u00b8\3\2\2\2\34\u00c1\3\2"+
		"\2\2\36\u00c6\3\2\2\2 \u00cb\3\2\2\2\"#\7\3\2\2#$\7.\2\2$(\7\4\2\2%\'"+
		"\5\4\3\2&%\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2).\3\2\2\2*(\3\2\2\2"+
		"+-\5\24\13\2,+\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2/\61\3\2\2\2\60"+
		".\3\2\2\2\61\62\7\5\2\2\62\63\7\2\2\3\63\3\3\2\2\2\64\65\5\6\4\2\65\66"+
		"\7.\2\2\66:\7\4\2\2\679\5\b\5\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2"+
		"\2\2;=\3\2\2\2<:\3\2\2\2=>\7\5\2\2>\5\3\2\2\2?@\t\2\2\2@\7\3\2\2\2AB\7"+
		"\b\2\2BD\7.\2\2CE\5\n\6\2DC\3\2\2\2DE\3\2\2\2EI\3\2\2\2FH\5\22\n\2GF\3"+
		"\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JO\3\2\2\2KI\3\2\2\2LN\5 \21\2ML\3"+
		"\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\u0082\3\2\2\2QO\3\2\2\2RS\7\t\2"+
		"\2SW\7.\2\2TV\5\22\n\2UT\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2X]\3\2\2"+
		"\2YW\3\2\2\2Z\\\5\36\20\2[Z\3\2\2\2\\_\3\2\2\2][\3\2\2\2]^\3\2\2\2^\u0082"+
		"\3\2\2\2_]\3\2\2\2`a\7\n\2\2ae\7.\2\2bd\5\22\n\2cb\3\2\2\2dg\3\2\2\2e"+
		"c\3\2\2\2ef\3\2\2\2f\u0082\3\2\2\2ge\3\2\2\2hi\7\13\2\2im\7.\2\2jl\5\22"+
		"\n\2kj\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\u0082\3\2\2\2om\3\2\2\2"+
		"pq\7\f\2\2qs\7.\2\2rt\5\16\b\2sr\3\2\2\2st\3\2\2\2tx\3\2\2\2uw\5\22\n"+
		"\2vu\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y\u0082\3\2\2\2zx\3\2\2\2{|"+
		"\7.\2\2|}\7\r\2\2}\u0082\7.\2\2~\177\7.\2\2\177\u0080\7\16\2\2\u0080\u0082"+
		"\7.\2\2\u0081A\3\2\2\2\u0081R\3\2\2\2\u0081`\3\2\2\2\u0081h\3\2\2\2\u0081"+
		"p\3\2\2\2\u0081{\3\2\2\2\u0081~\3\2\2\2\u0082\t\3\2\2\2\u0083\u0084\7"+
		"\17\2\2\u0084\u0085\5\f\7\2\u0085\13\3\2\2\2\u0086\u0087\t\3\2\2\u0087"+
		"\r\3\2\2\2\u0088\u0089\7\17\2\2\u0089\u008a\5\20\t\2\u008a\17\3\2\2\2"+
		"\u008b\u008c\t\4\2\2\u008c\21\3\2\2\2\u008d\u008e\7\26\2\2\u008e\u00ab"+
		"\7.\2\2\u008f\u0090\7\26\2\2\u0090\u0091\7\27\2\2\u0091\u00ab\7.\2\2\u0092"+
		"\u0093\7\26\2\2\u0093\u0094\7\30\2\2\u0094\u0095\7.\2\2\u0095\u00ab\7"+
		".\2\2\u0096\u0097\7\26\2\2\u0097\u0098\7\31\2\2\u0098\u0099\7.\2\2\u0099"+
		"\u00ab\7.\2\2\u009a\u009b\7\26\2\2\u009b\u009c\5\34\17\2\u009c\u009d\7"+
		".\2\2\u009d\u00ab\3\2\2\2\u009e\u009f\7\26\2\2\u009f\u00a0\7\32\2\2\u00a0"+
		"\u00ab\7.\2\2\u00a1\u00a2\7\26\2\2\u00a2\u00a3\7\33\2\2\u00a3\u00ab\7"+
		".\2\2\u00a4\u00a5\7\26\2\2\u00a5\u00a6\7\34\2\2\u00a6\u00ab\7.\2\2\u00a7"+
		"\u00a8\7\26\2\2\u00a8\u00a9\7\35\2\2\u00a9\u00ab\7.\2\2\u00aa\u008d\3"+
		"\2\2\2\u00aa\u008f\3\2\2\2\u00aa\u0092\3\2\2\2\u00aa\u0096\3\2\2\2\u00aa"+
		"\u009a\3\2\2\2\u00aa\u009e\3\2\2\2\u00aa\u00a1\3\2\2\2\u00aa\u00a4\3\2"+
		"\2\2\u00aa\u00a7\3\2\2\2\u00ab\23\3\2\2\2\u00ac\u00ad\7\36\2\2\u00ad\u00ae"+
		"\5\32\16\2\u00ae\u00af\7\37\2\2\u00af\u00b0\5\26\f\2\u00b0\u00b1\7\37"+
		"\2\2\u00b1\u00b2\5\32\16\2\u00b2\25\3\2\2\2\u00b3\u00b4\5\30\r\2\u00b4"+
		"\u00b5\7.\2\2\u00b5\27\3\2\2\2\u00b6\u00b7\t\5\2\2\u00b7\31\3\2\2\2\u00b8"+
		"\u00bb\7.\2\2\u00b9\u00ba\7 \2\2\u00ba\u00bc\7.\2\2\u00bb\u00b9\3\2\2"+
		"\2\u00bb\u00bc\3\2\2\2\u00bc\33\3\2\2\2\u00bd\u00c2\7!\2\2\u00be\u00c2"+
		"\7\"\2\2\u00bf\u00c2\7#\2\2\u00c0\u00c2\7$\2\2\u00c1\u00bd\3\2\2\2\u00c1"+
		"\u00be\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2\35\3\2\2"+
		"\2\u00c3\u00c4\t\6\2\2\u00c4\u00c7\7-\2\2\u00c5\u00c7\7,\2\2\u00c6\u00c3"+
		"\3\2\2\2\u00c6\u00c5\3\2\2\2\u00c7\37\3\2\2\2\u00c8\u00c9\t\7\2\2\u00c9"+
		"\u00cc\7-\2\2\u00ca\u00cc\5\36\20\2\u00cb\u00c8\3\2\2\2\u00cb\u00ca\3"+
		"\2\2\2\u00cc!\3\2\2\2\24(.:DIOW]emsx\u0081\u00aa\u00bb\u00c1\u00c6\u00cb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}