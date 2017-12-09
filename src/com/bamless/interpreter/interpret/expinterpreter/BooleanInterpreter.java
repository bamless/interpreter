package com.bamless.interpreter.interpret.expinterpreter;

import java.math.BigDecimal;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment.Frame;

public class BooleanInterpreter extends VisitorAdapter<Boolean, Frame> {
	private Interpreter interpreter;
	
	public BooleanInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Boolean visit(RelationalExpression r, Frame frame) {
		BigDecimal left  = r.getLeft().accept(interpreter.arithmeticInterpreter(), frame);
		BigDecimal right = r.getRight().accept(interpreter.arithmeticInterpreter(), frame);
		
		switch(r.getOperation()) {
		case LT:
			return left.compareTo(right) < 0;
		case LE:
			return left.compareTo(right) <= 0;
		case GT:
			return left.compareTo(right) > 0;
		case GE:
			return left.compareTo(right) >= 0;
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Boolean visit(EqualityExpression e, Frame frame) {
		Object l = null, r = null;
		if(e.getLeft().getType() == Type.INT || e.getLeft().getType() == Type.FLOAT) {
			BigDecimal bl = e.getLeft().accept(interpreter.arithmeticInterpreter(), frame);
			BigDecimal br = e.getRight().accept(interpreter.arithmeticInterpreter(), frame);
			
			switch(e.getOperation()) {
			case EQ:
				return bl.compareTo(br) == 0;
			case NEQ:
				return bl.compareTo(br) != 0;
			}
		} else if(e.getLeft().getType() == Type.STRING) {
			l = e.getLeft().accept(interpreter.stringInterpreter(), frame);
			r = e.getRight().accept(interpreter.stringInterpreter(), frame);
		} else if(e.getLeft().getType().isArray()){
			l = e.getLeft().accept(interpreter.arrayInterpreter(), frame);
			r = e.getRight().accept(interpreter.arrayInterpreter(), frame);
		} else {
			l = e.getLeft().accept(this, frame);
			r = e.getRight().accept(this, frame);
		}
		
		switch(e.getOperation()) {
		case EQ:
			return l.equals(r);
		case NEQ:
			return !l.equals(r);
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Boolean visit(LogicalExpression l, Frame frame) {
		switch(l.getOperation()) {
		case AND:
			return l.getLeft().accept(this, frame) && l.getRight().accept(this, frame);
		case OR:
			return l.getLeft().accept(this, frame) || l.getRight().accept(this, frame);
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Boolean visit(LogicalNotExpression n, Frame frame) {
		return !n.getExpression().accept(this, frame);
	}
	
	@Override
	public Boolean visit(VarLiteral v, Frame frame) {
		return frame.<Boolean>retrieve(v);
	}
	
	@Override
	public Boolean visit(ArrayAccess a, Frame frame) {
		return frame.<Boolean>retrieve(a);
	}
	
	@Override
	public Boolean visit(BooleanLiteral b, Frame frame) {
		return b.getValue();
	}
	
	@Override
	public Boolean visit(AssignExpression e, Frame frame) {
		boolean res = e.getExpression().accept(this, frame);
		frame.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	@Override
	public Boolean visit(FuncCallExpression f, Frame frame) {
		interpreter.callFunction(f);
		return frame.<Boolean>getReturnRegister();
	}

}
