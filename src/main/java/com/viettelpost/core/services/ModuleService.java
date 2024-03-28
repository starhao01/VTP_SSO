package com.viettelpost.core.services;

import com.viettelpost.core.controller.request.ModuleRequest;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.ModuleInfo;
import com.viettelpost.core.services.dtos.ModuleInforDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ModuleService {
    List<ModuleInfo> listModuleByRole(String roleList, Long appId);

    List<ModuleInfo> getChildrenModuleByModuleID(Long ModuleID, Long AppID);

    List<ModuleInfo> getListModuleByAppID(Long appId, String txtSearch);

    void insertModule(ModuleRequest moduleRequest, Long App_ID);

    List<ModuleInfo> deleteModuleById(Long moduleId);

    void updateModule(ModuleRequest moduleInfo);

    List<ModuleInfo> getListModuleByAppIDPaging(int start, int end, Long appId, String txtSearch);

    List<ModuleInfo> listModuleByRolePaging(Long roleId, int rowStart, int rowEnd, String txtSearch);

    int getTotalModule(Long roleId, String txtSearch) throws Exception;

    List<ModuleInforDTO> getModuleInfo(Long ModuleId, Long appId);

    void insertLogs(Logs logs) throws Exception;
}
