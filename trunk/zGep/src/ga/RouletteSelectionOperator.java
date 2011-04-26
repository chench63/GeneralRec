package ga;

/**
 * ���̶�ѡ������
 */
public class RouletteSelectionOperator implements SelectionOperator
{
	private GA ga;

	public RouletteSelectionOperator(GA ga)
	{
		this.ga = ga;
	}

	public int[] select(double[] fitnesses)
	{
		// ����ÿһ�������ѡ�����
		int size = fitnesses.length;
		double sum = 0.0;
		for (int i=0; i<size; i++)
		{
			sum += fitnesses[i];				
		}
		double[] probabilities = new double[size+1]; 
		double s = 0.0;
		for (int i=0; i<size; i++)
		{
			probabilities[i] = s/sum; 
			s += fitnesses[i];
		}
		probabilities[size] = 1.0;

		// ѡ�����
		int[] index = new int[size];
		for (int i=1; i<size; i++)		// ��һ��λ���������Ÿ���
		{
			double p = Math.random();
			
			// TODO ���ԸĽ�Ϊ���ֲ���
			// �ҵ����� probabilities[j] <= p < probabilities[j+1] �� j
			int j;
			for (j=0; j<size; j++)
			{
				if (p<probabilities[j+1]) 
					break;
			}
			
			index[i] = j;
		}
				
		return index;
	}
	
	public String toString()
	{
		return getClass().getName();
	}
}
