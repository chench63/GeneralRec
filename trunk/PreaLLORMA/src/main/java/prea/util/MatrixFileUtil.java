/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package prea.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import prea.data.structure.MatlabFasionSparseMatrix;
import prea.data.structure.SparseColumnMatrix;
import prea.data.structure.SparseMatrix;
import prea.data.structure.SparseRowMatrix;
import prea.data.structure.SparseVector;

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
        FileUtil.existDirAndMakeDir(file);

        int itemCount = 0;
        StringBuilder buffer = new StringBuilder();
        for (int u = 0, rowCount = matrix.length()[0]; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();
            if (itemList == null) {
                continue;
            }

            for (int i : itemList) {
                double val = matrix.getValue(u, i);
                String elemnt = u + "::" + i + "::" + String.format("%.1f", val);
                buffer.append(elemnt).append('\n');
                itemCount++;
            }

            // if greater than buffer size, then clear the buffer.
            if (itemCount >= 1000000) {
                FileUtil.writeAsAppend(file, buffer.toString());

                //reset buffer
                itemCount = 0;
                buffer = new StringBuilder();
            }
        }

        FileUtil.writeAsAppend(file, buffer.toString());
    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     */
    public static void write(String file, SparseRowMatrix matrix) {
        FileUtil.delete(file);
        FileUtil.existDirAndMakeDir(file);

        int itemCount = 0;
        StringBuilder buffer = new StringBuilder();
        for (int u = 0, rowCount = matrix.length()[0]; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemList = Fu.indexList();
            if (itemList == null) {
                continue;
            }

            for (int i : itemList) {
                double val = matrix.getValue(u, i);
                String elemnt = u + "::" + i + "::" + String.format("%.1f", val);
                buffer.append(elemnt).append('\n');
                itemCount++;
            }

            // if greater than buffer size, then clear the buffer.
            if (itemCount >= 1000000) {
                FileUtil.writeAsAppend(file, buffer.toString());

                //reset buffer
                itemCount = 0;
                buffer = new StringBuilder();
            }
        }

        FileUtil.writeAsAppend(file, buffer.toString());
    }

    /**
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     */
    public static void write(String file, SparseColumnMatrix matrix) {
        FileUtil.delete(file);
        FileUtil.existDirAndMakeDir(file);

        int itemCount = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0, colCount = matrix.length()[1]; i < colCount; i++) {
            SparseVector Mi = matrix.getColRef(i);
            int[] usrList = Mi.indexList();
            if (usrList == null) {
                continue;
            }

            for (int u : usrList) {
                double val = matrix.getValue(u, i);
                String elemnt = u + "::" + i + "::" + String.format("%.1f", val);
                buffer.append(elemnt).append('\n');
                itemCount++;
            }

            // if greater than buffer size, then clear the buffer.
            if (itemCount >= 1000000) {
                FileUtil.writeAsAppend(file, buffer.toString());

                //reset buffer
                itemCount = 0;
                buffer = new StringBuilder();
            }
        }

        FileUtil.writeAsAppend(file, buffer.toString());
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
        StringBuilder content = new StringBuilder();
        for (int i = 0, rowCount = matrix.length()[0]; i < rowCount; i++) {
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
     * write matrix to disk
     * 
     * @param file              the file to write
     * @param matrix            the matrix contains the data
     * @param rowAssign         the row relation mapping old index to new index
     * @param colAssign         the column relation mapping old index to new index
     * @param cleanMatrix       if true, clear the input matrix to save memory
     */
    public static void write(String file, SparseRowMatrix matrix, Map<Integer, Integer> rowAssig,
                             Map<Integer, Integer> colAssig, boolean cleanMatrix) {
        FileUtil.delete(file);
        FileUtil.existDirAndMakeDir(file);

        int itemCount = 0;
        StringBuilder buffer = new StringBuilder();
        for (int u = 0, rowCount = matrix.length()[0]; u < rowCount; u++) {
            SparseVector Fu = matrix.getRowRef(u);
            int[] itemIndex = Fu.indexList();
            if (itemIndex == null) {
                continue;
            }

            StringBuilder content = new StringBuilder();
            for (int i : itemIndex) {
                double val = matrix.getValue(u, i);
                String newElem = rowAssig.get(u) + "::" + colAssig.get(i) + "::"
                                 + String.format("%.1f", val);
                content.append(newElem).append('\n');
                itemCount++;

                // element in dataset cannot be zero
                if (val == 0.0) {
                    throw new RuntimeException("Dataset must be wrong!");
                } else {
                    matrix.setValue(u, i, 0.0d);
                }
            }

            // if greater than buffer size, then clear the buffer.
            if (itemCount >= 1000000) {
                FileUtil.writeAsAppend(file, buffer.toString());

                //reset buffer
                itemCount = 0;
                buffer = new StringBuilder();
            }
        }
        FileUtil.writeAsAppend(file, buffer.toString());

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
    public static SparseMatrix read(String file, int rowCount, int colCount) {

        SparseMatrix result = new SparseMatrix(rowCount, colCount);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elems = line.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);

                result.setValue(row, col, val);
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
    public static SparseRowMatrix reads(String file, int rowCount, int colCount) {
        SparseRowMatrix result = new SparseRowMatrix(rowCount, colCount);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elems = line.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);

                result.setValue(row, col, val);
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
    public static int reads(String file, int[] uSeq, int[] iSeq, double[] valSeq) {
        int itemCount = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elems = line.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);

                uSeq[itemCount] = row;
                iSeq[itemCount] = col;
                valSeq[itemCount] = val;
                itemCount++;
            }

        } catch (FileNotFoundException e) {
            ExceptionUtil.caught(e, "无法找到对应的加载文件: " + file);
        } catch (IOException e) {
            ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return itemCount;
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
    public static SparseColumnMatrix readCols(String file, int rowCount, int colCount) {

        SparseColumnMatrix result = new SparseColumnMatrix(rowCount, colCount);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elems = line.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);

                result.setValue(row, col, val);
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
    public static MatlabFasionSparseMatrix reads(String file, int nnz) {

        MatlabFasionSparseMatrix result = new MatlabFasionSparseMatrix(nnz);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] elems = line.split("\\::");
                int row = Integer.valueOf(elems[0]) + 1;
                int col = Integer.valueOf(elems[1]) + 1;
                double val = Double.valueOf(elems[2]);

                result.setValue(row, col, val);
            }
            result.reduceMem();

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

}
