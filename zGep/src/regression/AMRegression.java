package regression;

import AdaGep.*;
import gep.export.*;
import gep.num.*;

import ga.Decoder;
import ga.SelectionOperator;
import ga.TournamentSelectionOperator;
import ga.Fitness;
import ga.Evaluator;
import ga.Stopper;
import ga.MaxGenerationStopper;
import ga.GepException;
import gep.GepMutationOperator;

import java.util.Date;
import java.util.Random;





/**
 * ��Żع�
 */
public class AMRegression implements Runnable
{
	private MGEP gepMap = new MGEP();
	private MGEP gep = new MGEP();
	public static int Generation =0;
	public static int MidGeneration =0;
	private static double testNum =0;
	private static double testOverNum =0;
	private static double testMidNum =0;
	
	
	
	public static long startTime = 0;
	public static long endTime =0;

	public AMRegression(MGEP gep)
	{
		this.gep = gep;
	}
	
	public synchronized void setGeneration()
	{		
		if(gep.bestGeneration < 10)
		{
			Generation+=gep.bestGeneration;
			testNum++;
		}
		else if(gep.bestGeneration < 1000)
		{
			MidGeneration+=gep.bestGeneration;
			testMidNum++;
		}
		else 
			testOverNum++;
		endTime =  System.currentTimeMillis();
		System.out.println("====================AdaGep====================");
		System.out.println("This Geration is :"+Generation/testNum);
		System.out.println("This MidGeration is :"+(Generation+MidGeneration)/(testNum+testMidNum));
		System.out.println("TTestNum :"+testNum);
		System.out.println("TTestMidNum :"+testMidNum);
		System.out.println("TTestOverNum :"+testOverNum);
		System.out.println("Testtime :"+(endTime - startTime)/1000.0);
	}

	public void run()
	{
		try
		{
			Date now = new Date();

			gepMap.setVariableSet("aaaaaaaaab");
			gepMap.setGeneNumber(6);
			gepMap.setGeneHead(0);
			
	
			
//_________________________________________________________________________________
			gep.setFunctionSet("+-*/+-*/");
//			gep.setFunctionSet("+-*/~QESCT");
//			gep.setFunctionSet("+-*/~+-*/~+-*/~+-*/~");
			
			gep.setVariableSet("a");
			
	//		gep.setConstantSet("0123456789");
//			gep.setConstantSet("0");
	
			gep.setLinkFunction('+');
			
			gep.setGeneNumber(3);
			gep.setGeneHead(10);
	
			gep.setAdaInitializer(new AdaGepInitializer(gep,gepMap, 100));
			gep.setSelectionOperator(new TournamentSelectionOperator(gep, 3));

//			gep.addMutationOperator(new GepMutationOperator(gep, 0.044));
			gep.addAdaMutationOperator(new AdaGepMutationOperator(gep,gepMap, 0.02));
			
			gep.addAdaMutationOperator(new AdaGepInsertSequenceOperator(gep, 0.01, new int[]{1,2,3}));
			gep.addAdaMutationOperator(new AdaGepRootInsertSequenceOperator(gep, 0.01, new int[]{1,2,3}));
			gep.addAdaMutationOperator(new AdaGepGeneTranspositionOperator(gep, 0.02));
			
			gep.addAdaCrossoverOperator(new AdaGepOnePointCrossoverOperator(gep, 0.05));
			gep.addAdaCrossoverOperator(new AdaGepTwoPointCrossoverOperator(gep, 0.05));
			gep.addAdaCrossoverOperator(new AdaGepGeneCrossoverOperator(gep, 0.05));
	
			Decoder decoder = new AdaDecoder(gep,gepMap); 
//			Decoder decoder = new NDecoder(gep); 
			gep.setAdaDecoder(decoder);
	
			NDataSet dataSet = new RegressionDataSet();
//			NDataSet dataSet = new FileNDataSet("e:/temp/1.txt", 2, 1, 25);
//			Fitness fitness = new AbsoluteErrorFitness(dataSet, 100);
			Fitness fitness = new RelativeErrorFitness(dataSet, 100);
//			Fitness fitness = new CorrelationCoefficientFitness(dataSet);
			gep.setFitnessFunction(fitness);
			Evaluator evaluator = new NEvaluator(fitness, dataSet);
			gep.setEvaluator(evaluator);
	
//			Stopper stopper = new MaxGenerationStopper(gep, 300); 
			Stopper stopper = new PrecisionStopper(gep, 0.01,1000); 
//			Stopper stopper = new ManualStopper(gep); 
//			Stopper stopper = new TimeStopper(3000); 
			gep.setStopper(stopper);
			
			gep.Adarun();
			setGeneration();
			
			
			
//			TextExporter exporter = new MathematicaTextExporter();
//			System.out.println("Mathematica: " + exporter.export((Formula) gep.getBestProtein()));
			
			//================================================================
//			Reporter reporter = new GepReporter();
//			reporter.report(gep, now);
		
//			Population population = gep.population;
//			Chromosome chromosome = population.get(0);
//			Formula formula = (Formula) decoder.decode(chromosome);
			
//			Formula formula = (Formula) gep.getBestProtein();
			
//			GraphExporter graphGenerator = new MetaPostGraphExporter();
//			graphGenerator.export(formula, (int) now.getTime() % 30000);
//			graphGenerator.export(formula, "d:/work/zGEP/preview/figure.mp");
		}
		catch (GepException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws GepException
	{
		AMRegression.startTime = System.currentTimeMillis();
		for(int i=0;i<100;i++)		{
		MGEP gep = new MGEP();
		AMRegression regression = new AMRegression(gep);
		
		Thread worker = new Thread(regression);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
		}
	}
}

class AMRegressionDataSet extends GeneralNDataSet
{
	private final int SIZE = 10;

	public AMRegressionDataSet()
	{
		Random random = new Random();

		targets = new double[SIZE];
		parameters = new double[SIZE][];

		for (int i=0; i<SIZE; i++)
		{
			double[] sample = new double[6];
			
			double a = i/10.0*10 - 50.5;
			sample[0] = a;
			
			parameters[i] = sample;

//			targets[i] = Math.PI * a * a;
//			targets[i] =  7*a*a*a*a*a*a + 6*a*a*a*a*a+5*a*a*a*a+4*a*a*a+3*a*a+2*a+1;
			targets[i]= a*a*a*a*a-2*a*a*a+a;
			
		}
	}
}
