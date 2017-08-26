package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class IntegerLiteral extends Expression {
	private int value;
	
	public IntegerLiteral(Position start, int value) {
		super(start);
		this.value = value;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public int getValue() {
		return value;
	}
}
