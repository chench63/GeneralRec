package app.predict;

import ga.Evaluator;
import ga.Fitness;
import ga.Protein;
import gep.num.NDataSet;
import gep.num.NEvaluable;
import gep.num.NFitness;
import gep.num.NFormula;
import gep.ode.OdeSolver;

/**
 * �߽׳�΢�ַ���Ԥ��������
 */
public class OdePredictEvaluator implements Evaluator
{
	private int step;					// Ԥ��һ���� ��Ҫ�����Ĳ���(Ӱ�쾫��)
	private OdeSolver solver;			// ΢�ַ��������
	private NFitness fitness;			// ��Ӧ�Ⱥ���
	
	private int size;					// ����������
	private int rank;					// ΢�ַ��̵Ľ�
	private int future;					// ����΢�ַ�������δ���ĳ���
	private NEvaluable[] equations;		// ΢�ַ��̵��ұ�
	private double[] xs;				// x 
	private double[][] ys;				// y��y�ĸ��׵���
	private double dx;					// ������ļ��
	
	private double[] buffer;			// ��ʱ����
	private double[] values;			// ��ʱ����, ���ڼ�����Ӧ��
	
	public OdePredictEvaluator(Fitness fitness, NDataSet dataSet, int step)
	{
		this.fitness = (NFitness) fitness;
		double[][] parameters = dataSet.getParameters();
		double[] targets = dataSet.getTargets();
		this.step = step;

		this.size = parameters.length;
		this.rank = parameters[0].length - 1;
		this.future = targets.length / size;

		this.xs = new double[size];
		this.ys = new double[size][rank];
		for (int i=0; i<size; i++)
		{
			xs[i] = parameters[i][0];
			for (int j=0; j<rank; j++)
			{
				ys[i][j] = parameters[i][j+1]; 
			}
		}
		
		this.dx = xs[1] - xs[0];
		
		this.equations = new NEvaluable[rank];
		for (int i=0; i<rank-1; i++)
		{
			this.equations[i] = new F(i+2);
		}
		
		this.solver = new OdeSolver(rank);
		
		this.buffer = new double[rank];
		this.values = new double[size * future];
	}

	public double evaluate(Protein protein)
	{
		NFormula formula = (NFormula) protein;
		equations[rank-1] = formula;

		solver.setEquations(equations);
		
		// ���ÿһ��΢�ַ���, ������Ԥ��
		int k = 0;
		for (int i=0; i<size; i++)
		{
			double[] y = buffer;
			for (int j=0; j<rank; j++)
			{
				y[j] = ys[i][j];
			}

			// ����Ԥ��δ���ļ�����
			double x = xs[i];
			for (int j=0; j<future; j++, x+=dx)
			{
				try
				{
					solver.solve(x, y, x+dx, step);
				}
				catch (Exception e)
				{
					return fitness.getMinFitness();
				}
				
				values[k++] = y[0];
			}
		}
		
		// ������Ӧ��
		double f = fitness.calculate(values);

		return f;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}

class F implements NEvaluable
{
	private int m;
	
	public F(int m)
	{
		this.m = m;
	}
	
	public double evaluate(double[] values)
	{
		return values[m];
	}
}
