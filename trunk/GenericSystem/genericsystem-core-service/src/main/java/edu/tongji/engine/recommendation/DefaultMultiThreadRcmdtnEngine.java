/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author Hanke Chen
 * @version $Id: NetflixSimlarityPerformanceMultiThreadEngine.java, v 0.1 2013-10-15 上午9:54:14 chench Exp $
 */
public class DefaultMultiThreadRcmdtnEngine extends RcmdtnEngine {

    /** 
     * @see edu.tongji.engine.recommendation.RcmdtnEngine#excuteInner()
     */
    @Override
    protected void excuteInner() {
        ExecutorService exec = Executors.newCachedThreadPool();
        for (Runnable runnable : recorder) {
            exec.execute(runnable);
        }
        exec.shutdown();
    }

}
