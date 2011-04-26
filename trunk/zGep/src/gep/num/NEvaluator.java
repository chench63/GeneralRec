package gep.num;

import ga.Evaluator;
import ga.Fitness;
import ga.Protein;
import gep.Formula;

/**
 * ��ֵ������
 */
public class NEvaluator implements Evaluator
{
	private NFitness fitnessFunction;	// ��Ӧ�Ⱥ���
	private double[][] parameters;				// ѵ�����ݵĲ���       
												//��λ���飬����Ϊ�������Ƕ�����Ĳ����б�
	
	public NEvaluator(Fitness fitnessFunction, NDataSet dataSet)
	{
		this.fitnessFunction = (NFitness) fitnessFunction;
		this.parameters = dataSet.getParameters();            
	}

	/**
	 * ���ۣ�������Ӧ��
	 * @param protein �����۵�"������"
	 * @return ��Ӧ��ֵ
	 */
	public static int count =0;
	public double evaluate(Protein protein)
	{
		NEvaluable evaluable = (NEvaluable) protein;

		int size = parameters.length;
		
		// ����ÿһ��������ֵ
		double[] model = new double[size];
		for (int i=0; i<size; i++)
		{
			double[] sample = parameters[i];        
			
			// ģ�ͼ���ֵ
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
		
		// ������Ӧ��
		double f = fitnessFunction.calculate(model);

		// TODO ����"�������ԭ��"
		if (false) f += 1/((Formula) protein).getComplex();
		
		return f;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
