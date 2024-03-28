package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.services.BuuCucService;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.BuuCucDTO;
import com.viettelpost.core.services.dtos.OfficeDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/buucuc")
public class BuuCucController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "BUU CUC CONTROLLER";

    @Autowired
    BuuCucService buuCucService;


    @ApiOperation(value = "API Get List BuuCuc")
    @GetMapping("/getListBuuCucPaging")
    public ResponseEntity getListBuuCucPaging(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'")){
            txtSearch = "";
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = buuCucService.getTotalBuuCuc(txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        List<BuuCucDTO> result = buuCucService.getListBuuCuc(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API Get Info BuuCuc")
    @GetMapping("/getInfoBuuCucByUsername")
    public ResponseEntity getInfoBuuCucByUsername(@RequestParam int page, @RequestParam int limit,@RequestParam String username) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = buuCucService.getTotalPost(username);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        List<OfficeDTO> result = buuCucService.listPostByUsername(start,end,username);
        return customList(numPs, page, result);
    }

    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        buuCucService.insertLogs(logs);
    }
}
