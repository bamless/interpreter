package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class AssignExpression extends Expression {
	private Lvalue lvalue;
	private Expression e;
	
	public AssignExpression(Position pos, Lvalue lvalue, Expression e) {
		super(pos);
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

	public Lvalue getLvalue() {
		return lvalue;
	}

	public Expression getExpression() {
		return e;
	}
	
	@Override
	public String toString() {
		return lvalue + " = " + e.toString();
	}

}
