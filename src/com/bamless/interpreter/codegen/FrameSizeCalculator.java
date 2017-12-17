package com.bamless.interpreter.codegen;

import java.util.HashMap;
import java.util.Map;

import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class FrameSizeCalculator extends VisitorAdapter<Map<String, Integer>, Void> {
	private HashMap<String, Integer> funcFrameSize = new HashMap<>();
	
	private int localVar;
	private int maxLocal;

	@Override
	public Map<String, Integer> visit(Program p, Void arg) {
		for(String s : p.getFunctions().keySet()) {
			p.getFunctions().get(s).accept(this, arg);
		}
		
		return funcFrameSize;
	}
	
	@Override
	public Map<String, Integer> visit(FuncDecl d, Void arg) {
		localVar = 0;
		maxLocal = 0;
		
		localVar += d.getFormalArgs().length;
		
		d.getBody().accept(this, arg);
		
		funcFrameSize.put(d.getId().getVal(), maxLocal);
		
		return null;
	}
	
	@Override
	public Map<String, Integer> visit(BlockStatement v, Void arg) {
		int locSave = localVar;
		
		for(Statement s : v.getStmts()) {
			s.accept(this, arg);
		}
		
		maxLocal = Math.max(maxLocal, localVar);
		localVar = locSave;
		
		return null;
	}
	
	@Override
	public Map<String, Integer> visit(VarDecl v, Void arg) {
		localVar++;
		
		return null;
	}
}
