package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleOfModuleDTO {
    Long appId;
    Long roleId;
    String roleName;
    Long moduleId;
    String moduleCode;
    String moduleName;
    Long parentId;
    Long type;
}
