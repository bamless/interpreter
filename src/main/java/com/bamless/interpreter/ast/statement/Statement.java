package com.bamless.interpreter.ast.statement;

import com.bamless.interpreter.ast.ASTNode;
import com.bamless.interpreter.ast.Position;

@SuppressWarnings("serial")
public abstract class Statement extends ASTNode {

    public Statement(Position start) {
        super(start);
    }

}
