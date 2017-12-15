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
import com.bamless.interpreter.visitor.Visitable;
import com.bamless.interpreter.visitor.VoidVisitor;

public class BytecodeGenerator implements VoidVisitor<Void> {
	private List<Integer> bytecode;
	private int count;
	
	private int nextLocal;
	private int maxLocal;
	
	private Map<String, Integer> localVar;
	private Map<String, Integer> funcFrameSize;

	public BytecodeGenerator() {
		bytecode = new ArrayList<>();
		localVar = new HashMap<>();
		funcFrameSize = new HashMap<>();
	}

	@Override
	public void visit(Visitable v, Void arg) {
	}

	@Override
	public void visit(Program p, Void arg) {
		gen(CALL, 5, 0, 0);
		gen(HALT);
		
		for (String f : p.getFunctions().keySet())
			p.getFunctions().get(f).accept(this, arg);
		
		bytecode.set(3, funcFrameSize.get("main"));
	}

	@Override
	public void visit(IfStatement i, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(WhileStatement w, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ForStatement f, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(BlockStatement b, Void arg) {
		int nextLocSave = nextLocal;
		for(Statement s : b.getStmts())
			s.accept(this, arg);
		maxLocal = Math.max(maxLocal, nextLocal);
		nextLocal = nextLocSave;
	}

	@Override
	public void visit(PrintStatement p, Void arg) {
		p.getExpression().accept(this, arg);
		gen(PRINT);
	}

	@Override
	public void visit(ReturnStatement r, Void arg) {
		if(r.getExpression() != null)
			r.getExpression().accept(this, arg);
		gen(RET);
	}

	@Override
	public void visit(BreakStatement b, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ContinueStatement c, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarDecl v, Void arg) {
		localVar.put(v.getId().getVal(), nextLocal++);
		if(v.getInitializer() != null)
			v.getInitializer().accept(this, arg);
	}

	@Override
	public void visit(ArrayDecl a, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ArithmeticBinExpression e, Void arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
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
	public void visit(LogicalExpression l, Void arg) {
		l.getLeft().accept(this, arg);
		l.getRight().accept(this, arg);
		switch(l.getOperation()) {
		case AND:
			break;
		case OR:
			break;
		}
	}

	@Override
	public void visit(RelationalExpression r, Void arg) {
		r.getLeft().accept(this, arg);
		r.getRight().accept(this, arg);
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
	public void visit(EqualityExpression e, Void arg) {
		e.getLeft().accept(this, arg);
		e.getRight().accept(this, arg);
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
	public void visit(LogicalNotExpression n, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PostIncrementOperation p, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PreIncrementOperation p, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(AssignExpression e, Void arg) {
		e.getExpression().accept(this, arg);
		gen(STORE, localVar.get(((VarLiteral) e.getLvalue()).getId().getVal()));
	}

	@Override
	public void visit(CastExpression c, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(LengthFuncExpression l, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FloatLiteral f, Void arg) {
		
	}

	@Override
	public void visit(IntegerLiteral i, Void arg) {
		gen(CONST_I32, i.getValue());
	}

	@Override
	public void visit(BooleanLiteral b, Void arg) {
		gen(CONST_I32, b.getValue() ? 1 : 0);
	}

	@Override
	public void visit(StringLiteral s, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(VarLiteral v, Void arg) {
		gen(LOAD, localVar.get(v.getId().getVal()));
	}

	@Override
	public void visit(ArrayAccess a, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FuncCallExpression f, Void arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(FuncDecl d, Void arg) {
		localVar.clear();
		
		nextLocal = 0;
		maxLocal = 0;
		
		for(FormalArg a : d.getFormalArgs()) {
			localVar.put(a.getIdentifier().getVal(), nextLocal++);
			gen(GETARG, localVar.get(a.getIdentifier().getVal()));
			gen(STORE,  localVar.get(a.getIdentifier().getVal()));
		}
		
		d.getBody().accept(this, arg);
		funcFrameSize.put(d.getId().getVal(), maxLocal);
		
		gen(RET);
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
