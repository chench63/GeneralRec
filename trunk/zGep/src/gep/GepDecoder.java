package gep;

import ga.Chromosome;
import ga.Decoder;
import ga.Protein;
import gep.num.NExpression;

import java.util.LinkedList;
import java.util.List;

/**
 * ������ֵ�Ż���GEP������
 */
public abstract class GepDecoder implements Decoder
{
	protected GEP gep;
	protected int complex;		// ��ǰ�����Ⱦɫ��ĸ��ӳ̶�(�������ٸ��ڵ�)

	public GepDecoder()
	{}
	
	public GepDecoder(GEP gep)
	{
		this.gep = gep;
	}
	

	/**
	 * ���ص�ǰ��������� code ��ʾ�ı���
	 * @param code
	 * @return
	 */
	protected abstract Expression getVariable(char code);
	
	/**
	 * ���ص�ǰ��������� code ��ʾ�ĳ���
	 * @param code
	 * @return
	 */
	protected abstract Expression getConstant(char code);

	
	/**
	 * ����ָ�������Ӧ�ı��ʽ
	 * @param code
	 * @return
	 */
	protected Expression getExpression(char code)
	{
		Expression expression;
		
		
		
		if (Character.isLowerCase(code))	// �Ǳ���
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
	 * ���������Ϊ���ʽ��  (���������������Ӻ���������)
	 * @param genes
	 * @param start ��ʼλ��
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
		
		// �����Ӻ�����������������
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
	 * ����һ������
	 * @param genes
	 * @param start ��ʼλ��
	 * @return
	 */
	protected Expression decode1(char[] genes, int start)
	{
		// ���ö��������� 
		List queue = new LinkedList();
		
		// ����ʼ�ڵ��������β��
		int index = start;
		char code = genes[index++];
		Expression root = getExpression(code);
		queue.add(root);
		
		// ���ε�������ͷ����Ԫ�ؽ��д���
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
//		// ���ö��������� 
//		List queue = new LinkedList();
//		int geneLength = gep.geneHead;
//		int end =start+geneLength;
//		
//		
//		// ����ʼ�ڵ��������β��
//		int index = start;
//		char code = genes[index++];
//		
//		Expression root = getExpression(code);
//		queue.add(root);
//		
//		// ���ε�������ͷ����Ԫ�ؽ��д���
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
	 * ����һ���ṹ����
	 * @param genes
	 * @param start ��ʼλ��
	 * @return
	 */
	private Expression decode2(char[] genes, int start)
	{
		// TODO ����ṹ���� �Ժ�����
		return null;
	}


	public abstract void reset();

	public abstract Protein decode(Chromosome chromosome);
	
	public String toString()
	{
		return getClass().getName();
	}
}
