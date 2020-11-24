package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.BreakStatement;
import com.bamless.interpreter.ast.statement.ContinueStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;

public class LoopBreakingChecker extends VoidVisitorAdapter<Statement> {

    @Override
    public void visit(BlockStatement v, Statement arg) {
        for(int i = 0; i < v.getStmts().length; i++) {
            v.getStmts()[i].accept(this, arg);
            if(v.getStmts()[i] instanceof BreakStatement && i < v.getStmts().length - 1)
                ErrUtils.semanticError(v.getStmts()[i + 1].getPosition(), "Unreachable code");
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
            ErrUtils.semanticError(b.getPosition(),
                    "break statement can only be used inside a loop");
        }
    }

    @Override
    public void visit(ContinueStatement c, Statement arg) {
        if(arg == null) {
            ErrUtils.semanticError(c.getPosition(),
                    "continue statement can only be used inside a loop");
        }
    }

}
