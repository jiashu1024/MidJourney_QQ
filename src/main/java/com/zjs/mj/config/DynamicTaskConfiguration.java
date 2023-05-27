package com.zjs.mj.config;

import com.zjs.mj.schedulings.SchedulingTask;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
public class DynamicTaskConfiguration implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

    }
//
//    private final SchedulingTask schedulingTask;
//    private final DynamicTaskConfiguration taskConfiguration;
//
//    private CronTask cronTask;
//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        //schedulingTask = new SchedulingTask()
//       cronTask = new CronTask(schedulingTask.queryRecentJobs(), );
//        taskRegistrar.addCronTask(cronTask);
//    }
}
