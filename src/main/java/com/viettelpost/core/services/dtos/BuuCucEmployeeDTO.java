package com.viettelpost.core.services.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuuCucEmployeeDTO {
    String TEN_BUUCUC;
    String TEN_CHUCDANH;
    String MA_BUUCUC;
    String MA_CHUCDANH;
    Long userId;
}
