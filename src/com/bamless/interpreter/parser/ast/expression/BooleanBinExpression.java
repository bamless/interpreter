package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class BooleanBinExpression extends BinaryExpression {
	public static enum BooleanBinOperation {
		OR, AND;
	}
	private BooleanBinOperation operation;
	
	public BooleanBinExpression(BooleanBinOperation op, Expression left, Expression right, Position pos) {
		super(left, right, pos);
		this.operation = op;
	}

	public BooleanBinOperation getOperation() {
		return operation;
	}
	
	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}
	
}
