/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.smartgrid.support;

import edu.tongji.util.CrawlerUtil;
import edu.tongji.util.support.DateSeqUrlGen;

/**
 * 
 * @author chench
 * @version $Id: WeatherCacheFileCrawler.java, v 0.1 2014-5-26 下午12:02:44 chench Exp $
 */
public final class WeatherCacheFileCrawler {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        DateSeqUrlGen gen = new DateSeqUrlGen();
        gen.setHead("http://www.wunderground.com/history/airport/KBOS");
        gen.setTail("DailyHistory.html?req_city=boston&req_state=MA&req_statename=Massachusetts&format=1");
        gen.setStart("20110601");
        gen.setEnd("20110701");

        CrawlerUtil.crawl(gen);
    }

}
