package gep;

import ga.Chromosome;
import ga.Decoder;
import ga.Protein;
import gep.num.NExpression;

import java.util.LinkedList;
import java.util.List;

/**
 * 用于数值优化的GEP解码器
 */
public abstract class GepDecoder implements Decoder
{
	protected GEP gep;
	protected int complex;		// 当前解码的染色体的复杂程度(包含多少个节点)

	public GepDecoder()
	{}
	
	public GepDecoder(GEP gep)
	{
		this.gep = gep;
	}
	

	/**
	 * 返回当前解码过程中 code 表示的变量
	 * @param code
	 * @return
	 */
	protected abstract Expression getVariable(char code);
	
	/**
	 * 返回当前解码过程中 code 表示的常量
	 * @param code
	 * @return
	 */
	protected abstract Expression getConstant(char code);

	
	/**
	 * 返回指定代码对应的表达式
	 * @param code
	 * @return
	 */
	protected Expression getExpression(char code)
	{
		Expression expression;
		
		
		
		if (Character.isLowerCase(code))	// 是变量
		{
			expression = getVariable(code);
			
		}
		else if (Character.isDigit(code))
		{
			expression = getConstant(code);
			
		}
		else
		{
			expression = gep.getFunction(code);
			
		}

		return expression;
	}

	/**
	 * 解码基因组为表达式树  (如果多基因，则用连接函数连起来)
	 * @param genes
	 * @param start 开始位置
	 * @return
	 */
	protected Expression decode0(char[] genes)
	{
		int geneNumber = gep.getGeneNumber();
		int geneLength = gep.getGeneLength();
		Expression[] expressions = new Expression[geneNumber];
		for (int i=0; i<geneNumber; i++)
		{	
			expressions[i] = decode1(genes, i*geneLength);
		}
		
		// 用连接函数将它们连接起来
		int arity = gep.getLinkFunctionArity();
		Expression root = expressions[0]; 
		int index = 1;
		while (index<expressions.length)
		{
			complex++;
			Expression expression = gep.getLinkFunction();
			expression.addChild(root);
			for (int i=1; i<arity; i++)
			{
				expression.addChild(expressions[index++]);
			}
			root = expression;
		}
		
		return root;
	}
	
	/**
	 * 解码一个基因
	 * @param genes
	 * @param start 开始位置
	 * @return
	 */
	protected Expression decode1(char[] genes, int start)
	{
		// 采用队列来解码 
		List queue = new LinkedList();
		
		// 将初始节点推入队列尾部
		int index = start;
		char code = genes[index++];
		Expression root = getExpression(code);
		queue.add(root);
		
		// 依次弹出队列头部的元素进行处理
		while (!queue.isEmpty())
		{
			Expression expression = (Expression) queue.remove(0);
			int arity = expression.getArity();
			
			for (int i=0; i<arity; i++)
			{
				code = genes[index++];
				Expression child = getExpression(code);
				
				expression.addChild(child);
				queue.add(child);
			}
		}
		
		complex += index - start;
		
		return root;
	}
	
//	protected Expression decode1(char[] genes, int start)
//	{
//		// 采用队列来解码 
//		List queue = new LinkedList();
//		int geneLength = gep.geneHead;
//		int end =start+geneLength;
//		
//		
//		// 将初始节点推入队列尾部
//		int index = start;
//		char code = genes[index++];
//		
//		Expression root = getExpression(code);
//		queue.add(root);
//		
//		// 依次弹出队列头部的元素进行处理
//		while (!queue.isEmpty())
//		{
//			Expression expression = (Expression) queue.remove(0);
//			int arity = expression.getArity();
//			
//			
//			if ( arity >= 1 )
//			{				
//				if(index < end)
//					code = genes[index++];
//				else
//					code = gep.terminals[
//					       (int) (  gep.terminals.length*Math.random() )            
//					                     ];
//				
//				if(   ((NExpression)expression).isArity()   )
//					continue;
//				
//				Expression child = getExpression (code);
//				queue.add(child);
//				expression.addChild(child);
//				queue.add(expression);
//			}
//			
//		}
//		
//		complex += index - start;
//		return root;
//	}
	

	/**
	 * 解码一个结构基因
	 * @param genes
	 * @param start 开始位置
	 * @return
	 */
	private Expression decode2(char[] genes, int start)
	{
		// TODO 解码结构基因 以后再做
		return null;
	}


	public abstract void reset();

	public abstract Protein decode(Chromosome chromosome);
	
	public String toString()
	{
		return getClass().getName();
	}
}
