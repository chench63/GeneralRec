/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.engine.recommendation;

import edu.tongji.function.Function;
import edu.tongji.processor.RecommendationProcessor;

/**
 * 
 * @author chench
 * @version $Id: RecommendationEngine.java, v 0.1 2013-9-7 下午9:44:35 chench Exp $
 */
public class FileBasedRcmdtnEngine extends RcmdtnEngine {

    /** 
     * @see edu.tongji.engine.Engine#excute()
     */
    @Override
    public void excute() {

        //0. 加载数据源，将数据加载至内存中
        dataSource.reload();

        //1. 对原始数据进行采集筛选
        contextEnvelope.sampling(dataSource);

        //2. 进一步转化了内部抽象数据集
        processorContext.switchToProcessorContext(contextEnvelope);

        //3. 调用处理器，处理数据
        for (Function function : functions) {
            ((RecommendationProcessor) processor).setSimilarityFunction(function);
            processor.process(processorContext);
        }
    }

}
