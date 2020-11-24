package com.bamless.interpreter;

import com.bamless.interpreter.ast.Position;
import com.bamless.interpreter.semantic.SemanticException;

public class ErrUtils {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_YELLOW = "\u001B[33m";

	private ErrUtils() {
	}

	public static void warn(String warn, Object... args) {
		System.out.println(ANSI_YELLOW + String.format(warn, args) + ANSI_RESET);
	}

	public static void semanticError(Position pos, String format, Object... args) {
		throw new SemanticException(
				String.format("Semantic error at " + pos + ": " + format, args));
	}

}
