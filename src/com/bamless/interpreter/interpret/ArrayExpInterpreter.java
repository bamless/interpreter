package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.runtime.Array;
import com.bamless.interpreter.interpret.runtime.Runtime;

public class ArrayExpInterpreter extends VisitorAdapter<Array, Void> {
	private ArithmeticExpInterpreter ai;
	private Runtime runtime;
	
	public ArrayExpInterpreter(Runtime r) {
		this.runtime = r;
	}
	
	@Override
	public Array visit(AssignExpression e, Void arg) {
		ArrayAccess lval = (ArrayAccess) e.getLvalue();
		
		Array arr = lval.getLvalue().accept(this, arg);
		int index = lval.getIndex().accept(ai, arg).intValue();
		
		arr.set(index, e.getExpression().accept(this, arg));
		return (Array) arr.get(index);
	}
	
	@Override
	public Array visit(ArrayAccess a, Void arg) {
		Array arr = a.getLvalue().accept(this, arg);
		return (Array) arr.get(a.getIndex().accept(ai, null).intValue());
	}
	
	@Override
	public Array visit(VarLiteral v, Void arg) {
		return (Array) runtime.retrieve(v.getId());
	}
	
	public void init(ArithmeticExpInterpreter ai) {
		this.ai = ai;
	}
	
}
