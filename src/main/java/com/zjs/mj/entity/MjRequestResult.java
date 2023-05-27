package com.zjs.mj.entity;

import lombok.Data;

/**
 * 向mj提交请求的结果
 * @param <T>
 */
@Data
public class MjRequestResult<T> {
    private boolean success;
    private int code;
    private String description;
    private T result;

    public MjRequestResult() {
    }

    public MjRequestResult(boolean success) {
        this.success = success;
    }

    public MjRequestResult(boolean success, String description) {
        this.success = success;
        this.description = description;
    }

    public MjRequestResult(boolean success, int code, String description) {
        this.success = success;
        this.code = code;
        this.description = description;
    }

    public MjRequestResult(boolean success, int code, String description, T result) {
        this.success = success;
        this.code = code;
        this.description = description;
        this.result = result;
    }

    public static <T> MjRequestResult<T> success(int code) {
        return new MjRequestResult<>(true);
    }

    public static <T> MjRequestResult<T> success(int code, String description) {
        return new MjRequestResult<>(true, code, description);
    }

    public static <T> MjRequestResult<T> success(String description) {
        return new MjRequestResult<>(true, description);
    }

    public static <T> MjRequestResult<T> success(int code, String description, T result) {
        return new MjRequestResult<>(true, code, description, result);
    }

    public static <T> MjRequestResult<T> failure(int code, String description) {
        return new MjRequestResult<>(false, code, description);
    }

    public static <T> MjRequestResult<T> failure(String description) {
        return new MjRequestResult<>(false, description);
    }

    public static <T> MjRequestResult<T> failure(int code, String description, T result) {
        return new MjRequestResult<>(false, code, description, result);
    }


    public static <T> MjRequestResult<T> failure(int code) {
        return new MjRequestResult<>(false, code, null);
    }

}
