package com.viettelpost.core.services.impl;

import com.viettelpost.core.controller.request.ModuleRequest;
import com.viettelpost.core.services.ModuleService;
import com.viettelpost.core.services.daos.ModuleDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.ModuleInfo;
import com.viettelpost.core.services.dtos.ModuleInforDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleImpl implements ModuleService {
    ModuleDAO moduleDAO;

    @Autowired
    public ModuleImpl(ModuleDAO moduleDAO) {
        this.moduleDAO = moduleDAO;
    }


    @Override
    public List<ModuleInfo> listModuleByRole(String roleList, Long appId) {
        return moduleDAO.listModuleByRole(roleList, appId);
    }

    @Override
    public List<ModuleInfo> getChildrenModuleByModuleID(Long ModuleID, Long AppID) {
        return moduleDAO.getChilrdModuleById(ModuleID, AppID);
    }

    @Override
    public List<ModuleInfo> getListModuleByAppID(Long appId, String txtSearch) {
        return moduleDAO.getListModuleByAppID(appId, txtSearch);
    }

    @Override
    public void insertModule(ModuleRequest moduleRequest, Long App_ID) {
        moduleDAO.insertModule(moduleRequest, App_ID);
    }

    @Override
    public List<ModuleInfo> deleteModuleById(Long moduleId) {
        return moduleDAO.deleteModuleById(moduleId);
    }

    @Override
    public void updateModule(ModuleRequest moduleInfo) {
        moduleDAO.updateModule(moduleInfo);
    }

    @Override
    public List<ModuleInfo> getListModuleByAppIDPaging(int start, int end, Long appId, String txtSearch) {
        return moduleDAO.getListModuleByAppIDPaging(start, end, appId, txtSearch);
    }

    @Override
    public List<ModuleInfo> listModuleByRolePaging(Long roleId, int rowStart, int rowEnd, String txtSearch) {
        return moduleDAO.listModuleByRolePaging(roleId, rowStart, rowEnd, txtSearch);
    }

    @Override
    public int getTotalModule(Long roleId, String txtSearch) throws Exception {
        return moduleDAO.getTotalModule(roleId, txtSearch);
    }

    @Override
    public List<ModuleInforDTO> getModuleInfo(Long ModuleId, Long appId) {
        return moduleDAO.getModuleInfo(ModuleId,appId);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        moduleDAO.insertLogs(logs);
    }


}
