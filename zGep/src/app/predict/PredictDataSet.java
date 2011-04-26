package app.predict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ga.GepException;
import gep.num.GeneralNDataSet;

/**
 * 时间序列的数据集
 * 
 * 直接预测法中 input = history
 * 微分方程预测法中则是 input = (rank+1)  
 */
public class PredictDataSet extends GeneralNDataSet
{
	private int input;			// 数据中输入部分的长度
	private int future;			// 预测未来的长度
	private int begin, end;		// 训练数据的范围

	public PredictDataSet(String file, int input, int future, int begin, int end) throws GepException
	{
		this.input = input;
		this.future = future;
		this.begin = begin;
		this.end = end;
		
		load(file);
	}

	/**
	 * 从文件装载预测数据集
	 * @throws GepException
	 */
	private void load(String file) throws GepException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			reader.mark(10000);
			String line = reader.readLine();
			reader.reset();
		
			double[] data = new double[1000];
			int column = 0;
			for (StringTokenizer t=new StringTokenizer(line); t.hasMoreTokens(); column++)
			{
				String token = t.nextToken();
				data[column] = Double.parseDouble(token);
			}
			int output = column - input;
			if (future>output) throw new GepException("没有足够的未来数据"); 
			output = future;

			List parameterList = new ArrayList();
			List targetList = new ArrayList();
			for (; ; )
			{
				line = reader.readLine();
				if (line==null) break;
				StringTokenizer t = new StringTokenizer(line);
				
				double[] parameter = new double[input];
				for (int i=0; i<input; i++)
				{
					parameter[i] = Double.parseDouble(t.nextToken());
				}
				parameterList.add(parameter);
				
				for (int i=0; i<output; i++)
				{
					targetList.add(new Double(t.nextToken()));
				}
			}
		
			int size = end-begin;
			parameters = new double[size][];
			targets = new double[size*output];
			int k = begin;
			int s = 0;
			for (int i=0; i<size; i++, k++)
			{
				double[] parameter = (double[]) parameterList.get(k);
				parameters[i] = parameter;

				for (int j=0; j<output; j++)
				{
					targets[s++] = ((Double) targetList.get(k*output+j)).doubleValue();
				}
			}
		}
		catch (Exception e)
		{
			throw new GepException("Load ode data error!", e);
		}
		finally
		{
			if (reader != null) try { reader.close(); } catch (Exception e) {}
		}
	}
}
