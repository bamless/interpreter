package com.bamless.interpreter.ast.type;

public class FloatType extends Type {
	
	/**
	 * Applying an arithmetic operation on a float would reult in a float if
	 * the other operand is a float or an int (incompatible otherwise).
	 */
	@Override
	public Type plus(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		if(other == Type.STRING)
			return Type.STRING;
		return null;
	}

	@Override
	public Type minus(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.FLOAT;
		return null;
	}

	@Override
	public Type mul(Type other) {
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

	/**
	 * relational and equality expression over a float resolves to a boolean
	 * if the other operand is another float or an int
	 */
	@Override
	public Type relationalOp(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.BOOLEAN;
		return null;
	}

	@Override
	public Type equalityOp(Type other) {
		if(other == Type.FLOAT || other == Type.INT)
			return Type.BOOLEAN;
		return null;
	}
	
	@Override
	public Type logicalOp(Type other) {
		return null;
	}

	@Override
	public boolean canAssign(Type other) {
		return other == Type.FLOAT || other == Type.INT;
	}
	
	@Override
	public String toString() {
		return "FLOAT";
	}

}
