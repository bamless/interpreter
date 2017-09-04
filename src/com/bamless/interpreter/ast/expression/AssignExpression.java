package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class AssignExpression extends Expression {
	private Identifier id;
	private Expression e;
	
	public AssignExpression(Position pos, Identifier id, Expression e) {
		super(pos);
		this.id = id;
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

	public Identifier getId() {
		return id;
	}

	public Expression getExpression() {
		return e;
	}
	
	@Override
	public String toString() {
		return id.getVal() + " = " + e.toString();
	}

}
