package com.zjs.mj.entity;

import lombok.Data;

/**
 * 向任务池提交任务的结果
 * 任务先被提交任务池，再被任务池分配再执行
 */
@Data
public class SubmitResult {
    private boolean success;
    private String message;

    public SubmitResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SubmitResult success(String message) {
        return new SubmitResult(true, message);
    }

    public static SubmitResult success() {
        return new SubmitResult(true,"");
    }

    public static SubmitResult failure(String message) {
       return new SubmitResult(false, message);
    }
}
