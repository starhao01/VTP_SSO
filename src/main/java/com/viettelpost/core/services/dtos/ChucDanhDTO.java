package com.viettelpost.core.services.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChucDanhDTO {
    private String MA_CHUCDANH;
    private String TEN_CHUCDANH;
    private int SU_DUNG;
    private String NGAY_NHAP_MAY;
}
