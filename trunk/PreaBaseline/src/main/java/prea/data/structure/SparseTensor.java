package prea.data.structure;

/**
 * This class implements sparse matrix, containing empty values for most space.
 * 
 * @author Joonseok Lee
 * @since 2013. 5. 28
 * @version 1.1
 */
public class SparseTensor {
	/** The number of rows. */
	private int M;
	/** The number of columns. */
	private int N;
	/** The number of floors. */
	private int L;
	/** The array of row references. */
	private SparseMatrix[] rows;
//	/** The array of column references. */
//	private SparseMatrix[] cols;
//	/** The array of floor references. */
//	private SparseMatrix[] flrs;
	
	/*========================================
	 * Constructors
	 *========================================*/
	/**
	 * Construct an empty sparse matrix, with a given size.
	 * 
	 * @param m The number of rows.
	 * @param n The number of columns.
	 * @param l The number of floors.
	 */
	public SparseTensor(int m, int n, int l) {
		this.M = m;
		this.N = n;
		this.L = l;
		rows = new SparseMatrix[M];
//		cols = new SparseMatrix[N];
//		flrs = new SparseMatrix[L];
		
		for (int i = 0; i < M; i++) {
			rows[i] = new SparseMatrix(N, L);
		}
//		for (int j = 0; j < N; j++) {
//			cols[j] = new SparseMatrix(M, L);
//		}
//		for (int k = 0; k < L; k++) {
//			flrs[k] = new SparseMatrix(M, N);
//		}
	}
	
	
	/*========================================
	 * Getter/Setter
	 *========================================*/
	/**
	 * Retrieve a stored value from the given index.
	 * 
	 * @param i The row index to retrieve.
	 * @param j The column index to retrieve.
	 * @param k The floor index to retrieve.
	 * @return The value stored at the given index.
	 */
	public double getValue(int i, int j, int k) {
		return rows[i].getValue(j, k);
	}
	
	/**
	 * Set a new value at the given index.
	 * 
	 * @param i The row index to store new value.
	 * @param j The column index to store new value.
	 * @param k The floor index to store new value.
	 * @param value The value to store.
	 */
	public void setValue(int i, int j, int k, double value) {
		rows[i].setValue(j, k, value);
//		cols[j].setValue(i, k, value);
//		flrs[k].setValue(i, j, value);
	}
	
	/**
	 * Return a reference of a given row.
	 * Make sure to use this method only for read-only purpose.
	 * 
	 * @param index The row index to retrieve.
	 * @return A reference to the designated row.
	 */
	public SparseMatrix getRowRef(int index) {
		return rows[index];
	}
	
//	/**
//	 * Return a reference of a given column.
//	 * Make sure to use this method only for read-only purpose.
//	 * 
//	 * @param index The column index to retrieve.
//	 * @return A reference to the designated column.
//	 */
//	public SparseMatrix getColRef(int index) {
//		return cols[index];
//	}
//	
//	/**
//	 * Return a reference of a given floor.
//	 * Make sure to use this method only for read-only purpose.
//	 * 
//	 * @param index The floor index to retrieve.
//	 * @return A reference to the designated floor.
//	 */
//	public SparseMatrix getFlrRef(int index) {
//		return flrs[index];
//	}
//	
	
	/*========================================
	 * Properties
	 *========================================*/
	/**
	 * Capacity of this tensor.
	 * 
	 * @return An array containing the length of this tensor.
	 * Index 0 contains row count, index 1 column count, and index 2 floor count.
	 */
	public int[] length() {
		int[] lengthArray = new int[3];
		
		lengthArray[0] = this.M;
		lengthArray[1] = this.N;
		lengthArray[2] = this.L;
		
		return lengthArray;
	}

	/**
	 * Actual number of items in the tensor.
	 * 
	 * @return The number of items in the tensor.
	 */
	public int itemCount() {
		int sum = 0;
		
		for (int i = 0; i < M; i++) {
			sum += rows[i].itemCount();
		}
		
		return sum;
	}
	
	/**
	 * Set a new size of the tensor.
	 * 
	 * @param m The new row count.
	 * @param n The new column count.
	 */
	public void setSize(int m, int n, int l) {
		this.M = m;
		this.N = n;
		this.L = l;
	}
	
	/**
	 * The value of maximum element in the tensor.
	 * 
	 * @return The maximum value.
	 */
	public double max() {
		double curr = Double.MIN_VALUE;
		
		for (int i = 0; i < this.M; i++) {
			SparseMatrix m = this.getRowRef(i);
			if (m.itemCount() > 0) {
				double rowMax = m.max();
				if (m.max() > curr) {
					curr = rowMax;
				}
			}
		}
		
		return curr;
	}
	
	/**
	 * The value of minimum element in the tensor.
	 * 
	 * @return The minimum value.
	 */
	public double min() {
		double curr = Double.MAX_VALUE;
		
		for (int i = 0; i < this.M; i++) {
			SparseMatrix m = this.getRowRef(i);
			if (m.itemCount() > 0) {
				double rowMin = m.min();
				if (m.min() < curr) {
					curr = rowMin;
				}
			}
		}
		
		return curr;
	}
	
	/**
	 * Sum of every element. It ignores non-existing values.
	 * 
	 * @return The sum of all elements.
	 */
	public double sum() {
		double sum = 0.0;
		
		for (int i = 0; i < this.M; i++) {
			SparseMatrix m = this.getRowRef(i);
			sum += m.sum();
		}
		
		return sum;
	}
	
	/**
	 * Average of every element. It ignores non-existing values.
	 * 
	 * @return The average value.
	 */
	public double average() {
		return this.sum() / this.itemCount();
	}
	
	/**
	 * Variance of every element. It ignores non-existing values.
	 * 
	 * @return The variance value.
	 */
	public double variance() {
		double avg = this.average();
		double sum = 0.0;
		
		for (int i = 0; i < this.M; i++) {
			for (int j = 0; j < this.N; j++) {
				int[] itemList = this.getRowRef(i).getRowRef(j).indexList();
				
				if (itemList != null) {
					for (int k : itemList) {
						sum += Math.pow(this.getValue(i, j, k) - avg, 2);
					}
				}
			}
		}
		
		return sum / this.itemCount();
	}
	
	/**
	 * Standard Deviation of every element. It ignores non-existing values.
	 * 
	 * @return The standard deviation value.
	 */
	public double stdev() {
		return Math.sqrt(this.variance());
	}
	
	
	/*========================================
	 * Tensor operations
	 *========================================*/
	/**
	 * Scalar subtraction (aX).
	 * 
	 * @param alpha The scalar value to be multiplied to this tensor.
	 * @return The resulting tensor after scaling.
	 */
	public SparseTensor scale(double alpha) {
		SparseTensor A = new SparseTensor(this.M, this.N, this.L);
		
		for (int i = 0; i < A.M; i++) {
			A.rows[i] = this.getRowRef(i).scale(alpha);
		}
//		for (int j = 0; j < A.N; j++) {
//			A.cols[j] = this.getColRef(j).scale(alpha);
//		}
//		for (int k = 0; k < A.L; k++) {
//			A.flrs[k] = this.getFlrRef(k).scale(alpha);
//		}
		
		return A;
	}
	
	/**
	 * Scalar subtraction (aX) on the tensor itself.
	 * This is used for minimizing memory usage.
	 * 
	 * @param alpha The scalar value to be multiplied to this tensor.
	 */
	public void selfScale(double alpha) {
		for (int i = 0; i < this.M; i++) {
			for (int j = 0; j < this.N; j++) {
				int[] itemList = this.getRowRef(i).getRowRef(j).indexList();
				
				if (itemList != null) {
					for (int k : itemList) {
						this.setValue(i, j, k, this.getValue(i, j, k) * alpha);
					}
				}
			}
		}
	}
	
	/**
	 * Scalar addition.
	 * @param alpha The scalar value to be added to this tensor.
	 * @return The resulting tensor after addition.
	 */
	public SparseTensor add(double alpha) {
		SparseTensor A = new SparseTensor(this.M, this.N, this.L);
		
		for (int i = 0; i < A.M; i++) {
			A.rows[i] = this.getRowRef(i).add(alpha);
		}
//		for (int j = 0; j < A.N; j++) {
//			A.cols[j] = this.getColRef(j).add(alpha);
//		}
//		for (int k = 0; k < A.L; k++) {
//			A.flrs[k] = this.getFlrRef(k).add(alpha);
//		}
		
		return A;
	}
	
	/**
	 * Scalar addition on the tensor itself.
	 * @param alpha The scalar value to be added to this tensor.
	 */
	public void selfAdd(double alpha) {
		for (int i = 0; i < this.M; i++) {
			for (int j = 0; j < this.N; j++) {
				int[] itemList = this.getRowRef(i).getRowRef(j).indexList();
				
				if (itemList != null) {
					for (int k : itemList) {
						this.setValue(i, j, k, this.getValue(i, j, k) + alpha);
					}
				}
			}
		}
	}
	
	/**
	 * Exponential of a given constant.
	 * 
	 * @param alpha The exponent.
	 * @return The resulting exponential tensor.
	 */
	public SparseTensor exp(double alpha) {
		for (int i = 0; i < this.M; i++) {
			for (int j = 0; j < this.N; j++) {
				int[] itemList = this.getRowRef(i).getRowRef(j).indexList();
			
				if (itemList != null) {
					for (int k : itemList) {
						this.setValue(i, j, k, Math.pow(alpha, this.getValue(i, j, k)));
					}
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Tensor-tensor sum (C = A + B)
	 * 
	 * @param B The tensor to be added to this tensor.
	 * @throws RuntimeException when dimensions disagree
	 * @return The resulting tensor after summation.
	 */
	public SparseTensor plus(SparseTensor B) {
		SparseTensor A = this;
		
		if (A.M != B.M || A.N != B.N || A.L != B.L)
			throw new RuntimeException("Dimensions disagree");
		
		SparseTensor C = new SparseTensor(M, N, L);
		for (int i = 0; i < M; i++) {
			C.rows[i] = A.rows[i].plus(B.rows[i]);
		}
//		for (int j = 0; j < N; j++) {
//			C.cols[j] = A.cols[j].plus(B.cols[j]);
//		}
//		for (int k = 0; k < L; k++) {
//			C.flrs[k] = C.flrs[k].plus(C.flrs[k]);
//		}
		
		return C;
	}
}
