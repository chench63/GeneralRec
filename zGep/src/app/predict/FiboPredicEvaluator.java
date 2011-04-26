package app.predict;


import ga.*;
import gep.num.*;

/**
 * fibonacci平滑预测的评价器
 */
public class FiboPredicEvaluator implements Evaluator
{
	private NFitness fitnessFunction;	// 适应度函数
	private double[][] parameters;		// 训练数据的参数
	
	private int size;					// 数据集的大小
	private int history;				// 历史长度
	private int future;					// 未来长度
	
	private double[] values;			// 临时变量
	private double[] buffer;			//
	
	public FiboPredicEvaluator(Fitness fitnessFunction, NDataSet dataSet)
	{
		this.fitnessFunction = (NFitness) fitnessFunction;
		this.parameters = dataSet.getParameters();
		this.size = parameters.length;
		this.history = parameters[0].length;
		
		double[] targets = dataSet.getTargets();
		future = targets.length / size;
		
		values = new double[size*future];
		buffer = new double[history];
	}

	public double evaluate(Protein protein)
	{
		NEvaluable evaluable = (NEvaluable) protein;

		// 计算每一个样本的值
		try
		{
			int s = 0;
			for (int i=0; i<size; i++)
			{
				for (int j=0; j<history; j++)
				{
					buffer[j] = parameters[i][j];
				}
	
				for (int j=0; j<future; j++)
				{			
					double value = evaluable.evaluate(buffer);
					
					// 利用刚刚计算出来的值进行下一步预测
					for (int k=1; k<history; k++)
					{
						buffer[k-1] = buffer[k];
					}
					buffer[history-1] = value; 
					
					values[s++] = value;
				}
			}
		}
		catch (Exception e)
		{
			return fitnessFunction.getMinFitness();
		}
		
		// 计算适应度
		double f = fitnessFunction.calculate(values);

		// TODO 测试"世界简单性原理"
//		if (false) f += 1/((Formula) protein).getComplex();
		
		return f;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
