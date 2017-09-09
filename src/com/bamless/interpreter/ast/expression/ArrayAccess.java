package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class ArrayAccess extends Lvalue {
	private Lvalue lvalue;
	private Expression index;

	public ArrayAccess(Position start, Lvalue lvalue, Expression index) {
		super(start);
		this.lvalue = lvalue;
		this.index = index;
	}


	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	public Lvalue getLvalue() {
		return lvalue;
	}
	
	public Expression getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		boolean par = lvalue instanceof AssignExpression;
		return (par ? "(" : "") + lvalue + (par ? ")" : "") + "[" + index + "]";
	}
	
}
