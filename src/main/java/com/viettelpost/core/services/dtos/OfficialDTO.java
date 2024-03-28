package com.viettelpost.core.services.dtos;

import lombok.Data;

@Data
public class OfficialDTO {
    Long userId;
    Long postId;
    String code;
    String org;
    String name;
    String maTinh;
    String maChucDanh;
    String VUNG;
    Long idTinh;
}
