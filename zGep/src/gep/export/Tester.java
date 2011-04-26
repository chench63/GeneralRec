package gep.export;

import ga.GepException;

/**
 * 输入一个前缀表达式, 输出其语法树的 MetaPost 图
 */
public class Tester
{
	private GraphLayouter layouter = new DefaultGraphLayouter(25, 25);
	private Outputter outputter = new MetaPostOutputter();
	private int id;
	private int p;
	
	/** 输出结果到文件 */
	private void export(String expression, String file) throws GepException
	{
		id = 0;
		p = 0;
		Node root = build(expression);
		layouter.layout(root);
		outputter.output(root, file);
	}

	private Node build(String expression)
	{
		Node node = new Node();
		node.id = id++;
		node.label = expression.charAt(p);
		int a = Integer.parseInt(expression.substring(p+1, p+2));
		p += 2;
		for (int i = 1; i <= a; i++)
		{
			Node n = build(expression);
			node.children.add(n);
		}
		
		return node;
	}

	public static void main(String args[]) throws GepException
	{
//		if (args.length<2)
//		{
//			System.out.println("USAGE: java gep.export.Tester <expression> <file>");
//			return;
//		}

//		String expression = "E3E1i0+0E3E1i0*0E1i0";
//		String expression = "E3E3E1i0+0E1i0*0E1i0";
//		String expression = "S1*2+2a0b0-2a0c0";
		String expression = "E4S0(0E3E3E1a0+0E1b0*0E3E1a0-0E1c0)0";
		String file = "d:/aaa.mp";
				
		Tester tester = new Tester();
		tester.export(expression, file);
	}
}
