package AdaGep;

import ga.GA;
import ga.GepException;
import ga.Reporter;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import gep.GEP;
import gep.GepChromosome;
import AdaGep.*;


/**
 * GEP�ı���������
 */
public class GepReporter implements Reporter
{
	private final String LOG_FILE = "E:/Myeclipse/Original zGEP/gep.log";

	private Date now = new Date();
	private String name;
	
	public GepReporter()
	{
		DateFormat format = new SimpleDateFormat("yyyyMMdd_kkmmss");
	}

	public void report(AdaGA ga, Date now) throws GepException
	{
		AdaGep gep = (AdaGep) ga;
		
		// ���ļ�
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter(LOG_FILE, true));
			DateFormat format1 = new SimpleDateFormat("yyyyMMddkkmmss");
			DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
			writer.println();
			writer.println("=======================================================");
			writer.println(format1.format(now));
			writer.println("------------------------------------------");
			writer.println("Date               : " + format2.format(now));

			ga.report(writer);
		}
		catch (Exception e)
		{
			throw new GepException("���ɱ������", e);
		}
		finally
		{
			if (writer!=null) try { writer.close(); } catch (Exception e) {}
		}

	}

	
	public void report(GA ga, Date now) throws GepException
	{
		GEP gep = (GEP) ga;
		
		// ���ļ�
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter(LOG_FILE, true));
			DateFormat format1 = new SimpleDateFormat("yyyyMMddkkmmss");
			DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
			writer.println();
			writer.println("=======================================================");
			writer.println(format1.format(now));
			writer.println("------------------------------------------");
			writer.println("Date               : " + format2.format(now));

			ga.report(writer);
		}
		catch (Exception e)
		{
			throw new GepException("���ɱ������", e);
		}
		finally
		{
			if (writer!=null) try { writer.close(); } catch (Exception e) {}
		}

	}

}
