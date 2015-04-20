package edu.tongji.data;

import java.util.List;
import java.util.Queue;

import edu.tongji.util.StringUtil;
import prea.util.ModelUtil;

/**
 * the group of models.
 * 
 * @author Hanke
 * @version $Id: Cocluster.java, v 0.1 2014-11-2 下午12:53:29 Exp $
 */
public class ModelGroup {

    /** root file directory*/
    private static String rootDir;
    /** file contains setting*/
    private String        settingFile;
    /** file contains mapping w.r.t rows*/
    private String        rowMappingFile;
    /** file contains mapping w.r.t columns*/
    private String        colMappingFile;
    /** real models*/
    private List<Model>   models;

    /**
     * join the contained models to the given queue
     * 
     * @param m the queue to add
     */
    public void join(Queue<Model> m, SparseMatrix rateMatrix, int groupId) {
        //read configuration
        if (StringUtil.isNotBlank(colMappingFile) && StringUtil.isNotBlank(rowMappingFile)
            && StringUtil.isNotBlank(settingFile)) {
            colMappingFile = rootDir + colMappingFile;
            rowMappingFile = rootDir + rowMappingFile;
            settingFile = rootDir + settingFile;

        }

        ModelUtil.readModels(settingFile, rowMappingFile, colMappingFile, models);

        //join models
        int currId = m.size();
        for (Model model : models) {
            float[][] userWeights = rateMatrix.probability(model.getRows(), model.getCols(),
                model.maxValue(), model.minValue(), true);
            float[][] itemWeights = rateMatrix.probability(model.getRows(), model.getCols(),
                model.maxValue(), model.minValue(), false);
            model.setWeights(userWeights, itemWeights);
            model.setId(currId);
            model.setGroupId(groupId);
            m.add(model);

            currId++;
        }

        //clear reference
        models.clear();
        models = null;
    }

    /**
     * Setter method for property <tt>settingFile</tt>.
     * 
     * @param settingFile value to be assigned to property settingFile
     */
    public void setSettingFile(String settingFile) {
        this.settingFile = settingFile;
    }

    /**
     * Setter method for property <tt>rowMappingFile</tt>.
     * 
     * @param rowMappingFile value to be assigned to property rowMappingFile
     */
    public void setRowMappingFile(String rowMappingFile) {
        this.rowMappingFile = rowMappingFile;
    }

    /**
     * Setter method for property <tt>colMappingFile</tt>.
     * 
     * @param colMappingFile value to be assigned to property colMappingFile
     */
    public void setColMappingFile(String colMappingFile) {
        this.colMappingFile = colMappingFile;
    }

    /**
     * Setter method for property <tt>models</tt>.
     * 
     * @param models value to be assigned to property models
     */
    public void setModels(List<Model> models) {
        this.models = models;
    }

    /**
     * Setter method for property <tt>rootDir</tt>.
     * 
     * @param rootDir value to be assigned to property rootDir
     */
    public static void setRootDir(String rootDir) {
        ModelGroup.rootDir = rootDir;
    }

    /**
     * Getter method for property <tt>models</tt>.
     * 
     * @return property value of models
     */
    public List<Model> getModels() {
        return models;
    }

}
