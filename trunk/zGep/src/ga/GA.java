package ga;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 遗传算法
 */
public abstract class GA
{
	public Initializer initializer = null;				// 初始种群产生器
	public SelectionOperator selectionOperator = null;	// 选择算子
	public List mutationOperators = new ArrayList();	// 所有的变异算子 (每个染色体都要经历每个变异算子的操作)
	public List crossoverOperators = new ArrayList();	// 所有的交叉算子 (每组染色体都要经历每个交叉算子的操作)
	public Decoder decoder;								// 解码器
	public Evaluator evaluator;							// 评价器
	public Stopper stopper;								// 停止器
	public Fitness fitnessFunction;				// 适应度函数 
	
	public int generation = 0;							// 当前遗传代数
	public Population population;						// 当前种群
	
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

	/**
	 * 设置初始种群产生器
	 * @param initializer
	 */
	public void setInitializer(Initializer initializer)
	{
		this.initializer = initializer;	
	}

	/**
	 * 设置选择器
	 * @param selector
	 */
	public void setSelectionOperator(SelectionOperator selectionOperator)
	{
		this.selectionOperator = selectionOperator;
	}

	/**
	 * 添加变异算子
	 * @param mutationOperator
	 */
	public void addMutationOperator(MutationOperator mutationOperator)
	{
		this.mutationOperators.add(mutationOperator);
	}

	/**
	 * 添加交叉算子 (可能有多个)
	 * @param crossoverOperators
	 */
	public void addCrossoverOperator(CrossoverOperator crossoverOperator)
	{
		this.crossoverOperators.add(crossoverOperator);
	}
	
	/**
	 * 设置解码器
	 * @param decoder
	 */
	public void setDecoder(Decoder decoder)
	{
		this.decoder = decoder;
	}
	
	/**
	 * 设置适应度函数
	 * @param fitnessFunction
	 */
	public void setFitnessFunction(Fitness fitnessFunction)
	{
		this.fitnessFunction = fitnessFunction;
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
	 * 设置停止器
	 * @param stopper
	 */
	public void setStopper(Stopper stopper)
	{
		this.stopper = stopper;
	}

	/**
	 * 进行遗传计算
	 */
	public void run()
	{
		startTime = System.currentTimeMillis();
		
		// 产生初始种群
		population = initializer.generateInitialPopulation();	

		// 评价当前种群 同时保留最优个体
		double[] fitnesses = evaluate(population);
		
		// 进行进化
		for (; !stopper.canStop(); generation++)
		{
			// 输出调试信息
//			System.out.println("" + g + " : " + bestFitness + " : " + bestProtein);
			verbose(fitnesses, 0);

			// 选择 同时添加上一代最优个体
			population = select(population, fitnesses);
			
			// 变异
			population = mutation(population);
			
			// 交叉
			population = crossover(population);

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

	/**
	 * 利用评价器对种群进行评价
	 * @return 种群中每一个个体的适应度
	 */
	private double[] evaluate(Population population)
	{
		int size = population.size();
		double[] fitnesses = new double[size];
		double averageFitness = 0.0;
		for (int i=0; i<size; i++)
		{
			Chromosome chromosome = population.get(i);
			
			// 解码
			//______________________________________________________________________________
			//GepMap Block
			//Protein protein = decoder.decode(chromosome,i);
			
			Protein protein = decoder.decode(chromosome);
			
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
	 * 返回最优个体的表现型
	 * @return
	 */
	public Protein getBestProtein()
	{
		return bestProtein;
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

	/**
	 * 报告运行情况
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

//		public List averageFitnessHistory = new ArrayList();// 平均适应度的变化历史
//		public List bestFitnessHistory = new ArrayList();	// 最高适应度的变化历史
	}
}
