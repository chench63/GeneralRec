package AdaGep;

import ga.Chromosome;
import ga.Population;
import ga.Decoder;
import ga.Protein;
import gep.Expression;
import AdaGep.AdaGep;
import gep.GEP;
import gep.GepDecoder;
import gep.GepChromosome;
import gep.num.NConstant;
import gep.num.NExpression;
import gep.num.NFormula;
import gep.num.NVariable;

import java.util.LinkedList;
import java.util.List;

public class AdaDecoder extends GepDecoder
{
	protected AdaGep gepMap;
	
	protected double[] constantValues = new double[10];				// ����ֵ�б� 
	protected NVariable[] variables = new NVariable[26];			// �����б�
	protected NConstant[] constants = new NConstant[10];			// �����б�

	
	
	public AdaDecoder(AdaGep gep)
	{
		super(gep);
	}
	
	public AdaDecoder(AdaGep gep,AdaGep gepMap)
	{
		super(gep);
		this.gepMap=gepMap;
		
	}
	

	/**
	 * ���ó���ֵ
	 * @param values
	 */
	public void setConstantValues(double[] values)
	{
		this.constantValues = values;
	}

	/**
	 * ���ý�������׼����ʼ������һ������
	 */
	public void reset()
	{
		complex = 0;
		variables = new NVariable[26];
		constants = new NConstant[10];
	}

	/**
	 * ȡ��ָ������ı���
	 * �ڽ���һ������Ĺ����У���ͬһ���룬ʼ�շ���ͬһ������
	 * @param code
	 * @return
	 */
	public Expression getVariable(char code)
	{
		int index = code - 'a';
		
		if (variables[index]==null)
		{
			variables[index] = new NVariable(code);
		}
		return variables[index];
	}

	public Expression getConstant(char code)
	{
		final double[] v = 
			//new double[]{0.1315, 0.2128, 0.3443, 0.5571, 0.9015, 1.4588, 2.3605, 3.8195, 6.1804, 10.0007};
			new double[]{0,1,2,3,4,5,6,7,8,9};
			
		int index = code - '0';
		if (constants[index]==null)
		{
			// TODO ����ֱ�������˳���
//			constants[index] = new NConstant(code, constantValues[index]);
			constants[index] = new NConstant(code, v[index]);

		}
		return constants[index];
	}

	
	
	
	
	/**
	 * ���������Ϊ���ʽ��  (���������������Ӻ���������)
	 * @param genes
	 * @param start ��ʼλ��
	 * @return
	 */
	protected Expression decode0(char[] genes,char[] genesMap)
	{
		
		int geneNumber = gep.getGeneNumber();
		int geneLength = gep.getGeneLength();
		Expression[] expressions = new Expression[geneNumber];
		
		
//		count++;
//		System.out.println("Hi! Start    decode0              _____"+count);
		
		int iTemp=0;
		for (int iScan=0; iScan<geneNumber; iScan++)
		{	
			if(  genesMap[iScan]=='a' )		
				continue;	
				
//			System.out.println(genes.length+"   "+ iTemp +"        "+iScan*geneLength);
			expressions[iTemp] = decode1(genes, iScan*geneLength);
			iTemp++	;
		}
		
		// �����Ӻ�����������������
		int arity = gep.getLinkFunctionArity();
		Expression root = expressions[0]; 
		int index = 1;
		while (index < iTemp )
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
	public static int Count = -1;
	protected Expression decode1(char[] genes, int start)
	{
		// ���ö��������� 
		List queue = new LinkedList();
		int geneLength = gep.geneHead;
		int end =start+geneLength;
		
		
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
			
			if ( arity >= 1 )
			{	
				if(index < end)
					code = genes[index++];
				else
					code = gep.terminals[
					       (int) (  gep.terminals.length*Math.random() )            
					                     ];
				
				if(   ((NExpression)expression).isArity()   )
					continue;
				
				Expression child = getExpression (code);
				queue.add(child);
				expression.addChild(child);
				queue.add(expression);
			}
			
		}
//		Count++;
//		System.out.println((Count/12+1)+"    "+(Count%12+1));
//		System.out.println(root.toString());
		
		complex += index - start;	
		return root;
	}
	
	
	public Protein decode(Chromosome chromosome,int GenMapNum,Population populationMap)
	{
		
		// �����ý�����
		reset();
		
		// �ֱ����ÿһ������
		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		gepMap.populationMap=populationMap;
			
//		System.out.println("Hello  Honey,Start!!    decode     ");
//		System.out.println(GenMapNum+"      "+((GepChromosome) populationMap.get(GenMapNum)).getGenes());
		
		String genesMap = ((GepChromosome) populationMap.get(GenMapNum)).getGenes();		
		char[] q = genesMap.toCharArray();
		
		
		NExpression root = (NExpression) decode0(p,q);
		
		
//		System.out.println("Successfully Invokle!      &Protein decode     ");
		return (Protein) new NFormula(root, complex, variables, constants);
	}
	
	
	
	public Protein decode(Chromosome chromosome)
	{
		// �����ý�����
		reset();
		
		// �ֱ����ÿһ������
		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		
		
		NExpression root = (NExpression) decode0(p);
		
		return (Protein) new NFormula(root, complex, variables, constants);
	}
}
