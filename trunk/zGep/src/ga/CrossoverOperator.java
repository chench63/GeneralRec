package ga;

/**
 * 交叉算子
 * 
 * 由两个或者多个父代染色体交叉产生同样数量的子代染色体的遗传操作。
 * 包括单点、多点交叉等
 */
abstract public class CrossoverOperator extends GeneticOperator
{
	protected CrossoverOperator(double probability)
	{
		super(probability);
	}

	/**
	 * 实施遗传操作 
	 */
	abstract public Chromosome[] operate(Chromosome parents[]); 
}
