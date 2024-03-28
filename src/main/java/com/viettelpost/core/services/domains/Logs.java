package com.viettelpost.core.services.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Logs {
    private String userName;
    private String actionLog;
    private String chucNang;
    private String timeLog;
}
