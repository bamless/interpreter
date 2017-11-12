package com.bamless.interpreter.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class FuncDecl extends ASTNode {
	private static final List<FormalArg> VOID_ARGS = Collections.unmodifiableList(new ArrayList<FormalArg>(0));
	
	private Type retType;
	private Identifier id;
	
	private List<FormalArg> args;
	private BlockStatement body;
	
	public FuncDecl(Position pos, Type retType, Identifier id,  List<FormalArg> args, BlockStatement body) {
		super(pos);
		this.retType = retType;
		this.id = id;
		this.args = args == null ? VOID_ARGS : args;
		this.body = body;
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
		return retType;
	}

	public Identifier getId() {
		return id;
	}
	
	public List<FormalArg> getFormalArgs() {
		return Collections.unmodifiableList(args);
	}
	
	public BlockStatement getBody() {
		return body;
	}
	
}
