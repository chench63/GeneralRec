package edu.tongji.prea.util;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.Test;
import org.springframework.util.Assert;

import prea.util.EvaluationMetrics;
//import prea.util.SimpleEvaluationMetrics;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;

/**
 * 
 * @author Hanke
 * @version $Id: EvaluationMetrics.java, v 0.1 2014-12-29 上午10:27:59 Exp $
 */
public class EvaluationMetricsTest {

    @Test
    public void test() {

        for (int k = 0; k < 1000; k++) {
            SparseRowMatrix prediction = new SparseRowMatrix(100, 100);
            SparseRowMatrix testMatrix = new SparseRowMatrix(100, 100);

            // initialize matrix
            UniformRealDistribution random = new UniformRealDistribution(1.0, 5.0);
            UniformRealDistribution z = new UniformRealDistribution(0.0, 1.0);
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    if (z.sample() > 0.90) {
                        continue;
                    }

                    prediction.setValue(i, j, random.sample());
                    testMatrix.setValue(i, j, random.sample());
                }
            }

            Assert.isTrue(isEqual(testMatrix, prediction));
        }
    }

    protected boolean isEqual(SparseRowMatrix testMatrix, SparseRowMatrix prediction) {
        //evaluate matrix
        EvaluationMetrics metric = new EvaluationMetrics(testMatrix, prediction, 1.0, 5.0);
        double se = 0.0d;
        double n = 0;
        for (int i = 0; i < 100; i++) {
            SparseVector Ri = testMatrix.getRowRef(i);
            int[] indexList = Ri.indexList();

            for (int j : indexList) {
                se += Math.pow(testMatrix.getValue(i, j) - prediction.getValue(i, j), 2.0d);
                n++;
            }
        }

        double testValue = metric.getRMSE();
        double realValue = Math.sqrt(se / n);
        return Math.abs(testValue - realValue) < 0.0000000000001d;
    }

}
