// Generated from AOL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.aol.parser; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AOLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, VERSION=17, 
		BOOLEAN=18, SIGNED_NUMBER=19, STRING_LITERAL=20, IDENT=21, WS=22, LINE_COMMENT=23, 
		BLOCK_COMMENT=24;
	public static final int
		RULE_model = 0, RULE_topLevelDecl = 1, RULE_agentDecl = 2, RULE_groupInstanceDecl = 3, 
		RULE_groupItemDecl = 4, RULE_playDecl = 5, RULE_entityInstanceDecl = 6, 
		RULE_linkDecl = 7, RULE_attributeValueBlock = 8, RULE_attributeValue = 9, 
		RULE_value = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "topLevelDecl", "agentDecl", "groupInstanceDecl", "groupItemDecl", 
			"playDecl", "entityInstanceDecl", "linkDecl", "attributeValueBlock", 
			"attributeValue", "value"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'aol'", "'for'", "'{'", "'}'", "'agent'", "'as'", "','", "';'", 
			"'group'", "'play'", "'by'", "'entity'", "'link'", "':'", "'->'", "'='"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, "VERSION", "BOOLEAN", "SIGNED_NUMBER", 
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
	public String getGrammarFileName() { return "AOL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public AOLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ModelContext extends ParserRuleContext {
		public TerminalNode VERSION() { return getToken(AOLParser.VERSION, 0); }
		public TerminalNode IDENT() { return getToken(AOLParser.IDENT, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(AOLParser.STRING_LITERAL, 0); }
		public TerminalNode EOF() { return getToken(AOLParser.EOF, 0); }
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
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitModel(this);
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
			setState(22);
			match(T__0);
			setState(23);
			match(VERSION);
			setState(24);
			match(IDENT);
			setState(25);
			match(T__1);
			setState(26);
			match(STRING_LITERAL);
			setState(27);
			match(T__2);
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__8) | (1L << T__11) | (1L << T__12))) != 0)) {
				{
				{
				setState(28);
				topLevelDecl();
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
			match(T__3);
			setState(35);
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
		public AgentDeclContext agentDecl() {
			return getRuleContext(AgentDeclContext.class,0);
		}
		public GroupInstanceDeclContext groupInstanceDecl() {
			return getRuleContext(GroupInstanceDeclContext.class,0);
		}
		public EntityInstanceDeclContext entityInstanceDecl() {
			return getRuleContext(EntityInstanceDeclContext.class,0);
		}
		public LinkDeclContext linkDecl() {
			return getRuleContext(LinkDeclContext.class,0);
		}
		public TopLevelDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_topLevelDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterTopLevelDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitTopLevelDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitTopLevelDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TopLevelDeclContext topLevelDecl() throws RecognitionException {
		TopLevelDeclContext _localctx = new TopLevelDeclContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_topLevelDecl);
		try {
			setState(41);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__4:
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				agentDecl();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				groupInstanceDecl();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				entityInstanceDecl();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 4);
				{
				setState(40);
				linkDecl();
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

	public static class AgentDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(AOLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AOLParser.IDENT, i);
		}
		public AttributeValueBlockContext attributeValueBlock() {
			return getRuleContext(AttributeValueBlockContext.class,0);
		}
		public AgentDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agentDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterAgentDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitAgentDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitAgentDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgentDeclContext agentDecl() throws RecognitionException {
		AgentDeclContext _localctx = new AgentDeclContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_agentDecl);
		int _la;
		try {
			setState(58);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(43);
				match(T__4);
				setState(44);
				match(IDENT);
				setState(45);
				match(T__5);
				setState(46);
				match(IDENT);
				setState(47);
				attributeValueBlock();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				match(T__4);
				setState(49);
				match(IDENT);
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__6) {
					{
					{
					setState(50);
					match(T__6);
					setState(51);
					match(IDENT);
					}
					}
					setState(56);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(57);
				match(T__7);
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

	public static class GroupInstanceDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(AOLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AOLParser.IDENT, i);
		}
		public List<GroupItemDeclContext> groupItemDecl() {
			return getRuleContexts(GroupItemDeclContext.class);
		}
		public GroupItemDeclContext groupItemDecl(int i) {
			return getRuleContext(GroupItemDeclContext.class,i);
		}
		public GroupInstanceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupInstanceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterGroupInstanceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitGroupInstanceDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitGroupInstanceDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupInstanceDeclContext groupInstanceDecl() throws RecognitionException {
		GroupInstanceDeclContext _localctx = new GroupInstanceDeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_groupInstanceDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(60);
			match(T__8);
			setState(61);
			match(IDENT);
			setState(62);
			match(T__5);
			setState(63);
			match(IDENT);
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				{
				setState(64);
				match(T__7);
				}
				break;
			case T__2:
				{
				setState(65);
				match(T__2);
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__11) | (1L << IDENT))) != 0)) {
					{
					{
					setState(66);
					groupItemDecl();
					}
					}
					setState(71);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(72);
				match(T__3);
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

	public static class GroupItemDeclContext extends ParserRuleContext {
		public GroupInstanceDeclContext groupInstanceDecl() {
			return getRuleContext(GroupInstanceDeclContext.class,0);
		}
		public PlayDeclContext playDecl() {
			return getRuleContext(PlayDeclContext.class,0);
		}
		public EntityInstanceDeclContext entityInstanceDecl() {
			return getRuleContext(EntityInstanceDeclContext.class,0);
		}
		public AttributeValueContext attributeValue() {
			return getRuleContext(AttributeValueContext.class,0);
		}
		public GroupItemDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupItemDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterGroupItemDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitGroupItemDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitGroupItemDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupItemDeclContext groupItemDecl() throws RecognitionException {
		GroupItemDeclContext _localctx = new GroupItemDeclContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_groupItemDecl);
		try {
			setState(79);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				enterOuterAlt(_localctx, 1);
				{
				setState(75);
				groupInstanceDecl();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(76);
				playDecl();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 3);
				{
				setState(77);
				entityInstanceDecl();
				}
				break;
			case IDENT:
				enterOuterAlt(_localctx, 4);
				{
				setState(78);
				attributeValue();
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

	public static class PlayDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(AOLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AOLParser.IDENT, i);
		}
		public AttributeValueBlockContext attributeValueBlock() {
			return getRuleContext(AttributeValueBlockContext.class,0);
		}
		public PlayDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_playDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterPlayDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitPlayDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitPlayDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PlayDeclContext playDecl() throws RecognitionException {
		PlayDeclContext _localctx = new PlayDeclContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_playDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			match(T__9);
			setState(82);
			match(IDENT);
			setState(83);
			match(T__5);
			setState(84);
			match(IDENT);
			setState(85);
			match(T__10);
			setState(86);
			match(IDENT);
			setState(89);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				{
				setState(87);
				match(T__7);
				}
				break;
			case T__2:
				{
				setState(88);
				attributeValueBlock();
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

	public static class EntityInstanceDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(AOLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AOLParser.IDENT, i);
		}
		public AttributeValueBlockContext attributeValueBlock() {
			return getRuleContext(AttributeValueBlockContext.class,0);
		}
		public EntityInstanceDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityInstanceDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterEntityInstanceDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitEntityInstanceDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitEntityInstanceDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityInstanceDeclContext entityInstanceDecl() throws RecognitionException {
		EntityInstanceDeclContext _localctx = new EntityInstanceDeclContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_entityInstanceDecl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(T__11);
			setState(92);
			match(IDENT);
			setState(93);
			match(T__5);
			setState(94);
			match(IDENT);
			setState(97);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				{
				setState(95);
				match(T__7);
				}
				break;
			case T__2:
				{
				setState(96);
				attributeValueBlock();
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

	public static class LinkDeclContext extends ParserRuleContext {
		public List<TerminalNode> IDENT() { return getTokens(AOLParser.IDENT); }
		public TerminalNode IDENT(int i) {
			return getToken(AOLParser.IDENT, i);
		}
		public LinkDeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_linkDecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterLinkDecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitLinkDecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitLinkDecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LinkDeclContext linkDecl() throws RecognitionException {
		LinkDeclContext _localctx = new LinkDeclContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_linkDecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(T__12);
			setState(100);
			match(IDENT);
			setState(101);
			match(T__13);
			setState(102);
			match(IDENT);
			setState(103);
			match(T__14);
			setState(104);
			match(IDENT);
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(105);
				match(T__6);
				setState(106);
				match(IDENT);
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(112);
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

	public static class AttributeValueBlockContext extends ParserRuleContext {
		public List<AttributeValueContext> attributeValue() {
			return getRuleContexts(AttributeValueContext.class);
		}
		public AttributeValueContext attributeValue(int i) {
			return getRuleContext(AttributeValueContext.class,i);
		}
		public AttributeValueBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeValueBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterAttributeValueBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitAttributeValueBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitAttributeValueBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeValueBlockContext attributeValueBlock() throws RecognitionException {
		AttributeValueBlockContext _localctx = new AttributeValueBlockContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_attributeValueBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			match(T__2);
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENT) {
				{
				{
				setState(115);
				attributeValue();
				}
				}
				setState(120);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(121);
			match(T__3);
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

	public static class AttributeValueContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(AOLParser.IDENT, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public AttributeValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attributeValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterAttributeValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitAttributeValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitAttributeValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AttributeValueContext attributeValue() throws RecognitionException {
		AttributeValueContext _localctx = new AttributeValueContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_attributeValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(IDENT);
			setState(124);
			match(T__15);
			setState(125);
			value();
			setState(126);
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

	public static class ValueContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(AOLParser.STRING_LITERAL, 0); }
		public TerminalNode SIGNED_NUMBER() { return getToken(AOLParser.SIGNED_NUMBER, 0); }
		public TerminalNode BOOLEAN() { return getToken(AOLParser.BOOLEAN, 0); }
		public TerminalNode IDENT() { return getToken(AOLParser.IDENT, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof AOLListener ) ((AOLListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof AOLVisitor ) return ((AOLVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOOLEAN) | (1L << SIGNED_NUMBER) | (1L << STRING_LITERAL) | (1L << IDENT))) != 0)) ) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\u0085\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\3\2\3\2\3\2\3\2\3\2\3\2\7\2 \n\2\f\2\16\2#\13\2\3\2"+
		"\3\2\3\2\3\3\3\3\3\3\3\3\5\3,\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\7\4\67\n\4\f\4\16\4:\13\4\3\4\5\4=\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7"+
		"\5F\n\5\f\5\16\5I\13\5\3\5\5\5L\n\5\3\6\3\6\3\6\3\6\5\6R\n\6\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7\\\n\7\3\b\3\b\3\b\3\b\3\b\3\b\5\bd\n\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\7\tn\n\t\f\t\16\tq\13\t\3\t\3\t\3\n\3\n\7"+
		"\nw\n\n\f\n\16\nz\13\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\2"+
		"\2\r\2\4\6\b\n\f\16\20\22\24\26\2\3\3\2\24\27\2\u0088\2\30\3\2\2\2\4+"+
		"\3\2\2\2\6<\3\2\2\2\b>\3\2\2\2\nQ\3\2\2\2\fS\3\2\2\2\16]\3\2\2\2\20e\3"+
		"\2\2\2\22t\3\2\2\2\24}\3\2\2\2\26\u0082\3\2\2\2\30\31\7\3\2\2\31\32\7"+
		"\23\2\2\32\33\7\27\2\2\33\34\7\4\2\2\34\35\7\26\2\2\35!\7\5\2\2\36 \5"+
		"\4\3\2\37\36\3\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2\2#!\3\2"+
		"\2\2$%\7\6\2\2%&\7\2\2\3&\3\3\2\2\2\',\5\6\4\2(,\5\b\5\2),\5\16\b\2*,"+
		"\5\20\t\2+\'\3\2\2\2+(\3\2\2\2+)\3\2\2\2+*\3\2\2\2,\5\3\2\2\2-.\7\7\2"+
		"\2./\7\27\2\2/\60\7\b\2\2\60\61\7\27\2\2\61=\5\22\n\2\62\63\7\7\2\2\63"+
		"8\7\27\2\2\64\65\7\t\2\2\65\67\7\27\2\2\66\64\3\2\2\2\67:\3\2\2\28\66"+
		"\3\2\2\289\3\2\2\29;\3\2\2\2:8\3\2\2\2;=\7\n\2\2<-\3\2\2\2<\62\3\2\2\2"+
		"=\7\3\2\2\2>?\7\13\2\2?@\7\27\2\2@A\7\b\2\2AK\7\27\2\2BL\7\n\2\2CG\7\5"+
		"\2\2DF\5\n\6\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3\2\2\2IG\3\2"+
		"\2\2JL\7\6\2\2KB\3\2\2\2KC\3\2\2\2L\t\3\2\2\2MR\5\b\5\2NR\5\f\7\2OR\5"+
		"\16\b\2PR\5\24\13\2QM\3\2\2\2QN\3\2\2\2QO\3\2\2\2QP\3\2\2\2R\13\3\2\2"+
		"\2ST\7\f\2\2TU\7\27\2\2UV\7\b\2\2VW\7\27\2\2WX\7\r\2\2X[\7\27\2\2Y\\\7"+
		"\n\2\2Z\\\5\22\n\2[Y\3\2\2\2[Z\3\2\2\2\\\r\3\2\2\2]^\7\16\2\2^_\7\27\2"+
		"\2_`\7\b\2\2`c\7\27\2\2ad\7\n\2\2bd\5\22\n\2ca\3\2\2\2cb\3\2\2\2d\17\3"+
		"\2\2\2ef\7\17\2\2fg\7\27\2\2gh\7\20\2\2hi\7\27\2\2ij\7\21\2\2jo\7\27\2"+
		"\2kl\7\t\2\2ln\7\27\2\2mk\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2pr\3\2"+
		"\2\2qo\3\2\2\2rs\7\n\2\2s\21\3\2\2\2tx\7\5\2\2uw\5\24\13\2vu\3\2\2\2w"+
		"z\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zx\3\2\2\2{|\7\6\2\2|\23\3\2\2"+
		"\2}~\7\27\2\2~\177\7\22\2\2\177\u0080\5\26\f\2\u0080\u0081\7\n\2\2\u0081"+
		"\25\3\2\2\2\u0082\u0083\t\2\2\2\u0083\27\3\2\2\2\r!+8<GKQ[cox";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}