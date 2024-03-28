package com.viettelpost.core.services.impl;

import com.viettelpost.core.services.BuuCucService;
import com.viettelpost.core.services.daos.BuuCucDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.BuuCucDTO;
import com.viettelpost.core.services.dtos.OfficeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuuCucImpl implements BuuCucService {

    @Autowired
    BuuCucDAO buuCucDAO;

    @Override
    public List<BuuCucDTO> getListBuuCuc(int rowStart, int rowEnd, String txtSearch) {
        return buuCucDAO.getListBuuCuc(rowStart, rowEnd, txtSearch);
    }

    @Override
    public int getTotalBuuCuc(String txtSearch) throws Exception {
        return buuCucDAO.getTotalBuuCuc(txtSearch);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        buuCucDAO.insertLogs(logs);
    }

    @Override
    public List<OfficeDTO> listPostByUsername(int rowStart, int rowEnd, String username) {
        return buuCucDAO.listPostByUsername(rowStart,rowEnd,username);
    }

    @Override
    public int getTotalPost(String username) throws Exception {
        return buuCucDAO.getTotalPost(username);
    }
}
