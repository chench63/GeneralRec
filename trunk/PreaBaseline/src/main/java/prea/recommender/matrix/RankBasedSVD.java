package prea.recommender.matrix;

import prea.data.structure.SparseMatrix;
import prea.util.EvaluationMetrics;
import prea.util.RankEvaluator;

/**
 * This is a class implementing Rank-based Regularized SVD.
 * 
 * @author Joonseok Lee
 * @since 2014. 6. 4
 * @version 2.0
 */
public class RankBasedSVD extends MatrixFactorizationRecommender {
	public SparseMatrix rateMatrix;
	public SparseMatrix testMatrix;
	
	private static final long serialVersionUID = 4001;
	private static int[] s_u;
	private int lossCode;
	private boolean preset;
	
	private double lastError;
	private boolean continuing;
	
	/*========================================
	 * Constructors
	 *========================================*/
	/**
	 * Construct a matrix-factorization model with the given data.
	 * 
	 * @param uc The number of users in the dataset.
	 * @param ic The number of items in the dataset.
	 * @param max The maximum rating value in the dataset.
	 * @param min The minimum rating value in the dataset.
	 * @param fc The number of features used for describing user and item profiles.
	 * @param lr Learning rate for gradient-based or iterative optimization.
	 * @param r Controlling factor for the degree of regularization. 
	 * @param m Momentum used in gradient-based or iterative optimization.
	 * @param iter The maximum number of iterations.
	 * @param loss The loss function code to be minimized.
	 * @param tm A pointer to the test matrix. Never use this for training!
	 * @param preU The initial user features. Set as 'null' if random assignment is wanted.
	 * @param preV The initial item features. Set as 'null' if random assignment is wanted.
	 * @param verbose Indicating whether to show iteration steps and train error.
	 */
	public RankBasedSVD(int uc, int ic, double max, double min, int fc, double lr, double r, double m, int iter, int loss, SparseMatrix tm, SparseMatrix preU, SparseMatrix preV, boolean verbose) {
		super(uc, ic, max, min, fc, lr, r, m, iter, verbose);
		lossCode = loss;
		testMatrix = tm;
		lastError = Double.MAX_VALUE;
		continuing = true;
		
		if (preU != null && preV != null) {
			preset = true;
			
			userFeatures = new SparseMatrix(userCount+1, featureCount);
			itemFeatures = new SparseMatrix(featureCount, itemCount+1);
			
			// Initialize user/item features:
			for (int u = 1; u <= userCount; u++) {
				for (int f = 0; f < featureCount; f++) {
					userFeatures.setValue(u, f, preU.getValue(u, f));
				}
			}
			for (int i = 1; i <= itemCount; i++) {
				for (int f = 0; f < featureCount; f++) {
					itemFeatures.setValue(f, i, preV.getValue(f, i));
				}
			}
		}
	}
	
	/*========================================
	 * Model Builder
	 *========================================*/
	/**
	 * Build a model with given training set.
	 * 
	 * @param rateMatrix Training data set.
	 */
	@Override
	public void buildModel(SparseMatrix rateMatrix) {
		this.rateMatrix = rateMatrix;
		
		if (!preset) {
			super.buildModel(rateMatrix);
		}
		
		// Preparing data structure:
		s_u = new int[userCount+1];
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
		
		
		int round = 0;

		while (continuing && round < maxIter) {
			// Intermediate evaluation:
			EvaluationMetrics evalPointTrain = this.evaluate(rateMatrix);
			EvaluationMetrics evalPointTest = this.evaluate(testMatrix);
			RankEvaluator evalRank = new RankEvaluator(rateMatrix, testMatrix, evalPointTrain.getPrediction().plus(evalPointTest.getPrediction()));
			if (showProgress) {
				System.out.println(round + "\t" + lossCode + "\t" + featureCount + "\t" + evalRank.printOneLine() + "\t" + String.format("%.4f", evalPointTest.getAveragePrecision()));
			}
			
			// Gradient Descent:
			for (int u = 1; u <= userCount; u++) {
				int[] itemIndexList = rateMatrix.getRowRef(u).indexList();
				
				if (itemIndexList != null) {
					for (int r = 0; r < featureCount; r++) {
						double Uuk = userFeatures.getValue(u, r);
						double userSum = 0.0;
						int userCaseCount = s_u[u];
						
						for (int i : itemIndexList) {
							double itemSum = 0.0;
							double Vik = itemFeatures.getValue(r, i);
							double pred_i = userFeatures.getRowRef(u).innerProduct(itemFeatures.getColRef(i));
							
							for (int j : itemIndexList) {
								double pred_j = userFeatures.getRowRef(u).innerProduct(itemFeatures.getColRef(j));
								double Mui = rateMatrix.getValue(u, i);
								double Muj = rateMatrix.getValue(u, j);
								
								if (Mui > Muj) {
									double dg = RankEvaluator.lossDiff(Mui, Muj, pred_i, pred_j, lossCode);
									double Vjk = itemFeatures.getValue(r, j);
									
									userSum += (Vik - Vjk) * dg;
									itemSum += Uuk * dg;
									
									if (Double.isInfinite(userSum)) {
										System.out.println("c");
									}
									if (Double.isInfinite(itemSum)) {
										System.out.println("d");
									}
								}
								
								else if (Mui < Muj) {
									double dg  = RankEvaluator.lossDiff(Muj, Mui, pred_j, pred_i, lossCode);
									itemSum -= Uuk * dg;
								}
							}
							
							// Update item profiles:
							if (itemSum != 0) {
								itemFeatures.setValue(r, i, Vik - learningRate/Math.sqrt(round+1) *((itemSum / (double) userCount / (double) userCaseCount) + 2*regularizer*Vik));
								if(Double.isNaN(Vik - learningRate/Math.sqrt(round+1) *((itemSum / (double) userCount / (double) userCaseCount) + 2*regularizer*Vik))) {
									System.out.println("b");
								}
							}
						}
						
						// Update user profiles:
						if (userSum != 0) {
							userFeatures.setValue(u, r, Uuk - learningRate/Math.sqrt(round+1) *(userSum / (double) userCount / (double) userCaseCount + 2*regularizer*Uuk));
							if(Double.isNaN(Uuk - learningRate/Math.sqrt(round+1) *(userSum / (double) userCount / (double) userCaseCount + 2*regularizer*Uuk))) {
								System.out.println("a");
							}
						}
					}
				}
			}
			
			round++;
			
			
			// Stop decision:
			if (evalRank.getLoss(lossCode) - lastError > -0.0001) {
				continuing = false;
			}
			else {
				lastError = evalRank.getLoss(lossCode);
			}
		}
	}
}
