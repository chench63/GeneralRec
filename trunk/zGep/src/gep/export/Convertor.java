package gep.export;

import gep.Expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 将表达式树转换为节点树
 */
public class Convertor
{
	private int id = 0;	
	
	public Node convert(Expression expression)
	{
		id = 0;
		Node node = convert1(expression);
		
		return node;
	}
	
	private Node convert1(Expression expression)
	{
		Node node = new Node();
		node.id = id++;  
		node.expression = expression;
		node.label = expression.getCode();
		
		List subNodes = new ArrayList();
		List subExpressions = expression.getChildren();
		for (Iterator i=subExpressions.iterator(); i.hasNext(); )
		{
			Expression subExpression = (Expression) i.next();
			Node subNode = convert1(subExpression);
			subNodes.add(subNode);
		}
		node.children = subNodes;
		
		return node;
	}
}
