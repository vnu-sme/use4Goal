// Generated from ACL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.acl.parser; 
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
		T__38=39, T__39=40, T__40=41, VERSION=42, BOOLEAN=43, INT=44, SIGNED_NUMBER=45, 
		STRING_LITERAL=46, IDENT=47, WS=48, LINE_COMMENT=49, BLOCK_COMMENT=50;
	public static final int
		RULE_model = 0, RULE_topLevelDecl = 1, RULE_enumDecl = 2, RULE_entityDecl = 3, 
		RULE_roleDecl = 4, RULE_specializesClause = 5, RULE_attributeBlock = 6, 
		RULE_attributeDecl = 7, RULE_attributeModifier = 8, RULE_defaultClause = 9, 
		RULE_defaultValue = 10, RULE_groupDecl = 11, RULE_groupItem = 12, RULE_groupMemberDecl = 13, 
		RULE_legacyTypedMemberDecl = 14, RULE_legacySubgroupDecl = 15, RULE_entityRelationDecl = 16, 
		RULE_relationKind = 17, RULE_endpointDecl = 18, RULE_compatibilityDecl = 19, 
		RULE_linkArrow = 20, RULE_linkScope = 21, RULE_compatibilityOption = 22, 
		RULE_compatibilityType = 23, RULE_scopeValue = 24, RULE_cardinality = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "topLevelDecl", "enumDecl", "entityDecl", "roleDecl", "specializesClause", 
			"attributeBlock", "attributeDecl", "attributeModifier", "defaultClause", 
			"defaultValue", "groupDecl", "groupItem", "groupMemberDecl", "legacyTypedMemberDecl", 
			"legacySubgroupDecl", "entityRelationDecl", "relationKind", "endpointDecl", 
			"compatibilityDecl", "linkArrow", "linkScope", "compatibilityOption", 
			"compatibilityType", "scopeValue", "cardinality"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'acl'", "'{'", "'}'", "'enum'", "','", "'entity'", "';'", "'abstract'", 
			"'role'", "'specializes'", "'extends'", "'attribute'", "':'", "'required'", 
			"'mutable'", "'default'", "'group'", "'subgroup'", "'association'", "'aggregation'", 
			"'composition'", "'relationship'", "'partOf'", "'compatibility'", "'link'", 
			"'compatible'", "'intra'", "'->'", "'<->'", "'inter'", "'scope'", "'extends-subgroups'", 
			"'bidirectional'", "'type'", "'incompatible'", "'intra-group'", "'inter-group'", 
			"'['", "']'", "'..'", "'*'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "VERSION", "BOOLEAN", "INT", "SIGNED_NUMBER", 
			"STRING_LITERAL", "IDENT", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
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
			setState(52);
			match(T__0);
			setState(53);
			match(VERSION);
			setState(54);
			match(IDENT);
			setState(55);
			match(T__1);
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__5) | (1L << T__7) | (1L << T__8) | (1L << T__16) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << IDENT))) != 0)) {
				{
				{
				setState(56);
				topLevelDecl();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62);
			match(T__2);
			setState(63);
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
		public CompatibilityDeclContext compatibilityDecl() {
			return getRuleContext(CompatibilityDeclContext.class,0);
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
			setState(71);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				enumDecl();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(66);
				entityDecl();
				}
				break;
			case T__7:
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(67);
				roleDecl();
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 4);
				{
				setState(68);
				groupDecl();
				}
				break;
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
				enterOuterAlt(_localctx, 5);
				{
				setState(69);
				entityRelationDecl();
				}
				break;
			case T__23:
			case T__24:
			case IDENT:
				enterOuterAlt(_localctx, 6);
				{
				setState(70);
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
		enterRule(_localctx, 4, RULE_enumDecl);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(T__3);
			setState(74);
			match(IDENT);
			setState(75);
			match(T__1);
			setState(76);
			match(IDENT);
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(77);
					match(T__4);
					setState(78);
					match(IDENT);
					}
					} 
				}
				setState(83);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(84);
				match(T__4);
				}
			}

			setState(87);
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
		enterRule(_localctx, 6, RULE_entityDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			match(T__5);
			setState(90);
			match(IDENT);
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9 || _la==T__10) {
				{
				setState(91);
				specializesClause();
				}
			}

			setState(96);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				{
				setState(94);
				match(T__6);
				}
				break;
			case T__1:
				{
				setState(95);
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
		public SpecializesClauseContext specializesClause() {
			return getRuleContext(SpecializesClauseContext.class,0);
		}
		public AttributeBlockContext attributeBlock() {
			return getRuleContext(AttributeBlockContext.class,0);
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
		enterRule(_localctx, 8, RULE_roleDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(98);
				match(T__7);
				}
			}

			setState(101);
			match(T__8);
			setState(102);
			match(IDENT);
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9 || _la==T__10) {
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
			case T__2:
			case T__3:
			case T__5:
			case T__7:
			case T__8:
			case T__16:
			case T__18:
			case T__19:
			case T__20:
			case T__21:
			case T__22:
			case T__23:
			case T__24:
			case IDENT:
				break;
			default:
				break;
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
		enterRule(_localctx, 10, RULE_specializesClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			_la = _input.LA(1);
			if ( !(_la==T__9 || _la==T__10) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(111);
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
		enterRule(_localctx, 12, RULE_attributeBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(T__1);
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11 || _la==IDENT) {
				{
				{
				setState(114);
				attributeDecl();
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(120);
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
		enterRule(_localctx, 14, RULE_attributeDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(122);
				match(T__11);
				}
			}

			setState(125);
			match(IDENT);
			setState(126);
			match(T__12);
			setState(127);
			match(IDENT);
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__13 || _la==T__14) {
				{
				{
				setState(128);
				attributeModifier();
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(134);
				defaultClause();
				}
			}

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
		enterRule(_localctx, 16, RULE_attributeModifier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			_la = _input.LA(1);
			if ( !(_la==T__13 || _la==T__14) ) {
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
		enterRule(_localctx, 18, RULE_defaultClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(T__15);
			setState(142);
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
		enterRule(_localctx, 20, RULE_defaultValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
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
		enterRule(_localctx, 22, RULE_groupDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(T__16);
			setState(147);
			match(IDENT);
			setState(148);
			match(T__1);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__8) | (1L << T__11) | (1L << T__17) | (1L << T__23) | (1L << T__24) | (1L << IDENT))) != 0)) {
				{
				{
				setState(149);
				groupItem();
				}
				}
				setState(154);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(155);
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
		public LegacyTypedMemberDeclContext legacyTypedMemberDecl() {
			return getRuleContext(LegacyTypedMemberDeclContext.class,0);
		}
		public LegacySubgroupDeclContext legacySubgroupDecl() {
			return getRuleContext(LegacySubgroupDeclContext.class,0);
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
		enterRule(_localctx, 24, RULE_groupItem);
		try {
			setState(162);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				attributeDecl();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(158);
				groupMemberDecl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(159);
				legacyTypedMemberDecl();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(160);
				legacySubgroupDecl();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(161);
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
		enterRule(_localctx, 26, RULE_groupMemberDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(IDENT);
			setState(165);
			cardinality();
			setState(166);
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

	public static class LegacyTypedMemberDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public LegacyTypedMemberDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_legacyTypedMemberDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterLegacyTypedMemberDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitLegacyTypedMemberDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitLegacyTypedMemberDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LegacyTypedMemberDeclContext legacyTypedMemberDecl() throws RecognitionException {
		LegacyTypedMemberDeclContext _localctx = new LegacyTypedMemberDeclContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_legacyTypedMemberDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			_la = _input.LA(1);
			if ( !(_la==T__5 || _la==T__8) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
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

	public static class LegacySubgroupDeclContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public CardinalityContext cardinality() {
			return getRuleContext(CardinalityContext.class,0);
		}
		public List<GroupItemContext> groupItem() {
			return getRuleContexts(GroupItemContext.class);
		}
		public GroupItemContext groupItem(int i) {
			return getRuleContext(GroupItemContext.class,i);
		}
		public LegacySubgroupDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_legacySubgroupDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterLegacySubgroupDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitLegacySubgroupDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitLegacySubgroupDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LegacySubgroupDeclContext legacySubgroupDecl() throws RecognitionException {
		LegacySubgroupDeclContext _localctx = new LegacySubgroupDeclContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_legacySubgroupDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(T__17);
			setState(174);
			match(IDENT);
			setState(175);
			cardinality();
			setState(176);
			match(T__1);
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__8) | (1L << T__11) | (1L << T__17) | (1L << T__23) | (1L << T__24) | (1L << IDENT))) != 0)) {
				{
				{
				setState(177);
				groupItem();
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(183);
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
		enterRule(_localctx, 32, RULE_entityRelationDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			relationKind();
			setState(186);
			match(IDENT);
			setState(187);
			match(T__1);
			setState(188);
			endpointDecl();
			setState(189);
			endpointDecl();
			setState(190);
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
		enterRule(_localctx, 34, RULE_relationKind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(192);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22))) != 0)) ) {
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
		enterRule(_localctx, 36, RULE_endpointDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(IDENT);
			setState(195);
			cardinality();
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8 || _la==IDENT) {
				{
				setState(197);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__8) {
					{
					setState(196);
					match(T__8);
					}
				}

				setState(199);
				match(IDENT);
				}
			}

			setState(202);
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
		public LinkArrowContext linkArrow() {
			return getRuleContext(LinkArrowContext.class,0);
		}
		public List<CompatibilityOptionContext> compatibilityOption() {
			return getRuleContexts(CompatibilityOptionContext.class);
		}
		public CompatibilityOptionContext compatibilityOption(int i) {
			return getRuleContext(CompatibilityOptionContext.class,i);
		}
		public LinkScopeContext linkScope() {
			return getRuleContext(LinkScopeContext.class,0);
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
		enterRule(_localctx, 38, RULE_compatibilityDecl);
		int _la;
		try {
			setState(233);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__23:
				enterOuterAlt(_localctx, 1);
				{
				setState(204);
				match(T__23);
				setState(205);
				match(IDENT);
				setState(206);
				linkArrow();
				setState(207);
				match(IDENT);
				setState(211);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__30) | (1L << T__31) | (1L << T__32) | (1L << T__33))) != 0)) {
					{
					{
					setState(208);
					compatibilityOption();
					}
					}
					setState(213);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(214);
				match(T__6);
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 2);
				{
				setState(216);
				match(T__24);
				setState(217);
				match(T__23);
				setState(218);
				match(IDENT);
				setState(219);
				linkArrow();
				setState(220);
				match(IDENT);
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__26 || _la==T__29) {
					{
					setState(221);
					linkScope();
					}
				}

				setState(224);
				match(T__6);
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 3);
				{
				setState(226);
				match(IDENT);
				setState(227);
				match(T__25);
				setState(228);
				match(IDENT);
				setState(230);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__26) {
					{
					setState(229);
					match(T__26);
					}
				}

				setState(232);
				match(T__6);
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

	public static class LinkArrowContext extends ParserRuleContext {
		public LinkArrowContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkArrow; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterLinkArrow(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitLinkArrow(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitLinkArrow(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkArrowContext linkArrow() throws RecognitionException {
		LinkArrowContext _localctx = new LinkArrowContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_linkArrow);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_la = _input.LA(1);
			if ( !(_la==T__27 || _la==T__28) ) {
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

	public static class LinkScopeContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public LinkScopeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkScope; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterLinkScope(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitLinkScope(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitLinkScope(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkScopeContext linkScope() throws RecognitionException {
		LinkScopeContext _localctx = new LinkScopeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_linkScope);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_la = _input.LA(1);
			if ( !(_la==T__26 || _la==T__29) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(238);
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

	public static class CompatibilityOptionContext extends ParserRuleContext {
		public ScopeValueContext scopeValue() {
			return getRuleContext(ScopeValueContext.class,0);
		}
		public TerminalNode BOOLEAN() { return getToken(ACLParser.BOOLEAN, 0); }
		public CompatibilityTypeContext compatibilityType() {
			return getRuleContext(CompatibilityTypeContext.class,0);
		}
		public CompatibilityOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compatibilityOption; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterCompatibilityOption(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitCompatibilityOption(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitCompatibilityOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompatibilityOptionContext compatibilityOption() throws RecognitionException {
		CompatibilityOptionContext _localctx = new CompatibilityOptionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_compatibilityOption);
		try {
			setState(248);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__30:
				enterOuterAlt(_localctx, 1);
				{
				setState(240);
				match(T__30);
				setState(241);
				scopeValue();
				}
				break;
			case T__31:
				enterOuterAlt(_localctx, 2);
				{
				setState(242);
				match(T__31);
				setState(243);
				match(BOOLEAN);
				}
				break;
			case T__32:
				enterOuterAlt(_localctx, 3);
				{
				setState(244);
				match(T__32);
				setState(245);
				match(BOOLEAN);
				}
				break;
			case T__33:
				enterOuterAlt(_localctx, 4);
				{
				setState(246);
				match(T__33);
				setState(247);
				compatibilityType();
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

	public static class CompatibilityTypeContext extends ParserRuleContext {
		public CompatibilityTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compatibilityType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterCompatibilityType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitCompatibilityType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitCompatibilityType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompatibilityTypeContext compatibilityType() throws RecognitionException {
		CompatibilityTypeContext _localctx = new CompatibilityTypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_compatibilityType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			_la = _input.LA(1);
			if ( !(_la==T__25 || _la==T__34) ) {
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

	public static class ScopeValueContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(ACLParser.IDENT, 0); }
		public ScopeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_scopeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).enterScopeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ACLListener ) ((ACLListener)listener).exitScopeValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ACLVisitor ) return ((ACLVisitor<? extends T>)visitor).visitScopeValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScopeValueContext scopeValue() throws RecognitionException {
		ScopeValueContext _localctx = new ScopeValueContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_scopeValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__35) | (1L << T__36) | (1L << IDENT))) != 0)) ) {
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
		enterRule(_localctx, 50, RULE_cardinality);
		int _la;
		try {
			setState(265);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(254);
				match(T__37);
				setState(255);
				match(INT);
				setState(256);
				match(T__38);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(257);
				match(T__37);
				setState(258);
				match(INT);
				setState(259);
				match(T__39);
				setState(260);
				_la = _input.LA(1);
				if ( !(_la==T__40 || _la==INT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(261);
				match(T__38);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(262);
				match(T__37);
				setState(263);
				match(T__40);
				setState(264);
				match(T__38);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\64\u010e\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\2\7\2<\n\2\f\2\16\2?\13\2\3\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\5\3J\n\3\3\4\3\4\3\4\3\4\3\4\3\4\7\4R\n"+
		"\4\f\4\16\4U\13\4\3\4\5\4X\n\4\3\4\3\4\3\5\3\5\3\5\5\5_\n\5\3\5\3\5\5"+
		"\5c\n\5\3\6\5\6f\n\6\3\6\3\6\3\6\5\6k\n\6\3\6\3\6\5\6o\n\6\3\7\3\7\3\7"+
		"\3\b\3\b\7\bv\n\b\f\b\16\by\13\b\3\b\3\b\3\t\5\t~\n\t\3\t\3\t\3\t\3\t"+
		"\7\t\u0084\n\t\f\t\16\t\u0087\13\t\3\t\5\t\u008a\n\t\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u0099\n\r\f\r\16\r\u009c\13"+
		"\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\5\16\u00a5\n\16\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\7\21\u00b5\n\21\f\21"+
		"\16\21\u00b8\13\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3"+
		"\23\3\24\3\24\3\24\5\24\u00c8\n\24\3\24\5\24\u00cb\n\24\3\24\3\24\3\25"+
		"\3\25\3\25\3\25\3\25\7\25\u00d4\n\25\f\25\16\25\u00d7\13\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00e1\n\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\5\25\u00e9\n\25\3\25\5\25\u00ec\n\25\3\26\3\26\3\27\3\27\3\27\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00fb\n\30\3\31\3\31\3\32"+
		"\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u010c"+
		"\n\33\3\33\2\2\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62"+
		"\64\2\f\3\2\f\r\3\2\20\21\3\2-\61\4\2\b\b\13\13\3\2\25\31\3\2\36\37\4"+
		"\2\35\35  \4\2\34\34%%\4\2&\'\61\61\4\2++..\2\u0117\2\66\3\2\2\2\4I\3"+
		"\2\2\2\6K\3\2\2\2\b[\3\2\2\2\ne\3\2\2\2\fp\3\2\2\2\16s\3\2\2\2\20}\3\2"+
		"\2\2\22\u008d\3\2\2\2\24\u008f\3\2\2\2\26\u0092\3\2\2\2\30\u0094\3\2\2"+
		"\2\32\u00a4\3\2\2\2\34\u00a6\3\2\2\2\36\u00aa\3\2\2\2 \u00af\3\2\2\2\""+
		"\u00bb\3\2\2\2$\u00c2\3\2\2\2&\u00c4\3\2\2\2(\u00eb\3\2\2\2*\u00ed\3\2"+
		"\2\2,\u00ef\3\2\2\2.\u00fa\3\2\2\2\60\u00fc\3\2\2\2\62\u00fe\3\2\2\2\64"+
		"\u010b\3\2\2\2\66\67\7\3\2\2\678\7,\2\289\7\61\2\29=\7\4\2\2:<\5\4\3\2"+
		";:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>@\3\2\2\2?=\3\2\2\2@A\7\5\2\2"+
		"AB\7\2\2\3B\3\3\2\2\2CJ\5\6\4\2DJ\5\b\5\2EJ\5\n\6\2FJ\5\30\r\2GJ\5\"\22"+
		"\2HJ\5(\25\2IC\3\2\2\2ID\3\2\2\2IE\3\2\2\2IF\3\2\2\2IG\3\2\2\2IH\3\2\2"+
		"\2J\5\3\2\2\2KL\7\6\2\2LM\7\61\2\2MN\7\4\2\2NS\7\61\2\2OP\7\7\2\2PR\7"+
		"\61\2\2QO\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TW\3\2\2\2US\3\2\2\2VX"+
		"\7\7\2\2WV\3\2\2\2WX\3\2\2\2XY\3\2\2\2YZ\7\5\2\2Z\7\3\2\2\2[\\\7\b\2\2"+
		"\\^\7\61\2\2]_\5\f\7\2^]\3\2\2\2^_\3\2\2\2_b\3\2\2\2`c\7\t\2\2ac\5\16"+
		"\b\2b`\3\2\2\2ba\3\2\2\2c\t\3\2\2\2df\7\n\2\2ed\3\2\2\2ef\3\2\2\2fg\3"+
		"\2\2\2gh\7\13\2\2hj\7\61\2\2ik\5\f\7\2ji\3\2\2\2jk\3\2\2\2kn\3\2\2\2l"+
		"o\7\t\2\2mo\5\16\b\2nl\3\2\2\2nm\3\2\2\2no\3\2\2\2o\13\3\2\2\2pq\t\2\2"+
		"\2qr\7\61\2\2r\r\3\2\2\2sw\7\4\2\2tv\5\20\t\2ut\3\2\2\2vy\3\2\2\2wu\3"+
		"\2\2\2wx\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7\5\2\2{\17\3\2\2\2|~\7\16\2\2"+
		"}|\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080\7\61\2\2\u0080\u0081\7\17"+
		"\2\2\u0081\u0085\7\61\2\2\u0082\u0084\5\22\n\2\u0083\u0082\3\2\2\2\u0084"+
		"\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0089\3\2"+
		"\2\2\u0087\u0085\3\2\2\2\u0088\u008a\5\24\13\2\u0089\u0088\3\2\2\2\u0089"+
		"\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\7\t\2\2\u008c\21\3\2\2"+
		"\2\u008d\u008e\t\3\2\2\u008e\23\3\2\2\2\u008f\u0090\7\22\2\2\u0090\u0091"+
		"\5\26\f\2\u0091\25\3\2\2\2\u0092\u0093\t\4\2\2\u0093\27\3\2\2\2\u0094"+
		"\u0095\7\23\2\2\u0095\u0096\7\61\2\2\u0096\u009a\7\4\2\2\u0097\u0099\5"+
		"\32\16\2\u0098\u0097\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a"+
		"\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009a\3\2\2\2\u009d\u009e\7\5"+
		"\2\2\u009e\31\3\2\2\2\u009f\u00a5\5\20\t\2\u00a0\u00a5\5\34\17\2\u00a1"+
		"\u00a5\5\36\20\2\u00a2\u00a5\5 \21\2\u00a3\u00a5\5(\25\2\u00a4\u009f\3"+
		"\2\2\2\u00a4\u00a0\3\2\2\2\u00a4\u00a1\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a4"+
		"\u00a3\3\2\2\2\u00a5\33\3\2\2\2\u00a6\u00a7\7\61\2\2\u00a7\u00a8\5\64"+
		"\33\2\u00a8\u00a9\7\t\2\2\u00a9\35\3\2\2\2\u00aa\u00ab\t\5\2\2\u00ab\u00ac"+
		"\7\61\2\2\u00ac\u00ad\5\64\33\2\u00ad\u00ae\7\t\2\2\u00ae\37\3\2\2\2\u00af"+
		"\u00b0\7\24\2\2\u00b0\u00b1\7\61\2\2\u00b1\u00b2\5\64\33\2\u00b2\u00b6"+
		"\7\4\2\2\u00b3\u00b5\5\32\16\2\u00b4\u00b3\3\2\2\2\u00b5\u00b8\3\2\2\2"+
		"\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b9\3\2\2\2\u00b8\u00b6"+
		"\3\2\2\2\u00b9\u00ba\7\5\2\2\u00ba!\3\2\2\2\u00bb\u00bc\5$\23\2\u00bc"+
		"\u00bd\7\61\2\2\u00bd\u00be\7\4\2\2\u00be\u00bf\5&\24\2\u00bf\u00c0\5"+
		"&\24\2\u00c0\u00c1\7\5\2\2\u00c1#\3\2\2\2\u00c2\u00c3\t\6\2\2\u00c3%\3"+
		"\2\2\2\u00c4\u00c5\7\61\2\2\u00c5\u00ca\5\64\33\2\u00c6\u00c8\7\13\2\2"+
		"\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cb"+
		"\7\61\2\2\u00ca\u00c7\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\3\2\2\2"+
		"\u00cc\u00cd\7\t\2\2\u00cd\'\3\2\2\2\u00ce\u00cf\7\32\2\2\u00cf\u00d0"+
		"\7\61\2\2\u00d0\u00d1\5*\26\2\u00d1\u00d5\7\61\2\2\u00d2\u00d4\5.\30\2"+
		"\u00d3\u00d2\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6"+
		"\3\2\2\2\u00d6\u00d8\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00d9\7\t\2\2\u00d9"+
		"\u00ec\3\2\2\2\u00da\u00db\7\33\2\2\u00db\u00dc\7\32\2\2\u00dc\u00dd\7"+
		"\61\2\2\u00dd\u00de\5*\26\2\u00de\u00e0\7\61\2\2\u00df\u00e1\5,\27\2\u00e0"+
		"\u00df\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2\u00e3\7\t"+
		"\2\2\u00e3\u00ec\3\2\2\2\u00e4\u00e5\7\61\2\2\u00e5\u00e6\7\34\2\2\u00e6"+
		"\u00e8\7\61\2\2\u00e7\u00e9\7\35\2\2\u00e8\u00e7\3\2\2\2\u00e8\u00e9\3"+
		"\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00ec\7\t\2\2\u00eb\u00ce\3\2\2\2\u00eb"+
		"\u00da\3\2\2\2\u00eb\u00e4\3\2\2\2\u00ec)\3\2\2\2\u00ed\u00ee\t\7\2\2"+
		"\u00ee+\3\2\2\2\u00ef\u00f0\t\b\2\2\u00f0\u00f1\7\61\2\2\u00f1-\3\2\2"+
		"\2\u00f2\u00f3\7!\2\2\u00f3\u00fb\5\62\32\2\u00f4\u00f5\7\"\2\2\u00f5"+
		"\u00fb\7-\2\2\u00f6\u00f7\7#\2\2\u00f7\u00fb\7-\2\2\u00f8\u00f9\7$\2\2"+
		"\u00f9\u00fb\5\60\31\2\u00fa\u00f2\3\2\2\2\u00fa\u00f4\3\2\2\2\u00fa\u00f6"+
		"\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fb/\3\2\2\2\u00fc\u00fd\t\t\2\2\u00fd"+
		"\61\3\2\2\2\u00fe\u00ff\t\n\2\2\u00ff\63\3\2\2\2\u0100\u0101\7(\2\2\u0101"+
		"\u0102\7.\2\2\u0102\u010c\7)\2\2\u0103\u0104\7(\2\2\u0104\u0105\7.\2\2"+
		"\u0105\u0106\7*\2\2\u0106\u0107\t\13\2\2\u0107\u010c\7)\2\2\u0108\u0109"+
		"\7(\2\2\u0109\u010a\7+\2\2\u010a\u010c\7)\2\2\u010b\u0100\3\2\2\2\u010b"+
		"\u0103\3\2\2\2\u010b\u0108\3\2\2\2\u010c\65\3\2\2\2\32=ISW^bejnw}\u0085"+
		"\u0089\u009a\u00a4\u00b6\u00c7\u00ca\u00d5\u00e0\u00e8\u00eb\u00fa\u010b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}