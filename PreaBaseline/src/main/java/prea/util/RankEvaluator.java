package prea.util;

import prea.data.structure.SparseMatrix;
import prea.data.structure.SparseVector;

/**
 * This is a class providing rank-based evaluation metrics.
 * Unlike EvaluationMetrics class, this considers both (test, test)
 * and (test, train) pairs for evaluation purpose.
 * 
 * @author Joonseok Lee
 * @since 2014. 6. 4
 * @version 2.0
 */
public class RankEvaluator {
	private static final int NDCG_THRESHOLD = 10;
	
	// Loss code:
	/** The number of loss functions. */
	public static final int LOSS_COUNT = 12;
	
	/** Logistic loss */
	public static final int LOGISTIC_LOSS = 0;
	/** Zero-one loss */
	public static final int DISCRETE_LOSS = 1;
	/** Log-loss (multiplicative) */
	public static final int LOG_LOSS_MULT = 2;
	/** Log-loss (additive) */
	public static final int LOG_LOSS_ADD = 3;
	/** Exponential-loss (multiplicative) */
	public static final int EXP_LOSS_MULT = 4;
	/** Exponential-loss (additive) */
	public static final int EXP_LOSS_ADD = 5;
	/** Hinge-loss (multiplicative) */
	public static final int HINGE_LOSS_MULT = 6;
	/** Hinge-loss (additive) */
	public static final int HINGE_LOSS_ADD = 7;
	/** Absolute-loss */
	public static final int ABSOLUTE_LOSS = 8;
	/** Squared-loss */
	public static final int SQUARED_LOSS = 9;
	/** Exponential regression */
	public static final int EXP_REGRESSION = 10;
	/** Smooth L1 regression */
	public static final int SMOOTH_L1_REGRESSION = 11;
	
	/** Rating matrix for each user (row) and item (column) */
	private SparseMatrix rateMatrix;
	/** A matrix for test purpose only. Do not use this during training. */
	private SparseMatrix testMatrix;
	
	/** User profile matrix. */
	private SparseMatrix U;
	/** Item profile matrix. */
	private SparseMatrix V;
	/** The matrix containing predicted values. */
	private SparseMatrix predicted;
	
	/** The number of users. */
	private int userCount;
	/** The number of items. */
	private int itemCount;
	
	/** The array storing each error score. */
	private double[] error;
	/** Normalized DCG score. */
	private double ndcg;
	
	/** The default constructor. */
	public RankEvaluator() {
		userCount = 0;
		itemCount = 0;
		
		error = new double[LOSS_COUNT];
		for (int i = 0; i < error.length; i++) {
			error[i] = 0.0;
		}
	}
	
	/**
	 * The constructor initializing training and testing matrix.
	 * 
	 * @param rm The training matrix.
	 * @param tm The testing matrix.
	 */
	public RankEvaluator(SparseMatrix rm, SparseMatrix tm) {
		rateMatrix = rm;
		testMatrix = tm;
		
		userCount = rateMatrix.length()[0] - 1;
		itemCount = rateMatrix.length()[1] - 1;
		
		error = new double[LOSS_COUNT];
		for (int i = 0; i < error.length; i++) {
			error[i] = 0.0;
		}
	}
	
	/**
	 * The constructor initializing training, testing matrix, and predicted values. 
	 * 
	 * @param rm The training matrix.
	 * @param tm The testing matrix.
	 * @param p The prediction matrix.
	 */
	public RankEvaluator(SparseMatrix rm, SparseMatrix tm, SparseMatrix p) {
		rateMatrix = rm;
		testMatrix = tm;
		predicted = p;
		
		userCount = rateMatrix.length()[0] - 1;
		itemCount = rateMatrix.length()[1] - 1;
		
		error = new double[LOSS_COUNT];
		for (int i = 0; i < error.length; i++) {
			error[i] = 0.0;
		}
		
		evaluate(true);
	}
	
	/**
	 * Add a new loss score to each error.
	 * 
	 * @param userLoss Values to be added.
	 */
	public void add(double[] userLoss) {
		for (int l = 0; l < LOSS_COUNT; l++) {
			error[l] += userLoss[l];
		}
		userCount++;
	}
	
	/** Calculate all evaluation metrics with given real and predicted rating matrices. */
	private void evaluate(boolean usePredicted) {
		int activeUserCount = 0;
		
		for (int u = 1; u <= userCount; u++) {
			SparseVector predictedItems = new SparseVector(itemCount+1);
			double[] userLoss = new double[LOSS_COUNT];
			
			int pairCount = 0;
			
			int[] trainItems = rateMatrix.getRowRef(u).indexList();
			int[] testItems = testMatrix.getRowRef(u).indexList();
			
			if (testItems != null) {
				for (int i : testItems) {
					double realRate_i = testMatrix.getValue(u, i);
					double predictedRate_i = usePredicted ? predicted.getValue(u, i) : U.getRowRef(u).innerProduct(V.getColRef(i));

					predictedItems.setValue(i, predictedRate_i);
					
					for (int j : testItems) {
						double realRate_j = testMatrix.getValue(u, j);
						double predictedRate_j = usePredicted ? predicted.getValue(u, j) : U.getRowRef(u).innerProduct(V.getColRef(j));
						
						if (realRate_i > realRate_j) {
							for (int l = 0; l < LOSS_COUNT; l++) {
								userLoss[l] += loss(realRate_i, realRate_j, predictedRate_i, predictedRate_j, l);
							}
							pairCount++;
						}
					}
					
					if (trainItems != null) {
						for (int t = 0; t < trainItems.length; t++) {
							int j = trainItems[t];
							double realRate_j = rateMatrix.getValue(u, j);
							double predictedRate_j = usePredicted ? predicted.getValue(u, j) : U.getRowRef(u).innerProduct(V.getColRef(j));

							if (realRate_i > realRate_j) {
								for (int l = 0; l < LOSS_COUNT; l++) {
									userLoss[l] += loss(realRate_i, realRate_j, predictedRate_i, predictedRate_j, l);
								}
								pairCount++;
							}
						}
					}
				}
			}
			
			if (pairCount > 0) {
				for (int l = 0; l < LOSS_COUNT; l++) {
					userLoss[l] /= pairCount;
				}
			}
			
			for (int l = 0; l < LOSS_COUNT; l++) {
				error[l] += userLoss[l];
			}
			
			
			SparseVector observedItems = testMatrix.getRowRef(u);
			if (observedItems.itemCount() > 0) {
				int[] observedIndices = observedItems.indexList();
				double[] observedValues = observedItems.valueList();
				double[] predictedValues = predictedItems.valueList();
				int listLength = Math.min(NDCG_THRESHOLD, observedIndices.length);
				
				double u_dcg = 0.0;
				Sort.kLargest(predictedValues, observedIndices, 0, observedIndices.length - 1, listLength);
				for (int i = 0; i < listLength; i++) {
					double observed = rateMatrix.getValue(u, observedIndices[i]) + testMatrix.getValue(u, observedIndices[i]);
					u_dcg += (Math.pow(2.0, observed) - 1.0) / (Math.log(i+2) / Math.log(2.0));
				}
				
				double best_dcg = 0.0;
				Sort.kLargest(observedValues, observedIndices, 0, observedIndices.length - 1, listLength);
				// Now observedIndices were corrupted, but we do not need them.
				for (int i = 0; i < listLength; i++) {
					best_dcg += (Math.pow(2.0, observedValues[i]) - 1.0) / (Math.log(i+2) / Math.log(2.0));
				}
				
				ndcg += (u_dcg / best_dcg);
				activeUserCount++;
			}
		}
		
		ndcg /= (double) activeUserCount;
	}
	
	/**
	 * Calculating value of a loss function on given point.
	 * 
	 * @param Mui The original score of user u on item i.
	 * @param Muj The original score of user u on item j.
	 * @param Fui The predicted score of user u on item i.
	 * @param Fuj The predicted score of user u on item j.
	 * @param lossCode The loss function to be evaluated.
	 * @return
	 */
	public static double loss(double Mui, double Muj, double Fui, double Fuj, int lossCode) {
		switch (lossCode) {
		case LOGISTIC_LOSS:
			return 1 / (1 + Math.exp((Mui - Muj)*(Fui - Fuj)));
		case DISCRETE_LOSS:
			return (Mui - Muj)*(Fui - Fuj) < 0 ? 1 : 0;
		case LOG_LOSS_MULT:
			return (Mui - Muj) * Math.log(1 + Math.exp(Fuj - Fui));
		case LOG_LOSS_ADD:
			return Math.log(1 + Math.exp(Mui - Muj - Fui + Fuj));
		case EXP_LOSS_MULT:
			return (Mui - Muj) * Math.exp(Fuj - Fui);
		case EXP_LOSS_ADD:
			return Math.exp(Muj - Mui + Fuj - Fui);
		case HINGE_LOSS_MULT:
			return (Mui - Muj) * Math.max(1 - Fui + Fuj, 0);
		case HINGE_LOSS_ADD:
			return Math.max(Mui - Muj - Fui + Fuj, 0);
		case ABSOLUTE_LOSS:
			return Math.abs(Mui - Muj - Fui + Fuj);
		case SQUARED_LOSS:
			return Math.pow(Mui - Muj - Fui + Fuj, 2);
		case EXP_REGRESSION:
			return Math.exp(Mui - Muj - Fui + Fuj) + Math.exp(Fui - Fuj - Mui + Muj);
		case SMOOTH_L1_REGRESSION:
			return Math.log(1 + Math.exp(Mui - Muj - Fui + Fuj)) + Math.log(1 + Math.exp(Fui - Fuj - Mui + Muj));
		default:
			return 0.0;
		}
	}
	
	/**
	 * Calculating derivative of a loss function on given point.
	 * 
	 * @param Mui The original score of user u on item i.
	 * @param Muj The original score of user u on item j.
	 * @param Fui The predicted score of user u on item i.
	 * @param Fuj The predicted score of user u on item j.
	 * @param lossCode The loss function to be evaluated.
	 * @return
	 */
	public static double lossDiff(double Mui, double Muj, double Fui, double Fuj, int lossCode) {
		switch (lossCode) {
		case LOGISTIC_LOSS:
			return (Muj - Mui) * Math.exp((Mui - Muj)*(Fui - Fuj)) / Math.pow(1 + Math.exp((Mui - Muj)*(Fui - Fuj)), 2);
		case DISCRETE_LOSS:
			return 0.0;
		case LOG_LOSS_MULT:
			return (Muj - Mui) / (1 + Math.exp(Fui - Fuj));
		case LOG_LOSS_ADD:
			return -1.0 / (1 + Math.exp(Fui - Fuj - Mui + Muj));
		case EXP_LOSS_MULT:
			return (Muj - Mui) * Math.exp(Fuj - Fui);
		case EXP_LOSS_ADD:
			return - Math.exp(Mui - Muj - Fui + Fuj);
		case HINGE_LOSS_MULT:
			return Fui - Fuj < 1 ? Muj - Mui : 0.0;
		case HINGE_LOSS_ADD:
			return Mui - Muj > Fui - Fuj ? -1.0 : 0.0;
		case ABSOLUTE_LOSS:
			if (Mui - Muj > Fui - Fuj)
				return -1.0;
			else if (Mui - Muj < Fui - Fuj)
				return 1.0;
			else
				return Math.random() * 2 - 1;
		case SQUARED_LOSS:
			return -2 * (Mui - Muj - Fui + Fuj);
		case EXP_REGRESSION:
			return Math.exp(Fui - Fuj - Mui + Muj) - Math.exp(Mui - Muj - Fui + Fuj);
		case SMOOTH_L1_REGRESSION:
			return -1.0 / (1 + Math.exp(Fui - Fuj - Mui + Muj)) + 1.0 / (1 + Math.exp(Mui - Muj - Fui + Fuj)); 
		default:
			return 0.0;
		}
	}
	
	/**
	 * Get a loss value for a given type.
	 * 
	 * @param lossCode The loss function to be evaluated.
	 * @return The loss score for given type.
	 */
	public double getLoss(int lossCode) {
		if (lossCode >= 0 && lossCode < LOSS_COUNT) {
			return error[lossCode] / userCount;
		}
		else {
			return 0.0;
		}
	}
	
	/**
	 * The getter method for NDCG score.
	 * 
	 * @return The normalized DCG score.
	 */
	public double getNDCG() {
		return ndcg;
	}
	
	/**
	 * Print all loss values in one line.
	 * 
	 * @return The one-line string to be printed.
	 */
	public String printOneLine() {
		return String.format("%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f\t%.4f",
			this.getLoss(LOGISTIC_LOSS),
			this.getLoss(LOG_LOSS_MULT),
			this.getLoss(LOG_LOSS_ADD),
			this.getLoss(EXP_LOSS_MULT),
			this.getLoss(EXP_LOSS_ADD),
			this.getLoss(HINGE_LOSS_MULT),
			this.getLoss(HINGE_LOSS_ADD),
			this.getLoss(ABSOLUTE_LOSS),
			this.getLoss(SQUARED_LOSS),
			this.getLoss(EXP_REGRESSION),
			this.getLoss(SMOOTH_L1_REGRESSION),
			this.getNDCG(),
			this.getLoss(DISCRETE_LOSS)
		);
	}
	
	/**
	 * Print a list of titles of each loss function.
	 * 
	 * @return The one-line title list to be printed.
	 */
	public static String printTitle() {
		return "=====================================================================================================================================================================\r\nName\tOptLoss\tRank\tLogs\tLog/M\tLog/A\tExp/M\tExp/A\tHinge/M\tHinge/A\tAbs\tSqr\tExpReg\tSmL1\tNDCG@" + NDCG_THRESHOLD + "\t0/1";
	}
}
