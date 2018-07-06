package com.example.matao.weatherforecast.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池工具类
 * Created by matao on 2018/6/29.
 */

public class ThreadPoolUtil {

    //私有化固定大小的线程池
    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(64);


    /**
     * 执行线程任务
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }


    /**
     * 关闭线程池
     */
    public static void shutdown() {
        if(!fixedThreadPool.isShutdown()){
            fixedThreadPool.shutdown();
        }
    }


}
