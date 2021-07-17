package com.imooc.config;

import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    /**
     * 使用定时任务关闭超期未支付的订单，会存在的弊端：
     * 1.会有时间差，程序不严谨
     *      10:39下单，11:00检查不足一小时，12:00检查，超过了1小时39分钟
     * 2.不支持集群
     *      单机没毛病，使用集群之后，就会有多个定时任务
     *      解决方案：只使用一台计算机节点，单独用来运行所有的定时任务
     * 3.会对数据库全表搜索，极其影响数据库性能：select * from order where orderStatus = 10;
     *  定时任务只适用于小型轻量级项目，传统项目
     *
     *
     *  后续课程会设计到消息队列：MQ -> RabbitMQ, RocketMQ, Kafka, ZeroMQ...
     *      延时任务(队列)
     *      10:12分下单，未付款(10)状态，11:12分检查，如果当前状态还是10，则直接关闭订单即可。
     */

    /**
     * second(秒) ,minute(分) ,hour(时) ,day of month (日),month(月),day of week(周几)
     * 0 * * * * MON-FRI
     * 【0 0/5 14,18 * * ?】 每天14点整，和18点整，每隔5分钟执行一次
     * 【0 15 10 ? * 1-6】 每个与的周一到周六的10点15分执行一次
     * 【0 0 2 ? * 6L】    最月的最后一个周六2点执行一次
     * 【0 0 2 LW * ?】   每月的最后一个工作日凌晨2点执行一次
     * 【0 0 2-4 ? * 1#1】 每月的第一个周一凌晨2点到4点期间，每个整点执行一次
     */
    //@Scheduled(cron = "0 * * * * MON-FRI")
    //@Scheduled(cron = "0,1,2,3,4,5 * * * * MON-FRI")
    //@Scheduled(cron = "0-4 * * * * MON-FRI")
    //@Scheduled(cron = "0/4 * * * * MON-FRI") //每4秒执行一次
//    @Scheduled(cron = "0/3 * * * * ?")
//    @Scheduled(cron = "0 0 0/1 * * ?")
        public void autoCloseOrder(){
        orderService.closeOrder();
        System.out.println("执行定时任务，当前时间为："+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
    }




}
