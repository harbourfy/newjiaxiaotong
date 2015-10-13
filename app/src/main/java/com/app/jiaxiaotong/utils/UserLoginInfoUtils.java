package com.app.jiaxiaotong.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.model.UserModel;

/**
 * Created by ekfans-com on 2015/10/13.
 */
public class UserLoginInfoUtils {

    private static final String USER_TEL = "user_tel";

    private static final String USER_LOGIN_INFO = "user_login_info";
    /**
     * 写入用户电话到首选项
     * @param context
     * @param tel
     */
    public static void writeUserTel(Context context,String tel){
        if (TextUtils.isEmpty(tel)) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(USER_LOGIN_INFO, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_TEL, tel);
        editor.commit();
    }

    /**
     * 读取用户电话
     * @param context
     */
    public static String readUserTel(Context context){
        if (null == context) {
            return null;
        }
        SharedPreferences pref = context.getSharedPreferences(USER_LOGIN_INFO, Context.MODE_APPEND);
        return pref.getString(USER_TEL,"");
    }
}
