package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInsertBC {
    Long userId;
    String MA_CHUCDANH;
    String MA_BUUCUC;
    Long active;
}
