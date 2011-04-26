package gep.num;

/**
 * 数值数据集
 */
public class GeneralNDataSet implements NDataSet
{
	protected double[] targets;				// 目标值
	protected double[][] parameters;		// 参数

	/**
	 * 返回训练样本的目标值
	 * @return
	 */
	public double[] getTargets()
	{
		return targets;
	}
	
	/**
	 * 返回训练样本的参数
	 * @return
	 */
	public double[][] getParameters()
	{
		return parameters;
	}
}