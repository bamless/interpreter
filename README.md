# interpreter

## What is it?
This project contains a toy c-like programming language that i'm implementing while studying compiler theory.

This branch contains a bytecode compiler that compiles bytecode for (this)[https://github.com/bamless/virtual-machine] virtual machine. The VM is still experimentals and for now doesn't support strings or any type of dynamic allocated object as it lacks a working garbage collector.
The VM for now supports ints, floats, booleans function calls and printing of the previous types.

## How do i run it?
If you use an IDE import the code as 'ant' project, if not simply run `ant run-interpreter` in the project's root folder. Obviously you'll need [apache ant](http://ant.apache.org/) for the latter approach.
