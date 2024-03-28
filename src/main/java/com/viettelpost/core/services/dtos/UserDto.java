package com.viettelpost.core.services.dtos;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class UserDto implements Serializable {
    String token;
    Long userId;
    Long dnUserId;
    String postCode;
    String orgCode;
    Long postId;
    Long orgId;
    String username;
    String firstname;
    String lastname;
    Long vung;
    long expired;
    List<String> authorities;

    public UserDto(String username, String token) {
        this.username = username;
        this.token = token;
        authorities = Arrays.asList("ROLE_USER");
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }

    public Long getDnUserId() {
        return dnUserId;
    }

    public void setDnUserId(Long dnUserId) {
        this.dnUserId = dnUserId;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public Long getVung() {
        return vung;
    }

    public void setVung(Long vung) {
        this.vung = vung;
    }

    public UserDto() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
}
