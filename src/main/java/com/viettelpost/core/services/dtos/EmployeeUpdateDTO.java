package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateDTO {
    Long id;
    String firstName;
    String lastName;
    String phone;
    String employeeCode;
    String username;
    String userpassword;
    String birthdate;
    String identifiernumber;
    String email;
}
