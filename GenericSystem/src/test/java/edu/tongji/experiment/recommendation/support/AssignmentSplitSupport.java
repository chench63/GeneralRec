/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 推荐系统产生评分过程，对UserSeq文件汇总至不同文件夹，
 * 拆分ur_/d{12}.txt任务文件;
 * 
 * @author chench
 * @version $Id: AssignmentSplitSupport.java, v 0.1 25 Apr 2014 12:42:12 chench Exp $
 */
public class AssignmentSplitSupport {

    /** 文件路径*/
    protected static final String FILE           = "G:/DataSet/Rating";

    /** 下划线*/
    protected static final char   UNDERLINE      = '_';

    /** 划分成的任务数量*/
    protected static final int    ASSIGNMENT_NUM = 10;

    /** logger */
    private static final Logger   logger         = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * @param args
     */
    public static void main(String[] args) {
        File dir = new File(FILE);
        if (!dir.isDirectory()) {
            LoggerUtil.warn(logger, dir.getAbsolutePath() + "is not a directory");
            return;
        }

        File[] files = dir.listFiles();
        int taskNum = files.length / ASSIGNMENT_NUM;
        for (int forStart = 0, forEnd = 0, globalEnd = files.length; forStart < globalEnd;) {
            File taskDir = null;
            try {
                //本次循环结束
                forEnd = (forEnd = (forStart + taskNum)) > globalEnd ? globalEnd : forEnd;

                //创建Assign目录
                //原路径_[digit]
                taskDir = new File((new StringBuilder(FILE)).append(UNDERLINE)
                    .append(forStart / taskNum).toString());
                if (!taskDir.exists()) {
                    taskDir.mkdir();
                }

                //[forStart, forEnd)
                for (; forStart < forEnd; forStart++) {
                    File targetFile = new File((new StringBuilder(FILE)).append(UNDERLINE)
                        .append(forStart / taskNum).append(File.separator)
                        .append(files[forStart].getName()).toString());
                    Files.copy(files[forStart].toPath(), targetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (SecurityException | IOException e) {
                ExceptionUtil.caught(e, "Directory had been created.");
            } finally {
            }
        }

    }
}
