package com.bamless.interpreter.codegen;

import static com.bamless.interpreter.codegen.Opcode.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bamless.interpreter.ast.FormalArg;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.BooleanLiteral;
import com.bamless.interpreter.ast.expression.CastExpression;
import com.bamless.interpreter.ast.expression.EqualityExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.FloatLiteral;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.IntegerLiteral;
import com.bamless.interpreter.ast.expression.LengthFuncExpression;
import com.bamless.interpreter.ast.expression.LogicalExpression;
import com.bamless.interpreter.ast.expression.LogicalNotExpression;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.RelationalExpression;
import com.bamless.interpreter.ast.expression.StringLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
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
import com.bamless.interpreter.ast.type.Type;
import com.bamless.interpreter.visitor.Visitable;
import com.bamless.interpreter.visitor.VoidVisitor;

public class BytecodeGenerator implements VoidVisitor<Boolean> {
	private static final String MAIN_FUNC = "main";
	
	private List<Integer> bytecode;
	private int count;
	
	private Map<String, Integer> funcFrameSize;
	
	private int nextLocal;
	private Map<String, Integer> localVar;
	
	private Map<Integer, String> unresolvedFuncCalls;
	private Map<String, Integer> funcAddr;
	
	public BytecodeGenerator(Map<String, Integer> funcFrameSize) {
		this.bytecode = new ArrayList<>();
		this.unresolvedFuncCalls = new HashMap<>();
		this.funcAddr = new HashMap<>();
		this.localVar = new HashMap<>();
		this.funcFrameSize = funcFrameSize;
	}

	@Override
	public void visit(Visitable v, Boolean statement) {
	}

	@Override
	public void visit(Program p, Boolean statement) {
		gen(CALL, 5, 0, funcFrameSize.get(MAIN_FUNC));
		gen(HALT);
		
		p.getFunctions().get(MAIN_FUNC).accept(this, statement);
		
		for(String f : p.getFunctions().keySet()) {
			if(f.equals(MAIN_FUNC)) continue;
			p.getFunctions().get(f).accept(this, statement);
		}
		
		for(int a : unresolvedFuncCalls.keySet()) {
			String funcName = unresolvedFuncCalls.get(a);
			bytecode.set(a, funcAddr.get(funcName));
		}
	}

	@Override
	public void visit(IfStatement i, Boolean statement) {
		i.getCondition().accept(this, statement);
		gen(JMPF, 0);
		int elseJmpAddr = count - 1;
		
		i.getThenStmt().accept(this, true);
		
		int endJmpAddr = 0;
		if(i.getElseStmt() != null) {
			gen(JMP, 0);
			endJmpAddr = count - 1;
		}
		
		bytecode.set(elseJmpAddr, count);
		
		if(i.getElseStmt() != null) {
			i.getElseStmt().accept(this, true);
			bytecode.set(endJmpAddr, count);
		}
	}

	@Override
	public void visit(WhileStatement w, Boolean statement) {
		int whileStart = count;
		w.getCondition().accept(this, false);
		
		// gen jump to while end with dummy address and save 
		// the address position
		gen(JMPF, 0);
		int whileEndAddr = count - 1;
		
		w.getBody().accept(this, true);
		
		gen(JMP, whileStart);
		
		// now we know the address of the while end, we can
		// replace the dummy address
		bytecode.set(whileEndAddr, count);
	}

	@Override
	public void visit(ForStatement f, Boolean statement) {
		if(f.getInit() != null)
			f.getInit().accept(this, true);
		
		int forStart = count;
		
		if(f.getCond() != null) {
			f.getCond().accept(this, false);
			gen(JMPF, 0);
		}
		int forEndAddr = count - 1;
		
		f.getBody().accept(this, true);
		
		if(f.getAct() != null)
			f.getAct().accept(this, true);
		
		gen(JMP, forStart);
		
		if(f.getCond() != null)
			bytecode.set(forEndAddr, count);
	}

	@Override
	public void visit(BlockStatement b, Boolean statement) {
		for(Statement s : b.getStmts()) {
			s.accept(this, true);
		}
	}

	@Override
	public void visit(PrintStatement p, Boolean statement) {
		p.getExpression().accept(this, false);
		
		if(p.getExpression().getType() == Type.INT)
			gen(PRINT);
		else
			gen(PRINT_FP);
	}

	@Override
	public void visit(ReturnStatement r, Boolean statement) {
		if(r.getExpression() != null)
			r.getExpression().accept(this, false);
		gen(RET);
	}

	@Override
	public void visit(BreakStatement b, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ContinueStatement c, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarDecl v, Boolean statement) {
		localVar.put(v.getId().getVal(), nextLocal++);
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, true);
	}

	@Override
	public void visit(ArrayDecl a, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArithmeticBinExpression e, Boolean statement) {
		e.getLeft().accept(this, false);
		e.getRight().accept(this, false);
		switch(e.getOperation()) {
		case DIV:
			gen(DIV_I32);
			break;
		case MINUS:
			gen(SUB_I32);
			break;
		case MOD:
			gen(MOD_I32);
			break;
		case MULT:
			gen(MUL_I32);
			break;
		case PLUS:
			gen(ADD_I32);
			break;
		}
	}

	@Override
	public void visit(LogicalExpression l, Boolean statement) {
		l.getLeft().accept(this, false);
		l.getRight().accept(this, false);
		switch(l.getOperation()) {
		case AND:
			break;
		case OR:
			break;
		}
	}

	@Override
	public void visit(RelationalExpression r, Boolean statement) {
		r.getLeft().accept(this, false);
		r.getRight().accept(this, false);
		switch(r.getOperation()) {
		case GE:
			gen(GE_I32);
			break;
		case GT:
			gen(GT_I32);
			break;
		case LE:
			gen(LE_I32);
			break;
		case LT:
			gen(LT_I32);
			break;
		}
	}

	@Override
	public void visit(EqualityExpression e, Boolean statement) {
		e.getLeft().accept(this, false);
		e.getRight().accept(this, false);
		switch(e.getOperation()) {
		case EQ:
			gen(EQ_I32);
			break;
		case NEQ:
			gen(NEQ_I32);
			break;
		}
	}

	@Override
	public void visit(LogicalNotExpression n, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PostIncrementOperation p, Boolean statement) {
		String var = ((VarLiteral) p.getExpression()).getId().getVal();
		gen(LOAD, localVar.get(var));
		if(!statement) gen(DUP);
		gen(CONST_I32, 1);
		
		switch(p.getOperator()) {
		case DECR:
			gen(SUB_I32);
			break;
		case INCR:
			gen(ADD_I32);
			break;
		}
		
		gen(STORE, localVar.get(var));
	}

	@Override
	public void visit(PreIncrementOperation p, Boolean statement) {
		String var = ((VarLiteral) p.getExpression()).getId().getVal();
		gen(LOAD, localVar.get(var));
		gen(CONST_I32, 1);
		
		switch(p.getOperator()) {
		case DECR:
			gen(SUB_I32);
			break;
		case INCR:
			gen(ADD_I32);
			break;
		}
		if(!statement) gen(DUP);
		gen(STORE, localVar.get(var));
	}

	@Override
	public void visit(AssignExpression e, Boolean statement) {
		e.getExpression().accept(this, false);
		if(!statement) gen(DUP);
		gen(STORE, localVar.get(((VarLiteral) e.getLvalue()).getId().getVal()));
	}

	@Override
	public void visit(CastExpression c, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LengthFuncExpression l, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FloatLiteral f, Boolean statement) {
		gen(CONST_F32, Float.floatToRawIntBits(f.getValue()));
	}

	@Override
	public void visit(IntegerLiteral i, Boolean statement) {
		gen(CONST_I32, i.getValue());
	}

	@Override
	public void visit(BooleanLiteral b, Boolean statement) {
		gen(CONST_I32, b.getValue() ? 1 : 0);
	}

	@Override
	public void visit(StringLiteral s, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarLiteral v, Boolean statement) {
		gen(LOAD, localVar.get(v.getId().getVal()));
	}

	@Override
	public void visit(ArrayAccess a, Boolean statement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FuncCallExpression f, Boolean statement) {
		for(Expression e : f.getArgs())
			e.accept(this, false);

		gen(CALL);
		
		Integer funcAddress = 0;
		if((funcAddress = funcAddr.get(f.getFuncName().getVal())) == null)
			unresolvedFuncCalls.put(count, f.getFuncName().getVal());
			
		gen(funcAddress == null ? 0 : funcAddress, f.getArgs().length, 
				funcFrameSize.get(f.getFuncName().getVal()));
	}

	@Override
	public void visit(FuncDecl d, Boolean statement) {
		localVar.clear();
		nextLocal = 0;
		
		funcAddr.put(d.getId().getVal(), count);
		
		for(FormalArg a : d.getFormalArgs()) {
			localVar.put(a.getIdentifier().getVal(), nextLocal++);
			gen(GETARG, localVar.get(a.getIdentifier().getVal()));
			gen(STORE,  localVar.get(a.getIdentifier().getVal()));
		}
		
		d.getBody().accept(this, statement);
		
		if(bytecode.get(count - 1) != RET) gen(RET);
	}
	
	public int[] getBytecode() {
		return bytecode.stream().mapToInt(i->i).toArray();
	}
	
	private void gen(int... code) {
		for(int b : code) {
			bytecode.add(b);
			count++;
		}
	}

}
