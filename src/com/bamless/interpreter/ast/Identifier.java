package com.bamless.interpreter.ast;

public class Identifier {
	private Position position;
	private String id;
	
	public Identifier(Position pos, String id) {
		this.position = pos;
		this.id = id;
	}

	public String getVal() {
		return id;
	}

	public Position getPosition() {
		return position;
	}
	
}
