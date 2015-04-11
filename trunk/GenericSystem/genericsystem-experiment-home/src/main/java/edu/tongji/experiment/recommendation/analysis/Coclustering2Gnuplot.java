/**
 * Tongji Edu.
 * Copyright (c) 2004-2015 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.analysis;

import prea.util.MatrixFileUtil;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.ml.CoclusterUtil;
import edu.tongji.util.FileUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: Coclustering2Gnuplot.java, v 0.1 2015-3-2 下午3:38:20 chench Exp $
 */
public final class Coclustering2Gnuplot {
    //==========================
    //      Common variable
    //==========================
    /** file to store the original data and cocluster directory, make sure the data is compact.*/
    public final static String[] ROOTDIRS       = { "E:/MovieLens/ml-1m/2/" };

    /** file to store the target gnuplot data.*/
    public final static String   TARGETDIR      = "C:/Users/ppiachen/Desktop/Graph/";

    /** the nickname of distance type involved*/
    public final static String[] DIVERGENCE_DIR = { "EW", "IW" };

    public final static int[]    CONSTRAINTS    = { CoclusterUtil.C_5 };

    /** the number of  row classes*/
    public final static int[]    K              = { 5 };

    /** the number of column classes*/
    public final static int[]    L              = { 5 };

    /** the number of rows*/
    public final static int      rowCount       = 6040;

    /** the number of columns*/
    public final static int      colCount       = 3706;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        // coclustering
        for (String rootDir : ROOTDIRS) {
            // load dataset
            String sourceFile = rootDir + "trainingset";
            String targetCoclusterRoot = rootDir + "Cocluster/";

            for (int diverIndx = 0; diverIndx < DIVERGENCE_DIR.length; diverIndx++) {
                for (int consts : CONSTRAINTS) {
                    for (int k : K) {
                        for (int l : L) {
                            String indenty = (new StringBuilder())
                                .append(DIVERGENCE_DIR[diverIndx]).append(consts).append('_')
                                .append(k).append('_').append(l).toString();

                            String settingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR)
                                .append("SETTING").toString();
                            String rowMappingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR).append("RM")
                                .toString();
                            String colMappingFile = (new StringBuilder(targetCoclusterRoot))
                                .append(indenty).append(FileUtil.UNION_DIR_SEPERATOR).append("CM")
                                .toString();

                            transfer(sourceFile, settingFile, rowMappingFile, colMappingFile,
                                indenty);
                        }
                    }
                }
            }
        }
    }

    public static void transfer(String sourceFile, String settingFile, String rowMappingFile,
                                String colMappingFile, String indenty) {
        String targetFile = TARGETDIR + indenty;
        SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, settingFile, rowMappingFile,
            colMappingFile, rowCount, colCount, null);
        //        SparseMatrix rateMatrix = MatrixFileUtil.read(sourceFile, rowCount, colCount, null);

        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = rateMatrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            StringBuilder content = new StringBuilder();
            for (int j : indexList) {
                String elemnt = i + "\t" + j;
                content.append(elemnt).append('\n');
            }
            FileUtil.writeAsAppend(targetFile, content.toString());
        }
    }

}
