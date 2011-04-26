package ga;

import java.util.Random;

/**
 * ÒÅ´«ÔËËã·û
 */
public abstract class GeneticOperator
{
	protected Random random = new Random();
	protected double probability;		// ¸ÅÂÊ
	
	protected GeneticOperator(double probability)
	{
		this.probability = probability;
	}
	
	protected boolean checkProbability()
	{
		if (random.nextDouble()<probability) return true;
		
		return false;
	}
	
	public String toString()
	{
		return getClass().getName() + "(probability:" + probability + ")";		
	}
}