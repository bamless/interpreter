package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class LogicalNotExpression extends UnaryExpression {

	public LogicalNotExpression(Expression e, Position start) {
		super(e, start);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public String toString() {
		return "!(" + getExpression() + ")";
	}

}
