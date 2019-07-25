package com.jin.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * 高山仰之可及，深渊度之可测
 */

public class TestCuratorLock {

    @Test
    public void testLock(){

        //1.创建重试策略
                                                              //等待（睡眠）时间       重置次数
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(4000,2);

        //2.通过工厂对象 构建CuratorFramework对象
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString("192.168.168.10:2182")
                .retryPolicy(retryPolicy).build();

        //3.开启
        cf.start();

        //4.分布式锁部分

        //使用线程池管理线程
        ExecutorService executorService = Executors.newCachedThreadPool();

        InterProcessMutex lock = new InterProcessMutex(cf,"/mylock");

        Consumer<InterProcessMutex> consumer = new Consumer<InterProcessMutex>() {
            @Override
            public void accept(InterProcessMutex interProcessMutex) {
                //创建线程集合
                List<Callable<String>> callables = new ArrayList<>();
                Callable<String> callable = new Callable<String>() {
                    @Override
                    public String call() throws Exception {

                        try {
                            //获得锁
                            interProcessMutex.acquire();
                            System.out.println(Thread.currentThread().getName()+"----->获得锁");

                            //各种业务逻辑
                            //比如抢票
                            //比如秒杀

                            System.out.println("秒杀业务开始");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            interProcessMutex.release();
                            System.out.println(Thread.currentThread().getName()+"====>释放锁");

                        }


                        return "true";
                    }
                };

                //创建多个线程
                for (int i = 0; i < 20; i++) {
                    callables.add(callable);
                }

                try {
                    //唤醒所有线程
                    executorService.invokeAll(callables);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        consumer.accept(lock);

        //释放资源
        executorService.shutdown();
        cf.close();

        //思考  一个应用场景


    }


}
