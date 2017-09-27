package com.bamless.interpreter.interpret;

import java.util.LinkedList;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.RelationalExpression;
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
import com.bamless.interpreter.interpret.expinterpreter.ArithmeticExpInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.ArrayExpInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.BooleanExpInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.StringExpInterpreter;
import com.bamless.interpreter.interpret.memenvironment.Array;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment;

/**
 * Interpret the language by directly interpreting the AST. This type of interpretation is slow,
 * but easy to implement in little time.
 * 
 * @author fabrizio
 *
 */
public class Interpreter  extends VoidVisitorAdapter<Void> {
	private ArithmeticExpInterpreter ai;
	private BooleanExpInterpreter bi;
	private StringExpInterpreter si;
	private ArrayExpInterpreter arri;
	
	private MemoryEnvironment memEnv;
	
	public Interpreter() {
		this.memEnv = new MemoryEnvironment(this);
		
		this.ai = new ArithmeticExpInterpreter(this);
		this.bi = new BooleanExpInterpreter(this);
		this.si = new StringExpInterpreter(this);
		this.arri = new ArrayExpInterpreter(this);
	}
	
	@Override
	public void visit(Program p, Void arg) {
		p.getBlock().accept(this, arg);
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
		
		memEnv.define(a.getId(), new Array(computetDim, ((ArrayType) a.getType()).getInternalType()));
	}
	
	private void interpretExpression(Expression e) {
		if(e.getType() == Type.BOOLEAN)
			e.accept(bi, null);
		if(e.getType() == Type.STRING)
			e.accept(si, null);
		if(e.getType() == Type.INT || e.getType() == Type.FLOAT)
			e.accept(ai, null);
		if(e.getType().isArray())
			e.accept(arri, null);
	}

	@Override
	public void visit(AssignExpression e, Void arg) {
		interpretExpression(e);
	}
	
	@Override
	public void visit(ArithmeticBinExpression e, Void arg) {
		interpretExpression(e);
	}
	
	@Override
	public void visit(ArrayAccess a, Void arg) {
		interpretExpression(a);
	}
	
	@Override
	public void visit(PreIncrementOperation p, Void arg) {
		interpretExpression(p);
	}

	@Override
	public void visit(LogicalExpression l, Void arg) {
		interpretExpression(l);
	}

	@Override
	public void visit(RelationalExpression r, Void arg) {
		interpretExpression(r);
	}

	@Override
	public void visit(EqualityExpression e, Void arg) {
		interpretExpression(e);
	}

	@Override
	public void visit(LogicalNotExpression n, Void arg) {
		interpretExpression(n);
	}
	
	@Override
	public void visit(PostIncrementOperation p, Void arg) {
		interpretExpression(p);
	}
	
	public ArithmeticExpInterpreter getArithmeticExpInterpreter() {
		return ai;
	}

	public BooleanExpInterpreter getBoolExpInterpreter() {
		return bi;
	}

	public StringExpInterpreter getStringExpInterpreter() {
		return si;
	}

	public ArrayExpInterpreter getArrayExpInterpreter() {
		return arri;
	}

	public MemoryEnvironment getMemEnv() {
		return memEnv;
	}

}
