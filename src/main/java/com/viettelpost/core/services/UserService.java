package com.viettelpost.core.services;

import com.viettelpost.core.services.dtos.AppDto;
import com.viettelpost.core.services.dtos.OfficeDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    Map<String, Object> getUserInfo(String username, String appId) throws Exception;

    List<OfficeDTO> listPostByUsername(String username, String buuCuc);

    String changePassword(String username, String newPass, String newPassSalt) throws Exception;

    Map<String, Object> sendOTP(String phone) throws Exception;


    Map<String, Object> checkOtp(String phone, String otp, String appCode) throws Exception;

    String changePasswordByOtp(String phone, String newPass, String newPassSalt) throws Exception;

    Map<String, Object> getToken(String sso, String code) throws Exception;

    Map<String, Object> getShareCode(String token, String appCode) throws Exception;
    Map<String, Object> getUserInfoNB(String employeeCode, String appId) throws Exception;

}
