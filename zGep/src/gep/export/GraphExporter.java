package gep.export;

import ga.GepException;
import gep.Formula;

/**
 * 图形输出器
 * 将找到的公式以图形的方式输出
 */
public interface GraphExporter
{
	/**
	 * 输出给定公式的图形表示到日志文件
	 * @param formula
	 * @throws GepException
	 */
	public abstract void export(Formula formula, int id) throws GepException;
	
	/**
	 * 输出给定公式的图形表示到指定文件
	 * @param formula
	 * @param file
	 * @throws GepException
	 */
	public abstract void export(Formula formula, String file) throws GepException;
}
