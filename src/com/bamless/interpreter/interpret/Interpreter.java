package com.bamless.interpreter.interpret;

import java.util.LinkedList;

import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.statement.ArrayDecl;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.PrintStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;
import com.bamless.interpreter.interpret.memenvironment.Array;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment;

public class Interpreter  extends VoidVisitorAdapter<Void> {
	private ArithmeticExpInterpreter ai;
	private BooleanExpInterpreter bi;
	private StringExpInterpreter si;
	private ArrayExpInterpreter arri;
	
	private MemoryEnvironment memEnv;
	
	public Interpreter() {
		this.memEnv = new MemoryEnvironment();
		
		this.ai = new ArithmeticExpInterpreter(memEnv);
		this.bi = new BooleanExpInterpreter(memEnv);
		this.si = new StringExpInterpreter(memEnv);
		this.arri = new ArrayExpInterpreter(memEnv);
		
		bi.init(ai, si);
		si.init(ai, bi);
	}
	
	@Override
	public void visit(BlockStatement v, Void arg) {
		memEnv.enterScope();
		for(Statement s : v.getStmts()) {
			s.accept(this, null);
		}
		memEnv.exitScope();
	}
	
	@Override
	public void visit(IfStatement v, Void arg) {
		if(v.getCondition().accept(bi, null)) {
			v.getThenStmt().accept(this, null);
		} else {
			if(v.getElseStmt() != null)
				v.getElseStmt().accept(this, null);
		}
	}
	
	@Override
	public void visit(WhileStatement v, Void arg) {
		while(v.getCondition().accept(bi, null)) {
			v.getBody().accept(this, null);
		}
	}
	
	@Override
	public void visit(ForStatement v, Void arg) {
		if(v.getInit() != null)
			v.getInit().accept(this, null);
		
		Expression cond = v.getCond();
		while(cond == null || cond.accept(bi, null)) {
			v.getBody().accept(this, null);
			
			if(v.getAct() != null)
				v.getAct().accept(this, null);
		}
	}
	
	@Override
	public void visit(PrintStatement p, Void arg) {
		System.out.print(p.getExpression().accept(si, null));
	}

	@Override
	public void visit(VarDecl v, Void arg) {
		memEnv.define(v.getId(), null);
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, null);
	}
	
	@Override
	public void visit(ArrayDecl a, Void arg) {
		LinkedList<Integer> computetDim = new LinkedList<>();
		for(Expression e : a.getDimensions()) {
			computetDim.add(e.accept(ai, null).intValue());
		}
		
		memEnv.define(a.getId(), new Array(computetDim, Type.internalTypeOf((ArrayType) a.getType())));
	}
	
	@Override
	public void visit(AssignExpression e, Void arg) {
		if(e.getType() == Type.BOOLEAN)
			e.accept(bi, null);
		if(e.getType() == Type.STRING)
			e.accept(si, null);
		if(e.getType() == Type.INT || e.getType() == Type.FLOAT)
			e.accept(ai, null);
		if(e.getType().isArray())
			e.accept(arri, null);
	}
	
}
