package gep.num;


/**
 * 通用适应度函数
 */
public abstract class GeneralNFitness implements NFitness
{
	protected double[] targets;			// 样本的目标值
	
	protected GeneralNFitness(NDataSet dataSet)
	{
		this.targets = dataSet.getTargets();
	}
}
