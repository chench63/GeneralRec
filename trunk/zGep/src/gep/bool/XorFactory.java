package gep.bool;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 与运算表达式工厂
 */
public class XorFactory implements FunctionFactory
{

	public char getCode()
	{
		return Xor.code;
	}

	public int getArity()
	{
		return Xor.arity;
	}

	public Expression get(char code)
	{
		return new Xor();
	}
}
