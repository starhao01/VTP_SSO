package com.viettelpost.core.services;

import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.BuuCucDTO;
import com.viettelpost.core.services.dtos.OfficeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuuCucService {

    List<BuuCucDTO> getListBuuCuc(int rowStart, int rowEnd, String txtSearch);

    int getTotalBuuCuc(String txtSearch) throws Exception;
    void insertLogs(Logs logs) throws Exception;
    List<OfficeDTO> listPostByUsername(int rowStart, int rowEnd,String username);

    int getTotalPost(String username) throws Exception;
}
