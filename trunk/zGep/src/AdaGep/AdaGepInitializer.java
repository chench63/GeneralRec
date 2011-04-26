package AdaGep;

import java.util.ArrayList;
import java.util.List;

import ga.Chromosome;
import ga.Decoder;
import ga.Initializer;
import ga.Population;
import ga.Evaluator;
import ga.Protein;
import AdaGep.AdaGep;
import gep.GepChromosome;


public class AdaGepInitializer implements Initializer{
	private AdaGep gep;
	private AdaGep gepMap;
	private int size;			// 种群大小
	public Evaluator evaluator;							// 评价器
	public AdaDecoder Adadecoder;
	public List averageFitnessHistory = new ArrayList();// 平均适应度的变化历史
	public List bestFitnessHistory = new ArrayList();	// 最高适应度的变化历史
	public Chromosome bestChromosome;					// 最优个体
	public Protein bestProtein;							// 最优个体的问题空间表达
	public double bestFitness = -200;					// 最优个体的适应度
	public int bestGeneration;							// 最优个体的出现的代数
	
	public Population populationMap;                    //当前种群的映射表
	public Population population;                    //当前种群的映射表
	
	

	public AdaGepInitializer(AdaGep gep, AdaGep gepMap,int size)
	{
		this.gep = gep;
		this.gepMap=gepMap;
		this.size = size;
		populationMap=new Population();
		population = new Population();
	}
	
	/**
	 * 设置评价器
	 * @param evaluator
	 */
	public void setEvaluator(Evaluator evaluator)
	{
		this.evaluator = evaluator;
	}
	
	
	/**
	 * 设置解码器
	 * @param decoder
	 */
	public void setAdaDecoder(Decoder decoder)
	{
		this.Adadecoder = (AdaDecoder) decoder;
	}
	
	
	
	/**
	 * 利用评价器对种群进行评价
	 * @return 种群中每一个个体的适应度
	 */
	public static int Count =  -1;
	private double[] evaluate(int index)
	{
		int geneNumber = gep.getGeneNumber();
		int size = (int)(Math.pow(2.0, (double)geneNumber))-1;
		double[] fitnesses = new double[size];
		for (int i=0; i<size; i++)
		{
			Chromosome chromosome = population.get(index);
			
			// 解码
			//______________________________________________________________________________
			//GepMap Block
			
//			System.out.println(     ((GepChromosome)chromosome).getGenes()   );
//			System.out.println(     ((GepChromosome)populationMap.get(i)).getGenes()   );
			
			
			Protein protein = Adadecoder.decode(chromosome,i,populationMap);
			
			

			//Protein protein = decoder.decode(chromosome);
			
			// 评价
			double fitness = evaluator.evaluate(protein);
			fitnesses[i] = fitness;

//___________________________________________________________________________________________				
//			Count++;
//			System.out.println("Count:"+(Count/7+1)+"  "+(Count%7+1)+"    "+fitness);
//			System.out.println(fitness);
	    }
		
		return fitnesses;
	}
	
	
	
	
	

	public Population generateInitialPopulation()
	{
//		int populationSize = gep.getPopulationSize();
//		Population population = new Population();
		
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
	//		System.out.println( k  +"   "+    ((GepChromosome)population.get(k)).getGenes()   );
		}
//		this.population=population;
		
		return population;
	}

	
/*	
//_____________________Important~___________________	
	public Population generateInitialPopulationMap()
	{
		Population population = new Population();
		
		int geneNumber = gep.getGeneNumber();
		int geneHead = gepMap.getGeneHead();
		int geneLength = gepMap.getGeneLength();

		int Size = (int)(Math.pow(2.0, (double)geneNumber)) -1;
		
		for(int i=0;i<Size;i++)
			format(i);
		
		for(int i=0;i<size;i++)
		{
			double[] tempf = evaluate(i);
			double Maxfitness=0.0;
			int Maxindex=0;
			for(int k=0;k<Size;k++)
			{
				if(tempf[k] > Maxfitness)
				{
					Maxfitness=tempf[k];
					Maxindex=k;
				}
//				System.out.println(i+"    "+k+"    "+tempf[k]);
			}
			population.add(populationMap.get(Maxindex));
//			System.out.println(i+":     "+Maxfitness+"       "+
//					((GepChromosome)populationMap.get(Maxindex)).getGenes());
		}
		
		return population;
	}
*/	
	
	

	
	public void format(int a)
	{
		String genesMap="";
		String temp = Integer.toBinaryString(a);
		String genesTemp= temp.format("%1$,03d", Integer.parseInt(temp));
		temp="";
		for(int i= 0;i<genesTemp.length();i++)
			if( Character.isDigit(genesTemp.charAt(i)))
				temp+=genesTemp.charAt(i);
			
			
		genesTemp=temp;
			//仅当它为3个基因时				
			for(int k=0;k<genesTemp.length();k++)
			{
				if(genesTemp.charAt(k) == '1')
					genesMap+="a";
				else
					genesMap+="b";
			}
		
		populationMap.add(new GepChromosome(genesMap));
	}


	public Population generateInitialPopulationMap()
	{
		Population population = new Population();
		
		int geneNumber = gepMap.getGeneNumber();
		int geneHead = gepMap.getGeneHead();
		int geneTail = gepMap.getGeneTail();
		int geneLength = gepMap.getGeneLength();
		for (int k=0; k<size; k++)
		{
			char[] genes = new char[geneLength*geneNumber];
			int index = 0;
			for (int j=0; j<geneNumber; j++)
			{
				for (int i=0; i<geneHead; i++)
				{
					genes[index++] = gepMap.getHeadCode(); 
					
//					boolean check = true;
//					int iTemp =0;
//					while( j == geneHead-1 && iTemp<=j )
//						if(genes[iTemp] == 'b')
//							check =false;
//					
//					if( check && j == geneHead-1 )
//						i=0;				
				}
				for (int i=0; i<geneTail; i++)
				{
					genes[index++] = gepMap.getTailCode();
				}
			}
			
			population.add(new GepChromosome(new String(genes)));
		}
		
		return population;
	}




}



