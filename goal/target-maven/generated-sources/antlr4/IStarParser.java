// Generated from IStar.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.istar.parser; 
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
		OCL_CLAUSE=32, OCL_BLOCK=33, IDENT=34, WS=35, LINE_COMMENT=36, BLOCK_COMMENT=37;
	public static final int
		RULE_model = 0, RULE_actorDef = 1, RULE_actorKind = 2, RULE_actorBody = 3, 
		RULE_goalType = 4, RULE_goalTypeName = 5, RULE_rel = 6, RULE_dependency = 7, 
		RULE_dependumRef = 8, RULE_dependumKind = 9, RULE_depEnd = 10, RULE_contribType = 11, 
		RULE_oclCondition = 12, RULE_goalCondition = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "actorDef", "actorKind", "actorBody", "goalType", "goalTypeName", 
			"rel", "dependency", "dependumRef", "dependumKind", "depEnd", "contribType", 
			"oclCondition", "goalCondition"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'istar'", "'{'", "'}'", "'role'", "'agent'", "'goal'", "'task'", 
			"'resource'", "'quality'", "'is-a'", "'participates-in'", "':'", "'Achieve'", 
			"'Maintain'", "'Sustain'", "'>'", "'or'", "'qualifies'", "'needed-by'", 
			"'depend'", "'->'", "'.'", "'make'", "'help'", "'hurt'", "'break'", "'pre'", 
			"'post'", "'condition'", "'satisfy'", "'ensure'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "OCL_CLAUSE", "OCL_BLOCK", 
			"IDENT", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
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
			setState(28);
			match(T__0);
			setState(29);
			match(IDENT);
			setState(30);
			match(T__1);
			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__4) {
				{
				{
				setState(31);
				actorDef();
				}
				}
				setState(36);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(40);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__19) {
				{
				{
				setState(37);
				dependency();
				}
				}
				setState(42);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(43);
			match(T__2);
			setState(44);
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
			setState(46);
			actorKind();
			setState(47);
			match(IDENT);
			setState(48);
			match(T__1);
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << IDENT))) != 0)) {
				{
				{
				setState(49);
				actorBody();
				}
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55);
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
			setState(57);
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
			setState(112);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				_localctx = new BodyGoalContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(59);
				match(T__5);
				setState(60);
				match(IDENT);
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__11) {
					{
					setState(61);
					goalType();
					}
				}

				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15) {
					{
					{
					setState(64);
					rel();
					}
					}
					setState(69);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << OCL_CLAUSE))) != 0)) {
					{
					{
					setState(70);
					goalCondition();
					}
					}
					setState(75);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 2:
				_localctx = new BodyTaskContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				match(T__6);
				setState(77);
				match(IDENT);
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15) {
					{
					{
					setState(78);
					rel();
					}
					}
					setState(83);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__26) | (1L << T__27) | (1L << OCL_CLAUSE))) != 0)) {
					{
					{
					setState(84);
					oclCondition();
					}
					}
					setState(89);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 3:
				_localctx = new BodyResourceContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(90);
				match(T__7);
				setState(91);
				match(IDENT);
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15) {
					{
					{
					setState(92);
					rel();
					}
					}
					setState(97);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 4:
				_localctx = new BodyQualityContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(98);
				match(T__8);
				setState(99);
				match(IDENT);
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15) {
					{
					{
					setState(100);
					rel();
					}
					}
					setState(105);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case 5:
				_localctx = new BodyIsAContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(106);
				match(IDENT);
				setState(107);
				match(T__9);
				setState(108);
				match(IDENT);
				}
				break;
			case 6:
				_localctx = new BodyParticipatesContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(109);
				match(IDENT);
				setState(110);
				match(T__10);
				setState(111);
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
			setState(114);
			match(T__11);
			setState(115);
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
			setState(117);
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
		enterRule(_localctx, 12, RULE_rel);
		try {
			setState(134);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				_localctx = new RelAndContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(119);
				match(T__15);
				setState(120);
				((RelAndContext)_localctx).target = match(IDENT);
				}
				break;
			case 2:
				_localctx = new RelOrContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(121);
				match(T__15);
				setState(122);
				match(T__16);
				setState(123);
				((RelOrContext)_localctx).target = match(IDENT);
				}
				break;
			case 3:
				_localctx = new RelContributeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(124);
				match(T__15);
				setState(125);
				contribType();
				setState(126);
				((RelContributeContext)_localctx).target = match(IDENT);
				}
				break;
			case 4:
				_localctx = new RelQualifiesContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(128);
				match(T__15);
				setState(129);
				match(T__17);
				setState(130);
				((RelQualifiesContext)_localctx).target = match(IDENT);
				}
				break;
			case 5:
				_localctx = new RelNeededByContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(131);
				match(T__15);
				setState(132);
				match(T__18);
				setState(133);
				((RelNeededByContext)_localctx).target = match(IDENT);
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
		enterRule(_localctx, 14, RULE_dependency);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(T__19);
			setState(137);
			depEnd();
			setState(138);
			match(T__20);
			setState(139);
			dependumRef();
			setState(140);
			match(T__20);
			setState(141);
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
		enterRule(_localctx, 16, RULE_dependumRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			dependumKind();
			setState(144);
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
		enterRule(_localctx, 18, RULE_dependumKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
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
		enterRule(_localctx, 20, RULE_depEnd);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(IDENT);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__21) {
				{
				setState(149);
				match(T__21);
				setState(150);
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
		enterRule(_localctx, 22, RULE_contribType);
		try {
			setState(157);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__22:
				_localctx = new CtMakeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(153);
				match(T__22);
				}
				break;
			case T__23:
				_localctx = new CtHelpContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(154);
				match(T__23);
				}
				break;
			case T__24:
				_localctx = new CtHurtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(155);
				match(T__24);
				}
				break;
			case T__25:
				_localctx = new CtBreakContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(156);
				match(T__25);
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
		enterRule(_localctx, 24, RULE_oclCondition);
		int _la;
		try {
			setState(162);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
			case T__27:
				enterOuterAlt(_localctx, 1);
				{
				setState(159);
				_la = _input.LA(1);
				if ( !(_la==T__26 || _la==T__27) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(160);
				match(OCL_BLOCK);
				}
				break;
			case OCL_CLAUSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(161);
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
		public TerminalNode OCL_CLAUSE() { return getToken(IStarParser.OCL_CLAUSE, 0); }
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
		enterRule(_localctx, 26, RULE_goalCondition);
		int _la;
		try {
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__28:
			case T__29:
			case T__30:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << T__30))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(165);
				match(OCL_BLOCK);
				}
				break;
			case OCL_CLAUSE:
				enterOuterAlt(_localctx, 2);
				{
				setState(166);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'\u00ac\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\7\2#\n\2\f\2\16"+
		"\2&\13\2\3\2\7\2)\n\2\f\2\16\2,\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3\65"+
		"\n\3\f\3\16\38\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\5\5A\n\5\3\5\7\5D\n\5"+
		"\f\5\16\5G\13\5\3\5\7\5J\n\5\f\5\16\5M\13\5\3\5\3\5\3\5\7\5R\n\5\f\5\16"+
		"\5U\13\5\3\5\7\5X\n\5\f\5\16\5[\13\5\3\5\3\5\3\5\7\5`\n\5\f\5\16\5c\13"+
		"\5\3\5\3\5\3\5\7\5h\n\5\f\5\16\5k\13\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5s\n"+
		"\5\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\5\b\u0089\n\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13"+
		"\3\13\3\f\3\f\3\f\5\f\u009a\n\f\3\r\3\r\3\r\3\r\5\r\u00a0\n\r\3\16\3\16"+
		"\3\16\5\16\u00a5\n\16\3\17\3\17\3\17\5\17\u00aa\n\17\3\17\2\2\20\2\4\6"+
		"\b\n\f\16\20\22\24\26\30\32\34\2\7\3\2\6\7\3\2\17\21\3\2\b\13\3\2\35\36"+
		"\3\2\37!\2\u00b6\2\36\3\2\2\2\4\60\3\2\2\2\6;\3\2\2\2\br\3\2\2\2\nt\3"+
		"\2\2\2\fw\3\2\2\2\16\u0088\3\2\2\2\20\u008a\3\2\2\2\22\u0091\3\2\2\2\24"+
		"\u0094\3\2\2\2\26\u0096\3\2\2\2\30\u009f\3\2\2\2\32\u00a4\3\2\2\2\34\u00a9"+
		"\3\2\2\2\36\37\7\3\2\2\37 \7$\2\2 $\7\4\2\2!#\5\4\3\2\"!\3\2\2\2#&\3\2"+
		"\2\2$\"\3\2\2\2$%\3\2\2\2%*\3\2\2\2&$\3\2\2\2\')\5\20\t\2(\'\3\2\2\2)"+
		",\3\2\2\2*(\3\2\2\2*+\3\2\2\2+-\3\2\2\2,*\3\2\2\2-.\7\5\2\2./\7\2\2\3"+
		"/\3\3\2\2\2\60\61\5\6\4\2\61\62\7$\2\2\62\66\7\4\2\2\63\65\5\b\5\2\64"+
		"\63\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\679\3\2\2\28\66\3"+
		"\2\2\29:\7\5\2\2:\5\3\2\2\2;<\t\2\2\2<\7\3\2\2\2=>\7\b\2\2>@\7$\2\2?A"+
		"\5\n\6\2@?\3\2\2\2@A\3\2\2\2AE\3\2\2\2BD\5\16\b\2CB\3\2\2\2DG\3\2\2\2"+
		"EC\3\2\2\2EF\3\2\2\2FK\3\2\2\2GE\3\2\2\2HJ\5\34\17\2IH\3\2\2\2JM\3\2\2"+
		"\2KI\3\2\2\2KL\3\2\2\2Ls\3\2\2\2MK\3\2\2\2NO\7\t\2\2OS\7$\2\2PR\5\16\b"+
		"\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TY\3\2\2\2US\3\2\2\2VX\5\32"+
		"\16\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Zs\3\2\2\2[Y\3\2\2\2\\]\7"+
		"\n\2\2]a\7$\2\2^`\5\16\b\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2\2bs\3"+
		"\2\2\2ca\3\2\2\2de\7\13\2\2ei\7$\2\2fh\5\16\b\2gf\3\2\2\2hk\3\2\2\2ig"+
		"\3\2\2\2ij\3\2\2\2js\3\2\2\2ki\3\2\2\2lm\7$\2\2mn\7\f\2\2ns\7$\2\2op\7"+
		"$\2\2pq\7\r\2\2qs\7$\2\2r=\3\2\2\2rN\3\2\2\2r\\\3\2\2\2rd\3\2\2\2rl\3"+
		"\2\2\2ro\3\2\2\2s\t\3\2\2\2tu\7\16\2\2uv\5\f\7\2v\13\3\2\2\2wx\t\3\2\2"+
		"x\r\3\2\2\2yz\7\22\2\2z\u0089\7$\2\2{|\7\22\2\2|}\7\23\2\2}\u0089\7$\2"+
		"\2~\177\7\22\2\2\177\u0080\5\30\r\2\u0080\u0081\7$\2\2\u0081\u0089\3\2"+
		"\2\2\u0082\u0083\7\22\2\2\u0083\u0084\7\24\2\2\u0084\u0089\7$\2\2\u0085"+
		"\u0086\7\22\2\2\u0086\u0087\7\25\2\2\u0087\u0089\7$\2\2\u0088y\3\2\2\2"+
		"\u0088{\3\2\2\2\u0088~\3\2\2\2\u0088\u0082\3\2\2\2\u0088\u0085\3\2\2\2"+
		"\u0089\17\3\2\2\2\u008a\u008b\7\26\2\2\u008b\u008c\5\26\f\2\u008c\u008d"+
		"\7\27\2\2\u008d\u008e\5\22\n\2\u008e\u008f\7\27\2\2\u008f\u0090\5\26\f"+
		"\2\u0090\21\3\2\2\2\u0091\u0092\5\24\13\2\u0092\u0093\7$\2\2\u0093\23"+
		"\3\2\2\2\u0094\u0095\t\4\2\2\u0095\25\3\2\2\2\u0096\u0099\7$\2\2\u0097"+
		"\u0098\7\30\2\2\u0098\u009a\7$\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2"+
		"\2\2\u009a\27\3\2\2\2\u009b\u00a0\7\31\2\2\u009c\u00a0\7\32\2\2\u009d"+
		"\u00a0\7\33\2\2\u009e\u00a0\7\34\2\2\u009f\u009b\3\2\2\2\u009f\u009c\3"+
		"\2\2\2\u009f\u009d\3\2\2\2\u009f\u009e\3\2\2\2\u00a0\31\3\2\2\2\u00a1"+
		"\u00a2\t\5\2\2\u00a2\u00a5\7#\2\2\u00a3\u00a5\7\"\2\2\u00a4\u00a1\3\2"+
		"\2\2\u00a4\u00a3\3\2\2\2\u00a5\33\3\2\2\2\u00a6\u00a7\t\6\2\2\u00a7\u00aa"+
		"\7#\2\2\u00a8\u00aa\7\"\2\2\u00a9\u00a6\3\2\2\2\u00a9\u00a8\3\2\2\2\u00aa"+
		"\35\3\2\2\2\22$*\66@EKSYair\u0088\u0099\u009f\u00a4\u00a9";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}