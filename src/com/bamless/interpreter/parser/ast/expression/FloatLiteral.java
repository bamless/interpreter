package com.bamless.interpreter.parser.ast.expression;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.parser.ast.visitor.Visitor;

public class FloatLiteral extends Expression {
	private float value;
	
	public FloatLiteral(Position start, float value) {
		super(start);
		this.value = value;
	}

	@Override
	public <T, A> T accept(Visitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	public float getValue() {
		return value;
	}

}
