package com.bamless.interpreter.interpret;

import java.io.InputStream;
import java.io.PrintStream;
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
import com.bamless.interpreter.interpret.expeval.ArrayEval;
import com.bamless.interpreter.interpret.expeval.BooleanEval;
import com.bamless.interpreter.interpret.expeval.FloatEval;
import com.bamless.interpreter.interpret.expeval.IntEval;
import com.bamless.interpreter.interpret.expeval.StringEval;
import com.bamless.interpreter.interpret.memenv.CmlArr;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;

/**
 * Interpret the language by directly interpreting the AST. This type of
 * interpretation is slow, but easy to implement in little time.
 * 
 * @author fabrizio
 *
 */
public class Interpreter extends VoidVisitorAdapter<Frame> {
    public static final String MAIN_FUNC = "main";

    private IntEval intEval;
    private FloatEval floatEval;
    private BooleanEval boolEval;
    private StringEval strEval;
    private ArrayEval arrEval;

    private Map<String, FuncDecl> functions;
    private Map<String, Native<?>> natives;

    private PrintStream out = System.out;
    private InputStream in = System.in;
    private MemoryEnvironment memEnv;

    private Object mainReturn;

    public Interpreter(Map<String, Native<?>> natives) {
        this.memEnv = new MemoryEnvironment(this);

        this.intEval = new IntEval(this);
        this.floatEval = new FloatEval(this);
        this.boolEval = new BooleanEval(this);
        this.strEval = new StringEval(this);
        this.arrEval = new ArrayEval(this);

        this.natives = natives;
    }

    @Override
    public void visit(Program p, Frame frame) {
        functions = p.getFunctions();

        FuncCallExpression main = new FuncCallExpression(new Identifier(MAIN_FUNC));

        try {
            callFunction(main);
        } catch (Return r) {
            mainReturn = r.val;
        }

    }

    @Override
    public void visit(BlockStatement v, Frame frame) {
        for(Statement s : v.getStmts()) {
            s.accept(this, frame);
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
                } catch (Continue c) {
                    // continue the cicle
                }
            }
        } catch (Break b) {
            // break out of cicle
        }
    }

    @Override
    public void visit(BreakStatement b, Frame arg) {
        throw Break.instance();
    }

    @Override
    public void visit(ContinueStatement c, Frame arg) {
        throw Continue.instance();
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
                } catch (Continue c) {
                }

                if(v.getAct() != null)
                    v.getAct().accept(this, frame);
            }
        } catch (Break b) {
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
        frame.define(v.getId(), null);

        if(v.getInitializer() != null)
            v.getInitializer().accept(this, frame);
    }

    @Override
    public void visit(ArrayDecl a, Frame frame) {
        LinkedList<Integer> computetDim = new LinkedList<>();
        for(Expression e : a.getDimensions()) {
            computetDim.add(e.accept(intEval, frame));
        }

        frame.define(a.getId(),
                new CmlArr(computetDim, ((ArrayType) a.getType()).getInternalType()));
    }

    @Override
    public void visit(ReturnStatement r, Frame frame) {
        Object ret = null;
        if(r.getExpression() != null) {
            ret = interpretExpression(r.getExpression(), frame);
        }
        throw Return.instance(ret);
    }

    /* ************************* */
    /* Expressions */
    /* ************************* */

    private Object interpretExpression(Expression e, Frame frame) {
        switch(e.getType().getId()) {
        case INT:
            return e.accept(intEval, frame);
        case FLOAT:
            return e.accept(floatEval, frame);
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
        try {
            callFunction(f);
        } catch (Return r) {
        }
    }

    public void callFunction(FuncCallExpression funcCall) {
        Expression[] args = funcCall.getArgs();

        // compute function argument expressions
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
        // push a new stack frame
        memEnv.pushStackFrame();

        FuncDecl func = functions.get(funcID);
        // set arguments on the newly pushed stack frame
        for(int i = 0; i < func.getFormalArgs().length; i++) {
            memEnv.getCurrentFrame().define(func.getFormalArgs()[i].getIdentifier(), args[i]);
        }

        // call the function
        try {
            func.getBody().accept(this, memEnv.getCurrentFrame());
        } finally {
            // clear the stack frame
            memEnv.popStackFrame();
        }
    }

    private void nativeCall(String funcID, Object[] args) {
        Native<?> nativeCall = natives.get(funcID);
        throw Return.instance(nativeCall.call(this, args));
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public PrintStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public Object getMainReturn() {
        return mainReturn;
    }

    public IntEval integer() {
        return intEval;
    }

    public FloatEval floatingPoint() {
        return floatEval;
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
