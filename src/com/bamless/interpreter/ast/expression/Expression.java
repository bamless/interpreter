package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.statement.Statement;

public abstract class Expression extends Statement {

	public Expression(Position start) {
		super(start);
	}


}
