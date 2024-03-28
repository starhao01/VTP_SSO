package com.viettelpost.core.services.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfo {
    Long id;
    String user_name;
    String employee_code;
    String display_name;
    String email;
    String identifiercreateondate;
    String active;
    String comment;
}
