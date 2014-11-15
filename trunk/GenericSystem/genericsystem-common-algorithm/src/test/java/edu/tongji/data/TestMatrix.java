package edu.tongji.data;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 
 * @author Hanke
 * @version $Id: TestMatrix.java, v 0.1 2014-11-15 上午10:50:33 Exp $
 */
public class TestMatrix {

    @Test
    public void test() {
        SparseMatrix matrix = new SparseMatrix(200, 100);
        matrix.setValue(5, 10, 1.0);
        matrix.setValue(5, 11, 1.0);
        matrix.setValue(5, 12, 1.0);
        matrix.setValue(5, 13, 1.0);

        matrix.setValue(5, 14, 2.0);
        matrix.setValue(5, 15, 2.0);
        matrix.setValue(5, 16, 2.0);
        matrix.setValue(5, 17, 2.0);

        matrix.setValue(5, 18, 3.0);
        matrix.setValue(5, 19, 4.0);
        matrix.setValue(5, 20, 4.0);
        matrix.setValue(5, 21, 4.0);

        matrix.setValue(5, 22, 5.0);
        matrix.setValue(5, 23, 5.0);
        matrix.setValue(5, 24, 5.0);

        int[] rows = { 5, 6, 7, 8, 152, 54, 12, 102 };
        int[] cols = { 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 54, 49 };
        float[][] rowPro = matrix.probability(rows, cols, 5.0, 1.0, true);
        Assert.isTrue(rowPro[5][0] == 0.25);
        Assert.isTrue(rowPro[5][1] == 0.25);
    }

}
