package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsDTO {
    Long id;
    String firstname;
    String lastname;
    String phone;
    String employeeCode;
    String username;
    String userPassword;
    String birthdate;
    String identifierNumber;
    String email;
    String comment;
    Long groupId;
}
