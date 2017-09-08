package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.Identifier;

public abstract class Lvalue extends Expression {

	public Lvalue(Position start) {
		super(start);
	}
	
	public abstract Identifier getId();

}
