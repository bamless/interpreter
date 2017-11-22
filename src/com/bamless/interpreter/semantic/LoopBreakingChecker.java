package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.BreakStatement;
import com.bamless.interpreter.ast.statement.ContinueStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.ast.visitor.VoidVisitorAdapter;

public class LoopBreakingChecker extends VoidVisitorAdapter<Statement> {

	@Override
	public void visit(BlockStatement v, Statement arg) {
		for(int i = 0; i < v.getStmts().size(); i++) {
			v.getStmts().get(i).accept(this, arg);
			if(v.getStmts().get(i) instanceof BreakStatement && i < v.getStmts().size() - 1)
				ErrUtils.semanticError(v.getStmts().get(i + 1).getPosition(), "Unreachable code");
		}
	}
	
	@Override
	public void visit(WhileStatement v, Statement arg) {
		v.getBody().accept(this, v);
	}

	@Override
	public void visit(ForStatement v, Statement arg) {
		v.getBody().accept(this, v);
	}

	@Override
	public void visit(BreakStatement b, Statement arg) {
		if(arg == null) {
			ErrUtils.semanticError(b.getPosition(), "Break statement can only be used inside a loop");
		}
	}

	@Override
	public void visit(ContinueStatement c, Statement arg) {
		if(arg == null) {
			ErrUtils.semanticError(c.getPosition(), "Break statement can only be used inside a loop");
		}
	}

}
