package prea.recommender.llorma;

import prea.data.structure.SparseVector;
import prea.data.structure.SparseMatrix;
import prea.recommender.Recommender;
import prea.recommender.matrix.RegularizedSVD;
import prea.util.RankEvaluator;
import prea.util.EvaluationMetrics;
import prea.util.KernelSmoothing;

/**
 * A class implementing Local Low-Rank Matrix Approximation for ranking.
 * 
 * @author Joonseok Lee
 * @since 2014. 6. 4
 * @version 2.0
 */
public class PairedGlobalLLORMA implements Recommender {
	/*========================================
	 * Common Variables
	 *========================================*/
	/** Rating matrix for each user (row) and item (column) */
	public SparseMatrix rateMatrix;
	/** A matrix for test purpose only. Do not use this during training. */
	public SparseMatrix testMatrix;
	/** Rating matrix for items which will be used during the validation phase.
	 * Not allowed to refer during training phase. */
	private SparseMatrix validationMatrix;
	
	/** The number of users. */
	public int userCount;
	/** The number of items. */
	public int itemCount;
	/** Maximum value of rating, existing in the dataset. */
	public double maxValue;
	/** Minimum value of rating, existing in the dataset. */
	public double minValue;
	
	// Data structures:
	/** User profile in low-rank matrix form. */
	SparseMatrix[] localUserFeatures;
	/** Item profile in low-rank matrix form. */
	SparseMatrix[] localItemFeatures;
	/** Array of anchor user IDs. */
	int[] anchorUser;
	/** Array of anchor item IDs. */
	int[] anchorItem;
	/** Similarity matrix between users. */
	private static SparseMatrix userSimilarity;
	/** Similarity matrix between items. */
	private static SparseMatrix itemSimilarity;
	/** A global SVD model used for calculating user/item similarity. */
	public static RegularizedSVD baseline;
	/** Precalculated sum of weights for each user-item pair. */
	double[][] weightSum;
	
	// Local model parameters:
	/** The number of features. */
	public int featureCount;
	/** Learning rate parameter. */
	public double learningRate;
	/** Regularization factor parameter. */
	public double regularizer;
	/** Maximum number of iteration. */
	public int maxIter;
	/** The code of loss function to be minimized.
	 *  Loss code can be found in prea.util.RankEvaluator. */
	public int lossCode;
	
	// Global combination parameters:
	/** Maximum number of local models. */
	public int modelMax;
	/** Type of kernel used in kernel smoothing. */
	public int kernelType;
	/** Width of kernel used in kernel smoothing. */
	public double kernelWidth;
	/** The maximum number of threads which will run simultaneously. 
	 * We recommend not to exceed physical limit of your machine. */
	private int multiThreadCount;
	/** Proportion of dataset, using for validation purpose. */
	public double validationRatio;
	
	/** Indicator whether to show progress of iteration. */
	public boolean showProgress;
	
	
	/*========================================
	 * Constructors
	 *========================================*/
	/**
	 * Construct a matrix-factorization-based model with the given data.
	 * 
	 * @param uc The number of users in the dataset.
	 * @param ic The number of items in the dataset.
	 * @param max The maximum rating value in the dataset.
	 * @param min The minimum rating value in the dataset.
	 * @param fc The number of features used for describing user and item profiles.
	 * @param lr Learning rate for gradient-based or iterative optimization.
	 * @param r Controlling factor for the degree of regularization. 
	 * @param iter The maximum number of iterations.
	 * @param mm The maximum number of local models.
	 * @param kt Type of kernel used in kernel smoothing.
	 * @param kw Width of kernel used in kernel smoothing.
	 * @param base A global SVD model used for calculating user/item similarity.
	 * @param ml The maximum number of threads which will run simultaneously.
	 * @param verbose Indicating whether to show iteration steps and train error.
	 */
	public PairedGlobalLLORMA(int uc, int ic, double max, double min, int fc, 
			double lr, double r, int iter, int mm, int kt, double kw, int loss, 
			SparseMatrix tm, RegularizedSVD base, int ml, boolean verbose) {
		userCount = uc;
		itemCount = ic;
		maxValue = max;
		minValue = min;
		
		featureCount = fc;
		learningRate = lr;
		regularizer = r;
		maxIter = iter;
		
		modelMax = mm;
		kernelType = kt;
		kernelWidth = kw;
		lossCode = loss;
		testMatrix = tm;
		baseline = base;
		
		multiThreadCount = ml;
		
		showProgress = verbose;
	}
	
	/*========================================
	 * Model Builder
	 *========================================*/
	/**
	 * Build a model with given training set.
	 * 
	 * @param rateMatrix The rating matrix with train data.
	 */
	@Override
	public void buildModel(SparseMatrix rm) {
		rateMatrix = rm;
		
		validationRatio = 0.1;
		makeValidationSet(validationRatio);
		
		// Preparing data structures:
		localUserFeatures = new SparseMatrix[modelMax];
		localItemFeatures = new SparseMatrix[modelMax];
		
		int[] s_u = new int[userCount+1];
		for (int u = 1; u <= userCount; u++) {
			s_u[u] = 0;
			int[] itemList = rateMatrix.getRowRef(u).indexList();
			if (itemList != null) {
				for (int i : itemList) {
					for (int j : itemList) {
						if (rateMatrix.getValue(u, i) > rateMatrix.getValue(u, j)) {
							s_u[u]++;
						}
					}
				}
			}
		}
		
		// Preparing anchor points:
		anchorUser = new int[modelMax];
		anchorItem = new int[modelMax];
		
		for (int l = 0; l < modelMax; l++) {
			boolean done = false;
			while (!done) {
				int u_t = (int) Math.floor(Math.random() * userCount) + 1;
				int[] itemList = rateMatrix.getRow(u_t).indexList();
	
				if (itemList != null) {
					int idx = (int) Math.floor(Math.random() * itemList.length);
					int i_t = itemList[idx];
					
					anchorUser[l] = u_t;
					anchorItem[l] = i_t;
					
					done = true;
				}
			}
		}
		
		// Pre-calculating similarity:
		userSimilarity = new SparseMatrix(userCount+1, userCount+1);
		itemSimilarity = new SparseMatrix(itemCount+1, itemCount+1);
		
		weightSum = new double[userCount+1][itemCount+1];
		for (int u = 1; u <= userCount; u++) {
			for (int i = 1; i <= itemCount; i++) {
				for (int l = 0; l < modelMax; l++) {
					weightSum[u][i] += KernelSmoothing.kernelize(getUserSimilarity(anchorUser[l], u), kernelWidth, kernelType)
									 * KernelSmoothing.kernelize(getItemSimilarity(anchorItem[l], i), kernelWidth, kernelType);
				}
			}
		}
		
		// Initialize local models:
		for (int l = 0; l < modelMax; l++) {
			localUserFeatures[l] = new SparseMatrix(userCount+1, featureCount);
			localItemFeatures[l] = new SparseMatrix(featureCount, itemCount+1);
			
			for (int u = 1; u <= userCount; u++) {
				for (int r = 0; r < featureCount; r++) {
					double rdm = Math.random();
					localUserFeatures[l].setValue(u, r, rdm);
				}
			}
			for (int i = 1; i <= itemCount; i++) {
				for (int r = 0; r < featureCount; r++) {
					double rdm = Math.random();
					localItemFeatures[l].setValue(r, i, rdm);
				}
			}
		}
		
		// Learn by gradient descent:
		int round = 0;
		double prevErr = 99999;
		double currErr = 9999;
		
		while (round < maxIter) {
			// Current prediction:
			EvaluationMetrics evalPoint = this.evaluate(validationMatrix);
			SparseMatrix predicted = this.evaluate(rateMatrix).getPrediction().plus(evalPoint.getPrediction());
			
			RankEvaluator evalRank = new RankEvaluator(rateMatrix, validationMatrix, predicted);
			prevErr = currErr;
			currErr = evalRank.getLoss(lossCode);
			
			
			if (showProgress) {
				System.out.println(round + "\t" + lossCode + "\t" + featureCount + "\t" + evalRank.printOneLine() + "\t" + String.format("%.4f", evalPoint.getAveragePrecision()));
			}

			if (prevErr < currErr) {
				break;
			}
			
			// Gradient Descent for each local model in parallel:
			int modelCount = 0;
			int[] runningThreadList = new int[multiThreadCount];
			int runningThreadCount = 0;
			int waitingThreadPointer = 0;
			int nextRunningSlot = 0;
			
			PairedLLORMAUpdater[] updater = new PairedLLORMAUpdater[multiThreadCount];
			
			while (modelCount < modelMax || runningThreadCount > 0) {
				if (runningThreadCount < multiThreadCount && modelCount < modelMax) {
					SparseVector w = kernelSmoothing(userCount+1, anchorUser[modelCount], kernelType, kernelWidth, false);
					SparseVector v = kernelSmoothing(itemCount+1, anchorItem[modelCount], kernelType, kernelWidth, true);
					
					updater[nextRunningSlot] = new PairedLLORMAUpdater(localUserFeatures[modelCount], localItemFeatures[modelCount], rateMatrix, predicted, weightSum, s_u, lossCode, learningRate, regularizer, w, v);
					updater[nextRunningSlot].start();
					
					runningThreadList[runningThreadCount] = modelCount;
					runningThreadCount++;
					modelCount++;
					nextRunningSlot++;
				}
				else if (runningThreadCount > 0) {
					try {
						updater[waitingThreadPointer].join();
						
						nextRunningSlot = waitingThreadPointer;
						waitingThreadPointer = (waitingThreadPointer + 1) % multiThreadCount;
						runningThreadCount--;
					}
					catch(InterruptedException ie) {
						System.out.println("Join failed: " + ie);
					}
				}
			}
			
			round++;
		}
		
		restoreValidationSet();
	}
	
	
	/*========================================
	 * Prediction
	 *========================================*/
	/**
	 * Evaluate the designated algorithm with the given test data.
	 * 
	 * @param testMatrix The rating matrix with test data.
	 * 
	 * @return The result of evaluation, such as MAE, RMSE, and rank-score.
	 */
	@Override
	public EvaluationMetrics evaluate(SparseMatrix testMatrix) {
		SparseMatrix predicted = new SparseMatrix(userCount+1, itemCount+1);
		
		for (int u = 1; u <= userCount; u++) {
			SparseVector items = testMatrix.getRowRef(u);
			int[] itemIndexList = items.indexList();
			
			if (itemIndexList != null) {
				for (int i : itemIndexList) {
					double prediction = 0.0;
					for (int l = 0; l < modelMax; l++) {
						prediction += localUserFeatures[l].getRow(u).innerProduct(localItemFeatures[l].getCol(i))
								* KernelSmoothing.kernelize(getUserSimilarity(anchorUser[l], u), kernelWidth, kernelType)
							 	* KernelSmoothing.kernelize(getItemSimilarity(anchorItem[l], i), kernelWidth, kernelType)
							 	/ weightSum[u][i];
					}
					
					if (Double.isNaN(prediction)) {
						prediction = 0.0;
					}
					
					predicted.setValue(u, i, prediction);
				}
			}
		}
		
		return new EvaluationMetrics(testMatrix, predicted, maxValue, minValue);
	}
	
	
	/**
	 * Calculate similarity between two users, based on the global base SVD.
	 * 
	 * @param idx1 The first user's ID.
	 * @param idx2 The second user's ID.
	 * 
	 * @return The similarity value between two users idx1 and idx2.
	 */
	private static double getUserSimilarity (int idx1, int idx2) {
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
	
	/**
	 * Calculate similarity between two items, based on the global base SVD.
	 * 
	 * @param idx1 The first item's ID.
	 * @param idx2 The second item's ID.
	 * 
	 * @return The similarity value between two items idx1 and idx2.
	 */
	private static double getItemSimilarity (int idx1, int idx2) {
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
	
	/**
	 * Given the similarity, it applies the given kernel.
	 * This is done either for all users or for all items.
	 * 
	 * @param size The length of user or item vector.
	 * @param id The identifier of anchor point.
	 * @param kernelType The type of kernel.
	 * @param width Kernel width.
	 * @param isItemFeature return item kernel if yes, return user kernel otherwise.
	 * 
	 * @return The kernel-smoothed values for all users or all items.
	 */
	private SparseVector kernelSmoothing(int size, int id, int kernelType, double width, boolean isItemFeature) {
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
	
	/*========================================
	 * Train/Validation set management
	 *========================================*/
	/**
	 * Items which will be used for validation purpose are moved from rateMatrix to validationMatrix.
	 * 
	 * @param validationRatio Proportion of dataset, using for validation purpose.
	 */
	private void makeValidationSet(double validationRatio) {
		validationMatrix = new SparseMatrix(userCount+1, itemCount+1);
		
		int validationCount = (int) (rateMatrix.itemCount() * validationRatio);
		while (validationCount > 0) {
			int index = (int) (Math.random() * userCount) + 1;
			SparseVector row = rateMatrix.getRowRef(index);
			int[] itemList = row.indexList();
			
			if (itemList != null && itemList.length > 5) {
				int index2 = (int) (Math.random() * itemList.length);
				
				validationMatrix.setValue(index, itemList[index2], rateMatrix.getValue(index, itemList[index2]));
				rateMatrix.setValue(index, itemList[index2], 0.0);
				
				validationCount--;
			}
		}
	}
	
	/** Items in validationMatrix are moved to original rateMatrix. */
	private void restoreValidationSet() {
		for (int i = 1; i <= userCount; i++) {
			SparseVector row = validationMatrix.getRowRef(i);
			int[] itemList = row.indexList();
			
			if (itemList != null) {
				for (int j : itemList) {
					rateMatrix.setValue(i, j, validationMatrix.getValue(i, j));
				}
			}
		}
		
		validationMatrix = null;
	}
}
