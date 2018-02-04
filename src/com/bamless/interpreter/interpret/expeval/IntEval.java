package com.bamless.interpreter.interpret.expeval;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.CastExpression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class IntEval extends VisitorAdapter<Integer, Frame> {
	private Interpreter interpreter;

	public IntEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public Integer visit(ArithmeticBinExpression e, Frame frame) {
		int l = e.getLeft().accept(this, frame);
		int r = e.getRight().accept(this, frame);
		
		switch(e.getOperation()) {
		case PLUS:
			return l + r;
		case MINUS:
			return l - r;
		case MULT:
			return l * r;
		case MOD:
			return l % r;
		case DIV:
			return l / r;
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Integer visit(AssignExpression e, Frame frame) {
		int res = e.getExpression().accept(this, frame);
		frame.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	@Override
	public Integer visit(FuncCallExpression f, Frame frame) {
		interpreter.callFunction(f);
		return frame.<Integer>getReturnRegister();
	}
	
	@Override
	public Integer visit(PreIncrementOperation p, Frame frame) {
		int res = p.getExpression().accept(this, frame);
		
		switch(p.getOperator()) {
		case INCR:
			res = res + 1;
			break;
		case DECR:
			res = res - 1;
			break;
		}
		
		frame.set((Lvalue) p.getExpression(), res);
		return res;
	}
	
	@Override
	public Integer visit(PostIncrementOperation p, Frame frame) {
		int old = p.getExpression().accept(this, frame);
		
		int res;
		switch(p.getOperator()) {
		case INCR:
			res = old + 1;
			break;
		case DECR:
			res = old - 1;
			break;
		default:
			res = 0;
			break;
		}
		
		frame.set((Lvalue) p.getExpression(), res);
		return old;
	}
	
	@Override
	public Integer visit(CastExpression c, Frame frame) {
		switch (c.getExpression().getType().getId()) {
		case FLOAT:
			return c.getExpression().accept(interpreter.floatingPoint(), frame).intValue();
		default:
			throw new RuntimeError("Fatal error.");
		}
	}
	
	@Override
	public Integer visit(VarLiteral v, Frame frame) {
		return frame.<Integer>retrieve(v);
	}
	
	@Override
	public Integer visit(ArrayAccess a, Frame frame) {
		return frame.<Integer>retrieve(a);
	}
	
	@Override
	public Integer visit(IntegerLiteral i, Frame frame) {
		return i.getValue();
	}
	
}
