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
 * 高阶常微分方程预测评价器
 */
public class OdePredictEvaluator implements Evaluator
{
	private int step;					// 预测一个点 需要迭代的步数(影响精度)
	private OdeSolver solver;			// 微分方程求解器
	private NFitness fitness;			// 适应度函数
	
	private int size;					// 样本的数量
	private int rank;					// 微分方程的阶
	private int future;					// 利用微分方程推算未来的长度
	private NEvaluable[] equations;		// 微分方程的右边
	private double[] xs;				// x 
	private double[][] ys;				// y及y的各阶导数
	private double dx;					// 采样点的间隔
	
	private double[] buffer;			// 临时变量
	private double[] values;			// 临时变量, 用于计算适应度
	
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
		
		// 求解每一个微分方程, 并进行预测
		int k = 0;
		for (int i=0; i<size; i++)
		{
			double[] y = buffer;
			for (int j=0; j<rank; j++)
			{
				y[j] = ys[i][j];
			}

			// 依次预测未来的几个点
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
		
		// 计算适应度
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
