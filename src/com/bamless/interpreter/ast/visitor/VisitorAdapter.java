package com.bamless.interpreter.ast.visitor;

import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;

public class VisitorAdapter<T, A> implements GenericVisitor<T, A> {

	@Override
	public T visit(Visitable v, A arg) {
		return null;
	}

	@Override
	public T visit(IfStatement v, A arg) {
		v.getThenStmt().accept(this, null);
		if(v.getElseStmt() != null) {
			v.getElseStmt().accept(this, null);
		}
		return null;
	}

	@Override
	public T visit(WhileStatement v, A arg) {
		v.getBody().accept(this, null);
		return null;
	}

	
	@Override
	public T visit(ForStatement v, A arg) {
		v.getBody().accept(this, null);
		return null;
	}

	@Override
	public T visit(BlockStatement v, A arg) {
		for(Statement stmt : v.getStmts()) {
			stmt.accept(this, null);
		}
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
