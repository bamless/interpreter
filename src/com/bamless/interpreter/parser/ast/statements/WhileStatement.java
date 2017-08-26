package com.bamless.interpreter.parser.ast.statements;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class WhileStatement extends Statement {
	private Expression condition;
	private Statement body;

	public WhileStatement(Expression condition, Statement body, Position start) {
		super(start);
		this.condition = condition;
		this.body = body;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}
	
	public Expression getCondition() {
		return condition;
	}
	
	public Statement getBody() {
		return body;
	}

}
