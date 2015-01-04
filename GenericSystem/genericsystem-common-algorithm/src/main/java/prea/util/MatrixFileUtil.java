/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.SparseMatrix;
import edu.tongji.data.SparseRowMatrix;
import edu.tongji.data.SparseVector;
import edu.tongji.parser.MovielensRatingTemplateParser;
import edu.tongji.parser.Parser;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.vo.RatingVO;

/**
 * Matrix write utilities
 * 
 * @author Hanke Chen
 * @version $Id: MatrixFileUtil.java, v 0.1 2014-10-16 下午2:32:09 chench Exp $
 */
public final class MatrixFileUtil {

    /**
     * forbid construction method
     */
    private MatrixFileUtil() {

    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     */
    public static void write(String file, SparseMatrix matrix) {
        FileUtil.delete(file);

        int rowCount = matrix.length()[0];
        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            StringBuilder content = new StringBuilder();
            for (int j : indexList) {
                double val = matrix.getValue(i, j);
                String elemnt = i + "::" + j + "::" + String.format("%.1f", val);
                content.append(elemnt).append('\n');
            }
            FileUtil.writeAsAppend(file, content.toString());
        }

    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     */
    public static void write(String file, SparseRowMatrix matrix) {
        FileUtil.delete(file);

        int rowCount = matrix.length()[0];
        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }
            StringBuilder content = new StringBuilder();
            for (int j : indexList) {
                double val = matrix.getValue(i, j);
                String elemnt = i + "::" + j + "::" + String.format("%.1f", val);
                content.append(elemnt).append('\n');
            }
            FileUtil.writeAsAppend(file, content.toString());
        }
    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     */
    public static void write(String file, BlockMatrix matrix) {
        int[][] structure = matrix.structure();

        for (int i = 0; i < structure.length; i++) {
            for (int j = 0; j < structure[i].length; j++) {
                SparseMatrix local = matrix.getBlock(i, j);

                String f = file + '_' + i + '_' + j;
                write(f, local);
            }
        }
    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     * @param rowAssign         the row relation mapping old index to new index
     * @param colAssign         the column relation mapping old index to new index
     * @param cleanMatrix       if true, clear the input matrix to save memory
     */
    public static void write(String file, SparseMatrix matrix, Map<Integer, Integer> rowAssign,
                             Map<Integer, Integer> colAssign, boolean cleanMatrix) {
        int rowCount = matrix.length()[0];
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < rowCount; i++) {
            SparseVector Mi = matrix.getRowRef(i);
            int[] indexList = Mi.indexList();
            if (indexList == null) {
                continue;
            }

            for (int j : indexList) {
                int newRow = rowAssign.get(i);
                int newCol = colAssign.get(j);
                double val = matrix.getValue(i, j);

                String elemnt = newRow + "::" + newCol + "::" + String.format("%.1f", val);
                content.append(elemnt).append('\n');

                if (cleanMatrix) {
                    matrix.setValue(i, j, 0.0d);
                }
            }
        }

        FileUtil.write(file, content.toString());
    }

    /**
     * 
     * 
     * @param SETTING_FILE
     * @param ROW_MAPPING_FILE
     * @param COL_MAPPING_FILE
     * @param K
     * @param L
     * @param rowBound
     * @param colBound
     * @param rowAssig
     * @param colAssig
     */
    public static void writeStructureSetting(String SETTING_FILE, String ROW_MAPPING_FILE,
                                             String COL_MAPPING_FILE, int K, int L, int[] rowBound,
                                             int[] colBound, Map<Integer, Integer> rowAssig,
                                             Map<Integer, Integer> colAssig) {
        //write cocluster structure
        StringBuilder setting = new StringBuilder();
        for (int k = 0; k < K; k++) {
            setting.append(rowBound[k]).append(": ").append(colBound[0]);
            for (int i = 1; i < L; i++) {
                setting.append(", ").append(colBound[i]);
            }
            setting.append('\n');
        }
        FileUtil.write(SETTING_FILE, setting.toString());
        setting = null;

        //write row mapping
        StringBuilder rowMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : rowAssig.entrySet()) {
            rowMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(ROW_MAPPING_FILE, rowMapping.toString());
        rowMapping = null;

        //write column mapping
        StringBuilder colMapping = new StringBuilder();
        for (Entry<Integer, Integer> entry : colAssig.entrySet()) {
            colMapping.append(entry.getKey()).append(':').append(entry.getValue()).append('\n');
        }
        FileUtil.write(COL_MAPPING_FILE, colMapping.toString());
        colMapping = null;
    }

    /**
     * write bounds to file
     * 
     * @param file          the file to write
     * @param rowBound      the bounds of rows
     * @param colBound      the bounds of columns
     */
    public static void write(String file, int[] rowBound, int[] colBound) {
        StringBuilder content = new StringBuilder();
        content.append(rowBound[0]);
        for (int i = 1; i < rowBound.length; i++) {
            content.append(',').append(rowBound[i]);
        }
        content.append('\n');

        content.append(colBound[0]);
        for (int j = 1; j < colBound.length; j++) {
            content.append(',').append(colBound[j]);
        }
        FileUtil.write(file, content.toString());
    }

    //=============================================
    //      Read methods
    //=============================================

    /**
     * Read matrix from file
     * 
     * @param file          file contain matrix data
     * @param rowCount      the number of rows
     * @param colCount      the number of columns
     * @param parser        the parser to parse the data structure
     * @return
     */
    public static SparseMatrix read(String file, int rowCount, int colCount, Parser parser) {
        if (parser == null) {
            parser = new MovielensRatingTemplateParser();
        }

        SparseMatrix result = new SparseMatrix(rowCount, colCount);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                RatingVO rating = (RatingVO) parser.parse(line);
                result.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            }

            return result;
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }

    /**
     * Read matrix from file
     * 
     * @param file          file contain matrix data
     * @param rowCount      the number of rows
     * @param colCount      the number of columns
     * @param parser        the parser to parse the data structure
     * @return
     */
    public static SparseRowMatrix reads(String file, int rowCount, int colCount, Parser parser) {
        if (parser == null) {
            parser = new MovielensRatingTemplateParser();
        }

        SparseRowMatrix result = new SparseRowMatrix(rowCount, colCount);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                RatingVO rating = (RatingVO) parser.parse(line);
                result.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            }

            return result;
        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }

    public static BlockMatrix read(String sourceFile, String settingFile, String rowMappingFile,
                                   String colMappingFile, int rowCount, int colCount, Parser parser) {
        if (parser == null) {
            parser = new MovielensRatingTemplateParser();
        }

        //reading training file
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix matrix = readEmptyBlock(settingFile, rowMappingFile, colMappingFile, rowAssig,
            colAssig);

        String[] lines = FileUtil.readLines(sourceFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            int row = rowAssig.get(rating.getUsrId());
            int col = colAssig.get(rating.getMovieId());

            matrix.setValue(row, col, rating.getRatingReal());
        }

        return matrix;
    }

    /**
     * 
     * 
     * @param sourceFile
     * @param settingFile
     * @param rowMappingFile
     * @param colMappingFile
     * @param rowCount
     * @param colCount
     * @param parser
     * @param iBlock
     * @param jBlock
     * @return
     */
    public static SparseMatrix readBlock(String sourceFile, String settingFile,
                                         String rowMappingFile, String colMappingFile,
                                         int rowCount, int colCount, Parser parser, int iBlock,
                                         int jBlock) {
        //
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix matrix = readEmptyBlock(settingFile, rowMappingFile, colMappingFile, rowAssig,
            colAssig);

        String[] lines = FileUtil.readLines(sourceFile);
        SparseMatrix target = new SparseMatrix(rowCount, colCount);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            int row = rowAssig.get(rating.getUsrId());
            int col = colAssig.get(rating.getMovieId());

            int[] location = matrix.locate(row, col);

            if (location[0] == iBlock && location[1] == jBlock) {
                target.setValue(rating.getUsrId(), rating.getMovieId(), rating.getRatingReal());
            }
        }

        return target;
    }

    //==================================================
    //
    //==================================================

    public static SparseMatrix toGlobalIndex(String localFile, String settingFile,
                                             String rowMappingFile, String colMappingFile,
                                             int rowCount, int colCount, Parser parser, int iBlock,
                                             int jBlock) {
        //Construct BlockMatrix
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix matrix = readEmptyBlock(settingFile, rowMappingFile, colMappingFile, rowAssig,
            colAssig);

        //construct result
        SparseMatrix result = new SparseMatrix(rowCount, colCount);
        int[] local = new int[4];
        local[0] = iBlock;
        local[1] = jBlock;
        String[] lines = FileUtil.readLines(localFile);
        for (String line : lines) {
            RatingVO rating = (RatingVO) parser.parse(line);
            local[2] = rating.getUsrId();
            local[3] = rating.getMovieId();
            int[] global = matrix.global(local);

            result.setValue(global[0], global[1], rating.getRatingReal());
        }

        return result;
    }

    /**
     * read empty BlockMatrix from setting files
     * 
     * @param settingFile
     * @param rowMappingFile
     * @param colMappingFile
     * @return
     */
    public static BlockMatrix readEmptyBlock(String settingFile, String rowMappingFile,
                                             String colMappingFile, Map<Integer, Integer> rowAssig,
                                             Map<Integer, Integer> colAssig) {
        String[] lines = FileUtil.readLines(settingFile);
        int[] rowBound = new int[lines.length];
        int[][] coclusterStructure = new int[lines.length][0];
        for (int i = 0; i < lines.length; i++) {
            String[] rc = lines[i].split("\\:");
            rowBound[i] = Integer.valueOf(rc[0].trim());

            String[] cs = rc[1].split("\\,");
            int[] rowStructure = new int[cs.length];
            for (int j = 0; j < cs.length; j++) {
                rowStructure[j] = Integer.valueOf(cs[j].trim());
            }
            coclusterStructure[i] = rowStructure;
        }

        //read row mapping file
        lines = FileUtil.readLines(rowMappingFile);
        for (String line : lines) {
            String[] elmnts = line.split("\\:");
            int key = Integer.valueOf(elmnts[0].trim());
            int val = Integer.valueOf(elmnts[1].trim());
            rowAssig.put(key, val);
        }

        //read col mapping file
        lines = FileUtil.readLines(colMappingFile);
        for (String line : lines) {
            String[] elmnts = line.split("\\:");
            int key = Integer.valueOf(elmnts[0].trim());
            int val = Integer.valueOf(elmnts[1].trim());
            colAssig.put(key, val);
        }
        BlockMatrix matrix = new BlockMatrix();
        matrix.initialize(rowBound, coclusterStructure);

        return matrix;
    }

}
