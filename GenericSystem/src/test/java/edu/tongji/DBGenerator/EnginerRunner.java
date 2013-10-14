/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.DBGenerator;

/**
 * 
 * @author chenkh
 * @version $Id: EnginerRunner.java, v 0.1 2013-9-12 下午4:33:20 chenkh Exp $
 */
public class EnginerRunner implements Runnable {

    private int    priority;

    private Worker worker;

    public EnginerRunner(int priority, Worker worker) {
        this.priority = priority;
        this.worker = worker;
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Thread.currentThread().setPriority(priority);
        worker.run();
    }

}
