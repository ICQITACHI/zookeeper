package com.jin;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

import java.util.List;

/**
 * 高山仰之可及，深渊度之可测
 */
public class TestCurator {

    @Test
    public  void test1() throws Exception{

        //1.创建重试策略（重新连接zookeeper的策略）
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,2);

        //2.CuratorFramework
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString("192.168.168.10:2181")
                .sessionTimeoutMs(9000)
                .retryPolicy(retryPolicy)
                .build();

        //3.开始连接（启动客户端）
        cf.start();

        //4.操作(create read write)

        //EPHEMERAL  临时节点
        //cf.create()  不使用writeMode指定类型，则默认为永久节点

       /* Object o1 = cf.create().forPath("/ww","测试".getBytes());
        System.out.println(o1);
        cf.close();*/
        Object o = cf.create().withMode(CreateMode.EPHEMERAL).forPath("/q2","测试临时节点".getBytes());

       // Object o1 =   cf.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/q3","测试永久节点".getBytes());
        //创建多级目录
       // Object o2 =   cf.create().creatingParentsIfNeeded().forPath("/q4/qq4","测试".getBytes());

       // System.out.println(o+"--"+o1+"---"+o2);

        //获取节点的值
      byte b[] = cf.getData().forPath("/q2");
        //byte[]---->String
        System.out.println(new String(b));


        //修改节点的值
        cf.setData().forPath("/q2","修改后的临时节点".getBytes());
         b = cf.getData().forPath("/q2");
        //byte[]---->String
        System.out.println(new String(b));

         //得到子目录
        List list = cf.getChildren().forPath("/q4");
        for (Object o1 : list) {
            System.out.println(o1);
        }

        //删除
        cf.delete().forPath("/q2");

        cf.delete().deletingChildrenIfNeeded().forPath("/q4");
        //5,关闭
        cf.close();






    }




    @Test
    public void teat() throws Exception{
        //1.创建重试策略（重新连接zookeeper的策略）
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(6000,2);

        //2.CuratorFramework
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString("192.168.168.10:2181")
                .sessionTimeoutMs(9000)
                .retryPolicy(retryPolicy)
                .build();

        //3.开始连接（启动客户端）
        cf.start();

        //缓存节点
        final NodeCache nodeCache = new NodeCache(cf,"/qfjava000");
        nodeCache.start(true);

        //设置回调函数      节点的值发送改变时执行
        nodeCache.getListenable().addListener(new NodeCacheListener() {

            public void nodeChanged() throws Exception {
                System.out.println("---->节点变了！！！");
                ChildData data =  nodeCache.getCurrentData();

                System.out.println(new String(data.getData())+"---"+nodeCache.getPath());

            }
        });

        cf.create().forPath("/qfjava000","测试".getBytes());

        Thread.sleep(1000);
        cf.setData().forPath("/qfjava000","修改".getBytes());

        Thread.sleep(2000);

        cf.close();
    }
}
