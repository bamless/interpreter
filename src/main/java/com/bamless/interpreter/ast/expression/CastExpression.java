package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class CastExpression extends UnaryExpression {
	
	public CastExpression(Type castType, Expression e, Position pos) {
		super(e, pos);
		setType(castType);
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
		return "(" + getType() + ") " + "(" + getExpression() + ")";
	}
	
}
