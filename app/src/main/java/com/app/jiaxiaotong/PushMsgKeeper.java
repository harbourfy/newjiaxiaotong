package com.app.jiaxiaotong;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.app.jiaxiaotong.model.LoginModel;
import com.app.jiaxiaotong.model.PushMsgModel;

/**
 * Created by Administrator on 2015/9/23.
 */
public class PushMsgKeeper {
    private static final String CLOCKIN = "clockin";
    private static final String ANNOUNCEMENT = "announcement";
    private static final String PREFERENCES_NAME = "push_msg";
    private static final String CLOCKIN_IS_READ = "clockin_is_read";
    private static final String ANNOUNCEMENT_IS_READ = "announcement_is_read";
    /**
     * 写入信息到首选项
     * @param context
     * @param info
     */
    public static void writePushInfo(Context context,PushMsgModel info){
        if (null == context || null == info) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CLOCKIN, info.getClockin());
        editor.putString(ANNOUNCEMENT, info.getAnnouncement());
        editor.putBoolean(CLOCKIN_IS_READ,false);
        editor.putBoolean(ANNOUNCEMENT_IS_READ,false);
        editor.commit();
    }


    /**
     * 从首选项读取数据
     * @param context
     * @return
     */
    public static PushMsgModel readPushMsg(Context context){
        if (null == context) {
            return null;
        }
        PushMsgModel info = new PushMsgModel();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        info.setClockin(pref.getString(CLOCKIN, ""));
        info.setAnnouncement(pref.getString(ANNOUNCEMENT, ""));
        info.setIsreadAnnouncement(pref.getBoolean(ANNOUNCEMENT_IS_READ, false));
        info.setIsreadClockin(pref.getBoolean(CLOCKIN_IS_READ, false));
        return info;
    }

    /**
     * 清除考勤
     */
    public static void clearCheckIn(Context context){
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(CLOCKIN_IS_READ, true);
        editor.commit();
    }
    /**
     * 清除公告
     */
    public static void clearNotice(Context context){
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(ANNOUNCEMENT_IS_READ, true);
        editor.commit();
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

    /**
     * 判断信息是否为空
     * @param context
     * @return
     */
    public static boolean isEmpty(Context context){
        if (null == context) {
            return true;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        if (!TextUtils.isEmpty(pref.getString(CLOCKIN, "")))
            return false;
        if (!TextUtils.isEmpty(pref.getString(ANNOUNCEMENT, "")))
            return false;
        return true;
    }
    /**
     * 判断信息是否已读
     * @param context
     * @return true 已读
     */
    public static boolean isRead(Context context){
        if (null == context) {
            return true;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        if (pref.getBoolean(CLOCKIN_IS_READ, true) && pref.getBoolean(ANNOUNCEMENT_IS_READ,true))
            return true;
        if (!pref.getBoolean(CLOCKIN_IS_READ, true) )
            return false;
        if (!pref.getBoolean(ANNOUNCEMENT_IS_READ,true))
            return false;
        return true;
    }
}
