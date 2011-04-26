package gep.num;

import ga.Chromosome;
import ga.Protein;
import gep.Expression;
import gep.GEP;
import gep.GepChromosome;
import gep.GepDecoder;

/**
 * 用于数值优化的GEP解码器
 * 
 * 对于解码每一个个体需要做以下几件事情：
 * 1) 调用 reset 重置解码器，准备解码当前个体
 * 2) 调用 setConstantValues 设置当前个体的常量 (常量值是属于每一个个体的)
 * 3) 调用 decode 解码当前个体
 */
public class NDecoder extends GepDecoder
{
	protected double[] constantValues = new double[10];								// 常量值列表 
	protected NVariable[] variables = new NVariable[26];			// 变量列表
	protected NConstant[] constants = new NConstant[10];			// 常量列表

	public NDecoder(GEP gep)
	{
		super(gep);
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
	
	public Protein decode(Chromosome chromosome,int GepMapNum)
	{
		return null;
	}
	
}
