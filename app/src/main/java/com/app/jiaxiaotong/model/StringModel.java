package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2015/9/6.
 */
public class StringModel extends BaseModel {

    @JsonProperty(value = "responseData")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
