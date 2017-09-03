package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class BooleanLiteral extends Expression {
	private boolean value;
	
	public BooleanLiteral(Position start, boolean value) {
		super(start);
		this.value = value;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public boolean getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value + "";
	}
}
