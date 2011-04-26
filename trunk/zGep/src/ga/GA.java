package ga;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * �Ŵ��㷨
 */
public abstract class GA
{
	public Initializer initializer = null;				// ��ʼ��Ⱥ������
	public SelectionOperator selectionOperator = null;	// ѡ������
	public List mutationOperators = new ArrayList();	// ���еı������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)
	public List crossoverOperators = new ArrayList();	// ���еĽ������� (ÿ��Ⱦɫ�嶼Ҫ����ÿ���������ӵĲ���)
	public Decoder decoder;								// ������
	public Evaluator evaluator;							// ������
	public Stopper stopper;								// ֹͣ��
	public Fitness fitnessFunction;				// ��Ӧ�Ⱥ��� 
	
	public int generation = 0;							// ��ǰ�Ŵ�����
	public Population population;						// ��ǰ��Ⱥ
	
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

	/**
	 * ���ó�ʼ��Ⱥ������
	 * @param initializer
	 */
	public void setInitializer(Initializer initializer)
	{
		this.initializer = initializer;	
	}

	/**
	 * ����ѡ����
	 * @param selector
	 */
	public void setSelectionOperator(SelectionOperator selectionOperator)
	{
		this.selectionOperator = selectionOperator;
	}

	/**
	 * ��ӱ�������
	 * @param mutationOperator
	 */
	public void addMutationOperator(MutationOperator mutationOperator)
	{
		this.mutationOperators.add(mutationOperator);
	}

	/**
	 * ��ӽ������� (�����ж��)
	 * @param crossoverOperators
	 */
	public void addCrossoverOperator(CrossoverOperator crossoverOperator)
	{
		this.crossoverOperators.add(crossoverOperator);
	}
	
	/**
	 * ���ý�����
	 * @param decoder
	 */
	public void setDecoder(Decoder decoder)
	{
		this.decoder = decoder;
	}
	
	/**
	 * ������Ӧ�Ⱥ���
	 * @param fitnessFunction
	 */
	public void setFitnessFunction(Fitness fitnessFunction)
	{
		this.fitnessFunction = fitnessFunction;
	}

	/**
	 * ����������
	 * @param evaluator
	 */
	public void setEvaluator(Evaluator evaluator)
	{
		this.evaluator = evaluator;
	}

	/**
	 * ����ֹͣ��
	 * @param stopper
	 */
	public void setStopper(Stopper stopper)
	{
		this.stopper = stopper;
	}

	/**
	 * �����Ŵ�����
	 */
	public void run()
	{
		startTime = System.currentTimeMillis();
		
		// ������ʼ��Ⱥ
		population = initializer.generateInitialPopulation();	

		// ���۵�ǰ��Ⱥ ͬʱ�������Ÿ���
		double[] fitnesses = evaluate(population);
		
		// ���н���
		for (; !stopper.canStop(); generation++)
		{
			// ���������Ϣ
//			System.out.println("" + g + " : " + bestFitness + " : " + bestProtein);
			verbose(fitnesses, 0);

			// ѡ�� ͬʱ�����һ�����Ÿ���
			population = select(population, fitnesses);
			
			// ����
			population = mutation(population);
			
			// ����
			population = crossover(population);

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

	/**
	 * ��������������Ⱥ��������
	 * @return ��Ⱥ��ÿһ���������Ӧ��
	 */
	private double[] evaluate(Population population)
	{
		int size = population.size();
		double[] fitnesses = new double[size];
		double averageFitness = 0.0;
		for (int i=0; i<size; i++)
		{
			Chromosome chromosome = population.get(i);
			
			// ����
			//______________________________________________________________________________
			//GepMap Block
			//Protein protein = decoder.decode(chromosome,i);
			
			Protein protein = decoder.decode(chromosome);
			
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
	 * �������Ÿ���ı�����
	 * @return
	 */
	public Protein getBestProtein()
	{
		return bestProtein;
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

	/**
	 * �����������
	 * @param writer
	 */
	public void report(PrintWriter writer) throws GepException
	{
		writer.println("Selection Operator : " + selectionOperator);
		for (Iterator i=mutationOperators.iterator(); i.hasNext(); )
		{
			writer.println("Mutation Operator  : " + i.next());
		}
		for (Iterator i=crossoverOperators.iterator(); i.hasNext(); )
		{
			writer.println("Crossover Operator : " + i.next());
		}
		writer.println("Decoder            : " + decoder);
		writer.println("Evaluator          : " + evaluator);
		writer.println("Stopper            : " + stopper);
		writer.println("Fitness Function   : " + fitnessFunction); 
	
		writer.println("Generation         : " + generation);
		writer.println("Population Size    : " + population.size());
	
		writer.println("Best Chromosome    : " + bestChromosome);
		writer.println("Best Fitness       : " + bestFitness);
		writer.println("Best Formula       : " + bestProtein);
	
		writer.println("Used Time          : " + (stopTime - startTime));
		writer.println("Used Time to Best  : " + (bestTime - startTime));

//		public List averageFitnessHistory = new ArrayList();// ƽ����Ӧ�ȵı仯��ʷ
//		public List bestFitnessHistory = new ArrayList();	// �����Ӧ�ȵı仯��ʷ
	}
}
