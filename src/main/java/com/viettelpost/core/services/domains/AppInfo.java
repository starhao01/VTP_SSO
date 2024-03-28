package com.viettelpost.core.services.domains;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppInfo implements Serializable {
    Long id;
    String code;
    String name;
    String desription;
}
