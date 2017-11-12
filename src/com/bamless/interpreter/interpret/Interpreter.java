package com.bamless.interpreter.interpret;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Identifier;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.statement.ArrayDecl;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.PrintStatement;
import com.bamless.interpreter.ast.statement.ReturnStatement;
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
	private static final String MAIN_FUNC = "main";
	
	private ArithmeticExpInterpreter ai;
	private BooleanExpInterpreter bi;
	private StringExpInterpreter si;
	private ArrayExpInterpreter arri;
	
	private Map<String, FuncDecl> functions;
	
	private MemoryEnvironment memEnv;
	
	private boolean returning;
	
	public Interpreter() {
		this.memEnv = new MemoryEnvironment(this);
		
		this.ai = new ArithmeticExpInterpreter(this);
		this.bi = new BooleanExpInterpreter(this);
		this.si = new StringExpInterpreter(this);
		this.arri = new ArrayExpInterpreter(this);
	}
	
	@Override
	public void visit(Program p, Void arg) {
		functions = p.getFunctions();
		
		FuncCallExpression main = new FuncCallExpression(new Identifier(MAIN_FUNC));
		callFunction(main);
	}
	
	@Override
	public void visit(BlockStatement v, Void arg) {
		memEnv.enterScope();
		for(Statement s : v.getStmts()) {
			s.accept(this, null);
			if(returning) break;
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
			if(returning) break;
		}
	}
	
	@Override
	public void visit(ForStatement v, Void arg) {
		if(v.getInit() != null)
			v.getInit().accept(this, null);
		
		Expression cond = v.getCond();
		while(cond == null || cond.accept(bi, null)) {
			v.getBody().accept(this, null);
			if(returning) break;
			
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
	
	@Override
	public void visit(ReturnStatement r, Void arg) {
		if(r.getExpression() != null)
			memEnv.setReturnRegister(interpretExpression(r.getExpression()));
		returning = true;
	}
	
	/* ************************* */
	/*        Expressions        */
	/* ************************* */
	
	private Object interpretExpression(Expression e) {
		if(e.getType() == Type.BOOLEAN) {
			boolean b = e.accept(bi, null);
			return b;
		} else if(e.getType() == Type.STRING) {
			String s = e.accept(si, null);
			return s;
		} else if(e.getType() == Type.INT || e.getType() == Type.FLOAT) {
			BigDecimal n = e.accept(ai, null);
			
			if(e.getType() == Type.INT)
				return n.intValue();
			else
				return n.floatValue();
		} else if(e.getType().isArray())
			return e.accept(arri, null);

		
		throw new RuntimeError("Fatal error.");
	}
	
	@Override
	public void visit(AssignExpression e, Void arg) {
		interpretExpression(e);
	}
	
	@Override
	public void visit(PreIncrementOperation p, Void arg) {
		interpretExpression(p);
	}
	
	@Override
	public void visit(PostIncrementOperation p, Void arg) {
		interpretExpression(p);
	}
	
	@Override
	public void visit(FuncCallExpression f, Void arg) {
		callFunction(f);
	}
	
	public void callFunction(FuncCallExpression funcCall) {
		FuncDecl func = functions.get(funcCall.getFuncName().getVal());
		List<Expression> args = funcCall.getArgs();
		
		//compute function argument expressions
		Object[] computedArgs = new Object[args.size()];
		for(int i = 0; i < func.getFormalArgs().size(); i++) {
			computedArgs[i] = interpretExpression(args.get(i));
		}
		
		//push a new stack frame
		memEnv.pushStackFrame();
		memEnv.enterScope();
		
		//set arguments on the newly pushed stackframe
		for(int i = 0; i < func.getFormalArgs().size(); i++) {
			memEnv.define(func.getFormalArgs().get(i).getIdentifier(), computedArgs[i]);
		}
		
		//call the function
		func.getBody().accept(this, null);
		
		//clear the stack frames
		memEnv.exitScope();
		memEnv.popStackFrame();
		
		//return
		returning = false;
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
