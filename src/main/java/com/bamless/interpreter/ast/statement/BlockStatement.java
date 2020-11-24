package com.bamless.interpreter.ast.statement;

import java.util.Collection;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class BlockStatement extends Statement {
	private Statement[] stmts;

	public BlockStatement(Collection<Statement> stmts, Position pos) {
		super(pos);
		this.stmts = stmts.toArray(new Statement[stmts.size()]);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public Statement[] getStmts() {
		return stmts;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

}
