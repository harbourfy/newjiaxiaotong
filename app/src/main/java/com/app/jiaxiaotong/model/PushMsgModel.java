package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 2015/9/22.
 * 消息推送信息model
 */
public class PushMsgModel extends BaseModel {
    @JsonProperty(value = "clockin")
    private String clockin;//考勤
    @JsonProperty(value = "announcement")
    private String announcement;
    @JsonProperty(value = "responseData")
    private PushMsgModel details;
    private boolean isreadClockin = true;//考勤是否已读，默认已读
    private boolean isreadAnnouncement = true;//通知是否已读

    public boolean isreadClockin() {
        return isreadClockin;
    }

    public void setIsreadClockin(boolean isreadClockin) {
        this.isreadClockin = isreadClockin;
    }

    public boolean isreadAnnouncement() {
        return isreadAnnouncement;
    }

    public void setIsreadAnnouncement(boolean isreadAnnouncement) {
        this.isreadAnnouncement = isreadAnnouncement;
    }

    public PushMsgModel getDetails() {
        return details;
    }

    public void setDetails(PushMsgModel details) {
        this.details = details;
    }

    public String getClockin() {
        return clockin;
    }

    public void setClockin(String clockin) {
        this.clockin = clockin;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
