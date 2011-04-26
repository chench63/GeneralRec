package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 加法表达式工厂
 */
public class PlusFactory implements FunctionFactory
{

	public char getCode()
	{
		return Plus.code;
	}

	public int getArity()
	{
		return Plus.arity;
	}

	public Expression get(char code)
	{
		return new Plus();
	}
}
