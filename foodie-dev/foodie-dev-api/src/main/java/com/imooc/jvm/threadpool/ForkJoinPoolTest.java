package com.imooc.jvm.threadpool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolTest {



    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 用ForkJoinPool实现 1 - 100 求和
        ForkJoinPool pool = new ForkJoinPool();
        ForkJoinTask<Integer> task = pool.submit(new MyTask(1, 100));
        System.out.println(task.get());


    }


}
    class MyTask extends RecursiveTask<Integer>{

        // 当前任务计算的起始
        private int start;
        //当前任务计算的结束
        private int end;
        // 阈值 ， 如果 end - start 在阈值以内，那么就不用再去细分任务
        private static final int threshold = 2;

        public MyTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            int sum = 0;
            boolean needFork = (end - start ) > threshold;
            if(needFork){
                int middle = (start + end) / 2;
                MyTask leftTask = new MyTask(start, middle);
                MyTask rightTask = new MyTask(middle + 1, end);

                leftTask.fork();
                rightTask.fork();

                // 子任务执行完成之后的结果
                Integer leftResult = leftTask.join();
                Integer rightResult = rightTask.join();

                sum += leftResult + rightResult;

            }else{
                for (int i = start; i <= end; i++) {
                    sum += i;
                }
            }
            return sum;
        }
    }



