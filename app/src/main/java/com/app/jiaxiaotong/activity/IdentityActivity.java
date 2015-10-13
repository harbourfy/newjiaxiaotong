package com.app.jiaxiaotong.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.jiaxiaotong.ChildrenInfoKeeper;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.adapter.ContactListAdapter;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChildModel;
import com.app.jiaxiaotong.model.ContactModel;
import com.app.jiaxiaotong.utils.DialogUtils;
import com.app.jiaxiaotong.utils.GetFirstLetter;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentityActivity extends BaseActivity implements View.OnClickListener, LoadFinishedListener {

    private ProgressDialog dialog;

    private IdentityActivity activity = IdentityActivity.this;

    public static final String IDENTITY_MOTHER = "mother";

    public static final String IDENTITY_FATHER = "father";

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        initView();
    }

    private void initView() {
        ToolBarUtils.initToolBar(activity,"我的身份");
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在修改身份...");
        View momView = findViewById(R.id.identity_user_mom_tv);
        View daddyView = findViewById(R.id.identity_user_daddy_tv);
        momView.setOnClickListener(this);
        daddyView.setOnClickListener(this);
    }

    /**
     * 家长获取好友列表
     */
    private void changeIdentity(String relation){
        dialog.show();
        List<ChildModel> childModels = ChildrenInfoKeeper.readUserInfo(activity);
        String childrenIds = "";
        if (childModels.size() >1){
            for (int i = 0; i < childModels.size() ; i ++){
                childrenIds = childrenIds + childModels.get(i).getUid() + ",";
            }
        }else if(childModels.size() == 1) childrenIds = childModels.get(0).getUid();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(this).getToken());
        reqMap.put(Constant.UID, UserInfoKeeper.readUserInfo(this).getUid());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/relation");
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_CHANGE_IDENTITY);
        reqMap.put("relation",relation);
        reqMap.put("children",childrenIds);
        BaseController.changeIdentity(activity, this, reqMap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.identity_user_mom_tv:
                changeIdentity(IDENTITY_MOTHER);
                type = IDENTITY_MOTHER;
                break;
            case R.id.identity_user_daddy_tv:
                changeIdentity(IDENTITY_FATHER);
                type = IDENTITY_FATHER;
                break;
        }
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        dialog.dismiss();
        if (baseModel.getCode() == null){
            if(baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                ToastUtils.ToastMsg(activity,"身份修改成功");
                Intent intent = new Intent();
                intent.putExtra("type",type);
                setResult(RESULT_OK, intent);
                UserInfoKeeper.writeUserIdentity(activity,type);
                finish();

            }else ToastUtils.ToastMsg(activity, baseModel.getMessage());
        }else if (baseModel.getCode().equalsIgnoreCase(Constant.TOEKN_EXPIRE)){//登录过期
            DialogUtils.loginDialog(activity);
        }else {
            ToastUtils.ToastMsg(activity,baseModel.getMessage());
        }
    }
}
