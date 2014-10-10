/**
 * Tongji Edu.
 * Copyright (c) 2004-2014 All Rights Reserved.
 */
package edu.tongji.experiment.recommendation.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.parser.ParserTemplate;
import edu.tongji.parser.TemplateType;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author Hanke Chen
 * @version $Id: MoiveSeqSupport.java, v 0.1 2014-5-1 上午10:38:03 chench Exp $
 */
public class MoiveSeqSupport {

    /** 文件路径*/
    protected static final String SOURCE_DIR = "E:/Proposed/Rating/";

    /** 文件路径*/
    protected static final String TARGET_DIR = "E:/Proposed/RatingSeqMovie/mv_";

    /** logger */
    private static final Logger   logger     = Logger.getLogger(LoggerDefineConstant.SERVICE_TEST);

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        File dir = new File(SOURCE_DIR);
        if (!dir.isDirectory()) {
            LoggerUtil.warn(logger, dir.getAbsolutePath() + "is not a directory");
            return;
        }

        File[] files = dir.listFiles();
        //MovieId [1, 17770]
        List<List<RatingVO>> movieSeqArr = new ArrayList<List<RatingVO>>(17771);
        for (int i = 0; i < 17771; i++) {
            movieSeqArr.add(new ArrayList<RatingVO>());
        }

        //Tabulate order by MovieId
        LoggerUtil.info(logger, "Tabulate Starts.");
        for (File file : files) {
            String[] contents = FileUtil.readLines(file.getAbsolutePath());

            RatingVO rating = null;
            for (int i = 0; i < contents.length; i++) {
                ParserTemplate template = new ParserTemplate();
                template.setTemplate(contents[i]);

                rating = (RatingVO) TemplateType.NETFLIX_RATINGVO_TEMPLATE.parser(template);
                List<RatingVO> movieSeq = movieSeqArr.get(rating.getMovieId());
                movieSeq.add(rating);
            }
        }
        LoggerUtil.info(logger, "Tabulate completes.");

        //Write files
        for (int movieId = 1; movieId < 17771; movieId++) {
            //1. 拼写文件名
            String fileName = (new StringBuilder(TARGET_DIR))
                .append(StringUtil.alignRight(String.valueOf(movieId), 7, FileUtil.ZERO_PAD_CHAR))
                .append(FileUtil.TXT_FILE_SUFFIX).toString();

            //2. 写入文件
            List<RatingVO> movieSeq = movieSeqArr.get(movieId);
            if (movieSeq.isEmpty()) {
                continue;
            }

            //Sort: ascend
            Collections.sort(movieSeq, new Comparator<RatingVO>() {
                @Override
                public int compare(RatingVO o1, RatingVO o2) {
                    if (o1.getUsrId() == o2.getUsrId()) {
                        return 0;
                    }
                    return o1.getUsrId() - o2.getUsrId() > 0 ? 1 : -1;
                }

            });
            StringBuilder content = new StringBuilder();
            for (RatingVO rating : movieSeq) {
                content.append(rating).append(FileUtil.BREAK_LINE);
            }
            FileUtil.write(fileName, content.toString());
        }
    }
}
