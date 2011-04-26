package gep.num;

import gep.Expression;
import gep.ExpressionFactory;

/**
 * 数值变量表达式工厂
 */
public class NConstantFactory implements ExpressionFactory
{
	private double[] constantValues;		// 常数的值
	private NConstant[] constants;					// 常数

	public Expression get(char code)
	{
		int index = code - '0';
		
		if (constants[index]==null)
		{
			constants[index] = new NConstant(code, constantValues[index]);
		}
		
		return constants[index];
	}
}
