/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.Parser;
import edu.tongji.parser.netflix.NetflixRatingVOTemplateParser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: DataSetCompact.java, v 0.1 2014-10-13 下午8:53:18 chench Exp $
 */
public class DatasetCompact {

    //==========================
    //      Common variable
    //==========================

    /** file to store the original data*/
    public final static String  SOURCE_FILE = "E:/Netflix/trainingset/.*";

    /** file to persist the new data */
    public final static String  OUTPUT_FILE = "E:/Netflix/r/ratings.dat";

    /** The parser to parse the dataset file  **/
    //    public final static Parser  parser      = new MovielensRatingTemplateParser();
    public final static Parser  parser      = new NetflixRatingVOTemplateParser();

    /** the number of rows*/
    public static int           maxRow      = 2649430;

    /** the number of cloumns*/
    public static int           maxCol      = 17771;

    /** logger */
    private final static Logger logger      = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        //read source file
        LoggerUtil.info(logger, "1. read source file.");
        SparseMatrix matrix = new SparseMatrix(maxRow, maxCol);
        readByPattern(SOURCE_FILE, matrix);

        //compact rows and columns
        int nextRow = 0;
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < maxRow; i++) {
            SparseVector row = matrix.getRowRef(i);

            if (row.indexList() != null) {
                rowAssig.put(i, nextRow);
                nextRow++;
            }
        }

        int nextCol = 0;
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        for (int i = 0; i < maxCol; i++) {
            SparseVector col = matrix.getColRef(i);

            if (col.indexList() != null) {
                colAssig.put(i, nextCol);
                nextCol++;
            }
        }

        //iterate the matrix
        LoggerUtil.info(logger, "2. iterate the matrix.   RowCount: " + nextRow + " ColCount: "
                                + nextCol);
        for (int i = 0; i < maxRow; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] colIndex = Mi.indexList();
            if (colIndex == null) {
                continue;
            }

            //write to disk
            StringBuilder content = new StringBuilder();
            for (int j : colIndex) {
                int compactRow = rowAssig.get(i);
                //                int compactCol = colAssig.get(j);
                int compactCol = colAssig.get(j);
                double val = matrix.getValue(i, j);

                String newElem = compactRow + "::" + compactCol + "::" + String.format("%.1f", val);
                content.append(newElem).append('\n');

                // element in dataset cannot be zero
                if (val == 0.0) {
                    throw new RuntimeException("Dataset must be wrong!");
                } else {
                    matrix.setValue(i, j, 0.0d);
                }
            }
            FileUtil.writeAsAppend(OUTPUT_FILE, content.toString());
        }
    }

    public static void readByPattern(String path, SparseMatrix matrix) {
        //拆分目录和正则表达式
        int index = path.lastIndexOf(FileUtil.UNION_DIR_SEPERATOR);
        String dirValue = path.substring(0, index);
        String regexValue = path.substring(index + 1);
        File dir = new File(dirValue);
        if (!dir.isDirectory() | StringUtil.isBlank(regexValue)) {
            ExceptionUtil.caught(new FileNotFoundException("File Not Found"), "目录不存在，校验文件路径: "
                                                                              + path);
            return;
        }

        //批量读取文件
        File[] files = dir.listFiles();
        Pattern p = Pattern.compile(regexValue);
        for (File file : files) {
            if (file.isFile() && p.matcher(file.getName()).matches()) {
                // log
                LoggerUtil.info(logger, "File: " + file.getName());

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        RatingVO rating = (RatingVO) parser.parse(line);
                        if (rating == null) {
                            continue;
                        }

                        int row = rating.getUsrId();
                        int col = rating.getMovieId();
                        double val = rating.getRatingReal();
                        matrix.setValue(row, col, val);
                    }

                } catch (FileNotFoundException e) {
                    ExceptionUtil.caught(e, "无法找到对应的加载文件: " + path);
                } catch (IOException e) {
                    ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
                } finally {
                    IOUtils.closeQuietly(reader);
                }
            }
        }
    }
}
