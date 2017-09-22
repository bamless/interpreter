package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class PreIncrementOperation extends UnaryExpression {
	private IncrementOperator op;
	
	public PreIncrementOperation(IncrementOperator op, Expression e, Position pos) {
		super(e, pos);
		this.op = op;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public IncrementOperator getOperator() {
		return op;
	}
	
	@Override
	public String toString() {
		String opStr = op == IncrementOperator.INCR ? "++" : "--";
		return opStr + getExpression();
	}
	
}