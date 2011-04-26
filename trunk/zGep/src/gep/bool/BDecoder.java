package gep.bool;

import ga.Chromosome;
import ga.Protein;
import gep.Expression;
import gep.GEP;
import gep.GepChromosome;
import gep.GepDecoder;

/**
 * ���ڲ����Ż���GEP������
 */
public class BDecoder extends GepDecoder
{
	protected BVariable[] variables = new BVariable[26];		// �����б�
	protected BConstant[] constants = new BConstant[2];			// �����б�

	public BDecoder(GEP gep)
	{
		super(gep);
	}

	/**
	 * ���ý�������׼����ʼ������һ������
	 */
	public void reset()
	{
		complex = 0;
		variables = new BVariable[26];
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
		// �����ý�����
		reset();
		
		// �ֱ����ÿһ������
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
