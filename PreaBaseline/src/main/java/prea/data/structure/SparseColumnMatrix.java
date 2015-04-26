package prea.data.structure;

/**
 * 
 * @author Hanke
 * @version $Id: SparseColumnMatrix.java, v 0.1 2014-11-14 下午5:31:24 Exp $
 */
public class SparseColumnMatrix {
    /** The number of rows. */
    private int            M;
    /** The number of columns. */
    private int            N;
    /** The array of column references. */
    private SparseVector[] cols;

    /*========================================
     * Constructors
     *========================================*/
    /**
     * Construct an empty sparse matrix, with a given size.
     * 
     * @param m The number of rows.
     * @param n The number of columns.
     */
    public SparseColumnMatrix(int m, int n) {
        this.M = m;
        this.N = n;
        cols = new SparseVector[N];

        for (int j = 0; j < N; j++) {
            cols[j] = new SparseVector(M);
        }
    }

    /**
     * Construct an empty sparse matrix, with data copied from another sparse matrix.
     * 
     * @param sm The matrix having data being copied.
     */
    public SparseColumnMatrix(SparseColumnMatrix sm) {
        this.M = sm.M;
        this.N = sm.N;
        cols = new SparseVector[N];

        for (int j = 0; j < N; j++) {
            cols[j] = sm.getCol(j);
        }
    }

    /*========================================
     * Getter/Setter
     *========================================*/
    /**
     * Retrieve a stored value from the given index.
     * 
     * @param i The row index to retrieve.
     * @param j The column index to retrieve.
     * @return The value stored at the given index.
     */
    public double getValue(int i, int j) {
        return cols[j].getValue(i);
    }

    /**
     * Set a new value at the given index.
     * 
     * @param i The row index to store new value.
     * @param j The column index to store new value.
     * @param value The value to store.
     */
    public void setValue(int i, int j, double value) {
        if (value == 0.0) {
            cols[j].remove(i);
        } else {
            cols[j].setValue(i, value);
        }
    }

    /**
     * Return a reference of a given column.
     * Make sure to use this method only for read-only purpose.
     * 
     * @param index The column index to retrieve.
     * @return A reference to the designated column.
     */
    public SparseVector getColRef(int index) {
        return cols[index];
    }

    /**
     * Return a copy of a given column.
     * Use this if you do not want to affect to original data.
     * 
     * @param index The column index to retrieve.
     * @return A reference to the designated column.
     */
    public SparseVector getCol(int index) {
        SparseVector newVector = this.cols[index].copy();

        return newVector;
    }

    /**
     * Capacity of this matrix.
     * 
     * @return An array containing the length of this matrix. Index 0 contains
     *         row count, while index 1 column count.
     */
    public int[] length() {
        int[] lengthArray = new int[2];

        lengthArray[0] = this.M;
        lengthArray[1] = this.N;

        return lengthArray;
    }

    /**
     * convert to object of SparseMatrix
     * 
     * @return
     */
    public SparseMatrix toSparseMatrix() {
        SparseMatrix result = new SparseMatrix(M, N);
        for (int j = 0; j < N; j++) {
            SparseVector Gj = cols[j];
            int[] indexList = Gj.indexList();
            if (indexList == null) {
                continue;
            }

            for (int i : indexList) {
                result.setValue(i, j, Gj.getValue(j));
            }
        }
        return result;
    }

    /**
     * clear all elements in matrix
     */
    public void clear() {
        for (SparseVector col : cols) {
            col.clear();
        }
    }
}
