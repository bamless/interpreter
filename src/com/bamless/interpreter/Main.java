package com.bamless.interpreter;

import com.bamless.interpreter.ast.Program;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		CML interpreter = new CML();
		Program p = interpreter.compile(Main.class.getResourceAsStream("/test.c+-"));
		Object ret = interpreter.run(p);
		
		System.out.println("\nDone executing.");
		System.out.println("Main returned: " + ret);
	}
	
	
}
