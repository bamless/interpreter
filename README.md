# interpreter

## What is it?
This project contains a toy c-like programming language that i'm implementing while studying compiler theory. 
For now the language is implemented by an interpreter that runs directly on the parsed AST, ~but in the future i may compile it down to bytecode or x86 machine code~ - I since have moved to [another language](https://github.com/bamless/jstar) that implements a full featured bytecode virtual machine and compiler, so if you're intrested in compilation to bytecode you can take a look at that.

## Examples 
You can find some examples of the language in [src/main/resources](https://github.com/bamless/interpreter/tree/master/src/main/resources)

## Compiling to bytecode
The branch [bytecode](https://github.com/bamless/interpreter/tree/bytecode) contains a simple experimental compiler that outputs bytecode to be run by [this](https://github.com/bamless/virtual-machine) virtual machine.
The implementation is pretty minimal and won't be expanded further, as I moved to [another project](https://github.com/bamless/jstar), but feel free to take a peek in there

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
