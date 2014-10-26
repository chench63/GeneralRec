/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package recommender.dataset;

import java.util.Map;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.FileUtil;

/**
 * Matrix write utilities
 * 
 * @author Hanke Chen
 * @version $Id: MatrixFileUtil.java, v 0.1 2014-10-16 下午2:32:09 chench Exp $
 */
public final class MatrixFileUtil {

    /**
     * forbid construction method
     */
    private MatrixFileUtil() {

    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     * @param rowAssign         the row relation mapping old index to new index
     * @param colAssign         the column relation mapping old index to new index
     * @param cleanMatrix       if true, clear the input matrix to save memory
     */
    public static void write(String file, SparseMatrix matrix, Map<Integer, Integer> rowAssign,
                             Map<Integer, Integer> colAssign, boolean cleanMatrix) {
        int rowCount = matrix.length()[0];
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            for (int j : indexList) {
                int newRow = rowAssign.get(i);
                int newCol = colAssign.get(j);
                double val = matrix.getValue(i, j);

                String elemnt = newRow + "::" + newCol + "::" + String.format("%.1f", val);
                content.append(elemnt).append('\n');

                if (cleanMatrix) {
                    matrix.setValue(i, j, 0.0d);
                }
            }
        }

        FileUtil.write(file, content.toString());
    }

    /**
     * write bounds to file
     * 
     * @param file          the file to write
     * @param rowBound      the bounds of rows
     * @param colBound      the bounds of columns
     */
    public static void write(String file, int[] rowBound, int[] colBound) {
        StringBuilder content = new StringBuilder();
        content.append(rowBound[0]);
        for (int i = 1; i < rowBound.length; i++) {
            content.append(',').append(rowBound[i]);
        }
        content.append('\n');

        content.append(colBound[0]);
        for (int j = 1; j < colBound.length; j++) {
            content.append(',').append(colBound[j]);
        }
        FileUtil.write(file, content.toString());
    }

}
