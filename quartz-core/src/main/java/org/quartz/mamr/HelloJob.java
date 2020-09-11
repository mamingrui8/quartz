package org.quartz.mamr;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.quartz.*;
import org.quartz.mamr.util.JacksonUtils;

/**
 * @author mamr
 * @date 2020/9/1 5:20 下午
 */
public class HelloJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("我是HelloJob，我正在执行...");

        try {
            System.out.println("运行时环境 变量: " + JacksonUtils.mapToString(context.getMergedJobDataMap()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        JobKey key = context.getJobDetail().getKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    }
}
