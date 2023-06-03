package com.zjs.mj.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("image_message")
public class ImageMessage {
    @TableId
    private String messageId;
    private String imageUrl;
    private String qq;
    private String groupId;
    private LocalDateTime time;

}
