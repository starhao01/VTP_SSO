package com.viettelpost.core.services.dtos;

import java.io.Serializable;

public class AppDto implements Serializable {
    String code;
    String token;
    String domain;
    long refreshed = System.currentTimeMillis();
    UserTokenDto user;

    public AppDto() {
    }

    public AppDto(String code, String host, String token) {
        this.code = code;
        this.domain = host;
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDomain() {
        if (domain != null) {
            if (!domain.endsWith("/")) {
                domain = domain + "/";
            }
        }
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getRefreshed() {
        return refreshed;
    }

    public void setRefreshed(long refreshed) {
        this.refreshed = refreshed;
    }

    public UserTokenDto getUser() {
        return user;
    }

    public void setUser(UserTokenDto user) {
        this.user = user;
    }
}
