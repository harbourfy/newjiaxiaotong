package com.app.jiaxiaotong.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.im.utils.ImageUtils;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.StringModel;
import com.app.jiaxiaotong.utils.FileUtils;
import com.app.jiaxiaotong.utils.GlideCircleTransform;
import com.app.jiaxiaotong.utils.ToastUtils;
import com.app.jiaxiaotong.utils.ToolBarUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChangeInfoActivity extends BaseActivity implements View.OnClickListener,LoadFinishedListener{

    private ChangeInfoActivity activity = ChangeInfoActivity.this;

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 3;
    private static final int RESULT_REQUEST_CODE = 4;
    private static final int IDENTITY_REQUEST_CODE = 5;
    private static final int CHANGE_TEL_REQUEST_CODE = 6;
    private static final int CHANGE_USERNAME_REQUEST_CODE = 7;
    private String[] items = new String[] { "选择本地图片", "拍照" };
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "JiaXiaoTong.jpg";

    //弹窗口
    private PopupWindow popupWindow;

    private View parentView;

    private ImageView avatarIv;
    private TextView identityTv;//家长身份
    private TextView userNameTv;//家长名字
    private TextView telTv;//电话号码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        ToolBarUtils.initToolBar(activity,"修改资料");
        initView();
    }

    private void initView() {
        View avatarView = findViewById(R.id.change_info_avatar_layout);
//        View nameView = findViewById(R.id.change_info_username_layout);
        View identityView =  findViewById(R.id.change_info_identity_layout);
        View phoneView = findViewById(R.id.change_info_tel_layout);
        View changePasswordView = findViewById(R.id.change_info_change_password_layout);
        View changeChildPasswordView = findViewById(R.id.change_info_change_child_password_layout);
        View schoolNameView = findViewById(R.id.change_info_school_name_layout);
        if (UserInfoKeeper.readUserInfo(activity).getType().equalsIgnoreCase(Constant.FAMILY)){
            schoolNameView.setVisibility(View.GONE);
        }else{
            identityView.setVisibility(View.GONE);
            changeChildPasswordView.setVisibility(View.GONE);
            TextView schoolNameTv = (TextView) findViewById(R.id.change_info_school_name_tv);
            if (!TextUtils.isEmpty(UserInfoKeeper.readUserInfo(activity).getSchoolName())){
                schoolNameTv.setText(UserInfoKeeper.readUserInfo(activity).getSchoolName());
            }else{
                schoolNameTv.setText("未知");
            }
        }
        identityTv = (TextView) findViewById(R.id.change_info_identity_tv);
        avatarIv = (ImageView) findViewById(R.id.change_info_avatar_iv);
        userNameTv = (TextView) findViewById(R.id.change_info_nickname_tv);
        telTv = (TextView) findViewById(R.id.change_info_tel_tv);
        telTv.setText(UserInfoKeeper.readUserInfo(activity).getMobilePhone());
        userNameTv.setText(UserInfoKeeper.readUserInfo(activity).getName());
        Glide.with(activity)
                .load(UserInfoKeeper.readUserInfo(activity).getAvatar())
                .transform(new GlideCircleTransform(activity))
                .placeholder(R.mipmap.father_default_icon)
                .into(avatarIv);
        avatarView.setOnClickListener(this);
//        nameView.setOnClickListener(this);
        identityView.setOnClickListener(this);
        phoneView.setOnClickListener(this);
        changeChildPasswordView.setOnClickListener(this);
        changePasswordView.setOnClickListener(this);
        if (UserInfoKeeper.readUserInfo(activity).getRelation().equalsIgnoreCase("father"))
            identityTv.setText("爸爸");
        else if(UserInfoKeeper.readUserInfo(activity).getRelation().equalsIgnoreCase("mother"))
            identityTv.setText("妈妈");
        else if(UserInfoKeeper.readUserInfo(activity).getRelation().equalsIgnoreCase("unkown"))
            identityTv.setText("未知");
        else identityView.setVisibility(View.GONE);
        /**PopupWindow的界面*/
        View contentView = getLayoutInflater()
                .inflate(R.layout.layout_get_photo, null);
        Button takePhotoBtn = (Button) contentView.findViewById(R.id.layout_take_photo_btn);
        Button getPhotoBtn = (Button) contentView.findViewById(R.id.layout_get_photo_btn);
        Button cancleBtn = (Button) contentView.findViewById(R.id.layout_cancle_btn);

        /**初始化PopupWindow*/
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        /**设置PopupWindow弹出和退出时候的动画效果*/
        popupWindow.setAnimationStyle(R.style.animation);
        parentView = this.findViewById(R.id.change_info_main);
        parentView.setOnClickListener(this);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        takePhotoBtn.setOnClickListener(this);
        getPhotoBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (popupWindow.isShowing())
            popupWindow.dismiss();
        switch (v.getId()){
            case R.id.change_info_avatar_layout:
                WindowManager.LayoutParams lp=getWindow().getAttributes();
                lp.alpha=0.7f;
                getWindow().setAttributes(lp);
                popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.change_info_identity_layout:
                startActivityForResult(new Intent(activity,IdentityActivity.class),IDENTITY_REQUEST_CODE);
                break;
            case R.id.change_info_tel_layout:
                startActivity(new Intent(activity,ChangeTelActivity.class));
                break;
            case R.id.change_info_change_password_layout:
                startActivity(new Intent(activity,ChangePasswordActivity.class));
                break;
            case R.id.change_info_change_child_password_layout:
                startActivity(new Intent(activity,ChangePasswordActivity.class).putExtra("type",1));
                break;
            case R.id.layout_cancle_btn:

                break;
            case R.id.layout_get_photo_btn:
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*"); // 设置文件类型
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                break;
            case R.id.layout_take_photo_btn:
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                if (FileUtils.hasSdcard()) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(path, IMAGE_FILE_NAME);
                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                }
                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
                break;
            case R.id.main:
                popupWindow.dismiss();
                break;
        }
    }

    private void updataAvatar(Bitmap bitmap){
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(activity).getToken());
        reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_UPDATA_AVATAR);
        reqMap.put("url",ServiceConst.SERVICE_URL + "/open/api/mobiles/header");
        reqMap.put("type","jpg");
        reqMap.put(Constant.UID,UserInfoKeeper.readUserInfo(activity).getUid());
        Map<String,Object> bodyMap = new HashMap<>();
//        reqMap.put("old-password", oldEt.getText().toString());
        bodyMap.put("base64", FileUtils.bitmapToString(bitmap));
        BaseController.updataAvatar(activity, this, reqMap,bodyMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

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
                        ToastUtils.ToastMsg(activity, "未找到存储卡，无法存储照片！");
                    }
                    break;
                case RESULT_REQUEST_CODE: // 图片缩放完成后
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                case IDENTITY_REQUEST_CODE:
                    if (data.getExtras().getString("type").equalsIgnoreCase(IdentityActivity.IDENTITY_MOTHER))
                        identityTv.setText("妈妈");
                    else identityTv.setText("爸爸");
                    break;
                case CHANGE_USERNAME_REQUEST_CODE:
                    userNameTv.setText(data.getExtras().getString(EditInfoActivity.INFO));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
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
        startActivityForResult(intent, RESULT_REQUEST_CODE);
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
            Glide.clear(avatarIv);
            avatarIv.setImageBitmap(toRoundBitmap(photo));
            updataAvatar(photo);
        }
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
                    UserInfoKeeper.writeUserAvatar(activity,((StringModel)baseModel).getResult());
                    ToastUtils.ToastMsg(activity,"修改头像成功！");
                }
            }else {
                ToastUtils.ToastMsg(activity,baseModel.getMessage());
            }
        }else ToastUtils.ToastMsg(activity,baseModel.getMsg());
    }
}
