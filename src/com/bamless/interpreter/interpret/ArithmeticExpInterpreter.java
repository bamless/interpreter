package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.runtime.Runtime;

public class ArithmeticExpInterpreter extends VisitorAdapter<Float, Void> {
	private Runtime runtime;

	public ArithmeticExpInterpreter(Runtime runtime) {
		this.runtime = runtime;
	}
	
	@Override
	public Float visit(ArithmeticBinExpression e, Void arg) {
		float l = e.getLeft().accept(this, null);
		float r = e.getRight().accept(this, null);
		
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
			if(e.getLeft().getType() == Type.INT && e.getRight().getType() == Type.INT)
				return (float) ((int) l / (int) r);
			else
				return l / r;
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Float visit(AssignExpression e, Void arg) {
		float res = e.getExpression().accept(this, null).floatValue();
		if(e.getType() == Type.INT)
			runtime.set(e.getLvalue(), (int) res);
		else
			runtime.set(e.getLvalue(), res);
		
		return res;
	}
	
	@Override
	public Float visit(VarLiteral v, Void arg) {
		if(v.getType() == Type.INT)
			return ((Integer) runtime.retrieve(v)).floatValue();
		else 
			return (Float) runtime.retrieve(v);
	}
	
	@Override
	public Float visit(ArrayAccess a, Void arg) {
		if(a.getType() == Type.INT) {
			return ((Integer) runtime.retrieve(a)).floatValue();
		}else {
			return (Float) runtime.retrieve(a);
		}
	}
	
	@Override
	public Float visit(FloatLiteral f, Void arg) {
		return f.getValue();
	}
	
	@Override
	public Float visit(IntegerLiteral i, Void arg) {
		return (float) i.getValue();
	}

}
