package gep.bool;

import gep.Expression;
import gep.FunctionFactory;

/**
 * B运算表达式工厂
 */
public class BFactory implements FunctionFactory
{

	public char getCode()
	{
		return B.code;
	}

	public int getArity()
	{
		return B.arity;
	}

	public Expression get(char code)
	{
		return new B();
	}
}
