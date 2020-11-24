package com.bamless.interpreter.ast.type;

@SuppressWarnings("serial")
public class VoidType extends Type {

    public VoidType() {
        super(TypeID.VOID, Type.NULL_WIDENFACT);
    }

    @Override
    public Type plus(Type other) {
        return null;
    }

    @Override
    public Type minus(Type other) {
        return null;
    }

    @Override
    public Type mul(Type other) {
        return null;
    }

    @Override
    public Type div(Type other) {
        return null;
    }

    @Override
    public Type modulus(Type other) {
        return null;
    }

    @Override
    public Type logicalOp(Type other) {
        return null;
    }

    @Override
    public Type relationalOp(Type other) {
        return null;
    }

    @Override
    public Type equalityOp(Type other) {
        return null;
    }

    @Override
    public boolean isCompatible(Type other) {
        return other == Type.VOID;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public String toString() {
        return "VOID";
    }

}
