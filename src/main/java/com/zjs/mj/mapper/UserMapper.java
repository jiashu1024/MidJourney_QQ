package com.zjs.mj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjs.mj.entity.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
