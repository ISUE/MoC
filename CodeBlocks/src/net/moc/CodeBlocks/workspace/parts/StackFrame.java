package net.moc.CodeBlocks.workspace.parts;

import java.util.ArrayList;

import net.moc.CodeBlocks.workspace.Function;
import net.moc.CodeBlocks.workspace.command.Command;

public class StackFrame {
	public enum FunctionVariables {arg1, arg2, arg3, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, fvar, retvar}
	private Function function; public Function getFunction() { return function; }
	private ArrayList<Command> code;
	private ArrayList<Integer> variable;
	private String[] args = new String[3]; public void setArg(int i, String v) { if (i >= 0 && i < args.length) args[i] = v; } public String[] getArgs() { return args; }
	private String[] vars = new String[11]; public void setVar(int i, String v) { if (i >= 0 && i < vars.length) vars[i] = v; } public String[] getVars() { return vars; }
	private String retval = null; public void setRetval(String v) { retval = v; } public String getRetval() { return retval; }
	private int pc;
	
	public StackFrame(Function function) {
		this.function = function;
		code = function.getCode();
		variable = new ArrayList<Integer>();
		pc = 0;
		
	}

	public Command getCommand() {
		Command command = null;
		
		if (pc < code.size()) { command = code.get(pc); pc++; }
		
		return command;
		
	}
	
	public void setPc(int pc) { this.pc = pc; }
	public int getPc() { return this.pc; }
	public void incrementPc(int value) { this.pc += value; }
	
	public Integer look() {
		if (variable.isEmpty()) return null;
		return variable.get(variable.size() - 1);
	}
	
	public void decrement(int i) { variable.set(variable.size() - 1, look() - i); }
	public void push(Integer v) { variable.add(v); }
	public void pop() { if (!variable.isEmpty()) variable.remove(variable.size() - 1); }
	
}
