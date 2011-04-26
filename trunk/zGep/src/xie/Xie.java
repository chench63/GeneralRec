package xie;

import ga.*;
import gep.*;
import gep.export.*;
import gep.num.*;

/**
 * ·ûºÅ»Ø¹é
 */
public class Xie implements Runnable
{
	private NGEP gep = new NGEP();

	public Xie(NGEP gep)
	{
		this.gep = gep;
	}

	public void run()
	{
		try
		{
			gep.setFunctionSet("+-*/~QELSCTA+-*/~QELSCTA");
//			gep.setFunctionSet("+-*/~QESCT");
//			gep.setFunctionSet("+-*/~+-*/~");
			
//			gep.setVariableSet("ababababab");
			gep.setVariableSet("abcabcabcabc");
			
			gep.setConstantSet("0123456789");
	
			gep.setLinkFunction('+');
			
			gep.setGeneNumber(2);
			gep.setGeneHead(6);
	
			gep.setInitializer(new GepInitializer(gep, 100));
			gep.setSelectionOperator(new TournamentSelectionOperator(gep, 4));

			gep.addMutationOperator(new GepMutationOperator(gep, 0.044));
			gep.addMutationOperator(new GepInsertSequenceOperator(gep, 0.1, new int[]{1,2,3}));
			gep.addMutationOperator(new GepRootInsertSequenceOperator(gep, 0.1, new int[]{1,2,3}));
			gep.addMutationOperator(new GepGeneTranspositionOperator(gep, 0.1));
			
			gep.addCrossoverOperator(new GepOnePointCrossoverOperator(gep, 0.4));
			gep.addCrossoverOperator(new GepTwoPointCrossoverOperator(gep, 0.2));
			gep.addCrossoverOperator(new GepGeneCrossoverOperator(gep, 0.1));
	
			Decoder decoder = new NDecoder(gep); 
			gep.setDecoder(decoder);
			
//			String dataFile = "d:/work/zGEP/data/xie/gepzh.txt";
			String dataFile = "d:/work/zGEP/data/sunspot/sunspot_ode.txt";
			NDataSet trainDataSet = new FileNDataSet(dataFile, 3, 120, 140);
			Fitness fitness = new CorrelationCoefficientFitness(trainDataSet);
//			Fitness fitness = new RelativeErrorFitness(trainDataSet, 100);
//			Fitness fitness = new AbsoluteErrorFitness(trainDataSet, 100);
			gep.setFitnessFunction(fitness);
			Evaluator evaluator = new NEvaluator(fitness, trainDataSet);
			gep.setEvaluator(evaluator);
	
//			Stopper stopper = new MaxGenerationStopper(gep, 1); 
			Stopper stopper = new ManualStopper(gep); 
			gep.setStopper(stopper);
			
			gep.run();

			//=====================================================================
			Formula formula = (Formula) gep.getBestProtein();
			
			TextExporter exporter = new MathematicaTextExporter();
			System.out.println("Mathematica: " + exporter.export(formula));

			
//			//================================================================
//			NDataSet testDataSet = trainDataSet;
//			fitness = new CorrelationCoefficientFitness(testDataSet);
//			NTester tester = new NTester(fitness, trainDataSet);
//			double r = tester.test(gep.getBestProtein());
//			System.out.println("===============================");
//			double[] targets = testDataSet.getTargets();
//			double[] values = tester.getValues();
//			for (int i=0; i<values.length; i++)
//			{
//				System.out.println("" + targets[i] + "       " + values[i]);
//			}
//			System.out.println("===============================");
//			System.out.println("Test R: " + r);
			
			//================================================================
//			Date now = new Date();
//
//			Reporter reporter = new GepReporter();
//			reporter.report(gep, now);
//		
////			Population population = gep.population;
////			Chromosome chromosome = population.get(0);
////			Formula formula = (Formula) decoder.decode(chromosome);
//			
//			Formula formula = (Formula) gep.getBestProtein();
//			
//			GraphExporter graphGenerator = new MetaPostGraphExporter();
//			graphGenerator.export(formula, now);
//			graphGenerator.export(formula, "d:/work/zGEP/preview/figure.mp");
		}
		catch (GepException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws GepException
	{
		NGEP gep = new NGEP();
		Xie regression = new Xie(gep);

		Thread worker = new Thread(regression);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
	}
}

