package com.bamless.interpreter.interpret.expeval;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.CastExpression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type.TypeID;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class ArithmeticEval extends VisitorAdapter<BigDecimal, Frame> {
	private Interpreter interpreter;

	public ArithmeticEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	
	@Override
	public BigDecimal visit(ArithmeticBinExpression e, Frame frame) {
		BigDecimal l = e.getLeft().accept(this, frame);
		BigDecimal r = e.getRight().accept(this, frame);
		
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
			if(e.getLeft().getType().getId() == TypeID.INT && 
					e.getRight().getType().getId() == TypeID.INT)
				return l.divide(r, 6,RoundingMode.HALF_EVEN).abs();
			else
				return l.divide(r, 6, RoundingMode.HALF_EVEN);
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public BigDecimal visit(AssignExpression e, Frame frame) {
		BigDecimal res = e.getExpression().accept(this, frame);
		if(e.getType().getId() == TypeID.INT)
			frame.set((Lvalue) e.getLvalue(), res.intValue());
		else
			frame.set((Lvalue) e.getLvalue(), res.floatValue());
		
		return res;
	}
	
	@Override
	public BigDecimal visit(FuncCallExpression f, Frame frame) {
		interpreter.callFunction(f);

		if(f.getType().getId() == TypeID.INT)
			return BigDecimal.valueOf(frame.<Integer>getReturnRegister());
		else
			return BigDecimal.valueOf(frame.<Float>getReturnRegister());
	}
	
	@Override
	public BigDecimal visit(PreIncrementOperation p, Frame frame) {
		BigDecimal res = p.getExpression().accept(this, frame);
		
		switch(p.getOperator()) {
		case INCR:
			res = res.add(BigDecimal.valueOf(1));
			break;
		case DECR:
			res = res.subtract(BigDecimal.valueOf(1));
			break;
		}
		
		if(p.getType().getId() == TypeID.INT)
			frame.set((Lvalue) p.getExpression(), res.intValue());
		else
			frame.set((Lvalue) p.getExpression(), res.floatValue());
		
		return res;
	}
	
	@Override
	public BigDecimal visit(PostIncrementOperation p, Frame frame) {
		BigDecimal old = p.getExpression().accept(this, frame);
		
		BigDecimal res = null;
		switch(p.getOperator()) {
		case INCR:
			res = old.add(BigDecimal.valueOf(1));
			break;
		case DECR:
			res = old.subtract(BigDecimal.valueOf(1));
			break;
		}
		
		if(p.getType().getId() == TypeID.INT)
			frame.set((Lvalue) p.getExpression(), res.intValue());
		else
			frame.set((Lvalue) p.getExpression(), res.floatValue());
		
		return old;
	}
	
	@Override
	public BigDecimal visit(CastExpression c, Frame arg) {
		BigDecimal ret = c.getExpression().accept(this, arg);
		if(c.getType().getId() == TypeID.INT)
			return BigDecimal.valueOf(ret.intValue());
		else
			return BigDecimal.valueOf(ret.floatValue()); 
	}
	
	@Override
	public BigDecimal visit(VarLiteral v, Frame frame) {
		if(v.getType().getId() == TypeID.INT)
			return BigDecimal.valueOf(frame.<Integer>retrieve(v));
		else 
			return BigDecimal.valueOf(frame.<Float>retrieve(v));
	}
	
	@Override
	public BigDecimal visit(ArrayAccess a, Frame frame) {
		if(a.getType().getId() == TypeID.INT)
			return BigDecimal.valueOf(frame.<Integer>retrieve(a));
		else
			return BigDecimal.valueOf(frame.<Float>retrieve(a));
	}
	
	@Override
	public BigDecimal visit(FloatLiteral f, Frame frame) {
		return BigDecimal.valueOf(f.getValue());
	}
	
	@Override
	public BigDecimal visit(IntegerLiteral i, Frame frame) {
		return BigDecimal.valueOf(i.getValue());
	}

}