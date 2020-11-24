package com.bamless.interpreter.ast.type;

public class ArrayType extends Type {
	private Type internal;
	
	protected ArrayType(Type internal) {
		super(TypeID.ARRAY, Type.NULL_WIDENFACT);
		this.internal = internal;
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
		if(this == other)
			return Type.BOOLEAN;
		return null;
	}

	@Override
	public boolean isCompatible(Type other) {
		//array with null internal type represents a generic array
		if(other.isArray() && ((ArrayType) other).getInternalType() == null)
			return true;
		return this == other;
	}
	
	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public String toString() {
		return internal + "[]";
	}
	
	public Type getInternalType() {
		return internal;
	}

}
