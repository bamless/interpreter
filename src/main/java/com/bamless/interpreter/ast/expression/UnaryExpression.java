package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;

public abstract class UnaryExpression extends Expression {
    private Expression expression;

    public UnaryExpression(Expression e, Position pos) {
        super(pos);
        this.expression = e;
    }

    public Expression getExpression() {
        return expression;
    }

}
