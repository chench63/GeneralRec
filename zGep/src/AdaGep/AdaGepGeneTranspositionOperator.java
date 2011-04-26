package AdaGep;

import ga.Chromosome;
import gep.GepChromosome;
import AdaGep.*;


/**
 * 基因迁移算子
 */
public class AdaGepGeneTranspositionOperator extends MutationOperator
{
	private AdaGep gep;
	
	public AdaGepGeneTranspositionOperator(AdaGep gep, double probability)
	{
		super(probability);
		this.gep = gep;
	}

	public Chromosome mutate(Chromosome chromosome)
	{
		if (!checkProbability()) return chromosome;
		
		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		char[] s = genes.toCharArray();					// 先从父代复制一份

		int geneNumber = gep.getGeneNumber();
		int geneLength = gep.getGeneLength();

		// 随机选择迁移的基因
		int g = (int) (Math.random()*geneNumber);
		
		// 挪出空位
		int q = g*geneLength;
		int m = geneLength;
		int n = 0;
		for (int i=0; i<q; i++)
		{
			s[m++] = p[n++];
		}
		
		// 前面插入迁移的基因
		m = 0;
		n = g*geneLength;
		for (int i=0; i<geneLength; i++)
		{
			s[m++] = p[n++];
		}
		
		return new GepChromosome(new String(s));
	}
	
	public Chromosome mutate(Chromosome chromosome,Chromosome chromosome1)
	{
if (!checkProbability()) return chromosome;
		
		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		char[] s = genes.toCharArray();					// 先从父代复制一份

		int geneNumber = gep.getGeneNumber();
		int geneLength = gep.getGeneLength();

		// 随机选择迁移的基因
		int g = (int) (Math.random()*geneNumber);
		
		// 挪出空位
		int q = g*geneLength;
		int m = geneLength;
		int n = 0;
		for (int i=0; i<q; i++)
		{
			s[m++] = p[n++];
		}
		
		// 前面插入迁移的基因
		m = 0;
		n = g*geneLength;
		for (int i=0; i<geneLength; i++)
		{
			s[m++] = p[n++];
		}
		
		return new GepChromosome(new String(s));
	}

}
