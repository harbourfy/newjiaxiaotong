package com.app.jiaxiaotong.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekfans-com on 2015/9/11.
 */
public class ContactModel extends BaseModel implements Parcelable {

    private String uid;// 用户的UID
    private String cn;//名称
    private String mobilePhone;//手机号
    private String group;// 分组
    private String gender;// 性别
    private String header;// 头像ID
    private String role;// teacher代表老师，family代表家长
    private String headerTxt;//头部文字显示
    public String getHeaderTxt() {
        return headerTxt;
    }

    public void setHeaderTxt(String headerTxt) {
        this.headerTxt = headerTxt;
    }

    //老师获取班级的情况
    private String ou;//班级的唯一标识，用于根据班级获取用户列表的classOu
    //cn名称一样
    //end
    @JsonProperty(value = "responseData")
    private List<ContactModel> result;//

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public List<ContactModel> getResult() {
        return result;
    }

    public void setResult(List<ContactModel> result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.cn);
        dest.writeString(this.mobilePhone);
        dest.writeString(this.group);
        dest.writeString(this.gender);
        dest.writeString(this.header);
        dest.writeString(this.role);
        dest.writeString(this.headerTxt);
        dest.writeString(this.ou);
        dest.writeList(this.result);
    }

    public ContactModel() {
    }

    protected ContactModel(Parcel in) {
        this.uid = in.readString();
        this.cn = in.readString();
        this.mobilePhone = in.readString();
        this.group = in.readString();
        this.gender = in.readString();
        this.header = in.readString();
        this.role = in.readString();
        this.headerTxt = in.readString();
        this.ou = in.readString();
        this.result = new ArrayList<ContactModel>();
        in.readList(this.result, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
        public ContactModel createFromParcel(Parcel source) {
            return new ContactModel(source);
        }

        public ContactModel[] newArray(int size) {
            return new ContactModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        return uid.equalsIgnoreCase(((ContactModel)o).getUid());
    }
}
