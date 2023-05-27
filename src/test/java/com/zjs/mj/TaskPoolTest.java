package com.zjs.mj;

import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.entity.MjRequestResult;
import com.zjs.mj.entity.SubmitResult;
import com.zjs.mj.entity.Task;
import com.zjs.mj.service.MjService;
import com.zjs.mj.util.TaskPool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class TaskPoolTest {
    @Autowired
    TaskPool taskPool;

    @Autowired
    MjService mjService;

    @Test
    public void test() throws InterruptedException {
//        for (int i = 0; i < 40; i++) {
//            Task task = new Task(i,ImagineMode.FAST, Action.IMAGINE, "a cat ,a horse");
//            SubmitResult submitResult = taskPool.submitTask(task);
//            System.out.println(submitResult);
//        }
//        System.out.println("end");
//        TimeUnit.HOURS.sleep(1);
    }
}
