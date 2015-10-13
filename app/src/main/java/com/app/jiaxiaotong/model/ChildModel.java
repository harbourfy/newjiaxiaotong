package com.app.jiaxiaotong.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Administrator on 2015/9/10.
 */
public class ChildModel extends BaseModel {

    private String uid;

    private String schoolName;

    private String name;

    private String gender;

    private String className;

    private String header;
    @JsonProperty(value = "responseData")
    private List<ChildModel> childs;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<ChildModel> getChilds() {
        return childs;
    }

    public void setChilds(List<ChildModel> childs) {
        this.childs = childs;
    }
}
