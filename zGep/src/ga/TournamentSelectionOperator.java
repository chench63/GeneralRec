package ga;

import java.util.Random;

/**
 * 联赛选择算子
 */
public class TournamentSelectionOperator implements SelectionOperator
{
	private GA ga;
	private Random random = new Random();
	private int size;			// 联赛规模

	public TournamentSelectionOperator(GA ga, int size)
	{
		this.ga = ga;
		this.size = size;
	}

	public int[] select(double[] fitnesses)
	{
		int populationSize = fitnesses.length;
		int[] index = new int[populationSize];
		
		for (int i=0; i<populationSize; i++)
		{
			int winner = random.nextInt(populationSize);
			double fitness = fitnesses[winner];
			for (int j=1; j<size; j++)
			{
				int k = random.nextInt(populationSize);
				double f = fitnesses[k];
				
				if (f>fitness)
				{
					winner = k;
					fitness = f;
				}
			}
			
			index[i] = winner;
		}
		
		return index;
	}
	
	public String toString()
	{
		return getClass().getName() + "(size:" + size + ")";		
	}
}
