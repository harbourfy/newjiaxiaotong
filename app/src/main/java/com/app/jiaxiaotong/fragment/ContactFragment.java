package com.app.jiaxiaotong.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.jiaxiaotong.AppContext;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.AddContactActivity;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.activity.ChatActivity;
import com.app.jiaxiaotong.activity.TeacherContactActivity;
import com.app.jiaxiaotong.adapter.ContactAdapter;
import com.app.jiaxiaotong.adapter.ContactListAdapter;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.MyHXSDKHelper;
import com.app.jiaxiaotong.im.controller.HXSDKHelper;
import com.app.jiaxiaotong.im.db.UserDao;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.im.utils.UserUtils;
import com.app.jiaxiaotong.im.widget.Sidebar;
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

public class ContactFragment extends Fragment implements LoadFinishedListener {

    private ContactListAdapter adapter;
    private List<ContactModel> contactList = new ArrayList<>();
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    ImageButton clearSearch;
    EditText query;
    View progressBar;

    public static ContactFragment newInstance(String classOu) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("classOu", classOu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        loadingDialog = new ProgressDialog(getActivity());
//        loadingDialog.setMessage("正在加载");
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        listView = (ListView) getView().findViewById(R.id.list);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
        sidebar.setListView(listView);

        //搜索框
        query = (EditText) getView().findViewById(R.id.query);
        query.setHint(R.string.search);
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
        if (UserInfoKeeper.readUserInfo(getActivity()).getType().equalsIgnoreCase(Constant.TEACHER)){
            teacherGetContactList();
        }else familyGetContactList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (UserInfoKeeper.readUserInfo(getActivity()).getType().equalsIgnoreCase(Constant.TEACHER)){
                    Intent intent = new Intent(getActivity(), TeacherContactActivity.class);
                    intent.putExtra("classOu",contactList.get(position).getOu());
                    startActivity(intent);
                }else {
                    // demo中直接进入聊天页面，实际一般是进入用户详情页
                    startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUid()));
                }
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

//        ImageView addContactView = (ImageView) getView().findViewById(R.id.iv_new_contact);
//        // 进入添加好友页
//        addContactView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AddContactActivity.class));
//            }
//        });
        registerForContextMenu(listView);

        progressBar = (View) getView().findViewById(R.id.progress_bar);

        if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    /**
     * 家长获取好友列表
     */
    private void familyGetContactList(){
//        loadingDialog.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(getActivity()).getToken());
        reqMap.put(Constant.UID,UserInfoKeeper.readUserInfo(getActivity()).getUid());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/family/addressbook");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_FAMILY_CONTACT_LIST);
        BaseController.getFamilyContactList((BaseActivity) getActivity(), this, reqMap);
    }

    /**
     * 获取教师班级列表
     */
    private void teacherGetContactList(){
//        loadingDialog.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(getActivity()).getToken());
        reqMap.put(Constant.UID,UserInfoKeeper.readUserInfo(getActivity()).getUid());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/teacher/classes");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_TEACHER_CLASS_LIST);
        BaseController.getTeacherClassList((BaseActivity) getActivity(), this, reqMap);
    }


    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
//        loadingDialog.dismiss();
        progressBar.setVisibility(View.GONE);
        if (baseModel != null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_FAMILY_CONTACT_LIST)) {
                        contactList = ((ContactModel) baseModel).getResult();
                        // 排序
                        Collections.sort(contactList, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel lhs, ContactModel rhs) {
                                return lhs.getCn().compareTo(rhs.getCn());
                            }
                        });
                        List<User> users = new ArrayList<>();
                        //设置头部标签，保存用户信息到数据库
                        for (int i = 0; i < contactList.size(); i++) {
                            contactList.get(i).setHeaderTxt(GetFirstLetter.getFristChar(contactList.get(i).getCn()));
                            User user = new User();
                            user.setAvatar(ServiceConst.SERVICE_URL + "/api/mobiles/header/" + contactList.get(i).getHeader());
                            user.setNick(contactList.get(i).getCn());
                            user.setUsername(contactList.get(i).getUid());
                            user.setGender(contactList.get(i).getGender());
                            user.setMobilePhone(contactList.get(i).getMobilePhone());
                            users.add(user);
                        }
                        //排序
                        Collections.sort(contactList, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel lhs, ContactModel rhs) {
                                return lhs.getHeaderTxt().compareTo(rhs.getHeaderTxt());
                            }
                        });
//                    ((MyHXSDKHelper)HXSDKHelper.getInstance()).updateContactList(users);
                        //存入内存
                        Map<String, User> userlist = new HashMap<String, User>();
                        for (User user : users) {
                            String username = user.getUsername();
                            userlist.put(username, user);
                        }
                        AppContext.getAppContext().setContactList(userlist);
                        new UserDao(getActivity()).saveContactList(users);
                        // 设置adapter
                        adapter = new ContactListAdapter(getActivity(), R.layout.row_contact, contactList);
                        listView.setAdapter(adapter);
                    } else if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_TEACHER_CLASS_LIST)) {
                        contactList = ((ContactModel) baseModel).getResult();
                        // 排序
                        Collections.sort(contactList, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel lhs, ContactModel rhs) {
                                return lhs.getCn().compareTo(rhs.getCn());
                            }
                        });
                        // 设置adapter
                        adapter = new ContactListAdapter(getActivity(), R.layout.row_contact, contactList);
                        listView.setAdapter(adapter);
                    }
                } else ToastUtils.ToastMsg(getActivity(), baseModel.getMessage());
            } else if (baseModel.getCode().equalsIgnoreCase(Constant.TOEKN_EXPIRE)) {//登录过期
                DialogUtils.loginDialog(getActivity());
            } else {
                ToastUtils.ToastMsg(getActivity(), baseModel.getMessage());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UserInfoKeeper.readUserInfo(getActivity()).getAvatarChange() == 1){
            adapter.notifyDataSetChanged();
            UserInfoKeeper.writeUserAvatarChange(getActivity(), 0);
        }
    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
//                    getContactList();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
