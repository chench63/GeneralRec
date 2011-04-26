package ga;


public class PrecisionStopper implements Stopper 
{
	public GA ga;
	public double precision;
	public int maxFitness;
	
	public PrecisionStopper(GA ga, double precision,int maxFitness)
	{
		this.ga = ga;
		this.precision = precision;
		this.maxFitness=maxFitness;
	}
	
	public boolean canStop()
	{		
		if (ga.bestFitness + this.precision > maxFitness)
			return true;
	
		if (ga.generation > 3000)
			return true;		
		return false;
	}

	public String toString()
	{
		return getClass().getName() + "(maxGeneration:"  + ")";
	}

}
