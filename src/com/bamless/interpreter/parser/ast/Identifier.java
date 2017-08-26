package com.bamless.interpreter.parser.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class Identifier extends ASTNode {
	private String id;
	
	public Identifier(Position pos, String id) {
		super(pos);
		this.id = id;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public String getId() {
		return id;
	}
	
}
