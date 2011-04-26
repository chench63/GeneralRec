package gep.export;

import ga.GepException;

/**
 * 输出器
 */
public interface Outputter
{
	/** 输出到日志文件 */
	public void output(Node root, int id) throws GepException;
	
	public void output(Node root, String file) throws GepException;
}
