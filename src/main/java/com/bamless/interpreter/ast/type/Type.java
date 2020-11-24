package com.bamless.interpreter.ast.type;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Type implements Serializable {
	/** Singleton types */
	public static final Type INT = new IntType();
	public static final Type FLOAT = new FloatType();
	public static final Type BOOLEAN = new BooleanType();
	public static final Type STRING = new StringType();
	public static final Type VOID = new VoidType();

	public final static int INT_WIDENFACT = 1;
	public final static int FLOAT_WIDENFACT = 2;
	public final static int NULL_WIDENFACT = -1;

	private static final HashMap<Type, ArrayType> arrayFromType = new HashMap<>();

	private TypeID id;

	/**
	 * widen factor of the type. Used to apply type coercion to the widest type when
	 * needed
	 */
	private int widenFactor;

	protected Type(TypeID id, int widenFactor) {
		this.id = id;
		this.widenFactor = widenFactor;
	}

	public static Type valueOf(String type) {
		switch(type.toUpperCase()) {
		case "INT":
			return INT;
		case "FLOAT":
			return FLOAT;
		case "BOOLEAN":
			return BOOLEAN;
		case "STRING":
			return STRING;
		case "VOID":
			return VOID;
		}

		throw new IllegalArgumentException("Invalid type name");
	}

	public static ArrayType arrayType(Type t) {
		if(!arrayFromType.containsKey(t)) {
			ArrayType at = new ArrayType(t);
			arrayFromType.put(t, at);
		}
		return arrayFromType.get(t);
	}

	/**
	 * For any given operation, return the type result of applying that operation
	 * over the given types (or null if the operation cannot be applied over the
	 * types)
	 */
	public abstract Type plus(Type other);

	public abstract Type minus(Type other);

	public abstract Type mul(Type other);

	public abstract Type div(Type other);

	public abstract Type modulus(Type other);

	public abstract Type logicalOp(Type other);

	public abstract Type relationalOp(Type other);

	public abstract Type equalityOp(Type other);

	public abstract boolean isCompatible(Type other);

	public abstract boolean isArray();

	public abstract String toString();

	public TypeID getId() {
		return id;
	}

	public int getWidenFactor() {
		return widenFactor;
	}

	public enum TypeID {
		INT, FLOAT, BOOLEAN, STRING, VOID, ARRAY
	}
}
