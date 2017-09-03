package com.bamless.interpreter.parser.ast.visitor;

import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.ForStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;

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
