package com.bamless.interpreter.parser.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitable;

public abstract class ASTNode implements Visitable {
	private Position start;
	
	public ASTNode(Position start) {
		this.start = start;
	}
	
	public Position getPosition() {
		 return start;
	}
}
