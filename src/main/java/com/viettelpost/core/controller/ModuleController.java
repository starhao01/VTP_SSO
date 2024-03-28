package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.base.VtException;
import com.viettelpost.core.controller.request.ModuleRequest;
import com.viettelpost.core.services.AppService;
import com.viettelpost.core.services.ModuleService;
import com.viettelpost.core.services.RoleService;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.ModuleInfo;
import com.viettelpost.core.services.domains.UserInfo;
import com.viettelpost.core.services.dtos.ModuleInforDTO;
import com.viettelpost.core.services.dtos.RoleInsertDTO;
import com.viettelpost.core.services.dtos.RoleOfModuleDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/module")
public class ModuleController extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "MODULE CONTROLLER";

    @Autowired
    ModuleService moduleService;

    @Autowired
    AppService appService;

    @Autowired
    RoleService roleService;


    @ApiOperation(value = "API Get Children Module By ID")
    @GetMapping("/getChildrenModuleByModuleID")
    public ResponseEntity getChildrenModuleByModuleID(@RequestParam Long ModuleID, @RequestParam Long AppID) {
        List<ModuleInfo> result = moduleService.getChildrenModuleByModuleID(ModuleID, AppID);
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Get List Module By AppID")
    @GetMapping("/getListModuleByAppID")
    public ResponseEntity getListModuleByAppID(@RequestParam Long AppID, String txtSearch) {
        List<ModuleInfo> result = moduleService.getListModuleByAppID(AppID, txtSearch);
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Insert Module")
    @PostMapping("/insertModule")
    public ResponseEntity insertModule(@RequestBody ModuleRequest moduleRequest, @RequestParam Long App_ID) throws Exception {
        List<ModuleInfo> result = moduleService.getListModuleByAppID(App_ID, null);
        for (ModuleInfo moduleInfo : result) {
            if (moduleInfo.getCode().equals(moduleRequest.getCode())) {
                return errorApi("Code đã tồn tại trong hệ thống !");
            }
        }
        moduleService.insertModule(moduleRequest, App_ID);
        insertLogs("INSERT MODULE:" + moduleRequest.getCode() + ", APP_ID: " + App_ID);
        return successApi(null, "Thêm mới Module thành công.");
    }

    @ApiOperation(value = "API getListModuleByToken")
    @GetMapping("/getListModuleByToken")
    public ResponseEntity getListModuleByToken() throws Exception {
        UserInfo userInfo = getCurrentUser();
        if (userInfo == null) {
            return errorApi("Token hết hạn, vui lòng thử lại sau.");
        }
        HashMap user = (HashMap) userInfo.getInfos().get("result");
        if (((String) user.get("roleid")) != null) {
            String roleList = ((String) user.get("roleid"));
            List<ModuleInfo> listmodule = moduleService.listModuleByRole(roleList, (Long) user.get("app_id"));
            return successApi(listmodule, "Thành công");
        } else {
            return errorApi("User chưa được gán quyền hoặc không có quyền truy cập.");
        }
    }

    @ApiOperation(value = "API getListModuleByTokenSSO")
    @GetMapping("/getListModuleByTokenSSO")
    public ResponseEntity getListModuleByTokenSSO() throws Exception {
        UserInfo userInfo = getCurrentUser();
        if (userInfo == null) {
            return errorApi("Token hết hạn, vui lòng thử lại sau.");
        }
        if (userInfo.getInfos().get("roleid") != null) {
            String roleList = (String) userInfo.getInfos().get("roleid");
            List<ModuleInfo> listmodule = moduleService.listModuleByRole(roleList, 71L);
            return successApi(listmodule, "Thành công");
        } else {
            return errorApi("User chưa được gán quyền hoặc không có quyền truy cập.");
        }
    }


    @ApiOperation(value = "API delete Module")
    @PostMapping("/deleteModule")
    public ResponseEntity deleteModule(@RequestParam Long moduleId, @RequestParam Long appId) throws Exception {
        List<ModuleInfo> rs = moduleService.getListModuleByAppID(appId, null);
        for (ModuleInfo module : rs) {
            if (module.getId() == moduleId || module.getId().equals(moduleId)) {
                moduleService.deleteModuleById(moduleId);
                insertLogs("DELETE MODULE BY ID: " + moduleId + ", APP_ID: " + appId);
                return successApi(null, "Xóa Module thành công.");
            }
        }
        return errorApi("Không tồn tại ID Module trong hệ thống.");
    }

    @ApiOperation(value = "API update module")
    @PostMapping("/updateModule")
    public ResponseEntity updateModule(@RequestBody ModuleRequest moduleRequest) throws Exception {
        moduleService.updateModule(moduleRequest);
        insertLogs("UPDATE MODULE: " + moduleRequest.getCode());
        return successApi(null, "Sửa module thành công.");
    }


    @ApiOperation(value = "API delete role")
    @DeleteMapping("/deleteModuleOfRole")
    public ResponseEntity deleteModuleOfRole(@RequestBody RoleInsertDTO roleInsertDTO) throws Exception {
        List<RoleOfModuleDTO> result = roleService.getListAllModuleOfRolePaging(roleInsertDTO.getRoleId(), roleInsertDTO.getAppId(), 1, 9999, null);
        for (RoleOfModuleDTO role :
                result) {
            if (role.getModuleId().equals(roleInsertDTO.getModuleId()) || role.getModuleId() == roleInsertDTO.getModuleId()) {
                roleService.deleteModuleOfRole(roleInsertDTO.getAppId(), roleInsertDTO.getRoleId(), roleInsertDTO.getModuleId());
                insertLogs("DELETE MODULE OF ROLE: " + roleInsertDTO.getModuleId() + ", APP_ID: " + roleInsertDTO.getAppId());
                return successApi(null, "Thành công");
            }
        }
        return errorApi("Không tồn tại Module.");
    }

    @ApiOperation(value = "API get List All Role Not Of Module Paging")
    @GetMapping("/getListAllModuleNotOfRolePaging")
    public ResponseEntity getListAllModuleNotOfRolePaging(@RequestParam Long roleId, @RequestParam Long appId, @RequestParam int limit, @RequestParam int page, String txtSearch) throws Exception {
        int numPs = 0, numperPage, numpage, start, end;
        List<RoleOfModuleDTO> result = new ArrayList<>();
        try {
            //Code phan trang
            if (page <= 0) {
                page = 1;
            }
            if(txtSearch.equalsIgnoreCase("'")){
                txtSearch = "";
            }
            numPs = roleService.getTotalRoleNotOfModule(roleId, appId, txtSearch);
            numperPage = limit;
            numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
            start = ((page - 1) * numperPage) + 1;
            if (page * numperPage > numPs) {
                end = numPs;
            } else {
                end = page * numperPage;
            }
            //Code phan trang
            result = roleService.getListAllModuleNotOfRolePaging(roleId, appId, start, end, txtSearch);
        } catch (Exception e) {
            throw new VtException("Vui lòng kiểm tra lại input đầu vào.");
        }
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API get List All Role Of Module Paging")
    @GetMapping("/getListAllModuleOfRolePaging")
    public ResponseEntity getListAllModuleOfRolePaging(@RequestParam Long roleId, @RequestParam Long appId, @RequestParam int limit, @RequestParam int page, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        int numPs, numperPage, numpage, start, end;
        numPs = roleService.getTotalRoleOfModule(roleId, appId, txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Code phan trang
        List<RoleOfModuleDTO> result = roleService.getListAllModuleOfRolePaging(roleId, appId, start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API get List All Role Of Module Paging")
    @GetMapping("/getListModuleByRolePaging")
    public ResponseEntity getListModuleByRole(@RequestParam Long roleId, @RequestParam int limit, @RequestParam int page, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        int numPs, numperPage, numpage, start, end;
        numPs = moduleService.getTotalModule(roleId, txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Code phan trang
        List<ModuleInfo> result = moduleService.listModuleByRolePaging(roleId, start, end, txtSearch);
        return customList2(false, numPs, page, result);
    }

    @ApiOperation(value = "API get List Module By App ID Paging")
    @GetMapping("/getListModuleByAppIDPaging")
    public ResponseEntity getListModuleByAppIDPaging(@RequestParam Long appId, @RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        int numPs, numperPage, numpage, start, end;
        numPs = moduleService.getListModuleByAppID(appId, txtSearch).size();
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }

        List<ModuleInfo> result = moduleService.getListModuleByAppIDPaging(start, end, appId, txtSearch);
        return customList2(false, numPs, page, result);
    }


    @ApiOperation(value = "API insert module to role")
    @PostMapping("/insertModuleToRole")
    public ResponseEntity InsertModuleToRole(@RequestBody RoleInsertDTO roleInsertDTO) throws Exception {
        List<RoleOfModuleDTO> result = roleService.getListAllModuleOfRolePaging(roleInsertDTO.getRoleId(), roleInsertDTO.getAppId(), 1, 9999, null);
        for (RoleOfModuleDTO role :
                result) {
            if (role.getModuleId().equals(roleInsertDTO.getModuleId()) || role.getModuleId() == roleInsertDTO.getModuleId()) {
                return errorApi("Đã tồn tại module trong quyền");
            }
        }
        roleService.insertModuleToRole(roleInsertDTO.getAppId(), roleInsertDTO.getRoleId(), roleInsertDTO.getModuleId());
        insertLogs("INSERT MODULE TO ROLE: " + roleInsertDTO.getRoleId() + ", APPID: " + roleInsertDTO.getAppId());
        return successApi(null, "Thành công");
    }
    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        appService.insertLogs(logs);
    }

}
