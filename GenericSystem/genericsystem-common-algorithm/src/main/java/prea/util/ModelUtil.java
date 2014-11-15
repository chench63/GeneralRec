package prea.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.data.BlockMatrix;
import edu.tongji.data.Model;

/**
 * 
 * @author Hanke
 * @version $Id: CoclusterUtil.java, v 0.1 2014-11-2 下午2:48:34 Exp $
 */
public class ModelUtil {

    /**
     * 
     * 
     * @param settingFile
     * @param rowMappingFile
     * @param colMappingFile
     * @param models
     */
    public static void readModels(String settingFile, String rowMappingFile, String colMappingFile,
                                  List<Model> models) {
        if (settingFile == null && rowMappingFile == null && colMappingFile == null) {
            return;
        }

        //Blocking setting file
        Map<Integer, Integer> rowAssig = new HashMap<Integer, Integer>();
        Map<Integer, Integer> colAssig = new HashMap<Integer, Integer>();
        BlockMatrix blockMatrix = MatrixFileUtil.readEmptyBlock(settingFile, rowMappingFile,
            colMappingFile, rowAssig, colAssig);

        int[][] blockStructure = blockMatrix.structure();
        Elmt[][] clusters = new Elmt[blockStructure.length][0];
        for (int i = 0; i < blockStructure.length; i++) {
            Elmt[] rowClusters = new Elmt[blockStructure[i].length];
            for (int j = 0; j < blockStructure[i].length; j++) {
                rowClusters[j] = new Elmt();
            }
            clusters[i] = rowClusters;
        }

        //rows
        for (int u = 0; u < blockMatrix.length()[0]; u++) {
            int rowLocal = rowAssig.get(u);
            int[] location = blockMatrix.locate(rowLocal, 0);
            for (int j = 0; j < clusters[location[0]].length; j++) {
                clusters[location[0]][j].addRow(u);
            }
        }

        //columns
        int[] rowBounds = blockMatrix.rowBound();
        for (int v = 0; v < blockMatrix.length()[1]; v++) {
            int col = colAssig.get(v);
            for (int rowIndex = 0; rowIndex < rowBounds.length; rowIndex++) {
                int row = rowBounds[rowIndex] - 1;
                int[] location = blockMatrix.locate(row, col);
                clusters[location[0]][location[1]].addCol(v);
            }
        }

        int indx = 0;
        for (int i = 0; i < clusters.length; i++) {
            for (int j = 0; j < clusters[i].length; j++) {
                if (indx >= models.size()) {
                    break;
                }

                Model model = models.get(indx);
                model.setRows(clusters[i][j].getRowSet());
                model.setCols(clusters[i][j].getColSet());
                clusters[i][j] = null;
                indx++;
            }
        }

        //release memory
        rowAssig.clear();
        colAssig.clear();
    }

    private static class Elmt {
        /** the rows of the cocluster*/
        private List<Integer> rowSet;

        /** the columns of the cocluster*/
        private List<Integer> colSet;

        /**
         * @param rowSet
         * @param colSet
         */
        public Elmt() {
            rowSet = new ArrayList<Integer>();
            colSet = new ArrayList<Integer>();
        }

        /**
         * add row to the cocluster
         * 
         * @param row the row index to add
         */
        public void addRow(int row) {
            this.rowSet.add(row);
        }

        /**
         * add column to the cocluster
         * 
         * @param col the column index to add
         */
        public void addCol(int col) {
            this.colSet.add(col);
        }

        /**
         * Getter method for property <tt>rowSet</tt>.
         * 
         * @return property value of rowSet
         */
        public int[] getRowSet() {
            int[] rows = new int[rowSet.size()];
            int index = 0;
            for (int row : rowSet) {
                rows[index++] = row;
            }
            return rows;
        }

        /**
         * Getter method for property <tt>colSet</tt>.
         * 
         * @return property value of colSet
         */
        public int[] getColSet() {
            int[] cols = new int[colSet.size()];
            int index = 0;
            for (int col : colSet) {
                cols[index++] = col;
            }
            return cols;
        }
    }
}
