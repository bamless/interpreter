package com.bamless.interpreter.ast.type;

/**
 * Applying any operation on an int would result in an int if the other operand is
 * also an int, or in a float if the other operand is a float (incompatible otherwise).
 */
public class IntType extends Type {

	@Override
	public Type plus(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type minus(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type times(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type div(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type modulus(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type relationalOp(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public Type equalityOp(Type other) {
		if(other == Type.INT)
			return Type.INT;
		if(other == Type.FLOAT)
			return Type.FLOAT;
		
		return null;
	}

	@Override
	public boolean canAssign(Type other) {
		if(other == Type.INT || other == Type.FLOAT)
			return true;
		
		return false;
	}

}
