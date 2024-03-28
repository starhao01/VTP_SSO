package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.base.Utils;
import com.viettelpost.core.services.AppService;
import com.viettelpost.core.services.domains.AppInfo;
import com.viettelpost.core.services.domains.Logs;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin("*")
@RestController
@RequestMapping("/app")
public class AppController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "APP CONTROLLER";

    @Autowired
    AppService appService;

    @ApiOperation(value = "API Get All App By UserID")
    @GetMapping("/getAllOwnerAppByUserID")
    public ResponseEntity getAllOwnerAppByUserID(@RequestParam Long userid) {
        List<AppInfo> result = appService.getAllAppInfoByUserID(userid);
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Get All App")
    @GetMapping("/getAllApp")
    public ResponseEntity getAllApp() throws Exception {
        List<AppInfo> result = appService.getAllApp();
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Get All App")
    @GetMapping("/getAllAppByRole")
    public ResponseEntity getAllAppByRole(@RequestParam Long roleId) throws Exception {
        List<AppInfo> result = appService.getAllAppByRole(roleId);
        return successApi(result, "Thành công");
    }


    @ApiOperation(value = "API Get All App Pagging")
    @GetMapping("/getAllAppPaging")
    public ResponseEntity getAllAppPaging(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'") || txtSearch.equalsIgnoreCase("\\")){
            txtSearch = "";
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = appService.getTotalApp(txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }

        List<AppInfo> result = appService.getAllAppPaging(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API Insert App")
    @PostMapping("/insertApp")
    public ResponseEntity insertApp(@RequestBody AppInfo appInfo) throws Exception {
        List<AppInfo> result = appService.getAllApp();
        for (AppInfo app : result) {
            if (Utils.isNullOrEmpty(app.getCode()) || app.getCode().equals(appInfo.getCode())) {
                return errorApi("Code đã tồn tại trong hệ thống !");
            }
        }
        appService.insertApp(appInfo.getName(), appInfo.getDesription(), appInfo.getCode());
        insertLogs("INSERT APP: " + appInfo.getCode());
        return successApi(null, "Thành công.");
    }

    @ApiOperation(value = "API Delete App")
    @PostMapping("/deleteApp")
    public ResponseEntity deleteApp(@RequestParam Long id) throws Exception {
        List<AppInfo> result = appService.getAllApp();
        for (AppInfo app : result) {
            if (Objects.equals(app.getId(), id) || app.getId().equals(id)) {
                appService.deleteApp(id);
                insertLogs("DELETE APP: " + id);
                return successApi(null, "Thành công.");
            }
        }
        return errorApi("Không tồn tại ID App nào trên hệ thống.");
    }

    @ApiOperation(value = "API Update App")
    @PostMapping("/updateApp")
    public ResponseEntity updateApp(@RequestBody AppInfo appInfo) throws Exception {
        List<AppInfo> result = appService.getAllApp();
        for (AppInfo app : result) {
            if (app.getCode().equals(appInfo.getCode()) && !Objects.equals(app.getId(), appInfo.getId())) {
                return errorApi("Code đã tồn tại trong hệ thống !");
            }
        }
        appService.updateApp(appInfo.getName(), appInfo.getDesription(), appInfo.getCode(), appInfo.getId());
        insertLogs("UPDATE APP: " + appInfo.getCode());
        return successApi(null, "Thành công.");
    }

    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        appService.insertLogs(logs);
    }

}
