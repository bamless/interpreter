package com.bamless.interpreter.ast.statement;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class IfStatement extends Statement {
	private Expression condition;
	private Statement thenStmt;
	private Statement elseStmt;
	
	public IfStatement(Expression condition, Statement thenStmt, Position pos) {
		this(condition, thenStmt, null, pos);
	}
	
	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	public IfStatement(Expression condition, Statement thenStmt, Statement elseStmt, Position pos) {
		super(pos);
		this.condition = condition;
		this.thenStmt = thenStmt;
		this.elseStmt = elseStmt;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Expression getCondition() {
		return condition;
	}

	public Statement getThenStmt() {
		return thenStmt;
	}

	public Statement getElseStmt() {
		return elseStmt;
	}
	
}
