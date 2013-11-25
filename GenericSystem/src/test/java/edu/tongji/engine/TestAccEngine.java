/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import edu.tongji.context.ContextEnvelope;
import edu.tongji.context.ProcessorContext;
import edu.tongji.function.FunctionHelper;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.processor.Processor;
import edu.tongji.stopper.TimestampStopper;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: TestAccEngine.java, v 0.1 2013-9-18 下午8:12:03 chenkh Exp $
 */
public class TestAccEngine {

    private static final Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    public void testMock() {
        List<Serializable> resultSet = new ArrayList<Serializable>();
        mock(resultSet, 0);

        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "META-INF/spring/application-context-netflix.xml");
            ProcessorContext processorContext = (ProcessorContext) ctx
                .getBean("recommendationContext");
            Processor processor = (Processor) ctx.getBean("recommendationProcessor");
            ;
            ContextEnvelope contextEnvelope = (ContextEnvelope) ctx.getBean("contextEnvelope");
            ;
            TimestampStopper stopper = (TimestampStopper) ctx.getBean("stopper");
            stopper.setSeed(Timestamp.valueOf("2013-09-02 00:00:00"));

            //0. 从数据库，获取测试数据集, 存储于ContextEnvelope
            contextEnvelope.setResultSet(resultSet);

            //1. 转化测试数据集为内部抽象数据集
            processorContext.switchToProcessorContext(contextEnvelope);

            //3. 调用处理器，处理数据
            processor.process(processorContext);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public void testMock1() {
        List<Serializable> resultSet = new ArrayList<Serializable>();
        mock(resultSet, 1);

        ClassPathXmlApplicationContext ctx = null;
        try {
            ctx = new ClassPathXmlApplicationContext(
                "META-INF/spring/application-context-netflix.xml");
            ProcessorContext processorContext = (ProcessorContext) ctx
                .getBean("recommendationContext");
            Processor processor = (Processor) ctx.getBean("recommendationProcessor");
            ;
            ContextEnvelope contextEnvelope = (ContextEnvelope) ctx.getBean("contextEnvelope");
            ;
            TimestampStopper stopper = (TimestampStopper) ctx.getBean("stopper");
            stopper.setSeed(Timestamp.valueOf("2013-09-02 00:00:00"));
            //0. 从数据库，获取测试数据集, 存储于ContextEnvelope
            contextEnvelope.setResultSet(resultSet);
            //1. 转化测试数据集为内部抽象数据集
            processorContext.switchToProcessorContext(contextEnvelope);
            //3. 调用处理器，处理数据
            processor.process(processorContext);

            mock(resultSet, 3);
            //0. 从数据库，获取测试数据集, 存储于ContextEnvelope
            contextEnvelope.setResultSet(resultSet);
            //1. 转化测试数据集为内部抽象数据集
            processorContext.switchToProcessorContext(contextEnvelope);
            //3. 调用处理器，处理数据
            processor.process(processorContext);

        } catch (Exception e) {
            ExceptionUtil.caught(e, "edu.tongji.engine.TestEngine 测试用例发生错误");
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }


    private void mock(List<Serializable> resultSet, int style) {
        resultSet.clear();
    }

    public void calue() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(3);
        list.add(4);
        list.add(4);
        list.add(5);
        double avg1 = FunctionHelper.averageValue(list).doubleValue();
        list.remove(3);

        List<Integer> list2 = new ArrayList<Integer>();
        list2.add(4);
        list2.add(2);
        list2.add(194);
        list2.add(0);
        double avg2 = FunctionHelper.averageValue(list2).doubleValue();
        list2.remove(2);

        LoggerUtil.info(logger, "Result: "
                                + FunctionHelper.adjustSumOfSquareValue(list, avg1).doubleValue());

        LoggerUtil.info(logger, "Result: "
                                + FunctionHelper.adjustSumOfSquareValue(list2, avg2).doubleValue());

        LoggerUtil.info(logger,
            "Result: "
                    + FunctionHelper.adjuestedDotProductValue(list, avg1, list2, avg2)
                        .doubleValue());
    }
}
