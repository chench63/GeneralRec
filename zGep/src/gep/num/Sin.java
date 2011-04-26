package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 正弦函数表达式
 */
public class Sin implements NExpression
{
	public static final char code = 'S';
	public static final int arity = 1;
	
	private int arityTemp = 0;
	
	private NExpression left;
	
	public boolean isArity()
	{
		return arity == arityTemp;
	}
	
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
			left = (NExpression) child;
			arityTemp++;
			return; 
		}
		
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public double evaluate()
	{
		return Math.sin(left.evaluate());
	}
	
	public String toString()
	{
		return "Sin[" + left + "]";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		
		return children;
	}
}
