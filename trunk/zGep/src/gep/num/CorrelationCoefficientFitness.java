package gep.num;


/**
 * 使用复相关系数的适应度函数
 */
public class CorrelationCoefficientFitness extends GeneralNFitness
{
	private static double MIN_FITNESS = -100;
	private double deviation;					// 目标数据的偏差

	public CorrelationCoefficientFitness(NDataSet dataSet)
	{
		super(dataSet);
		initialize();
	}

	/**
	 * 初始化  计算目标值的偏差
	 */
	private void initialize()
	{
		int size = targets.length;
		double average = 0.0;
		for (int i=0; i<size; i++)
		{
			average += targets[i];
		}
		average /= size;
		
		deviation = 0.0;
		for (int i=0; i<size; i++)
		{
			double t = targets[i] - average;
			deviation += t * t;
		}
	}
	
	public double calculate(double[] values)
	{
		double f = 0.0;
		for (int i=0; i<values.length; i++)
		{
			double t = values[i] - targets[i];
			f += t * t;
		}
		f = 1 - f/deviation;
		
		if (f<MIN_FITNESS) f = MIN_FITNESS;

		return f;
	}

	public double getMinFitness()
	{
		return MIN_FITNESS;
	}
	
	public String toString()
	{
		return getClass().getName();
	}

}
