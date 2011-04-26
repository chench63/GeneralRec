package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 指数函数表达式
 */
public class Exp implements NExpression
{
	public static final char code = 'E';
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
		return Math.exp(left.evaluate());
	}
	
	public String toString()
	{
		return "Exp[" + left + "]";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		
		return children;
	}
}
