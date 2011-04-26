package gep.export;

import java.util.Iterator;
import java.util.List;

/**
 * 默认的布局器
 * 每一颗子树都占用的宽度(width)是由最左边的子代节点和最右边的子代节点决定；
 * 任意两棵子树之间有一定的间隔(space)
 */
public class DefaultGraphLayouter implements GraphLayouter
{
	private double space;			// 间隔, 两个水平节点之间的最短间隔
	private double height;			// 层高

	public DefaultGraphLayouter()
	{
		this.space = 25;
		this.height = 25;
	}

	public DefaultGraphLayouter(double space, double height)
	{
		this.space = space;
		this.height = height;
	}

	public void layout(Node root)
	{
		layout1(root);
		root.x = 0.0;
		root.y = 0.0;
	}
	
	/** 进行布局 自下而上进行 */
	private final void layout1(Node node)
	{
		List children = node.children;
		
	// 叶节点
		if (children.size()==0) return;
		
	// 非叶节点
		// 分别布局每一个子树
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			layout1(child);
		}

		// 计算树宽度 并调整x坐标为从第一个子树的左边界的偏移量
		node.width = ((Node) children.get(0)).width;
		for (int i=1; i<children.size(); i++)
		{
			node.width += space;

			Node child = (Node) children.get(i);
			child.x += node.width;
			
			node.width += child.width; 
		}
		
		// 计算x坐标
		node.x = (((Node) children.get(0)).x + ((Node) children.get(children.size()-1)).x) / 2;
		for (int i=0; i<children.size(); i++)
		{
			Node child = (Node) children.get(i);
			child.x = child.x - node.x;
			child.y = -height;
		}
	}
}
