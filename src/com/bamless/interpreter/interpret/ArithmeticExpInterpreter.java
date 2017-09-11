package com.bamless.interpreter.interpret;

import java.math.BigDecimal;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;

public class ArithmeticExpInterpreter extends VisitorAdapter<BigDecimal, Void> {
	private Interpreter interpreter;

	public ArithmeticExpInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public BigDecimal visit(ArithmeticBinExpression e, Void arg) {
		BigDecimal l = e.getLeft().accept(this, null);
		BigDecimal r = e.getRight().accept(this, null);
		
		switch(e.getOperation()) {
		case PLUS:
			return l.add(r);
		case MINUS:
			return l.subtract(r);
		case MULT:
			return l.multiply(r);
		case MOD:
			return l.remainder(r);
		case DIV:
			if(e.getLeft().getType() == Type.INT && e.getRight().getType() == Type.INT)
				return l.divide(r).abs();
			else
				return l.divide(r);
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public BigDecimal visit(AssignExpression e, Void arg) {
		BigDecimal res = e.getExpression().accept(this, null);
		if(e.getType() == Type.INT)
			interpreter.getMemEnv().set((Lvalue) e.getLvalue(), res.intValue());
		else
			interpreter.getMemEnv().set((Lvalue) e.getLvalue(), res.floatValue());
		
		return res;
	}
	
	@Override
	public BigDecimal visit(VarLiteral v, Void arg) {
		if(v.getType() == Type.INT)
			return BigDecimal.valueOf(interpreter.getMemEnv().<Integer>retrieve(v));
		else 
			return BigDecimal.valueOf(interpreter.getMemEnv().<Float>retrieve(v));
	}
	
	@Override
	public BigDecimal visit(ArrayAccess a, Void arg) {
		if(a.getType() == Type.INT) {
			return BigDecimal.valueOf(interpreter.getMemEnv().<Integer>retrieve(a));
		}else {
			return BigDecimal.valueOf(interpreter.getMemEnv().<Float>retrieve(a));
		}
	}
	
	@Override
	public BigDecimal visit(FloatLiteral f, Void arg) {
		return BigDecimal.valueOf(f.getValue());
	}
	
	@Override
	public BigDecimal visit(IntegerLiteral i, Void arg) {
		return BigDecimal.valueOf(i.getValue());
	}

}
