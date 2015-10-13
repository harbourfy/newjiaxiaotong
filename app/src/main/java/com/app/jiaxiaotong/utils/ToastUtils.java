package com.app.jiaxiaotong.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	/**
	 * 打印结果
	 * @param context
	 * @param msg
	 */
	public static void ToastMsg(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
}
