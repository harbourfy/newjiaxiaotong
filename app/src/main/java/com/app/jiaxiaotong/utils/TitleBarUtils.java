package com.app.jiaxiaotong.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.activity.BaseActivity;

/**
 * Created by Administrator on 2015/8/25.
 */
public class TitleBarUtils {

    public static void setTitle(final BaseActivity baseActivity,String title) throws Exception {
        try {
            TextView titleTv = (TextView) baseActivity.findViewById(R.id.title_title_tv);
            ImageView backIv = (ImageView) baseActivity.findViewById(R.id.title_left_back_iv);
            titleTv.setText(title);
            backIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseActivity.finish();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
