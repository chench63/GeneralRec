package gep.num;

/**
 * 数值数据集
 */
public interface NDataSet
{
	/**
	 * 返回样本目标值
	 * @return
	 */
	public abstract double[] getTargets();
	
	/**
	 * 返回样本参数
	 * @return
	 */
	public abstract double[][] getParameters();
}