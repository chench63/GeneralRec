/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package recommender.dataset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import edu.tongji.data.SparseMatrix;
import edu.tongji.parser.Parser;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.RatingVO;

/**
 * split datast into trainingset and testset
 * 
 * @author Hanke Chen
 * @version $Id: DatasetSplitUtil.java, v 0.1 2014-10-16 下午2:04:39 chench Exp $
 */
public final class DatasetSplitUtil {

    /**
     * forbid construction method
     */
    private DatasetSplitUtil() {

    }

    /**
     * divide dataset into training matrix and test matrix
     * 
     * @param sourceFile        the file contains data
     * @param ratio             training / total data 
     * @param rateMatrix        training matrix to store training data
     * @param testMatrix        test matrix to store test data
     * @param parser            the parser to parse the dataset file
     */
    public static void split(String sourceFile, double ratio, SparseMatrix rateMatrix,
                             SparseMatrix testMatrix, Parser parser) {
        //1. reading data
        Queue<String> contents = new LinkedList<>(Arrays.asList(FileUtil
            .readLinesByPattern(sourceFile)));

        //2. divide dataset into training matrix and test matrix
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        while (!contents.isEmpty()) {
            String content = contents.poll();
            RatingVO rating = (RatingVO) parser.parse(content);

            if (uniform.sample() >= ratio) {
                testMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            } else {
                rateMatrix.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            }
        }
    }

    /**
     * divide data into training set and test set, while
     * the distribution between them is conjugate.
     * 
     * @param sourceFile        the file contains data
     * @param ratio             training / total data 
     * @param rateMatrix        training matrix to store training data
     * @param testMatrix        test matrix to store test data
     * @param parser            the parser to parse the dataset file
     */
    public static void conjugateSplit(String sourceFile, double ratio, SparseMatrix rateMatrix,
                                      SparseMatrix testMatrix, Parser parser) {
        //1. reading data
        Queue<String> contents = new LinkedList<>(Arrays.asList(FileUtil
            .readLinesByPattern(sourceFile)));
        int totalCount = contents.size();

        //2. divide dataset into training matrix and test matrix
        Map<Float, Queue<RatingVO>> distributions = new HashMap<Float, Queue<RatingVO>>();
        while (!contents.isEmpty()) {
            String content = contents.poll();
            RatingVO rating = (RatingVO) parser.parse(content);

            Queue<RatingVO> localDistri = distributions.get(rating.getRatingReal());
            if (localDistri == null) {
                localDistri = new LinkedList<RatingVO>();
                distributions.put(rating.getRatingReal(), localDistri);
            }
            localDistri.add(rating);
        }
        for (Float key : distributions.keySet()) {
            System.out.println(key + "\t" + distributions.get(key).size() + "\t"
                               + distributions.get(key).size() * 1.0 / totalCount);
        }

        //3. construct matrix
        UniformRealDistribution uniform = new UniformRealDistribution(0, 1.0);
        for (Queue<RatingVO> localDistri : distributions.values()) {
            while (!localDistri.isEmpty()) {
                RatingVO rating = localDistri.poll();

                if (uniform.sample() >= ratio) {
                    testMatrix.setValue(rating.getUsrId(), rating.getMovieId(),
                        rating.getRatingReal());
                } else {
                    rateMatrix.setValue(rating.getUsrId(), rating.getMovieId(),
                        rating.getRatingReal());
                }
            }
        }
    }
}
