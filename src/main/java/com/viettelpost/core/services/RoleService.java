package com.viettelpost.core.services;

import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.RoleInfo;
import com.viettelpost.core.services.dtos.RoleOfModuleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {
    void insertRole(String name, String description, Long status) throws Exception;

    List<RoleInfo> getListAllRole();

    void deleteRole(Long id);

    int getTotalRole(String txtSearch) throws Exception;

    List<RoleInfo> getAllRole(int RowStart, int RowEnd, String txtSearch);

    List<RoleInfo> getAllRoleByUserId(String userName, Long appId) throws VtException;

    List<RoleInfo> getAllRoleNotGrantByUserId(Long userId, Long appId);

    List<RoleInfo> getAllRoleByAppID(Long appId, int RowStart, int RowEnd, String txtSearch);

    void updateRole(String name, String description, Long status, Long id);

    void changeStatusRole(Long id);

    void insertModuleToRole(Long appId, Long roleId, Long moduleId);

    void deleteModuleOfRole(Long appId, Long roleId, Long moduleId);

    List<RoleOfModuleDTO> getListAllModuleOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch);

    List<RoleOfModuleDTO> getListAllModuleNotOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch);

    int getTotalRoleOfModule(Long roleId, Long appId, String txtSearch) throws Exception;

    int getTotalRoleNotOfModule(Long roleId, Long appId, String txtSearch) throws Exception;

    void insertLogs(Logs logs) throws Exception;

    List<RoleInfo> getAllRoleByEmployeeCode(String employeeCode, Long appId) throws VtException;

}
