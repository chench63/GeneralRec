package gep.num;

/**
 * 数值公式
 */
public interface NEvaluable
{
	/**
	 * 计算公式的值
	 * @return
	 */
	public abstract double evaluate(double[] values);
}