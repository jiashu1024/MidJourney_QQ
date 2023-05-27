package com.zjs.mj.service;

import com.zjs.mj.entity.Task;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static UserMapper userMapper;

    private final UserMapper mapper;

    @PostConstruct
    public void init() {
        userMapper = mapper;
    }


    public static void decreaseCount(Task task) {
        if (task.getStatus().equals(TaskStatus.SUCCESS)) {
            String qq = task.getUser().getQq();
            User user = userMapper.selectById(qq);
            if (task.getMode() == ImagineMode.FAST) {
                user.setFastCount(user.getFastCount() - 1);
            } else {
                user.setRelaxCount(user.getRelaxCount() - 1);
            }
            userMapper.updateById(user);
            log.info("user[{}] : " + task.getMode() + "count -1", qq);
        }
    }
}
