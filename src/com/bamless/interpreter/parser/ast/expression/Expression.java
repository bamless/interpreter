package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.statements.Statement;

public abstract class Expression extends Statement {

	public Expression(Position start) {
		super(start);
	}


}
