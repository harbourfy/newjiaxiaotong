package com.app.jiaxiaotong.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.jiaxiaotong.ChildrenInfoKeeper;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChangeTelModel;
import com.app.jiaxiaotong.model.StringModel;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;
import com.bumptech.glide.Glide;
import com.easemob.chat.EMChatManager;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends BaseActivity implements View.OnClickListener, LoadFinishedListener {

    private SettingActivity activity = SettingActivity.this;

    private TextView serverPhoneTv;

    private String tel;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        dialog = new ProgressDialog(activity);
        dialog.setMessage("正在加载...");
        dialog.setCanceledOnTouchOutside(false);
        initView();
        getTel();
    }

    private void initView() {
        ToolBarUtils.initToolBar(activity, "系统设置");
        serverPhoneTv = (TextView) findViewById(R.id.setting_server_phone_tv);
        TextView reportInfoTv = (TextView) findViewById(R.id.setting_report_info_tv);
        TextView aboutAppTv = (TextView) findViewById(R.id.setting_about_app_tv);
        TextView clearCacheTv = (TextView) findViewById(R.id.setting_clear_cache_tv);
        TextView loginOutTv = (TextView) findViewById(R.id.setting_logout_tv);
        serverPhoneTv.setText("服务热线");
        serverPhoneTv.setOnClickListener(this);
        reportInfoTv.setOnClickListener(this);
        aboutAppTv.setOnClickListener(this);
        clearCacheTv.setOnClickListener(this);
        loginOutTv.setOnClickListener(this);
    }

    private void getTel() {
        dialog.show();
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_SERVICE_TEL);
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/hotline");
        BaseController.getServiceTel(activity, this, reqMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_server_phone_tv:
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
                startActivity(intent);
                break;
            case R.id.setting_report_info_tv:
                startActivity(new Intent(activity,ReportActivity.class));
                break;
            case R.id.setting_about_app_tv:
                startActivity(new Intent(activity,AboutAppActivity.class));
                break;
            case R.id.setting_clear_cache_tv:
                Glide.getPhotoCacheDir(getApplication()).deleteOnExit();
                ToastUtils.ToastMsg(activity,"清除缓存成功");
                break;
            case R.id.setting_logout_tv:
                UserInfoKeeper.clearInfo(activity);
                LoginInfoKeeper.clearInfo(activity);
                ChildrenInfoKeeper.clearInfo(activity);
                EMChatManager.getInstance().logout();
                startActivity(new Intent(activity,LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        dialog.dismiss();
        if (baseModel !=  null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_SERVICE_TEL)) {
                        StringModel telModel = (StringModel) baseModel;
                        serverPhoneTv.setText("服务热线（" + telModel.getResult() + "）");
                        tel = telModel.getResult();
                    }
                }
            }
        }
    }
}
