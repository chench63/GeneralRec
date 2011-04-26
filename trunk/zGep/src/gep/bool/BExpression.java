package gep.bool;

import gep.Expression;

/**
 * 布尔表达式
 */
public interface BExpression extends Expression
{
	/**
	 * 计算表达式的值 
	 */
	public boolean evaluate();
}
