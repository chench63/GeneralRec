package AdaGep;

import ga.Protein;

/**
 * ��ʽ
 * 
 * Ӧ�ð���һ��Expression��һ���������Լ�һ�������б�
 */
public class Formula implements Protein
{
	protected Expression tree;		// ���ʽ
	protected int complex;					// ��ʽ�ĸ��Ӷ�
	
	public Formula(Expression tree, int complex)
	{
		this.tree = tree;
		this.complex = complex;
	}
	
	/**
	 * ȡ�ñ��ʽ
	 */
	public Expression getExpression()
	{
		return tree;
	}
	
	/**
	 * ���ع�ʽ�ĸ��Ӷ� (��ʽ�а����Ľڵ�����)
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
