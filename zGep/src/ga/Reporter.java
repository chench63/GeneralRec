package ga;

import java.util.Date;

/**
 * 报表生成器
 */
public interface Reporter
{
	/**
	 * 产生报表
	 * @param ga
	 */
	public abstract void report(GA ga, Date now) throws GepException;
}
