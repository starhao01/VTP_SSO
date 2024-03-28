package com.viettelpost.core.services.impl;

import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.RoleService;
import com.viettelpost.core.services.daos.RoleDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.RoleInfo;
import com.viettelpost.core.services.dtos.RoleOfModuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleImpl implements RoleService {

    @Autowired
    RoleDAO roleDAO;


    @Override
    public void insertRole(String name, String description, Long status) throws Exception {
        roleDAO.insertRole(name, description, status);
    }

    @Override
    public List<RoleInfo> getListAllRole() {
        return roleDAO.getListAllRole();
    }

    @Override
    public void deleteRole(Long id) {
        roleDAO.deleteRole(id);
    }


    @Override
    public int getTotalRole(String txtSearch) throws Exception {
        return roleDAO.getTotalRole(txtSearch);
    }

    @Override
    public List<RoleInfo> getAllRole(int RowStart, int RowEnd, String txtSearch) {
        return roleDAO.getAllRole(RowStart, RowEnd, txtSearch);
    }

    @Override
    public List<RoleInfo> getAllRoleByUserId(String userName, Long appId) throws VtException {
        return roleDAO.getAllRoleByUserId(userName, appId);
    }

    @Override
    public List<RoleInfo> getAllRoleNotGrantByUserId(Long userId, Long appId) {
        return roleDAO.getAllRoleNotGrantByUserId(userId, appId);
    }

    @Override
    public List<RoleInfo> getAllRoleByAppID(Long appId, int RowStart, int RowEnd, String txtSearch) {
        return roleDAO.getAllRoleByAppID(appId, RowStart, RowEnd, txtSearch);
    }

    @Override
    public void updateRole(String name, String description, Long status, Long id) {
        roleDAO.updateRole(name, description, status, id);
    }

    @Override
    public void changeStatusRole(Long id) {
        roleDAO.changeStatusRole(id);
    }

    @Override
    public void insertModuleToRole(Long appId, Long roleId, Long moduleId) {
        roleDAO.insertModuleToRole(appId, roleId, moduleId);
    }

    @Override
    public void deleteModuleOfRole(Long appId, Long roleId, Long moduleId) {
        roleDAO.deleteModuleOfRole(appId, roleId, moduleId);
    }

    @Override
    public List<RoleOfModuleDTO> getListAllModuleOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch) {
        return roleDAO.getListAllModuleOfRolePaging(roleId, appId, rowStart, rowEnd, txtSearch);
    }

    @Override
    public List<RoleOfModuleDTO> getListAllModuleNotOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch) {
        return roleDAO.getListAllModuleNotOfRolePaging(roleId, appId, rowStart, rowEnd, txtSearch);
    }


    @Override
    public int getTotalRoleOfModule(Long roleId, Long appId, String txtSearch) throws Exception {
        return roleDAO.getTotalRoleOfModule(roleId, appId, txtSearch);
    }

    @Override
    public int getTotalRoleNotOfModule(Long roleId, Long appId, String txtSearch) throws Exception {
        return roleDAO.getTotalRoleNotOfModule(roleId, appId, txtSearch);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        roleDAO.insertLogs(logs);
    }

    @Override
    public List<RoleInfo> getAllRoleByEmployeeCode(String employeeCode, Long appId) throws VtException {
        return roleDAO.getAllRoleByEmployeeCode(employeeCode,appId);
    }


}
