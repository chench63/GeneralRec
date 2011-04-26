package gep.bool;

import gep.Formula;

/**
 * ������ʽ
 */
public class BFormula extends Formula implements BEvaluable
{
	private BVariable[] variables;		// ������
	
	public BFormula(BExpression tree, int complex, BVariable[] variables)
	{
		super(tree, complex);
		this.variables = variables;
	}

	public boolean evaluate(boolean[] values)
	{
		for (int index=0; index<values.length; index++)
		{
			BVariable variable = variables[index-1];
			if (variable==null) continue;
			 
			variable.setValue(values[index]);
		}

		return ((BExpression) tree).evaluate();
	}
}
