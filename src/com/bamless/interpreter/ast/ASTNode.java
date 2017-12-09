package com.bamless.interpreter.ast;

import com.bamless.interpreter.visitor.Visitable;

public abstract class ASTNode implements Visitable {
	private Position start;
	
	public ASTNode(Position start) {
		this.start = start;
	}
	
	public Position getPosition() {
		 return start;
	}
}
