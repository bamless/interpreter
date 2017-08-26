package com.bamless.interpreter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import com.bamless.interpreter.parser.ASTParser;
import com.bamless.interpreter.parser.ast.ASTNode;
import com.bamless.interpreter.parser.ast.visitor.PrinterVisitor;

public class Main {
	private final static String LEX_FILE = "/lexical-spec.lex";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	System.out.println(url.getFile());
        }
		
		ASTParser p = new ASTParser();
		//trick program to show else binding. The x at the end will be 0.
		ASTNode root = p.parse(ClassLoader.class.getResourceAsStream("/test.lang"));
		
		PrinterVisitor v = new PrinterVisitor(4);
		root.accept(v, 0);
	}
	
}
