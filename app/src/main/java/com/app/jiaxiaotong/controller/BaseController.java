package com.app.jiaxiaotong.controller;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.app.jiaxiaotong.Constant;
import com.app.jiaxiaotong.UserInfoKeeper;
import com.app.jiaxiaotong.activity.BaseActivity;
import com.app.jiaxiaotong.data.DataController;
import com.app.jiaxiaotong.data.ResultCode;
import com.app.jiaxiaotong.data.ServiceConst;
import com.app.jiaxiaotong.fragment.MineFragment;
import com.app.jiaxiaotong.listener.LoadFinishedListener;
import com.app.jiaxiaotong.model.BaseModel;
import com.app.jiaxiaotong.model.UserModel;
import com.app.jiaxiaotong.utils.ToastUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2015/8/24.
 */
public class BaseController {
    /**
     * 登录
     * @param context
     * @param loadFinished
     * @param reqMap
     */
    public static void login(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new LoginAsyncTask(context,loadFinished, ServiceConst.SERVICE_LOGIN).execute(reqMap);
    }

    public static void getUserInfo(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_USERINFO).execute(reqMap);
    }

    public static void getAuthCode(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_AUTHCODE).execute(reqMap);
    }
    public static void verifyAuthCode(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_VERIFY_AUTHCODE).execute(reqMap);
    }

    public static void forgetPassword(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_FORGET_PASSWORD).execute(reqMap);
    }

    public static void changePassword(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new PostAsyncTask(context,loadFinished, ServiceConst.SERVICE_CHANGE_PASSWORD).execute(reqMap);
    }

    public static void updataAvatar(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap,Map<String, Object> body){
        new PostAsyncTask(context,loadFinished, ServiceConst.SERVICE_UPDATA_AVATAR).execute(reqMap,body);
    }
    public static void changeTel(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new PostAsyncTask(context,loadFinished, ServiceConst.SERVICE_CHANGE_TEL).execute(reqMap);
    }
    /**
     * 改变身份
     * @param context
     * @param loadFinished
     * @param reqMap
     */
    public static void changeIdentity(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new PostAsyncTask(context,loadFinished, ServiceConst.SERVICE_CHANGE_IDENTITY).execute(reqMap);
    }

    //*获取家长的通讯录列表
    public static void getFamilyContactList(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_FAMILY_CONTACT_LIST).execute(reqMap);
    }
    //获取教师班级列表
    public static void getTeacherClassList(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_TEACHER_CLASS_LIST).execute(reqMap);
    }
    //获取教师班级联系人列表
    public static void getTeacherClassContactList(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_TEACHER_CONTACT_LIST).execute(reqMap);
    }
    public static void getServiceTel(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_SERVICE_TEL).execute(reqMap);
    }
    public static void getChildInfo(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_CHILD_LIST_INFO).execute(reqMap);
    }

    public static void reportMessage(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap,Map<String, Object> body){
        new PostAsyncTask(context,loadFinished, ServiceConst.SERVICE_REPORT_MESSAGE).execute(reqMap,body);
    }

    /**
     * 获取推送消息
     * @param context
     * @param loadFinished
     * @param reqMap
     */
    public static void getPushMsg(BaseActivity context,LoadFinishedListener loadFinished,Map<String, Object> reqMap){
        new GetAsyncTask(context,loadFinished, ServiceConst.SERVICE_GET_PUSH_MSG).execute(reqMap);
    }

    private static class LoginAsyncTask extends AsyncTask<Map<String,Object>,Void,BaseModel>{
        private LoadFinishedListener loadFinished;

        private String actionType;
        private BaseActivity context;

        public LoginAsyncTask(BaseActivity context,LoadFinishedListener loadFinished,String actionType){
            this.loadFinished = loadFinished;
            this.actionType = actionType;
            this.context = context;
        }

        @Override
        protected BaseModel doInBackground(Map<String, Object>... params) {
            BaseModel baseModel = null;
            if (params.length == 1){

                baseModel = (BaseModel) DataController.getModelFromService(params[0], null);
            }else if (params.length == 2){
                baseModel = (BaseModel) DataController.getModelFromService(params[0],params[1]);
            }


            return baseModel;
        }

        @Override
        protected void onPostExecute(BaseModel result) {
            super.onPostExecute(result);
            if (loadFinished != null && result != null) {
                if (result.getCode() == null){
                    result.setActionType(actionType);
                    loadFinished.loadFinished(result);
                }else{
                    loadFinished.loadFinished(result);
                    ToastUtils.ToastMsg(context, result.getMsg());
                }
            }else{
                loadFinished.loadFinished(null);
                ToastUtils.ToastMsg(context, "服务器异常");
            }
        }
    }

    /**
     * get方式从服务器获取数据
     */
    private static class GetAsyncTask extends AsyncTask<Map<String,Object>,Void,BaseModel>{
        private LoadFinishedListener loadFinished;

        private String actionType;
        private BaseActivity context;

        public GetAsyncTask(BaseActivity context,LoadFinishedListener loadFinished,String actionType){
            this.loadFinished = loadFinished;
            this.actionType = actionType;
            this.context = context;
        }

        @Override
        protected BaseModel doInBackground(Map<String, Object>... params) {
            BaseModel baseModel = null;
            if (params.length == 1){

                baseModel = (BaseModel) DataController.getModelFromService(params[0], null);
            }else if (params.length == 2){
                baseModel = (BaseModel) DataController.getModelFromService(params[0],params[1]);
            }
            return baseModel;
        }

        @Override
        protected void onPostExecute(BaseModel result) {
            super.onPostExecute(result);
            if (loadFinished != null && result != null) {
                if (result.getCode() == null){
                    result.setActionType(actionType);
                    loadFinished.loadFinished(result);
                }else{
                    loadFinished.loadFinished(result);
                    ToastUtils.ToastMsg(context, result.getMsg());
                }
            }else{
                loadFinished.loadFinished(null);
                ToastUtils.ToastMsg(context, "服务器异常");
            }
        }
    }

    /**
     * post方式从服务器获取数据
     */
    private static class PostAsyncTask extends AsyncTask<Map<String,Object>,Void,BaseModel>{
        private LoadFinishedListener loadFinished;

        private String actionType;

        private BaseActivity activity;

        public PostAsyncTask(BaseActivity activity,LoadFinishedListener loadFinished,String actionType){
            this.loadFinished = loadFinished;
            this.actionType = actionType;
            this.activity = activity;
        }

        @Override
        protected BaseModel doInBackground(Map<String, Object>... params) {
            BaseModel baseModel = null;
            if (params.length == 1){

                baseModel = (BaseModel) DataController.postModelFromService(params[0], null);
            }else if (params.length == 2){
                baseModel = (BaseModel) DataController.postModelFromService(params[0], params[1]);
            }


            return baseModel;
        }

        @Override
        protected void onPostExecute(BaseModel result) {
            super.onPostExecute(result);
            if (loadFinished != null && result != null) {
                if (result.getCode() == null){
                    result.setActionType(actionType);
                    loadFinished.loadFinished(result);
                }else{
                    loadFinished.loadFinished(result);
                    ToastUtils.ToastMsg(activity, result.getMsg());
                }
            }else{
                loadFinished.loadFinished(null);
                ToastUtils.ToastMsg(activity, "服务器异常");
            }
        }
    }
}
