package com.bamless.interpreter.semantic;

import java.util.Map;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.FormalArg;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.Expression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.PostIncrementOperation;
import com.bamless.interpreter.ast.expression.PreIncrementOperation;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.statement.ArrayDecl;
import com.bamless.interpreter.ast.statement.BlockStatement;
import com.bamless.interpreter.ast.statement.ForStatement;
import com.bamless.interpreter.ast.statement.IfStatement;
import com.bamless.interpreter.ast.statement.Statement;
import com.bamless.interpreter.ast.statement.VarDecl;
import com.bamless.interpreter.ast.statement.WhileStatement;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;

/**
 * AST walker that checks for various context sensitive constrains, such as
 * declaration of variables/functions before use, initialization of variables,
 * correct number of arguments passed to functions, etc...
 * 
 * @author fabrizio
 *
 */
public class SemanticChecker extends VoidVisitorAdapter<Void> {
    /**
     * Marker object used to denote the presence of an identifier in the 'varDecl'
     * symboltable
     */
    private static final Object DECL = new Object();
    /**
     * Symbol table to keep track of declared variables. The Object associated with
     * the identidfier is used as a marker.
     */
    private SymbolTable<Object> varDecl;
    /** Symbol table to keep track of initialized variables. */
    private SymbolTable<Boolean> init;

    private Map<String, FuncDecl> funcs;

    public SemanticChecker() {
        varDecl = new SymbolTable<>();
        init = new SymbolTable<>();
    }

    @Override
    public void visit(Program p, Void arg) {
        this.funcs = p.getFunctions();

        if(funcs.get(Interpreter.MAIN_FUNC) == null)
            ErrUtils.semanticError(p.getPosition(), "Could not find main function");

        for(String id : funcs.keySet()) {
            funcs.get(id).accept(this, arg);
        }
    }

    @Override
    public void visit(FuncDecl d, Void arg) {
        varDecl.enterScope();
        init.enterScope();

        for(FormalArg a : d.getFormalArgs()) {
            varDecl.define(a.getIdentifier().getVal(), DECL);
            init.define(a.getIdentifier().getVal(), true);
        }

        d.getBody().accept(this, arg);

        init.exitScope();
        varDecl.exitScope();
    }

    @Override
    public void visit(BlockStatement v, Void arg) {
        varDecl.enterScope();
        for(Statement s : v.getStmts()) {
            if(s instanceof Expression && !hasSideEffect((Expression) s)) {
                ErrUtils.semanticError(s.getPosition(), "Statement without effect.");
            }
            s.accept(this, null);
        }
        varDecl.exitScope();
    }

    @Override
    public void visit(ForStatement v, Void arg) {
        if(v.getInit() != null && (v.getInit() instanceof Expression
                && !hasSideEffect((Expression) v.getInit()))) {
            ErrUtils.warn("Warning %s: computed value is not used", v.getInit().getPosition());
        }
        if(v.getAct() != null && !hasSideEffect(v.getAct())) {
            ErrUtils.warn("Warning %s: computed value is not used", v.getAct().getPosition());
        }

        boolean forDecl = v.getInit() instanceof VarDecl;

        if(forDecl)
            varDecl.enterScope();
        if(forDecl)
            init.enterScope();

        if(v.getInit() != null)
            v.getInit().accept(this, arg);

        init.enterScope();

        if(v.getCond() != null)
            v.getCond().accept(this, arg);
        if(v.getAct() != null)
            v.getAct().accept(this, arg);

        v.getBody().accept(this, arg);

        init.exitScope();

        if(forDecl)
            init.exitScope();
        if(forDecl)
            varDecl.exitScope();
    }

    @Override
    public void visit(IfStatement v, Void arg) {
        v.getCondition().accept(this, arg);

        init.enterScope();
        v.getThenStmt().accept(this, arg);
        init.exitScope();

        if(v.getElseStmt() != null) {
            init.enterScope();
            v.getElseStmt().accept(this, arg);
            init.exitScope();
        }
    }

    @Override
    public void visit(WhileStatement v, Void arg) {
        v.getCondition().accept(this, arg);

        init.enterScope();
        v.getBody().accept(this, arg);
        init.exitScope();
    }

    @Override
    public void visit(VarDecl v, Void arg) {
        // we considered declaring a variable with the same name of another variable in
        // an outer scope an error
        if(varDecl.lookup(v.getId().getVal()) != null)
            ErrUtils.semanticError(v.getPosition(), "double declaration of variable %s",
                    v.getId().getVal());

        varDecl.define(v.getId().getVal(), DECL);
        init.define(v.getId().getVal(), false);

        if(v.getInitializer() != null)
            v.getInitializer().accept(this, arg);
    }

    @Override
    public void visit(ArrayDecl a, Void arg) {
        try {
            varDecl.define(a.getId().getVal(), DECL);
        } catch (IllegalArgumentException e) {
            ErrUtils.semanticError(a.getPosition(), "double declaration of variable %s",
                    a.getId().getVal());
        }
        // true because arrays get initialized automagically
        init.define(a.getId().getVal(), true);

        for(Expression e : a.getDimensions()) {
            e.accept(this, arg);
        }
    }

    @Override
    public void visit(AssignExpression e, Void arg) {
        if(!(e.getLvalue() instanceof Lvalue))
            ErrUtils.semanticError(e.getPosition(),
                    "left hand side of assignement must be an lvalue");

        if(e.getLvalue() instanceof VarLiteral) {
            VarLiteral v = (VarLiteral) e.getLvalue();

            if(varDecl.lookup(v.getId().getVal()) == null) {
                ErrUtils.semanticError(v.getId().getPosition(), "variable %s cannot be resolved",
                        v.getId().getVal());
            }

            e.getExpression().accept(this, arg);

            init.defineOrSet(v.getId().getVal(), true);

            e.getLvalue().accept(this, arg);
        } else {
            e.getLvalue().accept(this, arg);
            e.getExpression().accept(this, arg);
        }
    }

    @Override
    public void visit(PreIncrementOperation p, Void arg) {
        if(!(p.getExpression() instanceof Lvalue))
            ErrUtils.semanticError(p.getPosition(),
                    "left hand side of assignement must be an lvalue");

        p.getExpression().accept(this, arg);
    }

    @Override
    public void visit(PostIncrementOperation p, Void arg) {
        if(!(p.getExpression() instanceof Lvalue))
            ErrUtils.semanticError(p.getPosition(),
                    "left hand side of assignement must be an lvalue");

        p.getExpression().accept(this, arg);
    }

    @Override
    public void visit(VarLiteral v, Void arg) {
        if(varDecl.lookup(v.getId().getVal()) == null)
            ErrUtils.semanticError(v.getPosition(), "variable %s cannot be resolved",
                    v.getId().getVal());

        if(!init.lookup(v.getId().getVal()))
            ErrUtils.semanticError(v.getId().getPosition(),
                    "the local variable %s may not have been initialized", v.getId().getVal());
    }

    @Override
    public void visit(FuncCallExpression f, Void arg) {
        for(Expression e : f.getArgs())
            e.accept(this, arg);

        FuncDecl decl = funcs.get(f.getFuncName().getVal());

        if(decl == null && !f.isNative())
            ErrUtils.semanticError(f.getPosition(), "Use of undeclared function `%s`.",
                    f.getFuncName());
    }

    private boolean hasSideEffect(Expression e) {
        return (e instanceof AssignExpression || e instanceof PostIncrementOperation
                || e instanceof PreIncrementOperation || e instanceof FuncCallExpression);
    }
}
