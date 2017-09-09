package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.memenvironment.Array;

public class ArrayExpInterpreter extends VisitorAdapter<Array, Void> {
	private Interpreter interpreter;
	
	public ArrayExpInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public Array visit(VarLiteral v, Void arg) {
		return (Array) interpreter.getMemEnv().retrieve(v);
	}
	
	@Override
	public Array visit(ArrayAccess a, Void arg) {
		return (Array) interpreter.getMemEnv().retrieve(a);
	}
	
	@Override
	public Array visit(AssignExpression e, Void arg) {
		Array res = e.getExpression().accept(this, null);
		interpreter.getMemEnv().set((Lvalue) e.getLvalue(), res);
		return res;
	}

}
