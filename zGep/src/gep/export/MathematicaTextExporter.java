package gep.export;

import java.util.List;

import gep.Expression;
import gep.Formula;
import gep.num.NConstant;

/**
 * 输出Mathematica格式的公式
 */
public class MathematicaTextExporter implements TextExporter
{
	private StringBuffer buffer;

	public String export(Formula formula)
	{
		buffer = new StringBuffer();
		
		buffer.append("ode = y''[x]==(Simplify[");
		export(formula.getExpression());
		buffer.append("]/.{a->x, b->y[x], c->y'[x]})");
		
		return buffer.toString();
	}

	private void export(Expression expression)
	{
		int arity = expression.getArity();
		
		switch (arity)
		{
			case 0: exportTerminal(expression); break;
			case 1: exportUnitaryFuntion(expression); break;
			case 2: exportBinaryFunction(expression); break;
			
			default: throw new RuntimeException("Unsuported ......");
		}
	}

	private void exportBinaryFunction(Expression expression)
	{
		List children = expression.getChildren();
		Expression left = (Expression) children.get(0);
		Expression right = (Expression) children.get(1);

		buffer.append("(");
		export(left);

		char code = expression.getCode();
		switch (code)
		{
			case '+': buffer.append(")+("); break;
			case '-': buffer.append(")-("); break;
			case '*': buffer.append(")*("); break;
			case '/': buffer.append(")/("); break;
			
			default: throw new RuntimeException("Why exception??");
		}

		export(right);
		buffer.append(")");		
	}

	private void exportUnitaryFuntion(Expression expression)
	{
		List children = expression.getChildren();
		Expression left = (Expression) children.get(0);

		char code = expression.getCode();
		switch (code)
		{
			case '~': buffer.append("-("); export(left); buffer.append(")"); return;
			case 'Q': buffer.append("Sqrt"); break;
			case 'L': buffer.append("Log"); break;
			case 'E': buffer.append("Exp"); break;
			case 'S': buffer.append("Sin"); break;
			case 'C': buffer.append("Cos"); break;
			case 'T': buffer.append("Tan"); break;
			case 'A': buffer.append("Abs"); break;
			
			default: throw new RuntimeException("Why exception??");
		}

		buffer.append("[");
		export(left);
		buffer.append("]");		
	}

	private void exportTerminal(Expression expression)
	{
		char code = expression.getCode();
		
		if ('a'<=code && code<='z')
		{
			buffer.append(code);
			return;
		}
		
		if ('0'<=code && code<='9')
		{
			buffer.append(((NConstant) expression).evaluate());
			return;
		}
		
		throw new RuntimeException("Why exception??");
	}
}
