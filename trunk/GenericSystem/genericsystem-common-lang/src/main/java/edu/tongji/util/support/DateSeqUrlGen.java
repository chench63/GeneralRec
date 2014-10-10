/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.util.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.tongji.util.DateUtil;
import edu.tongji.util.ExceptionUtil;
import edu.tongji.util.FileUtil;
import edu.tongji.util.StringUtil;

/**
 * 按日期为索引，构造页面 <br/>
 * wunderground.com
 * 
 * @author Hanke Chen
 * @version $Id: DateSeqUrlGen.java, v 0.1 2014-5-26 上午10:42:55 chench Exp $
 */
public class DateSeqUrlGen implements UrlGen {

    /** 开始时间*/
    private String             start;

    /** 结束时间*/
    private String             end;

    /** Url 前缀*/
    private String             head;

    /** Url 后缀*/
    private String             tail;

    /** 本地缓存*/
    private final List<String> urlArr = new ArrayList<String>();

    /** 
     * @see edu.tongji.util.support.UrlGen#iterator()
     */
    @Override
    public Iterator<String> iterator() {

        try {
            Date begin = DateUtil.parse(start, DateUtil.SHORT_FORMAT);
            Date expire = DateUtil.parse(end, DateUtil.SHORT_FORMAT);

            while (begin.before(expire)) {

                String date = DateUtil.format(begin, DateUtil.SHORT_FORMAT);
                String year = date.substring(0, 4);
                String month = date.substring(4, 6);
                String day = date.substring(6, 8);

                //拼接Url
                String url = (new StringBuilder(head)).append('/').append(year).append('/')
                    .append(month).append('/').append(day).append('/').append(tail).toString();
                urlArr.add(url);

                //递进到后一天
                begin = new Date(begin.getTime() + 24 * 60 * 60 * 1000);
            }

            return urlArr.iterator();

        } catch (ParseException e) {
            ExceptionUtil.caught(e, "Parse Date Error!");
        }

        return null;
    }

    /** 
     * @see edu.tongji.util.support.UrlGen#regulate(java.io.InputStream)
     */
    @Override
    public StringBuilder regulate(InputStream inputStream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                if (StringUtil.isBlank(line) || line.charAt(0) < '0' || line.charAt(0) > '9') {
                    //过滤非数字的前几行
                    continue;
                }

                //剔除HTML，换行符
                int endIndex = line.lastIndexOf("<br />");

                content.append(FileUtil.BREAK_LINE).append(line.subSequence(0, endIndex));
            }
        } catch (IOException e) {
            ExceptionUtil.caught(e, "CrawlerUtil Crashed");
        }

        return content;
    }

    /**
     * Getter method for property <tt>head</tt>.
     * 
     * @return property value of head
     */
    public String getHead() {
        return head;
    }

    /**
     * Setter method for property <tt>head</tt>.
     * 
     * @param head value to be assigned to property head
     */
    public void setHead(String head) {
        this.head = head;
    }

    /**
     * Getter method for property <tt>tail</tt>.
     * 
     * @return property value of tail
     */
    public String getTail() {
        return tail;
    }

    /**
     * Setter method for property <tt>tail</tt>.
     * 
     * @param tail value to be assigned to property tail
     */
    public void setTail(String tail) {
        this.tail = tail;
    }

    /**
     * Getter method for property <tt>start</tt>.
     * 
     * @return property value of start
     */
    public String getStart() {
        return start;
    }

    /**
     * Setter method for property <tt>start</tt>.
     * 
     * @param start value to be assigned to property start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * Getter method for property <tt>end</tt>.
     * 
     * @return property value of end
     */
    public String getEnd() {
        return end;
    }

    /**
     * Setter method for property <tt>end</tt>.
     * 
     * @param end value to be assigned to property end
     */
    public void setEnd(String end) {
        this.end = end;
    }

}
