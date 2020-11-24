package com.bamless.interpreter.ast.expression;

@SuppressWarnings("serial")
public abstract class PostFixExpression extends Expression {
    private Expression expression;

    public PostFixExpression(Expression e) {
        super(e.getPosition());
        this.expression = e;
    }

    public Expression getExpression() {
        return expression;
    }
}
