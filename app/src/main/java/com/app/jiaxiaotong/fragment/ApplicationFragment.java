package com.app.jiaxiaotong.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.PushMsgKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.activity.MainActivity;
import com.app.jiaxiaotong.activity.MainActivity.RefreshListener;
import com.app.jiaxiaotong.activity.WebViewActivity;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.listener.OnMessageIsReadListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.PushMsgModel;
import com.app.jiaxiaotong.utils.DialogUtils;
import com.app.jiaxiaotong.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ApplicationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApplicationFragment extends Fragment implements View.OnClickListener, LoadFinishedListener,RefreshListener {

    private TextView checkInTv,noticeTv;

    private ImageView checkInUnreadIv,noticeUnreadIv;

    public static ApplicationFragment newInstance() {
        ApplicationFragment fragment = new ApplicationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ApplicationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application, container, false);
        initView(view);
//        getPushMsg();
        return view ;
        
    }

    private void initView(View view) {
        View checkInLayout = view.findViewById(R.id.application_checking_in_layout);
        View  consumeLayout = view.findViewById(R.id.application_consume_layout);
        View classNoticeLayout = view.findViewById(R.id.application_class_notice_layout);
        checkInTv = (TextView) view.findViewById(R.id.application_checking_in_desc_tv);
        checkInUnreadIv = (ImageView) view.findViewById(R.id.application_checking_in_unread_iv);
        noticeTv = (TextView) view.findViewById(R.id.application_class_notice_desc_tv);
        noticeUnreadIv = (ImageView) view.findViewById(R.id.application_class_notice_unread_iv);
        if (!PushMsgKeeper.readPushMsg(getActivity()).isreadClockin()){
            checkInUnreadIv.setVisibility(View.VISIBLE);
        }
        if (!PushMsgKeeper.readPushMsg(getActivity()).isreadAnnouncement()){
            noticeUnreadIv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(PushMsgKeeper.readPushMsg(getActivity()).getClockin()))
            checkInTv.setText(PushMsgKeeper.readPushMsg(getActivity()).getClockin());
        if (!TextUtils.isEmpty(PushMsgKeeper.readPushMsg(getActivity()).getAnnouncement()))
            noticeTv.setText(PushMsgKeeper.readPushMsg(getActivity()).getAnnouncement());
        checkInLayout.setOnClickListener(this);
        consumeLayout.setOnClickListener(this);
        classNoticeLayout.setOnClickListener(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.application_checking_in_layout:
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("type", 0));
                PushMsgKeeper.clearCheckIn(getActivity());
                if (checkInUnreadIv.getVisibility() == View.VISIBLE){
                    checkInUnreadIv.setVisibility(View.GONE);
                }
                if (PushMsgKeeper.isEmpty(getActivity())){
                    onMessageIsReadListener.messageIsRead(true);
                }
                break;
            case R.id.application_class_notice_layout:
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("type",1));
                PushMsgKeeper.clearNotice(getActivity());
                if (noticeUnreadIv.getVisibility() == View.VISIBLE){
                    noticeUnreadIv.setVisibility(View.GONE);
                }
                if (PushMsgKeeper.isEmpty(getActivity())){
                    onMessageIsReadListener.messageIsRead(true);
                }
                break;
//            case R.id.application_consume_layout:
//
//                break;
        }
    }
    /**
     * 获取推送消息
     */
    private void getPushMsg(){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(getActivity()).getToken());
        reqMap.put(Constant.UID, UserInfoKeeper.readUserInfo(getActivity()).getUid());
        reqMap.put("url", ServiceConst.SERVICE_URL + "/open/api/mobiles/pushedmsg");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_PUSH_MSG);
        BaseController.getPushMsg((BaseActivity) getActivity(), this, reqMap);
    }
    @Override
    public void loadFinished(BaseModel baseModel) {
        if (baseModel.getCode() == null){
            if(baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_PUSH_MSG)) {
                    PushMsgModel oldMsg = PushMsgKeeper.readPushMsg(getActivity());
                    PushMsgModel newMsg = ((PushMsgModel) baseModel).getDetails();
                    if (TextUtils.isEmpty(oldMsg.getClockin())){
                        checkInTv.setText(newMsg.getClockin());
                        checkInUnreadIv.setVisibility(View.VISIBLE);
                    }else if (!TextUtils.isEmpty(oldMsg.getClockin()) && !oldMsg.getClockin().equalsIgnoreCase(newMsg.getClockin())){
                        checkInTv.setText(newMsg.getClockin());
                        checkInUnreadIv.setVisibility(View.VISIBLE);
                    }
                    if (TextUtils.isEmpty(oldMsg.getAnnouncement())){
                        noticeTv.setText(newMsg.getClockin());
                        noticeUnreadIv.setVisibility(View.VISIBLE);
                    }else if (!TextUtils.isEmpty(oldMsg.getAnnouncement()) && !oldMsg.getAnnouncement().equalsIgnoreCase(newMsg.getAnnouncement())){
                        noticeTv.setText(newMsg.getClockin());
                        noticeUnreadIv.setVisibility(View.VISIBLE);
                    }
                    PushMsgKeeper.writePushInfo(getActivity(),newMsg);
//                    if (PushMsgKeeper.isEmpty(getActivity())){
//                        onMessageIsReadListener.messageIsRead(false);
//                    }
                }
            }else ToastUtils.ToastMsg(getActivity(), baseModel.getMessage());
        }else if (baseModel.getCode().equalsIgnoreCase(Constant.TOEKN_EXPIRE)){//登录过期
            DialogUtils.loginDialog(getActivity());
        }else {
            ToastUtils.ToastMsg(getActivity(),baseModel.getMessage());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onMessageIsReadListener = (OnMessageIsReadListener) activity;
    }

    private OnMessageIsReadListener onMessageIsReadListener;

    public OnMessageIsReadListener getOnMessageIsReadListener() {
        return onMessageIsReadListener;
    }

    public void setOnMessageIsReadListener(OnMessageIsReadListener onMessageIsReadListener) {
        this.onMessageIsReadListener = onMessageIsReadListener;
    }

    @Override
    public void onRefresh() {
        getPushMsg();
    }


}
