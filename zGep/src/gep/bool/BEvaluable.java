package gep.bool;

/**
 * 布尔可评价
 */
public interface BEvaluable
{
	/**
	 * 评价结果
	 * @return
	 */
	public abstract boolean evaluate(boolean[] values);
}
