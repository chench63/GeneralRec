package gep.num;

import ga.Fitness;

/**
 * 数值的适应度函数
 */
public interface NFitness extends Fitness
{
	/**
	 * 由模型预测值计算适应度函数
	 * @param predicates
	 * @return
	 */
	public abstract double calculate(double[] values);
}
