package com.bamless.interpreter.semantic.symboltable;

import com.bamless.interpreter.ast.type.Type;

public class Symbol {
	public final Type type;
	
	public Symbol(Type type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
}
