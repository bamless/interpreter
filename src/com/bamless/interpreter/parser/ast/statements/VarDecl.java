package com.bamless.interpreter.parser.ast.statements;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.type.Type;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class VarDecl extends Statement {
	private Type type;
	private Identifier id;
	
	public VarDecl(Position pos, Type type, Identifier id) {
		super(pos);
		this.type = type;
		this.id = id;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}
	
	public Type getType() {
		return type;
	}

	public Identifier getId() {
		return id;
	}

}
