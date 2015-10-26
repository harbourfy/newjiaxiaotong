package com.app.jiaxiaotong.data;

import java.util.HashMap;
import java.util.Map;

public class ServiceConst {
	
	public static final String SERVICE_URL = "http://121.40.78.2:7890";

	public static final String SERVICE_LOGIN = "001";//登录

	public static final String SERVICE_GET_USERINFO = "002";//获取用户信息

	public static final String SERVICE_GET_AUTHCODE = "003";//获取修改手机号码验证码

	public static final String SERVICE_VERIFY_AUTHCODE = "004";//验证验证码

	public static final String SERVICE_GET_CHILD_LIST_INFO = "005";//获取孩子列表
	public static final String SERVICE_CHANGE_PASSWORD = "006";//修改密码

	public static final String SERVICE_UPDATA_AVATAR = "007";//修改用户头像

	public static final String SERVICE_CHANGE_IDENTITY = "008";//修改身份

	public static final String SERVICE_FORGET_PASSWORD = "009";//忘记密码

	public static final String SERVICE_CHANGE_TEL = "010";//修改手机号

	public static final String SERVICE_GET_NICK_AND_AVATAR = "011";//获取用户昵称和头像

	public static final String SERVICE_GET_FAMILY_CONTACT_LIST = "100";//获取家长的通讯录列表
	public static final String SERVICE_GET_TEACHER_CLASS_LIST = "101";//获取教师班级列表
	public static final String SERVICE_GET_TEACHER_CONTACT_LIST = "102";//获取教师班级联想列表

	public static final String	 SERVICE_GET_PUSH_MSG = "997";//获取推送的新消息

	public static final String SERVICE_REPORT_MESSAGE = "998";//反馈意见
	public static final String SERVICE_GET_SERVICE_TEL = "999";//获取服务热线

	public static final Map<String,String> codeModelMap = new HashMap<String,String>();
	static{
		//备注：ListModel<T extends BaseModel>泛型T下面有每个位置需要的描述
		//用户接口
		codeModelMap.put(SERVICE_LOGIN,"com.app.jiaxiaotong.model.LoginModel");
		codeModelMap.put(SERVICE_GET_USERINFO,"com.app.jiaxiaotong.model.UserModel");
		codeModelMap.put(SERVICE_GET_AUTHCODE,"com.app.jiaxiaotong.model.BaseModel");
		codeModelMap.put(SERVICE_VERIFY_AUTHCODE,"com.app.jiaxiaotong.model.ChangeTelModel");
		codeModelMap.put(SERVICE_GET_CHILD_LIST_INFO,"com.app.jiaxiaotong.model.ChildModel");
		codeModelMap.put(SERVICE_CHANGE_PASSWORD,"com.app.jiaxiaotong.model.BaseModel");
		codeModelMap.put(SERVICE_UPDATA_AVATAR,"com.app.jiaxiaotong.model.StringModel");
		codeModelMap.put(SERVICE_CHANGE_IDENTITY,"com.app.jiaxiaotong.model.BaseModel");
		codeModelMap.put(SERVICE_FORGET_PASSWORD,"com.app.jiaxiaotong.model.BaseModel");
		codeModelMap.put(SERVICE_GET_FAMILY_CONTACT_LIST,"com.app.jiaxiaotong.model.ContactModel");
		codeModelMap.put(SERVICE_GET_TEACHER_CLASS_LIST,"com.app.jiaxiaotong.model.ContactModel");
		codeModelMap.put(SERVICE_GET_TEACHER_CONTACT_LIST,"com.app.jiaxiaotong.model.ContactModel");
		codeModelMap.put(SERVICE_CHANGE_TEL,"com.app.jiaxiaotong.model.StringModel");
		codeModelMap.put(SERVICE_GET_NICK_AND_AVATAR,"com.app.jiaxiaotong.model.UserModel");


		codeModelMap.put(SERVICE_GET_PUSH_MSG,"com.app.jiaxiaotong.model.PushMsgModel");
		codeModelMap.put(SERVICE_REPORT_MESSAGE,"com.app.jiaxiaotong.model.BaseModel");
		codeModelMap.put(SERVICE_GET_SERVICE_TEL,"com.app.jiaxiaotong.model.StringModel");
	}

}
