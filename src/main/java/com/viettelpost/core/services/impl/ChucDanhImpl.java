package com.viettelpost.core.services.impl;

import com.viettelpost.core.services.ChucDanhService;
import com.viettelpost.core.services.daos.ChucDanhDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.ChucDanhDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChucDanhImpl implements ChucDanhService {

    ChucDanhDAO chucDanhDAO;

    @Autowired
    public ChucDanhImpl(ChucDanhDAO chucDanhDAO) {
        this.chucDanhDAO = chucDanhDAO;
    }

    @Override
    public List<ChucDanhDTO> getAllChucDanhDTO() {
        return chucDanhDAO.getAllChucDanh();
    }

    @Override
    public List<ChucDanhDTO> getAllChucDanhByPaging(int rowStart, int rowEnd, String txtSearch) {
        return chucDanhDAO.getAllChucDanhByPaging(rowStart, rowEnd, txtSearch);
    }

    @Override
    public void insertChucDAnh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE) {
        chucDanhDAO.insertChucDAnh(MA_CHUCDANH, TEN_CHUCDANH, ACTIVE);
    }

    @Override
    public void deleteChucDanh(String MA_CHUCDANH) {
        chucDanhDAO.deleteChucDanh(MA_CHUCDANH);
    }

    @Override
    public List<ChucDanhDTO> getChucDanhByName(String txtSearch) {
        return chucDanhDAO.getChucDanhByName(txtSearch);
    }

    @Override
    public void updateChucDanh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE) {
        chucDanhDAO.updateChucDanh(MA_CHUCDANH, TEN_CHUCDANH, ACTIVE);
    }

    @Override
    public void changeStatusChucDanh(String MA_CHUCDANH) {
        chucDanhDAO.changeStatusChucDanh(MA_CHUCDANH);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        chucDanhDAO.insertLogs(logs);
    }

    @Override
    public List<ChucDanhDTO> getAllChucDanhHoatDongByPaging(int rowStart, int rowEnd, String txtSearch) {
        return chucDanhDAO.getAllChucDanhHoatDongByPaging(rowStart, rowEnd, txtSearch);
    }

}
