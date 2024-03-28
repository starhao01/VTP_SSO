package com.viettelpost.core.services.impl;

import com.viettelpost.core.services.AppService;
import com.viettelpost.core.services.daos.AppDAO;
import com.viettelpost.core.services.domains.AppInfo;
import com.viettelpost.core.services.domains.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppImpl implements AppService {

    AppDAO appDAO;

    @Autowired
    public AppImpl(AppDAO appDAO) {
        this.appDAO = appDAO;
    }

    @Override
    public List<AppInfo> getAllAppInfoByUserID(Long userid) {
        return appDAO.getAllAppInfoByUserID(userid);
    }

    @Override
    public List<AppInfo> getAllApp() {
        return appDAO.getAllApp();
    }

    @Override
    public List<AppInfo> getAllAppByRole(long roleId) {
        return appDAO.getAllAppByRole(roleId);
    }

    @Override
    public List<AppInfo> getAllAppPaging(int rowStart, int rowEnd, String txtSearch) {
        return appDAO.getAllAppPaging(rowStart, rowEnd, txtSearch);
    }

    @Override
    public void insertApp(String name, String desription, String Code) throws Exception {
        appDAO.insertApp(name, desription, Code);
    }

    @Override
    public void deleteApp(Long id) {
        appDAO.deleteApp(id);
    }

    @Override
    public void updateApp(String name, String description, String Code, Long id) throws Exception {
        appDAO.updateApp(name, description, Code, id);
    }

    @Override
    public int getTotalApp(String txtSearch) throws Exception {
        return appDAO.getTotalApp(txtSearch);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        appDAO.insertLogs(logs);
    }

}
