package prea.recommender.llorma;

import prea.data.structure.SparseVector;
import prea.data.structure.SparseMatrix;
import prea.util.RankEvaluator;

/**
 * A class for updating each local model.
 * 
 * @author Joonseok Lee
 * @since 2013. 6. 26
 * @version 1.3
 */
public class PairedLLORMAUpdater extends Thread {
	private SparseMatrix localUserFeature;
	private SparseMatrix localItemFeature;
	private int rank;
	private int userCount;
	private SparseMatrix rateMatrix;
	private SparseMatrix currentPrediction;
	private double[][] weightSum;
	private int[] s_u;
	private int lossCode;
	private double learningRate;
	private double regularizer;
	private SparseVector w;
	private SparseVector v;
	
	public PairedLLORMAUpdater(SparseMatrix luf, SparseMatrix lif, SparseMatrix rm, SparseMatrix cp,
			double[][] ws, int[] su, int loss, double lr, double r, SparseVector w0, SparseVector v0) {
		localUserFeature = luf;
		localItemFeature = lif;
		userCount = (localUserFeature.length())[0];
		rank = (localUserFeature.length())[1];
		rateMatrix = rm;
		currentPrediction = cp;
		weightSum = ws;
		s_u = su;
		lossCode = loss;
		learningRate = lr;
		regularizer = r;
		w = w0;
		v = v0;
	}
	
	public SparseMatrix getUserFeature() {
		return localUserFeature;
	}
	
	public SparseMatrix getItemFeature() {
		return localItemFeature;
	}
	
	@Override
	public void run() {
		for (int u = 1; u <= userCount; u++) {
			int[] itemIndexList = rateMatrix.getRowRef(u).indexList();
			
			if (itemIndexList != null) {
				for (int r = 0; r < rank; r++) {
					double Uuk = localUserFeature.getValue(u, r);
					double userSum = 0.0;
					
					for (int i : itemIndexList) {
						double itemSum = 0.0;
						double Vik = localItemFeature.getValue(r, i);
						double Mui = rateMatrix.getValue(u, i);
						double pred_i = currentPrediction.getValue(u, i);
						
						double Ku = w.getValue(u);
						double Ki = v.getValue(i);
						
						for (int j : itemIndexList) {
							double pred_j = currentPrediction.getValue(u, j);
							double Muj = rateMatrix.getValue(u, j);
							
							double Kj = v.getValue(j);
						
							if (Mui > Muj) {
								double dg = RankEvaluator.lossDiff(Mui, Muj, pred_i, pred_j, lossCode);
								double Vjk = localItemFeature.getValue(r, j);
								
								userSum += (Vik*Ku*Ki/weightSum[u][i] - Vjk*Ku*Kj/weightSum[u][j]) * dg;
								itemSum += Uuk*Ku*Ki/weightSum[u][i] * dg;
								
								if (Double.isInfinite(userSum)) {
									System.out.println("c");
								}
								if (Double.isInfinite(itemSum)) {
									System.out.println("d");
								}
							}
							
							else if (Mui < Muj) {
								double dg  = RankEvaluator.lossDiff(Muj, Mui, pred_j, pred_i, lossCode);
								itemSum -= Uuk*Ku*Ki/weightSum[u][i] * dg;
							}
						}
					
						// Update item profiles:
						if (itemSum != 0) {
							localItemFeature.setValue(r, i, Vik - learningRate *(itemSum / (double) userCount / s_u[u] + 2*regularizer*Vik));
							if(Double.isNaN(Vik - learningRate *(itemSum / (double) userCount / s_u[u] + 2*regularizer*Vik))) {
								System.out.println("b");
							}
						}
					}
				
					// Update user profiles:
					if (userSum != 0) {
						localUserFeature.setValue(u, r, Uuk - learningRate *(userSum / (double) userCount / s_u[u] + 2*regularizer*Uuk));
						if(Double.isNaN(Uuk - learningRate *(userSum / (double) userCount / s_u[u] + 2*regularizer*Uuk))) {
							System.out.println("a");
						}
					}
				}
			}
		}
	}
}
