package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 负号表达式工厂
 */
public class NegativeFactory implements FunctionFactory
{
	public char getCode()
	{
		return Negative.code;
	}

	public int getArity()
	{
		return Negative.arity;
	}

	public Expression get(char code)
	{
		return new Negative();
	}
}
