package com.bamless.interpreter.interpret;

import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.runtime.Runtime;

public class BooleanExpInterpreter extends VisitorAdapter<Boolean, Void> {
	private ArithmeticExpInterpreter arithmeticInterpreter;
	private StringExpInterpreter stringInterpreter;
	private Runtime runtime;
	
	public BooleanExpInterpreter(Runtime runtime) {
		this.runtime = runtime;
	}

	@Override
	public Boolean visit(RelationalExpression r, Void arg) {
		float left  = r.getLeft().accept(arithmeticInterpreter, null);
		float right = r.getRight().accept(arithmeticInterpreter, null);
		
		switch(r.getOperation()) {
		case LT:
			return left < right;
		case LE:
			return left <= right;
		case GT:
			return left > right;
		case GE:
			return left >= right;
		default:
			throw new RuntimeError("fatal error");
		}
	}
	
	@Override
	public Boolean visit(EqualityExpression e, Void arg) {
		Object l;
		Object r;
		if(e.getLeft().getType() == Type.INT || e.getLeft().getType() == Type.FLOAT) {
			l = e.getLeft().accept(arithmeticInterpreter, null);
			r = e.getRight().accept(arithmeticInterpreter, null);
		} else if(e.getLeft().getType() == Type.STRING) {
			l = e.getLeft().accept(stringInterpreter, null);
			r = e.getRight().accept(stringInterpreter, null);
		} else {
			l = e.getLeft().accept(this, null);
			r = e.getRight().accept(this, null);
		}
		
		switch(e.getOperation()) {
		case EQ:
			return l.equals(r);
		case NEQ:
			return !r.equals(r);
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
		return (Boolean) runtime.retrieve(v);
	}
	
	@Override
	public Boolean visit(ArrayAccess a, Void arg) {
		return (Boolean) runtime.retrieve(a);
	}
	
	@Override
	public Boolean visit(BooleanLiteral b, Void arg) {
		return b.getValue();
	}
	
	@Override
	public Boolean visit(AssignExpression e, Void arg) {
		boolean res = e.getExpression().accept(this, null);
		runtime.set(e.getLvalue(), res);
		return res;
	}
	
	public void init(ArithmeticExpInterpreter ai, StringExpInterpreter si) {
		arithmeticInterpreter = ai;
		stringInterpreter = si;
	}
	
}
