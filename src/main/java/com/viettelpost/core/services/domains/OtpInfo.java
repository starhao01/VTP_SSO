package com.viettelpost.core.services.domains;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OtpInfo implements Serializable {
    String phone;
    String email;
    String otp;
    Date otpDate;
    Long count;
    Long check;
}
