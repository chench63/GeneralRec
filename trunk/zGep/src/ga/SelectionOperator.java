package ga;

/**
 * ѡ������
 */
public interface SelectionOperator
{
	/**
	 * ѡ����� (������Ӧ�ȣ�����Ӧ��ѡ��ĸ��������)
	 * @param population
	 * @return
	 */
	public int[] select(double[] fitnesses);
}
