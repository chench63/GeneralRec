package gep.num;

import ga.Fitness;

/**
 * ��ֵ����Ӧ�Ⱥ���
 */
public interface NFitness extends Fitness
{
	/**
	 * ��ģ��Ԥ��ֵ������Ӧ�Ⱥ���
	 * @param predicates
	 * @return
	 */
	public abstract double calculate(double[] values);
}
