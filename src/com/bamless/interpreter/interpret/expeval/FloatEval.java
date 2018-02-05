package com.bamless.interpreter.interpret.expeval;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.CastExpression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.Return;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class FloatEval extends VisitorAdapter<Float, Frame> {
	private Interpreter interpreter;

	public FloatEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public Float visit(ArithmeticBinExpression e, Frame frame) {
		float l = e.getLeft().accept(this, frame);
		float r = e.getRight().accept(this, frame);
		
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
	public Float visit(AssignExpression e, Frame frame) {
		float res = e.getExpression().accept(this, frame);
		frame.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	@Override
	public Float visit(FuncCallExpression f, Frame frame) {
		try {
			interpreter.callFunction(f);
		} catch(Return r) {
			return (Float) r.getVal();
		}
		throw new RuntimeError("Fatal error, function " + f + " declares return type but doesn't return");
	}
	
	@Override
	public Float visit(PreIncrementOperation p, Frame frame) {
		float res = p.getExpression().accept(this, frame);
		
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
	public Float visit(PostIncrementOperation p, Frame frame) {
		float old = p.getExpression().accept(this, frame);
		
		float res;
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
	public Float visit(CastExpression c, Frame frame) {
		switch (c.getExpression().getType().getId()) {
		case INT:
			return c.getExpression().accept(interpreter.integer(), frame).floatValue();
		default:
			throw new RuntimeError("Fatal error.");
		}
	}
	
	@Override
	public Float visit(VarLiteral v, Frame frame) {
		return (Float) frame.retrieve(v);
	}
	
	@Override
	public Float visit(ArrayAccess a, Frame frame) {
		return (Float) frame.retrieve(a);
	}
	
	@Override
	public Float visit(FloatLiteral i, Frame frame) {
		return i.getValue();
	}
	
}
