package com.app.jiaxiaotong.activity;

import android.os.Bundle;
import android.view.View;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.im.widget.photoview.PhotoView;
import com.app.jiaxiaotong.im.widget.photoview.PhotoViewAttacher;
import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2015/10/9.
 */
public class ShowBigAvatar extends BaseActivity {
    private String url;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_show_big_image);
        url = getIntent().getExtras().getString("url");
        PhotoView avatar = (PhotoView) findViewById(R.id.image);
        Glide.with(this).load(url).placeholder(R.mipmap.father_default_icon).into(avatar);
        avatar.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }
}
