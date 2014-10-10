/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.BayesianEventVO;

/**
 * 根据Redd Event文件，输出java版本的贝叶斯网络CP代码
 * 
 * @author Hanke Chen
 * @version $Id: ConditionalProbabilityCodeGen.java, v 0.1 2014-5-27 下午9:06:18 chench Exp $
 */
public final class ConditionalProbabilityCodeGen {

    /** 原始文件*/
    private final static String   SOURCE = "E:/H[1,6]_EVNT_TRAININGSET";

    /** 目标文件*/
    private final static String[] TARGET = { "E:/H16_CP_AHI", "E:/H16_CP_PA" };

    /** logger */
    private final static Logger   logger = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        LoggerUtil.info(logger, "0. Program: ConditionalProbabilityFileTransformer ");

        //1. 读取并解析数据
        String[] lines = FileUtil.readLinesByPattern(SOURCE);
        List<BayesianEventVO> source = new ArrayList<BayesianEventVO>();
        for (String line : lines) {

            if (StringUtil.isBlank(line)) {
                continue;
            }

            source.add((BayesianEventVO) TemplateType.BAYESIAN_EVENT_TEMPLATE
                .parser(new ParserTemplate(line)));
        }
        LoggerUtil.info(logger, "1. Finish loading source files.");

        //2. 计算条件概率
        //  a) P[ O=on | H,I ]
        p_O_TI(source);
        LoggerUtil.info(logger, "2. Finish creating P[ O=on | H,I ] file.");
        //  b) P[P | O]
        p_P_O(source);
        LoggerUtil.info(logger, "3. Finish creating P[P | O] file.");

    }

    /**
     * 统计 P[ O=on | H,I ]
     * 
     * @param source
     */
    protected static void p_O_TI(List<BayesianEventVO> source) {

        //1. 统计时间发生频率
        Map<String, Float> probblties = new HashMap<String, Float>();
        int[][] numOfConditions = new int[5][2];
        for (BayesianEventVO evnt : source) {
            short ac = evnt.getAc();
            int hot = hotType(evnt.getHot());
            short indoor = evnt.getIndoor();

            //p_O_TI[ac][hot][indoor]=
            String key = (new StringBuilder("p_O_TI[")).append(ac).append("][").append(hot)
                .append("][").append(indoor).append("]=").toString();

            //记录时间值
            Float prob = probblties.get(key);
            if (prob == null) {
                prob = 0.0f;
            }
            prob += 1.0f;
            numOfConditions[hot][indoor]++;

            probblties.put(key, prob);
        }

        //2. 输出条件概率
        StringBuilder content = new StringBuilder();
        //  a)indoor = 1,条件
        short indoor = 1;
        for (int ac = 0; ac <= 1; ac++) {
            for (int hot = 0; hot <= 3; hot++) {
                String key = (new StringBuilder("p_O_TI[")).append(ac).append("][").append(hot)
                    .append("][").append(indoor).append("]=").toString();
                Float prob = probblties.get(key);

                //laplace smooth
                prob = (prob == null) ? 1.0f : (prob + 1.0f);
                prob /= (numOfConditions[hot][indoor] + 2);

                //加入文件
                content.append(key).append(prob).append("f;").append(FileUtil.BREAK_LINE);
            }
        }

        //  b)indoor = 0条件
        indoor = 0;
        short ac = 0;
        for (int hot = 0; hot <= 3; hot++) {
            String key = (new StringBuilder("p_O_TI[")).append(ac).append("][").append(hot)
                .append("][").append(indoor).append("]=").toString();
            Float prob = 1.0f;

            //加入文件
            content.append(key).append(prob).append("f;").append(FileUtil.BREAK_LINE);
        }

        //4. 写入文件
        FileUtil.write(TARGET[0], content.toString());
    }

    /**
     * 统计P[P | O]
     * 
     * @param source
     */
    protected static void p_P_O(List<BayesianEventVO> source) {
        //1. 统计时间发生频率
        Map<String, Float> probblties = new HashMap<String, Float>();
        int[] numOfConditions = new int[3];
        for (BayesianEventVO evnt : source) {
            short ac = evnt.getAc();
            int power = powerType(evnt.getPower());

            //p_P_O[power][ac]=
            String key = (new StringBuilder("p_P_O[")).append(power).append("][").append(ac)
                .append("]=").toString();

            //记录时间值
            Float prob = probblties.get(key);
            if (prob == null) {
                prob = 0.0f;
            }
            prob += 1.0f;
            numOfConditions[ac]++;

            probblties.put(key, prob);
        }

        //2. 输出条件概率
        StringBuilder content = new StringBuilder();
        for (int power = 0; power <= 3; power++) {
            for (int ac = 0; ac <= 1; ac++) {
                String key = (new StringBuilder("p_P_O[")).append(power).append("][").append(ac)
                    .append("]=").toString();
                Float prob = probblties.get(key);

                //laplace smooth
                prob = (prob == null) ? 1.0f : (prob + 1.0f);
                prob /= (numOfConditions[ac] + 4);

                //加入文件
                content.append(key).append(prob).append("f;").append(FileUtil.BREAK_LINE);
            }
        }

        //4. 写入文件
        FileUtil.write(TARGET[1], content.toString());
    }

    /**
     * 离散化温度值<br/>
     * (-,16)      : 0<br/>
     * [16, 22)    : 1<br/>
     * [22, 28)    : 2<br/>
     * [28, +)     : 3<br/>
     * 
     * @param hot
     * @return
     */
    protected static int hotType(float hot) {
        if (hot < 16.0f) {
            return 0;
        }

        int type = 1;
        return (type += Float.valueOf((hot - 16) / 6).intValue()) > 3 ? 3 : type;
    }

    /**
     * 离散化能耗值<br/>
     * 0: (-, 400）<br/>
     * 1： [400， 800）<br/>
     * 2: [800, 1200)<br/>
     * 3: [1200, +)<br/>
     * 
     * @param power
     * @return
     */
    protected static int powerType(float power) {
        if (power < 400) {
            return 0;
        }

        int type = 1;
        return (type += Float.valueOf((power - 400) / 400).intValue()) > 3 ? 3 : type;
    }
}
