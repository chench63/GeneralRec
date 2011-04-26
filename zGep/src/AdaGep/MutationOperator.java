package AdaGep;

import ga.GeneticOperator;
import ga.Chromosome;
/**
 * 变异算子
 * 
 * 单个染色体内部进行的遗传操作。
 * 包括重组、变异等
 */
public abstract class MutationOperator extends GeneticOperator
{
	public MutationOperator(double probability)
	{
		super(probability);
	}

	/**
	 * 实施遗传操作 
	 */
	abstract public Chromosome mutate(Chromosome chromosome); 
	abstract public Chromosome mutate(Chromosome chromosome,Chromosome chromosomeMap);
}
