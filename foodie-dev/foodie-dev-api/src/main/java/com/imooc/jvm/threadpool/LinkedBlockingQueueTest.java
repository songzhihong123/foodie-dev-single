package com.imooc.jvm.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author szh
 */
public class LinkedBlockingQueueTest {

    public static void main(String[] args) {
        LinkedBlockingDeque<Object> queue = new LinkedBlockingDeque<>(1);
        queue.add("abc");
        boolean def = queue.offer("def");
        System.out.println(def);
        queue.add("g");



    }


}
