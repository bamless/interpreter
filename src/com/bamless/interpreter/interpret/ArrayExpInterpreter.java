package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.runtime.Array;
import com.bamless.interpreter.interpret.runtime.Runtime;

public class ArrayExpInterpreter extends VisitorAdapter<Array, Void> {
	private Runtime runtime;
	
	public ArrayExpInterpreter(Runtime runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public Array visit(VarLiteral v, Void arg) {
		return (Array) runtime.retrieve(v);
	}
	
	@Override
	public Array visit(ArrayAccess a, Void arg) {
		return (Array) runtime.retrieve(a);
	}
	
	@Override
	public Array visit(AssignExpression e, Void arg) {
		Array res = e.getExpression().accept(this, null);
		runtime.set((Lvalue) e.getLvalue(), res);
		return res;
	}

}
