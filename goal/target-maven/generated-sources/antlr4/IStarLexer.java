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
		OCL_CLAUSE=32, OCL_BLOCK=33, IDENT=34, WS=35, LINE_COMMENT=36, BLOCK_COMMENT=37;
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
			"T__25", "T__26", "T__27", "T__28", "T__29", "T__30", "OCL_CLAUSE", "OCL_BLOCK", 
			"OCL_DQ_STRING", "OCL_SQ_STRING", "IDENT", "WS", "LINE_COMMENT", "BLOCK_COMMENT"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\'\u017b\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31"+
		"\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34"+
		"\3\34\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3"+
		" \3!\3!\3!\3!\3!\7!\u0113\n!\f!\16!\u0116\13!\3!\3!\3!\3!\7!\u011c\n!"+
		"\f!\16!\u011f\13!\3!\3!\3!\3!\3!\3!\3!\3!\3!\3!\7!\u012b\n!\f!\16!\u012e"+
		"\13!\3!\5!\u0131\n!\3\"\3\"\3\"\3\"\7\"\u0137\n\"\f\"\16\"\u013a\13\""+
		"\3\"\3\"\3\"\3#\3#\3#\3#\7#\u0143\n#\f#\16#\u0146\13#\3#\3#\3$\3$\3$\3"+
		"$\7$\u014e\n$\f$\16$\u0151\13$\3$\3$\3%\3%\7%\u0157\n%\f%\16%\u015a\13"+
		"%\3&\6&\u015d\n&\r&\16&\u015e\3&\3&\3\'\3\'\3\'\3\'\7\'\u0167\n\'\f\'"+
		"\16\'\u016a\13\'\3\'\3\'\3(\3(\3(\3(\7(\u0172\n(\f(\16(\u0175\13(\3(\3"+
		"(\3(\3(\3(\5\u011d\u0138\u0173\2)\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E\2G\2I$K%M&O\'\3\2\t\5\2\13"+
		"\f\16\17\"\"\5\2$$))==\6\2\f\f\17\17$$^^\6\2\f\f\17\17))^^\5\2C\\aac|"+
		"\6\2\62;C\\aac|\4\2\f\f\17\17\2\u0187\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3"+
		"\2\2\2\2C\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\3Q\3\2\2"+
		"\2\5W\3\2\2\2\7Y\3\2\2\2\t[\3\2\2\2\13`\3\2\2\2\rf\3\2\2\2\17k\3\2\2\2"+
		"\21p\3\2\2\2\23y\3\2\2\2\25\u0081\3\2\2\2\27\u0086\3\2\2\2\31\u0096\3"+
		"\2\2\2\33\u0098\3\2\2\2\35\u00a0\3\2\2\2\37\u00a9\3\2\2\2!\u00b1\3\2\2"+
		"\2#\u00b3\3\2\2\2%\u00b6\3\2\2\2\'\u00c0\3\2\2\2)\u00ca\3\2\2\2+\u00d1"+
		"\3\2\2\2-\u00d4\3\2\2\2/\u00d6\3\2\2\2\61\u00db\3\2\2\2\63\u00e0\3\2\2"+
		"\2\65\u00e5\3\2\2\2\67\u00eb\3\2\2\29\u00ef\3\2\2\2;\u00f4\3\2\2\2=\u00fe"+
		"\3\2\2\2?\u0106\3\2\2\2A\u0130\3\2\2\2C\u0132\3\2\2\2E\u013e\3\2\2\2G"+
		"\u0149\3\2\2\2I\u0154\3\2\2\2K\u015c\3\2\2\2M\u0162\3\2\2\2O\u016d\3\2"+
		"\2\2QR\7k\2\2RS\7u\2\2ST\7v\2\2TU\7c\2\2UV\7t\2\2V\4\3\2\2\2WX\7}\2\2"+
		"X\6\3\2\2\2YZ\7\177\2\2Z\b\3\2\2\2[\\\7t\2\2\\]\7q\2\2]^\7n\2\2^_\7g\2"+
		"\2_\n\3\2\2\2`a\7c\2\2ab\7i\2\2bc\7g\2\2cd\7p\2\2de\7v\2\2e\f\3\2\2\2"+
		"fg\7i\2\2gh\7q\2\2hi\7c\2\2ij\7n\2\2j\16\3\2\2\2kl\7v\2\2lm\7c\2\2mn\7"+
		"u\2\2no\7m\2\2o\20\3\2\2\2pq\7t\2\2qr\7g\2\2rs\7u\2\2st\7q\2\2tu\7w\2"+
		"\2uv\7t\2\2vw\7e\2\2wx\7g\2\2x\22\3\2\2\2yz\7s\2\2z{\7w\2\2{|\7c\2\2|"+
		"}\7n\2\2}~\7k\2\2~\177\7v\2\2\177\u0080\7{\2\2\u0080\24\3\2\2\2\u0081"+
		"\u0082\7k\2\2\u0082\u0083\7u\2\2\u0083\u0084\7/\2\2\u0084\u0085\7c\2\2"+
		"\u0085\26\3\2\2\2\u0086\u0087\7r\2\2\u0087\u0088\7c\2\2\u0088\u0089\7"+
		"t\2\2\u0089\u008a\7v\2\2\u008a\u008b\7k\2\2\u008b\u008c\7e\2\2\u008c\u008d"+
		"\7k\2\2\u008d\u008e\7r\2\2\u008e\u008f\7c\2\2\u008f\u0090\7v\2\2\u0090"+
		"\u0091\7g\2\2\u0091\u0092\7u\2\2\u0092\u0093\7/\2\2\u0093\u0094\7k\2\2"+
		"\u0094\u0095\7p\2\2\u0095\30\3\2\2\2\u0096\u0097\7<\2\2\u0097\32\3\2\2"+
		"\2\u0098\u0099\7C\2\2\u0099\u009a\7e\2\2\u009a\u009b\7j\2\2\u009b\u009c"+
		"\7k\2\2\u009c\u009d\7g\2\2\u009d\u009e\7x\2\2\u009e\u009f\7g\2\2\u009f"+
		"\34\3\2\2\2\u00a0\u00a1\7O\2\2\u00a1\u00a2\7c\2\2\u00a2\u00a3\7k\2\2\u00a3"+
		"\u00a4\7p\2\2\u00a4\u00a5\7v\2\2\u00a5\u00a6\7c\2\2\u00a6\u00a7\7k\2\2"+
		"\u00a7\u00a8\7p\2\2\u00a8\36\3\2\2\2\u00a9\u00aa\7U\2\2\u00aa\u00ab\7"+
		"w\2\2\u00ab\u00ac\7u\2\2\u00ac\u00ad\7v\2\2\u00ad\u00ae\7c\2\2\u00ae\u00af"+
		"\7k\2\2\u00af\u00b0\7p\2\2\u00b0 \3\2\2\2\u00b1\u00b2\7@\2\2\u00b2\"\3"+
		"\2\2\2\u00b3\u00b4\7q\2\2\u00b4\u00b5\7t\2\2\u00b5$\3\2\2\2\u00b6\u00b7"+
		"\7s\2\2\u00b7\u00b8\7w\2\2\u00b8\u00b9\7c\2\2\u00b9\u00ba\7n\2\2\u00ba"+
		"\u00bb\7k\2\2\u00bb\u00bc\7h\2\2\u00bc\u00bd\7k\2\2\u00bd\u00be\7g\2\2"+
		"\u00be\u00bf\7u\2\2\u00bf&\3\2\2\2\u00c0\u00c1\7p\2\2\u00c1\u00c2\7g\2"+
		"\2\u00c2\u00c3\7g\2\2\u00c3\u00c4\7f\2\2\u00c4\u00c5\7g\2\2\u00c5\u00c6"+
		"\7f\2\2\u00c6\u00c7\7/\2\2\u00c7\u00c8\7d\2\2\u00c8\u00c9\7{\2\2\u00c9"+
		"(\3\2\2\2\u00ca\u00cb\7f\2\2\u00cb\u00cc\7g\2\2\u00cc\u00cd\7r\2\2\u00cd"+
		"\u00ce\7g\2\2\u00ce\u00cf\7p\2\2\u00cf\u00d0\7f\2\2\u00d0*\3\2\2\2\u00d1"+
		"\u00d2\7/\2\2\u00d2\u00d3\7@\2\2\u00d3,\3\2\2\2\u00d4\u00d5\7\60\2\2\u00d5"+
		".\3\2\2\2\u00d6\u00d7\7o\2\2\u00d7\u00d8\7c\2\2\u00d8\u00d9\7m\2\2\u00d9"+
		"\u00da\7g\2\2\u00da\60\3\2\2\2\u00db\u00dc\7j\2\2\u00dc\u00dd\7g\2\2\u00dd"+
		"\u00de\7n\2\2\u00de\u00df\7r\2\2\u00df\62\3\2\2\2\u00e0\u00e1\7j\2\2\u00e1"+
		"\u00e2\7w\2\2\u00e2\u00e3\7t\2\2\u00e3\u00e4\7v\2\2\u00e4\64\3\2\2\2\u00e5"+
		"\u00e6\7d\2\2\u00e6\u00e7\7t\2\2\u00e7\u00e8\7g\2\2\u00e8\u00e9\7c\2\2"+
		"\u00e9\u00ea\7m\2\2\u00ea\66\3\2\2\2\u00eb\u00ec\7r\2\2\u00ec\u00ed\7"+
		"t\2\2\u00ed\u00ee\7g\2\2\u00ee8\3\2\2\2\u00ef\u00f0\7r\2\2\u00f0\u00f1"+
		"\7q\2\2\u00f1\u00f2\7u\2\2\u00f2\u00f3\7v\2\2\u00f3:\3\2\2\2\u00f4\u00f5"+
		"\7e\2\2\u00f5\u00f6\7q\2\2\u00f6\u00f7\7p\2\2\u00f7\u00f8\7f\2\2\u00f8"+
		"\u00f9\7k\2\2\u00f9\u00fa\7v\2\2\u00fa\u00fb\7k\2\2\u00fb\u00fc\7q\2\2"+
		"\u00fc\u00fd\7p\2\2\u00fd<\3\2\2\2\u00fe\u00ff\7u\2\2\u00ff\u0100\7c\2"+
		"\2\u0100\u0101\7v\2\2\u0101\u0102\7k\2\2\u0102\u0103\7u\2\2\u0103\u0104"+
		"\7h\2\2\u0104\u0105\7{\2\2\u0105>\3\2\2\2\u0106\u0107\7g\2\2\u0107\u0108"+
		"\7p\2\2\u0108\u0109\7u\2\2\u0109\u010a\7w\2\2\u010a\u010b\7t\2\2\u010b"+
		"\u010c\7g\2\2\u010c@\3\2\2\2\u010d\u010e\7q\2\2\u010e\u010f\7e\2\2\u010f"+
		"\u0110\7n\2\2\u0110\u0114\3\2\2\2\u0111\u0113\t\2\2\2\u0112\u0111\3\2"+
		"\2\2\u0113\u0116\3\2\2\2\u0114\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115"+
		"\u0117\3\2\2\2\u0116\u0114\3\2\2\2\u0117\u0118\7}\2\2\u0118\u0119\7]\2"+
		"\2\u0119\u011d\3\2\2\2\u011a\u011c\13\2\2\2\u011b\u011a\3\2\2\2\u011c"+
		"\u011f\3\2\2\2\u011d\u011e\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u0120\3\2"+
		"\2\2\u011f\u011d\3\2\2\2\u0120\u0121\7_\2\2\u0121\u0131\7\177\2\2\u0122"+
		"\u0123\7q\2\2\u0123\u0124\7e\2\2\u0124\u0125\7n\2\2\u0125\u0126\7<\2\2"+
		"\u0126\u012c\3\2\2\2\u0127\u012b\5E#\2\u0128\u012b\5G$\2\u0129\u012b\n"+
		"\3\2\2\u012a\u0127\3\2\2\2\u012a\u0128\3\2\2\2\u012a\u0129\3\2\2\2\u012b"+
		"\u012e\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012f\3\2"+
		"\2\2\u012e\u012c\3\2\2\2\u012f\u0131\7=\2\2\u0130\u010d\3\2\2\2\u0130"+
		"\u0122\3\2\2\2\u0131B\3\2\2\2\u0132\u0133\7}\2\2\u0133\u0134\7]\2\2\u0134"+
		"\u0138\3\2\2\2\u0135\u0137\13\2\2\2\u0136\u0135\3\2\2\2\u0137\u013a\3"+
		"\2\2\2\u0138\u0139\3\2\2\2\u0138\u0136\3\2\2\2\u0139\u013b\3\2\2\2\u013a"+
		"\u0138\3\2\2\2\u013b\u013c\7_\2\2\u013c\u013d\7\177\2\2\u013dD\3\2\2\2"+
		"\u013e\u0144\7$\2\2\u013f\u0140\7^\2\2\u0140\u0143\13\2\2\2\u0141\u0143"+
		"\n\4\2\2\u0142\u013f\3\2\2\2\u0142\u0141\3\2\2\2\u0143\u0146\3\2\2\2\u0144"+
		"\u0142\3\2\2\2\u0144\u0145\3\2\2\2\u0145\u0147\3\2\2\2\u0146\u0144\3\2"+
		"\2\2\u0147\u0148\7$\2\2\u0148F\3\2\2\2\u0149\u014f\7)\2\2\u014a\u014b"+
		"\7^\2\2\u014b\u014e\13\2\2\2\u014c\u014e\n\5\2\2\u014d\u014a\3\2\2\2\u014d"+
		"\u014c\3\2\2\2\u014e\u0151\3\2\2\2\u014f\u014d\3\2\2\2\u014f\u0150\3\2"+
		"\2\2\u0150\u0152\3\2\2\2\u0151\u014f\3\2\2\2\u0152\u0153\7)\2\2\u0153"+
		"H\3\2\2\2\u0154\u0158\t\6\2\2\u0155\u0157\t\7\2\2\u0156\u0155\3\2\2\2"+
		"\u0157\u015a\3\2\2\2\u0158\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159J\3"+
		"\2\2\2\u015a\u0158\3\2\2\2\u015b\u015d\t\2\2\2\u015c\u015b\3\2\2\2\u015d"+
		"\u015e\3\2\2\2\u015e\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u0160\3\2"+
		"\2\2\u0160\u0161\b&\2\2\u0161L\3\2\2\2\u0162\u0163\7\61\2\2\u0163\u0164"+
		"\7\61\2\2\u0164\u0168\3\2\2\2\u0165\u0167\n\b\2\2\u0166\u0165\3\2\2\2"+
		"\u0167\u016a\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016b"+
		"\3\2\2\2\u016a\u0168\3\2\2\2\u016b\u016c\b\'\2\2\u016cN\3\2\2\2\u016d"+
		"\u016e\7\61\2\2\u016e\u016f\7,\2\2\u016f\u0173\3\2\2\2\u0170\u0172\13"+
		"\2\2\2\u0171\u0170\3\2\2\2\u0172\u0175\3\2\2\2\u0173\u0174\3\2\2\2\u0173"+
		"\u0171\3\2\2\2\u0174\u0176\3\2\2\2\u0175\u0173\3\2\2\2\u0176\u0177\7,"+
		"\2\2\u0177\u0178\7\61\2\2\u0178\u0179\3\2\2\2\u0179\u017a\b(\2\2\u017a"+
		"P\3\2\2\2\21\2\u0114\u011d\u012a\u012c\u0130\u0138\u0142\u0144\u014d\u014f"+
		"\u0158\u015e\u0168\u0173\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}