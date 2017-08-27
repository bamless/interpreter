package com.bamless.interpreter.parser.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.type.Type;
import com.bamless.interpreter.parser.ast.visitor.GenericVisitor;
import com.bamless.interpreter.parser.ast.visitor.VoidVisitor;

public class FormalArg extends ASTNode {
	private Type type;
	private Identifier id;
	
	public FormalArg(Position pos, Type type, Identifier id) {
		super(pos);
		this.id = id;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Type getType() {
		return type;
	}

	public Identifier getIdentifier() {
		return id;
	}
	
}