package gep.num;

import gep.Formula;

/**
 * GEP��ֵ��ʽ
 */
public class NFormula extends Formula implements NEvaluable
{
	private NVariable[] variables;		// ������
	private NConstant[] constants;		// ������

	public NFormula(NExpression tree, int complex, NVariable[] variables, NConstant[] constants)
	{
		super(tree, complex);
		this.variables = variables;
		this.constants = constants;
	}

	public double evaluate(double[] values)
	{
		// ���ñ�����ֵ
		for (int index=0; index<values.length; index++)
		{
			NVariable variable = variables[index];
			if (variable==null) continue;
			 
			variable.setValue(values[index]);
		}

		// ���㹫ʽ��ֵ
		return ((NExpression) tree).evaluate();
	}
}
