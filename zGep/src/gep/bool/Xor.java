package gep.bool;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 与表达式
 */
public class Xor implements BExpression
{
	public static final char code = '^';
	public static final int arity = 2;

	private BExpression left;
	private BExpression right;
	
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
		
		if (right==null)
		{
			right = (BExpression) child;
			return;
		}
		
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public boolean evaluate()
	{
		return left.evaluate() != right.evaluate();
	}

	public String toString()
	{
		return "(" + left + "^" + right + ")";
	}

	public List getChildren()
	{
		List children = new ArrayList();
		children.add(left);
		children.add(right);
		
		return children;
	}
}
