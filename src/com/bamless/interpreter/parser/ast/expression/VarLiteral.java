package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.visitor.GenericVisitor;
import com.bamless.interpreter.parser.ast.visitor.VoidVisitor;

public class VarLiteral extends Expression {
	private Identifier id;
	
	public VarLiteral(Identifier id, Position start) {
		super(start);
		this.id = id;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public Identifier getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return id.getId();
	}
	
}