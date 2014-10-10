/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import edu.tongji.context.ContextEnvelope;
import edu.tongji.context.ProcessorContext;
import edu.tongji.engine.Engine;
import edu.tongji.function.Function;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.orm.DataSource;
import edu.tongji.processor.Processor;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke Chen
 * @version $Id: RecommendationEngine.java, v 0.1 16 Sep 2013 21:51:35 chench Exp $
 */
public abstract class RcmdtnEngine implements Engine {

    /** 数据读取*/
    protected Thread              reader;

    /** 数据记录*/
    protected List<Runnable>      recorder;

    /** 数据源*/
    protected DataSource          dataSource;

    /** 处理器*/
    protected Processor           processor;

    /** 处理器上下文*/
    protected ProcessorContext    processorContext;

    /** 处理函数集合*/
    protected List<Function>      functions;

    /** 原始数据集*/
    protected ContextEnvelope     contextEnvelope;

    protected String              testingDataSet;

    /** logger */
    protected final static Logger logger     = Logger
                                                 .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** logger */
    protected final static Logger loggerCore = Logger.getLogger(LoggerDefineConstant.SERVICE_CORE);

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {
        //1. 载入数据集
        loadDataSet();

        //2. 组装数据集
        assembleDataSet();

        //3. 处理业务逻辑
        excuteInner();
    }

    /**
     * 载入数据集
     */
    protected void loadDataSet() {
        LoggerUtil.info(logger, "1. loading data set.");

        try {
            reader.start();
            reader.join();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "NetflixSimularityPerformanceRecorder 线程等待join异常");
        }
    }

    /**
     * 组装数据集
     */
    protected void assembleDataSet() {
        LoggerUtil.info(logger, "2. assembling data set.");
    }

    /**
     * 处理业务逻辑
     */
    protected void excuteInner() {
        LoggerUtil.info(logger, "3. initializing working threads.");

        ExecutorService exec = Executors.newCachedThreadPool();
        for (Runnable runnable : recorder) {
            exec.execute(runnable);
        }
        exec.shutdown();
    }

    /**
     * Getter method for property <tt>testingDataSet</tt>.
     * 
     * @return property value of testingDataSet
     */
    public String getTestingDataSet() {
        return testingDataSet;
    }

    /**
     * Setter method for property <tt>testingDataSet</tt>.
     * 
     * @param testingDataSet value to be assigned to property testingDataSet
     */
    public void setTestingDataSet(String testingDataSet) {
        this.testingDataSet = testingDataSet;
    }

    /**
     * Getter method for property <tt>dataSource</tt>.
     * 
     * @return property value of dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Setter method for property <tt>dataSource</tt>.
     * 
     * @param dataSource value to be assigned to property dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Getter method for property <tt>processor</tt>.
     * 
     * @return property value of processor
     */
    public Processor getProcessor() {
        return processor;
    }

    /**
     * Setter method for property <tt>processor</tt>.
     * 
     * @param processor value to be assigned to property processor
     */
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    /**
     * Getter method for property <tt>processorContext</tt>.
     * 
     * @return property value of processorContext
     */
    public ProcessorContext getProcessorContext() {
        return processorContext;
    }

    /**
     * Setter method for property <tt>processorContext</tt>.
     * 
     * @param processorContext value to be assigned to property processorContext
     */
    public void setProcessorContext(ProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    /**
     * Getter method for property <tt>functions</tt>.
     * 
     * @return property value of functions
     */
    public List<Function> getFunctions() {
        return functions;
    }

    /**
     * Setter method for property <tt>functions</tt>.
     * 
     * @param functions value to be assigned to property functions
     */
    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }

    /**
     * Getter method for property <tt>contextEnvelope</tt>.
     * 
     * @return property value of contextEnvelope
     */
    public ContextEnvelope getContextEnvelope() {
        return contextEnvelope;
    }

    /**
     * Setter method for property <tt>contextEnvelope</tt>.
     * 
     * @param contextEnvelope value to be assigned to property contextEnvelope
     */
    public void setContextEnvelope(ContextEnvelope contextEnvelope) {
        this.contextEnvelope = contextEnvelope;
    }

    /**
     * Setter method for property <tt>reader</tt>.
     * 
     * @param reader value to be assigned to property reader
     */
    public void setReader(Thread reader) {
        this.reader = reader;
    }

    /**
     * Setter method for property <tt>recorder</tt>.
     * 
     * @param recorder value to be assigned to property recorder
     */
    public void setRecorder(List<Runnable> recorder) {
        this.recorder = recorder;
    }

}
