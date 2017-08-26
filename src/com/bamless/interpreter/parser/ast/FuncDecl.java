package com.bamless.interpreter.parser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.type.Type;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class FuncDecl extends ASTNode {
	private Type type;
	private Identifier id;
	private List<FormalArg> args = new ArrayList<>();
	
	public FuncDecl(Position pos, Type type, Identifier id,  List<FormalArg> args) {
		super(pos);
		this.type = type;
		this.id = id;
		this.args.addAll(args);
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
	
	public List<FormalArg> getFormalArgs() {
		return Collections.unmodifiableList(args);
	}
	
}
