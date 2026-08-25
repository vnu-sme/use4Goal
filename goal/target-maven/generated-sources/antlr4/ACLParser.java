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
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, VERSION=44, BOOLEAN=45, 
		INT=46, SIGNED_NUMBER=47, STRING_LITERAL=48, IDENT=49, WS=50, LINE_COMMENT=51, 
		BLOCK_COMMENT=52;
	public static final int
		RULE_model = 0, RULE_topLevelDecl = 1, RULE_invariantDecl = 2, RULE_oclExpression = 3, 
		RULE_oclToken = 4, RULE_enumDecl = 5, RULE_entityDecl = 6, RULE_roleDecl = 7, 
		RULE_specializesClause = 8, RULE_attributeBlock = 9, RULE_attributeDecl = 10, 
		RULE_attributeModifier = 11, RULE_defaultClause = 12, RULE_defaultValue = 13, 
		RULE_groupDecl = 14, RULE_groupItem = 15, RULE_groupMemberDecl = 16, RULE_entityRelationDecl = 17, 
		RULE_relationKind = 18, RULE_endpointDecl = 19, RULE_compatibilityDecl = 20, 
		RULE_cardinality = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "topLevelDecl", "invariantDecl", "oclExpression", "oclToken", 
			"enumDecl", "entityDecl", "roleDecl", "specializesClause", "attributeBlock", 
			"attributeDecl", "attributeModifier", "defaultClause", "defaultValue", 
			"groupDecl", "groupItem", "groupMemberDecl", "entityRelationDecl", "relationKind", 
			"endpointDecl", "compatibilityDecl", "cardinality"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'acl'", "'{'", "'}'", "'context'", "'inv'", "':'", "';'", "'group'", 
			"'.'", "'->'", "'('", "')'", "'|'", "'#'", "'::'", "'='", "'<>'", "'<'", 
			"'<='", "'>'", "'>='", "'+'", "'-'", "'*'", "'/'", "'enum'", "','", "'entity'", 
			"'role'", "'specializes'", "'extends'", "'attribute'", "'optional'", 
			"'required'", "'mutable'", "'default'", "'association'", "'aggregation'", 
			"'composition'", "'compatible'", "'['", "']'", "'..'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "VERSION", "BOOLEAN", 
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
			setState(44);
			match(T__0);
			setState(45);
			match(VERSION);
			setState(46);
			match(IDENT);
			setState(47);
			match(T__1);
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__7) | (1L << T__25) | (1L << T__27) | (1L << T__28) | (1L << T__36) | (1L << T__37) | (1L << T__38))) != 0)) {
				{
				{
				setState(48);
				topLevelDecl();
				}
				}
				setState(53);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(54);
			match(T__2);
			setState(55);
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
			setState(63);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
				setState(57);
				enumDecl();
				}
				break;
			case T__27:
				enterOuterAlt(_localctx, 2);
				{
				setState(58);
				entityDecl();
				}
				break;
			case T__28:
				enterOuterAlt(_localctx, 3);
				{
				setState(59);
				roleDecl();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 4);
				{
				setState(60);
				groupDecl();
				}
				break;
			case T__36:
			case T__37:
			case T__38:
				enterOuterAlt(_localctx, 5);
				{
				setState(61);
				entityRelationDecl();
				}
				break;
			case T__3:
				enterOuterAlt(_localctx, 6);
				{
				setState(62);
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
			setState(65);
			match(T__3);
			setState(66);
			match(IDENT);
			setState(67);
			match(T__4);
			setState(68);
			match(IDENT);
			setState(69);
			match(T__5);
			setState(70);
			oclExpression();
			setState(71);
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
			setState(74); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(73);
				oclToken();
				}
				}
				setState(76); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << BOOLEAN) | (1L << INT) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0) );
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
			setState(78);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << BOOLEAN) | (1L << INT) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0)) ) {
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
			setState(80);
			match(T__25);
			setState(81);
			match(IDENT);
			setState(82);
			match(T__1);
			setState(83);
			match(IDENT);
			setState(88);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(84);
					match(T__26);
					setState(85);
					match(IDENT);
					}
					} 
				}
				setState(90);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(91);
				match(T__26);
				}
			}

			setState(94);
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
			setState(96);
			match(T__27);
			setState(97);
			match(IDENT);
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__29 || _la==T__30) {
				{
				setState(98);
				specializesClause();
				}
			}

			setState(103);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				{
				setState(101);
				match(T__6);
				}
				break;
			case T__1:
				{
				setState(102);
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
			setState(105);
			match(T__28);
			setState(106);
			match(IDENT);
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__29 || _la==T__30) {
				{
				setState(107);
				specializesClause();
				}
			}

			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				{
				setState(110);
				match(T__6);
				}
				break;
			case T__1:
				{
				setState(111);
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
			setState(114);
			_la = _input.LA(1);
			if ( !(_la==T__29 || _la==T__30) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(115);
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
		enterRule(_localctx, 18, RULE_attributeBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			match(T__1);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__31 || _la==IDENT) {
				{
				{
				setState(118);
				attributeDecl();
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(124);
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
		enterRule(_localctx, 20, RULE_attributeDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__31) {
				{
				setState(126);
				match(T__31);
				}
			}

			setState(129);
			match(IDENT);
			setState(130);
			match(T__5);
			setState(131);
			match(IDENT);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__34))) != 0)) {
				{
				{
				setState(132);
				attributeModifier();
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__35) {
				{
				setState(138);
				defaultClause();
				}
			}

			setState(141);
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
		enterRule(_localctx, 22, RULE_attributeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__32) | (1L << T__33) | (1L << T__34))) != 0)) ) {
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
		enterRule(_localctx, 24, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(T__35);
			setState(146);
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
		enterRule(_localctx, 26, RULE_defaultValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
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
		enterRule(_localctx, 28, RULE_groupDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			match(T__7);
			setState(151);
			match(IDENT);
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__29 || _la==T__30) {
				{
				setState(152);
				specializesClause();
				}
			}

			setState(155);
			match(T__1);
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__31 || _la==IDENT) {
				{
				{
				setState(156);
				groupItem();
				}
				}
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(162);
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
		enterRule(_localctx, 30, RULE_groupItem);
		try {
			setState(167);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				attributeDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				groupMemberDecl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(166);
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
		enterRule(_localctx, 32, RULE_groupMemberDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(IDENT);
			setState(170);
			cardinality();
			setState(171);
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
		enterRule(_localctx, 34, RULE_entityRelationDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			relationKind();
			setState(174);
			match(IDENT);
			setState(175);
			match(T__1);
			setState(176);
			endpointDecl();
			setState(177);
			endpointDecl();
			setState(178);
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
		enterRule(_localctx, 36, RULE_relationKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__36) | (1L << T__37) | (1L << T__38))) != 0)) ) {
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
		enterRule(_localctx, 38, RULE_endpointDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(IDENT);
			setState(183);
			cardinality();
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__28 || _la==IDENT) {
				{
				setState(185);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__28) {
					{
					setState(184);
					match(T__28);
					}
				}

				setState(187);
				match(IDENT);
				}
			}

			setState(190);
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
		enterRule(_localctx, 40, RULE_compatibilityDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			match(IDENT);
			setState(193);
			match(T__39);
			setState(194);
			match(IDENT);
			setState(195);
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
		enterRule(_localctx, 42, RULE_cardinality);
		int _la;
		try {
			setState(208);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(197);
				match(T__40);
				setState(198);
				match(INT);
				setState(199);
				match(T__41);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(200);
				match(T__40);
				setState(201);
				match(INT);
				setState(202);
				match(T__42);
				setState(203);
				_la = _input.LA(1);
				if ( !(_la==T__23 || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(204);
				match(T__41);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(205);
				match(T__40);
				setState(206);
				match(T__23);
				setState(207);
				match(T__41);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\66\u00d5\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3\2\3\2"+
		"\7\2\64\n\2\f\2\16\2\67\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\5\3B"+
		"\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6\5M\n\5\r\5\16\5N\3\6\3\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\7\7Y\n\7\f\7\16\7\\\13\7\3\7\5\7_\n\7\3\7\3\7\3"+
		"\b\3\b\3\b\5\bf\n\b\3\b\3\b\5\bj\n\b\3\t\3\t\3\t\5\to\n\t\3\t\3\t\5\t"+
		"s\n\t\3\n\3\n\3\n\3\13\3\13\7\13z\n\13\f\13\16\13}\13\13\3\13\3\13\3\f"+
		"\5\f\u0082\n\f\3\f\3\f\3\f\3\f\7\f\u0088\n\f\f\f\16\f\u008b\13\f\3\f\5"+
		"\f\u008e\n\f\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\5"+
		"\20\u009c\n\20\3\20\3\20\7\20\u00a0\n\20\f\20\16\20\u00a3\13\20\3\20\3"+
		"\20\3\21\3\21\3\21\5\21\u00aa\n\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\24\3\24\3\25\3\25\3\25\5\25\u00bc\n\25\3\25\5\25"+
		"\u00bf\n\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00d3\n\27\3\27\2\2\30\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\32\34\36 \"$&(*,\2\b\4\2\n\33/\63\3\2 !\3\2#%\3\2/"+
		"\63\3\2\')\4\2\32\32\60\60\2\u00d7\2.\3\2\2\2\4A\3\2\2\2\6C\3\2\2\2\b"+
		"L\3\2\2\2\nP\3\2\2\2\fR\3\2\2\2\16b\3\2\2\2\20k\3\2\2\2\22t\3\2\2\2\24"+
		"w\3\2\2\2\26\u0081\3\2\2\2\30\u0091\3\2\2\2\32\u0093\3\2\2\2\34\u0096"+
		"\3\2\2\2\36\u0098\3\2\2\2 \u00a9\3\2\2\2\"\u00ab\3\2\2\2$\u00af\3\2\2"+
		"\2&\u00b6\3\2\2\2(\u00b8\3\2\2\2*\u00c2\3\2\2\2,\u00d2\3\2\2\2./\7\3\2"+
		"\2/\60\7.\2\2\60\61\7\63\2\2\61\65\7\4\2\2\62\64\5\4\3\2\63\62\3\2\2\2"+
		"\64\67\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\668\3\2\2\2\67\65\3\2\2\28"+
		"9\7\5\2\29:\7\2\2\3:\3\3\2\2\2;B\5\f\7\2<B\5\16\b\2=B\5\20\t\2>B\5\36"+
		"\20\2?B\5$\23\2@B\5\6\4\2A;\3\2\2\2A<\3\2\2\2A=\3\2\2\2A>\3\2\2\2A?\3"+
		"\2\2\2A@\3\2\2\2B\5\3\2\2\2CD\7\6\2\2DE\7\63\2\2EF\7\7\2\2FG\7\63\2\2"+
		"GH\7\b\2\2HI\5\b\5\2IJ\7\t\2\2J\7\3\2\2\2KM\5\n\6\2LK\3\2\2\2MN\3\2\2"+
		"\2NL\3\2\2\2NO\3\2\2\2O\t\3\2\2\2PQ\t\2\2\2Q\13\3\2\2\2RS\7\34\2\2ST\7"+
		"\63\2\2TU\7\4\2\2UZ\7\63\2\2VW\7\35\2\2WY\7\63\2\2XV\3\2\2\2Y\\\3\2\2"+
		"\2ZX\3\2\2\2Z[\3\2\2\2[^\3\2\2\2\\Z\3\2\2\2]_\7\35\2\2^]\3\2\2\2^_\3\2"+
		"\2\2_`\3\2\2\2`a\7\5\2\2a\r\3\2\2\2bc\7\36\2\2ce\7\63\2\2df\5\22\n\2e"+
		"d\3\2\2\2ef\3\2\2\2fi\3\2\2\2gj\7\t\2\2hj\5\24\13\2ig\3\2\2\2ih\3\2\2"+
		"\2j\17\3\2\2\2kl\7\37\2\2ln\7\63\2\2mo\5\22\n\2nm\3\2\2\2no\3\2\2\2or"+
		"\3\2\2\2ps\7\t\2\2qs\5\24\13\2rp\3\2\2\2rq\3\2\2\2s\21\3\2\2\2tu\t\3\2"+
		"\2uv\7\63\2\2v\23\3\2\2\2w{\7\4\2\2xz\5\26\f\2yx\3\2\2\2z}\3\2\2\2{y\3"+
		"\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\177\7\5\2\2\177\25\3\2\2\2\u0080"+
		"\u0082\7\"\2\2\u0081\u0080\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\3\2"+
		"\2\2\u0083\u0084\7\63\2\2\u0084\u0085\7\b\2\2\u0085\u0089\7\63\2\2\u0086"+
		"\u0088\5\30\r\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3"+
		"\2\2\2\u0089\u008a\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008c"+
		"\u008e\5\32\16\2\u008d\u008c\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\3"+
		"\2\2\2\u008f\u0090\7\t\2\2\u0090\27\3\2\2\2\u0091\u0092\t\4\2\2\u0092"+
		"\31\3\2\2\2\u0093\u0094\7&\2\2\u0094\u0095\5\34\17\2\u0095\33\3\2\2\2"+
		"\u0096\u0097\t\5\2\2\u0097\35\3\2\2\2\u0098\u0099\7\n\2\2\u0099\u009b"+
		"\7\63\2\2\u009a\u009c\5\22\n\2\u009b\u009a\3\2\2\2\u009b\u009c\3\2\2\2"+
		"\u009c\u009d\3\2\2\2\u009d\u00a1\7\4\2\2\u009e\u00a0\5 \21\2\u009f\u009e"+
		"\3\2\2\2\u00a0\u00a3\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2"+
		"\u00a4\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a4\u00a5\7\5\2\2\u00a5\37\3\2\2"+
		"\2\u00a6\u00aa\5\26\f\2\u00a7\u00aa\5\"\22\2\u00a8\u00aa\5*\26\2\u00a9"+
		"\u00a6\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00a8\3\2\2\2\u00aa!\3\2\2\2"+
		"\u00ab\u00ac\7\63\2\2\u00ac\u00ad\5,\27\2\u00ad\u00ae\7\t\2\2\u00ae#\3"+
		"\2\2\2\u00af\u00b0\5&\24\2\u00b0\u00b1\7\63\2\2\u00b1\u00b2\7\4\2\2\u00b2"+
		"\u00b3\5(\25\2\u00b3\u00b4\5(\25\2\u00b4\u00b5\7\5\2\2\u00b5%\3\2\2\2"+
		"\u00b6\u00b7\t\6\2\2\u00b7\'\3\2\2\2\u00b8\u00b9\7\63\2\2\u00b9\u00be"+
		"\5,\27\2\u00ba\u00bc\7\37\2\2\u00bb\u00ba\3\2\2\2\u00bb\u00bc\3\2\2\2"+
		"\u00bc\u00bd\3\2\2\2\u00bd\u00bf\7\63\2\2\u00be\u00bb\3\2\2\2\u00be\u00bf"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\7\t\2\2\u00c1)\3\2\2\2\u00c2"+
		"\u00c3\7\63\2\2\u00c3\u00c4\7*\2\2\u00c4\u00c5\7\63\2\2\u00c5\u00c6\7"+
		"\t\2\2\u00c6+\3\2\2\2\u00c7\u00c8\7+\2\2\u00c8\u00c9\7\60\2\2\u00c9\u00d3"+
		"\7,\2\2\u00ca\u00cb\7+\2\2\u00cb\u00cc\7\60\2\2\u00cc\u00cd\7-\2\2\u00cd"+
		"\u00ce\t\7\2\2\u00ce\u00d3\7,\2\2\u00cf\u00d0\7+\2\2\u00d0\u00d1\7\32"+
		"\2\2\u00d1\u00d3\7,\2\2\u00d2\u00c7\3\2\2\2\u00d2\u00ca\3\2\2\2\u00d2"+
		"\u00cf\3\2\2\2\u00d3-\3\2\2\2\25\65ANZ^einr{\u0081\u0089\u008d\u009b\u00a1"+
		"\u00a9\u00bb\u00be\u00d2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}