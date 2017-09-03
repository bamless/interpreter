package com.bamless.interpreter.semantic.symboltable;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.bamless.interpreter.ast.type.Type;

public class SymbolTable {
	private Scope globalScope;
	private Scope currScope;
	
	public void enterScope() {
		if(currScope == null)
			currScope = globalScope;
		else
			currScope = currScope.nextNestedScope();
	}
	
	public void exitScope() {
		currScope = currScope.parent;
	}
	
	public boolean lookup(String id) {
		return retrieveSymbol(id) != null;
	}
	
	public boolean lookupScope(String id) {
		return currScope.symbols.containsKey(id);
	}
	
	public Symbol retrieveSymbol(String id) {
		Scope s = currScope;
		while(s != null) {
			if(currScope.symbols.containsKey(id))
				return currScope.symbols.get(id);
			
			s = currScope.parent;
		}
		return null;
	}
	
	public Symbol retrieveSymbolScope(String id) {
		return currScope.symbols.get(id);
	}
	
	public void addNestedScope() {
		if(currScope == null)
			globalScope = new Scope();
		else
			currScope.nestedScopes.add(new Scope(currScope));
	}
	
	public void define(String id, Type type) {
		currScope.symbols.put(id, new Symbol(type));
	}
	
	private class Scope {
		private Scope parent;
		
		private List<Scope> nestedScopes;
		private int nextNestedScope = 0;
		
		private Hashtable<String, Symbol> symbols;
		
		public Scope(Scope parent) {
			this.parent = parent;
			this.nestedScopes = new ArrayList<>();
			this.symbols = new Hashtable<>();
		}
		
		public Scope() {
			this(null);
		}
		
		public Scope nextNestedScope() {
			nextNestedScope %= nestedScopes.size();
			return nestedScopes.get(nextNestedScope++);
		}
		
	}

}
