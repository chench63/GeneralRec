/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import org.apache.log4j.Logger;

import edu.tongji.ai.cluster.KMeansUtil;
import edu.tongji.ai.support.Cluster;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.RatingVO;

/**
 * Divide the data into K clusters,
 * and resort the original sequentce of dataset w.r.t the new clusters
 * 
 * @author Hanke Chen
 * @version $Id: KmeansPrepocess.java, v 0.1 2014-10-12 上午11:36:40 chench Exp $
 */
public class KmeansPrepocess {

    //==========================
    //      Common variable
    //==========================

    /** file to store the original data*/
    public final static String  SOURCE_FILE   = "E:/MovieLens/ml-1m/r/ratings.dat";

    /** file to persist the new data */
    public final static String  OUTPUT_FILE   = "E:/MovieLens/ml-1m/ratings.dat";

    /** Clustering Information*/
    public final static String  SETTING_FILE  = "E:/MovieLens/ml-1m/setting";

    /** The parser to parse the dataset file  **/
    public final static Parser  parser        = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public static int           userCount     = 6040;

    /** the number of cloumns*/
    public static int           itemCount     = 3952;

    /** the number of clusters w.r.t row */
    public final static int     K_ROW         = 2;

    /** the number of clusters w.r.t row */
    public final static int     K_COL         = 2;

    /** the max iterations*/
    public final static int     maxIterations = 10;

    public final static int     distanceType  = KMeansUtil.CONSINE_DISTANCE;

    /** logger */
    private final static Logger logger        = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        // store data into memory
        LoggerUtil.info(logger, "load into memory.");
        SparseMatrix original = new SparseMatrix(userCount, itemCount);
        String[] contents = FileUtil.readLines(SOURCE_FILE);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                int row = rating.getUsrId() - 1;
                int col = rating.getMovieId() - 1;
                original.setValue(row, col, rating.getRatingReal());
            }
        }

        // cluster the rows
        LoggerUtil.info(logger, "divide rows.");
        int[] rowBound = new int[K_ROW];
        SparseMatrix rowClusteredMatrix = divideRows(original, K_ROW, rowBound);
        original = null;

        //cluster the coloum
        LoggerUtil.info(logger, "divide column.");
        int[] columnBound = new int[K_COL];
        rowClusteredMatrix.selfTranspose();
        SparseMatrix clusterMatrix = divideRows(rowClusteredMatrix, K_COL, columnBound);
        rowClusteredMatrix = null;

        //write to disk
        LoggerUtil.info(logger, "Write back.");
        clusterMatrix.selfTranspose();
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < userCount; i++) {
            SparseVector Mi = clusterMatrix.getRowRef(i);
            int[] indexList = Mi.indexList();

            for (int j : indexList) {
                int userId = i + 1;
                int itemId = j + 1;

                content.append(userId).append("::").append(itemId).append("::")
                    .append(String.format("%.1f", clusterMatrix.getValue(i, j))).append("::0\n");
            }

        }
        FileUtil.write(OUTPUT_FILE, content.toString());
        content = null;

        //write setting file to disk
        StringBuilder setting = new StringBuilder();
        setting.append(rowBound[0]);
        for (int k = 1; k < K_ROW; k++) {
            setting.append(',').append(rowBound[k]);
        }
        setting.append('\n').append(columnBound[0]);
        for (int k = 1; k < K_COL; k++) {
            setting.append(',').append(columnBound[k]);
        }
        FileUtil.write(SETTING_FILE, setting.toString());
    }

    /**
     * cluster rows
     * 
     * @param rateMatrix
     */
    public static SparseMatrix divideRows(final SparseMatrix rateMatrix, int K, int[] bound) {
        //divide data into K classes
        Cluster[] resultSet = KMeansUtil.cluster(rateMatrix, K, maxIterations, distanceType);

        bound[0] = resultSet[0].size();
        for (int k = 1; k < K; k++) {
            bound[k] = bound[k - 1] + resultSet[k].size();
        }
        bound[K - 1]++;

        //forge a new matrix, where elements in each class stay compact
        SparseMatrix newMatrix = new SparseMatrix(rateMatrix);
        int index = 0;
        for (Cluster cluster : resultSet) {
            int[] userLen = cluster.indexList();

            for (int u : userLen) {
                SparseVector Mu = rateMatrix.getRowRef(u);
                int[] itemLen = Mu.indexList();
                if (itemLen == null) {
                    continue;
                }

                for (int i : itemLen) {
                    newMatrix.setValue(index, i, Mu.getValue(i));
                }
                index++;
            }
        }

        return newMatrix;
    }

}
