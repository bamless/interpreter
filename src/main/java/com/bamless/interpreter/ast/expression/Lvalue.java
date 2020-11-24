package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;

@SuppressWarnings("serial")
public abstract class Lvalue extends Expression {

    public Lvalue(Position start) {
        super(start);
    }

}
