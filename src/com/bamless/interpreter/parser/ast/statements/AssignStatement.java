package com.bamless.interpreter.parser.ast.statements;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class AssignStatement extends Statement {
	private Identifier id;
	private Expression e;
	
	public AssignStatement(Position pos, Identifier id, Expression e) {
		super(pos);
		this.id = id;
		this.e = e;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Identifier getId() {
		return id;
	}

	public Expression getE() {
		return e;
	}

}
