package com.app.jiaxiaotong.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ResultCode {

    public static final String USER_AUTH_FAIL = "user_auth_fail";
    public static final String AUTH_FAIL = "auth_fail";
    public static final String TOKEN_EXPIRE = "token_expire";
    public static final String TOEKN_ILLEGAL = "token_illegal";
    public static final String TOKEN_LEGAL = "token_legal";
    public static final String SERVICE_EXCEPTION = "server_exception";
    public static final String OK = "ok";

    public static final String SUCCESS = "success";

    public static final Map<String,String> resultCode = new HashMap<String,String>();

    static{
        resultCode.put(USER_AUTH_FAIL,"用户名密码认证失败");
        resultCode.put(AUTH_FAIL,"Systemname和systempassword认证失败");
        resultCode.put(TOKEN_EXPIRE,"Token过期，需要重新获取");
        resultCode.put(TOEKN_ILLEGAL,"非法的token");
        resultCode.put(TOKEN_LEGAL,"合法的token");
        resultCode.put(SERVICE_EXCEPTION,"服务端异常");
        resultCode.put(OK,"调用正常");

    }
}
