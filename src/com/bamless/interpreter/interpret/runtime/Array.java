package com.bamless.interpreter.interpret.runtime;

import java.util.LinkedList;

public class Array implements Cloneable {
	private Object[] arr;
	
	public Array(int length) {
		if(length < 1)
			throw new IllegalArgumentException("An array must have at least size 1");
		arr = new Object[length];
	}
	
	public Array(LinkedList<Integer> dimensions) {
		arr = new Object[dimensions.poll()];
		for(int i = 0; i < arr.length; i++) {
			if(dimensions.size() > 1)
				arr[i] = new Array(dimensions);
			else
				arr[i] = new Array(dimensions.peekFirst());
		}
		dimensions.push(arr.length);
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
