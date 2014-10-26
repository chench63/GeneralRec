/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.data;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 
 * @author Hanke Chen
 * @version $Id: TestBlockMatrix.java, v 0.1 2014-10-15 上午9:57:00 chench Exp $
 */
public class TestBlockMatrix {

    @Test
    public void testlocate() {
        int[] rowBound = { 100, 300 };
        int[] colBound = { 200, 500 };
        BlockMatrix blockMatrix = new BlockMatrix(rowBound, colBound);

        int[] p1 = blockMatrix.locate(23, 45);
        Assert.isTrue(p1[0] == 0);
        Assert.isTrue(p1[1] == 0);
        Assert.isTrue(p1[2] == 23);
        Assert.isTrue(p1[3] == 45);
        blockMatrix.setValue(23, 45, 1);

        int[] p2 = blockMatrix.locate(23, 245);
        Assert.isTrue(p2[0] == 0);
        Assert.isTrue(p2[1] == 1);
        Assert.isTrue(p2[2] == 23);
        Assert.isTrue(p2[3] == 45);
        blockMatrix.setValue(23, 245, 1);

        int[] p3 = blockMatrix.locate(123, 45);
        Assert.isTrue(p3[0] == 1);
        Assert.isTrue(p3[1] == 0);
        Assert.isTrue(p3[2] == 23);
        Assert.isTrue(p3[3] == 45);
        blockMatrix.setValue(123, 45, 1);

        int[] p4 = blockMatrix.locate(123, 245);
        Assert.isTrue(p4[0] == 1);
        Assert.isTrue(p4[1] == 1);
        Assert.isTrue(p4[2] == 23);
        Assert.isTrue(p4[3] == 45);
        blockMatrix.setValue(123, 245, 1);

        //===============================================================

        int[] q1 = blockMatrix.locate(55, 200);
        Assert.isTrue(q1[0] == 0);
        Assert.isTrue(q1[1] == 1);
        Assert.isTrue(q1[2] == 55);
        Assert.isTrue(q1[3] == 0);
        blockMatrix.setValue(55, 200, 1);

        int[] q2 = blockMatrix.locate(100, 158);
        Assert.isTrue(q2[0] == 1);
        Assert.isTrue(q2[1] == 0);
        Assert.isTrue(q2[2] == 0);
        Assert.isTrue(q2[3] == 158);
        blockMatrix.setValue(100, 158, 1);

        int[] q3 = blockMatrix.locate(100, 200);
        Assert.isTrue(q3[0] == 1);
        Assert.isTrue(q3[1] == 1);
        Assert.isTrue(q3[2] == 0);
        Assert.isTrue(q3[3] == 0);
        blockMatrix.setValue(100, 200, 1);

        int[] q4 = blockMatrix.locate(100, 375);
        Assert.isTrue(q4[0] == 1);
        Assert.isTrue(q4[1] == 1);
        Assert.isTrue(q4[2] == 0);
        Assert.isTrue(q4[3] == 175);
        blockMatrix.setValue(123, 375, 1);

        int[] q5 = blockMatrix.locate(223, 200);
        Assert.isTrue(q5[0] == 1);
        Assert.isTrue(q5[1] == 1);
        Assert.isTrue(q5[2] == 123);
        Assert.isTrue(q5[3] == 0);
        blockMatrix.setValue(223, 200, 1);

        //=======================================================
        Assert.isTrue(blockMatrix.itemCount() == 9);

        Assert.isTrue(blockMatrix.getSparsity(0, 0) == 1.0 / (100 * 200));
        Assert.isTrue(blockMatrix.getSparsity(0, 1) == 2.0 / (100 * 300));
        Assert.isTrue(blockMatrix.getSparsity(1, 0) == 2.0 / (200 * 200));
        Assert.isTrue(blockMatrix.getSparsity(1, 1) == 4.0 / (200 * 300));
        Assert.isTrue(blockMatrix.getSparsity() == 9.0 / (500 * 300));
    }

    @Test
    public void testSetValue() {
        int[] rowBound = { 100, 300 };
        int[] colBound = { 200, 500 };
        BlockMatrix blockMatrix = new BlockMatrix(rowBound, colBound);

        int testTimes = 1000;
        UniformIntegerDistribution random = new UniformIntegerDistribution(0, 500);
        int round = 0;
        while (round < testTimes) {
            int i = random.sample() % 300;
            int j = random.sample() % 500;
            int value = random.sample();

            blockMatrix.setValue(i, j, value);
            Assert.isTrue(blockMatrix.getValue(i, j) == value);

            round++;
        }
    }

    @Test
    public void testCommonProperties() {
        int[] rowBound = { 100, 300 };
        int[] colBound = { 200, 400 };
        BlockMatrix blockMatrix = new BlockMatrix(rowBound, colBound);

        int[] length = blockMatrix.length();
        Assert.isTrue(length[0] == 300 && length[1] == 400);

    }
}
