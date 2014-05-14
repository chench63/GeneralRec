/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.List;

import edu.tongji.context.ContextEnvelope;
import edu.tongji.context.ProcessorContext;
import edu.tongji.engine.Engine;
import edu.tongji.function.Function;
import edu.tongji.orm.DataSource;
import edu.tongji.processor.Processor;

/**
 * 
 * @author chench
 * @version $Id: RecommendationEngine.java, v 0.1 16 Sep 2013 21:51:35 chench Exp $
 */
public abstract class RcmdtnEngine implements Engine {
    /** 数据源*/
    protected DataSource       dataSource;

    /** 处理器*/
    protected Processor        processor;

    /** 处理器上下文*/
    protected ProcessorContext processorContext;

    /** 处理函数集合*/
    protected List<Function>   functions;

    /** 原始数据集*/
    protected ContextEnvelope  contextEnvelope;

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

}
