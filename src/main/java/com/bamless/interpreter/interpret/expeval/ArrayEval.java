package com.bamless.interpreter.interpret.expeval;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.Return;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.CmlArr;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class ArrayEval extends VisitorAdapter<CmlArr, Frame> {
	private Interpreter interpreter;
	
	public ArrayEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public CmlArr visit(VarLiteral v, Frame frame) {
		return (CmlArr) frame.retrieve(v);
	}
	
	@Override
	public CmlArr visit(ArrayAccess a, Frame frame) {
		return (CmlArr) frame.retrieve(a);
	}
	
	@Override
	public CmlArr visit(AssignExpression e, Frame frame) {
		CmlArr res = e.getExpression().accept(this, frame);
		frame.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	@Override
	public CmlArr visit(FuncCallExpression f, Frame frame) {
		try {
			interpreter.callFunction(f);
		} catch(Return r) {
			return (CmlArr) r.val;
		}
		throw new RuntimeError("Fatal error, function " + f + " declares return type but doesn't return");
	}

}
