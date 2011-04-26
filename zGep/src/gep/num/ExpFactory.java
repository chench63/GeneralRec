package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 指数函数表达式工厂
 */
public class ExpFactory implements FunctionFactory
{

	public char getCode()
	{
		return Exp.code;
	}

	public int getArity()
	{
		return Exp.arity;
	}

	public Expression get(char code)
	{
		return new Exp();
	}
}
