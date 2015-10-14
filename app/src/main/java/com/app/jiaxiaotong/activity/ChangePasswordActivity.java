package com.app.jiaxiaotong.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.app.jiaxiaotong.model.ChildModel;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangePasswordActivity extends BaseActivity implements LoadFinishedListener{

    private ChangePasswordActivity activity = ChangePasswordActivity.this;

    private EditText oldEt,newEt,sureEt;

    private ListView childrenListView;

    private List<ChildModel> childModels = new ArrayList<>();

    private ProgressDialog dialog;
    private int type;//修改密码类型，0修改家长密码，1修改孩子密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ToolBarUtils.initToolBar(activity, "修改密码");
        if (getIntent().hasExtra("type")){
            type = getIntent().getExtras().getInt("type");
        }
        initView();

    }

    private void initView() {
        dialog = new ProgressDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("正在修改密码...");
        oldEt = (EditText) findViewById(R.id.change_password_input_oldpassword_tv);
        newEt = (EditText) findViewById(R.id.change_password_input_newpassword_tv);
        sureEt = (EditText) findViewById(R.id.change_password_sure_password_tv);
        final TextView subTv = (TextView) findViewById(R.id.change_password_next_btn);
        subTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != 1) {
                    if (oldEt.getText().length() > 5 && newEt.getText().toString().length() > 5
                            && newEt.getText().toString().equalsIgnoreCase(sureEt.getText().toString())
                            && !oldEt.getText().toString().equalsIgnoreCase(newEt.getText().toString())) {
                        dialog.show();
                        initReq();
                    }else if (oldEt.getText().toString().equalsIgnoreCase(newEt.getText().toString()))
                        ToastUtils.ToastMsg(activity, "新密码不能和旧密码一致");
                    else if (oldEt.getText().length() <= 5) {
                        ToastUtils.ToastMsg(activity, "旧密码长度不够，请重新输入！");
                    } else if (newEt.getText().toString().length() < 6) {
                        ToastUtils.ToastMsg(activity, "新密码必须大于等于6位哦！");
                    } else {
                        ToastUtils.ToastMsg(activity, "两次输入密码不一致！");
                    }
                }else{
                    if (newEt.getText().toString().length() > 5
                            && newEt.getText().toString().equalsIgnoreCase(sureEt.getText().toString())) {
                        dialog.show();
                        initReq();
                    } else if (newEt.getText().toString().length() < 6) {
                        ToastUtils.ToastMsg(activity, "密码必须大于等于6位哦！");
                    } else {
                        ToastUtils.ToastMsg(activity, "两次输入密码不一致！");
                    }
                }
            }
        });
        if (type == 1){
            oldEt.setVisibility(View.GONE);
            childModels.addAll(ChildrenInfoKeeper.readUserInfo(activity));
            if (childModels.size()> 1){
                childrenListView = (ListView) findViewById(R.id.change_password_listview);
                childrenListView.setVisibility(View.VISIBLE);
                ChildrenAdapter adapter = new ChildrenAdapter(this,android.R.layout.simple_expandable_list_item_1,R.id.item_children_tv);
                childrenListView.setAdapter(adapter);
            }



        }
    }

    private void initReq(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_VERIFY_AUTHCODE);
        if (type != 1){
            reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/users/password/" + UserInfoKeeper.readUserInfo(activity).getUid());
            reqMap.put("old-password", oldEt.getText().toString());
        }else{
            reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/users/password/");
        }

        reqMap.put("new-password", sureEt.getText().toString());

        if (type == 1){
            String uids = "";
            if (childModels.size() > 1){
                for (int i = 0 ;i <childModels.size();i++){
                    uids = uids + childModels.get(i).getUid() + ",";
                }
            }else uids = childModels.get(0).getUid();
            reqMap.put("userIds",uids);
        }
        BaseController.changePassword(activity, this, reqMap);
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        dialog.dismiss();
        if (baseModel != null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_CHANGE_PASSWORD)) {
                        finish();
                        ToastUtils.ToastMsg(activity, baseModel.getMessage());
                    }
                } else {
                    ToastUtils.ToastMsg(activity, "修改密码失败");
                }

            } else {
                if (baseModel.getMsg() != null)
                    ToastUtils.ToastMsg(activity, baseModel.getMsg());
                else ToastUtils.ToastMsg(activity, "网络错误");
            }
        }
    }
    class ChildrenAdapter extends ArrayAdapter<ChildModel>{

        private int position = 0;

        public ChildrenAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView textView = (TextView) convertView;
            textView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.ic_launcher,0);
                    ChildrenAdapter.this.position = position;
                    notifyDataSetChanged();
                }
            });
            return textView;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
