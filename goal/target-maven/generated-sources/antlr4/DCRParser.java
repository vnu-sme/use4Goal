// Generated from DCR.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dcr.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DCRParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, IDENT=21, INT=22, STRING=23, WS=24, LINE_COMMENT=25, 
		BLOCK_COMMENT=26;
	public static final int
		RULE_model = 0, RULE_statement = 1, RULE_markItem = 2, RULE_relKind = 3, 
		RULE_relTime = 4, RULE_deadline = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "statement", "markItem", "relKind", "relTime", "deadline"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'dcr'", "'{'", "'}'", "'event'", "';'", "'marking'", "':'", "','", 
			"'->'", "'executed'", "'included'", "'pending'", "'condition'", "'response'", 
			"'include'", "'exclude'", "'milestone'", "'after'", "'within'", "'omega'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "IDENT", "INT", 
			"STRING", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
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
	public String getGrammarFileName() { return "DCR.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DCRParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(DCRParser.IDENT, 0); }
		public TerminalNode EOF() { return getToken(DCRParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitModel(this);
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
			setState(12);
			match(T__0);
			setState(13);
			match(IDENT);
			setState(14);
			match(T__1);
			setState(18);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__5) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16))) != 0)) {
				{
				{
				setState(15);
				statement();
				}
				}
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(21);
			match(T__2);
			setState(22);
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

	public static class StatementContext extends ParserRuleContext {
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	 
		public StatementContext() { }
		public void copyFrom(StatementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelationStmtContext extends StatementContext {
		public RelKindContext relKind() {
			return getRuleContext(RelKindContext.class,0);
		}
		public List<TerminalNode> IDENT() { return getTokens(DCRParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(DCRParser.IDENT, i);
		}
		public RelTimeContext relTime() {
			return getRuleContext(RelTimeContext.class,0);
		}
		public RelationStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelationStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelationStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelationStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MarkingStmtContext extends StatementContext {
		public TerminalNode IDENT() { return getToken(DCRParser.IDENT, 0); }
		public List<MarkItemContext> markItem() {
			return getRuleContexts(MarkItemContext.class);
		}
		public MarkItemContext markItem(int i) {
			return getRuleContext(MarkItemContext.class,i);
		}
		public MarkingStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterMarkingStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitMarkingStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitMarkingStmt(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EventStmtContext extends StatementContext {
		public TerminalNode IDENT() { return getToken(DCRParser.IDENT, 0); }
		public TerminalNode STRING() { return getToken(DCRParser.STRING, 0); }
		public EventStmtContext(StatementContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterEventStmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitEventStmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitEventStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		int _la;
		try {
			setState(53);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				_localctx = new EventStmtContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(24);
				match(T__3);
				setState(25);
				match(IDENT);
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==STRING) {
					{
					setState(26);
					match(STRING);
					}
				}

				setState(29);
				match(T__4);
				}
				break;
			case T__5:
				_localctx = new MarkingStmtContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(30);
				match(T__5);
				setState(31);
				match(IDENT);
				setState(32);
				match(T__6);
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << T__10) | (1L << T__11))) != 0)) {
					{
					setState(33);
					markItem();
					setState(38);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__7) {
						{
						{
						setState(34);
						match(T__7);
						setState(35);
						markItem();
						}
						}
						setState(40);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(43);
				match(T__4);
				}
				break;
			case T__12:
			case T__13:
			case T__14:
			case T__15:
			case T__16:
				_localctx = new RelationStmtContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(44);
				relKind();
				setState(45);
				match(IDENT);
				setState(47);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__17 || _la==T__18) {
					{
					setState(46);
					relTime();
					}
				}

				setState(49);
				match(T__8);
				setState(50);
				match(IDENT);
				setState(51);
				match(T__4);
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

	public static class MarkItemContext extends ParserRuleContext {
		public MarkItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_markItem; }
	 
		public MarkItemContext() { }
		public void copyFrom(MarkItemContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MarkExecutedContext extends MarkItemContext {
		public TerminalNode INT() { return getToken(DCRParser.INT, 0); }
		public MarkExecutedContext(MarkItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterMarkExecuted(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitMarkExecuted(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitMarkExecuted(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MarkIncludedContext extends MarkItemContext {
		public MarkIncludedContext(MarkItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterMarkIncluded(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitMarkIncluded(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitMarkIncluded(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MarkPendingContext extends MarkItemContext {
		public DeadlineContext deadline() {
			return getRuleContext(DeadlineContext.class,0);
		}
		public MarkPendingContext(MarkItemContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterMarkPending(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitMarkPending(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitMarkPending(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MarkItemContext markItem() throws RecognitionException {
		MarkItemContext _localctx = new MarkItemContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_markItem);
		int _la;
		try {
			setState(64);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__9:
				_localctx = new MarkExecutedContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(55);
				match(T__9);
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==INT) {
					{
					setState(56);
					match(INT);
					}
				}

				}
				break;
			case T__10:
				_localctx = new MarkIncludedContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				match(T__10);
				}
				break;
			case T__11:
				_localctx = new MarkPendingContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(60);
				match(T__11);
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__19 || _la==INT) {
					{
					setState(61);
					deadline();
					}
				}

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

	public static class RelKindContext extends ParserRuleContext {
		public RelKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relKind; }
	 
		public RelKindContext() { }
		public void copyFrom(RelKindContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class RelIncludeContext extends RelKindContext {
		public RelIncludeContext(RelKindContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelInclude(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelInclude(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelInclude(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelResponseContext extends RelKindContext {
		public RelResponseContext(RelKindContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelResponse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelResponse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelResponse(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelExcludeContext extends RelKindContext {
		public RelExcludeContext(RelKindContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelExclude(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelExclude(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelExclude(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelConditionContext extends RelKindContext {
		public RelConditionContext(RelKindContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelCondition(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelMilestoneContext extends RelKindContext {
		public RelMilestoneContext(RelKindContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterRelMilestone(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitRelMilestone(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitRelMilestone(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelKindContext relKind() throws RecognitionException {
		RelKindContext _localctx = new RelKindContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_relKind);
		try {
			setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__12:
				_localctx = new RelConditionContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				match(T__12);
				}
				break;
			case T__13:
				_localctx = new RelResponseContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				match(T__13);
				}
				break;
			case T__14:
				_localctx = new RelIncludeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(68);
				match(T__14);
				}
				break;
			case T__15:
				_localctx = new RelExcludeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(69);
				match(T__15);
				}
				break;
			case T__16:
				_localctx = new RelMilestoneContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(70);
				match(T__16);
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

	public static class RelTimeContext extends ParserRuleContext {
		public RelTimeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relTime; }
	 
		public RelTimeContext() { }
		public void copyFrom(RelTimeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ResponseTimeContext extends RelTimeContext {
		public DeadlineContext deadline() {
			return getRuleContext(DeadlineContext.class,0);
		}
		public ResponseTimeContext(RelTimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterResponseTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitResponseTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitResponseTime(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ConditionTimeContext extends RelTimeContext {
		public TerminalNode INT() { return getToken(DCRParser.INT, 0); }
		public ConditionTimeContext(RelTimeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterConditionTime(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitConditionTime(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitConditionTime(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelTimeContext relTime() throws RecognitionException {
		RelTimeContext _localctx = new RelTimeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_relTime);
		try {
			setState(77);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				_localctx = new ConditionTimeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(73);
				match(T__17);
				setState(74);
				match(INT);
				}
				break;
			case T__18:
				_localctx = new ResponseTimeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				match(T__18);
				setState(76);
				deadline();
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

	public static class DeadlineContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(DCRParser.INT, 0); }
		public DeadlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_deadline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).enterDeadline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DCRListener ) ((DCRListener)listener).exitDeadline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DCRVisitor ) return ((DCRVisitor<? extends T>)visitor).visitDeadline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeadlineContext deadline() throws RecognitionException {
		DeadlineContext _localctx = new DeadlineContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_deadline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_la = _input.LA(1);
			if ( !(_la==T__19 || _la==INT) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\34T\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\7\2\23\n\2\f\2\16\2"+
		"\26\13\2\3\2\3\2\3\2\3\3\3\3\3\3\5\3\36\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\7\3\'\n\3\f\3\16\3*\13\3\5\3,\n\3\3\3\3\3\3\3\3\3\5\3\62\n\3\3\3\3"+
		"\3\3\3\3\3\5\38\n\3\3\4\3\4\5\4<\n\4\3\4\3\4\3\4\5\4A\n\4\5\4C\n\4\3\5"+
		"\3\5\3\5\3\5\3\5\5\5J\n\5\3\6\3\6\3\6\3\6\5\6P\n\6\3\7\3\7\3\7\2\2\b\2"+
		"\4\6\b\n\f\2\3\4\2\26\26\30\30\2]\2\16\3\2\2\2\4\67\3\2\2\2\6B\3\2\2\2"+
		"\bI\3\2\2\2\nO\3\2\2\2\fQ\3\2\2\2\16\17\7\3\2\2\17\20\7\27\2\2\20\24\7"+
		"\4\2\2\21\23\5\4\3\2\22\21\3\2\2\2\23\26\3\2\2\2\24\22\3\2\2\2\24\25\3"+
		"\2\2\2\25\27\3\2\2\2\26\24\3\2\2\2\27\30\7\5\2\2\30\31\7\2\2\3\31\3\3"+
		"\2\2\2\32\33\7\6\2\2\33\35\7\27\2\2\34\36\7\31\2\2\35\34\3\2\2\2\35\36"+
		"\3\2\2\2\36\37\3\2\2\2\378\7\7\2\2 !\7\b\2\2!\"\7\27\2\2\"+\7\t\2\2#("+
		"\5\6\4\2$%\7\n\2\2%\'\5\6\4\2&$\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2"+
		"),\3\2\2\2*(\3\2\2\2+#\3\2\2\2+,\3\2\2\2,-\3\2\2\2-8\7\7\2\2./\5\b\5\2"+
		"/\61\7\27\2\2\60\62\5\n\6\2\61\60\3\2\2\2\61\62\3\2\2\2\62\63\3\2\2\2"+
		"\63\64\7\13\2\2\64\65\7\27\2\2\65\66\7\7\2\2\668\3\2\2\2\67\32\3\2\2\2"+
		"\67 \3\2\2\2\67.\3\2\2\28\5\3\2\2\29;\7\f\2\2:<\7\30\2\2;:\3\2\2\2;<\3"+
		"\2\2\2<C\3\2\2\2=C\7\r\2\2>@\7\16\2\2?A\5\f\7\2@?\3\2\2\2@A\3\2\2\2AC"+
		"\3\2\2\2B9\3\2\2\2B=\3\2\2\2B>\3\2\2\2C\7\3\2\2\2DJ\7\17\2\2EJ\7\20\2"+
		"\2FJ\7\21\2\2GJ\7\22\2\2HJ\7\23\2\2ID\3\2\2\2IE\3\2\2\2IF\3\2\2\2IG\3"+
		"\2\2\2IH\3\2\2\2J\t\3\2\2\2KL\7\24\2\2LP\7\30\2\2MN\7\25\2\2NP\5\f\7\2"+
		"OK\3\2\2\2OM\3\2\2\2P\13\3\2\2\2QR\t\2\2\2R\r\3\2\2\2\r\24\35(+\61\67"+
		";@BIO";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}