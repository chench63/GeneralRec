package gep.bool;

import ga.Evaluator;
import ga.Protein;
import gep.GEP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 布尔评价器
 */
public class BEvaluator implements Evaluator
{
	protected GEP gep;
	protected double maxFitness;
	protected List trainData = new ArrayList();	// 训练数据

	protected BEvaluator(GEP gep)
	{
		this.gep = gep;
	}

	/**
	 * 评价
	 * @param protein
	 * @return
	 */
	public double evaluate(Protein protein)
	{
		BFormula formula = (BFormula) protein;

		int ok = 0;				// 模型正确判断的数量
		for (Iterator iterator=trainData.iterator(); iterator.hasNext(); )
		{
			boolean[] sample = (boolean[]) iterator.next();
			boolean target = sample[0];  

			boolean model;
			try
			{
				model = formula.evaluate(sample);
			}
			catch (Exception e)
			{
				return 1.0;
			}
			
			if (target==model) ok++;
		}

		return ok;
	}
}
