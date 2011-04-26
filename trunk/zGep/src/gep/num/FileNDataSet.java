package gep.num;

import ga.GepException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 文件数据集
 */
public class FileNDataSet extends GeneralNDataSet
{
	private int begin, end;				// 训练数据的范围
	private int targetColumn;			// 目标列 

	public FileNDataSet(String file, int targetColumn, int begin, int end) throws GepException
	{
		this.begin = begin;
		this.end = end;
		this.targetColumn = targetColumn;

		load(file);
	}

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

			List targetsList = new ArrayList();
			List parametersList = new ArrayList();
			for (; ; )
			{
				line = reader.readLine();
				if (line==null) break;
				StringTokenizer t = new StringTokenizer(line);
				
				double[] sample = new double[column-1];
				for (int i=0, j=0; i<column; i++)
				{
					if (i==targetColumn)
					{
						targetsList.add(new Double(t.nextToken()));
						continue;
					}
					sample[j++] = Double.parseDouble(t.nextToken());
				}
				parametersList.add(sample);
			}
		
			int size = end-begin;
			targets = new double[size];
			parameters = new double[size][];
			int j = begin;
			for (int i=0; i<size; i++, j++)
			{
				Double target = (Double) targetsList.get(j);
				targets[i] = target.doubleValue();
				
				double[] sample = (double[]) parametersList.get(j);
				parameters[i] = sample;
			}
		}
		catch (Exception e)
		{
			throw new GepException("Load numerical data error!", e);
		}
		finally
		{
			if (reader != null) try { reader.close(); } catch (Exception e) {}
		}
	}
}
