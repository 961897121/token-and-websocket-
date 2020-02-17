package com.websocket.websocket.pojo;

import com.alibaba.fastjson.JSONObject;

/**
 * 服务器返回信息类
 */
public class Info {
    private int member;
    private String userName;
    private String userType;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Info() {
        this.userName = "服务器";
        this.userType = "server";
    }

    public int getMember() {
        return member;
    }

    public void setMember(int member) {
        this.member = member;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public static void main(String[] args) {
        JSONObject object = (JSONObject) JSONObject.toJSON(new Info());
        System.out.println(object);
    }

    @Override
    public String toString() {
        return "Info{" +
                "member=" + member +
                ", userName='" + userName + '\'' +
                ", userType='" + userType + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
