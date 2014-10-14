package edu.tongji.data;

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

        matrix.setValue(0, 0, 1);
        matrix.setValue(0, 1, 1);
        matrix.setValue(0, 2, 1);

        matrix.setValue(1, 0, 2);
        matrix.setValue(1, 1, 2);
        matrix.setValue(1, 2, 2);

        matrix.setValue(2, 0, 3);
        matrix.setValue(2, 1, 3);
        matrix.setValue(2, 2, 3);

        Cluster cluster = new Cluster();
        cluster.add(0);
        cluster.add(1);
        cluster.add(2);

        SparseVector result = cluster.centroid(matrix);

        Assert.isTrue(result.getValue(0) == 2);
        Assert.isTrue(result.getValue(1) == 2);
        Assert.isTrue(result.getValue(2) == 2);
    }

}
