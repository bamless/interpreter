package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class AssignExpression extends Expression {
	private Expression lvalue;
	private Expression e;

	public AssignExpression(Expression lvalue, Expression e) {
		super(lvalue.getPosition());
		this.lvalue = lvalue;
		this.e = e;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Expression getLvalue() {
		return lvalue;
	}

	public Expression getExpression() {
		return e;
	}

	public void setExpression(Expression e) {
		this.e = e;
	}

	@Override
	public String toString() {
		return lvalue + " = " + e.toString();
	}

}
