package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.type.Type;

public abstract class Expression extends Statement {
	private Type type;
	
	public Expression(Position start) {
		super(start);
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

}
