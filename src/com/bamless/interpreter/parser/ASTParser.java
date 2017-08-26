package com.bamless.interpreter.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bamless.interpreter.Position;
import com.bamless.interpreter.lex.Lexer;
import com.bamless.interpreter.lex.Token;
import com.bamless.interpreter.parser.ast.ASTNode;
import com.bamless.interpreter.parser.ast.Identifier;
import com.bamless.interpreter.parser.ast.expression.Expression;
import com.bamless.interpreter.parser.ast.statements.AssignStatement;
import com.bamless.interpreter.parser.ast.statements.BlockStatement;
import com.bamless.interpreter.parser.ast.statements.IfStatement;
import com.bamless.interpreter.parser.ast.statements.Statement;
import com.bamless.interpreter.parser.ast.statements.VarDecl;
import com.bamless.interpreter.parser.ast.statements.WhileStatement;
import com.bamless.interpreter.parser.ast.type.Type;

public class ASTParser {
	private final static String LEX_FILE = "/lexical-spec.lex";
	
	private Lexer lex;
	
	public ASTParser() {
		lex = new Lexer(ClassLoader.class.getResourceAsStream(LEX_FILE), true);
	}
	
	public ASTNode parse(File f) throws FileNotFoundException, IOException {
		lex.tokenize(f);
		return parse();
	}
	
	public ASTNode parse(String src) {
		lex.tokenize(src);
		return parse();
	}
	
	private ASTNode parse() {
		ASTNode root = block();
		
		if(!lex.isFinished()) {
			throw new ParseException(String.format("Syntax error at %s: unexpected token \"%s\"", 
					lex.peek().getPosition(), lex.peek().getValue()));
		}
		
		return root;
	}
	
	private Statement statement() {
		switch(lex.peek().getType()) {
			case "INT":
			case "FLOAT":
			case "BOOLEAN":
				return varDecl();
			case "IDENTIFIER":
				return assignStmt();
			case "IF":
				return ifStmt();
			case "WHILE":
				return whileStmt();
			case "{":
				return block();
			default:
				error("expected statement but instead found \"%s\"", lex.peek().getValue());
				return null;
		}
	}
	
	private Statement block() {
		Position start = require("{").getPosition();
		
		List<Statement> statements = new ArrayList<>();
		while(!lex.peek().getType().equals("}")) {
			statements.add(statement());
		}
		
		require("}");
		
		return new BlockStatement(statements, start);
	}

	private Statement varDecl() {
		Token typeTok = lex.next();
		Token idTok = require("IDENTIFIER");
		require(";");
		
		Type t = Type.valueOf(typeTok.getType());
		Identifier id = new Identifier(idTok.getPosition(), idTok.getValue());
		
		return new VarDecl(typeTok.getPosition(), t, id);
	}

	private Statement assignStmt() {
		Token idTok = require("IDENTIFIER");

		require("=");
		
		//here expression, for now ignore
		Expression e = null;
		lex.next();
		
		require(";");
		
		Identifier id = new Identifier(idTok.getPosition(), idTok.getValue());
		return new AssignStatement(idTok.getPosition(), id, e);
	}
	
	private Statement ifStmt() {
		Position start = require("IF").getPosition();
		require("(");
		
		//here expression, for now ignore
		Expression e = null;
		lex.next();
		
		require(")");
		
		Statement thenBody = statement();
		Statement elseBody = null;
		
		if(lex.peek().getType().equals("ELSE")) {
			lex.next();
			elseBody = statement();
		}
		
		return new IfStatement(e, thenBody, elseBody, start);
	}
	
	private Statement whileStmt() {
		Position start = require("WHILE").getPosition();
		require("(");
		
		//here expression, for now ignore
		Expression cond = null;
		lex.next();
		
		require(")");
		
		Statement body = statement();
		return new WhileStatement(cond, body, start);
	}

	private Token require(String tokType) {
		Token next = lex.next();
		if(!next.getType().equals(tokType)) {
			error("expected \"%s\" but instead found \"%s\"", tokType, next.getValue());
		}
		return next;
	}
	
	private void error(String format, Object... args) {
		//lex.curr() could return null. In that case just omit the "near or at" part of the error
		String tokenErr = "";
		Position pos = new Position(0, 0);
		if(lex.curr() != null) {
			tokenErr = "near or at token \"" + lex.curr().getValue() + "\" ";
			pos = lex.curr().getPosition();
		}
		throw new ParseException(String.format("Syntax error " + tokenErr + pos + ": " + format, args));
	}

}
