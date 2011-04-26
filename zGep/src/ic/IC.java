package ic;

import ga.Decoder;
import ga.Evaluator;
import ga.GepException;
import ga.ManualStopper;
import ga.Stopper;
import ga.TournamentSelectionOperator;
import gep.GepGeneCrossoverOperator;
import gep.GepGeneTranspositionOperator;
import gep.GepInitializer;
import gep.GepInsertSequenceOperator;
import gep.GepMutationOperator;
import gep.GepOnePointCrossoverOperator;
import gep.GepRootInsertSequenceOperator;
import gep.GepTwoPointCrossoverOperator;
import gep.bool.BDecoder;
import gep.bool.BGEP;

/**
 * ·ûºÅ»Ø¹é
 */
public class IC implements Runnable
{
	public void run()
	{
		try
		{
			for (int i=0; ; i++)
			{
				BGEP gep = new BGEP();
	
				gep.setFunctionSet("&^F&^F");
				gep.setVariableSet("abcdef");
				gep.setLinkFunction('^');
			
				gep.setGeneNumber(4);
				gep.setGeneHead(10);
	
				gep.setInitializer(new GepInitializer(gep, 100));
	
//				gep.setSelector(new RouletteSelector(gep));
				gep.setSelectionOperator(new TournamentSelectionOperator(gep, 2));
	
				gep.addMutationOperator(new GepMutationOperator(gep, 0.044));
				gep.addMutationOperator(new GepInsertSequenceOperator(gep, 0.1, new int[]{1,2,3}));
				gep.addMutationOperator(new GepRootInsertSequenceOperator(gep, 0.1, new int[]{1,2,3}));
				gep.addMutationOperator(new GepGeneTranspositionOperator(gep, 0.1));
			
				gep.addCrossoverOperator(new GepOnePointCrossoverOperator(gep, 0.4));
				gep.addCrossoverOperator(new GepTwoPointCrossoverOperator(gep, 0.2));
				gep.addCrossoverOperator(new GepGeneCrossoverOperator(gep, 0.1));
	
				Decoder decoder = new BDecoder(gep); 
				gep.setDecoder(decoder);
	
				Evaluator evaluator = new ICEvaluator(gep);
				gep.setEvaluator(evaluator);
			
				Stopper stopper = new ManualStopper(gep);
				gep.setStopper(stopper);
			
				gep.run(); 
			}
		}
		catch (GepException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws GepException
	{
		IC ic = new IC();
		ic.run();
	}
}
