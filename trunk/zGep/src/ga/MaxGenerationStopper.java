package ga;

/**
 * 最大代数的停止器
 */
public class MaxGenerationStopper implements Stopper
{
	public GA ga;
	public int maxGeneration;
	
	public MaxGenerationStopper(GA ga, int maxGeneration)
	{
		this.ga = ga;
		this.maxGeneration = maxGeneration;
	}
	
	public boolean canStop()
	{
		if (ga.generation>=maxGeneration) return true;
	
		
		return false;
	}

	public String toString()
	{
		return getClass().getName() + "(maxGeneration:" + maxGeneration + ")";
	}
}
