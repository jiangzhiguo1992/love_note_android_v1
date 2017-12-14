package com.jiangzg.ita.domain;

/**
 * Created by JiangZhiGuo on 2016/9/30.
 * describe 用户实体类
 */
public class User extends BaseObj {

    private String id;
    private String userToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
