package gep;

/**
 * 表达式工厂
 */
public interface ExpressionFactory
{
	/**
	 * 返回表达式
	 * @return
	 */
	public Expression get(char code);

}