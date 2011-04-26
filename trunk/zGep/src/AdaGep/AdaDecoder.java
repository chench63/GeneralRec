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
	
	protected double[] constantValues = new double[10];				// 常量值列表 
	protected NVariable[] variables = new NVariable[26];			// 变量列表
	protected NConstant[] constants = new NConstant[10];			// 常量列表

	
	
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
	 * 设置常量值
	 * @param values
	 */
	public void setConstantValues(double[] values)
	{
		this.constantValues = values;
	}

	/**
	 * 重置解码器，准备开始解码下一个个体
	 */
	public void reset()
	{
		complex = 0;
		variables = new NVariable[26];
		constants = new NConstant[10];
	}

	/**
	 * 取得指定代码的变量
	 * 在解码一个个体的过程中，对同一代码，始终返回同一个变量
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
			// TODO 这里直接设置了常量
//			constants[index] = new NConstant(code, constantValues[index]);
			constants[index] = new NConstant(code, v[index]);

		}
		return constants[index];
	}

	
	
	
	
	/**
	 * 解码基因组为表达式树  (如果多基因，则用连接函数连起来)
	 * @param genes
	 * @param start 开始位置
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
		
		// 用连接函数将它们连接起来
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
	 * 解码一个基因
	 * @param genes
	 * @param start 开始位置
	 * @return
	 */
	public static int Count = -1;
	protected Expression decode1(char[] genes, int start)
	{
		// 采用队列来解码 
		List queue = new LinkedList();
		int geneLength = gep.geneHead;
		int end =start+geneLength;
		
		
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
		
		// 先重置解码器
		reset();
		
		// 分别解码每一个基因
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
		// 先重置解码器
		reset();
		
		// 分别解码每一个基因
		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		
		
		NExpression root = (NExpression) decode0(p);
		
		return (Protein) new NFormula(root, complex, variables, constants);
	}
}
