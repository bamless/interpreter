package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class ArithmeticBinExpression extends BinaryExpression {
	public static enum ArithmeticBinOperation {
		PLUS, MINUS, MULT, DIV;
	}
	private ArithmeticBinOperation operation;

	public ArithmeticBinExpression(ArithmeticBinOperation op, Expression left, Expression right, Position pos) {
		super(left, right, pos);
		this.operation = op;
	}

	public ArithmeticBinOperation getOperation() {
		return operation;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

}
