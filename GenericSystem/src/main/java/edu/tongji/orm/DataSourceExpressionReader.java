/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.orm;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.tongji.exception.ExpressionErrorCode;
import edu.tongji.exception.OwnedException;
import edu.tongji.util.StringUtil;

/**
 * DataSource结构表达式：
 * <p>
 *      语法：<br/>
 *      FROM [TABLENAME] WHERE [CONDITION]<br/>
 *      *TABLENAME*:<br/>
 *      [SERIALIZABLE_BEAN_CLASS_NAME]<br/>
 *      *CONDITION*:<br/>
 *      [TABLENAME].property [operator] [CONSTANT]
 *      
 * </p>
 * 
 * @author chenkh
 * @version $Id: DatDatasourceHelper.java, v 0.1 2013-9-6 下午7:21:46 chenkh Exp $
 */
public final class DataSourceExpressionReader {

    /** 关联对象词素FROM */
    public final static String FROM               = "FROM";

    /** 关联条件词素WHERE */
    public final static String WHERE              = "WHERE";

    /** 布尔算术符号 [>] */
    public final static String BIGGER             = ">";

    /** 布尔算术符号 [<] */
    public final static String SMALLER            = "<";

    /** 布尔算术符号 [=] */
    public final static String EQUAL              = "=";

    /** 表名命名规则中，后缀 */
    public final static String TABLE_NAME_SUFFIX  = "_BEAN";

    /** 词素分隔符(正则表达方式) */
    public final static String WORD_SEPERATOR     = ">|<|=";

    /** 对象属性分隔符(正则表达方式) */
    public final static String PROPERTY_SEPERATOR = "//.";

    /**
     * 表示式expr，解析出FROM和WHERE之间的表名
     * 
     * @param expr
     * @return
     */
    public static SerializableBeanType readBeanType(String expr) {
        int indexOfFROM = expr.indexOf(FROM) + FROM.length();
        if (indexOfFROM < 0) {
            throw new OwnedException(ExpressionErrorCode.NOT_EXIST_FROM);
        }

        String tableName = null;
        int indexOfWhere = expr.indexOf(WHERE);
        tableName = (indexOfWhere != -1) ? expr.substring(indexOfFROM, indexOfWhere) : expr
            .substring(indexOfFROM);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtil.toUpperCase(tableName.trim())).append(TABLE_NAME_SUFFIX);

        return SerializableBeanType.valueOf(stringBuilder.toString());
    }

    /**
     * 在给定数据集合中，按照DataSource结构结构表达式，搜索出符合Condition的结果集合
     * 
     * @param expr
     * @param context
     * @return
     * @throws Throwable 
     */
    public static List<? extends Serializable> read(String expr,
                                                    Map<String, ? extends Serializable> context,
                                                    SerializableBeanType beanType) throws Throwable {
        String conditions = StringUtil.substringAfter(expr, WHERE);
        if (StringUtil.isEmpty(conditions)) {
            return (new ArrayList<Serializable>(context.values()));
        }

        return doReadAsSingleCondition(expr, context, beanType);
    }

    /**
     * 
     * 
     * @param singleCondition       单条件
     * @param context               数据集
     * @param beanType              SerializableBean枚举
     * @return
     * @throws Throwable
     */
    private static List<? extends Serializable> doReadAsSingleCondition(String singleCondition,
                                                                        Map<String, ? extends Serializable> context,
                                                                        SerializableBeanType beanType)
                                                                                                      throws Throwable {
        String[] words = singleCondition.split(WORD_SEPERATOR);
        String propertyName = words[0].split(PROPERTY_SEPERATOR)[1];
        String propertyValue = words[1];
        String operator = singleCondition.substring(propertyName.length(),
            singleCondition.length() - propertyValue.length()).trim();

        return doReadAsSingleCondition(context, beanType, propertyName, propertyValue, operator);

    }

    /**
     * 
     * 
     * @param context           数据集
     * @param beanType          SerializableBean枚举
     * @param propertyName      SerializableBean的成员变量名
     * @param propertyValue     表达式左值的实例对象
     * @param operator          表达式的操作符
     * @return
     * @throws Throwable
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<? extends Serializable> doReadAsSingleCondition(Map<String, ? extends Serializable> context,
                                                                        SerializableBeanType beanType,
                                                                        String propertyName,
                                                                        String propertyValue,
                                                                        String operator)
                                                                                        throws Throwable {
        //获得属性的getter
        BeanInfo beanInfo = null;
        PropertyDescriptor[] descriptors = null;
        Class clazTypeOfProperty = null;
        Method readMethodOfProperty = null;

        beanInfo = Introspector.getBeanInfo(beanType.getClass());
        descriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {
            if (StringUtil.equals(descriptor.getName(), propertyName)) {
                clazTypeOfProperty = descriptor.getPropertyType();
                readMethodOfProperty = descriptor.getReadMethod();
                break;
            }
        }

        //实例化 常数值 对象
        Constructor constructorOfProperty = clazTypeOfProperty.getConstructor();
        Object ObjectOfProperty = constructorOfProperty.newInstance();

        if (ObjectOfProperty instanceof Timestamp && StringUtil.isNumeric(propertyValue)) {
            ((Timestamp) ObjectOfProperty).setTime(Long.valueOf(propertyValue));
        } else if (ObjectOfProperty instanceof Timestamp) {
            ObjectOfProperty = Timestamp.valueOf(propertyValue);
        } else if (ObjectOfProperty instanceof Integer) {
            ObjectOfProperty = Integer.valueOf(propertyValue);
        } else {
            ObjectOfProperty = propertyValue;
        }

        return doReadAsSingleCondition(context, readMethodOfProperty, ObjectOfProperty, operator);
    }

    /**
     * 
     * 
     * @param context                   数据集
     * @param readMethodOfProperty      getter方法
     * @param constValueOfProperty      表达式左值的实例对象
     * @param operator                  表达式的操作符
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<? extends Serializable> doReadAsSingleCondition(Map<String, ? extends Serializable> context,
                                                                        Method readMethodOfProperty,
                                                                        Object constValueOfProperty,
                                                                        String operator)
                                                                                        throws IllegalAccessException,
                                                                                        IllegalArgumentException,
                                                                                        InvocationTargetException {
        if (!(constValueOfProperty instanceof Comparable)) {
            throw new OwnedException(ExpressionErrorCode.NOT_INSTANCEOF_COMPARABLE);
        }

        List<Serializable> resutlSet = new ArrayList<Serializable>();

        for (Serializable bean : context.values()) {
            int diff = ((Comparable) readMethodOfProperty.invoke(bean))
                .compareTo((Comparable) constValueOfProperty);

            if (isMatched(diff, operator)) {
                resutlSet.add(bean);
            }
        }

        return resutlSet;
    }

    /**
     * compareTo返回值是否与操作符语义一致
     * 
     * @param diff
     * @param operator
     * @return
     */
    private static boolean isMatched(int diff, String operator) {
        return (diff > 0 && StringUtil.equals(operator, BIGGER))
               || (diff == 0 && StringUtil.equals(operator, EQUAL))
               || (diff < 0 && StringUtil.equals(operator, SMALLER));
    }
}
