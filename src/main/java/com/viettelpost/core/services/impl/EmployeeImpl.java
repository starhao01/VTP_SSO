package com.viettelpost.core.services.impl;

import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.EmployeeService;
import com.viettelpost.core.services.daos.EmployeeDAO;
import com.viettelpost.core.services.domains.*;
import com.viettelpost.core.services.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeImpl implements EmployeeService {

    @Autowired
    EmployeeDAO employeeDAO;

    @Autowired
    public EmployeeImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    @Override
    public List<EmployeeInfo> getListEmployeeByPaging(int start, int end, String txtSearch, Long active) throws VtException {
        return employeeDAO.getListEmployeeByPaging(start, end, txtSearch, active);
    }

    @Override
    public int totalEmployee(String txtSearch, Long active) throws Exception {
        return employeeDAO.getTotalEmployee(txtSearch, active);
    }

    @Override
    public Long insertEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        return employeeDAO.insertEmployee(employeeInsertDTO);
    }

    @Override
    public void updateEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        employeeDAO.updateEmployee(employeeInsertDTO);
    }

    @Override
    public void changeStatusEmployee(Long id) {
        employeeDAO.changeStatusEmployee(id);
    }


    @Override
    public EmployeeDetailsDTO getDetailsEmployeeById(Long id) {
        return employeeDAO.getDetailsEmployeeById(id);
    }

    @Override
    public List<RoleEmployeeInfo> getListRoleOfEmployee(Long id, Long appId) {
        return employeeDAO.getListRoleOfEmployee(id, appId);
    }


    @Override
    public PagingResponse getRoleInfomation(Long appId, Long pageNumber, Long limit, Long id, String txtSearch) throws Exception {
        return employeeDAO.getRoleInfomation(appId, pageNumber, limit, id, txtSearch);
    }

    @Override
    public List<BuuCucEmployeeDTO> getListBuuCucOfEmployee(Long id, String txtSearch) throws VtException {
        return employeeDAO.getListBuuCucOfEmployee(id, txtSearch);
    }

    @Override
    public PagingResponse getListBuuCucOfEmployeeByPaging(Long pageNumber, Long limit, Long id, String txtSearch) throws Exception {
        return employeeDAO.getListBuuCucOfEmployeeByPaging(pageNumber, limit, id, txtSearch);
    }

    @Override
    public void insertNewBuuCucEmployee(EmployeeInsertBC em) throws Exception {
        employeeDAO.insertNewBuuCucEmployee(em);
    }

    @Override
    public ByteArrayInputStream getListEmployeeAll() throws IOException {
        return employeeDAO.getListEmployeeAll();
    }

    @Override
    public List<HopDongLaoDongInfo> getListHopDongLaoDong() throws VtException {
        return employeeDAO.getListHopDongLaoDong();
    }

    @Override
    public void insertRoleForEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception {
        employeeDAO.insertRoleForEmployee(roleInsertEmployeeDTO);
    }

    @Override
    public void updateRoleEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception {
        employeeDAO.updateRoleEmployee(roleInsertEmployeeDTO);
    }

    @Override
    public void deleteBuuCucEmployee(EmployeeDelelteBCDTO employeeDelelteBCDTO) throws Exception {
        employeeDAO.deleteBuuCucEmployee(employeeDelelteBCDTO);
    }

    @Override
    public Map<String, Object> checkHrEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        return employeeDAO.checkHrEmployee(employeeInsertDTO);
    }

    @Override
    public void insertLogs(Logs logs) throws Exception {
        employeeDAO.insertLogs(logs);
    }


}
