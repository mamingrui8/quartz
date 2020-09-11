package org.quartz.mamr;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author mamr
 * @date 2020/9/1 4:56 下午
 */
public class BasicInitialTest {
    public static void main(String[] args){

        try {
            // 创建一个Scheduler工厂
            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            // 创建一个Scheduler api对象  它仅仅是一个与调度器沟通的api
            Scheduler scheduler = stdSchedulerFactory.getScheduler();
            // 启动调度器
            scheduler.start();

            // 存储一些在任务运行时，希望赋给任务的信息
            // 定义存储数据时，一定要注意是否能序列化、后续反序列化时会不会出现版本转换的问题。(比如LocalDateTime)
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("id", "00001");
            jobDataMap.put("executeTime", LocalDateTime.now());

            // 定义作业详情
            JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                    .withIdentity("HelloJob", "HelloJobGroup")
                    // 向JobExecutionContext传参的方式1: 使用JobDataMap  必须存放可以被序列化的对象
                    .setJobData(jobDataMap)
                    .usingJobData("jobUrl", "http://127.0.0.1")
                    .usingJobData("jobTag", "V-1.0.0")
                    .build();

            // 定义触发规则
            // 触发规则可以在定义阶段绑定其它作业
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("HelloTrigger", "HelloTriggerGroup")
                    .startNow()
                    // 触发器同样可以定义自己的jobDataMap
                    .usingJobData(new JobDataMap())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
                    .build();

            // 让Quartz使用我们的触发规则来调度作业
            scheduler.scheduleJob(jobDetail, trigger);

            // 停用调度器 调度器一旦关闭便无法重新启动，除非重新实例化一个Scheduler api对象
            // 如果调度器关掉了，作业还能定时执行吗？答案是不能，调度器停止触发后，Quartz会将调度器绑定的资源清空
            TimeUnit.SECONDS.sleep(10);
            System.out.println("停用调度器");
            scheduler.shutdown();
            System.out.println("主线程不退出");
            TimeUnit.SECONDS.sleep(100);
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
