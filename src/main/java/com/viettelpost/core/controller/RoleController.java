package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.services.ModuleService;
import com.viettelpost.core.services.RoleService;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.ModuleInfo;
import com.viettelpost.core.services.domains.RoleInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "ROLE CONTROLLER";

    @Autowired
    RoleService roleService;

    @Autowired
    ModuleService moduleService;


    @ApiOperation(value = "API update Role")
    @PostMapping("/updateRole")
    public ResponseEntity updateRole(@RequestParam String name, @RequestParam String desription, @RequestParam Long status, @RequestParam Long id) throws Exception {
        roleService.updateRole(name, desription, status, id);
        insertLogs("UPDATE ROLE: " + name);
        return successApi(null, "Thành công.");
    }

    @ApiOperation(value = "API changeStatus Role")
    @PostMapping("/changeStatusRole")
    public ResponseEntity changeStatusRole(@RequestParam Long id) throws Exception {
        roleService.changeStatusRole(id);
        insertLogs("CHANGE STATUS ROLE: " + id);
        return successApi(null, "Thành công.");
    }

    @ApiOperation(value = "API delete role")
    @PostMapping("/deleteRole")
    public ResponseEntity deleteRole(@RequestParam Long id) throws Exception {
        List<RoleInfo> result = roleService.getListAllRole();
        for (RoleInfo roleInfo : result) {
            if (roleInfo.getId() == id || roleInfo.getId().equals(id)) {
                roleService.deleteRole(id);
                insertLogs("DELETE ROLE: " + id);
                return successApi(null, "Thành công.");
            }
        }
        return errorApi("Không tồn tại ID nào trên hệ thống.");

    }

    @ApiOperation(value = "API get List All Role Of Module Paging")
    @GetMapping("/getListModuleByRolePaging")
    public ResponseEntity getListModuleByRole(@RequestParam Long roleId, @RequestParam int limit, @RequestParam int page, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'") || txtSearch.equalsIgnoreCase("\\")){
            txtSearch = "";
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

    @ApiOperation(value = "API get all role paging")
    @GetMapping("/getAllRole")
    public ResponseEntity getAllRole(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = roleService.getTotalRole(txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Get result list
        List<RoleInfo> result = roleService.getAllRole(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API get all role paging")
    @GetMapping("/getAllRoleActive")
    public ResponseEntity getAllRoleActive(@RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = roleService.getTotalRole(txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Get result list
        List<RoleInfo> result = roleService.getAllRole(start, end, txtSearch);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API get all role paging")
    @GetMapping("/getAllRoleByAppId")
    public ResponseEntity getAllRoleByAppId(@RequestParam Long appId, @RequestParam int page, @RequestParam int limit, String txtSearch) throws Exception {
        //Code phan trang
        if (page <= 0) {
            page = 1;
        }
        if(txtSearch.equalsIgnoreCase("'")){
            txtSearch = "";
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = roleService.getTotalRole(txtSearch);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Get result list
        List<RoleInfo> result = roleService.getAllRoleByAppID(appId, start, end, txtSearch);
        return customList(result.size(), page, result);
    }

    @ApiOperation(value = "API insert role")
    @PostMapping("/insertRole")
    public ResponseEntity InsertRole(@RequestParam String name, @RequestParam String description, @RequestParam Long status) throws Exception {
        List<RoleInfo> result = roleService.getListAllRole();
        for (RoleInfo role : result) {
            if (role.getName().equals(name.trim())) {
                return errorApi("Đã tồn tại Mã Quyền trên hệ thống.");
            }
        }
        roleService.insertRole(name, description, status);
        insertLogs("INSERT ROLE: " + name);
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API get all role by userId")
    @GetMapping("/getAllRoleByUserID")
    public List<RoleInfo> getAllRoleByUserId(@RequestParam String username, @RequestParam Long AppId) throws Exception {
        List<RoleInfo> result = roleService.getAllRoleByUserId(username, AppId);
        if (result == null) {
            return result;
        }
        return result;
    }

    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        roleService.insertLogs(logs);
    }


}
