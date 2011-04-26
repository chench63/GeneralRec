package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 正弦函数表达式工厂
 */
public class SinFactory implements FunctionFactory
{

	public char getCode()
	{
		return Sin.code;
	}

	public int getArity()
	{
		return Sin.arity;
	}

	public Expression get(char code)
	{
		return new Sin();
	}
}
