package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 减法表达式工厂
 */
public class MinusFactory implements FunctionFactory
{

	public char getCode()
	{
		return Minus.code;
	}

	public int getArity()
	{
		return Minus.arity;
	}

	public Expression get(char code)
	{
		return new Minus();
	}
}
