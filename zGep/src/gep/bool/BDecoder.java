package gep.bool;

import ga.Chromosome;
import ga.Protein;
import gep.Expression;
import gep.GEP;
import gep.GepChromosome;
import gep.GepDecoder;

/**
 * 用于布尔优化的GEP解码器
 */
public class BDecoder extends GepDecoder
{
	protected BVariable[] variables = new BVariable[26];		// 变量列表
	protected BConstant[] constants = new BConstant[2];			// 变量列表

	public BDecoder(GEP gep)
	{
		super(gep);
	}

	/**
	 * 重置解码器，准备开始解码下一个个体
	 */
	public void reset()
	{
		complex = 0;
		variables = new BVariable[26];
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
			variables[index] = new BVariable(code);
		}
		return variables[index];
	}

	public Expression getConstant(char code)
	{
		int index = code - 'a';
		if (constants[index]==null)
		{
			constants[index] = new BConstant(code, index!=0);
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
		
		BExpression root = (BExpression) decode0(p);
		
		return new BFormula(root, complex, variables);
	}
	
	public Protein decode(Chromosome chromosome,int GepMapNum)
	{
		return null;
	}

}
