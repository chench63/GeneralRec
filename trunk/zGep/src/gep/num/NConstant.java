package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 数值常量表达式
 */
public class NConstant implements NExpression
{
	private char code;
	private double value;
	
	public boolean isArity()
	{
		return true ;
	}
	
	public NConstant(char code, double value)
	{
		this.code = code;
		this.value = value;
	}

	public char getCode()
	{
		return code;
	}

	public int getArity()
	{
		return 0;
	}

	public void addChild(Expression child)
	{
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public double evaluate()
	{
		return value;
	}
	
	public String toString()
	{
		return "" + value;
	}


	public List getChildren()
	{
		List children = new ArrayList();
		
		return children;
	}
}
