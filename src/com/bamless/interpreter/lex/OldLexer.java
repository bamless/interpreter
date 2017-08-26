package com.bamless.interpreter.lex;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldLexer {
	private final static Token END = new Token("__END_OF_INPUT__", "end of input");
	
	private LinkedHashMap<String, String> typesRegx;
	private Pattern tokensRegx;
	private boolean skipSpaces;
	
	private int pos;
	private ArrayList<Token> tokens;
	
	public OldLexer(String[] types, boolean skipSpaces) {
		if(types.length % 2 != 0) {
			throw new IllegalArgumentException("Invalid types array. "
					+ "The array should be composed of pairs TOKEN_TYPE, regex.");
		}
		this.skipSpaces = skipSpaces;
		this.typesRegx = new LinkedHashMap<>();
		this.tokens = new ArrayList<>();
		initRegs(types);
	}

	private void initRegs(String[] types) {
		StringBuffer regex = new StringBuffer();
		for(int i = 0; i < types.length; i+=2) {
			typesRegx.put(types[i], types[i+1]);
			regex.append(types[i+1] + "|");
		}
		
		if(typesRegx.containsKey(END.getType())) {
			throw new IllegalArgumentException("Type " + 
					END.getType() + " is reserved and cannot be used by the user");
		}
		
		regex.append(skipSpaces ? "([^\\s]+)" : "(.+)") ;
		tokensRegx = Pattern.compile(regex.toString());
	}
	
	public void tokenize(String src) {
		pos = -1;
		Matcher m = tokensRegx.matcher(src);
		
		while(m.find()) {			
			String lexeme = m.group();
			if(skipSpaces && lexeme.matches("\\s+")) {
				continue;
			}
			
			String type = null;
			for(String t : typesRegx.keySet()) {
				if(lexeme.matches(typesRegx.get(t))) {
					type = t;
					break;
				}
			}
			
			if(type == null) {
				throw new LexicalException(String.format(
						"Lexical error: %d: invalid token \"%s\"", m.start(), lexeme));
			}
				
			tokens.add(new Token(type, lexeme));
		}
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
		return pos < tokens.size() - 1 ? tokens.get(pos + 1) : END;
	}
	
	public boolean isFinished() {
		return pos == tokens.size() - 1;
	}
}
