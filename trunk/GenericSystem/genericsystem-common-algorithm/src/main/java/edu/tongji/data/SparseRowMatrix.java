package edu.tongji.data;

import java.io.Serializable;

/**
 * This class implements sparse matrix, containing empty values for most space.
 * 
 * @author Hanke
 * @version $Id: SparseColumnMatrix.java, v 0.1 2014-11-13 下午7:54:31 Exp $
 */
public class SparseRowMatrix implements Serializable {
    /** SerialVersionNum */
    private static final long serialVersionUID = 9003;

    /** The number of rows. */
    private int               M;
    /** The number of columns. */
    private int               N;
    /** The array of row references. */
    private SparseVector[]    rows;

    /*
     * ======================================== Constructors
     * ========================================
     */
    /**
     * Construct an empty sparse matrix, with a given size.
     * 
     * @param m
     *            The number of rows.
     * @param n
     *            The number of columns.
     */
    public SparseRowMatrix(int m, int n) {
        this.M = m;
        this.N = n;
        rows = new SparseVector[M];

        for (int i = 0; i < M; i++) {
            rows[i] = new SparseVector(N);
        }

    }

    /**
     * Construct an empty sparse matrix, with data copied from another sparse
     * matrix.
     * 
     * @param sm
     *            The matrix having data being copied.
     */
    public SparseRowMatrix(SparseRowMatrix sm) {
        this.M = sm.M;
        this.N = sm.N;
        rows = new SparseVector[M];

        for (int i = 0; i < M; i++) {
            rows[i] = sm.getRow(i);
        }
    }

    /*
     * ======================================== Getter/Setter
     * ========================================
     */
    /**
     * Retrieve a stored value from the given index.
     * 
     * @param i
     *            The row index to retrieve.
     * @param j
     *            The column index to retrieve.
     * @return The value stored at the given index.
     */
    public double getValue(int i, int j) {
        return rows[i].getValue(j);
    }

    /**
     * Set a new value at the given index.
     * 
     * @param i
     *            The row index to store new value.
     * @param j
     *            The column index to store new value.
     * @param value
     *            The value to store.
     */
    public void setValue(int i, int j, double value) {
        if (value == 0.0) {
            rows[i].remove(j);
        } else {
            rows[i].setValue(j, value);
        }
    }

    /**
     * Return a reference of a given row. Make sure to use this method only for
     * read-only purpose.
     * 
     * @param index
     *            The row index to retrieve.
     * @return A reference to the designated row.
     */
    public SparseVector getRowRef(int index) {
        return rows[index];
    }

    /**
     * Return a copy of a given row. Use this if you do not want to affect to
     * original data.
     * 
     * @param index
     *            The row index to retrieve.
     * @return A reference to the designated row.
     */
    public SparseVector getRow(int index) {
        SparseVector newVector = this.rows[index].copy();

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
     * Actual number of items in the matrix.
     * 
     * @return The number of items in the matrix.
     */
    public int itemCount() {
        int sum = 0;
        for (int i = 0; i < M; i++) {
            sum += rows[i].itemCount();
        }
        return sum;
    }

    /**
     * Set a new size of the matrix.
     * 
     * @param m
     *            The new row count.
     * @param n
     *            The new column count.
     */
    public void setSize(int m, int n) {
        this.M = m;
        this.N = n;
    }

    /**
     * clear all elements in matrix
     */
    public void clear() {
        for (SparseVector row : rows) {
            row.clear();
        }
    }

    /**
     * Return items in the diagonal in vector form.
     * 
     * @return Diagonal vector from the matrix.
     */
    public SparseVector diagonal() {
        SparseVector v = new SparseVector(Math.min(this.M, this.N));

        for (int i = 0; i < Math.min(this.M, this.N); i++) {
            double value = this.getValue(i, i);
            if (value > 0.0) {
                v.setValue(i, value);
            }
        }

        return v;
    }

    /**
     * The value of maximum element in the matrix.
     * 
     * @return The maximum value.
     */
    public double max() {
        double curr = Double.MIN_VALUE;

        for (int i = 0; i < this.M; i++) {
            SparseVector v = this.getRowRef(i);
            if (v.itemCount() > 0) {
                double rowMax = v.max();
                if (v.max() > curr) {
                    curr = rowMax;
                }
            }
        }

        return curr;
    }

    /**
     * The value of minimum element in the matrix.
     * 
     * @return The minimum value.
     */
    public double min() {
        double curr = Double.MAX_VALUE;

        for (int i = 0; i < this.M; i++) {
            SparseVector v = this.getRowRef(i);
            if (v.itemCount() > 0) {
                double rowMin = v.min();
                if (v.min() < curr) {
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
            SparseVector v = this.getRowRef(i);
            sum += v.sum();
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
            int[] itemList = this.getRowRef(i).indexList();
            if (itemList != null) {
                for (int j : itemList) {
                    sum += Math.pow(this.getValue(i, j) - avg, 2);
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

    /**
     * part of the matrix w.r.t the given row and column index set
     * 
     * @param rows the rows to be partitioned to sub-matrix
     * @param cols the columns to be partitioned to sub-matrix
     * @return the sub-matrix with the given row and column index set
     */
    public SparseRowMatrix partition(int[] rows, int[] cols) {
        if (rows == null && cols == null) {
            return this;
        }

        //construct column tables
        boolean[] colTable = new boolean[N];
        for (int col : cols) {
            colTable[col] = true;
        }

        //copy data
        SparseRowMatrix result = new SparseRowMatrix(M, N);
        for (int row : rows) {
            SparseVector Fr = this.getRowRef(row);
            int[] indexList = Fr.indexList();
            if (indexList == null) {
                continue;
            }

            for (int col : indexList) {
                if (colTable[col]) {
                    double val = this.getValue(row, col);
                    result.setValue(row, col, val);
                }
            }
        }
        return result;
    }

    /**
     * part of the matrix w.r.t the given row and column index set
     * 
     * @param rows the rows to be partitioned to sub-matrix
     * @param cols the columns to be partitioned to sub-matrix
     * @return the sub-matrix with the given row and column index set
     */
    public SparseRowMatrix partitionWithBorderDependency(int[] rows, int[] cols) {
        if (rows == null && cols == null) {
            return this;
        }

        //construct row and column tables
        boolean[] rowTable = new boolean[M];
        for (int row : rows) {
            rowTable[row] = true;
        }

        boolean[] colTable = new boolean[N];
        for (int col : cols) {
            colTable[col] = true;
        }

        //copy data
        SparseRowMatrix result = new SparseRowMatrix(M, N);
        for (int u = 0; u < M; u++) {
            SparseVector Mu = this.getRowRef(u);
            int[] indexList = Mu.indexList();

            if (rowTable[u]) {
                for (int i : indexList) {
                    result.setValue(u, i, Mu.getValue(i));
                }
            } else {
                for (int i : indexList) {
                    if (colTable[i]) {
                        result.setValue(u, i, Mu.getValue(i));
                    }
                }
            }
        }
        return result;
    }

    /**
     * part of the matrix w.r.t the given row and column index set
     * 
     * @param rows the rows to be partitioned to sub-matrix
     * @param cols the columns to be partitioned to sub-matrix
     * @return the sub-matrix with the given row and column index set
     */
    public MatlabFasionSparseMatrix partition(int nnz, int[] rows, int[] cols) {
        //construct row tables
        boolean[] colTable = new boolean[N];
        for (int col : cols) {
            colTable[col] = true;
        }

        //copy data
        MatlabFasionSparseMatrix result = new MatlabFasionSparseMatrix(nnz);
        for (int row : rows) {
            SparseVector Fr = this.getRowRef(row);
            int[] indexList = Fr.indexList();
            if (indexList == null) {
                continue;
            }

            for (int col : indexList) {
                if (colTable[col]) {
                    double val = this.getValue(row, col);
                    result.setValue(row, col, val);
                }
            }
        }
        result.reduceMem();
        return result;
    }

    /**
     * convert to object of SparseMatrix
     * 
     * @return
     */
    public SparseMatrix toSparseMatrix() {
        SparseMatrix result = new SparseMatrix(M, N);
        for (int i = 0; i < M; i++) {
            SparseVector Fi = rows[i];
            int[] indexList = Fi.indexList();
            if (indexList == null) {
                continue;
            }

            for (int j : indexList) {
                result.setValue(i, j, Fi.getValue(j));
            }
        }
        return result;
    }
}
