package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;

public abstract class Lvalue extends Expression {

	public Lvalue(Position start) {
		super(start);
	}

}
