package com.app.jiaxiaotong;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.jiaxiaotong.model.LoginModel;

/**
 * Created by Administrator on 2015/9/3.
 */
public class LoginInfoKeeper {

    private static final String TOKEN = "token";
    private static final String UID = "uid";
    private static final String EXPIRETIME = "expireTime";
    private static final String CREATETIME = "ereateTime";
    private static final String PREFERENCES_NAME = "login_info";

    /**
     * 写入用户信息到首选项
     * @param context
     * @param info
     */
    public static void writeUserInfo(Context context,LoginModel info){
        if (null == context || null == info) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TOKEN, info.getToken());
//        editor.putString(UID, info.getUid());
        editor.putString(UID,info.getUid());
        editor.putLong(EXPIRETIME, info.getExpireTime());
        editor.putLong(CREATETIME, info.getCreateTime());
        editor.commit();
    }


    /**
     * 从首选项读取数据
     * @param context
     * @return
     */
    public static LoginModel readUserInfo(Context context){
        if (null == context) {
            return null;
        }
        LoginModel info = new LoginModel();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        info.setToken(pref.getString(TOKEN, ""));
        info.setUid(pref.getString(UID, ""));
        info.setCreateTime(pref.getLong(CREATETIME, 0));
        info.setExpireTime(pref.getLong(EXPIRETIME, 0));
        return info;
    }
    /**
     * 清除首选项数据
     * @param context
     */
    public static void clearInfo(Context context){
        if (null == context) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
