package com.bamless.interpreter.ast;

import java.util.Collection;

import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class FuncDecl extends ASTNode {
	private static final FormalArg[] VOID_ARGS = new FormalArg[0];

	private Type retType;
	private Identifier id;

	private FormalArg[] args;
	private BlockStatement body;

	public FuncDecl(Position pos, Type retType, Identifier id, Collection<FormalArg> args,
			BlockStatement body) {
		super(pos);
		this.retType = retType;
		this.id = id;
		this.args = args == null ? VOID_ARGS : args.toArray(new FormalArg[args.size()]);
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

	public FormalArg[] getFormalArgs() {
		return args;
	}

	public Type[] getFormalArgsTypes() {
		Type[] t = new Type[args.length];
		for(int i = 0; i < args.length; i++)
			t[i] = args[i].getType();
		return t;
	}

	public BlockStatement getBody() {
		return body;
	}

}
