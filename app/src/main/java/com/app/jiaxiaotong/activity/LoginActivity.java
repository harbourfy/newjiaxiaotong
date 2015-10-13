package com.app.jiaxiaotong.activity;

import android.app.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.jiaxiaotong.AppContext;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.MyHXSDKHelper;
import com.app.jiaxiaotong.im.controller.HXSDKHelper;
import com.app.jiaxiaotong.im.db.HXDBManager;
import com.app.jiaxiaotong.im.db.UserDao;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.im.utils.CommonUtils;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.LoginModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import org.bitlet.weupnp.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener,LoadFinishedListener{

    public static final int LOGIN_REQ = 1000;

    private EditText userNameEt,passwordEt;

    private LoginActivity activity = LoginActivity.this;

    private String currentUsername;
    private String currentPassword;

    private boolean progressShow;
    private boolean autoLogin = false;
    private ProgressDialog pd;

    private LoginModel loginModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 如果用户名密码都有，直接进入主页面
        if (MyHXSDKHelper.getInstance().isLogined()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        userNameEt = (EditText) findViewById(R.id.login_activity_login_et);
        passwordEt = (EditText) findViewById(R.id.login_activity_password_et);
        Button loginBtn = (Button) findViewById(R.id.login_activity_login_btn);
        TextView forgetPasswordTv = (TextView) findViewById(R.id.login_activity_forget_password_tv);
        loginBtn.setOnClickListener(this);
        forgetPasswordTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_activity_forget_password_tv:
                startActivity(new Intent(activity,ResetPasswordActivity.class));
                break;
            case R.id.login_activity_login_btn:
//                login();
                loginToService();
                break;
        }
    }

    private void loginToService(){
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = userNameEt.getText().toString().trim();
        currentPassword = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("systemName","onecard-android");
        reqMap.put("systemPassword","hhxxttxs");
        reqMap.put("uid",userNameEt.getText().toString());
        reqMap.put("userPassword",passwordEt.getText().toString());
        reqMap.put("grant_type","password");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_LOGIN);
        reqMap.put("url",ServiceConst.SERVICE_URL + "/token");
        BaseController.login(activity,this,reqMap);
    }

    /**
     * 登录环信
     *
     */
    public void login() {
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(UserInfoKeeper.readUserInfo(activity).getUid(), "ichson2015", new EMCallBack() {

            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
                // 登陆成功，保存用户名密码
                AppContext.getAppContext().setUserName(currentUsername);
                AppContext.getAppContext().setPassword(currentPassword);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    EMChatManager.getInstance().loadAllConversations();
                    //将好友信息加载到内存
                    // 处理好友和群组
                    initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            MyHXSDKHelper.getInstance().logout(true, null);
                            Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        AppContext.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                //保存登录信息
                LoginInfoKeeper.writeUserInfo(activity, loginModel);
//                if (UserInfoKeeper.readUserInfo(activity).getType().equalsIgnoreCase("family")) {
////                    getChildInfo();
//                }
                if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                    pd.dismiss();
                }
                // 进入主页面
                if (MainActivity.sMainActivity != null){
                    MainActivity.sMainActivity.finish();
                }
                Intent intent = new Intent(LoginActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
//            manager.killBackgroundProcesses(getPackageName());
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 存入内存
        ((MyHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
    /**
     * 获取用户信息
     */
    private void getUserInfo(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, loginModel.getToken());
        reqMap.put("mobile","android");
        reqMap.put(Constant.URL, ServiceConst.SERVICE_URL + "/open/api/mobiles/user/" + LoginInfoKeeper.readUserInfo(activity).getUid());
//        reqMap.put(Constant.URL, ServiceConst.SERVICE_URL + "/open/api/users/{" + currentUsername +"}");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_USERINFO);
        BaseController.getUserInfo(activity, this, reqMap);

    }
    @Override
    public void loadFinished(BaseModel baseModel) {
        if (baseModel != null){
            if (baseModel.getCode() == null){
                if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_LOGIN)){
                    //登录成功，获取用户信息
                    loginModel = (LoginModel) baseModel;
                    LoginInfoKeeper.writeUserInfo(activity, (LoginModel) baseModel);
                    getUserInfo();

                }else if(baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_USERINFO)){
                    if(baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                        UserModel userModel = ((UserModel) baseModel).getDetails();
                        if (userModel.getUserTypes().length > 1){
                            showChoiceDialog(userModel);
                        }else{
                            //获取用户信息成功，登录环信
                            if (userModel.getUserTypes() != null && userModel.getUserTypes().length >0)
                                userModel.setType(userModel.getUserTypes()[0]);
                            UserInfoKeeper.writeUserInfo(activity, userModel);
                            login();
                        }
                    }else {
                        pd.dismiss();
                        ToastUtils.ToastMsg(activity, baseModel.getMessage());
                    }
                }
            }else{
                pd.dismiss();
                if (baseModel.getMsg() != null)
                    ToastUtils.ToastMsg(activity,baseModel.getMsg());
                else ToastUtils.ToastMsg(activity,"网络错误");
            }

        }
    }

    private void showChoiceDialog(final UserModel userModel){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        String[] identity = new String[userModel.getUserTypes().length];
        for (int i = 0;i < userModel.getUserTypes().length;i++){
            if (userModel.getUserTypes()[i].equalsIgnoreCase("teacher")){
                identity[i] = "教师";
            }else{
                identity[i] = "家长";
            }
        }
        builder.setTitle("请选择你的身份")
                .setItems(identity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userModel.setType(userModel.getUserTypes()[which]);
                        UserInfoKeeper.writeUserInfo(activity, userModel);
                        login();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
               if (pd.isShowing())
                   pd.dismiss();
            }
        });
        dialog.show();
    }
}
