package com.app.jiaxiaotong.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.fragment.TeacherContactListFragment;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ContactModel;
import com.app.jiaxiaotong.utils.DialogUtils;
import com.app.jiaxiaotong.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends BaseActivity implements LoadFinishedListener{

    private ReportActivity activity = ReportActivity.this;
    private EditText descEt;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initView();
    }

    private void initView() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("正在提交意见...");
        ImageView backIv = (ImageView) findViewById(R.id.title_left_back_iv);
        TextView titleTv = (TextView) findViewById(R.id.title_title_tv);
        TextView submitTv = (TextView) findViewById(R.id.title_right_sure_tv);
        descEt = (EditText) findViewById(R.id.reprot_desc_et);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv.setText("意见反馈");
        submitTv.setVisibility(View.VISIBLE);
        submitTv.setText("提交");
        submitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descEt.getText().length() > 0)
                    submitReport();
                else ToastUtils.ToastMsg(activity,"请输入反馈意见哦！");
            }
        });

    }

    /**
     * 提交意见
     */
    private void submitReport(){
//        loadingDialog.show();
        dialog.show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Map<String, Object> reqMap = new HashMap<>();
                reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
                reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/suggest");
                reqMap.put(Constant.UID, UserInfoKeeper.readUserInfo(activity).getUid());
                reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_REPORT_MESSAGE);
                Map<String, Object> bodyMap = new HashMap<>();
                reqMap.put("content", descEt.getText().toString());
                BaseController.reportMessage(activity, activity, reqMap, bodyMap);
            }

        }, 2000);
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        dialog.dismiss();
        if (baseModel.getCode() == null){
            if(baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_REPORT_MESSAGE)){
                    ToastUtils.ToastMsg(this, "感谢你的意见反馈！！！");
                    finish();
                }
            }else ToastUtils.ToastMsg(this, baseModel.getMessage());
        }else if (baseModel.getCode().equalsIgnoreCase(Constant.TOEKN_EXPIRE)){//登录过期
            DialogUtils.loginDialog(this);
        }else {
            ToastUtils.ToastMsg(this,baseModel.getMsg());
        }
    }
}
