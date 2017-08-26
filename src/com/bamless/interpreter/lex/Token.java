package com.bamless.interpreter.lex;

import com.bamless.interpreter.Position;

public class Token {
	
	private String type;
	private String lexeme;
	
	/**Starting position in the source file*/
	private Position pos;

	public Token(String type, String lexeme) {
		this(type, lexeme, new Position(0, 0));
	}
	
	public Token(String type, String lexeme, Position pos) {
		this.type = type;
		this.lexeme = lexeme;
		this.pos = pos;
	}

	public String getValue() {
		return lexeme;
	}

	public String getType() {
		return type;
	}
	
	public Position getPosition() {
		return pos;
	}
	
	@Override
	public String toString() {
		return pos + " " + type + ": " + lexeme + " ";
	}
}
