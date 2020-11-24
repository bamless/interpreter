package com.bamless.interpreter.interpret.expeval;

import com.bamless.interpreter.ast.expression.ArithmeticBinExpression;
import com.bamless.interpreter.ast.expression.ArithmeticBinExpression.ArithmeticBinOperation;
import com.bamless.interpreter.ast.expression.ArrayAccess;
import com.bamless.interpreter.ast.expression.AssignExpression;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.ast.expression.Lvalue;
import com.bamless.interpreter.ast.expression.StringLiteral;
import com.bamless.interpreter.ast.expression.VarLiteral;
import com.bamless.interpreter.ast.type.Type.TypeID;
import com.bamless.interpreter.interpret.Interpreter;
import com.bamless.interpreter.interpret.Return;
import com.bamless.interpreter.interpret.RuntimeError;
import com.bamless.interpreter.interpret.memenv.MemoryEnvironment.Frame;
import com.bamless.interpreter.visitor.VisitorAdapter;

public class StringEval extends VisitorAdapter<String, Frame> {
    private Interpreter interpreter;

    public StringEval(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public String visit(ArithmeticBinExpression e, Frame frame) {
        if(e.getOperation() != ArithmeticBinOperation.PLUS)
            throw new RuntimeError("fatal error");

        TypeID leftType = e.getLeft().getType().getId();
        TypeID rightType = e.getRight().getType().getId();

        if(leftType == TypeID.FLOAT) {
            String l = e.getLeft().accept(interpreter.floatingPoint(), frame).toString();
            String r = e.getRight().accept(this, frame);
            return l + r;
        } else if(rightType == TypeID.FLOAT) {
            String r = e.getRight().accept(interpreter.floatingPoint(), frame).toString();
            String l = e.getLeft().accept(this, frame);
            return l + r;
        } else if(leftType == TypeID.INT) {
            String l = e.getLeft().accept(interpreter.integer(), frame).toString();
            String r = e.getRight().accept(this, frame);
            return l + r;
        } else if(rightType == TypeID.INT) {
            String r = e.getRight().accept(interpreter.integer(), frame).toString();
            String l = e.getLeft().accept(this, frame);
            return l + r;
        } else if(leftType == TypeID.BOOLEAN) {
            String l = e.getLeft().accept(interpreter.bool(), frame).toString();
            String r = e.getRight().accept(this, frame);
            return l + r;
        } else if(rightType == TypeID.BOOLEAN) {
            String l = e.getLeft().accept(this, frame);
            String r = e.getRight().accept(interpreter.bool(), frame).toString();
            return l + r;
        }

        return e.getLeft().accept(this, frame) + e.getRight().accept(this, frame);
    }

    @Override
    public String visit(VarLiteral v, Frame frame) {
        return (String) frame.retrieve(v);
    }

    @Override
    public String visit(ArrayAccess a, Frame frame) {
        return (String) frame.retrieve(a);
    }

    @Override
    public String visit(StringLiteral s, Frame frame) {
        return s.getValue();
    }

    @Override
    public String visit(AssignExpression e, Frame frame) {
        String res = e.getExpression().accept(this, frame);
        frame.set((Lvalue) e.getLvalue(), res);
        return res;
    }

    @Override
    public String visit(FuncCallExpression f, Frame frame) {
        try {
            interpreter.callFunction(f);
        } catch (Return r) {
            return (String) r.val;
        }
        throw new RuntimeError(
                "Fatal error, function " + f + " declares return type but doesn't return");
    }

}
