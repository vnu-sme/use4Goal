// Generated from IStarScenario.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.iscn.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IStarScenarioParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, STRING=22, IDENT=23, WS=24, LINE_COMMENT=25, 
		BLOCK_COMMENT=26;
	public static final int
		RULE_scenario = 0, RULE_instanceDecl = 1, RULE_stmt = 2, RULE_fireStmt = 3, 
		RULE_assignStmt = 4, RULE_statusValue = 5, RULE_qualifiedId = 6, RULE_aggregateStmt = 7, 
		RULE_aggMode = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"scenario", "instanceDecl", "stmt", "fireStmt", "assignStmt", "statusValue", 
			"qualifiedId", "aggregateStmt", "aggMode"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'scenario'", "'for'", "'{'", "'}'", "'instance'", "','", "':'", 
			"';'", "'fire'", "'assign'", "'='", "'Fulfilled'", "'Pending'", "'True'", 
			"'False'", "'.'", "'aggregate'", "'of'", "'over'", "'all'", "'any'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "STRING", 
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
	public String getGrammarFileName() { return "IStarScenario.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public IStarScenarioParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ScenarioContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(IStarScenarioParser.IDENT, 0); }
		public TerminalNode STRING() { return getToken(IStarScenarioParser.STRING, 0); }
		public TerminalNode EOF() { return getToken(IStarScenarioParser.EOF, 0); }
		public List<InstanceDeclContext> instanceDecl() {
			return getRuleContexts(InstanceDeclContext.class);
		}
		public InstanceDeclContext instanceDecl(int i) {
			return getRuleContext(InstanceDeclContext.class,i);
		}
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
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterScenario(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitScenario(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitScenario(this);
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
			setState(18);
			match(T__0);
			setState(19);
			match(IDENT);
			setState(20);
			match(T__1);
			setState(21);
			match(STRING);
			setState(22);
			match(T__2);
			setState(26);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(23);
				instanceDecl();
				}
				}
				setState(28);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(32);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__16))) != 0)) {
				{
				{
				setState(29);
				stmt();
				}
				}
				setState(34);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(35);
			match(T__3);
			setState(36);
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

	public static class InstanceDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarScenarioParser.IDENT, i);
		}
		public InstanceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instanceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterInstanceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitInstanceDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitInstanceDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstanceDeclContext instanceDecl() throws RecognitionException {
		InstanceDeclContext _localctx = new InstanceDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_instanceDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38);
			match(T__4);
			setState(39);
			match(IDENT);
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(40);
				match(T__5);
				setState(41);
				match(IDENT);
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47);
			match(T__6);
			setState(48);
			match(IDENT);
			setState(49);
			match(T__7);
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
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterStmtFire(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitStmtFire(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitStmtFire(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtAggregateContext extends StmtContext {
		public AggregateStmtContext aggregateStmt() {
			return getRuleContext(AggregateStmtContext.class,0);
		}
		public StmtAggregateContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterStmtAggregate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitStmtAggregate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitStmtAggregate(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StmtAssignContext extends StmtContext {
		public AssignStmtContext assignStmt() {
			return getRuleContext(AssignStmtContext.class,0);
		}
		public StmtAssignContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterStmtAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitStmtAssign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitStmtAssign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_stmt);
		try {
			setState(54);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				_localctx = new StmtFireContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(51);
				fireStmt();
				}
				break;
			case T__9:
				_localctx = new StmtAssignContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(52);
				assignStmt();
				}
				break;
			case T__16:
				_localctx = new StmtAggregateContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(53);
				aggregateStmt();
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

	public static class FireStmtContext extends ParserRuleContext {
		public QualifiedIdContext qualifiedId() {
			return getRuleContext(QualifiedIdContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(IStarScenarioParser.IDENT, 0); }
		public FireStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fireStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterFireStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitFireStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitFireStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FireStmtContext fireStmt() throws RecognitionException {
		FireStmtContext _localctx = new FireStmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_fireStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(T__8);
			setState(57);
			qualifiedId();
			setState(60);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(58);
				match(T__1);
				setState(59);
				match(IDENT);
				}
			}

			setState(62);
			match(T__7);
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

	public static class AssignStmtContext extends ParserRuleContext {
		public QualifiedIdContext qualifiedId() {
			return getRuleContext(QualifiedIdContext.class,0);
		}
		public StatusValueContext statusValue() {
			return getRuleContext(StatusValueContext.class,0);
		}
		public AssignStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterAssignStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitAssignStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitAssignStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignStmtContext assignStmt() throws RecognitionException {
		AssignStmtContext _localctx = new AssignStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_assignStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(T__9);
			setState(65);
			qualifiedId();
			setState(66);
			match(T__10);
			setState(67);
			statusValue();
			setState(68);
			match(T__7);
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

	public static class StatusValueContext extends ParserRuleContext {
		public StatusValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statusValue; }
	 
		public StatusValueContext() { }
		public void copyFrom(StatusValueContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SvFalseContext extends StatusValueContext {
		public SvFalseContext(StatusValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterSvFalse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitSvFalse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitSvFalse(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SvFulfilledContext extends StatusValueContext {
		public SvFulfilledContext(StatusValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterSvFulfilled(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitSvFulfilled(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitSvFulfilled(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SvPendingContext extends StatusValueContext {
		public SvPendingContext(StatusValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterSvPending(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitSvPending(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitSvPending(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SvTrueContext extends StatusValueContext {
		public SvTrueContext(StatusValueContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterSvTrue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitSvTrue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitSvTrue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatusValueContext statusValue() throws RecognitionException {
		StatusValueContext _localctx = new StatusValueContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_statusValue);
		try {
			setState(74);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				_localctx = new SvFulfilledContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(70);
				match(T__11);
				}
				break;
			case T__12:
				_localctx = new SvPendingContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(71);
				match(T__12);
				}
				break;
			case T__13:
				_localctx = new SvTrueContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(72);
				match(T__13);
				}
				break;
			case T__14:
				_localctx = new SvFalseContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(73);
				match(T__14);
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

	public static class QualifiedIdContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarScenarioParser.IDENT, i);
		}
		public QualifiedIdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedId; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterQualifiedId(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitQualifiedId(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitQualifiedId(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedIdContext qualifiedId() throws RecognitionException {
		QualifiedIdContext _localctx = new QualifiedIdContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_qualifiedId);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(76);
				match(IDENT);
				setState(77);
				match(T__15);
				}
				break;
			}
			setState(80);
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

	public static class AggregateStmtContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(IStarScenarioParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(IStarScenarioParser.IDENT, i);
		}
		public AggModeContext aggMode() {
			return getRuleContext(AggModeContext.class,0);
		}
		public AggregateStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateStmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterAggregateStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitAggregateStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitAggregateStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateStmtContext aggregateStmt() throws RecognitionException {
		AggregateStmtContext _localctx = new AggregateStmtContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_aggregateStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__16);
			setState(83);
			match(IDENT);
			setState(84);
			match(T__6);
			setState(85);
			aggMode();
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(86);
				match(T__17);
				setState(87);
				match(IDENT);
				}
			}

			setState(90);
			match(T__18);
			setState(91);
			match(IDENT);
			setState(92);
			match(T__7);
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

	public static class AggModeContext extends ParserRuleContext {
		public AggModeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggMode; }
	 
		public AggModeContext() { }
		public void copyFrom(AggModeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AggAllContext extends AggModeContext {
		public AggAllContext(AggModeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterAggAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitAggAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitAggAll(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AggAnyContext extends AggModeContext {
		public AggAnyContext(AggModeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).enterAggAny(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IStarScenarioListener ) ((IStarScenarioListener)listener).exitAggAny(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IStarScenarioVisitor ) return ((IStarScenarioVisitor<? extends T>)visitor).visitAggAny(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggModeContext aggMode() throws RecognitionException {
		AggModeContext _localctx = new AggModeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_aggMode);
		try {
			setState(96);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				_localctx = new AggAllContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				match(T__19);
				}
				break;
			case T__20:
				_localctx = new AggAnyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(95);
				match(T__20);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34e\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\7\2\33\n\2\f\2\16\2\36\13\2\3\2\7\2!\n\2\f\2\16\2$\13\2\3"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3-\n\3\f\3\16\3\60\13\3\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\5\49\n\4\3\5\3\5\3\5\3\5\5\5?\n\5\3\5\3\5\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\5\7M\n\7\3\b\3\b\5\bQ\n\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\5\t[\n\t\3\t\3\t\3\t\3\t\3\n\3\n\5\nc\n\n\3\n\2\2\13\2\4\6"+
		"\b\n\f\16\20\22\2\2\2g\2\24\3\2\2\2\4(\3\2\2\2\68\3\2\2\2\b:\3\2\2\2\n"+
		"B\3\2\2\2\fL\3\2\2\2\16P\3\2\2\2\20T\3\2\2\2\22b\3\2\2\2\24\25\7\3\2\2"+
		"\25\26\7\31\2\2\26\27\7\4\2\2\27\30\7\30\2\2\30\34\7\5\2\2\31\33\5\4\3"+
		"\2\32\31\3\2\2\2\33\36\3\2\2\2\34\32\3\2\2\2\34\35\3\2\2\2\35\"\3\2\2"+
		"\2\36\34\3\2\2\2\37!\5\6\4\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2"+
		"\2#%\3\2\2\2$\"\3\2\2\2%&\7\6\2\2&\'\7\2\2\3\'\3\3\2\2\2()\7\7\2\2).\7"+
		"\31\2\2*+\7\b\2\2+-\7\31\2\2,*\3\2\2\2-\60\3\2\2\2.,\3\2\2\2./\3\2\2\2"+
		"/\61\3\2\2\2\60.\3\2\2\2\61\62\7\t\2\2\62\63\7\31\2\2\63\64\7\n\2\2\64"+
		"\5\3\2\2\2\659\5\b\5\2\669\5\n\6\2\679\5\20\t\28\65\3\2\2\28\66\3\2\2"+
		"\28\67\3\2\2\29\7\3\2\2\2:;\7\13\2\2;>\5\16\b\2<=\7\4\2\2=?\7\31\2\2>"+
		"<\3\2\2\2>?\3\2\2\2?@\3\2\2\2@A\7\n\2\2A\t\3\2\2\2BC\7\f\2\2CD\5\16\b"+
		"\2DE\7\r\2\2EF\5\f\7\2FG\7\n\2\2G\13\3\2\2\2HM\7\16\2\2IM\7\17\2\2JM\7"+
		"\20\2\2KM\7\21\2\2LH\3\2\2\2LI\3\2\2\2LJ\3\2\2\2LK\3\2\2\2M\r\3\2\2\2"+
		"NO\7\31\2\2OQ\7\22\2\2PN\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RS\7\31\2\2S\17\3"+
		"\2\2\2TU\7\23\2\2UV\7\31\2\2VW\7\t\2\2WZ\5\22\n\2XY\7\24\2\2Y[\7\31\2"+
		"\2ZX\3\2\2\2Z[\3\2\2\2[\\\3\2\2\2\\]\7\25\2\2]^\7\31\2\2^_\7\n\2\2_\21"+
		"\3\2\2\2`c\7\26\2\2ac\7\27\2\2b`\3\2\2\2ba\3\2\2\2c\23\3\2\2\2\13\34\""+
		".8>LPZb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}