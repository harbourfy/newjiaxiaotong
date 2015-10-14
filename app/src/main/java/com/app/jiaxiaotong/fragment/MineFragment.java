package com.app.jiaxiaotong.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.ChildrenInfoKeeper;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.activity.ChangeInfoActivity;
import com.app.jiaxiaotong.activity.SettingActivity;
import com.app.jiaxiaotong.activity.UserInfoActivity;
import com.app.jiaxiaotong.adapter.ChildViewPagerAdapter;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChildModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.GlideCircleTransform;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends Fragment implements View.OnClickListener, LoadFinishedListener {

    private ProgressDialog dialog;

    private ChildViewPagerAdapter adapter;

    private ViewPager viewPager;

    private ImageView leftIv,rightIv;//左右滑动按钮



    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MineFragment() {
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
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        return view;
    }

    private View topView,userInfoView;//家长信息顶部视图，用户信息点击视图

    private ImageView avatarIv;//用户头像

    private TextView nickNameTv,telTv,settingTv;

    private void initView(View view) {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("正在加载孩子信息...");
        topView = view.findViewById(R.id.mine_user_info_top_layout);
        avatarIv = (ImageView) view.findViewById(R.id.mine_user_avatar_iv);
        nickNameTv = (TextView) view.findViewById(R.id.mine_user_nickname_tv);
        telTv = (TextView) view.findViewById(R.id.mine_user_tel_tv);
        settingTv = (TextView) view.findViewById(R.id.mine_user_setting_tv);
        userInfoView = view.findViewById(R.id.mine_user_info_layout);
        viewPager = (ViewPager) view.findViewById(R.id.mine_child_info_pager);
        leftIv = (ImageView) view.findViewById(R.id.user_info_top_go_left);
        rightIv = (ImageView) view.findViewById(R.id.user_info_top_go_right);
        userInfoView.setOnClickListener(this);
        settingTv.setOnClickListener(this);
        if (UserInfoKeeper.readUserInfo(getActivity()).getType().equalsIgnoreCase(Constant.TEACHER)){
            topView.setVisibility(View.GONE);
        }else{
            if (ChildrenInfoKeeper.readUserInfo(getActivity()) != null && ChildrenInfoKeeper.readUserInfo(getActivity()).size() > 0){
                readChildInfo();
            }else
                getChildInfo();
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter.getCount() == 1){
                    rightIv.setVisibility(View.GONE);
                    leftIv.setVisibility(View.GONE);
                }else if(adapter.getCount() == 2){
                    if (position == 0){
                        rightIv.setVisibility(View.VISIBLE);
                        leftIv.setVisibility(View.GONE);
                    }else{
                        rightIv.setVisibility(View.GONE);
                        leftIv.setVisibility(View.VISIBLE);
                    }
                }else {
                    if (position == 0){
                        rightIv.setVisibility(View.VISIBLE);
                        leftIv.setVisibility(View.GONE);
                    }else if (position == (adapter.getCount() - 1)){
                        rightIv.setVisibility(View.GONE);
                        leftIv.setVisibility(View.VISIBLE);
                    }else {
                        rightIv.setVisibility(View.VISIBLE);
                        leftIv.setVisibility(View.VISIBLE);
                    }
                }
                    ;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void readChildInfo() {
        List<ChildModel> childModels = ChildrenInfoKeeper.readUserInfo(getActivity());
        List<ChildFragment> childFragments = new ArrayList<>();
        if (childModels != null && childModels.size() > 0) {
            for (int i = 0;i < childModels.size();i++) {
                ChildFragment childFragment = ChildFragment.newInstance(childModels.get(i),i);
                childFragments.add(childFragment);
            }
            adapter = new ChildViewPagerAdapter(getChildFragmentManager(), childFragments);
            viewPager.setAdapter(adapter);
            if (childModels != null && childModels.size() > 1)
                rightIv.setVisibility(View.VISIBLE);
        }
    }

    private void setUserData(){
        UserModel userModel = UserInfoKeeper.readUserInfo(getActivity());
        Glide.with(this).load(userModel.getAvatar()).transform(new GlideCircleTransform(getActivity())).placeholder(R.mipmap.father_default_icon).into(avatarIv);
        nickNameTv.setText(userModel.getName());
        telTv.setText(LoginInfoKeeper.readUserInfo(getActivity()).getUid());
    }

    @Override
    public void onResume() {
        super.onResume();
        setUserData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_user_info_layout:
//                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                startActivity(new Intent(getActivity(), ChangeInfoActivity.class));
                break;
            case R.id.mine_user_setting_tv:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.user_info_top_go_left:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.user_info_top_go_right:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
        }
    }

    /**
     * 获取孩子信息
     */
    private void getChildInfo(){
        dialog.show();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(getActivity()).getToken());
        reqMap.put(Constant.UID,UserInfoKeeper.readUserInfo(getActivity()).getUid());
        reqMap.put("mobile","android");
        reqMap.put(Constant.URL, ServiceConst.SERVICE_URL + "/open/api/mobiles/family/children");
        reqMap.put(Constant.SOURCE,ServiceConst.SERVICE_GET_CHILD_LIST_INFO);
        BaseController.getChildInfo((BaseActivity) getActivity(), this, reqMap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getChildFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        dialog.dismiss();
        if (baseModel != null) {
            if (baseModel.getCode() == null) {
                if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)) {
                    if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_GET_CHILD_LIST_INFO)) {
                        List<ChildModel> childModels = ((ChildModel) baseModel).getChilds();
                        ChildrenInfoKeeper.writeChildrenInfo(getActivity(), childModels);
                        List<ChildFragment> childFragments = new ArrayList<>();
                        if (childModels != null && childModels.size() > 0) {
                            if (childModels != null && childModels.size() > 1) {
                                rightIv.setVisibility(View.VISIBLE);
                            }
                            for (int i = 0; i < childModels.size(); i++) {
                                ChildFragment childFragment = ChildFragment.newInstance(childModels.get(i), i);
                                childFragments.add(childFragment);
                            }
                            adapter = new ChildViewPagerAdapter(getChildFragmentManager(), childFragments);
                            viewPager.setAdapter(adapter);
                        }


                    }
                }
            }
        }
    }
}
