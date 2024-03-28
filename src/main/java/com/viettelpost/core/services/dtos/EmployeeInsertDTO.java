package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInsertDTO {
    private Long employeeId;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String phone;
    private String sex;
    private String username;
    private String password;
    private String birthdate;
    private String identifierNumber;
    private String email;
    private Long groupId;
    private String comment;
    private Long createByUser;
    private String isDeleted;
}
