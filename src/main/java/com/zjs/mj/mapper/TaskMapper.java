package com.zjs.mj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zjs.mj.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
