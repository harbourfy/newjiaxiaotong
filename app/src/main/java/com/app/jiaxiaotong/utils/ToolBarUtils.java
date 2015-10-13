package com.app.jiaxiaotong.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.activity.BaseActivity;

/**
 * Created by Administrator on 2015/8/27.
 *
 *
 */
public class ToolBarUtils {

    public static void initToolBar(final BaseActivity activity,String title){
        ImageView backIv = (ImageView) activity.findViewById(R.id.title_left_back_iv);
        TextView titleTv = (TextView) activity.findViewById(R.id.title_title_tv);
        titleTv.setText(title);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

}
