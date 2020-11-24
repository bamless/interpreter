# interpreter

## What is it?
This project contains a toy c-like programming language that i'm implementing while studying compiler theory. 
For now the language is implemented by an interpreter that runs directly on the parsed AST, but in the future i may compile it down to bytecode or x86 machine code.

## How do i run it?
If you use an IDE, simply import the project as a `maven project`.

If not, manually compile it using the maven command:
```
mvn package
```
A jar file will be created in the `target` folder. Then, to run the interpreter, type in the terminal:
```
java -jar target/interpreter-1.0.jar src/main/resources/test.c+-
``` 
