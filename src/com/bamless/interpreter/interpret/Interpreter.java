package com.bamless.interpreter.interpret;

import java.math.BigDecimal;
import java.util.LinkedList;
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
import com.bamless.interpreter.ast.statement.BreakStatement;
import com.bamless.interpreter.ast.statement.ContinueStatement;
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
import com.bamless.interpreter.interpret.expinterpreter.ArithmeticInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.ArrayInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.BooleanInterpreter;
import com.bamless.interpreter.interpret.expinterpreter.StringInterpreter;
import com.bamless.interpreter.interpret.memenvironment.Array;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment;
import com.bamless.interpreter.interpret.memenvironment.MemoryEnvironment.Frame;

/**
 * Interpret the language by directly interpreting the AST. This type of interpretation is slow,
 * but easy to implement in little time.
 * 
 * @author fabrizio
 *
 */
public class Interpreter  extends VoidVisitorAdapter<Frame> {
	public static final String MAIN_FUNC = "main";
	
	private ArithmeticInterpreter ai;
	private BooleanInterpreter bi;
	private StringInterpreter si;
	private ArrayInterpreter arri;
	
	private Map<String, FuncDecl> functions;
	
	private MemoryEnvironment memEnv;
	private boolean returning;
	
	public Interpreter() {
		this.memEnv = new MemoryEnvironment(this);
		
		this.ai = new ArithmeticInterpreter(this);
		this.bi = new BooleanInterpreter(this);
		this.si = new StringInterpreter(this);
		this.arri = new ArrayInterpreter(this);
	}
	
	@Override
	public void visit(Program p, Frame frame) {
		functions = p.getFunctions();
		
		FuncCallExpression main = new FuncCallExpression(new Identifier(MAIN_FUNC));
		callFunction(main);
	}
	
	@Override
	public void visit(BlockStatement v, Frame frame) {
		for(Statement s : v.getStmts()) {
			s.accept(this, frame);
			if(returning) break;
		}
	}
	
	@Override
	public void visit(IfStatement v, Frame frame) {
		if(v.getCondition().accept(bi, frame)) {
			v.getThenStmt().accept(this, frame);
		} else {
			if(v.getElseStmt() != null)
				v.getElseStmt().accept(this, frame);
		}
	}
	
	@Override
	public void visit(WhileStatement v, Frame frame) {
		try {
			while(v.getCondition().accept(bi, frame)) {
				try {
					v.getBody().accept(this, frame);
				} catch(ContinueException c) {
					//continue the cicle
				}
				if(returning) break;
			}
		} catch(BreakException b) {
			//break out of cicle
		}
	}
	
	@Override
	public void visit(BreakStatement b, Frame arg) {
		throw new BreakException();
	}
	
	@Override
	public void visit(ContinueStatement c, Frame arg) {
		throw new ContinueException();
	}
	
	@Override
	public void visit(ForStatement v, Frame frame) {
		if(v.getInit() != null)
			v.getInit().accept(this, frame);
		
		Expression cond = v.getCond();
		try {
			while(cond == null || cond.accept(bi, frame)) {
				try {
					v.getBody().accept(this, frame);
				} catch(ContinueException c) {
				}
	
				if(returning) break;
				
				if(v.getAct() != null)
					v.getAct().accept(this, frame);
			}
		} catch(BreakException b) {
		}
	}
	
	@Override
	public void visit(PrintStatement p, Frame frame) {
		if(p.isNweLine())
			System.out.println(interpretExpression(p.getExpression(), frame));
		else
			System.out.print(interpretExpression(p.getExpression(), frame));
	}

	@Override
	public void visit(VarDecl v, Frame frame) {
		//define if not present
		try {
			frame.define(v.getId(), null);
		} catch(IllegalArgumentException e) {
		}
		
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, frame);
	}
	
	@Override
	public void visit(ArrayDecl a, Frame frame) {
		LinkedList<Integer> computetDim = new LinkedList<>();
		for(Expression e : a.getDimensions()) {
			computetDim.add(e.accept(ai, null).intValue());
		}
		
		frame.define(a.getId(), new Array(computetDim, ((ArrayType) a.getType()).getInternalType()));
	}
	
	@Override
	public void visit(ReturnStatement r, Frame frame) {
		if(r.getExpression() != null)
			frame.setReturnRegister(interpretExpression(r.getExpression(), frame));
		returning = true;
	}
	
	/* ************************* */
	/*        Expressions        */
	/* ************************* */
	
	private Object interpretExpression(Expression e, Frame frame) {
		if(e.getType() == Type.INT || e.getType() == Type.FLOAT) {
			BigDecimal n = e.accept(ai, frame);
			
			if(e.getType() == Type.INT)
				return n.intValue();
			else
				return n.floatValue();
		} else if(e.getType() == Type.BOOLEAN)
			return e.accept(bi, frame);
		else if(e.getType() == Type.STRING)
			return e.accept(si, frame);
		else if(e.getType().isArray())
			return e.accept(arri, frame);

		throw new RuntimeError("Fatal error.");
	}
	
	@Override
	public void visit(AssignExpression e, Frame frame) {
		interpretExpression(e, frame);
	}
	
	@Override
	public void visit(PreIncrementOperation p, Frame frame) {
		interpretExpression(p, frame);
	}
	
	@Override
	public void visit(PostIncrementOperation p, Frame frame) {
		interpretExpression(p, frame);
	}
	
	@Override
	public void visit(FuncCallExpression f, Frame frame) {
		callFunction(f);
	}
	
	public void callFunction(FuncCallExpression funcCall) {
		FuncDecl func = functions.get(funcCall.getFuncName().getVal());
		Expression[] args = funcCall.getArgs();
		
		//compute function argument expressions
		Object[] computedArgs = new Object[args.length];
		for(int i = 0; i < func.getFormalArgs().length; i++) {
			computedArgs[i] = interpretExpression(args[i], memEnv.getCurrentFrame());
		}
		
		//push a new stack frame
		memEnv.pushStackFrame();
		
		//set arguments on the newly pushed stack frame
		for(int i = 0; i < func.getFormalArgs().length; i++) {
			memEnv.getCurrentFrame().define(func.getFormalArgs()[i].getIdentifier(), computedArgs[i]);
		}
		
		//call the function
		func.getBody().accept(this, memEnv.getCurrentFrame());
		Object ret = memEnv.getCurrentFrame().getReturnRegister();
		
		//clear the stack frames
		memEnv.popStackFrame();
		
		if(memEnv.getCurrentFrame() != null)
			memEnv.getCurrentFrame().setReturnRegister(ret);
		
		//return
		returning = false;
	}
	
	public ArithmeticInterpreter arithmeticInterpreter() {
		return ai;
	}

	public BooleanInterpreter boolInterpreter() {
		return bi;
	}

	public StringInterpreter stringInterpreter() {
		return si;
	}

	public ArrayInterpreter arrayInterpreter() {
		return arri;
	}

}
