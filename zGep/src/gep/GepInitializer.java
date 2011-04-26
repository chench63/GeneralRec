package gep;

import ga.Initializer;
import ga.Population;

/**
 * 用于数值优化的初始种群产生器
 */
public class GepInitializer implements Initializer
{
	private GEP gep;
	private int size;			// 种群大小

	public GepInitializer(GEP gep, int size)
	{
		this.gep = gep;
		this.size = size;
	}

	public Population generateInitialPopulation()
	{
//		int populationSize = gep.getPopulationSize();
		Population population = new Population();
		
		int geneNumber = gep.getGeneNumber();
		int geneHead = gep.getGeneHead();
		int geneTail = gep.getGeneTail();
		int geneLength = gep.getGeneLength();
		for (int k=0; k<size; k++)
		{
			char[] genes = new char[geneLength*geneNumber];
			int index = 0;
			for (int j=0; j<geneNumber; j++)
			{
				for (int i=0; i<geneHead; i++)
				{
					genes[index++] = gep.getHeadCode(); 
				}
				for (int i=0; i<geneTail; i++)
				{
					genes[index++] = gep.getTailCode();
				}
			}
			
			population.add(new GepChromosome(new String(genes)));
		}
		
		return population;
	}
	
}
