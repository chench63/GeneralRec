package AdaGep;

import ga.Chromosome;
import AdaGep.MutationOperator;
import gep.GepChromosome;
import AdaGep.*;

/**
 * �崮����
 */
public class AdaGepInsertSequenceOperator extends MutationOperator
{
	private AdaGep gep;
	private AdaGep gepMap;
	private int[] lengthes;			// ���ĳ��ȿ�ѡֵ
	
	public AdaGepInsertSequenceOperator(AdaGep gep, double probability, int[] lengthes)
	{
		super(probability);
		this.gep = gep;
		this.lengthes = lengthes;
	}
	
	
	public AdaGepInsertSequenceOperator(AdaGep gep,AdaGep gepMap, double probability, int[] lengthes)
	{
		super(probability);
		this.gep = gep;
		this.gepMap=gepMap;
		this.lengthes = lengthes;
	}
	

	public Chromosome mutate(Chromosome chromosome)
	{
		int geneNumber = gep.getGeneNumber();
		int geneHead = gep.getGeneHead();
		int geneLength = gep.getGeneLength();

		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		char[] s = genes.toCharArray();					// �ȴӸ�������һ��

		// ���ÿһ��������в���
		for (int g=0; g<geneNumber; g++)
		{
			if (!checkProbability()) continue;	// ������ʲ����㣬������һ������
			int base = g*geneLength;					// ����ʼ��λ��
			
			// �����崮���ȡ�Դ��λ�ú�Ŀ��λ��
			int length = lengthes[(int) (Math.random() * lengthes.length)];
			int source = (int) (Math.random() * (geneLength-length));
			int target = (int) (Math.random() * (geneHead-1-length) + 1); 	// ��֤һ������������
			
			// Ų������Ϊlength�Ŀռ�
			int m = base+target+length;
			int n = base+target;
			for (int i=0; i<geneHead-target-length; i++)
			{
				s[m++] = p[n++];
			}
			
			// ��ѡ���Դ�����뵽Ŀ��λ��
			m = base+target;
			n = base+source;
			for (int i=0; i<length; i++)
			{
				s[m++] = p[n++];
			}
		}
		
		return new GepChromosome(new String(s));
	}
	
	
	public Chromosome mutate(Chromosome chromosome,Chromosome chromosomeMap)
	{
		int geneNumber = gep.getGeneNumber();
		int geneHead = gep.getGeneHead();
		int geneLength = gep.getGeneLength();

		String genes = ((GepChromosome) chromosome).getGenes();
		char[] p = genes.toCharArray();
		char[] s = genes.toCharArray();					// �ȴӸ�������һ��

		// ���ÿһ��������в���
		for (int g=0; g<geneNumber; g++)
		{
			if (!checkProbability()) continue;	// ������ʲ����㣬������һ������
			int base = g*geneLength;					// ����ʼ��λ��
			
			// �����崮���ȡ�Դ��λ�ú�Ŀ��λ��
			int length = lengthes[(int) (Math.random() * lengthes.length)];
			int source = (int) (Math.random() * (geneLength-length));
			int target = (int) (Math.random() * (geneHead-1-length) + 1); 	// ��֤һ������������
			
			// Ų������Ϊlength�Ŀռ�
			int m = base+target+length;
			int n = base+target;
			for (int i=0; i<geneHead-target-length; i++)
			{
				s[m++] = p[n++];
			}
			
			// ��ѡ���Դ�����뵽Ŀ��λ��
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
