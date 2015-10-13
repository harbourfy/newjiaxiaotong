package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2015/9/6.
 */
public class ChangeTelModel extends BaseModel {
    @JsonProperty(value = "responseData")
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
