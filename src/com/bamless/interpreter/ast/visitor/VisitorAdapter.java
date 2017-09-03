package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public class VisitorAdapter<T, A> implements GenericVisitor<T, A> {

	@Override
	public T visit(Visitable v, A arg) {
		return null;
	}

	@Override
	public T visit(IfStatement v, A arg) {
		return null;
	}

	@Override
	public T visit(WhileStatement v, A arg) {
		return null;
	}

	@Override
	public T visit(BlockStatement v, A arg) {
		return null;
	}

	@Override
	public T visit(VarDecl v, A arg) {
		return null;
	}

	@Override
	public T visit(Identifier v, A arg) {
		return null;
	}

}
