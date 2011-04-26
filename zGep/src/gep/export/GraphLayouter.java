package gep.export;

/**
 * 布局器
 */
public interface GraphLayouter
{
	/**
	 * 布局以root为根的子树
	 * @param root
	 */
	public abstract void layout(Node root);
}
