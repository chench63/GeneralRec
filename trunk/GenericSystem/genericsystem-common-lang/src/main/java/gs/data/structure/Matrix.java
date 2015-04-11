/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package gs.data.structure;

/**
 * This interface contains basic matrix operation
 * 
 * @author Hanke Chen
 * @version $Id: Matrix.java, v 0.1 2015-1-19 下午2:57:11 chench Exp $
 */
public interface Matrix {

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
    public double getValue(int i, int j);

    /**
     * Set a new value at the given index.
     * 
     * @param i The row index to store new value.
     * @param j The column index to store new value.
     * @param value The value to store.
     */
    public void setValue(int i, int j, double value);

}
