package com.juc.demo2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
/*
* 一、线程池：提供了一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁额外开销，提高了响应的速度。
*
* 二、线程池的体系结构：
*     java.util.concurrent.Executor : 负责线程的使用与调度的根接口
*         |--ExecutorService 子接口: 线程池的主要接口
*             |--ThreadPoolExecutor 线程池的实现类
*             |--ScheduledExecutorService 子接口：负责线程的调度
*                 |--ScheduledThreadPoolExecutor ：继承 ThreadPoolExecutor， 实现 ScheduledExecutorService
*
* 三、工具类 : Executors
* ExecutorService newFixedThreadPool() : 创建固定大小的线程池
* ExecutorService newCachedThreadPool() : 缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量。
* ExecutorService newSingleThreadExecutor() : 创建单个线程池。线程池中只有一个线程
*
* ScheduledExecutorService newScheduledThreadPool() : 创建固定大小的线程，可以延迟或定时的执行任务。
*/
public class ThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //创建线程池 5个
        ExecutorService pool = Executors.newFixedThreadPool(5);
        //创建一个数组储存
        List<Future<String>> list = new ArrayList<>();

        //为线程池中的线程分配任务
        for(int i = 0; i < 20; i++){
            Future<String> future = pool.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    int sum = 0;
                    //计算0-100的和
                    for (int i = 0; i <= 100; i++) {
                        sum += i;
                    }
                    //返回线程名+结果
                    return Thread.currentThread().getName()+" : "+sum;
                }
            });
            //将返回值添加到集合中
            list.add(future);
        }
        
        //遍历集合
        for (Future<String> future : list) {
            System.out.println(future.get());
        }

        //关闭线程池
        pool.shutdown();
    }
}