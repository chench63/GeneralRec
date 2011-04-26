package ga;

/**
 * 产生初始种群
 */
public interface Initializer
{
	/**
	 * 产生初始种群
	 * @return
	 */
	public Population generateInitialPopulation();
}
