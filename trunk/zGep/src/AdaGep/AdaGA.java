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
	public AdaGepInitializer initializer = null;				// 初始种群产生器
	public SelectionOperator selectionOperator = null;	// 选择算子
	public List mutationOperators = new ArrayList();	// 所有的变异算子 (每个染色体都要经历每个变异算子的操作)
	public List crossoverOperators = new ArrayList();	// 所有的交叉算子 (每组染色体都要经历每个交叉算子的操作)
	public AdaDecoder decoder;								// 解码器
	public Evaluator evaluator;							// 评价器
	public Stopper stopper;								// 停止器
	public Fitness fitnessFunction;				// 适应度函数 
	
	public int generation = 0;							// 当前遗传代数
	public Population population;						// 当前种群
	public Population populationMap;                    //当前种群的映射表
	
	public Chromosome bestChromosome;					// 最优个体
	public Protein bestProtein;							// 最优个体的问题空间表达
	public double bestFitness = -200;					// 最优个体的适应度
	public int bestGeneration;							// 最优个体的出现的代数
	
	public double lastBestFitness = -300;				// 前一个最优适应度

	public long startTime;								// 运行开始的时间
	public long stopTime;								// 运行结束的时间
	public long bestTime;								// 当前最优个体的时间

	public List averageFitnessHistory = new ArrayList();// 平均适应度的变化历史
	public List bestFitnessHistory = new ArrayList();	// 最高适应度的变化历史
	*/
	
	public Population populationMap;                    //当前种群的映射表
	public AdaGepInitializer Adainitializer = null;				// 初始种群产生器
	public AdaDecoder Adadecoder;								// 解码器
//	public List mutationOperators = new ArrayList();	// 所有的变异算子 (每个染色体都要经历每个变异算子的操作)
//	public List crossoverOperators = new ArrayList();	// 所有的交叉算子 (每组染色体都要经历每个交叉算子的操作)

	/**
	 * 设置初始种群产生器
	 * @param initializer
	 */
	public void setAdaInitializer(AdaGepInitializer initializer)
	{
		this.Adainitializer = initializer;	
	}


	/**
	 * 添加变异算子
	 * @param mutationOperator
	 */
	public void addAdaMutationOperator(MutationOperator mutationOperator)
	{
		this.mutationOperators.add(mutationOperator);
	}

	/**
	 * 添加交叉算子 (可能有多个)
	 * @param crossoverOperators
	 */
	public void addAdaCrossoverOperator(CrossoverOperator crossoverOperator)
	{
		this.crossoverOperators.add(crossoverOperator);
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
	 * 设置GepMap
	 * @param decoder
	 */

	public Population generateInitialPopulation()
	{
		return null;
	}
	
	
	
	


	/**
	 * 进行遗传计算
	 */
	public void Adarun()
	{
		startTime = System.currentTimeMillis();
		
		// 产生初始种群
		population = Adainitializer.generateInitialPopulation();
		Adainitializer.setAdaDecoder(Adadecoder);
		Adainitializer.setEvaluator(evaluator);
		populationMap = Adainitializer.generateInitialPopulationMap();
		
		
	//	for(int i=0;i<50;i++)
	//		System.out.println(   i+":"+((GepChromosome)populationMap.get(i)).getGenes()  );
		
		
		// 评价当前种群 同时保留最优个体
		double[] fitnesses = evaluate(population);
		
		// 进行进化
		for (; !stopper.canStop(); generation++)
		{
			// 输出调试信息
	//		System.out.println("" + g + " : " + bestFitness + " : " + bestProtein);
			verbose(fitnesses, 0);
		

	//		for(int i=0;i<50;i++)
	//		System.out.println(   i+":"+((GepChromosome)population.get(i)).getGenes()  );
			
			
			// 选择 同时添加上一代最优个体
			population = select(population, fitnesses);
			
			// 变异
			population = mutation(population,populationMap);
			
			
			// 交叉
			population = crossover(population,populationMap);
			
			
	//		population = mutation(population);
	//		population = crossover(population);
			
			
	//		for(int i=0;i<50;i++)
	//			System.out.println(   i+":"+((GepChromosome)population.get(i)).getGenes()  );

			// 评价当前种群 同时保留最优个体
			fitnesses = evaluate(population);
		}
		System.out.println("==============================================");
		System.out.println("" + bestGeneration + ": " + bestFitness + " : " + bestProtein);

		stopTime = System.currentTimeMillis();
	}
	
	/**
	 * 根据适应度选择种群
	 * @param population
	 * @param index
	 * @return
	 */
	private Population select(Population population, double fitnesses[])
	{
		// 根据选择器计算索引
//		for(int i=0;i<50;i++)
//		System.out.println(fitnesses[i]);
		int[] index = selectionOperator.select(fitnesses);

		Population pop = new Population();

		// 添加最优个体
		pop.add(bestChromosome);

		// 根据索引选择
		int size = population.size();
		for (int i=1; i<size; i++)
		{
			pop.add(population.get(index[i]));
		}

		return pop;
	}

	/**
	 * 变异操作
	 * @param population
	 * @return
	 */
	private Population mutation(Population population)
	{
		// 应用每一个变异算子
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
		// 应用每一个变异算子
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
	 * 选择操作
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
	 * 利用评价器对种群进行评价
	 * @return 种群中每一个个体的适应度
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
			
			// 解码
			//______________________________________________________________________________
			//GepMap Block
			
			Protein protein = Adadecoder.decode(chromosome,i,populationMap);
			
			//Protein protein = decoder.decode(chromosome);
			
			// 评价
			double fitness = evaluator.evaluate(protein);
			fitnesses[i] = fitness;
			averageFitness += fitness; 	
			
			// 保留最优个体
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
	 * 调试输出
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
