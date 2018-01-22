package com.bamless.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.Program;

public class Main {
	private static String VALID_SRC_EXT = ".*\\.(cml|c\\+\\-)$";
	private static String VALID_CC_EXT  = ".*\\.(ccml|cc\\+\\-)$";
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("No input file provided");
			usage();
		}
		
		CML cml = new CML();
		
		try {
			if(args[0].equals("-c")) {
				if(args.length < 2) {
					System.err.println("No input file provided");
					System.exit(1);
				}
				
				String filename = args[1];
				if(!filename.matches(VALID_SRC_EXT)) {
					System.err.println("File format not recognized. File extension should be 'c+-' or 'cml'.");
					usage();
				}
	
				Program p = cml.compile(new File(filename));
				CML.serialize(p, filename.replaceAll(VALID_SRC_EXT.replace(".*", ""), ".ccml"));
			} else if(args[0].matches(VALID_SRC_EXT)) {
				Program p = cml.compile(new File(args[0]));
				Object ret = cml.run(p);
				System.out.println("\nmain returned: " + ret);
			} else if(args[0].matches(VALID_CC_EXT)) {
				Program p = CML.deSerialize(args[0]);
				Object ret = cml.run(p);
				System.out.println("\nmain returned: " + ret);
			} else {
				System.err.println("File format not recognized. File extension should "
						+ "be 'c+-' or 'cml' for source files, 'cc+-' or 'ccml' for compiled ones.");
				usage();
			}
		} catch(FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Error while reading file: " + e.getMessage());
			System.exit(1);
		}
		
	}
	
	private static void usage() {
		System.err.println("Usage: cml [-c] filepath");
		System.exit(1);
	}
	
}
