package com.bamless.interpreter.interpret;

import java.math.BigDecimal;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.StringLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VisitorAdapter;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment;

public class StringExpInterpreter extends VisitorAdapter<String, Void> {
	private ArithmeticExpInterpreter arithmeticInterpreter;
	private BooleanExpInterpreter booleanInterpreter;
	
	private MemoryEnvironment memEnv;
	
	public StringExpInterpreter(MemoryEnvironment memEnv) {
		this.memEnv = memEnv;
	}
	
	@Override
	public String visit(ArithmeticBinExpression e, Void arg) {
		if(e.getOperation() != ArithmeticBinOperation.PLUS)
			throw new RuntimeError("fatal error");
		
		Type leftType = e.getLeft().getType();
		Type rightType = e.getRight().getType();
		
		if(leftType == Type.FLOAT || leftType == Type.INT) {
			BigDecimal res = e.getLeft().accept(arithmeticInterpreter, null);
			
			String l = leftType == Type.FLOAT ? res.floatValue() + "" : res.intValue() + "";
			String r = e.getRight().accept(this, null);
			
			return l + r;
		} else if(rightType == Type.FLOAT || rightType == Type.INT) {
			BigDecimal res = e.getRight().accept(arithmeticInterpreter, null);
			
			String l = e.getLeft().accept(this, null);
			String r = rightType == Type.FLOAT ? res.floatValue() + "" : res.intValue() + "";
			
			return l + r;
		}
		
		if(leftType == Type.BOOLEAN) {
			String l = e.getLeft().accept(booleanInterpreter, null).toString();
			String r = e.getRight().accept(this, null);
			
			return l + r;
		} else if(rightType == Type.BOOLEAN) {
			String l = e.getLeft().accept(this, null);
			String r = e.getRight().accept(booleanInterpreter, null).toString();
			
			return l + r;
		}
		
		return e.getLeft().accept(this, null) + e.getRight().accept(this, null);
	}
	
	@Override
	public String visit(VarLiteral v, Void arg) {
		return (String) memEnv.retrieve(v);
	}
	
	@Override
	public String visit(ArrayAccess a, Void arg) {
		return (String) memEnv.retrieve(a);
	}
	
	@Override
	public String visit(StringLiteral s, Void arg) {
		return s.getValue();
	}
	
	@Override
	public String visit(AssignExpression e, Void arg) {
		String res = e.getExpression().accept(this, null);
		memEnv.set((Lvalue) e.getLvalue(), res);
		return res;
	}
	
	public void init(ArithmeticExpInterpreter ai, BooleanExpInterpreter bi) {
		this.arithmeticInterpreter = ai;
		this.booleanInterpreter = bi;
	}
}
