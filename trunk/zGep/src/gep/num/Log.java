package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 自然对数函数表达式
 */
public class Log implements NExpression
{
	public static final char code = 'L';
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
		double answer = Math.log(left.evaluate());
		if (Double.isNaN(answer)) throw new RuntimeException("error on log");
		return answer;
	}
	
	public String toString()
	{
		return "Log[" + left + "]";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		
		return children;
	}
}
