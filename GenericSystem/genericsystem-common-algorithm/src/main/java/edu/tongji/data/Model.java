package edu.tongji.data;

import org.apache.log4j.Logger;

import prea.util.EvaluationMetrics;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.ml.matrix.MatrixFactorizationRecommender;
import edu.tongji.ml.matrix.WeigtedRSVD;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.SerializeUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author Hanke
 * @version $Id: Model.java, v 0.1 2014-11-2 下午3:26:24 Exp $
 */
public class Model {
    /** matrix factorization */
    public MatrixFactorizationRecommender recmmd;
    /** included index of rows */
    private int[]                         rows;
    /** included index of columns */
    private int[]                         cols;
    /** the unique id of model*/
    private int                           id;
    /** the unique id of the model group */
    private int                           groupId;
    /** the file to store the predictions*/
    private String                        resultFile;

    /** logger */
    protected final static Logger         logger = Logger
                                                     .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /**
     * @param recmmd
     * @param resultFile
     */
    public Model(MatrixFactorizationRecommender recmmd, String resultFile) {
        super();
        this.recmmd = recmmd;
        this.resultFile = resultFile;
    }

    /**
     * process before building the model.
     * 
     * @param rateMatrix
     */
    public void preProc(final SparseMatrix rateMatrix) {

        if (recmmd instanceof WeigtedRSVD) {
            WeigtedRSVD lRecmmd = (WeigtedRSVD) recmmd;
            double[][] ensnblWeightInU = rateMatrix.probability(rows, cols, recmmd.maxValue,
                recmmd.minValue, true);
            lRecmmd.setEnsnblWeightInU(ensnblWeightInU);

            double[][] ensnblWeightInI = rateMatrix.probability(rows, cols, recmmd.maxValue,
                recmmd.minValue, false);
            lRecmmd.setEnsnblWeightInI(ensnblWeightInI);
        }

    }

    /**
     * build model
     * 
     * @param rateMatrix
     */
    public void buildModel(final SparseRowMatrix rateMatrix) {
        if (recmmd instanceof WeigtedRSVD) {
            WeigtedRSVD lRecmmd = (WeigtedRSVD) recmmd;
            MatlabFasionSparseMatrix lrMatrix = rateMatrix.partition(80 * 1000 * 1000, rows, cols);
            lRecmmd.buildModel(lrMatrix, rows, cols);
        } else {

            if (rows == null | cols == null) {
                recmmd.buildModel(rateMatrix);
            } else {
                SparseRowMatrix localMatrix = rateMatrix.partition(rows, cols);
                recmmd.buildModel(localMatrix);

                // suggest JVM to gc
                if (recmmd.userCount > 400 * 1000 & recmmd.itemCount > 12 * 1000) {
                    localMatrix.clear();
                    System.gc();
                }
            }
        }
    }

    /**
     * evaluate the model
     * 
     * @param testMatrix
     *            the matrix with testing data
     * @param cumPrediction
     *            the cumulative prediction
     * @param cumWeight
     *            the cumulative weights
     */
    public void evaluate(final SparseRowMatrix testMatrix, SparseRowMatrix cumPrediction,
                         SparseRowMatrix cumWeight) {
        if (rows != null | cols != null) {

            if (StringUtil.isBlank(resultFile)) {
                lEvaInner(testMatrix, cumPrediction, cumWeight);
            } else if (resultFile.endsWith("RWSVD")) {
                lEvaAndRecordWSVD(testMatrix, cumPrediction, cumWeight);
            } else if (resultFile.endsWith("Serial")) {
                lEvaAndSerialWSVD(testMatrix, cumPrediction, cumWeight);
            } else {
                throw new RuntimeException("the setting of resultFile is not matched.");
            }

        } else {
            //Specail Operation output Global result
            EvaluationMetrics globalResult = recmmd.evaluate(testMatrix);
            LoggerUtil.info(logger, "Singleton Model : \n" + EvaluationMetrics.printTitle() + "\n"
                                    + globalResult.printOneLine());

            // catch global model
            for (int u = 0; u < testMatrix.length()[0]; u++) {
                int[] indexList = testMatrix.getRowRef(u).indexList();
                if (indexList == null) {
                    continue;
                }

                for (int v : indexList) {
                    double prediction = recmmd.predict(u, v);
                    double weight = recmmd.weight(u, v, prediction);

                    double newCumPrediction = prediction * weight + cumPrediction.getValue(u, v);
                    double newCumWeight = weight + cumWeight.getValue(u, v);

                    cumPrediction.setValue(u, v, newCumPrediction);
                    cumWeight.setValue(u, v, newCumWeight);
                }
            }
        }

        // release recommender memory
        rows = null;
        cols = null;
        recmmd.explicitClear();
    }

    /**
     * evaluate the model
     * 
     * @param testMatrix
     *            the matrix with testing data
     * @param cumPrediction
     *            the cumulative prediction
     * @param cumWeight
     *            the cumulative weights
     */
    public void evaluate(final MatlabFasionSparseMatrix testMatrix) {
        //check whether the object belongs to WSVD
        if (!(recmmd instanceof WeigtedRSVD)) {
            throw new RuntimeException("The instance inf lEvaAndRecordWSVD is not WSVD!");
        }

        WeigtedRSVD lRecmmd = (WeigtedRSVD) recmmd;

        int[] uIndx = testMatrix.getRowIndx();
        int[] iIndx = testMatrix.getColIndx();
        double[] Auis = testMatrix.getVals();
        int nnz = testMatrix.getNnz();

        int bufferUsed = 0;
        StringBuilder buffer = new StringBuilder();
        for (int numSeq = 0; numSeq < nnz; numSeq++) {
            int u = uIndx[numSeq];
            int i = iIndx[numSeq];
            double AuiReal = Auis[numSeq];
            double AuiEst = lRecmmd.predicts(u, i);

            //record local prediction
            //userId, itemId, AuiReal, AuiEst, Pu, Pi, Pr, GroupId
            buffer.append(u).append(',').append(i).append(',').append(AuiReal).append(',')
                .append(AuiEst).append(',').append(lRecmmd.getPu(u, AuiEst)).append(',')
                .append(lRecmmd.getPi(i, AuiEst)).append(',').append(lRecmmd.getPr(AuiEst))
                .append(',').append(groupId).append('\n');
            bufferUsed++;

            // if greater than buffer size, then clear the buffer.
            if (bufferUsed >= 1000 * 1000) {
                FileUtil.writeAsAppend(resultFile, buffer.toString());

                //reset buffer
                bufferUsed = 0;
                buffer = new StringBuilder();
            }
        }

        FileUtil.writeAsAppend(resultFile, buffer.toString());

    }

    /**
     * locally evaluate the model
     * 
     * @param testMatrix
     * @param cumPrediction
     * @param cumWeight
     */
    protected void lEvaInner(final SparseRowMatrix testMatrix, SparseRowMatrix cumPrediction,
                             SparseRowMatrix cumWeight) {
        SparseRowMatrix ltestMatrix = testMatrix.partition(rows, cols);
        for (int u = 0; u < ltestMatrix.length()[0]; u++) {
            int[] itemList = ltestMatrix.getRowRef(u).indexList();
            if (itemList == null) {
                continue;
            }

            for (int i : itemList) {
                double prediction = recmmd.predict(u, i);
                double weight = recmmd.weight(u, i, prediction);

                double newCumPrediction = prediction * weight + cumPrediction.getValue(u, i);
                double newCumWeight = weight + cumWeight.getValue(u, i);

                cumPrediction.setValue(u, i, newCumPrediction);
                cumWeight.setValue(u, i, newCumWeight);
            }
        }
    }

    /**
     * locally evaluate the WeightedSVD model
     * 
     * @param testMatrix
     * @param cumPrediction
     * @param cumWeight
     */
    protected void lEvaAndRecordWSVD(final SparseRowMatrix testMatrix,
                                     SparseRowMatrix cumPrediction, SparseRowMatrix cumWeight) {
        //check whether the object belongs to WSVD
        if (!(recmmd instanceof WeigtedRSVD)) {
            throw new RuntimeException("The instance inf lEvaAndRecordWSVD is not WSVD!");
        }

        SparseRowMatrix ltestMatrix = testMatrix.partition(rows, cols);
        WeigtedRSVD lRecmmd = (WeigtedRSVD) recmmd;

        int itemCount = 0;
        StringBuilder buffer = new StringBuilder();
        for (int u = 0; u < ltestMatrix.length()[0]; u++) {
            int[] indexList = ltestMatrix.getRowRef(u).indexList();
            if (indexList == null) {
                continue;
            }

            for (int i : indexList) {
                double AuiEst = lRecmmd.predict(u, i);
                double weight = lRecmmd.weight(u, i, AuiEst);

                double newCumPrediction = AuiEst * weight + cumPrediction.getValue(u, i);
                double newCumWeight = weight + cumWeight.getValue(u, i);

                cumPrediction.setValue(u, i, newCumPrediction);
                cumWeight.setValue(u, i, newCumWeight);

                //record local prediction
                //userId, itemId, AuiReal, AuiEst, Pu, Pi, Pr, GroupId
                double AuiReal = testMatrix.getValue(u, i);
                buffer.append(u).append(',').append(i).append(',').append(AuiReal).append(',')
                    .append(AuiEst).append(',').append(lRecmmd.getPu(u, AuiEst)).append(',')
                    .append(lRecmmd.getPi(i, AuiEst)).append(',').append(lRecmmd.getPr(AuiEst))
                    .append(',').append(groupId).append('\n');
                itemCount++;
            }

            // if greater than buffer size, then clear the buffer.
            if (itemCount >= 1000 * 1000) {
                FileUtil.writeAsAppend(resultFile, buffer.toString());

                //reset buffer
                itemCount = 0;
                buffer = new StringBuilder();
            }
        }
        FileUtil.writeAsAppend(resultFile, buffer.toString());

        // release local model memory
        if (recmmd.userCount > 400 * 1000 & recmmd.itemCount > 12 * 1000) {
            ltestMatrix.clear();
        }
    }

    protected void lEvaAndSerialWSVD(final SparseRowMatrix testMatrix,
                                     SparseRowMatrix cumPrediction, SparseRowMatrix cumWeight) {
        lEvaInner(testMatrix, cumPrediction, cumWeight);

        //serialize the recommender
        String serialFile = resultFile + id + ".obj";
        SerializeUtil.writeObject(recmmd, serialFile);
    }

    /**
     * get the default predicted rating
     * 
     * @return
     */
    public double getDefaultRating() {
        return (recmmd.maxValue + recmmd.minValue) / 2.0;
    }

    /**
     * max rating value
     * 
     * @return
     */
    public double maxValue() {
        return recmmd.maxValue;
    }

    /**
     * min rating value
     * 
     * @return
     */
    public double minValue() {
        return recmmd.minValue;
    }

    /**
     * Setter method for property <tt>rows</tt>.
     * 
     * @param rows
     *            value to be assigned to property rows
     */
    public void setRows(int[] rows) {
        this.rows = rows;
    }

    /**
     * Setter method for property <tt>cols</tt>.
     * 
     * @param cols
     *            value to be assigned to property cols
     */
    public void setCols(int[] cols) {
        this.cols = cols;
    }

    /**
     * Getter method for property <tt>rows</tt>.
     * 
     * @return property value of rows
     */
    public int[] getRows() {
        return rows;
    }

    /**
     * Getter method for property <tt>cols</tt>.
     * 
     * @return property value of cols
     */
    public int[] getCols() {
        return cols;
    }

    /**
     * Getter method for property <tt>id</tt>.
     * 
     * @return property value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for property <tt>id</tt>.
     * 
     * @param id value to be assigned to property id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method for property <tt>groupId</tt>.
     * 
     * @return property value of groupId
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Setter method for property <tt>groupId</tt>.
     * 
     * @param groupId value to be assigned to property groupId
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Getter method for property <tt>recmmd</tt>.
     * 
     * @return property value of recmmd
     */
    public MatrixFactorizationRecommender getRecmmd() {
        return recmmd;
    }

    /**
     * Setter method for property <tt>recmmd</tt>.
     * 
     * @param recmmd value to be assigned to property recmmd
     */
    public void setRecmmd(MatrixFactorizationRecommender recmmd) {
        this.recmmd = recmmd;
    }

}