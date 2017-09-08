package com.bamless.interpreter.interpret.runtime;

public class Array {
	private Object[] arr;
	
	public Array(int length) {
		if(length < 1)
			throw new IllegalArgumentException("An array must have at least size 1");
		arr = new Object[length];
	}
	
	public Object get(int i) {
		if(i < 1 || i > arr.length - 1) 
			throw new ArrayIndexOutOfBoundsException(i);
		return arr[i];
	}
	
	public void set(int i, Object o) {
		if(i < 1 || i > arr.length - 1) 
			throw new ArrayIndexOutOfBoundsException(i);
		arr[i] = o;
	}
	
	public int getLength() {
		return arr.length;
	}
	
}
