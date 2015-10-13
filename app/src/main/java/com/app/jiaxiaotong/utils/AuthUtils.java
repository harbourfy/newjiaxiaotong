package com.app.jiaxiaotong.utils;

import com.app.jiaxiaotong.data.ResultCode;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/9/3.
 */
public class AuthUtils {

    public static String getResult(String response){
        String result = "";
        if (StringUtil.isEmpty(response))
            return "";
        try {
            JSONObject object = new JSONObject(response);
            String code = object.optString("code");
            if (code.equalsIgnoreCase(ResultCode.OK)){
                result = object.getString("result");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
