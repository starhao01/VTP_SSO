package com.viettelpost.core.services.dtos;

import com.viettelpost.core.utils.Utils;

import java.io.Serializable;

public class OfficeDTO implements Serializable {
    Long userId;
    Long postId;
    String code;
    String name;
    String org;
    String maTinh;
    String vung;
    Long idTinh;
    String maChucDanh;
    Long idVung;
    Long OrgId ;
    String OrgName;

    public Long getIdVung() {
        return idVung;
    }

    public void setIdVung(Long idVung) {
        this.idVung = idVung;
    }



    public String getMaChucDanh() {
        return maChucDanh;
    }

    public void setMaChucDanh(String maChucDanh) {
        this.maChucDanh = maChucDanh;
    }


    public Long getIdTinh() {
        return idTinh;
    }

    public void setIdTinh(Long idTinh) {
        this.idTinh = idTinh;
    }

    public String getVung() {
        return vung;
    }

    public void setVung(String vung) {
        this.vung = vung;
    }

    public String getMaTinh() {
        return maTinh;
    }

    public void setMaTinh(String maTinh) {
        this.maTinh = maTinh;
    }


    public OfficeDTO() {
    }

    public OfficeDTO(Long postId, String code, String name) {
        this.postId = postId;
        this.code = code;
        this.name = name;
    }

    public OfficeDTO(Long postId, String code, String name, String org) {
        this.postId = postId;
        this.code = code;
        this.name = name;
        this.org = org;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {        this.postId = postId;    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public Long getOrgId() {
        return OrgId;
    }

    public void setOrgId(Long OrgId) {
        this.OrgId = OrgId;
    }

    public String getOrgName() {
        return OrgName;
    }

    public void setOrgName(String OrgName) {
        this.OrgName = OrgName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof OfficeDTO) {
            OfficeDTO office = (OfficeDTO) obj;
            return office.getPostId() != null && this.getPostId() != null && office.getPostId().compareTo(this.postId) == 0;
        }
        return super.equals(obj);
    }
}
