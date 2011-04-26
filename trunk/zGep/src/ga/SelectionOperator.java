package ga;

/**
 * 选择算子
 */
public interface SelectionOperator
{
	/**
	 * 选择操作 (根据适应度，返回应该选择的个体的索引)
	 * @param population
	 * @return
	 */
	public int[] select(double[] fitnesses);
}
