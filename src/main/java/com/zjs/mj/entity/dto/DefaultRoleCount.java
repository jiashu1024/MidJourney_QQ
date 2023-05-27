package com.zjs.mj.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 默认各种role的请求额度
 */
@Data
@TableName("default_role_count")
public class DefaultRoleCount {
    @TableId
    private String role;
    private int fastCount;
    private int relaxCount;
}
