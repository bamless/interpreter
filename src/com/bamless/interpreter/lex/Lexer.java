package com.bamless.interpreter.lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.bamless.interpreter.ast.Position;

/**
 * Generic Lexer that accepts a file with definable lexeme regexs.
 * It allows indefinite peeking in the token stream.
 * 
 * The lexer interns the token types and lexemes via {@link String#intern()} method
 * in order to save space and to permit type comparison via == or != (i.e. tok.getType() == "AND_OP"
 * instead of type.getType.equals("AND_OP")). This works because interned string are all mapped to the
 * same memory object.
 * 
 * @author fabrizio
 *
 */
public class Lexer {
	public final static Token END = new Token("_END_OF_INPUT_", "end of input", new Position(0, 0));
	private final static Pattern SPACES = Pattern.compile("\\s+");
	
	/**Regex of a comment*/
	private String commentRegx;
	private Pattern invalid;
	
	/**Hashmap that connects the user-defined token types to the regexs*/
	private LinkedHashMap<String, Pattern> typesRegx = new LinkedHashMap<>();
	/**Whether to save the source lexeme in the token*/
	private HashMap<String, Boolean> keepLexeme = new HashMap<>();
	/**Wheter to skip spaces or not*/
	private boolean skipSpaces;
	
	private int pos;
	private ArrayList<Token> tokens = new ArrayList<>();
	
	private Lexer(boolean skipSpaces, String commentsRegx) {
		this.skipSpaces = skipSpaces;
		this.commentRegx = commentsRegx;
		this.invalid = Pattern.compile(skipSpaces ? "[^\\s]+" : ".+");
	}
	
	/**
	 * Construct a new lexer by taking a string array of the form { TOKEN_TYPE, regex, keep_flag, TOKEN_TYPE, regex, keep_flag...} as input.
	 * the keep_flag string must be a 0 or a 1, indicating whether or not to save the source lexeme in the token.
	 * @param skipSpaces whether to skip spaces or not. If this is false you must provide a token that matches spaces (otherwise Lexical error).
	 * @param commentsRegx If provided, the lexer will skip the matching comments in the code
	 */
	public Lexer(String[] types, boolean skipSpaces, String commentsRegx) {
		this(skipSpaces, commentsRegx);
		if(types.length % 3 != 0) {
			throw new IllegalArgumentException("Invalid types array. "
					+ "The array should be composed of pairs TYPE_NAME, REGEX.");
		}
		
		//the token types get interned
		for(int i = 0; i < types.length; i += 3) {
			typesRegx.put(types[i].intern(), Pattern.compile(types[i + 1]));
			keepLexeme.put(types[i].intern(), types[i + 2].equals("0") ? false: true);
		}
	}
	
	/**
	 * @see Lexer#Lexer(String[], boolean, String)
	 */
	public Lexer(String[] types, boolean skipSpaces) {
		this(types, skipSpaces, null);
	}
	
	/**
	 * Construct a new lexer by taking lexical rules from a file of the form:
	 * TYPE_NAME "REGEX" keep_flag
	 * TYPE_NAME "REGEX" keep_flag
	 * ...
	 * 
	 * The keep flag should be a 0 or a 1, indicating whether or not to save the source lexeme
	 * in the token.
	 * 
	 * @param skipSpaces wheter to skip spaces or not. If this is false you must provide a token that matches spaces (otherwise Lexical error).
	 * @param commentsRegx If provided, the lexer will skip the matching comments in the code
	 */
	public Lexer(InputStream lexFile, boolean skipSpaces, String commentRegx) {
		this(skipSpaces, commentRegx);
		parseLexFile(lexFile);
	}
	
	/**
	 * @see Lexer#Lexer(InputStream, boolean, String)
	 */
	public Lexer(InputStream lexFile, boolean skipSpaces) {
		this(lexFile, skipSpaces, null);
	}
	
	/**
	 * @see Lexer#Lexer(InputStream, boolean, String)
	 */
	public Lexer(File lexFile, boolean skipSpaces, String commentRegx) throws FileNotFoundException {
		this(new FileInputStream(lexFile), skipSpaces, commentRegx);
	}	
	
	/**
	 * @see Lexer#Lexer(InputStream, boolean, String)
	 */
	public Lexer(File lexFile, boolean skipSpaces) throws FileNotFoundException {
		this(lexFile, skipSpaces, null);
	}	
	
	public void tokenize(File f) throws FileNotFoundException, IOException {
		tokenize(new FileInputStream(f));
	}
	
	public void tokenize(InputStream is) throws IOException {
		if(is == null) throw new IllegalArgumentException("null input stream");
		
		clear();
		
		try(BufferedReader r = new BufferedReader(new InputStreamReader(is))){
			String line;
			int lineNo = 0;
			while((line = r.readLine()) != null) {
				tokenize(++lineNo, line);
			}
		}
	}
	
	public void tokenize(String src) {
		clear();
		
		String[] lines = src.split("\\r?\\n");
		for(int i = 0; i < lines.length; i++) {
			tokenize(i + 1, lines[i]);
		}
	}
	
	private void tokenize(int lineNo, String line) {
		LinkedHashMap<Matcher, String> matchersType = initMatchers(line);
		Matcher spaces = SPACES.matcher(line);
		
		//remove comments
		if(commentRegx != null) {
			line = line.replaceAll(commentRegx, "");
		}
			
		int start = 0;
		while(start < line.length()) {
			//find and skip eventual spaces
			if(skipSpaces && spaces.find(start) && spaces.start() == start) {
				start += spaces.group().length();
				continue;
			}
			
			int length = 0;
			String type = null, lexeme = null;
			
			for(Matcher m : matchersType.keySet()) {
				if(m.find(start)) {
					if(m.start() != start) continue;
					
					//maximal length match
					if(m.end() - m.start() > length) {
						type = matchersType.get(m);
						lexeme = m.group();
						
						length = lexeme.length();
					}
				}
			}
			
			if(type == null) {
				Matcher invTok = invalid.matcher(line);
				invTok.find(start);
				
				throw new LexicalException(String.format("Lexical error (%d, %d): invalid token \"%s\"",
						lineNo, invTok.start(), invTok.group()));
			}

			//add interned lexeme if needed
			tokens.add(new Token(type, keepLexeme.get(type) ? lexeme.intern() : null, new Position(lineNo, start + 1)));
 			start += length;
		}
	}

	private LinkedHashMap<Matcher, String> initMatchers(String src) {
		String[] tokenTypes = typesRegx.keySet().toArray(new String[typesRegx.keySet().size()]);
		LinkedHashMap<Matcher, String> matchersType = new LinkedHashMap<>();
		for(int i = 0; i < tokenTypes.length; i++) {
			matchersType.put(typesRegx.get(tokenTypes[i]).matcher(src), tokenTypes[i]);
		}
		return matchersType;
	}
	
	public Token curr() {
		return pos >= 0 ? tokens.get(pos) : null;
	}
	
	public boolean hasNext() {
		return pos < tokens.size() - 1;
	}
	
	public Token next() {
		return pos < tokens.size() - 1 ? tokens.get(++pos) : END;
	}
	
	public void consume() {
		if(pos < tokens.size() - 1) pos++;
	}
	
	public Token peek() {
		return peek(1);
	}
	
	public Token peek(int i) {
		if(i < 1) throw new IllegalArgumentException("Can only peek forward in the token stream!");
		return pos + i < tokens.size() ? tokens.get(pos + i) : END;
	}
	
	public boolean isFinished() {
		return pos == tokens.size() - 1;
	}
	
	public String patternsToString() {
		StringBuilder sb = new StringBuilder();
		for(String key : typesRegx.keySet())
			sb.append(key + " " + typesRegx.get(key) + "\n");
		return sb.toString();
	}
	
	public void clear() {
		pos = -1;
		tokens.clear();
	}
	
	private void parseLexFile(InputStream lex) {
		if(lex == null) throw new IllegalArgumentException("The inputstream cannot be null");
		
		Lexer tok = new Lexer(new String[] {
				"KEEP_LEXEME",  "[0|1]",              "1",
				"REGEX",        "\"(\\\\.|[^\"])*\"", "1",
				"TYPE_NAME",    "[^\\s\"]+",          "1",
		}, true, "//.*");
		
		try {
			tok.tokenize(lex);
		} catch(IOException e) {
			throw new RuntimeException("Error while reading lex file", e);
		}
		
		while(tok.hasNext()) {
			Token type = tok.next();
			Token regx = tok.next();
			Token keep = tok.next();
			
			if(type.getType() != "TYPE_NAME")
				throw new IllegalArgumentException("Error at " + type.getPosition() + ": " + type.getValue() + " is not a valid token type name.");
			
			if(regx.getType() != "REGEX") {
				String errStr = regx == Lexer.END ? "Error: expected regex but instead found end of file." :
					"Error at " + regx.getPosition() + ": " + regx.getValue() + " is not a valid regex.";
				throw new IllegalArgumentException(errStr);
			}
			
			if(keep.getType() != "KEEP_LEXEME") {
				String errStr = regx == Lexer.END ? "Error: expected flag but instead found end of file." :
					"Error at " + regx.getPosition() + ": " + regx.getValue() + " is not a valid flag.";
				throw new IllegalArgumentException(errStr);
			}
			
			Pattern pattern;
			try {
				pattern = Pattern.compile(unescapeRegex(regx.getValue()));
			} catch(PatternSyntaxException e) {
				throw new IllegalArgumentException("Error at " + regx.getPosition() + ": " + regx.getValue() + " is not a valid regex.", e);
			}
			
			typesRegx.put(type.getValue(), pattern);
			keepLexeme.put(type.getValue(), keep.getValue().equals("0") ? false : true);
		}
	}
	
	private String unescapeRegex(String regex) {
		String unescapedRegx = "";
		for(int c = 1; c < regex.length() - 1; c++) {
			if(regex.charAt(c) == '\\') c++;
			unescapedRegx += regex.charAt(c);
		}
		return unescapedRegx;
	}

}
