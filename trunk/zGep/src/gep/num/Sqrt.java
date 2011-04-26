package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 平方根函数表达式
 */
public class Sqrt implements NExpression
{
	public static final char code = 'Q';
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
		double answer = Math.sqrt(left.evaluate());
		if (Double.isNaN(answer)) throw new RuntimeException("error on sqrt");
		return Math.sqrt(answer);
	}
	
	public String toString()
	{
		return "Sqrt[" + left + "]";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);

		return children;
	}
}
