package com.bamless.interpreter.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bamless.interpreter.ErrUtils;
import com.bamless.interpreter.visitor.GenericVisitor;
import com.bamless.interpreter.visitor.VoidVisitor;

public class Program extends ASTNode {
	private Map<String, FuncDecl> functions;
	
	public Program(Position start, Collection<FuncDecl> functions) {
		super(start);
		this.functions = new LinkedHashMap<>();
		for(FuncDecl d : functions) {
			if(this.functions.get(d.getId().getVal()) != null)
				ErrUtils.semanticError(d.getPosition(), "double declaration od function `%s`", d.getId());
			this.functions.put(d.getId().getVal(), d);
		}
	}

	@Override
	public <T, A> T accept(GenericVisitor<T, A> v, A arg) {
		return v.visit(this, arg);
	}

	@Override
	public <A> void accept(VoidVisitor<A> v, A arg) {
		v.visit(this, arg);
	}

	public Map<String, FuncDecl> getFunctions() {
		return Collections.unmodifiableMap(functions);
	}
	
}
