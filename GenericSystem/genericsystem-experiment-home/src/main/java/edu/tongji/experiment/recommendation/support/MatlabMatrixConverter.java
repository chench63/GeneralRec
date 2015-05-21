package edu.tongji.experiment.recommendation.support;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;

/**
 * 
 * @author Hanke
 * @version $Id: MatlabMatrixConverter.java, v 0.1 2015-5-21 下午1:13:24 Exp $
 */
public final class MatlabMatrixConverter {
    /** file to store the original data and cocluster directory, make sure the data is compact.*/
    public final static String[] rootDirs = { "E:/MovieLens/zWarmStart/ml-10M100K/1/",
            "E:/MovieLens/zWarmStart/ml-10M100K/2/", "E:/MovieLens/zWarmStart/ml-10M100K/3/",
            "E:/MovieLens/zWarmStart/ml-10M100K/4/", "E:/MovieLens/zWarmStart/ml-10M100K/5/" };
    /** logger */
    private final static Logger  logger   = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        for (String rootDir : rootDirs) {
            doConvert(rootDir);
        }
    }

    public static void doConvert(String rootDir) {
        LoggerUtil.info(logger, "Entering rootDir: " + rootDir);

        LoggerUtil.info(logger, "\t\ta)Converting trainingFile.");
        String trainIn = rootDir + "trainingset";
        String trainOut = rootDir + "train.mm";
        innerConvert(trainIn, trainOut);

        LoggerUtil.info(logger, "\t\tb)Converting trainingFile.");
        String testIn = rootDir + "testingset";
        String testOut = rootDir + "test.mm";
        innerConvert(testIn, testOut);
    }

    public static void innerConvert(String fileInput, String fileOutput) {

        //converting
        int maxUId = 0;
        int maxIId = 0;
        int num = 0;
        StringBuilder ratingContent = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileInput));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split("\\::");
                int userId = Integer.valueOf(elements[0]);
                int itemId = Integer.valueOf(elements[1]);
                //ItemId,UserId,Rating
                ratingContent.append('\n').append(elements[1]).append(' ').append(elements[0])
                    .append(' ').append(elements[2]);

                if (userId > maxUId) {
                    maxUId = userId;
                }
                if (itemId > maxIId) {
                    maxIId = itemId;
                }
                num++;
            }

        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + fileInput);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        //recording
        StringBuilder header = new StringBuilder("%%MatrixMarket matrix array real general\n"
                                                 + "% Generated 22-May-2015\n" + maxIId + ' '
                                                 + maxUId + ' ' + num);
        header.append(ratingContent);
        FileUtil.write(fileOutput, header.toString());
    }
}
