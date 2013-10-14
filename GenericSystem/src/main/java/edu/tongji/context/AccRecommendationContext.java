/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.model.Rating;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;

/**
 * 
 * @author chench
 * @version $Id: MultipartSecureRecommendationContext.java, v 0.1 16 Sep 2013 21:30:44 chench Exp $
 */
public class AccRecommendationContext implements ProcessorContext {

    /** Item I平均值版本更新记录 */
    private final Map<String, VersionEnvelope> versionsTableOfItemI       = new HashMap<String, VersionEnvelope>();

    /** Item J平均值版本更新记录 */
    private final Map<String, VersionEnvelope> versionsTableOfItemJ       = new HashMap<String, VersionEnvelope>();

    /** Item I 用户标记模板 */
    protected final Map<String, Integer>       userTableOfItemI           = new HashMap<String, Integer>();

    /** Item J 用户标记模板 */
    protected final Map<String, Integer>       userTableOfItemJ           = new HashMap<String, Integer>();

    /** 新增Item I用户列表*/
    protected final List<Rating>               newCustomersOfItemI        = new ArrayList<Rating>();

    /** 新增Item J用户列表*/
    protected final List<Rating>               newCustomersOfItemJ        = new ArrayList<Rating>();

    /** 累计分子值 */
    private double                             accNumeratorValue          = 0.0;

    /** 累计分母值item I部分 */
    private double                             accNenominatorOfItemIValue = 0.0;

    /** 累计分母值item J部分 */
    private double                             accNenominatorOfItemJValue = 0.0;

    /** 当前版本号 */
    private String                             currentVersionOfItemI;

    /** 当前版本号 */
    private String                             currentVersionOfItemJ;

    /** itemI编号*/
    protected String                           itemI;

    /** itemJ编号*/
    protected String                           itemJ;

    /** itemI 平均值变化大小 */
    private double                             avgDiffOfI                 = 0.0;

    /** itemJ 平均值变化大小 */
    private double                             avgDiffOfJ                 = 0.0;

    /** logger */
    private final static Logger                logger                     = Logger
                                                                              .getLogger(LoggerDefineConstant.SERVICE_NORMAL);

    /** 
     * @see edu.tongji.context.ProcessorContext#clearContext()
     */
    @Override
    public void clearContext() {
        versionsTableOfItemI.clear();
        versionsTableOfItemJ.clear();
        userTableOfItemI.clear();
        userTableOfItemJ.clear();

        accNumeratorValue = 0.0;
        accNenominatorOfItemIValue = 0.0;
        accNenominatorOfItemJValue = 0.0;

        //其实不清楚也可以，保持整体一致
        itemI = null;
        itemJ = null;
        currentVersionOfItemI = null;
        currentVersionOfItemJ = null;
        avgDiffOfI = 0.0;
        avgDiffOfJ = 0.0;
    }

    /** 
     * @see edu.tongji.context.ProcessorContext#switchToProcessorContext(edu.tongji.context.ContextEnvelope)
     */
    @Override
    public void switchToProcessorContext(ContextEnvelope contextEnvelope) {
        newCustomersOfItemI.clear();
        newCustomersOfItemJ.clear();

        //0. 修补数据集
        complementMissingRating(contextEnvelope);

        //1. 校验数据集
        if (checkDataSetAndShouldStop(contextEnvelope)) {
            return;
        }

        //2. 计算itemI(J)相关的用户集大小,此处计算的用户集不包括新增部分
        int countOfCommonUsers = 0;
        for (String usrId : userTableOfItemI.keySet()) {
            if (userTableOfItemJ.containsKey(usrId)) {
                countOfCommonUsers++;
            }
        }
        int countOfUsers = userTableOfItemI.size() + userTableOfItemJ.size() - countOfCommonUsers;

        //3. 筛选获得对itemI(J)新增的评价
        Rating rating = null;
        for (Serializable customer : contextEnvelope.getResultSet()) {
            rating = (Rating) customer;
            doSwitchToProcessorContext(rating);
        }

        //4. 更新相应的itemI(J)的平均值及对旧用户误差修正
        avgDiffOfI = updateToNewVersion(newCustomersOfItemI, versionsTableOfItemI, true,
            accNenominatorOfItemIValue, countOfUsers);
        avgDiffOfJ = updateToNewVersion(newCustomersOfItemJ, versionsTableOfItemJ, false,
            accNenominatorOfItemJValue, countOfUsers);
        accNumeratorValue += countOfUsers * avgDiffOfI * avgDiffOfJ;
        LoggerUtil.debug(logger, "修正分子部分: \t    " + accNumeratorValue);
    }

    /**
     * 校验和修补数据集。<br />
     * 
     * 但更新周期内的人数少于2时，会不更新这部分的用户投票数据，为了避免丢失[丢失用户]。
     * 用户当天投票即算在线，整合当天投过票的[丢失用户]算入数据集，如果数据集数量小于2，则返回true，否则返回false。
     * 
     * @param dataSet
     * @return
     */
    protected void complementMissingRating(ContextEnvelope contextEnvelope) {
        //Do nothing
    }

    /**
     * 校验和修补数据集。<br />
     * 如果需要更新的数据集数量小于2，则返回true，否则返回false。
     * 
     * @param contextEnvelope
     * @return
     */
    protected boolean checkDataSetAndShouldStop(ContextEnvelope contextEnvelope) {
        return false;
    }

    /**
     * 筛选用户，并将用户放于应该对应的计算列表
     * 
     * @param rating
     */
    private void doSwitchToProcessorContext(Rating rating) {
        //过滤不需要更新的老用户
        if (userTableOfItemI.containsKey(rating.getUsrId())
            && userTableOfItemJ.containsKey(rating.getUsrId())) {
            return;
        }

        if (StringUtil.equalsIgnoreCase(rating.getMovieId(), itemI)
            && !userTableOfItemI.containsKey(rating.getUsrId())) {
            userTableOfItemI.put(rating.getUsrId(), rating.getRating());
            newCustomersOfItemI.add(rating);
        }

        if (StringUtil.equalsIgnoreCase(rating.getMovieId(), itemJ)
            && !userTableOfItemJ.containsKey(rating.getUsrId())) {
            userTableOfItemJ.put(rating.getUsrId(), rating.getRating());
            newCustomersOfItemJ.add(rating);
        }

    }

    /**
     * 生成新的平均值，并计入版本
     * 
     * @param newCustomers      item评分向量
     * @param versionsTable     对应item的版本记录表
     * @param currentVersion    对应item当前版本号
     * @param accNenominatorOfItemValue     累计分母的裸值
     * @param countOfAllUsers               原始集合中，ItemI与ItemJ所拥有的用户集大小
     * @return                  版本变更引起的平均值变化大小
     */
    private double updateToNewVersion(List<Rating> newCustomers,
                                      Map<String, VersionEnvelope> versionsTable, boolean isItemI,
                                      double accNenominatorOfItemValue, int countOfUsers) {
        if (versionsTable.isEmpty()) {
            initialNewVersion(newCustomers, versionsTable, isItemI);
            return 0.0;
        }

        //0. 计算itemI(J)评分总和
        double accSumOfRating = 0.0;
        Rating rating = null;
        for (Serializable customer : newCustomers) {
            rating = (Rating) customer;
            accSumOfRating += rating.getRating();
        }

        //1. 计算itemI(J)相关的用户集大小,此处计算的用户集[包括]新增部分
        int countOfCommonUsers = 0;
        for (String usrId : userTableOfItemI.keySet()) {
            if (userTableOfItemJ.containsKey(usrId)) {
                countOfCommonUsers++;
            }
        }
        int countOfAllUsers = userTableOfItemI.size() + userTableOfItemJ.size()
                              - countOfCommonUsers;

        String currentVersion = isItemI ? currentVersionOfItemI : currentVersionOfItemJ;
        VersionEnvelope oldVer = versionsTable.get(currentVersion);
        double sumOfRating = oldVer.getSumOfRating() + accSumOfRating;
        VersionEnvelope newVer = new VersionEnvelope();
        newVer.setCountOfUsers(countOfUsers);
        newVer.setSumOfRating(sumOfRating);
        //注意此处，求平均值使用 总人数
        newVer.setAvgOfRating(sumOfRating / countOfAllUsers);

        //更新上下文
        int versionValue = Integer.valueOf(currentVersion) + 1;
        versionsTable.put(String.valueOf(versionValue), newVer);
        accNenominatorOfItemValue += countOfUsers
                                     * Math.pow(newVer.getAvgOfRating() - oldVer.getAvgOfRating(),
                                         2);
        if (isItemI) {
            currentVersionOfItemI = String.valueOf(versionValue);
            accNenominatorOfItemIValue = accNenominatorOfItemValue;
        } else {
            currentVersionOfItemJ = String.valueOf(versionValue);
            accNenominatorOfItemJValue = accNenominatorOfItemValue;
        }

        LoggerUtil.debug(logger, "修正" + (isItemI ? "itemI" : "itemJ") + "分母部分： "
                                 + accNenominatorOfItemValue);
        return newVer.getAvgOfRating() - oldVer.getAvgOfRating();
    }

    /**
     * 初始化首批数据
     */
    private void initialNewVersion(List<Rating> newCustomers,
                                   Map<String, VersionEnvelope> versionsTable, boolean isItemI) {
        //0. 计算itemI(J)评分总和
        double accSumOfRating = 0.0;
        Rating rating = null;
        for (Serializable customer : newCustomers) {
            rating = (Rating) customer;
            accSumOfRating += rating.getRating();
        }

        //1. 计算itemI(J)相关的用户集大小,此处计算的用户集[包括]新增部分
        int countOfCommonUsers = 0;
        for (String usrId : userTableOfItemI.keySet()) {
            if (userTableOfItemJ.containsKey(usrId)) {
                countOfCommonUsers++;
            }
        }
        int countOfAllUsers = userTableOfItemI.size() + userTableOfItemJ.size()
                              - countOfCommonUsers;

        //2. 更新版本,初始化上下文
        VersionEnvelope newVer = new VersionEnvelope();
        newVer.setCountOfUsers(newCustomers.size());
        newVer.setSumOfRating(accSumOfRating);
        newVer.setAvgOfRating(accSumOfRating / countOfAllUsers);

        if (isItemI) {
            currentVersionOfItemI = "1";
        } else {
            currentVersionOfItemJ = "1";
        }
        versionsTable.put("1", newVer);
    }

    /**
     * Getter method for property <tt>accNumeratorValue</tt>.
     * 
     * @return property value of accNumeratorValue
     */
    public double getAccNumeratorValue() {
        return accNumeratorValue;
    }

    /**
     * Setter method for property <tt>accNumeratorValue</tt>.
     * 
     * @param accNumeratorValue value to be assigned to property accNumeratorValue
     */
    public void setAccNumeratorValue(double accNumeratorValue) {
        this.accNumeratorValue = accNumeratorValue;
    }

    /**
     * Getter method for property <tt>accNenominatorOfItemIValue</tt>.
     * 
     * @return property value of accNenominatorOfItemIValue
     */
    public double getAccNenominatorOfItemIValue() {
        return accNenominatorOfItemIValue;
    }

    /**
     * Setter method for property <tt>accNenominatorOfItemIValue</tt>.
     * 
     * @param accNenominatorOfItemIValue value to be assigned to property accNenominatorOfItemIValue
     */
    public void setAccNenominatorOfItemIValue(double accNenominatorOfItemIValue) {
        this.accNenominatorOfItemIValue = accNenominatorOfItemIValue;
    }

    /**
     * Getter method for property <tt>accNenominatorOfItemJValue</tt>.
     * 
     * @return property value of accNenominatorOfItemJValue
     */
    public double getAccNenominatorOfItemJValue() {
        return accNenominatorOfItemJValue;
    }

    /**
     * Setter method for property <tt>accNenominatorOfItemJValue</tt>.
     * 
     * @param accNenominatorOfItemJValue value to be assigned to property accNenominatorOfItemJValue
     */
    public void setAccNenominatorOfItemJValue(double accNenominatorOfItemJValue) {
        this.accNenominatorOfItemJValue = accNenominatorOfItemJValue;
    }

    /**
     * Getter method for property <tt>itemI</tt>.
     * 
     * @return property value of itemI
     */
    public String getItemI() {
        return itemI;
    }

    /**
     * Setter method for property <tt>itemI</tt>.
     * 
     * @param itemI value to be assigned to property itemI
     */
    public void setItemI(String itemI) {
        this.itemI = itemI;
    }

    /**
     * Getter method for property <tt>itemJ</tt>.
     * 
     * @return property value of itemJ
     */
    public String getItemJ() {
        return itemJ;
    }

    /**
     * Setter method for property <tt>itemJ</tt>.
     * 
     * @param itemJ value to be assigned to property itemJ
     */
    public void setItemJ(String itemJ) {
        this.itemJ = itemJ;
    }

    /**
     * Getter method for property <tt>userTableOfItemI</tt>.
     * 
     * @return property value of userTableOfItemI
     */
    public Map<String, Integer> getUserTableOfItemI() {
        return userTableOfItemI;
    }

    /**
     * Getter method for property <tt>userTableOfItemJ</tt>.
     * 
     * @return property value of userTableOfItemJ
     */
    public Map<String, Integer> getUserTableOfItemJ() {
        return userTableOfItemJ;
    }

    /**
     * Getter method for property <tt>newCustomersOfItemI</tt>.
     * 
     * @return property value of newCustomersOfItemI
     */
    public List<Rating> getNewCustomersOfItemI() {
        return newCustomersOfItemI;
    }

    /**
     * Getter method for property <tt>newCustomersOfItemJ</tt>.
     * 
     * @return property value of newCustomersOfItemJ
     */
    public List<Rating> getNewCustomersOfItemJ() {
        return newCustomersOfItemJ;
    }

    /**
     * Getter method for property <tt>avgDiffOfI</tt>.
     * 
     * @return property value of avgDiffOfI
     */
    public double getAvgDiffOfI() {
        return avgDiffOfI;
    }

    /**
     * Getter method for property <tt>avgDiffOfJ</tt>.
     * 
     * @return property value of avgDiffOfJ
     */
    public double getAvgDiffOfJ() {
        return avgDiffOfJ;
    }

    /**
     * Getter method for property <tt>versionsTableOfItemI</tt>.
     * 
     * @return property value of versionsTableOfItemI
     */
    public Map<String, VersionEnvelope> getVersionsTableOfItemI() {
        return versionsTableOfItemI;
    }

    /**
     * Getter method for property <tt>versionsTableOfItemJ</tt>.
     * 
     * @return property value of versionsTableOfItemJ
     */
    public Map<String, VersionEnvelope> getVersionsTableOfItemJ() {
        return versionsTableOfItemJ;
    }

    /**
     * Getter method for property <tt>currentVersionOfItemI</tt>.
     * 
     * @return property value of currentVersionOfItemI
     */
    public String getCurrentVersionOfItemI() {
        return currentVersionOfItemI;
    }

    /**
     * Getter method for property <tt>currentVersionOfItemJ</tt>.
     * 
     * @return property value of currentVersionOfItemJ
     */
    public String getCurrentVersionOfItemJ() {
        return currentVersionOfItemJ;
    }

}
