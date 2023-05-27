package com.zjs.mj.processors.qqProcessor;

import com.zjs.mj.entity.AuthenticationResult;
import com.zjs.mj.entity.Task;
import net.mamoe.mirai.event.Event;

public interface ChatProcessor {
    AuthenticationResult checkAuth(Event event, Task task);

    void process(Event event);


}
