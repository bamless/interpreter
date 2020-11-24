package com.bamless.interpreter.ast.statement;

import java.util.Collection;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class ArrayDecl extends Statement {
    private Identifier id;
    private Expression[] dimensions;
    private Type type;

    public ArrayDecl(Position start, Type type, Collection<Expression> dimensions, Identifier id) {
        super(start);
        this.dimensions = dimensions.toArray(new Expression[dimensions.size()]);
        this.type = type;
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

    public Type getType() {
        return type;
    }

    public Expression[] getDimensions() {
        return dimensions;
    }

    public Identifier getId() {
        return id;
    }

}
