package ga;

/**
 * 适应度函数  根据训练数据的所有值, 计算适应度函数
 */
public interface Fitness
{
	/**
	 * 返回该适应度函数返回的最小适应度值   (作为死基因的染色体适应度值) 
	 * @return
	 */
	public abstract double getMinFitness();
}
