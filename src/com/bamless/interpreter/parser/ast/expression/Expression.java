package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.ASTNode;

public abstract class Expression extends ASTNode {

	public Expression(Position start) {
		super(start);
	}


}
