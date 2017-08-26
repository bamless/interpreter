package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class RelationalBinExpression extends BinaryExpression {
	public static enum RelationalBinOperation {
		LT, GT, LE, GE, EQ, NEQ;
	}
	private RelationalBinOperation operation;
	
	public RelationalBinExpression(RelationalBinOperation op, Expression left, Expression right, Position pos) {
		super(left, right, pos);
		this.operation = op;
	}

	public RelationalBinOperation getOperation() {
		return operation;
	}
	
	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

}
