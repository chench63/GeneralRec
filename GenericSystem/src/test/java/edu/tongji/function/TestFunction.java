/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.function;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author chenkh
 * @version $Id: TestFunction.java, v 0.1 2013-9-7 下午6:48:21 chenkh Exp $
 */
public class TestFunction {
    
    private List<Integer> ratingsOfI = new LinkedList<Integer>();
    private List<Integer> ratingsOfJ = new LinkedList<Integer>();
    
    private void mockRatings(){
        mockRatings(1, 1, 5);
    }
    
    private void mockRatings(int right1, int right2, int size){
        if( !ratingsOfI.isEmpty() && !ratingsOfJ.isEmpty()){
            return;
        }
        
        for(int i = 0; i < size; i++){
            ratingsOfI.add(i*right1);
            ratingsOfJ.add(i*right2);
        }
    }
    
    @Test
    public void testCosineSimularityFunction() {
        mockRatings();
        double cosineSim = (new CosineSimularityFunction()).calculate(ratingsOfI, ratingsOfJ).doubleValue();
        
        Assert.assertTrue(cosineSim == 1.0);
    }

    @Test
    public void testAdjustedCosineSimularityFunction() {
        mockRatings();
        ratingsOfI.add(0);
        ratingsOfJ.add(1);
        double cosineSim = (new AdjustedCosineSimularityFunction()).calculate(ratingsOfI, ratingsOfJ).doubleValue();
        Assert.assertFalse(cosineSim == 1.0);
    }
    
    @Test
    public void testCorrelationBasedSimularityFunction() {
        ratingsOfI.clear();
        ratingsOfJ.clear();
        mockRatings(1, 2, 5);
        ratingsOfI.add(FunctionHelper.averageValue(ratingsOfI).intValue());
        ratingsOfJ.add(FunctionHelper.averageValue(ratingsOfJ).intValue());
        mockRatings();
        double cosineSim = (new CorrelationBasedSimularityFunction()).calculate(ratingsOfI, ratingsOfJ).doubleValue();
        
        Assert.assertTrue(cosineSim == 1.0);
    }
    
}
