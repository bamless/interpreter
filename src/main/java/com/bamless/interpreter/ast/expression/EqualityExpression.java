package com.bamless.interpreter.ast.expression;

import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class EqualityExpression extends BinaryExpression {
	public static enum EqualityOperation {
		EQ("=="), NEQ("!=");
		
		private String repr;
		
		private EqualityOperation(String repr) { this.repr = repr; }
		
		@Override
		public String toString() { return repr; }
	}
	private EqualityOperation operation;
	
	public EqualityExpression(EqualityOperation op, Expression left, Expression right) {
		super(left, right);
		this.operation = op;
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}
	
	public EqualityOperation getOperation() {
		return operation;
	}
	
	@Override
	public String toString() {
		return "(" + getLeft() + " " + operation + " " + getRight() + ")";
	}
}
