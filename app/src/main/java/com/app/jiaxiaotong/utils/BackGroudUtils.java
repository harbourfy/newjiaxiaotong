package com.app.jiaxiaotong.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.app.jiaxiaotong.AppContext;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.activity.BaseActivity;

/**
 * Created by Administrator on 2015/8/27.
 */
public class BackGroudUtils {

    public static Button setLoginButtonStyle(BaseActivity activity,int resId){
        Button button = (Button) activity.findViewById(resId);
        if (AppContext.getIdentityType() == 1){
            button.setBackgroundResource(R.drawable.login_btn_bg);

        }else {
            button.setBackgroundResource(R.drawable.login_btn_bg);
        }
        button.setTextColor(activity.getResources().getColor(R.color.app_white));
        return button;
    }

}
