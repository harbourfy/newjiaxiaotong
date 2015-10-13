package com.app.jiaxiaotong.im.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.jiaxiaotong.AppContext;
import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.R;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.data.DataController;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.im.MyHXSDKHelper;
import com.app.jiaxiaotong.im.controller.HXSDKHelper;
import com.app.jiaxiaotong.im.db.HXDBManager;
import com.app.jiaxiaotong.im.domain.User;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.ContactModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.GlideCircleTransform;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogRecord;


public class UserUtils {

	/**
	 * 根据username获取相应user，
	 * @param username
	 * @return
	 */
	public static User getUserInfo(final String username,final Context context){
		final User user = ((MyHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
		if(user == null){
			return null;
		}else
			return user;
	}
	/**
	 * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
	 * @param username
	 * @return
	 */
	public static User getUserInfo(String username){
		User user = ((MyHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
		if(user == null){
			user = new User(username);
		}

		if(user != null){
			//demo没有这些数据，临时填充
			if(TextUtils.isEmpty(user.getNick()))
				user.setNick(username);
		}
		return user;
	}
	private static void getUserNick(){

	}
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(final Context context, final String username, final ImageView imageView){
//    	User user = getUserInfo(username,context);
//        if(user != null && user.getAvatar() != null){
//            Glide.with(context).load(user.getAvatar()).transform(new GlideCircleTransform(context)).placeholder(R.mipmap.default_avatar).into(imageView);
//        }else{
//			Glide.with(context).load(R.mipmap.default_avatar).transform(new GlideCircleTransform(context)).into(imageView);
//        }
		if (UserInfoKeeper.readUserInfo(context).getUid().equalsIgnoreCase(username))
			Glide.with(context)
					.load(UserInfoKeeper.readUserInfo(context).getAvatar())
					.transform(new GlideCircleTransform(context))
					.placeholder(R.mipmap.default_avatar)
					.into(imageView);
		else {
			final User user = getUserInfo(username,context);
			if(user != null){
				Glide.with(context)
						.load(user.getAvatar())
						.transform(new GlideCircleTransform(context))
						.placeholder(R.mipmap.default_avatar)
						.into(imageView);
			}else{
				final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						UserModel contactModel = (UserModel) msg.obj;
						Glide.with(context)
								.load(ServiceConst.SERVICE_URL + "/api/mobiles/header/" + contactModel.getAvatar())
								.transform(new GlideCircleTransform(context))
								.placeholder(R.mipmap.default_avatar)
								.into(imageView);
					}
				};
				new Thread(new Runnable() {
					@Override
					public void run() {
						Map<String,Object> reqMap = new HashMap<>();
						reqMap.put("uid",username);
						reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(context).getToken());
						reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_NICK_AND_AVATAR);
						reqMap.put("url",ServiceConst.SERVICE_URL + "/open/api/mobiles/chat/" + username);
						UserModel	baseModel = (UserModel) DataController.getModelFromService(reqMap, null);
						Message msg = new Message();
						msg.obj = baseModel.getDetails();
						handler.sendMessage(msg);
					}
				}).start();
			}
		}
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
//		User user = ((MyHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		UserModel user = UserInfoKeeper.readUserInfo(context);
		if (user != null && user.getAvatar() != null) {
			Glide.with(context).load(user.getAvatar()).transform(new GlideCircleTransform(context)).placeholder(R.mipmap.default_avatar).into(imageView);
		} else {
			Glide.with(context).load(R.mipmap.default_avatar).transform(new GlideCircleTransform(context)).into(imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }
	/**
	 * 设置用户昵称
	 */
	public static void setUserNick(final String username, final TextView textView, final Context context){
		if (UserInfoKeeper.readUserInfo(context).getUid().equalsIgnoreCase(username))
			textView.setText(UserInfoKeeper.readUserInfo(context).getName());
		else {
			User user = getUserInfo(username,context);
			if(user != null){
				textView.setText(user.getNick());
			}else{
				final Handler handler = new Handler(){
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						UserModel contactModel = (UserModel) msg.obj;
						if (TextUtils.isEmpty(contactModel.getName())){
							textView.setText(username);
						}else
							textView.setText(contactModel.getName());
					}
				};
				new Thread(new Runnable() {
					@Override
					public void run() {
						Map<String,Object> reqMap = new HashMap<>();
						reqMap.put("uid",username);
						reqMap.put(Constant.HEADER, LoginInfoKeeper.readUserInfo(context).getToken());
						reqMap.put(Constant.SOURCE, ServiceConst.SERVICE_GET_NICK_AND_AVATAR);
						reqMap.put("url",ServiceConst.SERVICE_URL + "/open/api/mobiles/chat/" + username);
						UserModel	baseModel = (UserModel) DataController.getModelFromService(reqMap, null);
						Message msg = new Message();
						msg.obj = baseModel.getDetails();
						handler.sendMessage(msg);
					}
				}).start();
			}
		}
	}
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView,String userName){
//    	User user = ((MyHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(userName);
    	}
    }

    /**
     * 保存或更新某个用户
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((MyHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}
    
}
