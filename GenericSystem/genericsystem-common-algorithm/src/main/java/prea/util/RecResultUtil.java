package prea.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import edu.tongji.data.SparseRowMatrix;
import edu.tongji.util.ExceptionUtil;

/**
 * 
 * @author Hanke
 * @version $Id: RecResultUtil.java, v 0.1 2015-4-19 下午9:28:30 Exp $
 */
public final class RecResultUtil {

    /**
     * forbid construction
     */
    private RecResultUtil() {

    }

    public static synchronized void write() {

    }

    /**
     * Read recommendation results
     * 
     * @param resultFile
     * @param estMatrix
     * @param testMatrix
     * @param puMatrix
     * @param piMatrix
     * @return
     */
    public static boolean readRec(String predctFile, SparseRowMatrix[] estMatrix,
                                  SparseRowMatrix testMatrix, SparseRowMatrix puMatrix,
                                  SparseRowMatrix piMatrix) {
        File file = new File(predctFile);
        if (!file.isFile() | !file.exists()) {
            ExceptionUtil.caught(new FileNotFoundException("File Not Found"), "读取文件发生异常，校验文件路径: "
                                                                              + predctFile);
            return false;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //userId, itemId, AuiReal, AuiEst, Pu, Pi, GroupId
                String[] elemnts = line.split("\\,");
                int usrId = Integer.valueOf(elemnts[0]);
                int itemId = Integer.valueOf(elemnts[1]);
                double AuiReal = Double.valueOf(elemnts[2]);
                double AuiEst = Double.valueOf(elemnts[3]);
                double Pu = Double.valueOf(elemnts[4]);
                double Pi = Double.valueOf(elemnts[5]);
                int groupId = Integer.valueOf(elemnts[6]);

                estMatrix[groupId].setValue(usrId, itemId, AuiEst);
                puMatrix.setValue(usrId, groupId, Pu);
                piMatrix.setValue(itemId, groupId, Pi);
                if (groupId == 0) {
                    testMatrix.setValue(usrId, itemId, AuiReal);
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + predctFile);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        //出现异常，返回null
        return false;
    }
}
