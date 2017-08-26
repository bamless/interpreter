package com.bamless.interpreter.lex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bamless.interpreter.Position;

public class Lexer {
	private final static Token END = new Token("_END_OF_INPUT_", "end of input");
	private final static Pattern SPACES = Pattern.compile("\\s+");
	
	private Pattern invalid;
	private LinkedHashMap<String, Pattern> typesRegx = new LinkedHashMap<>();
	private boolean skipSpaces;
	
	private int pos;
	private ArrayList<Token> tokens = new ArrayList<>();
	
	public Lexer(String[] types, boolean skipSpaces) {
		if(types.length % 2 != 0) {
			throw new IllegalArgumentException("Invalid types array. "
					+ "The array should be composed of pairs TOKEN_TYPE, regex.");
		}
		this.skipSpaces = skipSpaces;
		initRegs(types);
	}
	
	private void initRegs(String[] types) {
		for(int i = 0; i < types.length; i+=2) {
			typesRegx.put(types[i], Pattern.compile(types[i + 1]));
		}
		invalid = Pattern.compile(skipSpaces ? "[^\\s]+" : ".+");
	}
	
	public Lexer(File lexFile, boolean skipSpaces) throws FileNotFoundException {
		this(new FileInputStream(lexFile), skipSpaces);
	}
	
	public Lexer(InputStream lexFile, boolean skipSpaces) {
		this.skipSpaces = skipSpaces;
		parseLexFile(lexFile);
		invalid = Pattern.compile(skipSpaces ? "[^\\s]+" : ".+");
	}
	
	public void tokenize(File f) throws FileNotFoundException, IOException {
		pos = -1;
		try(BufferedReader r = new BufferedReader(new FileReader(f))){
			String line;
			int lineNo = 0;
			while((line = r.readLine()) != null) {
				lineNo++;
				tokenize(lineNo, line);
			}
		}
	}
	
	public void tokenize(String src) {
		pos = -1;
		String[] lines = src.split("\\r?\\n");
		for(int i = 0; i < lines.length; i++) {
			tokenize(i + 1, lines[i]);
		}
	}
	
	private void tokenize(int lineNo, String line) {
		LinkedHashMap<Matcher, String> matchersType = initMatchers(line);
		Matcher spaces = SPACES.matcher(line);
		
		int start = 0;
		while(start < line.length()) {
			//find and skip eventual spaces
			if(skipSpaces && spaces.find(start) && spaces.start() == start) {
				start += spaces.group().length();
				continue;
			}
			
			String type = null;
			String lexeme = null;
			int length = 0;
			
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

			tokens.add(new Token(type, lexeme, new Position(lineNo, start + 1)));
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
		return pos < tokens.size() - 1 ? tokens.get(pos + 1) : END;
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
	
	private void parseLexFile(InputStream lex) {
		if(lex == null) throw new IllegalArgumentException("The inputstream cannot be null");
		
		try(BufferedReader r = new BufferedReader(new InputStreamReader(lex))) {
			int lineNo = 0;
			String line = null; 
			while((line = removeComments(r.readLine())) != null) {
				lineNo++;
				if(line.matches("\\s*")) continue;
				
				StringBuilder type = new StringBuilder();
				int i, j;
				for(i = 0; i < line.length() && line.charAt(i) != '"'; i++) {
					if(!line.substring(i, i + 1).matches("\\s+"))
						type.append(line.charAt(i));
				}
				
				StringBuilder regex = new StringBuilder();
				for(j = i + 1; j < line.length() && line.charAt(j) != '"'; j++) {
					if(line.charAt(j) == '\\') j += 1;
					regex.append(line.charAt(j));
				}
				
				if(regex.toString().equals("") || type.toString().equals("") 
						|| !line.substring(j + 1, line.length()).matches("\\s*")) {
					throw new LexicalException("Error at line " + lineNo + " of lex file: malformed line.");
				}
				typesRegx.put(type.toString(), Pattern.compile(regex.toString()));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private String removeComments(String line) {
		if(line == null) return null;
		return line.replaceAll("//.*", "");
	}

}
