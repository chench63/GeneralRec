package ga;

/**
 * 评价器
 */
public interface Evaluator
{
	/**
	 * 评价一个个体，返回适应度值
	 * @param protein
	 * @return
	 */
	public double evaluate(Protein protein);
}
