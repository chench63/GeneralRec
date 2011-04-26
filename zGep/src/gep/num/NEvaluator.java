package gep.num;

import ga.Evaluator;
import ga.Fitness;
import ga.Protein;
import gep.Formula;

/**
 * 数值评价器
 */
public class NEvaluator implements Evaluator
{
	private NFitness fitnessFunction;	// 适应度函数
	private double[][] parameters;				// 训练数据的参数       
												//二位数组，是因为他可能是多变量的参数列表
	
	public NEvaluator(Fitness fitnessFunction, NDataSet dataSet)
	{
		this.fitnessFunction = (NFitness) fitnessFunction;
		this.parameters = dataSet.getParameters();            
	}

	/**
	 * 评价，返回适应度
	 * @param protein 待评价的"蛋白质"
	 * @return 适应度值
	 */
	public static int count =0;
	public double evaluate(Protein protein)
	{
		NEvaluable evaluable = (NEvaluable) protein;

		int size = parameters.length;
		
		// 计算每一个样本的值
		double[] model = new double[size];
		for (int i=0; i<size; i++)
		{
			double[] sample = parameters[i];        
			
			// 模型计算值
			try
			{
				model[i] = evaluable.evaluate(sample);
			}
			catch (Exception e)
			{
//				count++;
//				System.out.println("_____"+count+"      evaluate(Protein protein)   "
//						+e.getMessage());
				return fitnessFunction.getMinFitness();
			}
		}
		
		// 计算适应度
		double f = fitnessFunction.calculate(model);

		// TODO 测试"世界简单性原理"
		if (false) f += 1/((Formula) protein).getComplex();
		
		return f;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
