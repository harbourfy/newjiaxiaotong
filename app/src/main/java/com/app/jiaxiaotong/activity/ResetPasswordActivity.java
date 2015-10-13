package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.utils.Utils;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChangeTelModel;
import com.app.jiaxiaotong.utils.BackGroudUtils;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener, LoadFinishedListener {

    private ResetPasswordActivity activity = ResetPasswordActivity.this;

    private EditText userNameEt,passwordEt,codeEt;

    private TextView codeTv;//获取验证码
    private TimeCount count;

    private ImageView visibilityIv;

    private boolean isVisibility = false;//设置密码是否可见，false不可见

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        initView();
    }

    private void getAuthCode(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_AUTHCODE);
        reqMap.put("phone", userNameEt.getText().toString());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/api/msgs/sendverify");
        BaseController.getAuthCode(activity, this, reqMap);
    }

    private void verifyAuthCode(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_VERIFY_AUTHCODE);
        reqMap.put("phone",userNameEt.getText().toString());
        reqMap.put("code", codeEt.getText().toString());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/api/msgs/verify");
        BaseController.verifyAuthCode(activity, this, reqMap);
    }

    private void resetPassword(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_FORGET_PASSWORD);
        reqMap.put("phone", userNameEt.getText().toString());
        reqMap.put("password", passwordEt.getText().toString());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/api/mobiles/password/forget");
        BaseController.forgetPassword(activity, this, reqMap);
    }

    private void initView() {
        ToolBarUtils.initToolBar(activity, "重置密码");
        userNameEt = (EditText) findViewById(R.id.forget_password_activity_user_name_et);
        passwordEt = (EditText) findViewById(R.id.forget_password_activity_password_et);
        codeEt = (EditText) findViewById(R.id.forget_password_activity_code_et);
        codeTv = (TextView) findViewById(R.id.forget_password_activity_get_code_tv);
        Button nextBtn = BackGroudUtils.setLoginButtonStyle(activity,R.id.forget_password_activity_next_btn);
        visibilityIv = (ImageView) findViewById(R.id.forget_password_activity_password_visibility_iv);
        visibilityIv.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        codeTv.setOnClickListener(this);
        count = new TimeCount(60000, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forget_password_activity_next_btn:
                if (Utils.isMobileNO(userNameEt.getText().toString()) && passwordEt.getText().toString().length() > 5 && codeEt.getText().toString().length() > 3) {
                    verifyAuthCode();
                }else if (!Utils.isMobileNO(userNameEt.getText().toString())){
                    ToastUtils.ToastMsg(activity,"请输入正确的手机号码");
                }else if (passwordEt.getText().toString().length() < 6){
                    ToastUtils.ToastMsg(activity,"密码长度不能小于6位");
                }else if (TextUtils.isEmpty(passwordEt.getText().toString())){
                    ToastUtils.ToastMsg(activity,"密码不能为空");
                }else if (codeEt.getText().toString().length() <= 3){
                    ToastUtils.ToastMsg(activity,"验证码长度不能小于4位");
                }else if (TextUtils.isEmpty(codeEt.getText().toString())) {
                    ToastUtils.ToastMsg(activity, "验证码不能为空");
                }
                break;
            case R.id.forget_password_activity_get_code_tv:
                if (Utils.isMobileNO(userNameEt.getText().toString())){
                    count.start();
                    getAuthCode();
                }else{
                    ToastUtils.ToastMsg(activity,"请输入正确的手机号码");
                }

                break;
            case R.id.forget_password_activity_password_visibility_iv:
                if (isVisibility) {//点击之后不可见
                    isVisibility = false;
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visibilityIv.setImageResource(R.mipmap.pw_invisibility_icon);
                }
                else {//点击之后可见
                    isVisibility = true;
                    passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visibilityIv.setImageResource(R.mipmap.pw_visibility_icon);
                }
                break;
        }
    }

    private void cancleTimeCount(){
        count.cancel();
        codeTv.setText(R.string.get_code);
        codeTv.setTextColor(Color.parseColor("#ffffff"));
        codeTv.setBackgroundColor(Color.parseColor("#b7d455"));
        codeTv.setClickable(true);
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        if (baseModel.getCode() == null){
            if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_AUTHCODE)){
                    ToastUtils.ToastMsg(activity, "验证码已发送");
                }else if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_VERIFY_AUTHCODE)){
                    ChangeTelModel telModel = (ChangeTelModel) baseModel;
                    if (telModel.isResult()){
                        resetPassword();
                    }else ToastUtils.ToastMsg(activity,"验证码错误");

                }else if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_FORGET_PASSWORD)){
                    ToastUtils.ToastMsg(activity,"密码重置成功！");
                    finish();
                }
            }else {
                ToastUtils.ToastMsg(activity,baseModel.getMessage());
                cancleTimeCount();
            }
        }else {
            ToastUtils.ToastMsg(activity,baseModel.getMsg() + "");
            cancleTimeCount();
        }
    }

    /**
     * 继承CountDownTimer来记录点击获取验证码
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            codeTv.setText(R.string.get_code);
            codeTv.setTextColor(Color.parseColor("#ffffff"));
            codeTv.setBackgroundColor(Color.parseColor("#b7d455"));
            codeTv.setClickable(true);
        }
        /**
         * 重写计时操作，设置按钮显示时间，按钮不可点击
         */
        @Override
        public void onTick(long millisUntilFinished) {
            codeTv.setText(millisUntilFinished / 1000 + "秒后重新获取");
            codeTv.setBackgroundColor(Color.parseColor("#95b528"));
            codeTv.setTextColor(Color.parseColor("#b7d455"));
            codeTv.setClickable(false);
        }

    }
}
