package gep.export;

import gep.Formula;

/**
 * 以文本的方式输出公式
 */
public interface TextExporter 
{
	/**
	 * 输出公式的文本表达
	 * @param formula
	 * @return
	 */
	public abstract String export(Formula formula);
}
