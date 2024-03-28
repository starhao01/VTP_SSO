package com.viettelpost.core.services;

import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.domains.*;
import com.viettelpost.core.services.dtos.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface EmployeeService {

    List<EmployeeInfo> getListEmployeeByPaging(int start, int end, String txtSearch, Long active) throws VtException;

    int totalEmployee(String txtSearch, Long active) throws Exception;

    Long insertEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception;

    void updateEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception;

    void changeStatusEmployee(Long id);

    EmployeeDetailsDTO getDetailsEmployeeById(Long id);

    List<RoleEmployeeInfo> getListRoleOfEmployee(Long id, Long appId);

    PagingResponse getRoleInfomation(Long appId,Long pageNumber, Long limit, Long id, String txtSearch) throws Exception;

    List<BuuCucEmployeeDTO> getListBuuCucOfEmployee(Long id, String txtSearch) throws VtException;

    PagingResponse getListBuuCucOfEmployeeByPaging(Long pageNumber, Long limit, Long id, String txtSearch) throws Exception;

    void insertNewBuuCucEmployee(EmployeeInsertBC em) throws Exception;

    ByteArrayInputStream getListEmployeeAll() throws IOException;

    List<HopDongLaoDongInfo> getListHopDongLaoDong() throws VtException;

    void insertRoleForEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception;

    void updateRoleEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception;

    void deleteBuuCucEmployee(EmployeeDelelteBCDTO employeeDelelteBCDTO) throws Exception;

    Map<String, Object> checkHrEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception;

    void insertLogs(Logs logs) throws Exception;

}
