package edu.tongji.data;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */

/**
 * 
 * @author Hanke Chen
 * @version $Id: TestCluster.java, v 0.1 2014-10-14 上午11:40:02 chench Exp $
 */
public class TestCluster {

    @Test
    public void test() {
        SparseMatrix matrix = new SparseMatrix(3, 3);
        Cluster cluster = new Cluster();

        int round = 0;
        while (round < 1000) {
            UniformRealDistribution uniform = new UniformRealDistribution(0.0, 200);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matrix.setValue(i, j, uniform.sample());
                }
            }
            round++;

            cluster.clear();
            cluster.add(0);
            cluster.add(1);
            cluster.add(2);

            SparseVector result = cluster.centroid(matrix);
            Assert.isTrue(Math.round(result.getValue(0)) == Math
                .round(matrix.getColRef(0).sum() / 3.0));
            Assert.isTrue(Math.round(result.getValue(1)) == Math
                .round(matrix.getColRef(1).sum() / 3.0));
            Assert.isTrue(Math.round(result.getValue(2)) == Math
                .round(matrix.getColRef(2).sum() / 3.0));
        }
    }

}
