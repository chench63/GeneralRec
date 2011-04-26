package gep.num;

import gep.Expression;
import gep.FunctionFactory;

/**
 * 自然对数函数表达式工厂
 */
public class LogFactory implements FunctionFactory
{

	public char getCode()
	{
		return Log.code;
	}

	public int getArity()
	{
		return Log.arity;
	}

	public Expression get(char code)
	{
		return new Log();
	}
	
}
