package AdaGep;

import ga.Protein;

/**
 * 公式
 * 
 * 应该包含一个Expression、一个变量表以及一个常量列表
 */
public class Formula implements Protein
{
	protected Expression tree;		// 表达式
	protected int complex;					// 公式的复杂度
	
	public Formula(Expression tree, int complex)
	{
		this.tree = tree;
		this.complex = complex;
	}
	
	/**
	 * 取得表达式
	 */
	public Expression getExpression()
	{
		return tree;
	}
	
	/**
	 * 返回公式的复杂度 (公式中包含的节点数量)
	 * @return
	 */
	public int getComplex()
	{
		return complex;
	}
	
	public String toString()
	{
		return tree.toString();
	}
}
