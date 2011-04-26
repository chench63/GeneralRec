package app.predict;


import ga.*;
import gep.num.*;

/**
 * fibonacciƽ��Ԥ���������
 */
public class FiboPredicEvaluator implements Evaluator
{
	private NFitness fitnessFunction;	// ��Ӧ�Ⱥ���
	private double[][] parameters;		// ѵ�����ݵĲ���
	
	private int size;					// ���ݼ��Ĵ�С
	private int history;				// ��ʷ����
	private int future;					// δ������
	
	private double[] values;			// ��ʱ����
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

		// ����ÿһ��������ֵ
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
					
					// ���øոռ��������ֵ������һ��Ԥ��
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
		
		// ������Ӧ��
		double f = fitnessFunction.calculate(values);

		// TODO ����"�������ԭ��"
//		if (false) f += 1/((Formula) protein).getComplex();
		
		return f;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
