package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.services.ChucDanhService;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.ChucDanhDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/chucdanh")
public class ChucDanhController extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "CHUCDANH CONTROLLER";

    @Autowired
    ChucDanhService chucDanhService;

    @ApiOperation(value = "API get List all ChucDanh")
    @GetMapping("/getListChucDanh")
    public ResponseEntity getListChucDanh() throws Exception {
        List<ChucDanhDTO> result = chucDanhService.getAllChucDanhDTO();
        if (result.size() <= 0) {
            return errorApi("Thất bại");
        }
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API get list ChucDanh paging")
    @GetMapping("/getListChucDanhPaging")
    public ResponseEntity getListChucDanhByPaging(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'")){
            txtSearch = "";
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = chucDanhService.getChucDanhByName(txtSearch).size();
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        List<ChucDanhDTO> result = chucDanhService.getAllChucDanhByPaging(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API get list ChucDanh paging")
    @GetMapping("/getListChucDanhHoatDongPaging")
    public ResponseEntity getListChucDanhHoatDongPaging(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'")){
            txtSearch = "";
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = chucDanhService.getChucDanhByName(txtSearch).size();
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        List<ChucDanhDTO> result = chucDanhService.getAllChucDanhHoatDongByPaging(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API insert ChucDanh")
    @PostMapping("/insertChucDanh")
    public ResponseEntity insertChucDanh(@RequestParam String MA_CHUCDANH, @RequestParam String TEN_CHUCDANH, @RequestParam Long ACTIVE) throws Exception {

        List<ChucDanhDTO> result = chucDanhService.getAllChucDanhDTO();
        for (ChucDanhDTO chucdanh : result) {
            if (chucdanh.getMA_CHUCDANH().equals(MA_CHUCDANH)) {
                return errorApi("Đã tồn tại mã chức danh trong hệ thống.");
            }
        }
        chucDanhService.insertChucDAnh(MA_CHUCDANH, TEN_CHUCDANH, ACTIVE);
        insertLogs("INSERT CHUCDANH: " + MA_CHUCDANH);
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API delete ChucDanh")
    @PostMapping("/deleteChucDanh")
    public ResponseEntity deleteChucDanh(@RequestParam String MA_CHUCDANH) throws Exception {
        List<ChucDanhDTO> result = chucDanhService.getAllChucDanhDTO();
        for (ChucDanhDTO chucdanh : result) {
            if (chucdanh.getMA_CHUCDANH().equals(MA_CHUCDANH)) {
                chucDanhService.deleteChucDanh(MA_CHUCDANH);
                insertLogs("DELETE CHUC DANH: " + MA_CHUCDANH);
                return successApi(null, "Thành công");
            }
        }
        return errorApi("Không tồn tại mã chức danh trong hệ thống !");
    }

    @ApiOperation(value = "API updateChucDanh")
    @PostMapping("/updateChucDanh")
    public ResponseEntity updateChucDanh(@RequestParam String ma_chucnang, @RequestParam String ten_chucdanh, @RequestParam Long status) throws Exception {
        chucDanhService.updateChucDanh(ma_chucnang, ten_chucdanh, status);
        insertLogs("UPDATE CHUC DANH: " + ma_chucnang);
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API changeStatusChucDanh")
    @PostMapping("/changeStatusChucDanh")
    public ResponseEntity changeStatusChucDanh(@RequestParam String ma_chucdanh) throws Exception {
        if (ma_chucdanh == null) return errorApi("Mã chức danh không được để trống.");
        chucDanhService.changeStatusChucDanh(ma_chucdanh);
        insertLogs("CHANGE STATUS CHUC DANH: " + ma_chucdanh);
        return successApi(null, "Thành công");
    }

    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        chucDanhService.insertLogs(logs);
    }
}
