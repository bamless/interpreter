package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

@SuppressWarnings("serial")
public class RelationalExpression extends BinaryExpression {
    public static enum RelationalOperation {
        LT("<"), GT(">"), LE("<="), GE(">=");

        private String repr;

        private RelationalOperation(String repr) {
            this.repr = repr;
        }

        @Override
        public String toString() {
            return repr;
        }
    }

    private RelationalOperation operation;

    public RelationalExpression(RelationalOperation op, Expression left, Expression right) {
        super(left, right);
        this.operation = op;
    }

    @Override
    public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public RelationalOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "(" + getLeft() + " " + operation + " " + getRight() + ")";
    }

}
