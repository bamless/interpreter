package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class PostIncrementOperation extends PostFixExpression {
	private IncrementOperator op;
	
	public PostIncrementOperation(IncrementOperator op, Expression e) {
		super(e);
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
		return getExpression() + opStr;
	}
	
}
