package gep;

import ga.Chromosome;
import ga.MutationOperator;

/**
 * 根插串算子
 */
public class GepRootInsertSequenceOperator extends MutationOperator
{
	private GEP gep;
	private int[] lengthes;			// 串的长度可选值
	
	public GepRootInsertSequenceOperator(GEP gep, double probability, int[] lengthes)
	{
		super(probability);
		this.gep = gep;
		this.lengthes = lengthes;
	}

	public Chromosome mutate(Chromosome chromosome)
	{
		int geneNumber = gep.getGeneNumber();
		int geneHead = gep.getGeneHead();
		int geneLength = gep.getGeneLength();

		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		char[] s = genes.toCharArray();					// 先从父代复制一份
		
		// 针对每一个基因进行操作
		for (int g=0; g<geneNumber; g++)
		{
			if (!checkProbability()) continue;	// 如果概率不满足，继续下一个基因
			int base = g*geneLength;					// 基因开始的位置
	
			// 产生插串长度、源串位置和目标位置
			int length = lengthes[(int) (Math.random() * lengthes.length)];
			int source = (int) (Math.random() * geneHead);
			int target = 0; 
			
			// 从source位置开始向后找，找到第一个函数
			int m = base+source;
			int n = base+geneHead;
			for (; m<n; m++)
			{
				if (gep.isFunction(p[m])) break;
			}
			if (m>=n) continue; 		// 如果没有找到，继续下一个基因
			source = m-base;
			
			// 挪出长度为length的空间
			m = base+target+length;
			n = base+target;
			for (int i=0; i<geneHead-target-length; i++)
			{
				s[m++] = p[n++];
			}
			
			// 将选择的源串插入到目标位置
			m = base+target;
			n = base+source;
			for (int i=0; i<length; i++)
			{
				s[m++] = p[n++];
			}
		}
		
		return new GepChromosome(new String(s));
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName() + "(");
		buffer.append("probability:" + probability);
		buffer.append(";lengthes:" + lengthes[0]);
		for (int i=1; i<lengthes.length; i++)
		{
			buffer.append(',');
			buffer.append(lengthes[i]);
		}
		buffer.append(")");
		
		return buffer.toString();
	}
}
