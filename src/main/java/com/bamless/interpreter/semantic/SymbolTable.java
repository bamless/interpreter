package com.bamless.interpreter.semantic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable<T> {
    private Deque<HashMap<String, T>> scopes;

    public SymbolTable() {
        this.scopes = new ArrayDeque<>();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public void define(String id, T val) {
        if(scopes.peek().containsKey(id))
            throw new IllegalArgumentException(
                    "Symbol " + id + " id already defined in this scope");
        scopes.peek().put(id, val);
    }

    public void set(String id, T val) {
        Iterator<HashMap<String, T>> i = scopes.iterator();
        while(i.hasNext()) {
            HashMap<String, T> symTable = i.next();
            if(symTable.containsKey(id)) {
                symTable.put(id, val);
                return;
            }
        }
        throw new IllegalArgumentException("Symbol " + id + " does not exist in any scope");
    }

    public void defineOrSet(String id, T val) {
        if(scopes.peek().containsKey(id))
            scopes.peek().put(id, val);
        scopes.peek().put(id, val);
    }

    public T lookup(String id) {
        Iterator<HashMap<String, T>> i = scopes.iterator();
        while(i.hasNext()) {
            HashMap<String, T> symTable = i.next();
            if(symTable.containsKey(id))
                return symTable.get(id);
        }
        return null;
    }

    public T probe(String id) {
        return scopes.peek().get(id);
    }
}
