package com.bamless.interpreter.ast.type;

public class StringType extends Type {

	@Override
	public Type plus(Type other) {
		return Type.STRING;
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
		if(other == Type.STRING)
			return Type.BOOLEAN;
		return null;
	}

	@Override
	public boolean isCompatible(Type other) {
		return other == Type.STRING;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public String toString() {
		return "STRING";
	}

}
