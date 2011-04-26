package regression;

import ga.*;
import gep.*;
import gep.export.*;
import gep.num.*;

import java.util.Date;
import java.util.Random;


/**
 * ·ûºÅ»Ø¹é
 */
public class Regression implements Runnable
{
	private NGEP gep = new NGEP();
	private static int Generation =0;
	private static int MidGeneration =0;
	private static double testNum =0;
	private static double testOverNum =0;
	private static double testMidNum =0;
	private static double sumFitness =0.0;
	
	
	public static long startTime = 0;
	public static long endTime =0;

	public Regression(NGEP gep)
	{
		this.gep = gep;
	}

	public  static synchronized void setInitialFitness(NGEP gep)
	{
		testNum++;
		sumFitness+=gep.bestFitness;
		System.out.println("====================BasicGep====================");
		System.out.println("This BestFitness is :"+sumFitness/testNum);
		System.out.println("TTestNum :"+testNum);
	}
	
	
	public static synchronized void setGeneration(GEP gep)
	{		
		if(gep.bestGeneration < 100)
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
		System.out.println("====================BasicGep====================");
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

			//			gep.setFunctionSet("+-*/~QELSCT+-*/~QELSCT+-*/~QELSCT");

			gep.setFunctionSet("+-*/+-*/");
//			gep.setFunctionSet("+-*/~QESCT");
//			gep.setFunctionSet("+-*/~+-*/~+-*/~+-*/~");
			
//			gep.setVariableSet("abcde");
			gep.setVariableSet("aaaaaaaaaa");
//			gep.setVariableSet("a");
//			gep.setVariableSet("aaaaaabbbbb");
			
//			gep.setConstantSet("0123456789");
//			gep.setConstantSet("0");
	
			gep.setLinkFunction('+');
			
			gep.setGeneNumber(3);
			gep.setGeneHead(10);

			gep.setInitializer(new GepInitializer(gep, 100));
			gep.setSelectionOperator(new TournamentSelectionOperator(gep, 3));

			gep.addMutationOperator(new GepMutationOperator(gep, 0.02));
			
			gep.addMutationOperator(new GepInsertSequenceOperator(gep, 0.01, new int[]{1,2,3}));
			gep.addMutationOperator(new GepRootInsertSequenceOperator(gep, 0.01, new int[]{1,2,3}));
			gep.addMutationOperator(new GepGeneTranspositionOperator(gep, 0.02));
			
			gep.addCrossoverOperator(new GepOnePointCrossoverOperator(gep, 0.05));
			gep.addCrossoverOperator(new GepTwoPointCrossoverOperator(gep, 0.05));
			gep.addCrossoverOperator(new GepGeneCrossoverOperator(gep, 0.05));
	
			Decoder decoder = new NDecoder(gep); 
			gep.setDecoder(decoder);
	
			NDataSet dataSet = new RegressionDataSet();
//			NDataSet dataSet = new FileNDataSet("e:/temp/1.txt", 2, 1, 25);
//			Fitness fitness = new AbsoluteErrorFitness(dataSet, 100);
			Fitness fitness = new RelativeErrorFitness(dataSet, 100);
//			Fitness fitness = new CorrelationCoefficientFitness(dataSet);
			gep.setFitnessFunction(fitness);
			Evaluator evaluator = new NEvaluator(fitness, dataSet);
			gep.setEvaluator(evaluator);
	
			Stopper stopper = new PrecisionStopper(gep,0.01,1000);
//			Stopper stopper = new MaxGenerationStopper(gep, 1); 
//			Stopper stopper = new ManualStopper(gep); 
//			Stopper stopper = new TimeStopper(3000); 
			gep.setStopper(stopper);
			
			gep.run();
			setGeneration(gep);
//			setInitialFitness(gep);
			
//			TextExporter exporter = new MathematicaTextExporter();
//			System.out.println("Mathematica: " + exporter.export((Formula) gep.getBestProtein()));
			
			//================================================================
//			Reporter reporter = new GepReporter();
//			reporter.report(gep, now);
		
//			Population population = gep.population;
//			Chromosome chromosome = population.get(0);
//			Formula formula = (Formula) decoder.decode(chromosome);
			
			Formula formula = (Formula) gep.getBestProtein();
			
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
		Regression.startTime = System.currentTimeMillis();
		for(int i=0;i<100;i++){
		NGEP gep = new NGEP();
		Regression regression = new Regression(gep);

		Thread worker = new Thread(regression);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
		}
	}
}

class RegressionDataSet extends GeneralNDataSet
{
	private final int SIZE = 10;

	public RegressionDataSet()
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

			//targets[i] = Math.PI * a * a;
//			targets[i] = a*a*a*a*a*a*a + 7*a*a*a*a*a*a + 6*a*a*a*a*a+a*a*a*a+4*a*a*a+3*a*a+2*a+1;
//			targets[i] = a*a*a + a ;
//			targets[i] = a*a*a*a + a*a ;
//			targets[i] =  7*a*a*a*a*a*a + 6*a*a*a*a*a+5*a*a*a*a+4*a*a*a+3*a*a+2*a+1;
			targets[i]= a*a*a*a*a-2*a*a*a+a;
//			targets[i]=a*a*a*a*a*a + 6*a*a*a*a*a+a*a*a*a+4*a*a*a+3*a*a+2*a+1;
		}
	}
}
