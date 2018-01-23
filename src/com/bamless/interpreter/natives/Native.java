package com.bamless.interpreter.natives;

import com.bamless.interpreter.ast.type.Type;

public abstract class Native<T> {
	private String id;
	private Type type;
	private Type[] argType;
	
	public Native(Type t, String id, Type... argType) {
		this.type = t;
		this.id = id;
		this.argType = argType;
	}
	
	public abstract T call(Object... args);
	
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
