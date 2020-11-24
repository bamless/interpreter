package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class VarLiteral extends Lvalue {
    private Identifier id;

    public VarLiteral(Identifier id) {
        super(id.getPosition());
        this.id = id;
    }

    @Override
    public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.getVal();
    }

}
