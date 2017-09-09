package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.memenvironment.Array;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment;

public class ArrayExpInterpreter extends VisitorAdapter<Array, Void> {
	private MemoryEnvironment memEnv;
	
	public ArrayExpInterpreter(MemoryEnvironment memEnv) {
		this.memEnv = memEnv;
	}
	
	@Override
	public Array visit(VarLiteral v, Void arg) {
		return (Array) memEnv.retrieve(v);
	}
	
	@Override
	public Array visit(ArrayAccess a, Void arg) {
		return (Array) memEnv.retrieve(a);
	}
	
	@Override
	public Array visit(AssignExpression e, Void arg) {
		Array res = e.getExpression().accept(this, null);
		memEnv.set((Lvalue) e.getLvalue(), res);
		return res;
	}

}
