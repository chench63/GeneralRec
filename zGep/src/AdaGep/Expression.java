package AdaGep;

import java.util.List;

/**
 * 表达式
 */
public interface Expression
{
	/**
	 * 返回代码
	 * @return
	 */
	public char getCode();

	/**
	 * 返回表达式的目数(即有几个操作数)
	 */
	public int getArity();
	
	/**
	 * 添加子表达式(操作数)。按从左到右的顺序 
	 */
	public void addChild(Expression child);
	
	/**
	 * 返回该节点的所有子节点
	 * @return
	 */
	public List getChildren();
}