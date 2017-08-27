package com.bamless.interpreter.parser.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.GenericVisitor;
import com.bamless.interpreter.parser.ast.visitor.VoidVisitor;

public class Identifier extends ASTNode {
	private String id;
	
	public Identifier(Position pos, String id) {
		super(pos);
		this.id = id;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public String getId() {
		return id;
	}
	
}
