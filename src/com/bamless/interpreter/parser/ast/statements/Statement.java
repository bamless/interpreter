package com.bamless.interpreter.parser.ast.statements;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.ASTNode;

public abstract class Statement extends ASTNode {

	public Statement(Position start) {
		super(start);
	}

}
