package com.app.jiaxiaotong.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.AppContext;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.fragment.TeacherContactListFragment;
import com.app.jiaxiaotong.im.MyHXSDKHelper;
import com.app.jiaxiaotong.im.controller.HXSDKHelper;
import com.app.jiaxiaotong.im.db.UserDao;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ContactModel;
import com.app.jiaxiaotong.utils.DialogUtils;
import com.app.jiaxiaotong.utils.GetFirstLetter;
import com.app.jiaxiaotong.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherContactActivity extends BaseActivity implements OnClickListener,LoadFinishedListener{

    private TextView topLeftTv,topRightTv;
    private List<ContactModel> allContactModel = new ArrayList<>();
    private List<ContactModel> teacherContactModel = new ArrayList<>();
    private List<ContactModel> familyContactModel = new ArrayList<>();
    private ProgressDialog dialog;

    private TeacherContactListFragment familyFragment,teacherFragment;

    private String classOu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_contact);
        classOu = getIntent().getExtras().getString("classOu");
        initView();
        teacherGetContactList();
    }

    private void initView() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在加载联系人列表...");
        topLeftTv = (TextView) findViewById(R.id.activity_teacher_top_left_tv);
        topRightTv = (TextView) findViewById(R.id.activity_teacher_top_right_tv);
        topLeftTv.setSelected(true);
        topLeftTv.setOnClickListener(this);
        topRightTv.setOnClickListener(this);
        ImageView backIv = (ImageView) findViewById(R.id.title_left_back_iv);
        backIv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_teacher_top_left_tv:
                if (!topLeftTv.isSelected()){
                    hideOrShowTab(0);
                    topRightTv.setSelected(false);
                    topLeftTv.setSelected(true);
                }

                break;
            case R.id.activity_teacher_top_right_tv:
                if (!topRightTv.isSelected()){
                    hideOrShowTab(1);
                    topRightTv.setSelected(true);
                    topLeftTv.setSelected(false);
                }
                break;
            case R.id.title_left_back_iv:
                finish();
                break;
        }
    }
    private void hideOrShowTab(int index){
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        if (index == 1){
            trx.hide(teacherFragment);
            trx.show(familyFragment).commit();
        }else{
            trx.hide(familyFragment);
            trx.show(teacherFragment).commit();
        }

    }

    /**
     * 获取教师班级列表
     */
    private void teacherGetContactList(){
//        loadingDialog.show();
        dialog.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(this).getToken());
        reqMap.put("classOu",classOu);
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/class/addressbook");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_TEACHER_CLASS_LIST);
        BaseController.getTeacherClassContactList(this, this, reqMap);
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
//        loadingDialog.dismiss();
        dialog.dismiss();
        if (baseModel != null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_TEACHER_CONTACT_LIST)) {
                        allContactModel.addAll(((ContactModel) baseModel).getResult());

                        //分类
                        for (ContactModel contactModel : allContactModel) {
                            if (contactModel.getRole().equalsIgnoreCase("teacher")) {
                                teacherContactModel.add(contactModel);
                            } else familyContactModel.add(contactModel);
                        }
                        List<User> users = new ArrayList<>();
                        //设置头部标签，保存用户信息到数据库
                        for (int i = 0; i < allContactModel.size(); i++) {
                            allContactModel.get(i).setHeaderTxt(GetFirstLetter.getFristChar(allContactModel.get(i).getCn()));
                            User user = new User();
                            user.setAvatar(ServiceConst.SERVICE_URL + "/api/mobiles/header/" + allContactModel.get(i).getHeader());
                            user.setNick(allContactModel.get(i).getCn());
                            user.setUsername(allContactModel.get(i).getUid());
                            user.setGender(allContactModel.get(i).getGender());
                            user.setMobilePhone(allContactModel.get(i).getMobilePhone());
                            users.add(user);
                        }
                        //排序
                        Collections.sort(teacherContactModel, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel lhs, ContactModel rhs) {
                                return lhs.getHeaderTxt().compareTo(rhs.getHeaderTxt());
                            }
                        });
                        //排序
                        Collections.sort(familyContactModel
                                , new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel lhs, ContactModel rhs) {
                                return lhs.getHeaderTxt().compareTo(rhs.getHeaderTxt());
                            }
                        });
                        //存入内存
                        Map<String, User> userlist = new HashMap<String, User>();
                        for (User user : users) {
                            String username = user.getUsername();
                            userlist.put(username, user);
                        }
                        AppContext.getAppContext().setContactList(userlist);
                        new UserDao(TeacherContactActivity.this).saveContactList(users);
                        familyFragment = TeacherContactListFragment.newInstance(familyContactModel);
                        teacherFragment = TeacherContactListFragment.newInstance(teacherContactModel);
                        // 添加显示第一个fragment
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.contact_fragment_container, teacherFragment)
                                .add(R.id.contact_fragment_container, familyFragment)
                                .hide(familyFragment).show(teacherFragment)
                                .commit();
                    }
                } else ToastUtils.ToastMsg(this, baseModel.getMessage());
            } else if (baseModel.getCode().equalsIgnoreCase(Constant.TOEKN_EXPIRE)) {//登录过期
                DialogUtils.loginDialog(this);
            } else {
                ToastUtils.ToastMsg(this, baseModel.getMessage());
            }
        }
    }
}
