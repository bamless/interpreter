package com.bamless.interpreter.ast.expression;

import java.util.List;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class FunctionCallExpression extends Expression {
	private Identifier id;
	private List<Expression> args;

	public FunctionCallExpression(Position start) {
		super(start);
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

}
