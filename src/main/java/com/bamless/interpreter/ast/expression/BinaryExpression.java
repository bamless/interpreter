package com.bamless.interpreter.ast.expression;

public abstract class BinaryExpression extends Expression {
	private Expression left;
	private Expression right;

	public BinaryExpression(Expression left, Expression right) {
		super(left.getPosition());
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public void setRight(Expression right) {
		this.right = right;
	}

}
