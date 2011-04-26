package gep.num;

import gep.Expression;
import gep.ExpressionFactory;

/**
 * 数值变量工厂
 */
public class NVariableFactory implements ExpressionFactory
{
	private NVariable[] variables;					// 变量列表

	public Expression get(char code)
	{
		int index = code - '0';
		
		if (variables[index]==null)
		{
			variables[index] = new NVariable(code);
		}
		
		return variables[index];
	}
}
