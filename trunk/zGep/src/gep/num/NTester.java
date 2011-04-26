package gep.num;

import ga.Fitness;
import ga.Protein;
import ga.Tester;

/**
 * 数值评价器
 */
public class NTester implements Tester
{
	private NFitness fitnessFunction;	// 适应度函数
	private NDataSet dataSet;					// 训练数据
	
	private double[] values;		// 计算出来的值
	
	public NTester(Fitness fitnessFunction, NDataSet dataSet)
	{
		this.fitnessFunction = (NFitness) fitnessFunction;
		this.dataSet = dataSet;
	}

	/**
	 * 测试，返回适应度
	 * @param protein 待测试的"蛋白质"
	 * @return 适应度值
	 */
	public double test(Protein protein)
	{
		NEvaluable formula = (NEvaluable) protein;

		double[][] parameters = dataSet.getParameters();
		int size = parameters.length;
		
		// 计算每一个样本的值
		values = new double[size];
		for (int i=0; i<size; i++)
		{
			double[] sample = parameters[i];
			
			// 模型预测值
			values[i] = formula.evaluate(sample);
			if (Double.isNaN(values[i])) return fitnessFunction.getMinFitness();
			if (Double.isInfinite(values[i])) return fitnessFunction.getMinFitness();
		}
		
		// 计算适应度
		double fitness = fitnessFunction.calculate(values);
		
		return fitness;
	}
	
	/**
	 * 返回测试计算出来的值
	 * @return
	 */
	public double[] getValues()
	{
		return values;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
