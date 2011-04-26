package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 绝对值函数表达式工厂
 */
public class AbsFactory implements FunctionFactory
{

	public char getCode()
	{
		return Abs.code;
	}

	public int getArity()
	{
		return Abs.arity;
	}

	public Expression get(char code)
	{
		return new Abs();
	}
}
