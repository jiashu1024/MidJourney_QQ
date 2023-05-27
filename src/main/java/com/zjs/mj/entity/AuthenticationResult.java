package com.zjs.mj.entity;

import lombok.Data;

/**
 * 鉴权结果
 */
@Data
public class AuthenticationResult {
    private boolean ok;
    private String message;

    public static AuthenticationResult ok() {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.setOk(true);
        return authenticationResult;
    }

    public static AuthenticationResult failure(String message) {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.setOk(false);
        authenticationResult.setMessage(message);
        return authenticationResult;
    }
}
