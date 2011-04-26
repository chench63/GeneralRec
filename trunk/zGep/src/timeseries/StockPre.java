package timeseries;



import ga.Evaluator;
import ga.GepException;
import ga.ManualStopper;
import ga.Reporter;
import ga.Stopper;
import ga.TournamentSelectionOperator;
import gep.GEP;
import gep.GepGeneCrossoverOperator;
import gep.GepGeneTranspositionOperator;
import gep.GepInitializer;
import gep.GepInsertSequenceOperator;
import gep.GepMutationOperator;
import gep.GepOnePointCrossoverOperator;
import gep.GepReporter;
import gep.GepRootInsertSequenceOperator;
import gep.GepTwoPointCrossoverOperator;
import gep.num.AbsoluteErrorFitness;
import gep.num.CorrelationCoefficientFitness;
import gep.num.RelativeErrorFitness;
import gep.num.NDataSet;
import gep.num.NDecoder;
import gep.num.NFitness;
import gep.num.NGEP;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

import app.predict.FiboPredicEvaluator;
import app.predict.PredictDataSet;

/**
 * fibonacci∆Ωª¨‘§≤‚∑®
 */
public class StockPre implements Runnable
{
	
	public void run()
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(new FileWriter("d:/direct.txt"));
//			for (int i=0; i<5; i++)
			{
				GEP gep = runOnce();
				writer.println("" + gep.generation + ": " + gep.bestFitness + ": " + gep.getBestProtein());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
		finally
		{
			if (writer!=null) try {writer.close();} catch (Exception e) {}
		}
		
	}

	public GEP runOnce() throws GepException
	{
		NGEP gep = new NGEP();
		
		gep.setFunctionSet("~+-*/~+-*/~+-*/~+-*/~+-*/~+-*/~+-*/~+-*/~+-*/~+-*/");
		gep.setVariableSet("abcdefghijklabcdefghijkl");
		//gep.setConstantSet("0123456789");

		gep.setLinkFunction('+');

		gep.setGeneNumber(5);
		gep.setGeneHead(10);

		gep.setInitializer(new GepInitializer(gep, 50));
		gep.setSelectionOperator(new TournamentSelectionOperator(gep, 4));

		gep.addMutationOperator(new GepMutationOperator(gep, 0.044));
		gep.addMutationOperator(new GepInsertSequenceOperator(gep, 0.1, new int[] { 1, 2, 3 }));
		gep.addMutationOperator(new GepRootInsertSequenceOperator(gep, 0.1, new int[] { 1, 2, 3 }));
		gep.addMutationOperator(new GepGeneTranspositionOperator(gep, 0.1));

		gep.addCrossoverOperator(new GepOnePointCrossoverOperator(gep, 0.4));
		gep.addCrossoverOperator(new GepTwoPointCrossoverOperator(gep, 0.2));
		gep.addCrossoverOperator(new GepGeneCrossoverOperator(gep, 0.1));

		gep.setDecoder(new NDecoder(gep));

		String file = "E:/Myeclipse/Original zGEP/data/stock/Misunspot.txt";
		NDataSet dataSet = new PredictDataSet(file, 5, 1, 1, 84);
		

//		NFitness fitnessFunction = new AbsoluteErrorFitness(dataSet,100);
		NFitness fitnessFunction = new CorrelationCoefficientFitness(dataSet);
//		NFitness fitnessFunction =new RelativeErrorFitness(dataSet, 100);;
		
		gep.setFitnessFunction(fitnessFunction);
		

		Evaluator evaluator = new FiboPredicEvaluator(fitnessFunction, dataSet);		

		gep.setEvaluator(evaluator);


		Stopper stopper = new ManualStopper(gep);
		gep.setStopper(stopper);

		gep.run();
		
		Date now = new Date();

		Reporter reporter = new GepReporter();
		reporter.report(gep, now);

		return gep;
	}


	public static void main(String[] args) throws Exception
	{
		StockPre sunspot = new StockPre();

		Thread worker = new Thread(sunspot);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
	}
}

