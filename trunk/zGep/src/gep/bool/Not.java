package gep.bool;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 非表达式
 */
public class Not implements BExpression
{
	public static final char code = '!';
	public static final int arity = 1;

	private BExpression left;
	
	public char getCode()
	{
		return code;
	}

	public int getArity()
	{
		return arity;
	}

	public void addChild(Expression child)
	{
		if (left==null)
		{
			left = (BExpression) child;
			return; 
		}
		
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public boolean evaluate()
	{
		return ! left.evaluate();
	}

	public String toString()
	{
		return "(!" + left + ")";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		
		return children;
	}
}
