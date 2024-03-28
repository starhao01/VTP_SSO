package com.viettelpost.core.services;

import com.viettelpost.core.services.domains.AppInfo;
import com.viettelpost.core.services.domains.Logs;

import java.util.List;

public interface AppService {

    List<AppInfo> getAllAppInfoByUserID(Long userid);

    List<AppInfo> getAllApp();

    List<AppInfo> getAllAppByRole(long roleId);

    List<AppInfo> getAllAppPaging(int rowStart, int rowEnd, String txtSearch);

    void insertApp(String name, String desription, String Code) throws Exception;

    void deleteApp(Long id);

    void updateApp(String name, String description, String Code, Long id) throws Exception;

    int getTotalApp(String txtSearch) throws Exception;

    public void insertLogs(Logs logs) throws Exception;

}
