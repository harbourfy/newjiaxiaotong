package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.utils.ToolBarUtils;

public class AboutAppActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ToolBarUtils.initToolBar(this, "关于软件");

        initView();
    }

    private void initView() {
        TextView useDescTv = (TextView) findViewById(R.id.activity_about_use_desc_tv);
        TextView lvlTv = (TextView) findViewById(R.id.activity_about_lvl_tv);
        useDescTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutAppActivity.this, UseDescActivity.class));
            }
        });
        lvlTv.setText(""+ getversion());
    }

    private String getversion(){
        String version = "";
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = packInfo.versionName;
        return version;
    }

}
