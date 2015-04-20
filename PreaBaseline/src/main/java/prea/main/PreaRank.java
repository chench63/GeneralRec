package prea.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import prea.data.splitter.*;
import prea.data.structure.SparseMatrix;
import prea.data.structure.SparseVector;
import prea.recommender.*;
import prea.recommender.matrix.RegularizedSVD;
import prea.recommender.matrix.RankBasedSVD;
import prea.recommender.llorma.PairedGlobalLLORMA;
import prea.util.KernelSmoothing;
import prea.util.Printer;
import prea.util.RankEvaluator;
import prea.util.EvaluationMetrics;

/**
 * An extension of LLORMA to rank-based losses.
 * 
 * @author Joonseok Lee
 * @since 2014. 6. 4
 * @version 2.0
 */
public class PreaRank {
	/** The maximum number of threads which will run simultaneously. 
	 *  We recommend not to exceed physical limit of your machine. */
	public static final int MULTI_THREAD_LEVEL = 8;
	
	/*========================================
	 * Parameters
	 *========================================*/
	/** The name of data file used for test. */
	public static String dataFileName;
	/** Evaluation mode */
	public static int evaluationMode;
	/** Proportion of items which will be used for test purpose. */
	public static double testRatio;
	/** The name of predefined split data file. */
	public static String splitFileName;
	/** The number of folds when k-fold cross-validation is used. */
	public static int foldCount;
	/** The number of training items for each user. */
	public static int userTrainCount;
	/** The number of test items guaranteed for each user. */
	public static int minTestCount;
	/** Indicating whether to run all algorithms. */
	public static boolean runAllAlgorithms;
	/** The code for an algorithm which will run. */
	public static String algorithmCode;
	/** Parameter list for the algorithm to run. */
	public static String[] algorithmParameters;
	/** Indicating whether pre-calculating user similarity or not */
	public static boolean userSimilarityPrefetch = false;
	/** Indicating whether pre-calculating item similarity or not */
	public static boolean itemSimilarityPrefetch = false;
	
	/*========================================
	 * Common Variables
	 *========================================*/
	/** Rating matrix for each user (row) and item (column) */
	public static SparseMatrix rateMatrix;
	/** Rating matrix for test items. Not allowed to refer during training and validation phase. */
	public static SparseMatrix testMatrix;
	/** Average of ratings for each user. */
	public static SparseVector userRateAverage;
	/** Average of ratings for each item. */
	public static SparseVector itemRateAverage;
	/** The number of users. */
	public static int userCount;
	/** The number of items. */
	public static int itemCount;
	/** Maximum value of rating, existing in the dataset. */
	public static int maxValue;
	/** Minimum value of rating, existing in the dataset. */
	public static int minValue;
	/** The list of item names, provided with the dataset. */
	public static String[] columnName;
	
	private static SparseMatrix userSimilarity;
	private static SparseMatrix itemSimilarity;
	public static RegularizedSVD baseline;
	
	public final static int PEARSON_CORR = 101;
	public final static int VECTOR_COS = 102;
	public final static int ARC_COS = 103;
	public final static int RATING_MATRIX = 111;
	public final static int MATRIX_FACTORIZATION = 112;
	
	/**
	 * Test examples for every algorithm. Also includes parsing the given parameters.
	 * 
	 * @param argv The argument list. Each element is separated by an empty space.
	 * First element is the data file name, and second one is the algorithm name.
	 * Third and later includes parameters for the chosen algorithm.
	 * Please refer to our web site for detailed syntax.
	 * @throws InterruptedException 
	 */
	public static void main(String argv[]) throws InterruptedException {
		// Set default setting first:
		dataFileName = "movieLens_100K";
		evaluationMode = DataSplitManager.SIMPLE_SPLIT;
		splitFileName = dataFileName + "_split.txt";
		testRatio = 0.5;
		foldCount = 8;
		userTrainCount = 10;
		minTestCount = 10;
		runAllAlgorithms = true;
		
		// Parsing the argument:
		if (argv.length > 1) {
			parseCommandLine(argv);
		}
		
		// Read input file:
		readArff (dataFileName + ".arff");
		
		// Train/test data split:
		switch (evaluationMode) {
			case DataSplitManager.SIMPLE_SPLIT:
				SimpleSplit sSplit = new SimpleSplit(rateMatrix, testRatio, maxValue, minValue);
				System.out.println("Evaluation\tSimple Split (" + (1 - testRatio) + " train, " + testRatio + " test)");
				testMatrix = sSplit.getTestMatrix();
				userRateAverage = sSplit.getUserRateAverage();
				itemRateAverage = sSplit.getItemRateAverage();
				
				run();
				break;
			case DataSplitManager.PREDEFINED_SPLIT:
				PredefinedSplit pSplit = new PredefinedSplit(rateMatrix, splitFileName, maxValue, minValue);
				System.out.println("Evaluation\tPredefined Split (" + splitFileName + ")");
				testMatrix = pSplit.getTestMatrix();
				userRateAverage = pSplit.getUserRateAverage();
				itemRateAverage = pSplit.getItemRateAverage();
				
				run();
				break;
			case DataSplitManager.K_FOLD_CROSS_VALIDATION:
				KfoldCrossValidation kSplit = new KfoldCrossValidation(rateMatrix, foldCount, maxValue, minValue);
				System.out.println("Evaluation\t" + foldCount + "-fold Cross-validation");
				for (int k = 1; k <= foldCount; k++) {
					testMatrix = kSplit.getKthFold(k);
					userRateAverage = kSplit.getUserRateAverage();
					itemRateAverage = kSplit.getItemRateAverage();
					
					run();
				}
				break;
			case DataSplitManager.RANK_EXP_SPLIT:
				RankExpSplit rSplit = new RankExpSplit(rateMatrix, userTrainCount, minTestCount, maxValue, minValue);
				System.out.println("Evaluation\t" + "Ranking Experiment with N = " + userTrainCount);
				testMatrix = rSplit.getTestMatrix();
				userRateAverage = rSplit.getUserRateAverage();
				itemRateAverage = rSplit.getItemRateAverage();
				
				run();
				break;
		}
	}
	
	/** Run an/all algorithm with given data, based on the setting from command arguments. */
	private static void run() throws InterruptedException {
		if (runAllAlgorithms) {
			runAll();
		}
		else {
			runIndividual(algorithmCode, algorithmParameters);
		}
	}
	
	/** Run one algorithm with customized parameters with given data. */
	public static void runIndividual(String algorithmCode, String[] parameters) {
		System.out.println(RankEvaluator.printTitle() + "\tAvgP\tTrain Time\tTest Time");
		
		// Prefetching user/item similarity:
		if (userSimilarityPrefetch) {
			userSimilarity = calculateUserSimilarity(MATRIX_FACTORIZATION, ARC_COS, 0);
		}
		else {
			userSimilarity = new SparseMatrix(userCount+1, userCount+1);
		}
		
		if (itemSimilarityPrefetch) {
			itemSimilarity = calculateItemSimilarity(MATRIX_FACTORIZATION, ARC_COS, 0);
		}
		else {
			itemSimilarity = new SparseMatrix(itemCount+1, itemCount+1);
		}
		
		// Loss code
		int lossCode = -1;
		if (parameters[0].equals("log_mult")) lossCode = RankEvaluator.LOG_LOSS_MULT;
		else if (parameters[0].equals("log_add")) lossCode = RankEvaluator.LOG_LOSS_ADD;
		else if (parameters[0].equals("exp_mult")) lossCode = RankEvaluator.EXP_LOSS_MULT;
		else if (parameters[0].equals("exp_add")) lossCode = RankEvaluator.EXP_LOSS_ADD;
		else if (parameters[0].equals("hinge_mult")) lossCode = RankEvaluator.HINGE_LOSS_MULT;
		else if (parameters[0].equals("hinge_add")) lossCode = RankEvaluator.HINGE_LOSS_ADD;
		else if (parameters[0].equals("abs")) lossCode = RankEvaluator.ABSOLUTE_LOSS;
		else if (parameters[0].equals("sqr")) lossCode = RankEvaluator.SQUARED_LOSS;
		else if (parameters[0].equals("expreg")) lossCode = RankEvaluator.EXP_REGRESSION;
		else if (parameters[0].equals("l1reg")) lossCode = RankEvaluator.SMOOTH_L1_REGRESSION;
		else if (parameters[0].equals("logistic")) lossCode = RankEvaluator.LOGISTIC_LOSS;
		else lossCode = RankEvaluator.LOG_LOSS_MULT; // default
		
		if (algorithmCode.toLowerCase().equals("pgllorma")) {
			// Run the baseline for calculating user/item similarity
			double learningRate = 0.005;
			double regularizer = 0.1;
			int maxIter = 100;
			baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue,	10, learningRate, regularizer, 0, maxIter, false);
			System.out.println("SVD\tFro\t10\t" + testRecommender("SVD", baseline));
			
			runPairedGlobalLLORMA(lossCode, Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]), Double.parseDouble(parameters[3]), true);
		}
		else if (algorithmCode.toLowerCase().equals("ranksvd")) {
			runRankBasedSVD(lossCode, Integer.parseInt(parameters[1]), Double.parseDouble(parameters[2]), true);
		}
	}
	
	/** Run an/all algorithm with given data, based on the setting from command arguments. 
	 * @throws InterruptedException */
	private static void runAll() throws InterruptedException {
		System.out.println(RankEvaluator.printTitle() + "\tAvgP\tTrain Time\tTest Time");
		
		// Prefetching user/item similarity:
		if (userSimilarityPrefetch) {
			userSimilarity = calculateUserSimilarity(MATRIX_FACTORIZATION, ARC_COS, 0);
		}
		else {
			userSimilarity = new SparseMatrix(userCount+1, userCount+1);
		}
		
		if (itemSimilarityPrefetch) {
			itemSimilarity = calculateItemSimilarity(MATRIX_FACTORIZATION, ARC_COS, 0);
		}
		else {
			itemSimilarity = new SparseMatrix(itemCount+1, itemCount+1);
		}
		
		// Regularized SVD
		int[] svdRank = {1, 5, 10};
		
		for (int r : svdRank) {
			double learningRate = 0.005;
			double regularizer = 0.1;
			int maxIter = 100;
			
			baseline = new RegularizedSVD(userCount, itemCount, maxValue, minValue,
				r, learningRate, regularizer, 0, maxIter, false);
			System.out.println("SVD\tFro\t" + r + "\t" + testRecommender("SVD", baseline));
		}
		
		// Rank-based SVD
		int[] rsvdRank = {1, 5, 10};
		for (int r : rsvdRank) {
			runRankBasedSVD(RankEvaluator.LOGISTIC_LOSS, r, 6000, true);
			runRankBasedSVD(RankEvaluator.LOG_LOSS_MULT, r, 1700, true);
			runRankBasedSVD(RankEvaluator.LOG_LOSS_ADD, r, 1700, true);
			runRankBasedSVD(RankEvaluator.EXP_LOSS_MULT, r, 370, true);
			runRankBasedSVD(RankEvaluator.EXP_LOSS_ADD, r, 25, true);
			runRankBasedSVD(RankEvaluator.HINGE_LOSS_MULT, r, 1700, true);
			runRankBasedSVD(RankEvaluator.HINGE_LOSS_ADD, r, 1700, true);
			runRankBasedSVD(RankEvaluator.EXP_REGRESSION, r, 40, true);
		}
		
		// Paired Global LLORMA
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT,  5,  1, 1500, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT,  5,  5, 1000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT,  5, 10,  500, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT, 10,  1, 3000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT, 10,  5, 2000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_MULT, 10, 10, 1000, true);
		
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD,  5,  1, 1200, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD,  5,  5, 1200, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD,  5, 10,  900, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD, 10,  1, 3000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD, 10,  5, 3000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOG_LOSS_ADD, 10, 10, 2000, true);
		
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT,  5,  1,  450, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT,  5,  5,  250, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT,  5, 10,  110, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT, 10,  1, 1000, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT, 10,  5,  400, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_MULT, 10, 10,  200, true);
		
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD,  5,  1, 20, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD,  5,  5, 13, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD,  5, 10,  7, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD, 10,  1, 40, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD, 10,  5, 25, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_LOSS_ADD, 10, 10, 15, true);
		
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT,  5,  1, 1000, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT,  5,  5,  500, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT,  5, 10,  300, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT, 10,  1, 2000, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT, 10,  5, 1000, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_MULT, 10, 10,  500, true);
		
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD,  5,  1, 1200, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD,  5,  5, 1200, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD,  5, 10,  500, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD, 10,  1, 2500, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD, 10,  5, 2500, true);
		runPairedGlobalLLORMA(RankEvaluator.HINGE_LOSS_ADD, 10, 10, 1000, true);
		
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION,  5,  1,  60, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION,  5,  5,  40, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION,  5, 10,  20, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION, 10,  1, 120, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION, 10,  5,  80, true);
		runPairedGlobalLLORMA(RankEvaluator.EXP_REGRESSION, 10, 10,  40, true);
		
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS,  5,  1, 4500, true);
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS,  5,  5, 3500, true);
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS,  5, 10, 2500, true);
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS, 10,  1, 9000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS, 10,  5, 7000, true);
		runPairedGlobalLLORMA(RankEvaluator.LOGISTIC_LOSS, 10, 10, 5000, true);
	}
	
	private static void runRankBasedSVD(int loss, int rank, double learningRate, boolean verbose) {
		// Insensitive parameters are fixed with the following values:
		int maxIter = 20;
		double regularizer = 1E-6;
		
		RankBasedSVD rsvd = new RankBasedSVD(
			userCount, itemCount, maxValue, minValue,
			rank, learningRate, regularizer, 0, maxIter,
			loss, testMatrix, null, null, verbose);
			
		System.out.println("RSVD" + "\t" + loss + "\t" + rank + "\t" + testRecommender("RSVD", rsvd));
	}
	
	private static void runPairedGlobalLLORMA(int loss, int modelCount, int rank, double learningRate, boolean verbose) {
		// Insensitive parameters are fixed with the following values:
		int maxIter = 100;
		double kernelWidth = 0.8;
		int kernelType = KernelSmoothing.EPANECHNIKOV_KERNEL;
		double regularizer = 1E-8;
		
		PairedGlobalLLORMA pgllorma = new PairedGlobalLLORMA(
			userCount, itemCount, maxValue, minValue,
			rank, learningRate, regularizer, maxIter,
			Math.min(testMatrix.itemCount(), modelCount),
			kernelType, kernelWidth, loss, testMatrix, baseline,
			MULTI_THREAD_LEVEL, verbose);
		
		System.out.println("pgLLR" + "\t" + loss + "\t" + rank + "\t" + testRecommender("pgLLR", pgllorma));
	}
	
	/**
	 * Parse the command from user.
	 * 
	 * @param command The command string given by user.
	 */
	private static void parseCommandLine(String[] command) {
		int i = 0;
		
		while (i < command.length) {
			if (command[i].equals("-f")) { // input file
				dataFileName = command[i+1];
				i += 2;
			}
			else if (command[i].equals("-s")) { // data split
				if (command[i+1].equals("simple")) {
					evaluationMode = DataSplitManager.SIMPLE_SPLIT;
					testRatio = Double.parseDouble(command[i+2]);
				}
				else if (command[i+1].equals("pred")) {
					evaluationMode = DataSplitManager.PREDEFINED_SPLIT;
					splitFileName = command[i+2].trim();
				}
				else if (command[i+1].equals("kcv")) {
					evaluationMode = DataSplitManager.K_FOLD_CROSS_VALIDATION;
					foldCount = Integer.parseInt(command[i+2]);
				}
				else if (command[i+1].equals("rank")) {
					evaluationMode = DataSplitManager.RANK_EXP_SPLIT;
					userTrainCount = Integer.parseInt(command[i+2]);
					minTestCount = 10;
				}
				i += 3;
			}
			else if (command[i].equals("-a")) { // algorithm
				runAllAlgorithms = false;
				algorithmCode = command[i+1];
				
				// parameters for the algorithm:
				int j = 0;
				while (command.length > i+2+j && !command[i+2+j].startsWith("-")) {
					j++;
				}
				
				algorithmParameters = new String[j];
				System.arraycopy(command, i+2, algorithmParameters, 0, j);
				
				i += (j + 2);
			}
		}
	}
	
	/**
	 * Test interface for a rank-based recommender system.
	 * Print ranking-based measures for given test data.
	 * Note that we take (test-test) pairs as well as (train-test) pairs.
	 * No (train-train) pairs are used for testing.
	 * 
	 * @return evaluation metrics and elapsed time for learning and evaluation.
	 */
	public static String testRecommender(String algorithmName, Recommender r) {
		long learnStart = System.currentTimeMillis();
		r.buildModel(rateMatrix);
		long learnEnd = System.currentTimeMillis();
		
		long evalStart = System.currentTimeMillis();
		EvaluationMetrics evalPointTrain = r.evaluate(rateMatrix);
		EvaluationMetrics evalPointTest = r.evaluate(testMatrix);
		RankEvaluator evalRank = new RankEvaluator(rateMatrix, testMatrix, evalPointTrain.getPrediction().plus(evalPointTest.getPrediction()));
		long evalEnd = System.currentTimeMillis();
		
		return evalRank.printOneLine() + "\t"
			 + String.format("%.4f", evalPointTest.getAveragePrecision()) + "\t"
			 + Printer.printTime(learnEnd - learnStart) + "\t"
			 + Printer.printTime(evalEnd - evalStart);
	}
	
	
	/*========================================
	 * File I/O
	 *========================================*/
	/**
	 * Read the data file in ARFF format, and store it in rating matrix.
	 * Peripheral information such as max/min values, user/item count are also set in this method.
	 * 
	 * @param fileName The name of data file.
	 */
	private static void readArff(String fileName) {
		try {
			FileInputStream stream = new FileInputStream(fileName);
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader buffer = new BufferedReader(reader);
			
			ArrayList<String> tmpColumnName = new ArrayList<String>();
			
			String line;
			int userNo = 0; // sequence number of each user
			int attributeCount = 0;
			
			maxValue = -1;
			minValue = 99999;
			
			// Read attributes:
			while((line = buffer.readLine()) != null && !line.equals("TT_EOF")) {
				if (line.contains("@ATTRIBUTE")) {
					String name;
					
					line = line.substring(10).trim();
					if (line.charAt(0) == '\'') {
						int idx = line.substring(1).indexOf('\'');
						name = line.substring(1, idx+1);
					}
					else {
						int idx = line.substring(1).indexOf(' ');
						name = line.substring(0, idx+1).trim();
					}
					
					tmpColumnName.add(name);
					attributeCount++;
				}
				else if (line.contains("@RELATION")) {
					// do nothing
				}
				else if (line.contains("@DATA")) {
					// This is the end of attribute section!
					break;
				}
				else if (line.length() <= 0) {
					// do nothing
				}
			}
			
			// Set item count to data structures:
			itemCount = (attributeCount - 1)/2;
			columnName = new String[attributeCount];
			tmpColumnName.toArray(columnName);
			
			int[] itemRateCount = new int[itemCount+1];
			rateMatrix = new SparseMatrix(500000, itemCount+1); // Netflix: [480189, 17770]
			
			// Read data:
			while((line = buffer.readLine()) != null && !line.equals("TT_EOF")) {
				if (line.length() > 0) {
					line = line.substring(1, line.length() - 1);
					
					StringTokenizer st = new StringTokenizer (line, ",");
					
					while (st.hasMoreTokens()) {
						String token = st.nextToken().trim();
						int i = token.indexOf(" ");
						
						int movieID, rate;
						int index = Integer.parseInt(token.substring(0, i));
						String data = token.substring(i+1);
						
						if (index == 0) { // User ID
							//int userID = Integer.parseInt(data);
							
							userNo++;
						}
						else if (data.length() == 1) { // Rate
							movieID = index;
							rate = Integer.parseInt(data);
							
							if (rate > maxValue) {
								maxValue = rate;
							}
							else if (rate < minValue) {
								minValue = rate;
							}
							
							(itemRateCount[movieID])++;
							rateMatrix.setValue(userNo, movieID, rate);
						}
						else { // Date
							// Do not use
						}
					}
				}
			}
			
			userCount = userNo;
			
			// Reset user vector length:
			rateMatrix.setSize(userCount+1, itemCount+1);
			for (int i = 1; i <= itemCount; i++) {
				rateMatrix.getColRef(i).setLength(userCount+1);
			}
			
			System.out.println ("Data File\t" + dataFileName);
			System.out.println ("User Count\t" + userCount);
			System.out.println ("Item Count\t" + itemCount);
			System.out.println ("Rating Count\t" + rateMatrix.itemCount());
			System.out.println ("Rating Density\t" + String.format("%.2f", ((double) rateMatrix.itemCount() / (double) userCount / (double) itemCount * 100.0)) + "%");
			
			stream.close();
		}
		catch (IOException ioe) {
			System.out.println ("No such file: " + ioe);
			System.exit(0);
		}
	}
	
	public static double getUserSimilarity (int idx1, int idx2) {
		if (userSimilarityPrefetch) {
			return userSimilarity.getValue(idx1, idx2);
		}
		else {
			double sim;
			if (idx1 <= idx2) {
				sim = userSimilarity.getValue(idx1, idx2);
			}
			else {
				sim = userSimilarity.getValue(idx2, idx1);
			}
			
			if (sim == 0.0) {
				SparseVector u_vec = baseline.getU().getRowRef(idx1);
				SparseVector v_vec = baseline.getU().getRowRef(idx2);
				
				sim = 1 - 2.0 / Math.PI * Math.acos(u_vec.innerProduct(v_vec) / (u_vec.norm() * v_vec.norm()));
				
				if (Double.isNaN(sim)) {
					sim = 0.0;
				}
				
				if (idx1 <= idx2) {
					userSimilarity.setValue(idx1, idx2, sim);
				}
				else {
					userSimilarity.setValue(idx2, idx1, sim);
				}
			}
			
			return sim;
		}
	}
	
	public static double getItemSimilarity (int idx1, int idx2) {
		if (itemSimilarityPrefetch) {
			return itemSimilarity.getValue(idx1, idx2);
		}
		else {
			double sim;
			if (idx1 <= idx2) {
				sim = itemSimilarity.getValue(idx1, idx2);
			}
			else {
				sim = itemSimilarity.getValue(idx2, idx1);
			} 
			
			if (sim == 0.0) {
				SparseVector i_vec = baseline.getV().getColRef(idx1);
				SparseVector j_vec = baseline.getV().getColRef(idx2);
				
				sim = 1 - 2.0 / Math.PI * Math.acos(i_vec.innerProduct(j_vec) / (i_vec.norm() * j_vec.norm()));
				
				if (Double.isNaN(sim)) {
					sim = 0.0;
				}
				
				if (idx1 <= idx2) {
					itemSimilarity.setValue(idx1, idx2, sim);
				}
				else {
					itemSimilarity.setValue(idx2, idx1, sim);
				}
			}
			
			return sim;
		}
	}
	
	public static SparseVector kernelSmoothing(int size, int id, int kernelType, double width, boolean isItemFeature) {
		SparseVector newFeatureVector = new SparseVector(size);
		newFeatureVector.setValue(id, 1.0);
		
		for (int i = 1; i < size; i++) {
			double sim;
			if (isItemFeature) {
				sim = getItemSimilarity(i, id);
			}
			else { // userFeature
				sim = getUserSimilarity(i, id);
			}
			
			newFeatureVector.setValue(i, KernelSmoothing.kernelize(sim, width, kernelType));
		}
		
		return newFeatureVector;
	}
	
	private static SparseMatrix calculateUserSimilarity(int dataType, int simMethod, double smoothingFactor) {
		SparseMatrix result = new SparseMatrix(userCount+1, userCount+1);
		
		for (int u = 1; u <= userCount; u++) {
			result.setValue(u, u, 1.0);
			for (int v = u+1; v <= userCount; v++) {
				SparseVector u_vec;
				SparseVector v_vec;
				double sim = 0.0;
				
				// Data Type:
				if (dataType == RATING_MATRIX) {
					u_vec = rateMatrix.getRowRef(u);
					v_vec = rateMatrix.getRowRef(v);
				}
				else if (dataType == MATRIX_FACTORIZATION) {
					u_vec = baseline.getU().getRowRef(u);
					v_vec = baseline.getU().getRowRef(v);
				}
				else { // Default: Rating Matrix
					u_vec = rateMatrix.getRowRef(u);
					v_vec = rateMatrix.getRowRef(v);
				}
				
				// Similarity Method:
				if (simMethod == PEARSON_CORR) { // Pearson correlation
					double u_avg = userRateAverage.getValue(u);
					double v_avg = userRateAverage.getValue(v);
					
					SparseVector a = u_vec.sub(u_avg);
					SparseVector b = v_vec.sub(v_avg);
					
					sim = a.innerProduct(b) / (a.norm() * b.norm());
				}
				else if (simMethod == VECTOR_COS) { // Vector cosine
					sim = u_vec.innerProduct(v_vec) / (u_vec.norm() * v_vec.norm());
				}
				else if (simMethod == ARC_COS) { // Arccos
					sim = 1 - 2.0 / Math.PI * Math.acos(u_vec.innerProduct(v_vec) / (u_vec.norm() * v_vec.norm()));
				}
				
				if (Double.isNaN(sim)) {
					sim = 0.0;
				}
				
				// Smoothing:
				if (smoothingFactor >= 0 && smoothingFactor <= 1) {
					sim = sim*(1 - smoothingFactor) + smoothingFactor;
				}
				
				result.setValue(u, v, sim);
				result.setValue(v, u, sim);
			}
		}
		
		return result;
	}
	
	private static SparseMatrix calculateItemSimilarity(int dataType, int simMethod, double smoothingFactor) {
		SparseMatrix result = new SparseMatrix(itemCount+1, itemCount+1);
		
		for (int i = 1; i <= itemCount; i++) {
			result.setValue(i, i, 1.0);
			for (int j = i+1; j <= itemCount; j++) {
				SparseVector i_vec;
				SparseVector j_vec;
				double sim = 0.0;
				
				// Data Type:
				if (dataType == RATING_MATRIX) {
					i_vec = rateMatrix.getRowRef(i);
					j_vec = rateMatrix.getRowRef(j);
				}
				else if (dataType == MATRIX_FACTORIZATION) {
					i_vec = baseline.getV().getColRef(i);
					j_vec = baseline.getV().getColRef(j);
				}
				else { // Default: Rating Matrix
					i_vec = rateMatrix.getRowRef(i);
					j_vec = rateMatrix.getRowRef(j);
				}
				
				// Similarity Method:
				if (simMethod == PEARSON_CORR) { // Pearson correlation
					double i_avg = userRateAverage.getValue(i);
					double j_avg = userRateAverage.getValue(j);
					
					SparseVector a = i_vec.sub(i_avg);
					SparseVector b = j_vec.sub(j_avg);
					
					sim = a.innerProduct(b) / (a.norm() * b.norm());
				}
				else if (simMethod == VECTOR_COS) { // Vector cosine
					sim = i_vec.innerProduct(j_vec) / (i_vec.norm() * j_vec.norm());
				}
				else if (simMethod == ARC_COS) { // Arccos
					sim = 1 - 2.0 / Math.PI * Math.acos(i_vec.innerProduct(j_vec) / (i_vec.norm() * j_vec.norm()));
					
					if (Double.isNaN(sim)) {
						sim = 0.0;
					}
				}
				
				if (Double.isNaN(sim)) {
					sim = 0.0;
				}
				
				// Smoothing:
				if (smoothingFactor >= 0 && smoothingFactor <= 1) {
					sim = sim*(1 - smoothingFactor) + smoothingFactor;
				}
				
				result.setValue(i, j, sim);
				result.setValue(j, i, sim);
			}
		}
		
		return result;
	}
}