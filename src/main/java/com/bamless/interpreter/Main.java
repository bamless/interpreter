package com.bamless.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.bamless.interpreter.ast.Program;
import com.bamless.interpreter.lex.LexicalException;
import com.bamless.interpreter.parser.ParseException;
import com.bamless.interpreter.semantic.SemanticException;
import com.bamless.interpreter.semantic.TypeException;

public class Main {
	private static final String VALID_SRC_EXT = ".*\\.(cml|c\\+\\-)$";
	private static final String VALID_CC_EXT = ".*\\.(ccml|cc\\+\\-)$";

	/**
	 * Entry point for the CML interpreter.
	 * 
	 * If no argument is provided the interpreter begins reading the program from
	 * standard input.
	 * 
	 * If arguments are provided:
	 *
	 * If no option (or the `-c`) option is specified the interpreter runs (or
	 * respectively compile) the source file provided as the next argument. It is
	 * recommended to compile only medium to large source files.
	 * 
	 * If the option `-e` is specified the second argument is interpreted as source
	 * code instead of a path to a source file and gets executed.
	 * 
	 * Option `-e` and `-c` are mutually exclusive
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CML cml = new CML();
		Program p = null;

		try {
			if(args.length == 0) {
				p = cml.compile(System.in);
			} else if(args[0].equals("-e")) {
				if(args.length < 2) {
					System.err.println("No program provided");
					System.exit(1);
				}

				p = cml.compile(args[1]);
			} else if(args[0].matches(VALID_SRC_EXT)) {
				p = cml.compile(new File(args[0]));
			} else if(args[0].matches(VALID_CC_EXT)) {
				p = CML.deSerialize(args[0]);
			} else if(args[0].equals("-c")) {
				if(args.length < 2) {
					System.err.println("No input file provided");
					System.exit(1);
				}

				String filename = args[1];
				if(!filename.matches(VALID_SRC_EXT)) {
					System.err.println(
							"File format not recognized. File extension should be 'c+-' or 'cml'.");
					usage();
				}

				Program pgr = cml.compile(new File(filename));
				CML.serialize(pgr, filename.replaceAll(VALID_SRC_EXT.replace(".*", ""), ".ccml"));
			} else {
				System.err.println("File format not recognized. File extension should be "
						+ "'c+-' or 'cml' for source files, 'cc+-' or 'ccml' for compiled ones.");
				usage();
			}
		} catch (LexicalException | ParseException | SemanticException | TypeException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error while reading file: " + e.getMessage());
			System.exit(1);
		}

		if(p != null)
			cml.run(p);
	}

	private static void usage() {
		System.err.println("Usage: cml [-c|-e] filepath");
		System.exit(1);
	}

}
