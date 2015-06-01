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
    public final static String[] rootDirs  = { "E:/MovieLens/zColdStart/ml-10M100K/Fetch50/1/",
            "E:/MovieLens/zColdStart/ml-10M100K/Fetch50/2/",
            "E:/MovieLens/zColdStart/ml-10M100K/Fetch50/3/" };
    public final static String   resultDir = "E:/";
    /** logger */
    private final static Logger  logger    = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        int seq = 0;
        for (String rootDir : rootDirs) {
            seq++;
            doConvert(rootDir, seq);
        }
    }

    public static void doConvert(String rootDir, int seq) {
        LoggerUtil.info(logger, "Entering rootDir: " + rootDir);

        LoggerUtil.info(logger, "\t\ta)Converting trainingFile.");
        String trainIn = rootDir + "trainingset";
        String trainOut = resultDir + "train" + seq + ".mm";
        innerConvert(trainIn, trainOut);

        LoggerUtil.info(logger, "\t\tb)Converting trainingFile.");
        String testIn = rootDir + "testingset";
        String testOut = resultDir + "test" + seq + ".mm";
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
                int userId = Integer.valueOf(elements[0]) + 1;
                int itemId = Integer.valueOf(elements[1]) + 1;
                //ItemId,UserId,Rating
                ratingContent.append('\n').append(itemId).append(' ').append(userId).append(' ')
                    .append(elements[2]);

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
        StringBuilder header = new StringBuilder("%%MatrixMarket matrix coordinate real general\n"
                                                 + "% Generated 22-May-2015\n" + (maxIId) + ' '
                                                 + (maxUId) + ' ' + num);
        header.append(ratingContent);
        FileUtil.write(fileOutput, header.toString());
    }
}
