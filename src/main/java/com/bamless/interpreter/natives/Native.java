package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.interpret.Interpreter;

public abstract class Native<T> {
	public static final Type[] VOID = new Type[0];

	private String id;
	private Type type;
	private Type[] argType;

	public Native(Type t, String id, Type... argType) {
		this.type = t;
		this.id = id;
		this.argType = argType;
	}

	public abstract T call(Interpreter i, Object... args);

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public Type[] getArgTypes() {
		return argType;
	}

}
