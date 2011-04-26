package AdaGep;

import ga.Chromosome;
import AdaGep.MutationOperator;
//import ga.MutationOperator;
import gep.GepChromosome;
import AdaGep.*;


/**
 * GEPµÄ±äÒìËã×Ó
 */
public class AdaGepMutationOperator extends MutationOperator
{
	private AdaGep gep;
	private AdaGep gepMap;
	
	public AdaGepMutationOperator(AdaGep gep,AdaGep gepMap, double probability)
	{
		super(probability);
		this.gep = gep;
		this.gepMap=gepMap;
	}
	
	public AdaGepMutationOperator(AdaGep gep,double probability)
	{
		super(probability);
		this.gep = gep;
	}

	public int count = 0;
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
	
	
	public Chromosome mutate(Chromosome chromosome,Chromosome chromosomeMap)
	{
		String genes = ((GepChromosome) chromosome).getGenes();
		String genesMap = ((GepChromosome) chromosomeMap).getGenes();
		
		char[] s = genes.toCharArray();
		char[] sMap = genesMap.toCharArray();
		
		int geneMapHead = gepMap.getGeneHead();
//		int geneMapTail = gepMap.getGeneTail();
//		int geneMapNumber = gepMap.getGeneNumber();
		
		
		int geneHead = gep.getGeneHead();
		int geneTail = gep.getGeneTail();
		int geneNumber = gep.getGeneNumber();
		int index = 0;
		
		count++;
	//	System.out.println("mutate                "+count+"     "+genes);
	//	System.out.println(geneNumber+"     "+geneHead);
		for (int k=0; k<geneNumber; k++)
		{
			for (int i=0; i<geneMapHead; i++)
			{
				if (!checkProbability()) continue; 
							
				
				sMap[i] = gepMap.getHeadCode();
			}
			for (int i=0; i<geneHead; i++, index++)
			{
				if (!checkProbability()) continue; 
			
			//	System.out.println("__________________________________Mutation");
				
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
