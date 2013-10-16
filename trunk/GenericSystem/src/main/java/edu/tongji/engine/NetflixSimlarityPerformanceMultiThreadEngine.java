/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author chenkh
 * @version $Id: NetflixSimlarityPerformanceMultiThreadEngine.java, v 0.1 2013-10-15 上午9:54:14 chenkh Exp $
 */
public class NetflixSimlarityPerformanceMultiThreadEngine extends RecommendationMultiThreadEngine {

    /** 数据读取*/
    private Thread              reader;

    /** 数据记录*/
    private List<Runnable>      recorder;

    /** logger */
    private final static Logger logger = Logger.getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {
        LoggerUtil.info(logger, "开始执行主线程.");
        
        reader.start();
        LoggerUtil.info(logger, "Reader线程启动完毕.");
        try {
            reader.join();
        } catch (InterruptedException e) {
            ExceptionUtil.caught(e, "NetflixSimularityPerformanceRecorder 线程等待join异常");
        }

        ExecutorService exec = Executors.newCachedThreadPool();
        for (Runnable runnable : recorder) {
            exec.execute(runnable);
        }
        LoggerUtil.info(logger, "Recorder线程启动完毕.");
        exec.shutdown();

    }

    /**
     * Getter method for property <tt>reader</tt>.
     * 
     * @return property value of reader
     */
    public Thread getReader() {
        return reader;
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
     * Getter method for property <tt>recorder</tt>.
     * 
     * @return property value of recorder
     */
    public List<Runnable> getRecorder() {
        return recorder;
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
