/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.ml;

import org.junit.Test;

import edu.tongji.data.Cluster;
import edu.tongji.data.SparseMatrix;

/**
 * 
 * @author Hanke Chen
 * @version $Id: TestCoclusterUtil.java, v 0.1 2014-10-28 上午9:50:43 chench Exp $
 */
public class TestCoclusterUtil {

    @Test
    public void test() {
        SparseMatrix rateMatrix = new SparseMatrix(6, 6);
        rateMatrix.setValue(0, 0, 0.05);
        rateMatrix.setValue(0, 1, 0.05);
        rateMatrix.setValue(0, 2, 0.05);
        rateMatrix.setValue(1, 0, 0.05);
        rateMatrix.setValue(1, 1, 0.05);
        rateMatrix.setValue(1, 2, 0.05);

        rateMatrix.setValue(2, 3, 0.05);
        rateMatrix.setValue(2, 4, 0.05);
        rateMatrix.setValue(2, 5, 0.05);
        rateMatrix.setValue(3, 3, 0.05);
        rateMatrix.setValue(3, 4, 0.05);
        rateMatrix.setValue(3, 5, 0.05);

        rateMatrix.setValue(4, 0, 0.04);
        rateMatrix.setValue(4, 1, 0.04);
        rateMatrix.setValue(4, 3, 0.04);
        rateMatrix.setValue(4, 4, 0.04);
        rateMatrix.setValue(4, 5, 0.04);

        rateMatrix.setValue(5, 0, 0.04);
        rateMatrix.setValue(5, 1, 0.04);
        rateMatrix.setValue(5, 2, 0.04);
        rateMatrix.setValue(5, 4, 0.04);
        rateMatrix.setValue(5, 5, 0.04);

        Cluster[][] result = CoclusterUtil.divideWithConjugateAssumption(rateMatrix, 3, 2, 10,
            CoclusterUtil.C_6, CoclusterUtil.EUCLIDEAN_DIVERGENCE);

        for (int i = 0; i < 2; i++) {
            System.out.println("================================");
            for (Cluster row : result[i]) {
                String msg = "";

                for (int index : row) {
                    msg = msg + " " + index;
                }
                System.out.println(msg);
            }
        }
    }

}
