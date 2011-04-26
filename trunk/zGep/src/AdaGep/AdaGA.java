package AdaGep;

import ga.GA;
import ga.Chromosome;
import AdaGep.CrossoverOperator;
//import ga.CrossoverOperator;
import ga.Decoder;
import ga.Evaluator;
import ga.Fitness;
import ga.GepException;
import ga.Initializer;
//import ga.MutationOperator;
import AdaGep.MutationOperator;
import ga.Population;
import ga.Protein;
import ga.SelectionOperator;
import ga.Stopper;
import ga.MaxGenerationStopper;
import gep.GepChromosome;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AdaGA  extends GA
{
	/*
	public AdaGepInitializer initializer = null;				// ��ʼ��Ⱥ������
	public SelectionOperator selectionOperator = null;	// ѡ������
	public List mutationOperators = new ArrayList();	// ���еı������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)
	public List crossoverOperators = new ArrayList();	// ���еĽ������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)
	public AdaDecoder decoder;								// ������
	public Evaluator evaluator;							// ������
	public Stopper stopper;								// ֹͣ��
	public Fitness fitnessFunction;				// ��Ӧ�Ⱥ��� 
	
	public int generation = 0;							// ��ǰ�Ŵ�����
	public Population population;						// ��ǰ��Ⱥ
	public Population populationMap;                    //��ǰ��Ⱥ��ӳ���
	
	public Chromosome bestChromosome;					// ���Ÿ���
	public Protein bestProtein;							// ���Ÿ��������ռ���
	public double bestFitness = -200;					// ���Ÿ������Ӧ��
	public int bestGeneration;							// ���Ÿ���ĳ��ֵĴ���
	
	public double lastBestFitness = -300;				// ǰһ��������Ӧ��

	public long startTime;								// ���п�ʼ��ʱ��
	public long stopTime;								// ���н�����ʱ��
	public long bestTime;								// ��ǰ���Ÿ����ʱ��

	public List averageFitnessHistory = new ArrayList();// ƽ����Ӧ�ȵı仯��ʷ
	public List bestFitnessHistory = new ArrayList();	// �����Ӧ�ȵı仯��ʷ
	*/
	
	public Population populationMap;                    //��ǰ��Ⱥ��ӳ���
	public AdaGepInitializer Adainitializer = null;				// ��ʼ��Ⱥ������
	public AdaDecoder Adadecoder;								// ������
//	public List mutationOperators = new ArrayList();	// ���еı������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)
//	public List crossoverOperators = new ArrayList();	// ���еĽ������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)

	/**
	 * ���ó�ʼ��Ⱥ������
	 * @param initializer
	 */
	public void setAdaInitializer(AdaGepInitializer initializer)
	{
		this.Adainitializer = initializer;	
	}


	/**
	 * ��ӱ�������
	 * @param mutationOperator
	 */
	public void addAdaMutationOperator(MutationOperator mutationOperator)
	{
		this.mutationOperators.add(mutationOperator);
	}

	/**
	 * ��ӽ������� (�����ж��)
	 * @param crossoverOperators
	 */
	public void addAdaCrossoverOperator(CrossoverOperator crossoverOperator)
	{
		this.crossoverOperators.add(crossoverOperator);
	}
	
	/**
	 * ���ý�����
	 * @param decoder
	 */
	public void setAdaDecoder(Decoder decoder)
	{
		this.Adadecoder = (AdaDecoder) decoder;
	}
	

	/**
	 * ����GepMap
	 * @param decoder
	 */

	public Population generateInitialPopulation()
	{
		return null;
	}
	
	
	
	


	/**
	 * �����Ŵ�����
	 */
	public void Adarun()
	{
		startTime = System.currentTimeMillis();
		
		// ������ʼ��Ⱥ
		population = Adainitializer.generateInitialPopulation();
		Adainitializer.setAdaDecoder(Adadecoder);
		Adainitializer.setEvaluator(evaluator);
		populationMap = Adainitializer.generateInitialPopulationMap();
		
		
	//	for(int i=0;i<50;i++)
	//		System.out.println(   i+":"+((GepChromosome)populationMap.get(i)).getGenes()  );
		
		
		// ���۵�ǰ��Ⱥ ͬʱ�������Ÿ���
		double[] fitnesses = evaluate(population);
		
		// ���н���
		for (; !stopper.canStop(); generation++)
		{
			// ���������Ϣ
	//		System.out.println("" + g + " : " + bestFitness + " : " + bestProtein);
			verbose(fitnesses, 0);
		

	//		for(int i=0;i<50;i++)
	//		System.out.println(   i+":"+((GepChromosome)population.get(i)).getGenes()  );
			
			
			// ѡ�� ͬʱ�����һ�����Ÿ���
			population = select(population, fitnesses);
			
			// ����
			population = mutation(population,populationMap);
			
			
			// ����
			population = crossover(population,populationMap);
			
			
	//		population = mutation(population);
	//		population = crossover(population);
			
			
	//		for(int i=0;i<50;i++)
	//			System.out.println(   i+":"+((GepChromosome)population.get(i)).getGenes()  );

			// ���۵�ǰ��Ⱥ ͬʱ�������Ÿ���
			fitnesses = evaluate(population);
		}
		System.out.println("==============================================");
		System.out.println("" + bestGeneration + ": " + bestFitness + " : " + bestProtein);

		stopTime = System.currentTimeMillis();
	}
	
	/**
	 * ������Ӧ��ѡ����Ⱥ
	 * @param population
	 * @param index
	 * @return
	 */
	private Population select(Population population, double fitnesses[])
	{
		// ����ѡ������������
//		for(int i=0;i<50;i++)
//		System.out.println(fitnesses[i]);
		int[] index = selectionOperator.select(fitnesses);

		Population pop = new Population();

		// ������Ÿ���
		pop.add(bestChromosome);

		// ��������ѡ��
		int size = population.size();
		for (int i=1; i<size; i++)
		{
			pop.add(population.get(index[i]));
		}

		return pop;
	}

	/**
	 * �������
	 * @param population
	 * @return
	 */
	private Population mutation(Population population)
	{
		// Ӧ��ÿһ����������
		int size = population.size();
		for (Iterator iterator=mutationOperators.iterator(); iterator.hasNext(); )
		{
			MutationOperator mutationOperator = (MutationOperator) iterator.next();
			Population pop = new Population();
			for (int i=0; i<size; i++)
			{
				Chromosome chromosome = population.get(i);
				chromosome = mutationOperator.mutate(chromosome);
				pop.add(chromosome);
			}
			population = pop;
		}
		
		return population;
	}
	
	
	private Population mutation(Population population,Population populationMap)
	{
		// Ӧ��ÿһ����������
		int size = population.size();
		for (Iterator iterator=mutationOperators.iterator(); iterator.hasNext(); )
		{
			MutationOperator mutationOperator = (MutationOperator) iterator.next();
			Population pop = new Population();
			Population popMap = new Population();
			
			
			for (int i=0; i<size; i++)
			{
				Chromosome chromosome = population.get(i);
				Chromosome chromosomeMap = populationMap.get(i);
				chromosome = mutationOperator.mutate(chromosome,chromosomeMap);
				pop.add(chromosome);
				popMap.add(chromosomeMap);
			}
			population = pop;
			populationMap = popMap;
		}
		
		return population;
	}
	
	

	/**
	 * ѡ�����
	 * @param population
	 * @return
	 */
	private Population crossover(Population population)
	{
		int size = population.size();
		for (Iterator iterator=crossoverOperators.iterator(); iterator.hasNext(); )
		{
			CrossoverOperator crossoverOperator = (CrossoverOperator) iterator.next();
			Population pop = new Population();
			for (int i=0; i<size; i+=2)
			{
				Chromosome[] parents = new Chromosome[2]; 
				parents[0] = population.get(i);
				parents[1] = population.get(i+1);
					
				Chromosome[] sons = crossoverOperator.operate(parents);
				pop.add(sons[0]);
				pop.add(sons[1]);
			}
			population = pop;
		}
		
		return population;
	}

	
	private Population crossover(Population population,Population populationMap)
	{
		int size = population.size();
		for (Iterator iterator=crossoverOperators.iterator(); iterator.hasNext(); )
		{
			CrossoverOperator crossoverOperator = (CrossoverOperator) iterator.next();
			Population pop = new Population();
			Population popMap = new Population();
			for (int i=0; i<size; i+=2)
			{
				Chromosome[] parents = new Chromosome[2]; 
				parents[0] = population.get(i);
				parents[1] = population.get(i+1);
				
					
				Chromosome[] sons = crossoverOperator.operate(parents);
				
				pop.add(sons[0]);
				pop.add(sons[1]);
				
				
				//_________________________________AdaGep
				Chromosome[] parentsMap = new Chromosome[2]; 
				parentsMap[0] = populationMap.get(i);
				parentsMap[1] = populationMap.get(i+1);
				
				Chromosome[] sonsMap = crossoverOperator.operate(parents);
				popMap.add(sonsMap[0]);
				popMap.add(sonsMap[1]);
				
				
			}
			population = pop;
			populationMap = popMap;
		}
		
		return population;
	}

	
	
	
	
	/**
	 * ��������������Ⱥ��������
	 * @return ��Ⱥ��ÿһ���������Ӧ��
	 */

	private double[] evaluate(Population population)
	{
		//
		int size = population.size();
		double[] fitnesses = new double[size];
		double averageFitness = 0.0;
		for (int i=0; i<size; i++)
		{
			Chromosome chromosome = population.get(i);
			
			// ����
			//______________________________________________________________________________
			//GepMap Block
			
			Protein protein = Adadecoder.decode(chromosome,i,populationMap);
			
			//Protein protein = decoder.decode(chromosome);
			
			// ����
			double fitness = evaluator.evaluate(protein);
			fitnesses[i] = fitness;
			averageFitness += fitness; 	
			
			// �������Ÿ���
			if (fitness>bestFitness)
			{
				bestChromosome = chromosome;
				bestProtein = protein;
				bestFitness = fitness;
				bestGeneration = generation;
				bestTime = System.currentTimeMillis();
			}
		}
		averageFitness /= size;
		
		averageFitnessHistory.add(new Double(averageFitness));
		bestFitnessHistory.add(new Double(bestFitness));
		
		
		
		
		return fitnesses;
	}


	/**
	 * �������
	 * @param population
	 * @param fitnesses
	 * @param level
	 */
	private void verbose(double[] fitnesses, int level)
	{
		boolean output = false;
		if (bestFitness!=lastBestFitness)
		{
			lastBestFitness = bestFitness;
			System.out.println("" + generation + ": " + bestFitness + " : " + bestProtein);
		}
		else if (generation%10==0)
		{
			System.out.print("" + generation + ": " + bestFitness + " : " + bestProtein + "\r");
		}

		if (level==0) return;
		
		int size = population.size();
		for (int i=0; i<size; i++)
		{
			Chromosome chromosome = population.get(i);
			System.out.println("" + fitnesses[i] + " : " + chromosome);
//			System.out.println("" + chromosome);
		}
	}

	
}
