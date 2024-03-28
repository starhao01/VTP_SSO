package com.viettelpost.core.services.dtos;

import lombok.Data;

@Data
public class AccountDTO {
    private String username;
    private String oldPass;
    private String newPass;
    private String phone;
    private String otp;
    private String appCode;
}
