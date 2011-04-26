package gep.bool;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 布尔变量表达式工厂
 */
public class BVariableFactory implements FunctionFactory
{
	private char code;
	private BVariable variable;
	
	public BVariableFactory(char code)
	{
		this.code = code;
	}

	public char getCode()
	{
		return code;
	}

	public int getArity()
	{
		return BVariable.arity;
	}

	public Expression get(char code)
	{
		if (variable==null) variable = new BVariable(code);
		return variable;
	}
}
