/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.engine.smartgrid.support;

import java.util.List;

import edu.tongji.vo.MeterReadingVO;

/**
 * Data set Assembler 主要用于SmartGridEngine.assembleDataSet方法 <br/>
 * 
 * @see edu.tongji.engine.smartgrid.SmartGridEngine#assembleDataSet() 
 * @author chench
 * @version $Id: DataSetAssembler.java, v 0.1 5 Apr 2014 12:21:10 chench Exp $
 */
public interface DataSetAssembler {

    /**
     * 汇总合并数据，并放入目标数据
     * 
     * @param source
     * @param target
     */
    public void assemble(List<MeterReadingVO> source, List<MeterReadingVO> target);

}
