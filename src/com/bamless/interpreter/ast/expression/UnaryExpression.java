package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;

public abstract class UnaryExpression extends Expression {
	private Expression expression;
	
	public UnaryExpression(Expression e, Position start) {
		super(start);
		this.expression = e;
	}

	public Expression getExpression() {
		return expression;
	}
	
}
