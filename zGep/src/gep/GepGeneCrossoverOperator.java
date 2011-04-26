package gep;

import ga.Chromosome;
import ga.CrossoverOperator;

/**
 * 基因重组(交叉)
 */
public class GepGeneCrossoverOperator extends CrossoverOperator
{
	private GEP gep;

	public GepGeneCrossoverOperator(GEP gep, double probability)
	{
		super(probability);
		this.gep = gep;
	}

	public Chromosome[] operate(Chromosome[] chromosomes)
	{
		// 如果不满足概率，直接返回
		if (!checkProbability()) return chromosomes;
		
		// 取出父代基因
		String genes0 = ((GepChromosome) chromosomes[0]).getGenes();
		String genes1 = ((GepChromosome) chromosomes[1]).getGenes();
		
		char[] p0 = genes0.toCharArray();
		char[] p1 = genes1.toCharArray();
		char[] s0 = genes0.toCharArray();		// 先从父代复制
		char[] s1 = genes1.toCharArray();
		
		int geneNumber = gep.getGeneNumber();
		int geneLength = gep.getGeneLength();

		// 选择交叉基因
		int g = (int) (Math.random()*geneNumber);
		
		// 进行交叉 (交换选中的基因)
		int m = g*geneLength;
		for (int i=0; i<geneLength; i++)
		{
			s0[m] = p1[m];
			s1[m] = p0[m];
			m++;
		}

		// 准备子代染色体
		Chromosome[] sons = new Chromosome[2];
		sons[0] = new GepChromosome(new String(s0));
		sons[1] = new GepChromosome(new String(s1));
		
		return sons;
	}

}
