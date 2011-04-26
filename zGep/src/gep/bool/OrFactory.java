package gep.bool;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 或运算表达式工厂
 */
public class OrFactory implements FunctionFactory
{

	public char getCode()
	{
		return Or.code;
	}

	public int getArity()
	{
		return Or.arity;
	}

	public Expression get(char code)
	{
		return new Or();
	}
}
