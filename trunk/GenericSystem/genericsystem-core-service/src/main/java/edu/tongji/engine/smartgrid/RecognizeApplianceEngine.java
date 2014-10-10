/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.vo.MeterReadingVO;

/**
 * 识别特定家用电器模型
 * 
 * @author Hanke Chen
 * @version $Id: RecognizeApplianceEngine.java, v 0.1 21 Apr 2014 14:39:20 chench Exp $
 */
public class RecognizeApplianceEngine extends SmartGridEngine {

    /** 测试集缓存*/
    protected final Map<String, List<MeterReadingVO>> testCases = new HashMap<String, List<MeterReadingVO>>();

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#loadDataSet()
     */
    @Override
    protected void loadDataSet() {
        super.loadDataSet();
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#assembleDataSet()
     */
    @Override
    protected void assembleDataSet() {
        super.assembleDataSet();
    }

    /** 
     * @see edu.tongji.engine.smartgrid.SmartGridEngine#emulate()
     */
    @Override
    protected void emulate() {
        //识别Appliance

    }

}
