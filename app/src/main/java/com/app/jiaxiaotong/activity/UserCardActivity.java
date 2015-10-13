package com.app.jiaxiaotong.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.db.HXDBManager;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ContactModel;
import com.app.jiaxiaotong.model.LoginModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.GlideCircleTransform;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

public class UserCardActivity extends BaseActivity implements View.OnClickListener, LoadFinishedListener {

    private UserCardActivity activity = UserCardActivity.this;
    private ContactModel userModel;
    private TextView telTv;

    //弹窗口
    private PopupWindow popupWindow;

    private View parentView;

    private View contentView;
    private String userid;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card);
        ToolBarUtils.initToolBar(activity, "详细信息");
        pd = new ProgressDialog(activity);
        pd.setMessage("正在加载用户信息...");
        if (getIntent().hasExtra("userinfo")) {
            userModel = getIntent().getExtras().getParcelable("userinfo");
            initView();
        }
        else {
            userModel = new ContactModel();
            userid = getIntent().getExtras().getString("username");
            if (userid.equalsIgnoreCase(UserInfoKeeper.readUserInfo(activity).getUid())){
                userModel.setCn(UserInfoKeeper.readUserInfo(activity).getName());
                userModel.setMobilePhone(UserInfoKeeper.readUserInfo(activity).getMobilePhone());
                userModel.setUid(UserInfoKeeper.readUserInfo(activity).getUid());
                userModel.setHeader(UserInfoKeeper.readUserInfo(activity).getAvatar());
                userModel.setGender(UserInfoKeeper.readUserInfo(activity).getGender());
                initView();
            }else{
                User user = HXDBManager.getInstance().getContactList().get(userid);
                if (user != null){
                    userModel.setCn(user.getNick());
                    userModel.setMobilePhone(user.getMobilePhone());
                    userModel.setUid(user.getUsername());
                    userModel.setHeader(user.getAvatar());
                    userModel.setGender(user.getGender());
                    initView();
                } else {
                    getUserInfo();
                }
            }


        }


    }
    /**
     * 获取用户信息
     */
    private void getUserInfo(){
        pd.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put("mobile","android");
        reqMap.put(Constant.URL, ServiceConst.SERVICE_URL + "/open/api/mobiles/user/" + userid);
//        reqMap.put(Constant.URL, ServiceConst.SERVICE_URL + "/open/api/users/{" + currentUsername +"}");
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_USERINFO);
        BaseController.getUserInfo(activity, this, reqMap);

    }
    private void initView() {
        ImageView avatarIv = (ImageView) findViewById(R.id.user_card_avatar_iv);
        TextView userNameTv = (TextView) findViewById(R.id.user_card_username_tv);
        View telView = findViewById(R.id.user_card_tel_layout);
        telTv = (TextView) findViewById(R.id.user_card_tel_tv);
        if (userModel.getUid().equalsIgnoreCase(UserInfoKeeper.readUserInfo(activity).getUid())){
            Glide.with(this)
                    .load(UserInfoKeeper.readUserInfo(activity).getAvatar())
                    .transform(new GlideCircleTransform(this))
                    .placeholder(R.mipmap.father_default_icon)
                    .into(avatarIv);
        }else if (getIntent().hasExtra("userinfo"))
            Glide.with(this)
                .load(ServiceConst.SERVICE_URL + "/api/mobiles/header/" + userModel.getHeader())
                .transform(new GlideCircleTransform(this))
                .placeholder(R.mipmap.father_default_icon)
                .into(avatarIv);
        else
            Glide.with(this)
                    .load(userModel.getHeader())
                    .transform(new GlideCircleTransform(this))
                    .placeholder(R.mipmap.father_default_icon)
                    .into(avatarIv);
        userNameTv.setText(userModel.getCn());
        telTv.setText(userModel.getMobilePhone() + "");
        if (userModel.getGender().equalsIgnoreCase("m")){
            userNameTv.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.user_icon_boy_nor,0);
        }
        avatarIv.setOnClickListener(this);
        telView.setOnClickListener(this);
        /**PopupWindow的界面*/
        contentView = getLayoutInflater()
                .inflate(R.layout.call_phone_layout, null);
        Button callPhoneBtn = (Button) contentView.findViewById(R.id.call_phone_btn);
        Button sendMsgBtn = (Button) contentView.findViewById(R.id.call_phone_send_msg_btn);
        callPhoneBtn.setOnClickListener(this);
        sendMsgBtn.setOnClickListener(this);
        /**初始化PopupWindow*/
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        /**设置PopupWindow弹出和退出时候的动画效果*/
        popupWindow.setAnimationStyle(R.style.animation);
        parentView = this.findViewById(R.id.user_card_main_view);
        parentView.setOnClickListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        parentView.setOnClickListener(this);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (popupWindow.isShowing())
            popupWindow.dismiss();
        switch (v.getId()){
            case R.id.call_phone_btn:
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userModel.getMobilePhone()));
                startActivity(intent);
                break;
            case R.id.call_phone_send_msg_btn:
                // demo中直接进入聊天页面，实际一般是进入用户详情页
                startActivity(new Intent(activity, ChatActivity.class).putExtra("userId", userModel.getUid()));
                break;
            case R.id.user_card_tel_layout:
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.7f;
                getWindow().setAttributes(lp);
                popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.user_card_main_view:
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
                break;
            case R.id.user_card_avatar_iv:
                startActivity(new Intent(this,ShowBigAvatar.class).putExtra("url",userModel.getHeader()));
                break;
        }
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        pd.dismiss();
        if (baseModel != null){
            if (baseModel.getCode() == null){
                if(baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_USERINFO)){
                    if(baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                        UserModel user = ((UserModel) baseModel).getDetails();
                        userModel.setCn(user.getName());
                        userModel.setMobilePhone(user.getMobilePhone());
                        userModel.setUid(user.getUid());
                        userModel.setHeader(user.getAvatar());
                        userModel.setGender(user.getGender());
                        initView();
                    }else {
                        ToastUtils.ToastMsg(activity, baseModel.getMessage());
                    }
                }
            }else{
                if (baseModel.getMsg() != null)
                    ToastUtils.ToastMsg(activity,baseModel.getMsg());
                else ToastUtils.ToastMsg(activity,"网络错误");
            }

        }
    }
}
