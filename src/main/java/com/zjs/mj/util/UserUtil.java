package com.zjs.mj.util;

import com.zjs.mj.config.Properties;
import com.zjs.mj.constant.UserRole;
import com.zjs.mj.entity.dto.DefaultRoleCount;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.mapper.DefaultRoleCountMapper;
import com.zjs.mj.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUtil {

    private final UserMapper userMapper;
    private final DefaultRoleCountMapper defaultRoleCountMapper;
    private final Properties properties;

    public  User getUser(String qq) {
        User user = userMapper.selectById(qq);
        if (user == null) {
            //如果配置文件设置了管理员用户则初始化管理员用户
            String admin = properties.getQq().getAdmin();
            if (qq.equals(admin)) {
                user = new User();
                user.setQq(qq);
                user.setRole(UserRole.ADMIN);
                user.setFastCount(9999);
                user.setRelaxCount(9999);
            } else {
                //如果没有用户，就创建一个normal用户
                user = new User();
                user.setQq(qq);
                user.setRole(UserRole.NORMAL);
                DefaultRoleCount defaultRoleCount = defaultRoleCountMapper.selectById(user.getRole());
                user.setFastCount(defaultRoleCount.getFastCount());
                user.setRelaxCount(defaultRoleCount.getRelaxCount());
                user.setFastExpireTime(DateTimeUtil.nextMonth());
                user.setRelaxExpireTime(DateTimeUtil.tomorrow());
            }
            userMapper.insert(user);
        }

        return user;
    }

    public void checkPlusUserExpire(User user) {
        if (user.getRole().equals(UserRole.PLUS)) {
            if (DateTimeUtil.expire(user.getFastExpireTime())) {
                log.info("user[{}] : fast plus expire", user.getQq());
                user.setRole(UserRole.NORMAL);
                DefaultRoleCount defaultRoleCount = defaultRoleCountMapper.selectById(user.getRole());
                user.setFastCount(defaultRoleCount.getFastCount());
                user.setRelaxCount(defaultRoleCount.getRelaxCount());
                user.setFastExpireTime(DateTimeUtil.nextMonth());
                user.setRelaxExpireTime(DateTimeUtil.tomorrow());
                userMapper.updateById(user);
            }
        }
    }

}
