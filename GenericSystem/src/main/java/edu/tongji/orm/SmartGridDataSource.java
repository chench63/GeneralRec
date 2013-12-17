/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import edu.tongji.exception.DataSourceErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.MeterReadingVO;

/**
 * SmartGrid数据集适配的数据源
 * 
 * @author chenkh
 * @version $Id: UMassSmartGridDataSource.java, v 0.1 2013-12-17 下午4:29:27 chenkh Exp $
 */
public class SmartGridDataSource implements DataSource {

    /** 需要加载的文件  **/
    private Map<TemplateType, String>  sourceEntity;

    /** 电表读数上下文*/
    public static List<MeterReadingVO> meterContexts    = new ArrayList<MeterReadingVO>();

    /** logger*/
    private static final Logger        logger           = Logger
                                                            .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 间隔读数*/
    public final static long           READING_INTERVAL = 15 * 60 * 1000;

    /** 
     * @see edu.tongji.orm.DataSource#isLazy()
     */
    @Override
    public boolean isLazy() {
        return false;
    }

    private void load() {

        for (Iterator<Entry<TemplateType, String>> iter = sourceEntity.entrySet().iterator(); iter
            .hasNext();) {
            Entry<TemplateType, String> entry = iter.next();
            TemplateType parserType = entry.getKey();
            File file = new File(entry.getValue());

            //验证文件是否存在
            LoggerUtil.debug(logger, "开始加载数据文件: " + entry.getValue());
            if (!file.isFile() | !file.exists()) {
                LoggerUtil.warn(logger, "无法找到对应的加载文件: " + entry.getValue());
                continue;
            }

            //读取并解析数据
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String context = null;
                MeterReadingVO meterReading = null;
                while ((context = reader.readLine()) != null) {
                    ParserTemplate template = new ParserTemplate();
                    template.setTemplate(context);

                    // 解析
                    MeterReadingVO meter = (MeterReadingVO) parserType.parser(template);
                    if (meterReading == null) {
                        //初始化
                        meterReading = meter;
                        continue;
                    } else if (meter.getTimeVal() > meterReading.getTimeVal() + READING_INTERVAL) {
                        //新的电表计时周期
                        meterContexts.add(meterReading);
                        meterReading = meter;
                        continue;
                    }
                    //在同一计时周期，累计读数
                    meterReading.setReading(meterReading.getReading() + meter.getReading());
                }
            } catch (FileNotFoundException e) {
                ExceptionUtil.caught(e, "无法找到对应的加载文件: " + entry.getValue());
            } catch (IOException e) {
                ExceptionUtil.caught(e, "读取文件发生异常，校验文件格式");
            } finally {
                IOUtils.closeQuietly(reader);
            }

        }
    }

    /** 
     * @see edu.tongji.orm.DataSource#reload()
     */
    @Override
    public void reload() {
        load();
    }

    /** 
     * @see edu.tongji.orm.DataSource#excute(java.lang.String)
     */
    @Override
    public List<? extends Serializable> excute(String expression) {
        throw new OwnedException(DataSourceErrorCode.NOT_SUPPORT_EXCUTE);
    }

    /** 
     * @see edu.tongji.orm.DataSource#excuteEx(java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map excuteEx(String expression) {
        throw new OwnedException(DataSourceErrorCode.NOT_SUPPORT_EXCUTEEX);
    }

    /**
     * Getter method for property <tt>sourceEntity</tt>.
     * 
     * @return property value of sourceEntity
     */
    public Map<TemplateType, String> getSourceEntity() {
        return sourceEntity;
    }

    /**
     * Setter method for property <tt>sourceEntity</tt>.
     * 
     * @param sourceEntity value to be assigned to property sourceEntity
     */
    public void setSourceEntity(Map<TemplateType, String> sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

}
