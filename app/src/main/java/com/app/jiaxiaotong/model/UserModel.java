package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel extends BaseModel {

    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "header")
    private String avatar;
    @JsonProperty(value = "userTypes")
    private String[] userTypes;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "uid")
    private String uid;
    @JsonProperty(value = "gender")
    private String gender;
    @JsonProperty(value = "responseData")
    private UserModel details;

    private String mobilePhone;
    @JsonProperty(value = "relation")
    private String relation;
    private String schoolName;

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public UserModel getDetails() {
        return details;
    }

    public void setDetails(UserModel details) {
        this.details = details;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String[] getUserTypes() {
        return userTypes;
    }

    public void setUserTypes(String[] userTypes) {
        this.userTypes = userTypes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}
