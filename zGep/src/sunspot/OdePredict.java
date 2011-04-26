package sunspot;

import app.predict.*;
import ga.*;
import ga.SelectionOperator;
import ga.RouletteSelectionOperator;
import gep.GEP;
import gep.export.*;
import gep.num.*;
import AdaGep.*;
import java.util.Date;;

class MGEP extends AdaGep
{
	public MGEP()
	{
		addFunctionFactory(new PlusFactory());
		addFunctionFactory(new MinusFactory());
		addFunctionFactory(new MultiplyFactory());
		addFunctionFactory(new DivideFactory());
		addFunctionFactory(new NegativeFactory());
		addFunctionFactory(new SqrtFactory());
		addFunctionFactory(new ExpFactory());
		addFunctionFactory(new LogFactory());
		addFunctionFactory(new SinFactory());
		addFunctionFactory(new CosFactory());
		addFunctionFactory(new TanFactory());
		addFunctionFactory(new AbsFactory());
	}
}

/**
 * Œ¢∑÷∑Ω≥Ã‘§≤‚
 */
public class OdePredict implements Runnable
{
	MGEP gep = new MGEP();
	MGEP gepMap = new MGEP();

	public OdePredict(MGEP gep)
	{
		this.gep = gep;
	}

	public void run()
	{
		try
		{
			long sT = System.currentTimeMillis();
			
			gepMap.setVariableSet("abababab");
			gepMap.setGeneNumber(3);
			gepMap.setGeneHead(0);
	//		gepMap.setInitializer(new GepInitializer(gepMap, 50));
	//		gepMap.setSelectionOperator(new TournamentSelectionOperator(gepMap, 4));
	//		gepMap.addMutationOperator(new AdaGepMutationOperator(gepMap, 0.044));
	//		gepMap.addMutationOperator(new AdaGepMutationOperator(gepMap, 0.1));
	//		gepMap.addMutationOperator(new AdaGepInsertSequenceOperator(gepMap, 0.1, new int[] { 1, 2, 3 }));
	//		gepMap.addMutationOperator(new AdaGepRootInsertSequenceOperator(gepMap, 0.1, new int[] { 1, 2, 3 }));
	//		gepMap.addMutationOperator(new AdaGepGeneTranspositionOperator(gepMap, 0.1));	
	//		gepMap.addCrossoverOperator(new AdaGepOnePointCrossoverOperator(gepMap, 0.4));
	//		gepMap.addCrossoverOperator(new AdaGepTwoPointCrossoverOperator(gepMap, 0.2));
	//		gepMap.addCrossoverOperator(new AdaGepGeneCrossoverOperator(gepMap, 0.1));
			
			
			
			
			
//			gep.setFunctionSet("+-*/LQA+-*/LQA+-*/LQA");
//			gep.setFunctionSet("+-*/LA+-*/LA+-*/LA+-*/LA");
			gep.setFunctionSet("~+-*/LSC~+-*/LSC~+-*/LSC~+-*/LSC~+-*/LSC");
			gep.setVariableSet("abcabcabccc");
			gep.setConstantSet("0123456789");
	
			
			gep.setLinkFunction('+');
	
			gep.setGeneNumber(3);
			gep.setGeneHead(6);
	
			gep.setAdaInitializer(new AdaGepInitializer(gep,gepMap, 50));
			
//			gep.setSelector(new SelectionOperator(gep));
			gep.setSelectionOperator(new TournamentSelectionOperator(gep, 4));

			gep.addAdaMutationOperator(new AdaGepMutationOperator(gep,gepMap, 0.044));
//			gep.addMutationOperator(new GepMutationOperator(gep, 0.1));
			gep.addAdaMutationOperator(new AdaGepInsertSequenceOperator(gep, 0.1, new int[] { 1, 2, 3 }));
			gep.addAdaMutationOperator(new AdaGepRootInsertSequenceOperator(gep, 0.1, new int[] { 1, 2, 3 }));
			gep.addAdaMutationOperator(new AdaGepGeneTranspositionOperator(gep, 0.1));
	
			gep.addAdaCrossoverOperator(new AdaGepOnePointCrossoverOperator(gep, 0.4));
			gep.addAdaCrossoverOperator(new AdaGepTwoPointCrossoverOperator(gep, 0.2));
			gep.addAdaCrossoverOperator(new AdaGepGeneCrossoverOperator(gep, 0.1));
	
			Decoder decoder = new AdaDecoder(gep,gepMap);
			gep.setAdaDecoder(decoder);

//			String file = "d:/work/zGEP/data/sunspot/sample_ode.txt";
//			NDataSet dataSet = new PredictDataSet(file, 3, 20, 80);
			String file = "E:/Myeclipse/Original zGEP/data/sunspot/sunspot_ode.txt";
//			NDataSet dataSet = new PredictDataSet(file, 3, 3, 150, 250);
			NDataSet dataSet = new PredictDataSet(file, 3, 1, 1,66);
			
			Fitness fitness = new CorrelationCoefficientFitness(dataSet);
//			Fitness fitness = new RelativeErrorFitness(dataSet, 100);
			gep.setFitnessFunction(fitness);
			
			Evaluator evaluator = new OdePredictEvaluator(fitness, dataSet, 1);
			gep.setEvaluator(evaluator);
	
//			gep.setStopper(new ManualStopper(gep));
			gep.setStopper(new MaxGenerationStopper(gep, 1000));
			gep.Adarun();

			long eT = System.currentTimeMillis();
			System.out.println( eT - sT );


			//=====================================================================
//			Formula formula = (Formula) gep.getBestProtein();
//			TextExporter exporter = new MathematicaTextExporter();
		//	System.out.println("Mathematica: " + exporter.export(formula));
			
//			GraphExporter graphExporter = new MetaPostGraphExporter();
	//		graphExporter.export(formula, "E:/Myeclipse/Original zGEP/data/test.mp");
			
//			double f = evaluator.evaluate(protein);
//			
//			System.out.println(protein);
//			System.out.println(f);


//			//======================================================================
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

			//======================================================================	
//			Tester tester = new NTester();
//			tester.setTestData(testData);
//			tester.loadTestData("d:/work/zGEP/data/sunspot/test.txt");
	
//			double r = tester.test(gep.getBestProtein(), "d:/work/zGEP/data/sunspot/model.txt");
	
//			System.out.println("R Square: " + r);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{
		MGEP gep = new MGEP();
		OdePredict sunspot = new OdePredict(gep);

		Thread worker = new Thread(sunspot);
		worker.setPriority(Thread.MIN_PRIORITY);
		worker.start();
	}
}
