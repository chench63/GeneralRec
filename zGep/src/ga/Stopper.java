package ga;

/**
 * 停止器 提供停止信号的对象
 */
public interface Stopper
{
	/**
	 * 是否能停止
	 * @return
	 */
	public boolean canStop();
}
