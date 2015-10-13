package com.app.jiaxiaotong.data;

import android.util.Log;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.LoginModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.AuthUtils;
import com.app.jiaxiaotong.utils.JsonUtil;
import com.app.jiaxiaotong.utils.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author ekfans
 * 
 */
public class DataController {

	public static Object getModelFromService(Map<String, Object> params,Map<String, Object> bodyMap) {
		// 如果传过来的参数Map为空，或者长度小于或等于0则返回空
		if (params == null || params.size() <= 0) {
			return new BaseModel();
		}
		// 从参数Map中获取source的值
		String sourceCode = (String) params.get("source");
		// 如果获取的SOURCE值为空，则返回null
		if (StringUtil.isEmpty(sourceCode)) {
			return new BaseModel();
		}
		try {
//			jsonStr = JsonUtil.convertToJsonString(params);
//			// TODO:对json加密
//			// jsonStr ＝ DESC。。。。
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("AllParam", jsonStr);
//			System.out.println("请求参数：" + jsonStr);
			// 调用方法访问服务器，获取返回数据
			String returnMsg = OkHttpUtil.getStringFromServerWithMoreHeader(params,bodyMap);
			Log.i(Constant.LOG_KEY,"返回数据" + returnMsg);
//			String returnMsg = "";
			// 如果返回的数据为空，则返回null
			if (StringUtil.isEmpty(returnMsg)) {
				return new BaseModel();
			}

			// 定义返回model
			Object model = null;

			// 调用方法解析JSON
//			model = JSONHelper.covertJson(sourceCode, returnMsg);
			
			String modelName = ServiceConst.codeModelMap.get(sourceCode);
			Class clazz = Class.forName(modelName);
			if (StringUtil.isEmpty(AuthUtils.getResult(returnMsg)))
				model = JsonUtil.readJson2Entity(returnMsg, clazz);
			else
				model = JsonUtil.readJson2Entity(AuthUtils.getResult(returnMsg), clazz);

			return model;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return new BaseModel();
	}

	public static Object postModelFromService(Map<String, Object> params,Map<String,Object> bodyMap) {
		// 如果传过来的参数Map为空，或者长度小于或等于0则返回空
		if (params == null || params.size() <= 0) {
			return new BaseModel();
		}
		// 从参数Map中获取source的值
		String sourceCode = (String) params.get("source");
		// 如果获取的SOURCE值为空，则返回null
		if (StringUtil.isEmpty(sourceCode)) {
			return new BaseModel();
		}

		String jsonStr;
		try {
//			jsonStr = JsonUtil.convertToJsonString(params);
			// TODO:对json加密
			// jsonStr ＝ DESC。。。。
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("AllParam", jsonStr);
//			System.out.println("请求参数：" + jsonStr);
			// 调用方法访问服务器，获取返回数据
			String returnMsg = OkHttpUtil.post(params,bodyMap);
			Log.i(Constant.LOG_KEY,"返回数据" + returnMsg);
//			String returnMsg = "";
			// 如果返回的数据为空，则返回null
			if (StringUtil.isEmpty(returnMsg)) {
				return new BaseModel();
			}

			// 定义返回model
			Object model = null;

			// 调用方法解析JSON
//			model = JSONHelper.covertJson(sourceCode, returnMsg);

			String modelName = ServiceConst.codeModelMap.get(sourceCode);
			Class clazz = Class.forName(modelName);
			if (StringUtil.isEmpty(AuthUtils.getResult(returnMsg)))
				model = JsonUtil.readJson2Entity(returnMsg, clazz);
			else
				model = JsonUtil.readJson2Entity(AuthUtils.getResult(returnMsg), clazz);
			return model;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return new BaseModel();
	}

	public static Object postFileModelFromService(Map<String, Object> params,Map<String,Object> body) {
		// 如果传过来的参数Map为空，或者长度小于或等于0则返回空
		if (params == null || params.size() <= 0) {
			return new BaseModel();
		}
		// 从参数Map中获取source的值
		String sourceCode = (String) params.get("source");
		// 如果获取的SOURCE值为空，则返回null
		if (StringUtil.isEmpty(sourceCode)) {
			return new BaseModel();
		}

		String jsonStr;
		try {
			jsonStr = JsonUtil.convertToJsonString(params);
			// TODO:对json加密
			// jsonStr ＝ DESC。。。。
			Map<String, String> map = new HashMap<String, String>();
			map.put("AllParam", jsonStr);
			System.out.println("请求参数：" + jsonStr);
			// 调用方法访问服务器，获取返回数据
			String returnMsg = OkHttpUtil.postStringFromServerWithMoreHeader(params,body);
//			String returnMsg = "";
			// 如果返回的数据为空，则返回null
			if (StringUtil.isEmpty(returnMsg)) {
				return new BaseModel();
			}

			// 定义返回model
			Object model = null;

			// 调用方法解析JSON
//			model = JSONHelper.covertJson(sourceCode, returnMsg);

			String modelName = ServiceConst.codeModelMap.get(sourceCode);
			Class clazz = Class.forName(modelName);
			if (StringUtil.isEmpty(AuthUtils.getResult(returnMsg)))
				model = JsonUtil.readJson2Entity(returnMsg, clazz);
			else
				model = JsonUtil.readJson2Entity(AuthUtils.getResult(returnMsg), clazz);
			return model;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return new BaseModel();
	}

	public static Object LoginToServer(Map<String, Object> params,Map<String, Object> bodyMap){
		// 如果传过来的参数Map为空，或者长度小于或等于0则返回空
		if (params == null || params.size() <= 0) {
			return new BaseModel();
		}
		// 从参数Map中获取source的值
		String url = (String) params.get("url");
		try {
			String returnMsg = OkHttpUtil.getStringFromServerWithMoreHeader(params,bodyMap);
			if (StringUtil.isEmpty(returnMsg)) {
				return new BaseModel();
			}

			// 定义返回model
			Object model = null;
			model = JsonUtil.readJson2Entity(returnMsg, LoginModel.class);
			return model;
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return new BaseModel();
	}
}
