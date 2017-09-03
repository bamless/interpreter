package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;

public abstract class BinaryExpression extends Expression {
	private Expression left;
	private Expression right;

	public BinaryExpression(Expression left, Expression right, Position pos) {
		super(pos);
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}
	
}
