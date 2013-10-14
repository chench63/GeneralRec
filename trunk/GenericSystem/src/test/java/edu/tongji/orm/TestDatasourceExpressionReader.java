/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;


import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 
 * @author chenkh
 * @version $Id: TestDatasourceExpressionReader.java, v 0.1 2013-9-9 下午4:09:08 chenkh Exp $
 */
public class TestDatasourceExpressionReader {

    @Test
    public void test() {
        String  ratingExpr = "FROM Rating WHERE xxx";
        SerializableBeanType ratingType = DataSourceExpressionReader.readBeanType(ratingExpr);
        Assert.isTrue( ratingType == SerializableBeanType.RATING_BEAN );
        
        
//        String singleConCaseI = "user.id = hello";
//        String singleConCaseII = "user.id > hello";
//        String singleConCaseIII = "user.id < hello";
//        String[] words = singleConCaseI.split(DatasourceExpressionReader.WORLD_SEPERATOR);
//        words = singleConCaseII.split(DatasourceExpressionReader.WORLD_SEPERATOR);
//        words = singleConCaseIII.split(DatasourceExpressionReader.WORLD_SEPERATOR);
    }

}
