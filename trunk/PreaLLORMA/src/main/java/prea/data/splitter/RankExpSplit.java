package prea.data.splitter;

import prea.data.structure.SparseMatrix;
import prea.util.Sort;

/**
 * This class helps to split data matrix into train set and test set,
 * with constant number of training examples for each user.
 * Users with less than the constant are simply dropped.
 * 
 * @author Joonseok Lee
 * @since 2014. 6. 4
 * @version 2.0
 */
public class RankExpSplit extends DataSplitManager {
	/*========================================
	 * Constructors
	 *========================================*/
	/** Construct an instance for simple splitter. */
	public RankExpSplit(SparseMatrix originalMatrix, int userTrainCount, int minTestCount, int max, int min) {
		super(originalMatrix, max, min);
		split(userTrainCount, minTestCount);
		calculateAverage((maxValue + minValue) / 2);
	}
	
	/**
	 * Items which will be used for test purpose are moved from rateMatrix to testMatrix.
	 * 
	 * @param userTrainCount The number of training items for each user.
	 * @param minTestCount The number of test items guaranteed for each user.
	 */
	private void split(int userTrainCount, int minTestCount) {
		if (userTrainCount <= 0) {
			return;
		}
		else {
			recoverTestItems();
			
			for (int u = 1; u <= userCount; u++) {
				int[] itemList = rateMatrix.getRowRef(u).indexList();
				
				if (itemList.length >= userTrainCount + minTestCount) {
					double[] rdmList = new double[itemList.length];

					for (int t = 0; t < rdmList.length; t++) {
						rdmList[t] = Math.random();
					}
					
					Sort.kLargest(rdmList, itemList, 0, itemList.length - 1, userTrainCount);

					// (Randomly-chosen) first N items remains in rateMatrix.
					// Rest of them are moved to testMatrix.
					for (int t = userTrainCount; t < itemList.length; t++) {
						testMatrix.setValue(u, itemList[t], rateMatrix.getValue(u, itemList[t]));
						rateMatrix.setValue(u, itemList[t], 0.0);
					}
				}
				else { // drop the user both from train/test matrix
					for (int t = 0; t < itemList.length; t++) {
						testMatrix.setValue(u, itemList[t], 0.0);
						rateMatrix.setValue(u, itemList[t], 0.0);
					}
				}
			}
		}
		
		System.out.println(rateMatrix.itemCount() + "\t" + testMatrix.itemCount());
	}
}
