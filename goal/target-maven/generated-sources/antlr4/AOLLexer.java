// Generated from AOL.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.aol.parser; 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AOLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, VERSION=17, 
		BOOLEAN=18, SIGNED_NUMBER=19, STRING_LITERAL=20, IDENT=21, WS=22, LINE_COMMENT=23, 
		BLOCK_COMMENT=24;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "VERSION", 
			"BOOLEAN", "SIGNED_NUMBER", "STRING_LITERAL", "IDENT", "WS", "LINE_COMMENT", 
			"BLOCK_COMMENT"
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


	public AOLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AOL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\32\u00c6\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16"+
		"\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3\22\6\22p\n\22\r\22"+
		"\16\22q\3\22\3\22\6\22v\n\22\r\22\16\22w\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\5\23\u0083\n\23\3\24\5\24\u0086\n\24\3\24\6\24\u0089\n"+
		"\24\r\24\16\24\u008a\3\24\3\24\6\24\u008f\n\24\r\24\16\24\u0090\5\24\u0093"+
		"\n\24\3\25\3\25\3\25\3\25\7\25\u0099\n\25\f\25\16\25\u009c\13\25\3\25"+
		"\3\25\3\26\3\26\7\26\u00a2\n\26\f\26\16\26\u00a5\13\26\3\27\6\27\u00a8"+
		"\n\27\r\27\16\27\u00a9\3\27\3\27\3\30\3\30\3\30\3\30\7\30\u00b2\n\30\f"+
		"\30\16\30\u00b5\13\30\3\30\3\30\3\31\3\31\3\31\3\31\7\31\u00bd\n\31\f"+
		"\31\16\31\u00c0\13\31\3\31\3\31\3\31\3\31\3\31\3\u00be\2\32\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\3\2\b\3\2\62;\6\2\f\f\17\17$$^^\5"+
		"\2C\\aac|\6\2\62;C\\aac|\5\2\13\f\16\17\"\"\4\2\f\f\17\17\2\u00d2\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2"+
		"\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2"+
		"\61\3\2\2\2\3\63\3\2\2\2\5\67\3\2\2\2\7;\3\2\2\2\t=\3\2\2\2\13?\3\2\2"+
		"\2\rE\3\2\2\2\17H\3\2\2\2\21J\3\2\2\2\23L\3\2\2\2\25R\3\2\2\2\27W\3\2"+
		"\2\2\31Z\3\2\2\2\33a\3\2\2\2\35f\3\2\2\2\37h\3\2\2\2!k\3\2\2\2#m\3\2\2"+
		"\2%\u0082\3\2\2\2\'\u0085\3\2\2\2)\u0094\3\2\2\2+\u009f\3\2\2\2-\u00a7"+
		"\3\2\2\2/\u00ad\3\2\2\2\61\u00b8\3\2\2\2\63\64\7c\2\2\64\65\7q\2\2\65"+
		"\66\7n\2\2\66\4\3\2\2\2\678\7h\2\289\7q\2\29:\7t\2\2:\6\3\2\2\2;<\7}\2"+
		"\2<\b\3\2\2\2=>\7\177\2\2>\n\3\2\2\2?@\7c\2\2@A\7i\2\2AB\7g\2\2BC\7p\2"+
		"\2CD\7v\2\2D\f\3\2\2\2EF\7c\2\2FG\7u\2\2G\16\3\2\2\2HI\7.\2\2I\20\3\2"+
		"\2\2JK\7=\2\2K\22\3\2\2\2LM\7i\2\2MN\7t\2\2NO\7q\2\2OP\7w\2\2PQ\7r\2\2"+
		"Q\24\3\2\2\2RS\7r\2\2ST\7n\2\2TU\7c\2\2UV\7{\2\2V\26\3\2\2\2WX\7d\2\2"+
		"XY\7{\2\2Y\30\3\2\2\2Z[\7g\2\2[\\\7p\2\2\\]\7v\2\2]^\7k\2\2^_\7v\2\2_"+
		"`\7{\2\2`\32\3\2\2\2ab\7n\2\2bc\7k\2\2cd\7p\2\2de\7m\2\2e\34\3\2\2\2f"+
		"g\7<\2\2g\36\3\2\2\2hi\7/\2\2ij\7@\2\2j \3\2\2\2kl\7?\2\2l\"\3\2\2\2m"+
		"o\7x\2\2np\t\2\2\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3\2\2\2rs\3\2\2\2s"+
		"u\7\60\2\2tv\t\2\2\2ut\3\2\2\2vw\3\2\2\2wu\3\2\2\2wx\3\2\2\2x$\3\2\2\2"+
		"yz\7v\2\2z{\7t\2\2{|\7w\2\2|\u0083\7g\2\2}~\7h\2\2~\177\7c\2\2\177\u0080"+
		"\7n\2\2\u0080\u0081\7u\2\2\u0081\u0083\7g\2\2\u0082y\3\2\2\2\u0082}\3"+
		"\2\2\2\u0083&\3\2\2\2\u0084\u0086\7/\2\2\u0085\u0084\3\2\2\2\u0085\u0086"+
		"\3\2\2\2\u0086\u0088\3\2\2\2\u0087\u0089\t\2\2\2\u0088\u0087\3\2\2\2\u0089"+
		"\u008a\3\2\2\2\u008a\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0092\3\2"+
		"\2\2\u008c\u008e\7\60\2\2\u008d\u008f\t\2\2\2\u008e\u008d\3\2\2\2\u008f"+
		"\u0090\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2"+
		"\2\2\u0092\u008c\3\2\2\2\u0092\u0093\3\2\2\2\u0093(\3\2\2\2\u0094\u009a"+
		"\7$\2\2\u0095\u0096\7^\2\2\u0096\u0099\13\2\2\2\u0097\u0099\n\3\2\2\u0098"+
		"\u0095\3\2\2\2\u0098\u0097\3\2\2\2\u0099\u009c\3\2\2\2\u009a\u0098\3\2"+
		"\2\2\u009a\u009b\3\2\2\2\u009b\u009d\3\2\2\2\u009c\u009a\3\2\2\2\u009d"+
		"\u009e\7$\2\2\u009e*\3\2\2\2\u009f\u00a3\t\4\2\2\u00a0\u00a2\t\5\2\2\u00a1"+
		"\u00a0\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4\3\2"+
		"\2\2\u00a4,\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00a8\t\6\2\2\u00a7\u00a6"+
		"\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa"+
		"\u00ab\3\2\2\2\u00ab\u00ac\b\27\2\2\u00ac.\3\2\2\2\u00ad\u00ae\7\61\2"+
		"\2\u00ae\u00af\7\61\2\2\u00af\u00b3\3\2\2\2\u00b0\u00b2\n\7\2\2\u00b1"+
		"\u00b0\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3\u00b4\3\2"+
		"\2\2\u00b4\u00b6\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6\u00b7\b\30\2\2\u00b7"+
		"\60\3\2\2\2\u00b8\u00b9\7\61\2\2\u00b9\u00ba\7,\2\2\u00ba\u00be\3\2\2"+
		"\2\u00bb\u00bd\13\2\2\2\u00bc\u00bb\3\2\2\2\u00bd\u00c0\3\2\2\2\u00be"+
		"\u00bf\3\2\2\2\u00be\u00bc\3\2\2\2\u00bf\u00c1\3\2\2\2\u00c0\u00be\3\2"+
		"\2\2\u00c1\u00c2\7,\2\2\u00c2\u00c3\7\61\2\2\u00c3\u00c4\3\2\2\2\u00c4"+
		"\u00c5\b\31\2\2\u00c5\62\3\2\2\2\20\2qw\u0082\u0085\u008a\u0090\u0092"+
		"\u0098\u009a\u00a3\u00a9\u00b3\u00be\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}