/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: DataSetCompact.java, v 0.1 2014-10-13 下午8:53:18 chench Exp $
 */
public class DatasetCompact {

    //==========================
    //      Common variable
    //==========================

    /** file to store the original data*/
    public final static String  SOURCE_FILE = "E:/MovieLens/ml-10M100K/r/ratings.dat";

    /** file to persist the new data */
    public final static String  OUTPUT_FILE = "E:/MovieLens/ml-10M100K/ratings.dat";

    /** The parser to parse the dataset file  **/
    public final static Parser  parser      = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public static int           maxRow      = 81567;

    /** the number of cloumns*/
    public static int           maxCol      = 65681;

    /** logger */
    private final static Logger logger      = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //read source file
        LoggerUtil.info(logger, "1. read source file.");
        SparseMatrix matrix = new SparseMatrix(maxRow, maxCol);
        String[] lines = FileUtil.readLines(SOURCE_FILE);

        int nextRow = 0;
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            int row = rating.getUsrId();
            int col = rating.getMovieId();
            double val = rating.getRatingReal();
            matrix.setValue(row, col, val);

            if (!rowAssig.containsKey(row)) {
                rowAssig.put(row, nextRow);
                nextRow++;
            }
        }

        int nextCol = 0;
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < maxCol; i++) {
            SparseVector col = matrix.getColRef(i);

            if (col.indexList() != null) {
                colAssig.put(i, nextCol);
                nextCol++;
            }
        }

        //iterate the matrix
        LoggerUtil.info(logger, "2. iterate the matrix.   RowCount: " + nextRow + " ColCount: "
                                + nextCol);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < maxRow; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] colIndex = Mi.indexList();
            if (colIndex == null) {
                continue;
            }

            for (int j : colIndex) {
                int compactRow = rowAssig.get(i);
                int compactCol = colAssig.get(j);
                double val = matrix.getValue(i, j);

                String newElem = compactRow + "::" + compactCol + "::" + String.format("%.1f", val);
                content.append(newElem).append('\n');

                // element in dataset cannot be zero
                if (val == 0.0) {
                    throw new RuntimeException("Dataset must be wrong!");
                } else {
                    matrix.setValue(i, j, 0.0d);
                }
            }
        }

        //write to disk
        LoggerUtil.info(logger, "3. write to disk");
        FileUtil.write(OUTPUT_FILE, content.toString());

    }
}
