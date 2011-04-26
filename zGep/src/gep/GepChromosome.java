package gep;

import ga.Chromosome;

/**
 * GEP的染色体
 */
public class GepChromosome implements Chromosome
{
	private String genes;				// 内容基因
	private String structureGene;		// 结构基因 (表示前几个基因空间结构的基因)
	
	public GepChromosome(String genes)
	{
		this.genes = genes;
	}
	
	public String getGenes()
	{
		return genes;
	}
	
	public String getStructureGene()
	{
		return structureGene;
	}
	
	public String toString()
	{
		// TODO 没有考虑结构基因的显示
		return genes;
	}
}
