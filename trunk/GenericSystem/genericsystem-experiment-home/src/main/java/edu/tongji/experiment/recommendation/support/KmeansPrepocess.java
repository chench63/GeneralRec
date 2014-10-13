/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import edu.tongji.ai.cluster.KMeansUtil;
import edu.tongji.ai.support.Cluster;
import edu.tongji.matrix.SparseMatrix;
import edu.tongji.matrix.SparseVector;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
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
    //      Commen variable
    //==========================

    /** file to store the original data*/
    public final static String SOURCE_FILE   = "E:/MovieLens/ml-10M100K/ratings.dat";

    /** file to persist the new data */
    public final static String OUTPUT_FILE   = "E:/MovieLens/ml-10M100K/cluster_cosine";

    /** Clustering Information*/
    public final static String SETTING_FILE  = "E:/MovieLens/ml-10M100K/setting";

    /** The parser to parse the dataset file  **/
    public final static Parser parser        = new MovielensRatingTemplateParser();

    /** the number of rows*/
    public final static int    userCount     = 71567;

    /** the number of cloumns*/
    public final static int    itemCount     = 10681;

    /** the number of clusters */
    public final static int    K             = 2;

    /** the max iterations*/
    public final static int    maxIterations = 20;

    public final static int    distanceType  = KMeansUtil.CONSINE_DISTANCE;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // store data into memory
        SparseMatrix original = new SparseMatrix(userCount, itemCount);
        String[] contents = FileUtil.readLines(SOURCE_FILE);
        for (String content : contents) {
            RatingVO rating = (RatingVO) parser.parse(content);
            if (rating != null) {
                original.setValue(rating.getUsrId() - 1, rating.getMovieId() - 1,
                    rating.getRatingReal());
            }
        }

        // cluster the rows
        Cluster[] resultSet = KMeansUtil.cluster(original, K, maxIterations, distanceType);
        int[] rowBound = new int[K];
        rowBound[0] = resultSet[0].size();
        for (int k = 1; k < K; k++) {
            rowBound[k] = rowBound[k - 1] + resultSet[k].size();
        }
        rowBound[K - 1]++;

        int index = 0;
        SparseMatrix modif1 = new SparseMatrix(userCount, itemCount);
        for (Cluster cluster : resultSet) {
            for (SparseVector vect : cluster) {
                int[] indexList = vect.indexList();
                if (indexList != null) {
                    for (int j : indexList) {
                        modif1.setValue(index, j, vect.getValue(j));
                        vect.remove(j);
                    }
                    index++;
                }
            }

        }
        original = null;

        //cluster the coloum
        Cluster[] resultSet2 = KMeansUtil.cluster(modif1.transpose(), K, maxIterations,
            distanceType);
        int[] columnBound = new int[K];
        columnBound[0] = resultSet2[0].size();
        for (int k = 1; k < K; k++) {
            columnBound[k] = columnBound[k - 1] + resultSet2[k].size();
        }
        columnBound[K - 1]++;

        index = 0;
        SparseMatrix modif2 = new SparseMatrix(userCount, itemCount);
        for (Cluster cluster : resultSet2) {
            for (SparseVector vect : cluster) {
                int[] indexList = vect.indexList();
                if (indexList != null) {
                    for (int j : indexList) {
                        modif2.setValue(j, index, vect.getValue(j));
                        vect.remove(j);
                    }
                }
                index++;
            }
        }
        modif1 = null;

        //write to disk
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < userCount; i++) {
            SparseVector Ai = modif2.getRowRef(i);
            int[] indexList = Ai.indexList();

            if (indexList != null) {
                for (int j : indexList) {
                    content.append(i + 1).append("::").append(j + 1).append("::")
                        .append((int) Ai.getValue(j)).append("::0\n");
                    Ai.remove(j);
                }
            }

        }
        FileUtil.write(OUTPUT_FILE, content.toString());

        StringBuilder setting = new StringBuilder();
        setting.append(rowBound[0]);
        for (int k = 1; k < K; k++) {
            setting.append(',').append(rowBound[k]);
        }
        setting.append('\n').append(columnBound[0]);
        for (int k = 1; k < K; k++) {
            setting.append(',').append(columnBound[k]);
        }
        FileUtil.write(SETTING_FILE, setting.toString());
    }
}
