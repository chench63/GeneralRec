/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.noise.Noise;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.util.StringUtil;
import edu.tongji.vo.RatingVO;

/**
 * 
 * @author chench
 * @version $Id: SimularityStreamCache.java, v 0.1 2013-10-12 下午2:53:34 chench Exp $
 */
public final class SimilarityStreamCache extends Observable {

    /** 读写锁 */
    private static final ReadWriteLock        lock           = new ReentrantReadWriteLock();

    /** logger */
    private final static Logger               logger         = Logger
                                                                 .getLogger(LoggerDefineConstant.SERVICE_CACHE);
    /** 本地数据缓存*/
    private final static List<List<RatingVO>> ratingContext  = new ArrayList<List<RatingVO>>(17770);

    /** 运行时间*/
    private final static CacheStopWatch       catchStopWatch = new CacheStopWatch();

    protected static SimilarityStreamCache    singletone     = new SimilarityStreamCache();

    /**
     * NetflixRatingRecorder任务
     * 
     * @return
     */
    public static synchronized CacheTask task() {
        CacheTask task = new CacheTask(CacheTask.I, 1, CacheTask.I);

        //判断任务是否结束
        if (CacheTask.I <= ConfigurationConstant.TASK_SIZE) {
            LoggerUtil.info(logger, "Release Task: " + task);
            CacheTask.I++;
            return task;
        } else if (CacheTask.I == (ConfigurationConstant.TASK_SIZE + ConfigurationConstant.THREAD_SIZE)
                   & !ratingContext.isEmpty()
                   & StringUtil.isNotBlank(ConfigurationConstant.USER_SEQ_FILE_PATH)) {
            //将数据转化为，按用户分类的文件
            singletone.tabulateSeqUserInner();
        }

        //更新任务累计量
        LoggerUtil.info(logger, "CacheTask  Completes.....");
        CacheTask.I++;
        return null;

    }

    /**
     * 累计计算运行时间
     * 
     * @param laps
     */
    public static synchronized void update(CacheHolder cacheHolder) {
        catchStopWatch.put(cacheHolder);
    }

    /**
     * 序列化插入，快速添加评分记录
     * 
     * @param movieId   
     * @param ratings
     */
    public static void put(int movieId, List<RatingVO> ratings) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            ratingContext.add(ratings);
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, (new StringBuilder("movieId ：")).append(movieId));
        }
    }

    /**
     * 添加新的评分记录,同时使用高斯噪声处理源数据，已保护用户隐私。
     * 
     * @param ratings
     */
    public static void putAndDisguise(int movieId, List<RatingVO> ratings, Noise noise) {
        //读写保护，加写锁
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            for (int i = 0, j = ratings.size(); i < j; i++) {
                RatingVO rating = ratings.get(i);

                //加入噪声
                rating.setRatingCmp((float) noise.perturb(rating.getRatingReal()));
            }

            ratingContext.add(ratings);
        } finally {
            writeLock.unlock();
            LoggerUtil.info(logger, (new StringBuilder("movieId ：")).append(movieId));
        }
    }

    /**
     * 此处直接得到List以后，其实后续的List的[读写操作]不是线程安全的。
     * 
     * @param movieId
     * @return
     */
    public static List<RatingVO> get(int movieId) {
        Lock readLock = lock.readLock();
        readLock.lock();

        try {
            //Movie_id [1, 17770]
            //对应的索引  [0, 17770-1]
            return ratingContext.get(movieId - 1);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 将数据转化为，按用户分类的文件
     */
    protected void tabulateSeqUserInner() {
        LoggerUtil.info(logger, "Tabulates prepare.");

        //使用Map加快查询速度,netflix总用户数40w, 20MBye
        //索引为User_Id
        Map<Integer, UserSeq> usrSeqCache = new HashMap<Integer, UserSeq>();
        for (int i = ratingContext.size() - 1; i >= 0; i--) {
            //节约内存，剔除原数据
            List<RatingVO> ratingRepo = ratingContext.remove(0);
            for (RatingVO rating : ratingRepo) {
                //1. 获得用户数组
                UserSeq userSeq = usrSeqCache.get(rating.getUsrId());
                if (userSeq == null) {
                    //没出现过该用户，新增一个UserSeq
                    userSeq = new UserSeq(rating.getUsrId(), new ArrayList<RatingVO>());
                    usrSeqCache.put(rating.getUsrId(), userSeq);
                }

                //2. 对应Rating,添加至用户数组
                userSeq.put(rating);
            }

            //确保释放子链内存
            ratingRepo.clear();
        }
        //确保全部释放
        ratingContext.clear();
        LoggerUtil.info(logger, "Tabulates writing file.");

        //输出至文件
        for (UserSeq userSeq : usrSeqCache.values()) {
            //1. 获取文件名 和 文件内容
            //文件名称   [ur_0000010.txt]
            StringBuilder fileName = (new StringBuilder(ConfigurationConstant.USER_SEQ_FILE_PATH))
                .append(
                    StringUtil.alignRight(String.valueOf(userSeq.userId), 10,
                        FileUtil.ZERO_PAD_CHAR)).append(FileUtil.TXT_FILE_SUFFIX);
            StringBuilder context = new StringBuilder();
            for (Iterator<RatingVO> iter = userSeq.iterator(); iter.hasNext();) {
                RatingVO rating = iter.next();
                context.append(rating.toString()).append(FileUtil.BREAK_LINE);
            }

            //输出至文件
            FileUtil.write(fileName.toString(), context.toString());
            LoggerUtil.info(
                logger,
                (new StringBuilder("FILE: ")).append(
                    StringUtil.alignRight(String.valueOf(userSeq.userId), 10,
                        FileUtil.ZERO_PAD_CHAR)).append(FileUtil.TXT_FILE_SUFFIX));
        }
        LoggerUtil.info(logger, "Tabulates Complete.");
    }

    protected class UserSeq {
        /** User_Id*/
        private final int      userId;

        /** Rating列表*/
        private List<RatingVO> ratingArr;

        /**
         * @param userId
         */
        public UserSeq(int userId) {
            this.userId = userId;
        }

        /**
         * @param userId
         * @param ratingArr
         */
        public UserSeq(int userId, List<RatingVO> ratingArr) {
            this.userId = userId;
            this.ratingArr = ratingArr;
        }

        /**
         * 插入Rating
         * 
         * @param rating
         */
        public void put(RatingVO rating) {
            ratingArr.add(rating);
        }

        /**
         * 迭代器
         * 
         * @return
         */
        public Iterator<RatingVO> iterator() {
            return ratingArr.iterator();
        }

        /** 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof UserSeq) {
                return ((UserSeq) obj).userId == this.userId;
            }
            return false;
        }

    }

}
