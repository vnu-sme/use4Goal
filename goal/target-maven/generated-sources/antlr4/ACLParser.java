// Generated from ACL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.acl.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ACLParser extends Parser {
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
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, VERSION=45, 
		BOOLEAN=46, INT=47, SIGNED_NUMBER=48, STRING_LITERAL=49, IDENT=50, WS=51, 
		LINE_COMMENT=52, BLOCK_COMMENT=53;
	public static final int
		RULE_model = 0, RULE_topLevelDecl = 1, RULE_invariantDecl = 2, RULE_oclExpression = 3, 
		RULE_oclToken = 4, RULE_enumDecl = 5, RULE_entityDecl = 6, RULE_roleDecl = 7, 
		RULE_specializesClause = 8, RULE_orgContextDecl = 9, RULE_orgContextItem = 10, 
		RULE_attributeBlock = 11, RULE_attributeDecl = 12, RULE_attributeModifier = 13, 
		RULE_defaultClause = 14, RULE_defaultValue = 15, RULE_groupDecl = 16, 
		RULE_groupItem = 17, RULE_groupMemberDecl = 18, RULE_entityRelationDecl = 19, 
		RULE_relationKind = 20, RULE_endpointDecl = 21, RULE_compatibilityDecl = 22, 
		RULE_cardinality = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "topLevelDecl", "invariantDecl", "oclExpression", "oclToken", 
			"enumDecl", "entityDecl", "roleDecl", "specializesClause", "orgContextDecl", 
			"orgContextItem", "attributeBlock", "attributeDecl", "attributeModifier", 
			"defaultClause", "defaultValue", "groupDecl", "groupItem", "groupMemberDecl", 
			"entityRelationDecl", "relationKind", "endpointDecl", "compatibilityDecl", 
			"cardinality"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'acl'", "'{'", "'}'", "'context'", "'inv'", "':'", "';'", "'group'", 
			"'orgContext'", "'.'", "'->'", "'('", "')'", "'|'", "'#'", "'::'", "'='", 
			"'<>'", "'<'", "'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'enum'", 
			"','", "'entity'", "'role'", "'specializes'", "'extends'", "'attribute'", 
			"'optional'", "'required'", "'mutable'", "'default'", "'association'", 
			"'aggregation'", "'composition'", "'compatible'", "'['", "']'", "'..'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "VERSION", "BOOLEAN", 
			"INT", "SIGNED_NUMBER", "STRING_LITERAL", "IDENT", "WS", "LINE_COMMENT", 
			"BLOCK_COMMENT"
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
	public String getGrammarFileName() { return "ACL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ACLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode VERSION() { return getToken(ACLParser.VERSION, 0); }
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public TerminalNode EOF() { return getToken(ACLParser.EOF, 0); }
		public List<TopLevelDeclContext> topLevelDecl() {
			return getRuleContexts(TopLevelDeclContext.class);
		}
		public TopLevelDeclContext topLevelDecl(int i) {
			return getRuleContext(TopLevelDeclContext.class,i);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitModel(this);
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
			setState(48);
			match(T__0);
			setState(49);
			match(VERSION);
			setState(50);
			match(IDENT);
			setState(51);
			match(T__1);
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__7) | (1L << T__8) | (1L << T__26) | (1L << T__28) | (1L << T__29) | (1L << T__37) | (1L << T__38) | (1L << T__39))) != 0)) {
				{
				{
				setState(52);
				topLevelDecl();
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58);
			match(T__2);
			setState(59);
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

	public static class TopLevelDeclContext extends ParserRuleContext {
		public EnumDeclContext enumDecl() {
			return getRuleContext(EnumDeclContext.class,0);
		}
		public EntityDeclContext entityDecl() {
			return getRuleContext(EntityDeclContext.class,0);
		}
		public RoleDeclContext roleDecl() {
			return getRuleContext(RoleDeclContext.class,0);
		}
		public OrgContextDeclContext orgContextDecl() {
			return getRuleContext(OrgContextDeclContext.class,0);
		}
		public GroupDeclContext groupDecl() {
			return getRuleContext(GroupDeclContext.class,0);
		}
		public EntityRelationDeclContext entityRelationDecl() {
			return getRuleContext(EntityRelationDeclContext.class,0);
		}
		public InvariantDeclContext invariantDecl() {
			return getRuleContext(InvariantDeclContext.class,0);
		}
		public TopLevelDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topLevelDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterTopLevelDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitTopLevelDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitTopLevelDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopLevelDeclContext topLevelDecl() throws RecognitionException {
		TopLevelDeclContext _localctx = new TopLevelDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_topLevelDecl);
		try {
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__26:
				enterOuterAlt(_localctx, 1);
				{
				setState(61);
				enumDecl();
				}
				break;
			case T__28:
				enterOuterAlt(_localctx, 2);
				{
				setState(62);
				entityDecl();
				}
				break;
			case T__29:
				enterOuterAlt(_localctx, 3);
				{
				setState(63);
				roleDecl();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 4);
				{
				setState(64);
				orgContextDecl();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 5);
				{
				setState(65);
				groupDecl();
				}
				break;
			case T__37:
			case T__38:
			case T__39:
				enterOuterAlt(_localctx, 6);
				{
				setState(66);
				entityRelationDecl();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 7);
				{
				setState(67);
				invariantDecl();
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

	public static class InvariantDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(ACLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(ACLParser.IDENT, i);
		}
		public OclExpressionContext oclExpression() {
			return getRuleContext(OclExpressionContext.class,0);
		}
		public InvariantDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_invariantDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterInvariantDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitInvariantDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitInvariantDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InvariantDeclContext invariantDecl() throws RecognitionException {
		InvariantDeclContext _localctx = new InvariantDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_invariantDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__3);
			setState(71);
			match(IDENT);
			setState(72);
			match(T__4);
			setState(73);
			match(IDENT);
			setState(74);
			match(T__5);
			setState(75);
			oclExpression();
			setState(76);
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

	public static class OclExpressionContext extends ParserRuleContext {
		public List<OclTokenContext> oclToken() {
			return getRuleContexts(OclTokenContext.class);
		}
		public OclTokenContext oclToken(int i) {
			return getRuleContext(OclTokenContext.class,i);
		}
		public OclExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oclExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterOclExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitOclExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitOclExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OclExpressionContext oclExpression() throws RecognitionException {
		OclExpressionContext _localctx = new OclExpressionContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_oclExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(78);
				oclToken();
				}
				}
				setState(81); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << BOOLEAN) | (1L << INT) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0) );
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

	public static class OclTokenContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ACLParser.STRING_LITERAL, 0); }
		public TerminalNode SIGNED_NUMBER() { return getToken(ACLParser.SIGNED_NUMBER, 0); }
		public TerminalNode BOOLEAN() { return getToken(ACLParser.BOOLEAN, 0); }
		public TerminalNode INT() { return getToken(ACLParser.INT, 0); }
		public OclTokenContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_oclToken; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterOclToken(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitOclToken(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitOclToken(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OclTokenContext oclToken() throws RecognitionException {
		OclTokenContext _localctx = new OclTokenContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_oclToken);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(83);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << BOOLEAN) | (1L << INT) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0)) ) {
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

	public static class EnumDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(ACLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(ACLParser.IDENT, i);
		}
		public EnumDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterEnumDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitEnumDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitEnumDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnumDeclContext enumDecl() throws RecognitionException {
		EnumDeclContext _localctx = new EnumDeclContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_enumDecl);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(T__26);
			setState(86);
			match(IDENT);
			setState(87);
			match(T__1);
			setState(88);
			match(IDENT);
			setState(93);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(89);
					match(T__27);
					setState(90);
					match(IDENT);
					}
					} 
				}
				setState(95);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__27) {
				{
				setState(96);
				match(T__27);
				}
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

	public static class EntityDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public AttributeBlockContext attributeBlock() {
			return getRuleContext(AttributeBlockContext.class,0);
		}
		public SpecializesClauseContext specializesClause() {
			return getRuleContext(SpecializesClauseContext.class,0);
		}
		public EntityDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterEntityDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitEntityDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitEntityDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityDeclContext entityDecl() throws RecognitionException {
		EntityDeclContext _localctx = new EntityDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_entityDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__28);
			setState(102);
			match(IDENT);
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__30 || _la==T__31) {
				{
				setState(103);
				specializesClause();
				}
			}

			setState(108);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				{
				setState(106);
				match(T__6);
				}
				break;
			case T__1:
				{
				setState(107);
				attributeBlock();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class RoleDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public AttributeBlockContext attributeBlock() {
			return getRuleContext(AttributeBlockContext.class,0);
		}
		public SpecializesClauseContext specializesClause() {
			return getRuleContext(SpecializesClauseContext.class,0);
		}
		public RoleDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roleDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterRoleDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitRoleDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitRoleDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoleDeclContext roleDecl() throws RecognitionException {
		RoleDeclContext _localctx = new RoleDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_roleDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(T__29);
			setState(111);
			match(IDENT);
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__30 || _la==T__31) {
				{
				setState(112);
				specializesClause();
				}
			}

			setState(117);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				{
				setState(115);
				match(T__6);
				}
				break;
			case T__1:
				{
				setState(116);
				attributeBlock();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class SpecializesClauseContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public SpecializesClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_specializesClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterSpecializesClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitSpecializesClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitSpecializesClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SpecializesClauseContext specializesClause() throws RecognitionException {
		SpecializesClauseContext _localctx = new SpecializesClauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_specializesClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(119);
			_la = _input.LA(1);
			if ( !(_la==T__30 || _la==T__31) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(120);
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

	public static class OrgContextDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public List<OrgContextItemContext> orgContextItem() {
			return getRuleContexts(OrgContextItemContext.class);
		}
		public OrgContextItemContext orgContextItem(int i) {
			return getRuleContext(OrgContextItemContext.class,i);
		}
		public OrgContextDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orgContextDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterOrgContextDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitOrgContextDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitOrgContextDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrgContextDeclContext orgContextDecl() throws RecognitionException {
		OrgContextDeclContext _localctx = new OrgContextDeclContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_orgContextDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(T__8);
			setState(123);
			match(IDENT);
			setState(124);
			match(T__1);
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__28) | (1L << T__29) | (1L << IDENT))) != 0)) {
				{
				{
				setState(125);
				orgContextItem();
				}
				}
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(131);
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

	public static class OrgContextItemContext extends ParserRuleContext {
		public EntityDeclContext entityDecl() {
			return getRuleContext(EntityDeclContext.class,0);
		}
		public RoleDeclContext roleDecl() {
			return getRuleContext(RoleDeclContext.class,0);
		}
		public OrgContextDeclContext orgContextDecl() {
			return getRuleContext(OrgContextDeclContext.class,0);
		}
		public CompatibilityDeclContext compatibilityDecl() {
			return getRuleContext(CompatibilityDeclContext.class,0);
		}
		public OrgContextItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orgContextItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterOrgContextItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitOrgContextItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitOrgContextItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrgContextItemContext orgContextItem() throws RecognitionException {
		OrgContextItemContext _localctx = new OrgContextItemContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_orgContextItem);
		try {
			setState(137);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__28:
				enterOuterAlt(_localctx, 1);
				{
				setState(133);
				entityDecl();
				}
				break;
			case T__29:
				enterOuterAlt(_localctx, 2);
				{
				setState(134);
				roleDecl();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(135);
				orgContextDecl();
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 4);
				{
				setState(136);
				compatibilityDecl();
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

	public static class AttributeBlockContext extends ParserRuleContext {
		public List<AttributeDeclContext> attributeDecl() {
			return getRuleContexts(AttributeDeclContext.class);
		}
		public AttributeDeclContext attributeDecl(int i) {
			return getRuleContext(AttributeDeclContext.class,i);
		}
		public AttributeBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterAttributeBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitAttributeBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitAttributeBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeBlockContext attributeBlock() throws RecognitionException {
		AttributeBlockContext _localctx = new AttributeBlockContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_attributeBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(T__1);
			setState(143);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__32 || _la==IDENT) {
				{
				{
				setState(140);
				attributeDecl();
				}
				}
				setState(145);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(146);
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

	public static class AttributeDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(ACLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(ACLParser.IDENT, i);
		}
		public List<AttributeModifierContext> attributeModifier() {
			return getRuleContexts(AttributeModifierContext.class);
		}
		public AttributeModifierContext attributeModifier(int i) {
			return getRuleContext(AttributeModifierContext.class,i);
		}
		public DefaultClauseContext defaultClause() {
			return getRuleContext(DefaultClauseContext.class,0);
		}
		public AttributeDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterAttributeDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitAttributeDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitAttributeDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeDeclContext attributeDecl() throws RecognitionException {
		AttributeDeclContext _localctx = new AttributeDeclContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_attributeDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__32) {
				{
				setState(148);
				match(T__32);
				}
			}

			setState(151);
			match(IDENT);
			setState(152);
			match(T__5);
			setState(153);
			match(IDENT);
			setState(157);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__33) | (1L << T__34) | (1L << T__35))) != 0)) {
				{
				{
				setState(154);
				attributeModifier();
				}
				}
				setState(159);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__36) {
				{
				setState(160);
				defaultClause();
				}
			}

			setState(163);
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

	public static class AttributeModifierContext extends ParserRuleContext {
		public AttributeModifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeModifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterAttributeModifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitAttributeModifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitAttributeModifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeModifierContext attributeModifier() throws RecognitionException {
		AttributeModifierContext _localctx = new AttributeModifierContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_attributeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__33) | (1L << T__34) | (1L << T__35))) != 0)) ) {
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

	public static class DefaultClauseContext extends ParserRuleContext {
		public DefaultValueContext defaultValue() {
			return getRuleContext(DefaultValueContext.class,0);
		}
		public DefaultClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterDefaultClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitDefaultClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitDefaultClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefaultClauseContext defaultClause() throws RecognitionException {
		DefaultClauseContext _localctx = new DefaultClauseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(T__36);
			setState(168);
			defaultValue();
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

	public static class DefaultValueContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(ACLParser.STRING_LITERAL, 0); }
		public TerminalNode INT() { return getToken(ACLParser.INT, 0); }
		public TerminalNode SIGNED_NUMBER() { return getToken(ACLParser.SIGNED_NUMBER, 0); }
		public TerminalNode BOOLEAN() { return getToken(ACLParser.BOOLEAN, 0); }
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public DefaultValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterDefaultValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitDefaultValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitDefaultValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefaultValueContext defaultValue() throws RecognitionException {
		DefaultValueContext _localctx = new DefaultValueContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_defaultValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << INT) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0)) ) {
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

	public static class GroupDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public SpecializesClauseContext specializesClause() {
			return getRuleContext(SpecializesClauseContext.class,0);
		}
		public List<GroupItemContext> groupItem() {
			return getRuleContexts(GroupItemContext.class);
		}
		public GroupItemContext groupItem(int i) {
			return getRuleContext(GroupItemContext.class,i);
		}
		public GroupDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterGroupDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitGroupDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitGroupDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupDeclContext groupDecl() throws RecognitionException {
		GroupDeclContext _localctx = new GroupDeclContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_groupDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			match(T__7);
			setState(173);
			match(IDENT);
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__30 || _la==T__31) {
				{
				setState(174);
				specializesClause();
				}
			}

			setState(177);
			match(T__1);
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__32 || _la==IDENT) {
				{
				{
				setState(178);
				groupItem();
				}
				}
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(184);
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

	public static class GroupItemContext extends ParserRuleContext {
		public AttributeDeclContext attributeDecl() {
			return getRuleContext(AttributeDeclContext.class,0);
		}
		public GroupMemberDeclContext groupMemberDecl() {
			return getRuleContext(GroupMemberDeclContext.class,0);
		}
		public CompatibilityDeclContext compatibilityDecl() {
			return getRuleContext(CompatibilityDeclContext.class,0);
		}
		public GroupItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterGroupItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitGroupItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitGroupItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupItemContext groupItem() throws RecognitionException {
		GroupItemContext _localctx = new GroupItemContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_groupItem);
		try {
			setState(189);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				attributeDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				groupMemberDecl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(188);
				compatibilityDecl();
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

	public static class GroupMemberDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public GroupMemberDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupMemberDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterGroupMemberDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitGroupMemberDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitGroupMemberDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupMemberDeclContext groupMemberDecl() throws RecognitionException {
		GroupMemberDeclContext _localctx = new GroupMemberDeclContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_groupMemberDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			match(IDENT);
			setState(192);
			cardinality();
			setState(193);
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

	public static class EntityRelationDeclContext extends ParserRuleContext {
		public RelationKindContext relationKind() {
			return getRuleContext(RelationKindContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public List<EndpointDeclContext> endpointDecl() {
			return getRuleContexts(EndpointDeclContext.class);
		}
		public EndpointDeclContext endpointDecl(int i) {
			return getRuleContext(EndpointDeclContext.class,i);
		}
		public EntityRelationDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityRelationDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterEntityRelationDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitEntityRelationDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitEntityRelationDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityRelationDeclContext entityRelationDecl() throws RecognitionException {
		EntityRelationDeclContext _localctx = new EntityRelationDeclContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_entityRelationDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			relationKind();
			setState(196);
			match(IDENT);
			setState(197);
			match(T__1);
			setState(198);
			endpointDecl();
			setState(199);
			endpointDecl();
			setState(200);
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

	public static class RelationKindContext extends ParserRuleContext {
		public RelationKindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationKind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterRelationKind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitRelationKind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitRelationKind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationKindContext relationKind() throws RecognitionException {
		RelationKindContext _localctx = new RelationKindContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_relationKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__37) | (1L << T__38) | (1L << T__39))) != 0)) ) {
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

	public static class EndpointDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(ACLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(ACLParser.IDENT, i);
		}
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public EndpointDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endpointDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterEndpointDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitEndpointDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitEndpointDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndpointDeclContext endpointDecl() throws RecognitionException {
		EndpointDeclContext _localctx = new EndpointDeclContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_endpointDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			match(IDENT);
			setState(205);
			cardinality();
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__29 || _la==IDENT) {
				{
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__29) {
					{
					setState(206);
					match(T__29);
					}
				}

				setState(209);
				match(IDENT);
				}
			}

			setState(212);
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

	public static class CompatibilityDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(ACLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(ACLParser.IDENT, i);
		}
		public CompatibilityDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compatibilityDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterCompatibilityDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitCompatibilityDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitCompatibilityDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompatibilityDeclContext compatibilityDecl() throws RecognitionException {
		CompatibilityDeclContext _localctx = new CompatibilityDeclContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_compatibilityDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(IDENT);
			setState(215);
			match(T__40);
			setState(216);
			match(IDENT);
			setState(217);
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

	public static class CardinalityContext extends ParserRuleContext {
		public List<TerminalNode> INT() { return getTokens(ACLParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(ACLParser.INT, i);
		}
		public CardinalityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cardinality; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterCardinality(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitCardinality(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitCardinality(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CardinalityContext cardinality() throws RecognitionException {
		CardinalityContext _localctx = new CardinalityContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_cardinality);
		int _la;
		try {
			setState(230);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(219);
				match(T__41);
				setState(220);
				match(INT);
				setState(221);
				match(T__42);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(222);
				match(T__41);
				setState(223);
				match(INT);
				setState(224);
				match(T__43);
				setState(225);
				_la = _input.LA(1);
				if ( !(_la==T__24 || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(226);
				match(T__42);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(227);
				match(T__41);
				setState(228);
				match(T__24);
				setState(229);
				match(T__42);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\67\u00eb\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\3\2\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\5\3G\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6\5R\n\5\r"+
		"\5\16\5S\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\7\7^\n\7\f\7\16\7a\13\7\3\7\5"+
		"\7d\n\7\3\7\3\7\3\b\3\b\3\b\5\bk\n\b\3\b\3\b\5\bo\n\b\3\t\3\t\3\t\5\t"+
		"t\n\t\3\t\3\t\5\tx\n\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\7\13\u0081\n\13"+
		"\f\13\16\13\u0084\13\13\3\13\3\13\3\f\3\f\3\f\3\f\5\f\u008c\n\f\3\r\3"+
		"\r\7\r\u0090\n\r\f\r\16\r\u0093\13\r\3\r\3\r\3\16\5\16\u0098\n\16\3\16"+
		"\3\16\3\16\3\16\7\16\u009e\n\16\f\16\16\16\u00a1\13\16\3\16\5\16\u00a4"+
		"\n\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\5\22"+
		"\u00b2\n\22\3\22\3\22\7\22\u00b6\n\22\f\22\16\22\u00b9\13\22\3\22\3\22"+
		"\3\23\3\23\3\23\5\23\u00c0\n\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\27\5\27\u00d2\n\27\3\27\5\27\u00d5"+
		"\n\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\5\31\u00e9\n\31\3\31\2\2\32\2\4\6\b\n\f\16\20"+
		"\22\24\26\30\32\34\36 \"$&(*,.\60\2\b\4\2\n\34\60\64\3\2!\"\3\2$&\3\2"+
		"\60\64\3\2(*\4\2\33\33\61\61\2\u00f0\2\62\3\2\2\2\4F\3\2\2\2\6H\3\2\2"+
		"\2\bQ\3\2\2\2\nU\3\2\2\2\fW\3\2\2\2\16g\3\2\2\2\20p\3\2\2\2\22y\3\2\2"+
		"\2\24|\3\2\2\2\26\u008b\3\2\2\2\30\u008d\3\2\2\2\32\u0097\3\2\2\2\34\u00a7"+
		"\3\2\2\2\36\u00a9\3\2\2\2 \u00ac\3\2\2\2\"\u00ae\3\2\2\2$\u00bf\3\2\2"+
		"\2&\u00c1\3\2\2\2(\u00c5\3\2\2\2*\u00cc\3\2\2\2,\u00ce\3\2\2\2.\u00d8"+
		"\3\2\2\2\60\u00e8\3\2\2\2\62\63\7\3\2\2\63\64\7/\2\2\64\65\7\64\2\2\65"+
		"9\7\4\2\2\668\5\4\3\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:<"+
		"\3\2\2\2;9\3\2\2\2<=\7\5\2\2=>\7\2\2\3>\3\3\2\2\2?G\5\f\7\2@G\5\16\b\2"+
		"AG\5\20\t\2BG\5\24\13\2CG\5\"\22\2DG\5(\25\2EG\5\6\4\2F?\3\2\2\2F@\3\2"+
		"\2\2FA\3\2\2\2FB\3\2\2\2FC\3\2\2\2FD\3\2\2\2FE\3\2\2\2G\5\3\2\2\2HI\7"+
		"\6\2\2IJ\7\64\2\2JK\7\7\2\2KL\7\64\2\2LM\7\b\2\2MN\5\b\5\2NO\7\t\2\2O"+
		"\7\3\2\2\2PR\5\n\6\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\t\3\2\2"+
		"\2UV\t\2\2\2V\13\3\2\2\2WX\7\35\2\2XY\7\64\2\2YZ\7\4\2\2Z_\7\64\2\2[\\"+
		"\7\36\2\2\\^\7\64\2\2][\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`c\3\2\2"+
		"\2a_\3\2\2\2bd\7\36\2\2cb\3\2\2\2cd\3\2\2\2de\3\2\2\2ef\7\5\2\2f\r\3\2"+
		"\2\2gh\7\37\2\2hj\7\64\2\2ik\5\22\n\2ji\3\2\2\2jk\3\2\2\2kn\3\2\2\2lo"+
		"\7\t\2\2mo\5\30\r\2nl\3\2\2\2nm\3\2\2\2o\17\3\2\2\2pq\7 \2\2qs\7\64\2"+
		"\2rt\5\22\n\2sr\3\2\2\2st\3\2\2\2tw\3\2\2\2ux\7\t\2\2vx\5\30\r\2wu\3\2"+
		"\2\2wv\3\2\2\2x\21\3\2\2\2yz\t\3\2\2z{\7\64\2\2{\23\3\2\2\2|}\7\13\2\2"+
		"}~\7\64\2\2~\u0082\7\4\2\2\177\u0081\5\26\f\2\u0080\177\3\2\2\2\u0081"+
		"\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2"+
		"\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7\5\2\2\u0086\25\3\2\2\2\u0087\u008c"+
		"\5\16\b\2\u0088\u008c\5\20\t\2\u0089\u008c\5\24\13\2\u008a\u008c\5.\30"+
		"\2\u008b\u0087\3\2\2\2\u008b\u0088\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008a"+
		"\3\2\2\2\u008c\27\3\2\2\2\u008d\u0091\7\4\2\2\u008e\u0090\5\32\16\2\u008f"+
		"\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2"+
		"\2\2\u0092\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0095\7\5\2\2\u0095"+
		"\31\3\2\2\2\u0096\u0098\7#\2\2\u0097\u0096\3\2\2\2\u0097\u0098\3\2\2\2"+
		"\u0098\u0099\3\2\2\2\u0099\u009a\7\64\2\2\u009a\u009b\7\b\2\2\u009b\u009f"+
		"\7\64\2\2\u009c\u009e\5\34\17\2\u009d\u009c\3\2\2\2\u009e\u00a1\3\2\2"+
		"\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f"+
		"\3\2\2\2\u00a2\u00a4\5\36\20\2\u00a3\u00a2\3\2\2\2\u00a3\u00a4\3\2\2\2"+
		"\u00a4\u00a5\3\2\2\2\u00a5\u00a6\7\t\2\2\u00a6\33\3\2\2\2\u00a7\u00a8"+
		"\t\4\2\2\u00a8\35\3\2\2\2\u00a9\u00aa\7\'\2\2\u00aa\u00ab\5 \21\2\u00ab"+
		"\37\3\2\2\2\u00ac\u00ad\t\5\2\2\u00ad!\3\2\2\2\u00ae\u00af\7\n\2\2\u00af"+
		"\u00b1\7\64\2\2\u00b0\u00b2\5\22\n\2\u00b1\u00b0\3\2\2\2\u00b1\u00b2\3"+
		"\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b7\7\4\2\2\u00b4\u00b6\5$\23\2\u00b5"+
		"\u00b4\3\2\2\2\u00b6\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2"+
		"\2\2\u00b8\u00ba\3\2\2\2\u00b9\u00b7\3\2\2\2\u00ba\u00bb\7\5\2\2\u00bb"+
		"#\3\2\2\2\u00bc\u00c0\5\32\16\2\u00bd\u00c0\5&\24\2\u00be\u00c0\5.\30"+
		"\2\u00bf\u00bc\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf\u00be\3\2\2\2\u00c0%"+
		"\3\2\2\2\u00c1\u00c2\7\64\2\2\u00c2\u00c3\5\60\31\2\u00c3\u00c4\7\t\2"+
		"\2\u00c4\'\3\2\2\2\u00c5\u00c6\5*\26\2\u00c6\u00c7\7\64\2\2\u00c7\u00c8"+
		"\7\4\2\2\u00c8\u00c9\5,\27\2\u00c9\u00ca\5,\27\2\u00ca\u00cb\7\5\2\2\u00cb"+
		")\3\2\2\2\u00cc\u00cd\t\6\2\2\u00cd+\3\2\2\2\u00ce\u00cf\7\64\2\2\u00cf"+
		"\u00d4\5\60\31\2\u00d0\u00d2\7 \2\2\u00d1\u00d0\3\2\2\2\u00d1\u00d2\3"+
		"\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\u00d5\7\64\2\2\u00d4\u00d1\3\2\2\2\u00d4"+
		"\u00d5\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d7\7\t\2\2\u00d7-\3\2\2\2"+
		"\u00d8\u00d9\7\64\2\2\u00d9\u00da\7+\2\2\u00da\u00db\7\64\2\2\u00db\u00dc"+
		"\7\t\2\2\u00dc/\3\2\2\2\u00dd\u00de\7,\2\2\u00de\u00df\7\61\2\2\u00df"+
		"\u00e9\7-\2\2\u00e0\u00e1\7,\2\2\u00e1\u00e2\7\61\2\2\u00e2\u00e3\7.\2"+
		"\2\u00e3\u00e4\t\7\2\2\u00e4\u00e9\7-\2\2\u00e5\u00e6\7,\2\2\u00e6\u00e7"+
		"\7\33\2\2\u00e7\u00e9\7-\2\2\u00e8\u00dd\3\2\2\2\u00e8\u00e0\3\2\2\2\u00e8"+
		"\u00e5\3\2\2\2\u00e9\61\3\2\2\2\279FS_cjnsw\u0082\u008b\u0091\u0097\u009f"+
		"\u00a3\u00b1\u00b7\u00bf\u00d1\u00d4\u00e8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}