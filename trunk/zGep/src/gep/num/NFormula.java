package gep.num;

import gep.Formula;

/**
 * GEP数值公式
 */
public class NFormula extends Formula implements NEvaluable
{
	private NVariable[] variables;		// 变量表
	private NConstant[] constants;		// 常量表

	public NFormula(NExpression tree, int complex, NVariable[] variables, NConstant[] constants)
	{
		super(tree, complex);
		this.variables = variables;
		this.constants = constants;
	}

	public double evaluate(double[] values)
	{
		// 设置变量的值
		for (int index=0; index<values.length; index++)
		{
			NVariable variable = variables[index];
			if (variable==null) continue;
			 
			variable.setValue(values[index]);
		}

		// 计算公式的值
		return ((NExpression) tree).evaluate();
	}
}
