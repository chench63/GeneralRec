/**
 * Tongji Edu.
 * Copyright (c) 2004-2013 All Rights Reserved.
 */
package edu.tongji.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import edu.tongji.configure.ConfigurationConstant;
import edu.tongji.log4j.LoggerDefineConstant;
import edu.tongji.thread.NetflixEvaPredctFileReader;
import edu.tongji.util.FileUtil;
import edu.tongji.util.LoggerUtil;
import edu.tongji.vo.SimilarityVO;

/**
 * 泛型数据缓存，通过CacheHolder与具体应用场景数据类型解耦。
 * 
 * @author chench
 * @version $Id: GeneralCache.java, v 0.1 2013-10-31 下午4:32:59 chench Exp $
 */
public final class GeneralCache {

	/** 读写锁 */
	private static final ReadWriteLock lock = new ReentrantReadWriteLock();

	/** logger */
	protected final static Logger logger = Logger
			.getLogger(LoggerDefineConstant.SERVICE_CACHE);

	/** 构建二维，数值缓存 */
	private final static List<List<Number>> numericCache = new ArrayList<List<Number>>(
			ConfigurationConstant.TASK_SIZE + 1);

	/** 辅助缓存 */
	private final static List<NumberSeq> auxiliaryCache = new ArrayList<NumberSeq>(
			ConfigurationConstant.AUXILIARY_MEM_SIZE);

	/** 任务缓存 */
	protected final static List<CacheTask> tasks = new ArrayList<CacheTask>();

	/**
	 * 获取任务
	 * 
	 * @return
	 */
	public static synchronized CacheTask task() {
		if (tasks.isEmpty()) {
			LoggerUtil.info(logger, "CacheTask  Completes.....");
			return null;
		} else {
			CacheTask task = tasks.remove(0);
			LoggerUtil
					.info(logger, "Release Task: " + task.get(CacheTask.FILE));
			return task;
		}
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public static void store(CacheTask task) {
		tasks.add(task);
	}

	/**
	 * 插入数值
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @param num
	 *            数值
	 */
	public static void put(int x, int y, Number num) {
		// 读写保护，加写锁
		Lock writeLock = lock.writeLock();
		writeLock.lock();

		try {
			// 首次使用初始化
			if (numericCache.isEmpty()) {
				for (int i = 0; i <= ConfigurationConstant.TASK_SIZE; i++) {
					numericCache.add(new ArrayList<Number>(1));
				}
			}

			List<Number> content = numericCache.get(x);
			// 首次接触该x对应的List，
			// 节约内存，对角初始化
			// x列，包含x行
			if (content.isEmpty()) {
				((ArrayList<Number>) content).ensureCapacity(x);
				for (int i = 0; i < x; i++) {
					content.add(0L);
				}
			}
			content.set(y, num);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 返回数值
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @return
	 */
	public static Number get(int x, int y) {
		// 主缓存未初始化，转化为智能模式
		if (ConfigurationConstant.ENABLE_ECONOMICAL_CACHE) {
			return smartget(x, y);
		}

		// 异常处理
		if (x > numericCache.size() | y > numericCache.get(x).size()) {
			return null;
		}
		return numericCache.get(x).get(y);

	}

	/**
	 * 返回数值 <br/>
	 * <br/>
	 * 
	 * <pre>
	 * 内存换页状态图：
	 *                                   
	 * <b>START</b>  --------->    Main.M.Get   --------(1)------->     <b>RETURN</b><br>
	 *                                |                                       ^
	 *                                |                                       |
	 *                                |(0)                                    |*
	 *                                |                                       |
	 *                                |                                       |
	 *                                V                                       |
	 *                  Main.M.Exist free Space  --------(1)------->   Insert into Main.M
	 *                                |                                       ^
	 *                                |                                       |
	 *                                |(0)                                    |*
	 *                                |                                       |
	 *                                |                                       |                              
	 *                        Frequency > MIN   --------(1)-------> release memory whose frequency < MIN
	 *                                |                        
	 *                                |                        
	 *                                |                        
	 *                                |                        
	 *                                |                        
	 *                        Auxiliary.M.Get   --------(1)------->     <b>RETURN</b><br>   
	 *                                |                                       ^
	 *                                |                                       |                               
	 *                                |(0)                                    |                       
	 *                                |                                       |                        
	 *                                |                                       |                     
	 *                  read from file, and load to A.M    --------------------
	 * </pre>
	 * 
	 * @param x
	 *            横坐标
	 * @param y
	 *            纵坐标
	 * @return
	 */
	public static Number smartget(int x, int y) {
		// 读写保护，加写锁
		Lock writeLock = lock.writeLock();
		writeLock.lock();

		try {
			// 首次使用初始化
			if (numericCache.isEmpty()) {
				// 初始化主内存
				for (int i = 0; i <= ConfigurationConstant.TASK_SIZE; i++) {
					numericCache.add(new ArrayList<Number>(1));
				}

				// 初始化工具类
				FrequencyUtil.initialize();
			}

			// 1. 尝试从主缓存获取数据
			List<Number> xArr = FrequencyUtil.get(x);
			if (!xArr.isEmpty()) {
				return xArr.get(y);
			}

			if (xArr.isEmpty() & !FrequencyUtil.existsFreeSpaceInMainMem()
					& !FrequencyUtil.existsFreeSpaceInAuxiliary()
					& FrequencyUtil.isHighFreq(x)) {
				// 一级缓存不足，尝试释放低频缓存
				FrequencyUtil.releaseMainMemLessThanMin();
			}
			if (xArr.isEmpty() & FrequencyUtil.existsFreeSpaceInMainMem()) {
				// 未获得一级缓存，且一级缓存有空间，载入缓存
				FrequencyUtil.insetIntoMainMem(x);
				return FrequencyUtil.get(x).get(y);
			}

			// 2. 尝试从辅助缓存读取数据
			int index = auxiliaryCache.indexOf(new NumberSeq(x));
			if (index == -1) {
				// 载入二级缓存，默认在列表头部
				FrequencyUtil.insertIntoAuxiliaryMem(x);
				index = 0;
			}
			return FrequencyUtil.get(x).get(y);

		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 内存访问频次管理工具
	 * 
	 * @author chench
	 * @version $Id: GeneralCache.java, v 0.1 26 Apr 2014 16:28:40 chench Exp $
	 */
	protected static class FrequencyUtil {
		/** 频数列表, [0, pInMainMem] 按频次从大到小 */
		private final static List<Frequency> freArrInMain = new ArrayList<Frequency>(
				ConfigurationConstant.TASK_SIZE + 1);

		/** id -> position 映射列表 */
		private final static List<Frequency> freArrGlobal = new ArrayList<Frequency>(
				ConfigurationConstant.TASK_SIZE + 1);

		/** 可用容量 */
		private static float availMainMem = ConfigurationConstant.NUMERIC_CACHE_LIMIT_GB;

		/** 当前最低引用次数 */
		private static int min_freqncy = 1;

		/**
		 * 初始化
		 */
		public static void initialize() {
			// 首次使用
			if (freArrGlobal.isEmpty()) {
				// 初始化
				for (int i = 0; i < ConfigurationConstant.TASK_SIZE + 1; i++) {
					// 第i个存放id为i的引用频次信息
					freArrGlobal.add(new Frequency(i, 0));
				}
			}
		}

		/**
		 * 查询主缓存是否还是空余空间
		 * 
		 * @return
		 */
		public static boolean existsFreeSpaceInMainMem() {
			return availMainMem > 0.0f;
		}

		/**
		 * 查询辅助缓存是否还是空余空间
		 * 
		 * @return
		 */
		public static boolean existsFreeSpaceInAuxiliary() {
			return auxiliaryCache.size() < ConfigurationConstant.AUXILIARY_MEM_SIZE;
		}

		/**
		 * 是否是高频次的内存块
		 * 
		 * @param id
		 * @return
		 */
		public static boolean isHighFreq(int id) {
			return freArrGlobal.get(id).getFreqncy() > min_freqncy;
		}

		/**
		 * 释放低频次内存块
		 */
		public static void releaseMainMemLessThanMin() {
			// 主缓存为空，不释放
			if (freArrInMain.isEmpty()) {
				return;
			}

			// 搜索需要释放内存区域
			Collections.sort(freArrInMain, new Comparator<Frequency>() {
				@Override
				public int compare(Frequency o1, Frequency o2) {
					return o1.compareTo(o2);
				}

			});

			// 释放内存
			StringBuilder logMsg = new StringBuilder("Release Main Mem: ");
			for (int pStart = ((Double) (freArrInMain.size() * 0.95))
					.intValue(), pEnd = freArrInMain.size() - 1; pStart <= pEnd; pEnd--) {
				Frequency freqncy = freArrInMain.remove(pEnd);
				int x = freqncy.getId();

				// 更新可用内存容量
				availMainMem += 4 * numericCache.get(x).size();
				numericCache.get(x).clear();

				// 记录日志
				logMsg.append(FileUtil.BREAK_LINE).append("\t").append(x);
			}
			// 写入日志
			LoggerUtil.info(logger, logMsg);

			// 更新最小引用次数
			min_freqncy = freArrInMain.get(freArrInMain.size() - 1)
					.getFreqncy();
		}

		/**
		 * 插入缓存
		 * 
		 * @param id
		 */
		public static void insetIntoMainMem(int id) {

			// 1. 载入内存
			List<Number> xArr = numericCache.get(id);
			List<SimilarityVO> content = new ArrayList<SimilarityVO>();
			String fileName = NetflixEvaPredctFileReader.loadSimilarityOutter(
					id, content);
			loadSingleArr(xArr, content, id);

			// 2. 更新可用内存容量
			availMainMem -= 4 * content.size();

			// 3. 按引用传递，保持同步
			freArrInMain.add(freArrGlobal.get(id));

			// 3. 输出日志
			LoggerUtil.debug(logger, "M.M       File: " + fileName);

		}

		/**
		 * 加载至辅助缓存
		 * 
		 * @param id
		 */
		public static void insertIntoAuxiliaryMem(int id) {
			// 1. 读取文件数据
			List<Number> xArr = numericCache.get(id);
			List<SimilarityVO> content = new ArrayList<SimilarityVO>();
			String fileName = NetflixEvaPredctFileReader.loadSimilarityOutter(
					id, content);
			loadSingleArr(xArr, content, id);

			// 2. 载入辅助缓存
			if (auxiliaryCache.size() < ConfigurationConstant.AUXILIARY_MEM_SIZE) {
				// 辅助缓存未满
				auxiliaryCache.add(new NumberSeq(id, xArr));
			} else {
				// 辅助缓存已满，头部插入，尾部剔除
				auxiliaryCache
						.remove(ConfigurationConstant.AUXILIARY_MEM_SIZE - 1);
				auxiliaryCache.add(0, new NumberSeq(id, xArr));
			}

			// 3. 输出日志
			LoggerUtil.debug(logger, "A.M       File: " + fileName);

		}

		/**
		 * 缓存中获得实例
		 * 
		 * @param id
		 * @return
		 */
		public static List<Number> get(int id) {

			// 1. 尝试从主缓存与辅助缓存获取数据
			List<Number> xArr = numericCache.get(id);
			int index = auxiliaryCache.indexOf(new NumberSeq(id));
			if (xArr.isEmpty() && index == -1) {
				// 缓存中不存在
				return xArr;
			} else if (index != -1) {
				xArr = auxiliaryCache.get(index).getNumArr();
			}
			// 缓存中存在，更新频次计数
			freArrGlobal.get(id).once();
			return xArr;
		}

		/**
		 * 按顺序Copy数值
		 * 
		 * @param xArr
		 * @param content
		 * @param x
		 */
		protected static void loadSingleArr(List<Number> xArr,
				List<SimilarityVO> content, int x) {
			xArr.clear();
			((ArrayList<Number>) xArr).ensureCapacity(x);
			for (int i = 0; i < x; i++) {
				xArr.add(0L);
			}

			for (SimilarityVO simlrty : content) {
				xArr.set(simlrty.getItemJ(), simlrty.getSimilarity());
			}

		}

	}

}

/**
 * 访问频数
 * 
 * @author chench
 * @version $Id: GeneralCache.java, v 0.1 26 Apr 2014 16:49:22 chench Exp $
 */
class Frequency implements Comparable<Frequency> {
	/** id */
	private int id;

	/** 内存访问次数 */
	private int freqncy;

	/**
	 * @param id
	 *            id
	 * @param freqncy
	 *            内存访问次数
	 */
	public Frequency(int id, int freqncy) {
		this.id = id;
		this.freqncy = freqncy;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Frequency o) {
		if (this.freqncy == o.freqncy)
			return 0;

		return (this.freqncy - o.freqncy) > 0 ? 1 : -1;
	}

	/**
	 * 使用一次
	 */
	public void once() {
		this.freqncy++;
	}

	/**
	 * Getter method for property <tt>id</tt>.
	 * 
	 * @return property value of id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter method for property <tt>id</tt>.
	 * 
	 * @param id
	 *            value to be assigned to property id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter method for property <tt>freqncy</tt>.
	 * 
	 * @return property value of freqncy
	 */
	public int getFreqncy() {
		return freqncy;
	}

	/**
	 * Setter method for property <tt>freqncy</tt>.
	 * 
	 * @param freqncy
	 *            value to be assigned to property freqncy
	 */
	public void setFreqncy(int freqncy) {
		this.freqncy = freqncy;
	}

}

/**
 * 数值序列内部类
 * 
 * @author chench
 * @version $Id: GeneralCache.java, v 0.1 26 Apr 2014 21:04:34 chench Exp $
 */
class NumberSeq {

	/** x序列值 */
	private int x;

	/** 数值列表s */
	private List<Number> numArr;

	/**
	 * @param x
	 */
	public NumberSeq(int x) {
		super();
		this.x = x;
	}

	/**
	 * @param x
	 * @param numArr
	 */
	public NumberSeq(int x, List<Number> numArr) {
		super();
		this.x = x;
		this.numArr = numArr;
	}

	public Number get(int y) {
		return numArr.get(y);
	}

	/**
	 * Getter method for property <tt>x</tt>.
	 * 
	 * @return property value of x
	 */
	public int getX() {
		return x;
	}

	/**
	 * Setter method for property <tt>x</tt>.
	 * 
	 * @param x
	 *            value to be assigned to property x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Getter method for property <tt>numArr</tt>.
	 * 
	 * @return property value of numArr
	 */
	public List<Number> getNumArr() {
		return numArr;
	}

	/**
	 * Setter method for property <tt>numArr</tt>.
	 * 
	 * @param numArr
	 *            value to be assigned to property numArr
	 */
	public void setNumArr(List<Number> numArr) {
		this.numArr = numArr;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NumberSeq) {
			return ((NumberSeq) obj).getX() == this.x;
		}

		return false;
	}
}
