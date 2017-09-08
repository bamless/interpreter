package com.bamless.interpreter.ast;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.visitor.GenericVisitor;
import com.bamless.interpreter.ast.visitor.VoidVisitor;

public class Program extends ASTNode {
	private BlockStatement stmts;
	
	public Program(Position start, BlockStatement stmts) {
		super(start);
		this.stmts = stmts;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public BlockStatement getBlock() {
		return stmts;
	}
	
}
