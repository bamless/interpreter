package com.bamless.interpreter.ast.type;

/**
 * Applying any operation on a float would reult in a float if
 * the other operand is a float or an int (incompatible otherwise).
 */
public class FloatType extends Type {
	
	@Override
	public Type plus(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type minus(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type times(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type div(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type modulus(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type relationalOp(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type equalityOp(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public boolean canAssign(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return true;
		return false;
	}

}
