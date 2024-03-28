package com.viettelpost.core.services;

import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.ChucDanhDTO;

import java.util.List;

public interface ChucDanhService {
    List<ChucDanhDTO> getAllChucDanhDTO();

    List<ChucDanhDTO> getAllChucDanhByPaging(int rowStart, int rowEnd, String txtSearch);

    void insertChucDAnh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE);

    void deleteChucDanh(String MA_CHUCDANH);

    List<ChucDanhDTO> getChucDanhByName(String txtSearch);

    void updateChucDanh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE);

    void changeStatusChucDanh(String MA_CHUCDANH);

    void insertLogs(Logs logs) throws Exception;

    List<ChucDanhDTO> getAllChucDanhHoatDongByPaging(int rowStart, int rowEnd, String txtSearch);
}
