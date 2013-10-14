/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.DataSource;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;


/**
 * 
 * @author chenkh
 * @version $Id: TestDatDatasource.java, v 0.1 2013-9-6 下午7:43:29 chenkh Exp $
 */
public class TestDatDatasource {

    private static final Logger logger =Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);
    
    @SuppressWarnings("unchecked")
    @Test
    public void test() {
        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext("META-INF/spring/application-context-netflix.xml");
            
            DataSource dataSource = (DataSource) ctx.getBean("datasource");
            List<Serializable> resultSet = (List<Serializable>) dataSource.excute(StringUtil.EMPTY_STRING);
            
            LoggerUtil.info(logger, resultSet);
            
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
        
    }

}
