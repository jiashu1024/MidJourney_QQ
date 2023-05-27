package com.zjs.mj.service;

import com.zjs.mj.entity.Task;
import com.zjs.mj.mapper.TaskMapper;
import kotlin.coroutines.Continuation;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;

    public void saveTask(Task task) {
        taskMapper.insert(task);
    }

}
