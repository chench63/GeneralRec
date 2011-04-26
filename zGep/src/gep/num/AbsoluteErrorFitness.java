package gep.num;

/**
 * 使用绝对误差计算适应度函数
 */
public class AbsoluteErrorFitness extends GeneralNFitness
{
	private final double MIN_FITNESS = 1.0;
	private double[] targets;					// 目标值
	private double M;							// 允许的绝对误差值上限
	
	public AbsoluteErrorFitness(NDataSet dataSet, double M)
	{
		super(dataSet);
		this.M = M;
	}

	public double calculate(double[] values)
	{
		double sum = 0.0;
		for (int i=0; i<values.length; i++)
		{
			sum += M - Math.abs(values[i]-targets[i]);
		}
		if (sum<0) sum = 0;

		return sum;
	}

	public double getMinFitness()
	{
		return MIN_FITNESS;
	}
	
	public String toString()
	{
		return getClass().getName() + "(M:" + M +")";
	}
}
