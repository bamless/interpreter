package com.bamless.interpreter.parser.ast.visitor;

import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.statements.AssignStatement;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;

public class VisitorAdapter<T, A> implements Visitor<T, A> {

	@Override
	public T visit(Visitable v, A arg) {
		return null;
	}

	@Override
	public T visit(AssignStatement v, A arg) {
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
