package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 加法表达式
 */
public class Plus implements NExpression
{
	public static final char code = '+';
	public static final int arity = 2;
	
	private int arityTemp = 0;
	
	private NExpression left;
	private NExpression right;
	
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
		
		if (right==null)
		{
			right = (NExpression) child;
			arityTemp++;
			return;
		}
		
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public double evaluate()
	{
		return left.evaluate() + right.evaluate();
	}
	
	public String toString()
	{
		return "(" + left + ")+(" + right + ")";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		children.add(right);
		
		return children;
	}
}
