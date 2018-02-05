package com.bamless.interpreter.interpret.expeval;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.Return;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.Array;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class ArrayEval extends VisitorAdapter<Array, Frame> {
	private Interpreter interpreter;
	
	public ArrayEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public Array visit(VarLiteral v, Frame frame) {
		return (Array) frame.retrieve(v);
	}
	
	@Override
	public Array visit(ArrayAccess a, Frame frame) {
		return (Array) frame.retrieve(a);
	}
	
	@Override
	public Array visit(AssignExpression e, Frame frame) {
		Array res = e.getExpression().accept(this, frame);
		frame.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	@Override
	public Array visit(FuncCallExpression f, Frame frame) {
		try {
			interpreter.callFunction(f);
		} catch(Return r) {
			return (Array) r.getVal();
		}
		throw new RuntimeError("Fatal error, function " + f + " declares return type but doesn't return");
	}

}
