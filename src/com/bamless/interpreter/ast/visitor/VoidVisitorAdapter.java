package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public class VoidVisitorAdapter<A> implements VoidVisitor<A> {

	@Override
	public void visit(Visitable v, A arg) {
	}

	@Override
	public void visit(IfStatement v, A arg) {
	}

	@Override
	public void visit(WhileStatement v, A arg) {
	}

	@Override
	public void visit(BlockStatement v, A arg) {
	}

	@Override
	public void visit(VarDecl v, A arg) {
	}

	@Override
	public void visit(Identifier v, A arg) {
	}

	@Override
	public void visit(ForStatement v, A arg) {
	}
}