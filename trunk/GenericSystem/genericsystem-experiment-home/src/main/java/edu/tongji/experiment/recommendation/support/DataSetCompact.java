/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;

import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: DataSetCompact.java, v 0.1 2014-10-13 下午8:53:18 chench Exp $
 */
public class DataSetCompact {

    //==========================
    //      Common variable
    //==========================

    /** file to store the original data*/
    public final static String SOURCE_FILE = "E:/MovieLens/ml-10M100K/t/ratings.dat";

    /** file to persist the new data */
    public final static String OUTPUT_FILE = "E:/MovieLens/ml-10M100K/t.dat";

    /** The parser to parse the dataset file  **/
    public final static Parser parser      = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public static int          userCount   = 71567;

    /** the number of cloumns*/
    public static int          itemCount   = 65681;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // store data into memory
        Map<Integer, Integer> rowValue = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colValue = new HashMap<Integer, Integer>();
        int rowIndex = 0;
        int colIndex = 0;

        SparseMatrix original = new SparseMatrix(userCount, itemCount);
        String[] contents = FileUtil.readLines(SOURCE_FILE);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                int row = rating.getUsrId() - 1;
                int col = rating.getMovieId() - 1;
                original.setValue(row, col, rating.getRatingReal());

                if (!rowValue.containsKey(row)) {
                    rowValue.put(row, rowIndex++);
                }
                if (!colValue.containsKey(col)) {
                    colValue.put(col, colIndex++);
                }
            }
        }

        // sort data in a compact matrix
        if (rowIndex != userCount | colIndex != itemCount) {
            SparseMatrix old = original;
            original = new SparseMatrix(rowIndex, colIndex);
            for (int i : rowValue.keySet()) {
                SparseVector row = old.getRowRef(i);
                int[] indexList = row.indexList();
                if (indexList == null) {
                    continue;
                }

                for (int j : indexList) {
                    double val = row.getValue(j);

                    int newRow = rowValue.get(i);
                    int newCol = colValue.get(j);
                    original.setValue(newRow, newCol, val);
                }

            }

            userCount = rowValue.size();
            itemCount = colValue.size();
            rowValue = null;
            colValue = null;
            old = null;
        }

        //write to disk
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < userCount; i++) {
            SparseVector Mi = original.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            for (int j : indexList) {
                int userId = i + 1;
                int itemId = j + 1;

                content.append(userId).append("::").append(itemId).append("::")
                    .append(String.format("%.1f", Mi.getValue(j))).append("::0\n");
            }

        }
        FileUtil.write(OUTPUT_FILE, content.toString());
    }

}
