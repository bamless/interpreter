package com.bamless.interpreter.ast;

import java.io.Serializable;

import com.bamless.interpreter.visitor.Visitable;

public abstract class ASTNode implements Visitable, Serializable {
	private transient Position start;
	
	public ASTNode(Position start) {
		this.start = start;
	}
	
	public Position getPosition() {
		 return start;
	}
}
