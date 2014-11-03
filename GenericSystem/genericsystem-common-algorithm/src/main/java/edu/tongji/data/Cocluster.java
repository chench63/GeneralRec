package edu.tongji.data;

import java.util.List;
import prea.util.ModelUtil;

/**
 * Cocluster
 * 
 * @author Hanke
 * @version $Id: Cocluster.java, v 0.1 2014-11-2 下午12:53:29 Exp $
 */
public class Cocluster {

    private String      settingFile;
    private String      rowMappingFile;
    private String      colMappingFile;

    private List<Model> models;

    public List<Model> assembleModels() {
        List<Model> result = models;
        ModelUtil.readCocluster(settingFile, rowMappingFile, colMappingFile, result);
        models = null;
        return result;
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

}
