package com.app.jiaxiaotong.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import com.app.jiaxiaotong.ChildrenInfoKeeper;
import com.app.jiaxiaotong.LoginInfoKeeper;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.LoginActivity;
import com.easemob.chat.EMChatManager;

public class DialogUtils {
	
	
	public static void loginDialog(final Context context){
		Builder builder = new Builder(context);
		builder.setTitle("登录已过期");
		builder.setMessage("亲，您的账号登录已过期，需要重新登录才可以哦！");
		builder.setPositiveButton("重新登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserInfoKeeper.clearInfo(context);
				LoginInfoKeeper.clearInfo(context);
				ChildrenInfoKeeper.clearInfo(context);
				EMChatManager.getInstance().logout();
				context.startActivity(new Intent(context, LoginActivity.class));
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
	public static void loginDialogForResult(final Activity context){
		Builder builder = new Builder(context);
		builder.setTitle("登录已过期");
		builder.setMessage("亲，您的账号登录已过期，需要重新登录才可以哦！");

		builder.setPositiveButton("重新登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UserInfoKeeper.clearInfo(context);
				LoginInfoKeeper.clearInfo(context);
				ChildrenInfoKeeper.clearInfo(context);
				EMChatManager.getInstance().logout();
				context.startActivityForResult(new Intent(context, LoginActivity.class),LoginActivity.LOGIN_REQ);

			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
	/**
	 * 首次登录对话提示框
	 * @param context
	 */
	public static void loginFristTimeDialog(final Context context){
		Builder builder = new Builder(context);
		builder.setTitle("未登录");
		builder.setMessage("亲，您还未登录哦！");
		builder.setPositiveButton("登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new Intent(context, LoginActivity.class));
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	/**
	 * 首次登录获取登录结果提示对话框
	 * @param context
	 */
	public static void loginFristTimeDialogForResult(final Activity context){
		Builder builder = new Builder(context);
		builder.setTitle("未登录");
		builder.setMessage("亲，您还未登录！");
		builder.setPositiveButton("登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivityForResult(new Intent(context, LoginActivity.class),LoginActivity.LOGIN_REQ);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	
}
