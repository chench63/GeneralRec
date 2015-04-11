/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.util.FileUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: Clustering2Gnuplot.java, v 0.1 2015-3-2 下午2:10:24 chench Exp $
 */
public final class KMean2Gnuplot {

    //==========================
    //      Common variable
    //==========================
    /** file to store the original data and cocluster directory, make sure the data is compact.*/
    public final static String[] ROOTDIRS         = { "E:/MovieLens/ml-1m/1/" };

    /** file to store the target gnuplot data.*/
    public final static String   TARGETDIR        = "C:/Users/ppiachen/Desktop/Graph/";

    /** the nickname of distance type involved*/
    public final static String[] DIVERGENCE_DIR   = { "EU"
                                                  //, "SI" 
                                                  };

    /** the number of  row classes*/
    public final static int[]    K                = { 3 };

    /** the number of column classes*/
    public final static int[]    L                = { 3 };

    /** the number of rows*/
    public final static int      rowCount         = 6040;

    /** the number of columns*/
    public final static int      colCount         = 3706;

    /** Dataset type*/
    public final static int      MOVIE_1M_NETFLIX = 101;
    public final static int      MOVIE_10M        = 102;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // coclustering
        for (String rootDir : ROOTDIRS) {
            // load dataset
            String sourceFile = rootDir + "trainingset";
            String targetCoclusterRoot = rootDir + "Kmeanspp/";
            //            String targetCoclusterRoot = rootDir + "KmeansppLimited/";

            for (int diverIndx = 0; diverIndx < DIVERGENCE_DIR.length; diverIndx++) {
                for (int k : K) {
                    for (int l : L) {
                        String indenty = (new StringBuilder()).append(DIVERGENCE_DIR[diverIndx])
                            .append('_').append(k).append('_').append(l).toString();

                        String settingFile = (new StringBuilder(targetCoclusterRoot))
                            .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR).append("SETTING")
                            .toString();
                        String rowMappingFile = (new StringBuilder(targetCoclusterRoot))
                            .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR).append("RM")
                            .toString();
                        String colMappingFile = (new StringBuilder(targetCoclusterRoot))
                            .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR).append("CM")
                            .toString();

                        transfer(sourceFile, settingFile, rowMappingFile, colMappingFile, indenty,
                            MOVIE_1M_NETFLIX);
                        //                        transfer(sourceFile, settingFile, rowMappingFile, colMappingFile, indenty,
                        //                            MOVIE_10M);
                    }
                }
            }
        }
    }

    public static void transfer(String sourceFile, String settingFile, String rowMappingFile,
                                String colMappingFile, String indenty, int dataType) {
        SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, settingFile, rowMappingFile,
            colMappingFile, rowCount, colCount, null);
        //        SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, rowCount, colCount, null);

        int fileCount = (dataType == MOVIE_1M_NETFLIX) ? 5 : 10;
        String[] targetFile = new String[fileCount];
        StringBuilder[] content = new StringBuilder[fileCount];
        for (int i = 0; i < fileCount; i++) {
            targetFile[i] = TARGETDIR + indenty + "_" + i;
            content[i] = new StringBuilder();
        }

        double minRating = (dataType == MOVIE_1M_NETFLIX) ? 1.0 : 0.5;
        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = rateMatrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            for (int j : indexList) {
                double val = rateMatrix.getValue(i, j);
                int targetIndex = (int) (val / minRating) - 1;
                content[targetIndex].append(i).append('\t').append(j).append('\n');
            }
        }

        for (int i = 0; i < fileCount; i++) {
            FileUtil.writeAsAppend(targetFile[i], content[i].toString());
        }

    }
}
