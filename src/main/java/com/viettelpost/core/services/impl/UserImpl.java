package com.viettelpost.core.services.impl;

import com.viettelpost.core.services.UserService;
import com.viettelpost.core.services.daos.UserDAO;
import com.viettelpost.core.services.dtos.OfficeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserImpl implements UserService {

    UserDAO userDAO;

    @Autowired
    public UserImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Map<String, Object> getUserInfo(String username, String appId) throws Exception {
        return userDAO.getUserInfo(username, appId);
    }

    @Override
    public List<OfficeDTO> listPostByUsername(String username, String buuCuc) {
        return userDAO.listPostByUsername(username, buuCuc);
    }

    @Override
    public String changePassword(String username, String newPass, String newPassSalt) throws Exception {
        return userDAO.changePassword(username, newPass, newPassSalt);
    }

    @Override
    public Map<String, Object> sendOTP(String phone) throws Exception {
        return userDAO.sendOTP(phone);
    }


    @Override
    public Map<String, Object> checkOtp(String phone, String otp, String appCode) throws Exception {
        return userDAO.checkOtp(phone, otp, appCode);
    }

    @Override
    public String changePasswordByOtp(String phone, String newPass, String newPassSalt) throws Exception {
        return userDAO.changePasswordByOtp(phone, newPass, newPassSalt);
    }

    @Override
    public Map<String, Object> getToken(String sso, String code) throws Exception {
        return userDAO.getToken(sso, code);
    }

    @Override
    public Map<String, Object> getShareCode(String username, String appCode) throws Exception {
        return userDAO.getShareCode(username, appCode);
    }

    @Override
    public Map<String, Object> getUserInfoNB(String employeeCode, String appId) throws Exception {
        return userDAO.getUserInfoNB(employeeCode,appId);
    }


}
