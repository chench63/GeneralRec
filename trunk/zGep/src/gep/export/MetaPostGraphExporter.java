package gep.export;

import ga.GepException;
import gep.Expression;
import gep.Formula;

/**
 * 输出数据文件
 */
public class MetaPostGraphExporter implements GraphExporter
{
	private Convertor convertor = new Convertor();
	private GraphLayouter layouter = new DefaultGraphLayouter(25, 25);
	private Outputter outputter = new MetaPostOutputter();
	
	/** 输出到日志文件 */
	public void export(Formula formula, int id) throws GepException
	{
		Expression expression = formula.getExpression();
		Node root = convertor.convert(expression);
		layouter.layout(root);
		outputter.output(root, id);
	}

	/** 输出到指定文件 */
	public void export(Formula formula, String file) throws GepException
	{
		Expression expression = formula.getExpression();
		Node root = convertor.convert(expression);
		layouter.layout(root);
		outputter.output(root, file);
	}
}
