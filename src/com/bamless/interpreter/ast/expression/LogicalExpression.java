package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class LogicalExpression extends BinaryExpression {
	public static enum BooleanBinOperation {
		OR, AND;
	}
	private BooleanBinOperation operation;
	
	public LogicalExpression(BooleanBinOperation op, Expression left, Expression right, Position pos) {
		super(left, right, pos);
		this.operation = op;
	}

	public BooleanBinOperation getOperation() {
		return operation;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}
	
	@Override
	public String toString() {
		return "(" + getLeft() +" "+ operation +" "+ getRight() + ")";
	}
	
}
