package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 平方根函数表达式工厂
 */
public class SqrtFactory implements FunctionFactory
{

	public char getCode()
	{
		return Sqrt.code;
	}

	public int getArity()
	{
		return Sqrt.arity;
	}

	public Expression get(char code)
	{
		return new Sqrt();
	}
}
