package com.bamless.interpreter.interpret.memenvironment;

import java.util.LinkedList;

import com.bamless.interpreter.ast.type.ArrayType;
import com.bamless.interpreter.ast.type.Type;

public class Array implements Cloneable {
	private Object[] arr;
	
	public Array(LinkedList<Integer> dimensions, Type t) {
		arr = new Object[dimensions.poll()];
		
		if(dimensions.size() == 0)
			init(t);
		else {
			for(int i = 0; i < arr.length; i++)
					arr[i] = new Array(dimensions, Type.internalTypeOf((ArrayType) t));
		}
	}
	
	private void init(Type t) {
		for(int i = 0; i < arr.length; i++) {
			Object o = null;
			if(t == Type.BOOLEAN) o = false;
			if(t == Type.INT) o = 0;
			if(t == Type.FLOAT) o = 0.0f;
			arr[i] = o;
		}
	}
	
	public Object get(int i) {
		if(i < 0 || i > arr.length - 1)
			throw new ArrayIndexOutOfBoundsException(i);
		return arr[i];
	}
	
	public void set(int i, Object o) {
		if(i < 0 || i > arr.length - 1) 
			throw new ArrayIndexOutOfBoundsException(i);
		arr[i] = o;
	}
	
	public int getLength() {
		return arr.length;
	}
	
}
