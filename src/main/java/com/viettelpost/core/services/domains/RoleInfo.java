package com.viettelpost.core.services.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo implements Serializable {
    Long id;
    String name;
    String description;
    Long userId;
    String displayName;
    String phone;
    String username;
    String email;
    Long status;
    String ngaydongbo;
}
