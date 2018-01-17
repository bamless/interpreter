package com.bamless.interpreter.semantic;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.ast.FuncDecl;
import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.ast.expression.FuncCallExpression;
import com.bamless.interpreter.natives.Native;
import com.bamless.interpreter.visitor.VoidVisitorAdapter;
import java.util.Map;

public class NativeCallsChecker extends VoidVisitorAdapter<Void> {
	private Map<String, Native<?>> natives;

	public NativeCallsChecker(Map<String, Native<?>> natives) {
		this.natives = natives;
	}

	@Override
	public void visit(Program p, Void arg) {
		for(String s : p.getFunctions().keySet()) {
			FuncDecl f = p.getFunctions().get(s);
			if(natives.containsKey(s)) {
				ErrUtils.semanticError(f.getPosition(), 
						"Double declaration of native function %s", s);
			}
			f.accept(this, null);
		}
	}

	@Override
	public void visit(FuncCallExpression f, Void arg) {
		if(natives.containsKey(f.getFuncName().getVal()))
			f.setNative(true);
	}
	
}
