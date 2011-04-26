package gep.export;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import ga.GepException;

/**
 */
public class MetaPostOutputter implements Outputter
{
	public void output(Node root, int id) throws GepException
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter("d:/work/zGEP/data/figure.mp", true));
			
			writer.println();
			writer.println();
			writer.println("%=======================================================");
			writer.println("%" + id);
			writer.println("beginfig(" + id + ")");
			writer.println("  defaultfont:=\"cmtt10\";");
			writer.println();
			
			circleit(root, writer);
			writer.println();
			
			axis0(root, writer);
			writer.println();
			axis1(root, null, writer);
			writer.println();
			
			drawboxed(root, writer);
			writer.println();
			
			drawarrow(root, null, writer);
			writer.println();
				
			writer.println("endfig;");
		}
		catch (Exception e)
		{
			throw new GepException("产生输出文件错误", e);
		}
		finally
		{
			if (writer!=null) try { writer.close(); } catch (Exception e) {}
		}
	}
	
	public void output(Node root, String file) throws GepException
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter(file));
			
			writer.println("%prologues:=1;");
			writer.println();
			writer.println("input boxes");
			writer.println();
			writer.println("beginfig(1)");
			writer.println("defaultfont:=\"cmtt10\";");
			writer.println();
			
			circleit(root, writer);
			writer.println();
			
			axis0(root, writer);
			writer.println();
			axis1(root, null, writer);
			writer.println();
			
			drawboxed(root, writer);
			writer.println();
			
			drawarrow(root, null, writer);
			writer.println();
				
			writer.println("endfig;");
			writer.println();
			writer.println("end");
		}
		catch (Exception e)
		{
			throw new GepException("产生输出文件错误", e);
		}
		finally
		{
			if (writer!=null) try { writer.close(); } catch (Exception e) {}
		}
	}
	
	private final void circleit(Node node, PrintWriter writer) throws GepException
	{
		final MessageFormat format = new MessageFormat("  circleit.node{0}(\"{1}\");");
		
		writer.println(format.format(new Object[]{""+node.id, label(node.label)}));
		
		List children = node.children;
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			circleit(child, writer);
		}
	}
	
	private final void axis0(Node node, PrintWriter writer) throws GepException
	{
		final MessageFormat format = new MessageFormat("  node{0}.n - node{0}.s = (0, 12);");
		final MessageFormat format2 = new MessageFormat("  node{0}.e - node{0}.w = (12, 0);");
		
		writer.println(format.format(new Object[]{""+node.id}));
		writer.println(format2.format(new Object[]{""+node.id}));
		
		List children = node.children;
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			axis0(child, writer);
		}
	}

	private final void axis1(Node node, Node parent, PrintWriter writer) throws GepException
	{
		final MessageFormat format = new MessageFormat("  node{0}.c = node{1}.c + ({2},{3});");
		
		if (parent!=null)
		{
			writer.println(format.format(new Object[]{""+node.id, ""+parent.id, ""+node.x, ""+node.y}));
		}
		
		List children = node.children;
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			axis1(child, node, writer);
		}
	}

	/** 给节点画框 */
	private final void drawboxed(Node node, PrintWriter writer) throws GepException
	{
		final MessageFormat format = new MessageFormat("  drawboxed(node{0});");
		
		writer.println(format.format(new Object[]{""+node.id}));
		
		List children = node.children;
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			drawboxed(child, writer);
		}
	}

	/** 在父子节点之间画箭头 */
	private final void drawarrow(Node node, Node parent, PrintWriter writer) throws GepException
	{
		final MessageFormat format = new MessageFormat("  draw node{0}.s -- node{1}.n;");
		
		if (parent!=null)
		{
			writer.println(format.format(new Object[]{""+parent.id, ""+node.id}));
		}
		
		List children = node.children;
		for (Iterator i=children.iterator(); i.hasNext(); )
		{
			Node child = (Node) i.next();
			drawarrow(child, node, writer);
		}
	}

	private final String label(char c)
	{
		switch(c)
		{
			case '~': return "-";
//			case '+': return "\\+";
		}
		return Character.toString(c);
	}
}
