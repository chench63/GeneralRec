package gep.export;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 画出表达式图的节点
 */
public class Node
{
	Expression expression;						// 代表的表达式节点

	int id;										// 唯一的编号
	char label;									// 节点的标签
	double width = 0.0;							// 以该节点为根的子树的宽度
	double x = 0.0;								// 该节点的横坐标(相对于父节点) (计算阶段则相对于子树的最左端点)
	double y = 0.0;								// 该节点的纵坐标(相对于父节点)
	List children = new ArrayList();			// 子节点
}
