package gep;

import ga.Chromosome;
import ga.MutationOperator;


/**
 * GEPµÄ±äÒìËã×Ó
 */
public class GepMutationOperator extends MutationOperator
{
	private GEP gep;
	
	public GepMutationOperator(GEP gep, double probability)
	{
		super(probability);
		this.gep = gep;
	}

	public Chromosome mutate(Chromosome chromosome)
	{
		String genes = ((GepChromosome) chromosome).getGenes();
		
		char[] s = genes.toCharArray();
		
		int geneHead = gep.getGeneHead();
		int geneTail = gep.getGeneTail();
		int geneNumber = gep.getGeneNumber();
		int index = 0;
		for (int k=0; k<geneNumber; k++)
		{
			for (int i=0; i<geneHead; i++, index++)
			{
				if (!checkProbability()) continue; 
				
				s[index] = gep.getHeadCode();
			}
			for (int i=0; i<geneTail; i++, index++)
			{
				if (!checkProbability()) continue; 
				
				s[index] = gep.getTailCode();
			}
		}
		
		return new GepChromosome(new String(s));
	}

}
