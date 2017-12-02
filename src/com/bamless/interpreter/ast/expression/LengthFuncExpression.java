package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class LengthFuncExpression extends Expression {
	private Expression arg;

	public LengthFuncExpression(Position start, Expression arg) {
		super(start);
		this.arg = arg;
		setType(Type.INT);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	public Expression getArg() {
		return arg;
	}

	@Override
	public String toString() {
		return "len(" + arg + ")";
	}
	
}
