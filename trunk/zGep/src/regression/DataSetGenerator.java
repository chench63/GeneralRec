package regression;

import java.text.*;

/**
 * ���ɲ�������
 */
public class DataSetGenerator
{
	public static void main(String[] args)
	{
		Format format = new MessageFormat("{0,number,' '    0.00000} {1,number,' '    0.00000}");
		
		for (int i=0; i<100; i++)
		{
			double r = i / 10.0;
			double s = Math.PI * r * r;
			
			System.out.println(format.format(new Object[]{new Double(r), new Double(s)}));
		}
	}
}
