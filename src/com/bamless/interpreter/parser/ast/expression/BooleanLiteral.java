package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class BooleanLiteral extends Expression {
	private boolean value;
	
	public BooleanLiteral(Position start, boolean value) {
		super(start);
		this.value = value;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public boolean getValue() {
		return value;
	}
	
}
