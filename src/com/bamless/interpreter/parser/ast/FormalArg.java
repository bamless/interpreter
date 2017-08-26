package com.bamless.interpreter.parser.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.type.Type;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class FormalArg extends ASTNode {
	private Type type;
	private Identifier id;
	
	public FormalArg(Position pos, Type type, Identifier id) {
		super(pos);
		this.id = id;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Type getType() {
		return type;
	}

	public Identifier getIdentifier() {
		return id;
	}
	
}