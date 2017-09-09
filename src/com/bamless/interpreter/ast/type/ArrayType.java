package com.bamless.interpreter.ast.type;

public class ArrayType extends Type {
	private Type elemType;
	
	protected ArrayType(Type elemType) {
		this.elemType = elemType;
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
	public boolean canAssign(Type other) {
		return false;
	}
	
	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public String toString() {
		return elemType + "[]";
	}
	
	public Type getElemType() {
		return elemType;
	}

}
