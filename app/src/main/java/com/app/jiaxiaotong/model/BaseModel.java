package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2015/8/24.
 */
public class BaseModel {

    private String actionType;
    //auth登录信息
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "msg")
    private String msg;
    //end
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "message")
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
