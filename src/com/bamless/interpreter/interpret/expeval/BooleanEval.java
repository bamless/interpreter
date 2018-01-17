package com.bamless.interpreter.interpret.expeval;

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
import com.bamless.interpreter.ast.type.Type.TypeID;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class BooleanEval extends VisitorAdapter<Boolean, Frame> {
	private Interpreter interpreter;
	
	public BooleanEval(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public Boolean visit(RelationalExpression r, Frame frame) {
		BigDecimal left  = r.getLeft().accept(interpreter.arithmetic(), frame);
		BigDecimal right = r.getRight().accept(interpreter.arithmetic(), frame);
		
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
		
		TypeID ltype = e.getLeft().getType().getId();
		
		switch(ltype) {
		case INT:
		case FLOAT:
			BigDecimal bl = e.getLeft().accept(interpreter.arithmetic(), frame);
			BigDecimal br = e.getRight().accept(interpreter.arithmetic(), frame);
			
			switch(e.getOperation()) {
			case EQ:
				return bl.compareTo(br) == 0;
			case NEQ:
				return bl.compareTo(br) != 0;
			}
		case STRING:
			l = e.getLeft().accept(interpreter.string(), frame);
			r = e.getRight().accept(interpreter.string(), frame);
			break;
		case ARRAY:
			l = e.getLeft().accept(interpreter.array(), frame);
			r = e.getRight().accept(interpreter.array(), frame);
			break;
		case BOOLEAN:
			l = e.getLeft().accept(this, frame);
			r = e.getRight().accept(this, frame);
			break;
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
