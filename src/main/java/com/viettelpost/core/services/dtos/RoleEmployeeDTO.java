package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleEmployeeDTO {
    Long appId;
    Long userId;
    String appcode;
    String role;
    Long roleId;
}
