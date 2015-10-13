package com.app.jiaxiaotong;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.model.ChildModel;
import com.app.jiaxiaotong.model.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ChildrenInfoKeeper {

    private static final String CHILDREN_UID = "children_uid";
    private static final String CHILDREN_SCHOOL_NAME = "children_school_name";
    private static final String CHILDREN_NAME = "children_name";
    private static final String CHILDREN_GENDER = "children_gender";
    private static final String CHILDREN_CLASS_NAME = "children_class_name";
    private static final String CHILDREN_HEADER = "children_header";

    private static final String CHILDREN_SIZE = "size";

    private static final String PREFERENCES_NAME = "children_info";

    public static void writeChildrenInfo(Context context,List<ChildModel> infos) {
        if (null == context || null == infos) {
            return;
        }
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(CHILDREN_SIZE,infos.size()); /*sKey is an array*/

        for(int i=0;i<infos.size();i++) {
            editor.remove(CHILDREN_UID + i);
            editor.remove(CHILDREN_SCHOOL_NAME + i);
            editor.remove(CHILDREN_NAME + i);
            editor.remove(CHILDREN_GENDER + i);
            editor.remove(CHILDREN_CLASS_NAME + i);
            editor.remove(CHILDREN_HEADER + i);

            editor.putString(CHILDREN_UID + i, infos.get(i).getUid());
            editor.putString(CHILDREN_SCHOOL_NAME + i, infos.get(i).getSchoolName());
            editor.putString(CHILDREN_NAME + i, infos.get(i).getName());
            editor.putString(CHILDREN_GENDER + i, infos.get(i).getGender());
            editor.putString(CHILDREN_CLASS_NAME + i, infos.get(i).getClassName());
            editor.putString(CHILDREN_HEADER + i, infos.get(i).getHeader());
        }

        editor.commit();
    }

    /**
     * 写入用户头像到首选项
     * @param context
     * @param info
     */
    public static void writeUserAvatar(Context context,String info,int position){
        if (null == context || null == info) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CHILDREN_HEADER + position, info);

        editor.commit();
    }

    /**
     * 从首选项读取数据
     * @param context
     * @return
     */
    public static List<ChildModel> readUserInfo(Context context){
        if (null == context) {
            return null;
        }
        List<ChildModel> info = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
        for(int i=0;i<pref.getInt(CHILDREN_SIZE,0);i++) {
            ChildModel childModel = new ChildModel();
            childModel.setUid(pref.getString(CHILDREN_UID + i,""));
            childModel.setName(pref.getString(CHILDREN_NAME + i, ""));
            childModel.setClassName(pref.getString(CHILDREN_CLASS_NAME + i, ""));
            childModel.setGender(pref.getString(CHILDREN_GENDER + i, ""));
            childModel.setHeader(pref.getString(CHILDREN_HEADER + i, ""));
            childModel.setSchoolName(pref.getString(CHILDREN_SCHOOL_NAME + i,""));
            info.add(childModel);
        }
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
