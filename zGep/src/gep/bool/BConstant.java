package gep.bool;

import gep.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * 布尔常量表达式
 */
public class BConstant implements BExpression
{
	private char code;
	private boolean value;
	
	public BConstant(char code, boolean value)
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

	public boolean evaluate()
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
