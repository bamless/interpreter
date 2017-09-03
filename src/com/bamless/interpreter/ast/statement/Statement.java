package com.bamless.interpreter.ast.statement;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.ASTNode;

public abstract class Statement extends ASTNode {

	public Statement(Position start) {
		super(start);
	}

}
