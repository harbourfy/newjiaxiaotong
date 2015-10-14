package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.utils.Utils;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChangeTelModel;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;
import com.app.jiaxiaotong.utils.UserLoginInfoUtils;

import java.util.HashMap;
import java.util.Map;

public class ChangeTelActivity extends BaseActivity implements View.OnClickListener,LoadFinishedListener{

    private EditText telEt;

    private TextView getCodeTv;

    private EditText authCodeEt;

    private TimeCount count;//计时器

    private ChangeTelActivity activity = ChangeTelActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tel);
        ToolBarUtils.initToolBar(this, "修改手机号");
        count = new TimeCount(60000, 1000);
        initView();
    }

    private void initView() {
        telEt = (EditText) findViewById(R.id.change_tel_input_tel_tv);
        getCodeTv = (TextView) findViewById(R.id.change_tel_get_code_tv);
        authCodeEt = (EditText) findViewById(R.id.change_tel_code_et);
        TextView nextTv = (TextView) findViewById(R.id.change_tel_next_btn);
        getCodeTv.setOnClickListener(this);
        nextTv.setOnClickListener(this);//下一步

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_tel_get_code_tv:
                if (Utils.isMobileNO(telEt.getText().toString())){
                    count.start();
                    getAuthCode();
                }else {
                    ToastUtils.ToastMsg(activity,"请输入正确的手机号");
                }
                break;
            case R.id.change_tel_next_btn:
                if (authCodeEt.getText().length() > 0 && Utils.isMobileNO(telEt.getText().toString())){
                    verifyAuthCode();
                    authCodeEt.setText("");
                    count.onFinish();
                    getCodeTv.setText(R.string.get_code);
                    getCodeTv.setBackgroundColor(Color.parseColor("#b7d455"));
                    getCodeTv.setClickable(true);

                }else if (!Utils.isMobileNO(telEt.getText().toString())){
                    ToastUtils.ToastMsg(activity,"请输入正确手机号");
                }else {
                    ToastUtils.ToastMsg(activity,"请输入正确的验证码");
                }
                break;
        }
    }

    private void getAuthCode(){
        Map<String,Object> reqMap = new HashMap<>();
//        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_AUTHCODE);
        reqMap.put("uid",UserInfoKeeper.readUserInfo(activity).getUid());
//        reqMap.put("type",UserInfoKeeper.readUserInfo(activity).getType());
        reqMap.put("phone",telEt.getText().toString());
        reqMap.put("url",ServiceConst.SERVICE_URL + "/api/msgs/sendverify");
        BaseController.getAuthCode(activity,this,reqMap);
    }

    private void verifyAuthCode(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_VERIFY_AUTHCODE);
        reqMap.put("uid",UserInfoKeeper.readUserInfo(activity).getUid());
        reqMap.put("phone",telEt.getText().toString());
        reqMap.put("code",authCodeEt.getText().toString());
        reqMap.put("url",ServiceConst.SERVICE_URL + "/api/msgs/verify");
        BaseController.verifyAuthCode(activity, this, reqMap);
    }

    private void changeTel(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_CHANGE_TEL);
        reqMap.put("uid",UserInfoKeeper.readUserInfo(activity).getUid());
        reqMap.put("phone",telEt.getText().toString());
        reqMap.put("code",authCodeEt.getText().toString());
        reqMap.put("url",ServiceConst.SERVICE_URL + "/open/api/mobiles/phone");
        BaseController.changeTel(activity, this, reqMap);
    }
    @Override
    public void loadFinished(BaseModel baseModel) {
        if (baseModel != null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_AUTHCODE)) {
                        ToastUtils.ToastMsg(activity, "验证码已发送");
                    } else if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_VERIFY_AUTHCODE)) {
                        ChangeTelModel telModel = (ChangeTelModel) baseModel;
                        if (telModel.isResult()) {
                            changeTel();
                        } else ToastUtils.ToastMsg(activity, "验证码错误");

                    } else if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_CHANGE_TEL)) {
                        UserInfoKeeper.writeUserTel(activity, telEt.getText().toString());
                        startActivity(new Intent(activity, LoginActivity.class));
                        ToastUtils.ToastMsg(activity, "手机号码修改成功");
                    }
                } else {
                    ToastUtils.ToastMsg(activity, baseModel.getMessage());
                    cancleTimeCount();
                }
            } else {
                ToastUtils.ToastMsg(activity, baseModel.getMsg() + "");
                cancleTimeCount();
            }
        }
    }

    private void cancleTimeCount(){
        count.cancel();
        getCodeTv.setText(R.string.get_code);
        getCodeTv.setTextColor(Color.parseColor("#ffffff"));
        getCodeTv.setBackgroundColor(Color.parseColor("#b7d455"));
        getCodeTv.setClickable(true);
    }
    /**
     * 继承CountDownTimer来记录点击获取验证码
     */
    /**
     * 继承CountDownTimer来记录点击获取验证码
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            getCodeTv.setText(R.string.get_code);
            getCodeTv.setTextColor(Color.parseColor("#ffffff"));
            getCodeTv.setBackgroundColor(Color.parseColor("#b7d455"));
            getCodeTv.setClickable(true);
        }
        /**
         * 重写计时操作，设置按钮显示时间，按钮不可点击
         */
        @Override
        public void onTick(long millisUntilFinished) {
            getCodeTv.setText(millisUntilFinished / 1000 + "秒后重新获取");
            getCodeTv.setBackgroundColor(Color.parseColor("#95b528"));
            getCodeTv.setTextColor(Color.parseColor("#b7d455"));
            getCodeTv.setClickable(false);
        }

    }
}
