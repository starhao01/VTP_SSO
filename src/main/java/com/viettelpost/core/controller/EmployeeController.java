package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.services.EmployeeService;
import com.viettelpost.core.services.domains.*;
import com.viettelpost.core.services.dtos.*;
import com.viettelpost.core.utils.Utils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String chucNang = "EMPLOYEE CONTROLLER";

    @Autowired
    EmployeeService employeeService;

    @ApiOperation(value = "API List Employee Paging")
    @GetMapping("/getListEmployeePaging")
    public ResponseEntity getListEmployeePaging(@RequestParam int page, @RequestParam int limit, String txtSearch, Long active) throws Exception {
        if (page <= 0) {
            page = 1;
        }
        //Code phan trang
        int numPs, numperPage, numpage, start, end;
        numPs = employeeService.totalEmployee(txtSearch, active);
        numperPage = limit;
        numpage = numPs / numperPage + (numPs % numperPage == 0 ? 0 : 1);
        start = ((page - 1) * numperPage) + 1;
        if (page * numperPage > numPs) {
            end = numPs;
        } else {
            end = page * numperPage;
        }
        //Code phan trang
        List<EmployeeInfo> result = employeeService.getListEmployeeByPaging(start, end, txtSearch, active);
        return customList(numPs, page, result);
    }

    @ApiOperation(value = "API Add Employee")
    @PostMapping("/addEmployee")
    public ResponseEntity addEmployee(@RequestBody EmployeeInsertDTO employeeInsertDTO) throws Exception {
        UserInfo userInfo = getCurrentUser();
        employeeInsertDTO.setCreateByUser(userInfo.getUserId());
        employeeInsertDTO.setEmployeeId(0L);
        Map<String, Object> rs = employeeService.checkHrEmployee(employeeInsertDTO);
        if (rs != null && rs.get("result") != null) {
            return errorWithDataApi(rs, "Lỗi thông tin");
        }
        Long result = employeeService.insertEmployee(employeeInsertDTO);
        insertLogs("ADD EMPLOYEE: " + employeeInsertDTO.getUsername());
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Update Employee")
    @PostMapping("/updateEmployee")
    public ResponseEntity updateEmployee(@RequestBody EmployeeInsertDTO employeeInsertDTO) throws Exception {
        Map<String, Object> rs = employeeService.checkHrEmployee(employeeInsertDTO);
        if (rs != null && rs.get("result") != null) {
            return errorWithDataApi(rs, "Lỗi thông tin");
        }
        employeeService.updateEmployee(employeeInsertDTO);
        insertLogs("UPDATE EMPLOYEE: " + employeeInsertDTO.getEmployeeId());
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API Change Status Employee")
    @PostMapping("/changeStatusEmployee")
    public ResponseEntity changeStatusEmployee(@RequestParam Long id) throws Exception {
        employeeService.changeStatusEmployee(id);
        insertLogs("CHANGE STATUS EMPLOYEE: " + id);
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API Get Details Employee By ID")
    @GetMapping("/getDetailsEmployeeById")
    public ResponseEntity getDetailsEmployeeById(@RequestParam Long id) throws Exception {
        return successApi(employeeService.getDetailsEmployeeById(id), "Thành công");
    }

    @ApiOperation(value = "API Get Details Role Employee")
    @GetMapping("/getDetailsRoleEmployeeById")
    public ResponseEntity getDetailsRoleEmployeeById(@RequestParam Long appId, @RequestParam Long page, @RequestParam Long limit, @RequestParam Long id, String txtSearch) throws Exception {
        PagingResponse result = employeeService.getRoleInfomation(appId, page, limit, id, txtSearch);
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Get Details BuuCuc,ChucDanh Employee by ID")
    @GetMapping("/getDetailsBuuCucEmployeeById")
    public ResponseEntity getDetailsBuuCucEmployeeById(@RequestParam Long page, @RequestParam Long limit, @RequestParam Long id, String txtSearch) throws Exception {
        PagingResponse result = employeeService.getListBuuCucOfEmployeeByPaging(page, limit, id, txtSearch);
        return successApi(result, "Thành công");
    }

    @ApiOperation(value = "API Insert New BuuCuc/ChucDanh Employee")
    @PostMapping("/addNewBuuCucEmployee")
    public ResponseEntity addNewBuuCucEmployee(@RequestBody EmployeeInsertBC employeeInsertBC) throws Exception {
        List<BuuCucEmployeeDTO> result = employeeService.getListBuuCucOfEmployee(employeeInsertBC.getUserId(), null);
        for (BuuCucEmployeeDTO bc : result) {
            if (bc.getMA_BUUCUC().equals(employeeInsertBC.getMA_BUUCUC())) {
                return errorApi("Đã tồn tại bưu cục trong hệ thống. Mỗi bưu cục chỉ được gán với một chức danh.");
            }
        }
        employeeService.insertNewBuuCucEmployee(employeeInsertBC);
        insertLogs("ADD NEW BUU CUC EMPLOYEE: " + employeeInsertBC.getUserId());
        return successApi(null, "Thành công");
    }


    @ApiOperation(value = "API Get List Hop Dong Lao Dong")
    @GetMapping("/getListHopDongLaoDong")
    public ResponseEntity getListHopDongLaoDong() throws Exception {
        List<HopDongLaoDongInfo> result = employeeService.getListHopDongLaoDong();
        return successApi(result, "Thành công");
    }


    @ApiOperation(value = "API Insert/Update Role of Employee")
    @PostMapping("/insertOrUpdateRoleEmployee")
    public ResponseEntity insertOrUpdateRoleEmployee(@RequestBody RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception {
        List<RoleEmployeeInfo> listRole = employeeService.getListRoleOfEmployee(roleInsertEmployeeDTO.getUserId(), roleInsertEmployeeDTO.getAppId());
        //Get List Role info from RoleList
        String result = "";
        if (listRole.size() > 0) {
            for (RoleEmployeeInfo role : listRole) {
                if (role.getAppId().equals(roleInsertEmployeeDTO.getAppId())) {
                    if (!Utils.isNullOrEmpty(role.getRoleList())) {
                        String[] rol = role.getRoleList().split(",");
                        String[] rolNew = roleInsertEmployeeDTO.getRoleList().split(",");
                        if (role.getAppId().equals(roleInsertEmployeeDTO.getAppId())) {
                            for (String trs : rol) {
                                for (String str : rolNew) {
                                    if (str.equals(trs)) {
                                        return errorApi("Role quyền đã tồn tại trong hệ thống.");
                                    }
                                }
                                result = result + trs + ",";
                            }
                        }
                    }
                } else {
                    employeeService.insertRoleForEmployee(roleInsertEmployeeDTO);
                }
            }
        }

        result = result.replaceAll(",$", "");
        result = result + "," + roleInsertEmployeeDTO.getRoleList();
        result = result.replaceAll(",$", "");
        if (result.startsWith(",")) {
            result = result.substring(1, result.length());
        }
        roleInsertEmployeeDTO.setRoleList(result);

        if (listRole.size() > 0) {
            employeeService.updateRoleEmployee(roleInsertEmployeeDTO);
        } else {
            employeeService.insertRoleForEmployee(roleInsertEmployeeDTO);
        }
        insertLogs("INSERT OR UPDATE ROLE EMPLOYEE: " + roleInsertEmployeeDTO.getUserId());
        return successApi(null, "Thành công");
    }


    @ApiOperation(value = "API Insert/Update Role of Employee3")
    @PostMapping("/insertOrUpdateRoleEmployee3")
    public ResponseEntity insertOrUpdateRoleEmployee3(@RequestBody List<RoleInsertEmployeeDTO> roleInsertEmployeeDTOx) throws Exception {
        for (RoleInsertEmployeeDTO roleInsertEmployeeDTO: roleInsertEmployeeDTOx) {
        List<RoleEmployeeInfo> listRole = employeeService.getListRoleOfEmployee(roleInsertEmployeeDTO.getUserId(), roleInsertEmployeeDTO.getAppId());
        //Get List Role info from RoleList
        String result = "";
        if (listRole.size() > 0) {
            for (RoleEmployeeInfo role : listRole) {
                if (role.getAppId().equals(roleInsertEmployeeDTO.getAppId())) {
                    if (!Utils.isNullOrEmpty(role.getRoleList())) {
                        String[] rol = role.getRoleList().split(",");
                        String[] rolNew = roleInsertEmployeeDTO.getRoleList().split(",");
                        if (role.getAppId().equals(roleInsertEmployeeDTO.getAppId())) {
                            for (String trs : rol) {
                                for (String str : rolNew) {
                                    if (str.equals(trs)) {
                                        return errorApi("Role quyền đã tồn tại trong hệ thống.");
                                    }
                                }
                                result = result + trs + ",";
                            }
                        }
                    }
                } else {
                    employeeService.insertRoleForEmployee(roleInsertEmployeeDTO);
                }
            }
        }

        result = result.replaceAll(",$", "");
        result = result + "," + roleInsertEmployeeDTO.getRoleList();
        result = result.replaceAll(",$", "");
        if (result.startsWith(",")) {
            result = result.substring(1, result.length());
        }
        roleInsertEmployeeDTO.setRoleList(result);

        if (listRole.size() > 0) {
            employeeService.updateRoleEmployee(roleInsertEmployeeDTO);
        } else {
            employeeService.insertRoleForEmployee(roleInsertEmployeeDTO);
        }
        insertLogs("INSERT OR UPDATE ROLE EMPLOYEE: " + roleInsertEmployeeDTO.getUserId());
        }
        return successApi(null, "Thành công");
    }

        @ApiOperation(value = "API Delete BuuCuc/ChucDanh Employee")
    @PostMapping("/deleteBuuCucEmployee")
    public ResponseEntity deleteBuuCucEmployee(@RequestBody EmployeeDelelteBCDTO employeeDelelteBCDTO) throws Exception {
        employeeService.deleteBuuCucEmployee(employeeDelelteBCDTO);
        insertLogs("DELELE BUU CUC EMPLOYEE: " +employeeDelelteBCDTO.getUserId());
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API Delete Role Of Employee")
    @PostMapping("/deleteRoleOfEmployee")
    public ResponseEntity deleteRoleOfEmployee(@RequestBody RoleDeleteDTO roleDeleteDTO) throws Exception {
        List<RoleEmployeeInfo> listRole = employeeService.getListRoleOfEmployee(roleDeleteDTO.getUserId(), roleDeleteDTO.getAppId());
        //Get List Role info from RoleList
        String result = "";
        boolean check = false;
        for (RoleEmployeeInfo role : listRole) {
            if (!Utils.isNullOrEmpty(role.getRoleList())) {
                String[] rol = role.getRoleList().split(",");
                for (String trs : rol) {
                    if (!trs.equals(roleDeleteDTO.getRoleId())) {
                        result = result + trs + ",";
                    }
                }
                if (rol[rol.length - 1].equals(',')) {
                    rol[rol.length - 1] = null;
                }
            }
        }
        result = result.replaceAll(",$", "");
        RoleInsertEmployeeDTO roleInsertEmployeeDTO = new RoleInsertEmployeeDTO();
        roleInsertEmployeeDTO.setAppId(roleDeleteDTO.getAppId());
        roleInsertEmployeeDTO.setUserId(roleDeleteDTO.getUserId());
        roleInsertEmployeeDTO.setRoleList(result);
        employeeService.updateRoleEmployee(roleInsertEmployeeDTO);
        insertLogs("DELETE ROLE EMPLOYEE: " + roleInsertEmployeeDTO.getUserId());
        return successApi(null, "Thành công");
    }

    @ApiOperation(value = "API Edit Role Of Employee")
    @PostMapping("/editRoleOfEmployee")
    public ResponseEntity editRoleOfEmployee(@RequestBody RoleEditDTO roleEditDTO) throws Exception {
        List<RoleEmployeeInfo> listRole = employeeService.getListRoleOfEmployee(roleEditDTO.getUserId(), roleEditDTO.getAppId());
        //Get List Role info from RoleList
        String result = "";
        boolean check = false;
        //Delele Old Role from List
        for (RoleEmployeeInfo role : listRole) {
            String[] rol = role.getRoleList().split(",");
            for (String trs : rol) {
                if (!trs.equals(roleEditDTO.getOldRoleId())) {
                    result = result + trs + ",";
                }
            }
            if (rol[rol.length - 1].equals(',')) {
                rol[rol.length - 1] = null;
            }
        }
        //Check role & insert
        String[] rolNew = roleEditDTO.getNewRoleId().split(",");
        String[] rolOld = result.split(",");
        for (String old : rolOld) {
            for (String news : rolNew) {
                if (old.equals(news)) {
                    return errorApi("Role quyền đã tồn tại");
                }
            }
        }
        result = result + roleEditDTO.getNewRoleId();
        result = result.replaceAll(",$", "");
        RoleInsertEmployeeDTO roleInsertEmployeeDTO = new RoleInsertEmployeeDTO();
        roleInsertEmployeeDTO.setAppId(roleEditDTO.getAppId());
        roleInsertEmployeeDTO.setUserId(roleEditDTO.getUserId());
        roleInsertEmployeeDTO.setRoleList(result);
        employeeService.updateRoleEmployee(roleInsertEmployeeDTO);
        insertLogs("EDIT ROLE EMPLOYEE: " + roleInsertEmployeeDTO.getUserId());
        return successApi(null, "Thành công");
    }


    @ApiOperation(value = "API Set Role To Many Users")
    @PostMapping("/setRoleManyUsers")
    public ResponseEntity setRoleManyUsers(@RequestBody List<RoleInsertEmployeeDTO> roleInsertEmployeeDTOS) throws Exception {
        String listUser = "";
        for (RoleInsertEmployeeDTO role : roleInsertEmployeeDTOS) {
            List<RoleEmployeeInfo> listRole = employeeService.getListRoleOfEmployee(role.getUserId(), role.getAppId());
            //Get List Role info from RoleList
            String result = "";
            listUser = listUser + role.getUserId() + ",";
            if (listRole.size() > 0) {
                for (RoleEmployeeInfo r : listRole) {
                    if (r.getAppId().equals(role.getAppId())) {
                        if (!Utils.isNullOrEmpty(r.getRoleList())) {
                            String[] rol = r.getRoleList().split(",");
                            String[] rolNew = role.getRoleList().split(",");
                            if (r.getAppId().equals(role.getAppId())) {
                                for (String trs : rol) {
                                    for (String str : rolNew) {
                                        if (str.equals(trs)) {
                                            return errorApi("Role quyền đã tồn tại trong hệ thống.");
                                        }
                                    }
                                    result = result + trs + ",";
                                }
                            }
                        }
                    } else {
                        employeeService.insertRoleForEmployee(role);
                    }
                }
            }

            result = result.replaceAll(",$", "");
            result = result + "," + role.getRoleList();
            result = result.replaceAll(",$", "");
            if (result.startsWith(",")) {
                result = result.substring(1, result.length());
            }
            role.setRoleList(result);

            if (listRole.size() > 0) {
                employeeService.updateRoleEmployee(role);
            } else {
                employeeService.insertRoleForEmployee(role);
            }
        }

        insertLogs("SET ROLE MANY USERS EMPLOYEE: " + listUser);
        return successApi(null, "Thành công");
    }

    public void insertLogs(String action) throws Exception {
        Logs logs = new Logs();
        logs.setUserName(getCurrentUser().getUserId()+"");
        logs.setChucNang(chucNang);
        logs.setActionLog(action);
        employeeService.insertLogs(logs);
    }

}
