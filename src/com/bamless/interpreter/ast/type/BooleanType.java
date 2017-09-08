package com.bamless.interpreter.ast.type;

/**
 * Can only use equality operations over a boolean, and the other
 * operand must also be a boolean.
 */
public class BooleanType extends Type {

	@Override
	public Type plus(Type other) {
		if(other == Type.STRING)
			return Type.STRING;
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
	public Type relationalOp(Type other) {
		return null;
	}

	@Override
	public Type equalityOp(Type other) {
		if(other == Type.BOOLEAN)
			return Type.BOOLEAN;
		
		return null;
	}
	
	@Override
	public Type logicalOp(Type other) {
		if(other == Type.BOOLEAN)
			return Type.BOOLEAN;
		
		return null;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean canAssign(Type other) {
		return other == Type.BOOLEAN;
	}
	
	@Override
	public String toString() {
		return "BOOLEAN";
	}

}
