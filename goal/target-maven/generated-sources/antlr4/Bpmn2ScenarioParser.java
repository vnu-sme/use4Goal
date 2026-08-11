// Generated from Bpmn2Scenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.bpmn2scenario.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Bpmn2ScenarioParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, STRING=33, NUMBER=34, IDENT=35, WS=36, LINE_COMMENT=37, BLOCK_COMMENT=38;
	public static final int
		RULE_scenario = 0, RULE_stmt = 1, RULE_processDecl = 2, RULE_actorDecl = 3, 
		RULE_bindStmt = 4, RULE_fireStmt = 5, RULE_completedStmt = 6, RULE_activeStmt = 7, 
		RULE_tokenStmt = 8, RULE_valueStmt = 9, RULE_assertStmt = 10, RULE_forClause = 11, 
		RULE_byClause = 12, RULE_qualifiedId = 13, RULE_ref = 14, RULE_value = 15, 
		RULE_listValue = 16, RULE_expr = 17, RULE_compOp = 18;
	private static String[] makeRuleNames() {
		return new String[] {
			"scenario", "stmt", "processDecl", "actorDecl", "bindStmt", "fireStmt", 
			"completedStmt", "activeStmt", "tokenStmt", "valueStmt", "assertStmt", 
			"forClause", "byClause", "qualifiedId", "ref", "value", "listValue", 
			"expr", "compOp"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'scenario'", "'for'", "'{'", "'}'", "'process'", "':'", "';'", 
			"'actor'", "','", "'bind'", "'='", "'fire'", "'completed'", "'active'", 
			"'token'", "'.'", "'::'", "'value'", "'assert'", "'by'", "'['", "']'", 
			"'count'", "'('", "'where'", "')'", "'=='", "'!='", "'>='", "'<='", "'>'", 
			"'<'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "STRING", "NUMBER", 
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
	public String getGrammarFileName() { return "Bpmn2Scenario.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Bpmn2ScenarioParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ScenarioContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2ScenarioParser.IDENT, 0); }
		public TerminalNode STRING() { return getToken(Bpmn2ScenarioParser.STRING, 0); }
		public TerminalNode EOF() { return getToken(Bpmn2ScenarioParser.EOF, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public ScenarioContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scenario; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterScenario(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitScenario(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitScenario(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScenarioContext scenario() throws RecognitionException {
		ScenarioContext _localctx = new ScenarioContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_scenario);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(T__0);
			setState(39);
			match(IDENT);
			setState(40);
			match(T__1);
			setState(41);
			match(STRING);
			setState(42);
			match(T__2);
			setState(46);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__7) | (1L << T__9) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__17) | (1L << T__18))) != 0)) {
				{
				{
				setState(43);
				stmt();
				}
				}
				setState(48);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(49);
			match(T__3);
			setState(50);
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

	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class StmtFireContext extends StmtContext {
		public FireStmtContext fireStmt() {
			return getRuleContext(FireStmtContext.class,0);
		}
		public StmtFireContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtFire(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtFire(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtFire(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtValueContext extends StmtContext {
		public ValueStmtContext valueStmt() {
			return getRuleContext(ValueStmtContext.class,0);
		}
		public StmtValueContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtValue(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtTokenContext extends StmtContext {
		public TokenStmtContext tokenStmt() {
			return getRuleContext(TokenStmtContext.class,0);
		}
		public StmtTokenContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtToken(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtToken(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtToken(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtProcessContext extends StmtContext {
		public ProcessDeclContext processDecl() {
			return getRuleContext(ProcessDeclContext.class,0);
		}
		public StmtProcessContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtProcess(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtProcess(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtProcess(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtCompletedContext extends StmtContext {
		public CompletedStmtContext completedStmt() {
			return getRuleContext(CompletedStmtContext.class,0);
		}
		public StmtCompletedContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtCompleted(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtCompleted(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtCompleted(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtAssertContext extends StmtContext {
		public AssertStmtContext assertStmt() {
			return getRuleContext(AssertStmtContext.class,0);
		}
		public StmtAssertContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtAssert(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtAssert(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtAssert(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtBindContext extends StmtContext {
		public BindStmtContext bindStmt() {
			return getRuleContext(BindStmtContext.class,0);
		}
		public StmtBindContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtBind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtBind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtBind(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtActiveContext extends StmtContext {
		public ActiveStmtContext activeStmt() {
			return getRuleContext(ActiveStmtContext.class,0);
		}
		public StmtActiveContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtActive(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtActive(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtActive(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtActorContext extends StmtContext {
		public ActorDeclContext actorDecl() {
			return getRuleContext(ActorDeclContext.class,0);
		}
		public StmtActorContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterStmtActor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitStmtActor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitStmtActor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stmt);
		try {
			setState(61);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__4:
				_localctx = new StmtProcessContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(52);
				processDecl();
				}
				break;
			case T__7:
				_localctx = new StmtActorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(53);
				actorDecl();
				}
				break;
			case T__9:
				_localctx = new StmtBindContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(54);
				bindStmt();
				}
				break;
			case T__11:
				_localctx = new StmtFireContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(55);
				fireStmt();
				}
				break;
			case T__12:
				_localctx = new StmtCompletedContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(56);
				completedStmt();
				}
				break;
			case T__13:
				_localctx = new StmtActiveContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(57);
				activeStmt();
				}
				break;
			case T__14:
				_localctx = new StmtTokenContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(58);
				tokenStmt();
				}
				break;
			case T__17:
				_localctx = new StmtValueContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(59);
				valueStmt();
				}
				break;
			case T__18:
				_localctx = new StmtAssertContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(60);
				assertStmt();
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

	public static class ProcessDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public ProcessDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_processDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterProcessDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitProcessDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitProcessDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProcessDeclContext processDecl() throws RecognitionException {
		ProcessDeclContext _localctx = new ProcessDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_processDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(T__4);
			setState(64);
			match(IDENT);
			setState(65);
			match(T__5);
			setState(66);
			match(IDENT);
			setState(67);
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

	public static class ActorDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public ActorDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actorDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterActorDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitActorDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitActorDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActorDeclContext actorDecl() throws RecognitionException {
		ActorDeclContext _localctx = new ActorDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_actorDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(T__7);
			setState(70);
			match(IDENT);
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(71);
				match(T__8);
				setState(72);
				match(IDENT);
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(78);
			match(T__5);
			setState(79);
			match(IDENT);
			setState(80);
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

	public static class BindStmtContext extends ParserRuleContext {
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public BindStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bindStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterBindStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitBindStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitBindStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BindStmtContext bindStmt() throws RecognitionException {
		BindStmtContext _localctx = new BindStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_bindStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__9);
			setState(83);
			ref();
			setState(84);
			match(T__10);
			setState(85);
			value();
			setState(86);
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

	public static class FireStmtContext extends ParserRuleContext {
		public QualifiedIdContext qualifiedId() {
			return getRuleContext(QualifiedIdContext.class,0);
		}
		public ForClauseContext forClause() {
			return getRuleContext(ForClauseContext.class,0);
		}
		public ByClauseContext byClause() {
			return getRuleContext(ByClauseContext.class,0);
		}
		public FireStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fireStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterFireStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitFireStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitFireStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FireStmtContext fireStmt() throws RecognitionException {
		FireStmtContext _localctx = new FireStmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_fireStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(T__11);
			setState(89);
			qualifiedId();
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(90);
				forClause();
				}
			}

			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__19) {
				{
				setState(93);
				byClause();
				}
			}

			setState(96);
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

	public static class CompletedStmtContext extends ParserRuleContext {
		public QualifiedIdContext qualifiedId() {
			return getRuleContext(QualifiedIdContext.class,0);
		}
		public ForClauseContext forClause() {
			return getRuleContext(ForClauseContext.class,0);
		}
		public ByClauseContext byClause() {
			return getRuleContext(ByClauseContext.class,0);
		}
		public CompletedStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_completedStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterCompletedStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitCompletedStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitCompletedStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompletedStmtContext completedStmt() throws RecognitionException {
		CompletedStmtContext _localctx = new CompletedStmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_completedStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(T__12);
			setState(99);
			qualifiedId();
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(100);
				forClause();
				}
			}

			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__19) {
				{
				setState(103);
				byClause();
				}
			}

			setState(106);
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

	public static class ActiveStmtContext extends ParserRuleContext {
		public QualifiedIdContext qualifiedId() {
			return getRuleContext(QualifiedIdContext.class,0);
		}
		public ForClauseContext forClause() {
			return getRuleContext(ForClauseContext.class,0);
		}
		public ByClauseContext byClause() {
			return getRuleContext(ByClauseContext.class,0);
		}
		public ActiveStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_activeStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterActiveStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitActiveStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitActiveStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActiveStmtContext activeStmt() throws RecognitionException {
		ActiveStmtContext _localctx = new ActiveStmtContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_activeStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			match(T__13);
			setState(109);
			qualifiedId();
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(110);
				forClause();
				}
			}

			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__19) {
				{
				setState(113);
				byClause();
				}
			}

			setState(116);
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

	public static class TokenStmtContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public ForClauseContext forClause() {
			return getRuleContext(ForClauseContext.class,0);
		}
		public TokenStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tokenStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterTokenStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitTokenStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitTokenStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TokenStmtContext tokenStmt() throws RecognitionException {
		TokenStmtContext _localctx = new TokenStmtContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_tokenStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(T__14);
			setState(119);
			match(IDENT);
			setState(120);
			match(T__15);
			setState(121);
			match(IDENT);
			setState(122);
			match(T__16);
			setState(123);
			match(IDENT);
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(124);
				forClause();
				}
			}

			setState(127);
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

	public static class ValueStmtContext extends ParserRuleContext {
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public ValueStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_valueStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterValueStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitValueStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitValueStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueStmtContext valueStmt() throws RecognitionException {
		ValueStmtContext _localctx = new ValueStmtContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_valueStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(T__17);
			setState(130);
			ref();
			setState(131);
			match(T__10);
			setState(132);
			value();
			setState(133);
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

	public static class AssertStmtContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssertStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assertStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterAssertStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitAssertStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitAssertStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssertStmtContext assertStmt() throws RecognitionException {
		AssertStmtContext _localctx = new AssertStmtContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_assertStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(T__18);
			setState(136);
			expr();
			setState(137);
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

	public static class ForClauseContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2ScenarioParser.IDENT, 0); }
		public ForClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterForClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitForClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitForClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForClauseContext forClause() throws RecognitionException {
		ForClauseContext _localctx = new ForClauseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_forClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(T__1);
			setState(140);
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

	public static class ByClauseContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(Bpmn2ScenarioParser.IDENT, 0); }
		public ByClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_byClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterByClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitByClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitByClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ByClauseContext byClause() throws RecognitionException {
		ByClauseContext _localctx = new ByClauseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_byClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			match(T__19);
			setState(143);
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

	public static class QualifiedIdContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public QualifiedIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterQualifiedId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitQualifiedId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitQualifiedId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedIdContext qualifiedId() throws RecognitionException {
		QualifiedIdContext _localctx = new QualifiedIdContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_qualifiedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(IDENT);
			setState(146);
			match(T__15);
			setState(147);
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

	public static class RefContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public RefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ref; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RefContext ref() throws RecognitionException {
		RefContext _localctx = new RefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_ref);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(IDENT);
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__15) {
				{
				{
				setState(150);
				match(T__15);
				setState(151);
				match(IDENT);
				}
				}
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
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

	public static class ValueContext extends ParserRuleContext {
		public ListValueContext listValue() {
			return getRuleContext(ListValueContext.class,0);
		}
		public TerminalNode STRING() { return getToken(Bpmn2ScenarioParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(Bpmn2ScenarioParser.NUMBER, 0); }
		public TerminalNode IDENT() { return getToken(Bpmn2ScenarioParser.IDENT, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_value);
		try {
			setState(161);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__20:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				listValue();
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				match(STRING);
				}
				break;
			case NUMBER:
				enterOuterAlt(_localctx, 3);
				{
				setState(159);
				match(NUMBER);
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 4);
				{
				setState(160);
				match(IDENT);
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

	public static class ListValueContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public ListValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterListValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitListValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitListValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListValueContext listValue() throws RecognitionException {
		ListValueContext _localctx = new ListValueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_listValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			match(T__20);
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENT) {
				{
				setState(164);
				match(IDENT);
				setState(169);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(165);
					match(T__8);
					setState(166);
					match(IDENT);
					}
					}
					setState(171);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(174);
			match(T__21);
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

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CountExprContext extends ExprContext {
		public List<TerminalNode> IDENT() { return getTokens(Bpmn2ScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(Bpmn2ScenarioParser.IDENT, i);
		}
		public CompOpContext compOp() {
			return getRuleContext(CompOpContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(Bpmn2ScenarioParser.NUMBER, 0); }
		public CountExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterCountExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitCountExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitCountExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CompareExprContext extends ExprContext {
		public RefContext ref() {
			return getRuleContext(RefContext.class,0);
		}
		public CompOpContext compOp() {
			return getRuleContext(CompOpContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public CompareExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterCompareExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitCompareExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitCompareExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_expr);
		try {
			setState(191);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__22:
				_localctx = new CountExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(176);
				match(T__22);
				setState(177);
				match(T__23);
				setState(178);
				match(IDENT);
				setState(179);
				match(T__24);
				setState(180);
				match(IDENT);
				setState(181);
				match(T__10);
				setState(182);
				match(IDENT);
				setState(183);
				match(T__25);
				setState(184);
				compOp();
				setState(185);
				match(NUMBER);
				}
				break;
			case IDENT:
				_localctx = new CompareExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				ref();
				setState(188);
				compOp();
				setState(189);
				value();
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

	public static class CompOpContext extends ParserRuleContext {
		public CompOpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compOp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).enterCompOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Bpmn2ScenarioListener ) ((Bpmn2ScenarioListener)listener).exitCompOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Bpmn2ScenarioVisitor ) return ((Bpmn2ScenarioVisitor<? extends T>)visitor).visitCompOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompOpContext compOp() throws RecognitionException {
		CompOpContext _localctx = new CompOpContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_compOp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__26) | (1L << T__27) | (1L << T__28) | (1L << T__29) | (1L << T__30) | (1L << T__31))) != 0)) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3(\u00c6\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\3\2\3\2\3\2\3\2\3\2\3\2\7\2/\n\2\f\2\16\2\62\13\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3@\n\3\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5L\n\5\f\5\16\5O\13\5\3\5\3\5\3\5\3\5\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\5\7^\n\7\3\7\5\7a\n\7\3\7\3\7\3\b\3"+
		"\b\3\b\5\bh\n\b\3\b\5\bk\n\b\3\b\3\b\3\t\3\t\3\t\5\tr\n\t\3\t\5\tu\n\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0080\n\n\3\n\3\n\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\7\20\u009b\n\20\f\20\16\20\u009e\13\20\3\21"+
		"\3\21\3\21\3\21\5\21\u00a4\n\21\3\22\3\22\3\22\3\22\7\22\u00aa\n\22\f"+
		"\22\16\22\u00ad\13\22\5\22\u00af\n\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00c2\n\23"+
		"\3\24\3\24\3\24\2\2\25\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&\2\3"+
		"\3\2\35\"\2\u00ca\2(\3\2\2\2\4?\3\2\2\2\6A\3\2\2\2\bG\3\2\2\2\nT\3\2\2"+
		"\2\fZ\3\2\2\2\16d\3\2\2\2\20n\3\2\2\2\22x\3\2\2\2\24\u0083\3\2\2\2\26"+
		"\u0089\3\2\2\2\30\u008d\3\2\2\2\32\u0090\3\2\2\2\34\u0093\3\2\2\2\36\u0097"+
		"\3\2\2\2 \u00a3\3\2\2\2\"\u00a5\3\2\2\2$\u00c1\3\2\2\2&\u00c3\3\2\2\2"+
		"()\7\3\2\2)*\7%\2\2*+\7\4\2\2+,\7#\2\2,\60\7\5\2\2-/\5\4\3\2.-\3\2\2\2"+
		"/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62\60\3\2\2\2\63"+
		"\64\7\6\2\2\64\65\7\2\2\3\65\3\3\2\2\2\66@\5\6\4\2\67@\5\b\5\28@\5\n\6"+
		"\29@\5\f\7\2:@\5\16\b\2;@\5\20\t\2<@\5\22\n\2=@\5\24\13\2>@\5\26\f\2?"+
		"\66\3\2\2\2?\67\3\2\2\2?8\3\2\2\2?9\3\2\2\2?:\3\2\2\2?;\3\2\2\2?<\3\2"+
		"\2\2?=\3\2\2\2?>\3\2\2\2@\5\3\2\2\2AB\7\7\2\2BC\7%\2\2CD\7\b\2\2DE\7%"+
		"\2\2EF\7\t\2\2F\7\3\2\2\2GH\7\n\2\2HM\7%\2\2IJ\7\13\2\2JL\7%\2\2KI\3\2"+
		"\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NP\3\2\2\2OM\3\2\2\2PQ\7\b\2\2QR\7%"+
		"\2\2RS\7\t\2\2S\t\3\2\2\2TU\7\f\2\2UV\5\36\20\2VW\7\r\2\2WX\5 \21\2XY"+
		"\7\t\2\2Y\13\3\2\2\2Z[\7\16\2\2[]\5\34\17\2\\^\5\30\r\2]\\\3\2\2\2]^\3"+
		"\2\2\2^`\3\2\2\2_a\5\32\16\2`_\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\7\t\2\2c"+
		"\r\3\2\2\2de\7\17\2\2eg\5\34\17\2fh\5\30\r\2gf\3\2\2\2gh\3\2\2\2hj\3\2"+
		"\2\2ik\5\32\16\2ji\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\7\t\2\2m\17\3\2\2\2n"+
		"o\7\20\2\2oq\5\34\17\2pr\5\30\r\2qp\3\2\2\2qr\3\2\2\2rt\3\2\2\2su\5\32"+
		"\16\2ts\3\2\2\2tu\3\2\2\2uv\3\2\2\2vw\7\t\2\2w\21\3\2\2\2xy\7\21\2\2y"+
		"z\7%\2\2z{\7\22\2\2{|\7%\2\2|}\7\23\2\2}\177\7%\2\2~\u0080\5\30\r\2\177"+
		"~\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082\7\t\2\2\u0082"+
		"\23\3\2\2\2\u0083\u0084\7\24\2\2\u0084\u0085\5\36\20\2\u0085\u0086\7\r"+
		"\2\2\u0086\u0087\5 \21\2\u0087\u0088\7\t\2\2\u0088\25\3\2\2\2\u0089\u008a"+
		"\7\25\2\2\u008a\u008b\5$\23\2\u008b\u008c\7\t\2\2\u008c\27\3\2\2\2\u008d"+
		"\u008e\7\4\2\2\u008e\u008f\7%\2\2\u008f\31\3\2\2\2\u0090\u0091\7\26\2"+
		"\2\u0091\u0092\7%\2\2\u0092\33\3\2\2\2\u0093\u0094\7%\2\2\u0094\u0095"+
		"\7\22\2\2\u0095\u0096\7%\2\2\u0096\35\3\2\2\2\u0097\u009c\7%\2\2\u0098"+
		"\u0099\7\22\2\2\u0099\u009b\7%\2\2\u009a\u0098\3\2\2\2\u009b\u009e\3\2"+
		"\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\37\3\2\2\2\u009e\u009c"+
		"\3\2\2\2\u009f\u00a4\5\"\22\2\u00a0\u00a4\7#\2\2\u00a1\u00a4\7$\2\2\u00a2"+
		"\u00a4\7%\2\2\u00a3\u009f\3\2\2\2\u00a3\u00a0\3\2\2\2\u00a3\u00a1\3\2"+
		"\2\2\u00a3\u00a2\3\2\2\2\u00a4!\3\2\2\2\u00a5\u00ae\7\27\2\2\u00a6\u00ab"+
		"\7%\2\2\u00a7\u00a8\7\13\2\2\u00a8\u00aa\7%\2\2\u00a9\u00a7\3\2\2\2\u00aa"+
		"\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00af\3\2"+
		"\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00a6\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\u00b1\7\30\2\2\u00b1#\3\2\2\2\u00b2\u00b3\7\31\2"+
		"\2\u00b3\u00b4\7\32\2\2\u00b4\u00b5\7%\2\2\u00b5\u00b6\7\33\2\2\u00b6"+
		"\u00b7\7%\2\2\u00b7\u00b8\7\r\2\2\u00b8\u00b9\7%\2\2\u00b9\u00ba\7\34"+
		"\2\2\u00ba\u00bb\5&\24\2\u00bb\u00bc\7$\2\2\u00bc\u00c2\3\2\2\2\u00bd"+
		"\u00be\5\36\20\2\u00be\u00bf\5&\24\2\u00bf\u00c0\5 \21\2\u00c0\u00c2\3"+
		"\2\2\2\u00c1\u00b2\3\2\2\2\u00c1\u00bd\3\2\2\2\u00c2%\3\2\2\2\u00c3\u00c4"+
		"\t\2\2\2\u00c4\'\3\2\2\2\21\60?M]`gjqt\177\u009c\u00a3\u00ab\u00ae\u00c1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}