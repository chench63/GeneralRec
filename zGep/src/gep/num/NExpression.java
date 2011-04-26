package gep.num;

import gep.Expression;

/**
 * 表达式
 */
public interface NExpression extends Expression
{
	/**
	 * 计算表达式的值 
	 */
	public double evaluate();
	
	public boolean isArity();
}
