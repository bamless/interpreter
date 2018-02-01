package com.bamless.interpreter.interpret;

import java.io.PrintStream;
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
import com.bamless.interpreter.ast.type.Type.TypeID;
import com.bamless.interpreter.interpret.expeval.ArithmeticEval;
import com.bamless.interpreter.interpret.expeval.ArrayEval;
import com.bamless.interpreter.interpret.expeval.BooleanEval;
import com.bamless.interpreter.interpret.expeval.StringEval;
import com.bamless.interpreter.interpret.memenv.Array;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;

/**
 * Interpret the language by directly interpreting the AST. This type of interpretation is slow,
 * but easy to implement in little time.
 * 
 * @author fabrizio
 *
 */
public class Interpreter  extends VoidVisitorAdapter<Frame> {
	public static final String MAIN_FUNC = "main";
	
	private ArithmeticEval arithEval;
	private BooleanEval boolEval;
	private StringEval strEval;
	private ArrayEval arrEval;
	
	private Map<String, FuncDecl> functions;
	private Map<String, Native<?>> natives;
	
	private PrintStream out = System.out;
	private MemoryEnvironment memEnv;
	private boolean returning;
	
	private Object mainReturn;
	
	public Interpreter(Map<String, Native<?>> natives) {
		this.memEnv = new MemoryEnvironment(this);
		
		this.arithEval = new ArithmeticEval(this);
		this.boolEval = new BooleanEval(this);
		this.strEval = new StringEval(this);
		this.arrEval = new ArrayEval(this);
		
		this.natives = natives;
	}
	
	@Override
	public void visit(Program p, Frame frame) {
		functions = p.getFunctions();
		
		memEnv.pushStackFrame();
		
		FuncCallExpression main = new FuncCallExpression(new Identifier(MAIN_FUNC));
		callFunction(main);
		
		mainReturn = memEnv.getCurrentFrame().getReturnRegister();
		memEnv.popStackFrame();
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
		if(v.getCondition().accept(boolEval, frame)) {
			v.getThenStmt().accept(this, frame);
		} else {
			if(v.getElseStmt() != null)
				v.getElseStmt().accept(this, frame);
		}
	}
	
	@Override
	public void visit(WhileStatement v, Frame frame) {
		try {
			while(v.getCondition().accept(boolEval, frame)) {
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
			while(cond == null || cond.accept(boolEval, frame)) {
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
			out.println(interpretExpression(p.getExpression(), frame));
		else
			out.print(interpretExpression(p.getExpression(), frame));
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
			computetDim.add(e.accept(arithEval, null).intValue());
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
		switch(e.getType().getId()) {
		case INT:
		case FLOAT:
			BigDecimal n = e.accept(arithEval, frame);
			
			if(e.getType().getId() == TypeID.INT)
				return n.intValue();
			else
				return n.floatValue();
		case BOOLEAN:
			return e.accept(boolEval, frame);
		case STRING:
			return e.accept(strEval, frame);
		case ARRAY:
			return e.accept(arrEval, frame);
		default:
			throw new RuntimeError("Fatal error.");
		}
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
		Expression[] args = funcCall.getArgs();
		
		//compute function argument expressions
		Object[] computedArgs = new Object[args.length];
		for(int i = 0; i < args.length; i++) {
			computedArgs[i] = interpretExpression(args[i], memEnv.getCurrentFrame());
		}
		
		if(funcCall.isNative()) {
			nativeCall(funcCall.getFuncName().getVal(), computedArgs);
		} else {
			call(funcCall.getFuncName().getVal(), computedArgs);
		}
	}
	
	private void call(String funcID, Object[] args) {
		//push a new stack frame
		memEnv.pushStackFrame();
		
		FuncDecl func = functions.get(funcID);
		
		//set arguments on the newly pushed stack frame
		for(int i = 0; i < func.getFormalArgs().length; i++) {
			memEnv.getCurrentFrame().define(func.getFormalArgs()[i].getIdentifier(), args[i]);
		}
		
		//call the function
		func.getBody().accept(this, memEnv.getCurrentFrame());
		Object ret = memEnv.getCurrentFrame().getReturnRegister();
		
		//clear the stack frames
		memEnv.popStackFrame();
		
		memEnv.getCurrentFrame().setReturnRegister(ret);
		
		//return
		returning = false;
	}
	
	private void nativeCall(String funcID, Object[] args) {
		Native<?> nativeCall = natives.get(funcID);
		
		Object ret = nativeCall.call(args);
		
		memEnv.getCurrentFrame().setReturnRegister(ret);
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public Object getMainReturn() {
		return mainReturn;
	}
	
	public ArithmeticEval arithmetic() {
		return arithEval;
	}

	public BooleanEval bool() {
		return boolEval;
	}

	public StringEval string() {
		return strEval;
	}

	public ArrayEval array() {
		return arrEval;
	}

}
