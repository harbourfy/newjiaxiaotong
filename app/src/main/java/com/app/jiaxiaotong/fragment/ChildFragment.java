package com.app.jiaxiaotong.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.jiaxiaotong.ChildrenInfoKeeper;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.controller.BaseController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ChildModel;
import com.app.jiaxiaotong.model.StringModel;
import com.app.jiaxiaotong.utils.FileUtils;
import com.app.jiaxiaotong.utils.GlideCircleTransform;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ChildFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChildFragment extends Fragment implements View.OnClickListener, LoadFinishedListener {

//    private View topView,userInfoView;//家长信息顶部视图，用户信息点击视图

    private ImageView topAvatarIv;//家长用户顶部头像，用户头像

    private TextView userNameTv,userSchoolTv,userClassTv;//家长顶部昵称、学校、班级

    private ChildModel childModel;

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0x002;
    private static final int CAMERA_REQUEST_CODE = 0x003;
    private static final int RESULT_REQUEST_CODE = 0x004;
    private String[] items = new String[] { "选择本地图片", "拍照" };
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "JiaXiaoTong.jpg";

    //弹窗口
    private PopupWindow popupWindow;

    private View parentView;

    private int position;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChildFragment.
     */
    public static ChildFragment newInstance(ChildModel childModel,int position) {
        ChildFragment fragment = new ChildFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.childModel = childModel;
        fragment.position = position;
        return fragment;
    }

    public ChildFragment(){

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
        View view = inflater.inflate(R.layout.item_child_info, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        topAvatarIv = (ImageView) view.findViewById(R.id.user_info_avatar_iv);
        userNameTv = (TextView) view.findViewById(R.id.user_info_username_tv);//用户姓名
        userSchoolTv = (TextView) view.findViewById(R.id.user_info_user_school_tv);//用户所在学校
        userClassTv = (TextView) view.findViewById(R.id.user_info_class_tv);//用户所在班级
        if (childModel.getGender().equalsIgnoreCase("m")) {
            userNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.user_icon_boy_nor, 0);
        }else {
            userNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.user_icon_girl_nor, 0);
        }
        Glide.with(this)
                .load(ServiceConst.SERVICE_URL + "/api/mobiles/header/" + childModel.getHeader())
                .transform(new GlideCircleTransform(getContext()))
                .placeholder(R.mipmap.child_default_icon)
                .into(topAvatarIv);
        userNameTv.setText(childModel.getName());
        userSchoolTv.setText(childModel.getSchoolName());
        userClassTv.setText(childModel.getClassName());

        /**PopupWindow的界面*/
        View contentView = getActivity().getLayoutInflater()
                .inflate(R.layout.layout_get_photo, null);
        Button takePhotoBtn = (Button) contentView.findViewById(R.id.layout_take_photo_btn);
        Button getPhotoBtn = (Button) contentView.findViewById(R.id.layout_get_photo_btn);
        Button cancleBtn = (Button) contentView.findViewById(R.id.layout_cancle_btn);

        /**初始化PopupWindow*/
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        /**设置PopupWindow弹出和退出时候的动画效果*/
        popupWindow.setAnimationStyle(R.style.animation);
        parentView = view.findViewById(R.id.main);
        parentView.setOnClickListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        takePhotoBtn.setOnClickListener(this);
        getPhotoBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);

        topAvatarIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowManager.LayoutParams lp=getActivity().getWindow().getAttributes();
                lp.alpha=0.7f;
                getActivity().getWindow().setAttributes(lp);
                popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_info_avatar_iv:
                WindowManager.LayoutParams lp= getActivity().getWindow().getAttributes();
                lp.alpha=0.7f;
                getActivity().getWindow().setAttributes(lp);
                popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.layout_cancle_btn:
                popupWindow.dismiss();
                break;
            case R.id.layout_get_photo_btn:
                popupWindow.dismiss();
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*"); // 设置文件类型
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                break;
            case R.id.layout_take_photo_btn:
                popupWindow.dismiss();
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                if (FileUtils.hasSdcard()) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(path, IMAGE_FILE_NAME);
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                }
                getActivity().startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                break;
            case R.id.main:
                popupWindow.dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != BaseActivity.RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (FileUtils.hasSdcard()) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                        File tempFile = new File(path, IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                    } else {
                        ToastUtils.ToastMsg(getActivity(), "未找到存储卡，无法存储照片！");
                    }
                    break;
                case RESULT_REQUEST_CODE: // 图片缩放完成后
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *m
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 340);
        intent.putExtra("outputY", 340);
        intent.putExtra("return-data", true);
        getActivity().startActivityForResult(intent, RESULT_REQUEST_CODE);
    }
    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            // Drawable drawable = new
            // BitmapDrawable(this.getResources(),photo);
//            avatarIv.setImageBitmap(toRoundBitmap(photo));
            Glide.clear(topAvatarIv);
            topAvatarIv.setImageBitmap(toRoundBitmap(photo));
            updataAvatar(photo);
        }
    }

    private void updataAvatar(Bitmap bitmap){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(getActivity()).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_UPDATA_AVATAR);
        reqMap.put("url",ServiceConst.SERVICE_URL + "/open/api/mobiles/header");
        reqMap.put("type","jpg");
        reqMap.put(Constant.UID, childModel.getUid());
        Map<String,Object> bodyMap = new HashMap<>();
//        reqMap.put("old-password", oldEt.getText().toString());
        bodyMap.put("base64", FileUtils.bitmapToString(bitmap));
        BaseController.updataAvatar((BaseActivity) getActivity(), this, reqMap, bodyMap);
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap
     *            传入Bitmap对象
     * @return
     */
    private Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom - 10);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    @Override
    public void loadFinished(BaseModel baseModel) {
        if (baseModel.getCode() == null){
            if (baseModel.getStatus().equalsIgnoreCase(ResultCode.SUCCESS)){
                if (baseModel.getActionType().equalsIgnoreCase(ServiceConst.SERVICE_UPDATA_AVATAR)){
                    ChildrenInfoKeeper.writeUserAvatar(getActivity(),((StringModel)baseModel).getResult(),position);
                    ToastUtils.ToastMsg(getActivity(),"修改头像成功！");
                }
            }else {
                ToastUtils.ToastMsg(getActivity(),baseModel.getMessage());
            }
        }else ToastUtils.ToastMsg(getActivity(),baseModel.getMsg());
    }
}
