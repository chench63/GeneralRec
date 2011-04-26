package gep.num;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * ±‰¡ø
 */
public class NVariable implements NExpression
{
	public static final int arity = 0;
	
	private char code;
	private double value;
	
	public boolean isArity()
	{
		return true;
	}

	public NVariable(char code)
	{
		this.code = code;
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
		throw new IllegalStateException("ADD_CHILD_ERROR");
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public double evaluate()
	{
		return value;
	}
	
	public String toString()
	{
		return "" + code;
	}

	public List getChildren()
	{
		List children = new ArrayList();
		
		return children;
	}
}
