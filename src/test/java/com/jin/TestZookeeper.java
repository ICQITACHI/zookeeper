package com.jin;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

/**
 * 高山仰之可及，深渊度之可测
 */
public class TestZookeeper {

    @Test
    public void create(){

        //1.创建客户端对象
        ZkClient zkClient = new ZkClient("192.168.168.10:2181");

        //2.操作

        //永久节点
//        zkClient.create("/java1901","best",CreateMode.PERSISTENT);
//        //永久有序节点
//        zkClient.createPersistentSequential("/qf01","qf");
        //临时
        zkClient.create("/qf2","临时节点", CreateMode.EPHEMERAL);
        //临时有序
        zkClient.create("/qf3","临时有序",CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(1111);
//        try {
//            Thread.sleep(100000);
//        }catch (Exception e){
//
//        }


        zkClient.close();//执行结束，临时节点会被删除

    }


    @Test
    public  void  test(){
        //1,创建客户端对象
        ZkClient zkClient = new ZkClient("192.168.82.88:2181");
        //2,操作
        zkClient.writeData("/java1903","很棒！");

        zkClient.close();
    }

    @Test
    public   void  read(){
        //1,创建客户端对象
        ZkClient zkClient = new ZkClient("192.168.82.88:2181");
        //2,操作
        Object o = zkClient.readData("/java1903");

        System.out.println(o);

        zkClient.close();

    }

    @Test
    public  void  testDelete(){
        //1,创建客户端对象
        ZkClient zkClient = new ZkClient("192.168.82.88:2181");

        //删除

        boolean f = zkClient.delete("/java1901");

        //zkClient.deleteRecursive("/java1901");

        System.out.println(f);
        zkClient.close();

    }
}
