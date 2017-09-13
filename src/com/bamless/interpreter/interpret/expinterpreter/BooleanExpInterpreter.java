package com.bamless.interpreter.interpret.expinterpreter;

import java.math.BigDecimal;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.RuntimeError;

public class BooleanExpInterpreter extends VisitorAdapter<Boolean, Void> {
	private Interpreter interpreter;
	
	public BooleanExpInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Boolean visit(RelationalExpression r, Void arg) {
		BigDecimal left  = r.getLeft().accept(interpreter.getArithmeticExpInterpreter(), null);
		BigDecimal right = r.getRight().accept(interpreter.getArithmeticExpInterpreter(), null);
		
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
	public Boolean visit(EqualityExpression e, Void arg) {
		Object l, r;
		if(e.getLeft().getType() == Type.INT || e.getLeft().getType() == Type.FLOAT) {
			l = e.getLeft().accept(interpreter.getArithmeticExpInterpreter(), null);
			r = e.getRight().accept(interpreter.getArithmeticExpInterpreter(), null);
		} else if(e.getLeft().getType() == Type.STRING) {
			l = e.getLeft().accept(interpreter.getStringExpInterpreter(), null);
			r = e.getRight().accept(interpreter.getStringExpInterpreter(), null);
		} else if(e.getLeft().getType().isArray()){
			l = e.getLeft().accept(interpreter.getArrayExpInterpreter(), null);
			r = e.getRight().accept(interpreter.getArrayExpInterpreter(), null);
		} else {
			l = e.getLeft().accept(this, null);
			r = e.getRight().accept(this, null);
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
	public Boolean visit(LogicalExpression l, Void arg) {
		boolean left  = l.getLeft().accept(this, null);
		boolean right = l.getRight().accept(this, null);
		
		switch(l.getOperation()) {
		case AND:
			return left && right;
		case OR:
			return left || right;
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Boolean visit(LogicalNotExpression n, Void arg) {
		return !n.getExpression().accept(this, null);
	}
	
	@Override
	public Boolean visit(VarLiteral v, Void arg) {
		return interpreter.getMemEnv().<Boolean>retrieve(v);
	}
	
	@Override
	public Boolean visit(ArrayAccess a, Void arg) {
		return interpreter.getMemEnv().<Boolean>retrieve(a);
	}
	
	@Override
	public Boolean visit(BooleanLiteral b, Void arg) {
		return b.getValue();
	}
	
	@Override
	public Boolean visit(AssignExpression e, Void arg) {
		boolean res = e.getExpression().accept(this, null);
		interpreter.getMemEnv().set((Lvalue) e.getLvalue(), res);
		return res;
	}

}
