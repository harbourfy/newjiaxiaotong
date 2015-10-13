package com.app.jiaxiaotong.fragment;


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
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.AddContactActivity;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.activity.ChatActivity;
import com.app.jiaxiaotong.activity.TeacherContactActivity;
import com.app.jiaxiaotong.adapter.ContactListAdapter;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.controller.HXSDKHelper;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeacherContactListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeacherContactListFragment extends Fragment {
    private ContactListAdapter adapter;
    private List<ContactModel> contactList ;
    private ListView listView;
    private boolean hidden;
    private Sidebar sidebar;
    private InputMethodManager inputMethodManager;
    ImageButton clearSearch;
    EditText query;
    View progressBar;

    public static TeacherContactListFragment newInstance(List<ContactModel> contactList) {
        TeacherContactListFragment fragment = new TeacherContactListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.contactList = contactList;
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
        View topView = getView().findViewById(R.id.contact_list_top_view);
        topView.setVisibility(View.GONE);
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
        adapter = new ContactListAdapter(getActivity(),R.layout.row_contact,contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // demo中直接进入聊天页面，实际一般是进入用户详情页
                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUid()));
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

        registerForContextMenu(listView);

        progressBar = (View) getView().findViewById(R.id.progress_bar);

        if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
