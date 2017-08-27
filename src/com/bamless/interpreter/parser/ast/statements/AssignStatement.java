package com.bamless.interpreter.parser.ast.statements;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.visitor.GenericVisitor;
import com.bamless.interpreter.parser.ast.visitor.VoidVisitor;

public class AssignStatement extends Statement {
	private Identifier id;
	private Expression e;
	
	public AssignStatement(Position pos, Identifier id, Expression e) {
		super(pos);
		this.id = id;
		this.e = e;
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public Identifier getId() {
		return id;
	}

	public Expression getExpression() {
		return e;
	}

}
