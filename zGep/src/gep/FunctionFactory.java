package gep;

/**
 * 表达式工厂(工厂方法模式)
 */
public interface FunctionFactory extends ExpressionFactory
{
	/**
	 * 返回表达式的代码
	 */
	public char getCode();

	/**
	 * 返回表达式的目数(即有几个操作数)
	 */
	public int getArity();

}
