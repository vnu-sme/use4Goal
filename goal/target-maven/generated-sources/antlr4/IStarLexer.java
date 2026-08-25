// Generated from IStar.g4 by ANTLR 4.9.3
 package org.vnu.sme.goal.dsl.istar.parser; 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IStarLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, OCL_CLAUSE=33, OCL_BLOCK=34, IDENT=35, WS=36, LINE_COMMENT=37, 
		BLOCK_COMMENT=38;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "T__21", "T__22", "T__23", "T__24", 
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "T__31", "OCL_CLAUSE", 
			"OCL_BLOCK", "OCL_DQ_STRING", "OCL_SQ_STRING", "IDENT", "WS", "LINE_COMMENT", 
			"BLOCK_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'istar'", "'{'", "'}'", "'role'", "'agent'", "'goal'", "'task'", 
			"'resource'", "'quality'", "'is-a'", "'participates-in'", "':'", "'Achieve'", 
			"'Maintain'", "'Sustain'", "'Recur'", "'>'", "'or'", "'qualifies'", "'needed-by'", 
			"'depend'", "'->'", "'.'", "'make'", "'help'", "'hurt'", "'break'", "'pre'", 
			"'post'", "'condition'", "'satisfy'", "'ensure'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "OCL_CLAUSE", "OCL_BLOCK", 
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


	public IStarLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "IStar.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2(\u0183\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\22\3\22\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\37"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \3 "+
		"\3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\7\"\u011b\n\"\f\"\16\"\u011e"+
		"\13\"\3\"\3\"\3\"\3\"\7\"\u0124\n\"\f\"\16\"\u0127\13\"\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\7\"\u0133\n\"\f\"\16\"\u0136\13\"\3\"\5\"\u0139"+
		"\n\"\3#\3#\3#\3#\7#\u013f\n#\f#\16#\u0142\13#\3#\3#\3#\3$\3$\3$\3$\7$"+
		"\u014b\n$\f$\16$\u014e\13$\3$\3$\3%\3%\3%\3%\7%\u0156\n%\f%\16%\u0159"+
		"\13%\3%\3%\3&\3&\7&\u015f\n&\f&\16&\u0162\13&\3\'\6\'\u0165\n\'\r\'\16"+
		"\'\u0166\3\'\3\'\3(\3(\3(\3(\7(\u016f\n(\f(\16(\u0172\13(\3(\3(\3)\3)"+
		"\3)\3)\7)\u017a\n)\f)\16)\u017d\13)\3)\3)\3)\3)\3)\5\u0125\u0140\u017b"+
		"\2*\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35"+
		"\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36"+
		";\37= ?!A\"C#E$G\2I\2K%M&O\'Q(\3\2\t\5\2\13\f\16\17\"\"\5\2$$))==\6\2"+
		"\f\f\17\17$$^^\6\2\f\f\17\17))^^\5\2C\\aac|\6\2\62;C\\aac|\4\2\f\f\17"+
		"\17\2\u018f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2"+
		"\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2"+
		"\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\3S\3\2\2\2\5Y\3\2\2\2\7["+
		"\3\2\2\2\t]\3\2\2\2\13b\3\2\2\2\rh\3\2\2\2\17m\3\2\2\2\21r\3\2\2\2\23"+
		"{\3\2\2\2\25\u0083\3\2\2\2\27\u0088\3\2\2\2\31\u0098\3\2\2\2\33\u009a"+
		"\3\2\2\2\35\u00a2\3\2\2\2\37\u00ab\3\2\2\2!\u00b3\3\2\2\2#\u00b9\3\2\2"+
		"\2%\u00bb\3\2\2\2\'\u00be\3\2\2\2)\u00c8\3\2\2\2+\u00d2\3\2\2\2-\u00d9"+
		"\3\2\2\2/\u00dc\3\2\2\2\61\u00de\3\2\2\2\63\u00e3\3\2\2\2\65\u00e8\3\2"+
		"\2\2\67\u00ed\3\2\2\29\u00f3\3\2\2\2;\u00f7\3\2\2\2=\u00fc\3\2\2\2?\u0106"+
		"\3\2\2\2A\u010e\3\2\2\2C\u0138\3\2\2\2E\u013a\3\2\2\2G\u0146\3\2\2\2I"+
		"\u0151\3\2\2\2K\u015c\3\2\2\2M\u0164\3\2\2\2O\u016a\3\2\2\2Q\u0175\3\2"+
		"\2\2ST\7k\2\2TU\7u\2\2UV\7v\2\2VW\7c\2\2WX\7t\2\2X\4\3\2\2\2YZ\7}\2\2"+
		"Z\6\3\2\2\2[\\\7\177\2\2\\\b\3\2\2\2]^\7t\2\2^_\7q\2\2_`\7n\2\2`a\7g\2"+
		"\2a\n\3\2\2\2bc\7c\2\2cd\7i\2\2de\7g\2\2ef\7p\2\2fg\7v\2\2g\f\3\2\2\2"+
		"hi\7i\2\2ij\7q\2\2jk\7c\2\2kl\7n\2\2l\16\3\2\2\2mn\7v\2\2no\7c\2\2op\7"+
		"u\2\2pq\7m\2\2q\20\3\2\2\2rs\7t\2\2st\7g\2\2tu\7u\2\2uv\7q\2\2vw\7w\2"+
		"\2wx\7t\2\2xy\7e\2\2yz\7g\2\2z\22\3\2\2\2{|\7s\2\2|}\7w\2\2}~\7c\2\2~"+
		"\177\7n\2\2\177\u0080\7k\2\2\u0080\u0081\7v\2\2\u0081\u0082\7{\2\2\u0082"+
		"\24\3\2\2\2\u0083\u0084\7k\2\2\u0084\u0085\7u\2\2\u0085\u0086\7/\2\2\u0086"+
		"\u0087\7c\2\2\u0087\26\3\2\2\2\u0088\u0089\7r\2\2\u0089\u008a\7c\2\2\u008a"+
		"\u008b\7t\2\2\u008b\u008c\7v\2\2\u008c\u008d\7k\2\2\u008d\u008e\7e\2\2"+
		"\u008e\u008f\7k\2\2\u008f\u0090\7r\2\2\u0090\u0091\7c\2\2\u0091\u0092"+
		"\7v\2\2\u0092\u0093\7g\2\2\u0093\u0094\7u\2\2\u0094\u0095\7/\2\2\u0095"+
		"\u0096\7k\2\2\u0096\u0097\7p\2\2\u0097\30\3\2\2\2\u0098\u0099\7<\2\2\u0099"+
		"\32\3\2\2\2\u009a\u009b\7C\2\2\u009b\u009c\7e\2\2\u009c\u009d\7j\2\2\u009d"+
		"\u009e\7k\2\2\u009e\u009f\7g\2\2\u009f\u00a0\7x\2\2\u00a0\u00a1\7g\2\2"+
		"\u00a1\34\3\2\2\2\u00a2\u00a3\7O\2\2\u00a3\u00a4\7c\2\2\u00a4\u00a5\7"+
		"k\2\2\u00a5\u00a6\7p\2\2\u00a6\u00a7\7v\2\2\u00a7\u00a8\7c\2\2\u00a8\u00a9"+
		"\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\36\3\2\2\2\u00ab\u00ac\7U\2\2\u00ac\u00ad"+
		"\7w\2\2\u00ad\u00ae\7u\2\2\u00ae\u00af\7v\2\2\u00af\u00b0\7c\2\2\u00b0"+
		"\u00b1\7k\2\2\u00b1\u00b2\7p\2\2\u00b2 \3\2\2\2\u00b3\u00b4\7T\2\2\u00b4"+
		"\u00b5\7g\2\2\u00b5\u00b6\7e\2\2\u00b6\u00b7\7w\2\2\u00b7\u00b8\7t\2\2"+
		"\u00b8\"\3\2\2\2\u00b9\u00ba\7@\2\2\u00ba$\3\2\2\2\u00bb\u00bc\7q\2\2"+
		"\u00bc\u00bd\7t\2\2\u00bd&\3\2\2\2\u00be\u00bf\7s\2\2\u00bf\u00c0\7w\2"+
		"\2\u00c0\u00c1\7c\2\2\u00c1\u00c2\7n\2\2\u00c2\u00c3\7k\2\2\u00c3\u00c4"+
		"\7h\2\2\u00c4\u00c5\7k\2\2\u00c5\u00c6\7g\2\2\u00c6\u00c7\7u\2\2\u00c7"+
		"(\3\2\2\2\u00c8\u00c9\7p\2\2\u00c9\u00ca\7g\2\2\u00ca\u00cb\7g\2\2\u00cb"+
		"\u00cc\7f\2\2\u00cc\u00cd\7g\2\2\u00cd\u00ce\7f\2\2\u00ce\u00cf\7/\2\2"+
		"\u00cf\u00d0\7d\2\2\u00d0\u00d1\7{\2\2\u00d1*\3\2\2\2\u00d2\u00d3\7f\2"+
		"\2\u00d3\u00d4\7g\2\2\u00d4\u00d5\7r\2\2\u00d5\u00d6\7g\2\2\u00d6\u00d7"+
		"\7p\2\2\u00d7\u00d8\7f\2\2\u00d8,\3\2\2\2\u00d9\u00da\7/\2\2\u00da\u00db"+
		"\7@\2\2\u00db.\3\2\2\2\u00dc\u00dd\7\60\2\2\u00dd\60\3\2\2\2\u00de\u00df"+
		"\7o\2\2\u00df\u00e0\7c\2\2\u00e0\u00e1\7m\2\2\u00e1\u00e2\7g\2\2\u00e2"+
		"\62\3\2\2\2\u00e3\u00e4\7j\2\2\u00e4\u00e5\7g\2\2\u00e5\u00e6\7n\2\2\u00e6"+
		"\u00e7\7r\2\2\u00e7\64\3\2\2\2\u00e8\u00e9\7j\2\2\u00e9\u00ea\7w\2\2\u00ea"+
		"\u00eb\7t\2\2\u00eb\u00ec\7v\2\2\u00ec\66\3\2\2\2\u00ed\u00ee\7d\2\2\u00ee"+
		"\u00ef\7t\2\2\u00ef\u00f0\7g\2\2\u00f0\u00f1\7c\2\2\u00f1\u00f2\7m\2\2"+
		"\u00f28\3\2\2\2\u00f3\u00f4\7r\2\2\u00f4\u00f5\7t\2\2\u00f5\u00f6\7g\2"+
		"\2\u00f6:\3\2\2\2\u00f7\u00f8\7r\2\2\u00f8\u00f9\7q\2\2\u00f9\u00fa\7"+
		"u\2\2\u00fa\u00fb\7v\2\2\u00fb<\3\2\2\2\u00fc\u00fd\7e\2\2\u00fd\u00fe"+
		"\7q\2\2\u00fe\u00ff\7p\2\2\u00ff\u0100\7f\2\2\u0100\u0101\7k\2\2\u0101"+
		"\u0102\7v\2\2\u0102\u0103\7k\2\2\u0103\u0104\7q\2\2\u0104\u0105\7p\2\2"+
		"\u0105>\3\2\2\2\u0106\u0107\7u\2\2\u0107\u0108\7c\2\2\u0108\u0109\7v\2"+
		"\2\u0109\u010a\7k\2\2\u010a\u010b\7u\2\2\u010b\u010c\7h\2\2\u010c\u010d"+
		"\7{\2\2\u010d@\3\2\2\2\u010e\u010f\7g\2\2\u010f\u0110\7p\2\2\u0110\u0111"+
		"\7u\2\2\u0111\u0112\7w\2\2\u0112\u0113\7t\2\2\u0113\u0114\7g\2\2\u0114"+
		"B\3\2\2\2\u0115\u0116\7q\2\2\u0116\u0117\7e\2\2\u0117\u0118\7n\2\2\u0118"+
		"\u011c\3\2\2\2\u0119\u011b\t\2\2\2\u011a\u0119\3\2\2\2\u011b\u011e\3\2"+
		"\2\2\u011c\u011a\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011f\3\2\2\2\u011e"+
		"\u011c\3\2\2\2\u011f\u0120\7}\2\2\u0120\u0121\7]\2\2\u0121\u0125\3\2\2"+
		"\2\u0122\u0124\13\2\2\2\u0123\u0122\3\2\2\2\u0124\u0127\3\2\2\2\u0125"+
		"\u0126\3\2\2\2\u0125\u0123\3\2\2\2\u0126\u0128\3\2\2\2\u0127\u0125\3\2"+
		"\2\2\u0128\u0129\7_\2\2\u0129\u0139\7\177\2\2\u012a\u012b\7q\2\2\u012b"+
		"\u012c\7e\2\2\u012c\u012d\7n\2\2\u012d\u012e\7<\2\2\u012e\u0134\3\2\2"+
		"\2\u012f\u0133\5G$\2\u0130\u0133\5I%\2\u0131\u0133\n\3\2\2\u0132\u012f"+
		"\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0131\3\2\2\2\u0133\u0136\3\2\2\2\u0134"+
		"\u0132\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0137\3\2\2\2\u0136\u0134\3\2"+
		"\2\2\u0137\u0139\7=\2\2\u0138\u0115\3\2\2\2\u0138\u012a\3\2\2\2\u0139"+
		"D\3\2\2\2\u013a\u013b\7}\2\2\u013b\u013c\7]\2\2\u013c\u0140\3\2\2\2\u013d"+
		"\u013f\13\2\2\2\u013e\u013d\3\2\2\2\u013f\u0142\3\2\2\2\u0140\u0141\3"+
		"\2\2\2\u0140\u013e\3\2\2\2\u0141\u0143\3\2\2\2\u0142\u0140\3\2\2\2\u0143"+
		"\u0144\7_\2\2\u0144\u0145\7\177\2\2\u0145F\3\2\2\2\u0146\u014c\7$\2\2"+
		"\u0147\u0148\7^\2\2\u0148\u014b\13\2\2\2\u0149\u014b\n\4\2\2\u014a\u0147"+
		"\3\2\2\2\u014a\u0149\3\2\2\2\u014b\u014e\3\2\2\2\u014c\u014a\3\2\2\2\u014c"+
		"\u014d\3\2\2\2\u014d\u014f\3\2\2\2\u014e\u014c\3\2\2\2\u014f\u0150\7$"+
		"\2\2\u0150H\3\2\2\2\u0151\u0157\7)\2\2\u0152\u0153\7^\2\2\u0153\u0156"+
		"\13\2\2\2\u0154\u0156\n\5\2\2\u0155\u0152\3\2\2\2\u0155\u0154\3\2\2\2"+
		"\u0156\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u015a"+
		"\3\2\2\2\u0159\u0157\3\2\2\2\u015a\u015b\7)\2\2\u015bJ\3\2\2\2\u015c\u0160"+
		"\t\6\2\2\u015d\u015f\t\7\2\2\u015e\u015d\3\2\2\2\u015f\u0162\3\2\2\2\u0160"+
		"\u015e\3\2\2\2\u0160\u0161\3\2\2\2\u0161L\3\2\2\2\u0162\u0160\3\2\2\2"+
		"\u0163\u0165\t\2\2\2\u0164\u0163\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0164"+
		"\3\2\2\2\u0166\u0167\3\2\2\2\u0167\u0168\3\2\2\2\u0168\u0169\b\'\2\2\u0169"+
		"N\3\2\2\2\u016a\u016b\7\61\2\2\u016b\u016c\7\61\2\2\u016c\u0170\3\2\2"+
		"\2\u016d\u016f\n\b\2\2\u016e\u016d\3\2\2\2\u016f\u0172\3\2\2\2\u0170\u016e"+
		"\3\2\2\2\u0170\u0171\3\2\2\2\u0171\u0173\3\2\2\2\u0172\u0170\3\2\2\2\u0173"+
		"\u0174\b(\2\2\u0174P\3\2\2\2\u0175\u0176\7\61\2\2\u0176\u0177\7,\2\2\u0177"+
		"\u017b\3\2\2\2\u0178\u017a\13\2\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3"+
		"\2\2\2\u017b\u017c\3\2\2\2\u017b\u0179\3\2\2\2\u017c\u017e\3\2\2\2\u017d"+
		"\u017b\3\2\2\2\u017e\u017f\7,\2\2\u017f\u0180\7\61\2\2\u0180\u0181\3\2"+
		"\2\2\u0181\u0182\b)\2\2\u0182R\3\2\2\2\21\2\u011c\u0125\u0132\u0134\u0138"+
		"\u0140\u014a\u014c\u0155\u0157\u0160\u0166\u0170\u017b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}