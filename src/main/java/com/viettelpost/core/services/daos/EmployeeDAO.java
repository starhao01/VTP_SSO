package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.domains.*;
import com.viettelpost.core.services.dtos.*;
import com.viettelpost.core.utils.EncryptionUtil;
import com.viettelpost.core.utils.Utils;
import oracle.jdbc.internal.OracleTypes;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.util.CellUtil.createCell;

@Service
public class EmployeeDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public EmployeeDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<EmployeeInfo> getListEmployeeByPaging(int start, int end, String txtSearch, Long active) {
        List<EmployeeInfo> rs = new ArrayList<>();
        String sql = "SELECT rn,\n" +
                "    EMPLOYEE_ID,USERNAME,\n" +
                "    EMPLOYEECODE,\n" +
                "   DISPLAYNAME,           \n" +
                "     IDENTIFIERCREATEONDATE,\n" +
                "      email,\n" +
                "      ISDELETED\n" +
                "     FROM (\n" +
                "    SELECT ROW_NUMBER() OVER (\n" +
                "   ORDER BY EMPLOYEE_ID DESC   \n" +
                "     ) AS rn,        \n" +
                "   (select EMPLOYEE_JOB_ID from ERP_HR.HR_EMPLOYEE_JOB where ERP_HR.HR_EMPLOYEE_JOB.EMPLOYEE_ID = a.EMPLOYEE_ID and POST_CODE LIKE '%' and rownum = 1) USERID,\n" +
                "    EMPLOYEE_ID,USERNAME,\n" +
                "    email,\n" +
                "    EMPLOYEECODE,\n" +
                "                                                      DISPLAYNAME,\n" +
                "   ISDELETED,\n" +
                "   TO_CHAR(BIRTHDATE, 'DD/MM/YYYY') as BIRTHDATE,                                               \n" +
                "   TO_CHAR(IDENTIFIERCREATEONDATE, 'DD/MM/YYYY') as IDENTIFIERCREATEONDATE      \n" +
                "   FROM  ERP_HR.HR_EMPLOYEE  A  \n" +
                "    ";
        if (txtSearch != null) {
            sql = sql + "WHERE (UPPER(DISPLAYNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(USERNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(EMPLOYEECODE) LIKE UPPER('%" + txtSearch + "%') or UPPER(PHONE) LIKE UPPER('%" + txtSearch + "%') or UPPER(email) LIKE UPPER('%" + txtSearch + "%') ) ";
        }
        if (active == 0 && txtSearch != null) {
            sql = sql + "and ISDELETED ='N' ";
        }
        if (active == 1 && txtSearch != null) {
            sql = sql + "and ISDELETED ='Y' ";
        }
        if (active == 0 && txtSearch == null) {
            sql = sql + "WHERE ISDELETED ='N' ";
        }
        if (active == 1 && txtSearch == null) {
            sql = sql + "WHERE ISDELETED ='Y' ";
        }
        sql = sql + ") t\n"
                +
                "                                                WHERE rn between ? and  ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("EMPLOYEE_ID", new LongType())
                .addScalar("USERNAME", new StringType())
                .addScalar("EMPLOYEECODE", new StringType())
                .addScalar("DISPLAYNAME", new StringType())
                .addScalar("IDENTIFIERCREATEONDATE", new StringType())
                .addScalar("EMAIL", new StringType())
                .addScalar("ISDELETED", new StringType());
        query.setParameter(1, start)
                .setParameter(2, end);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            EmployeeInfo data = new EmployeeInfo();
            data.setId((Long) objs[0]);
            data.setUser_name((String) objs[1]);
            data.setEmployee_code((String) objs[2]);
            data.setDisplay_name((String) objs[3]);
            data.setIdentifiercreateondate((String) objs[4]);
            data.setEmail((String) objs[5]);
            data.setActive((String) objs[6]);
            rs.add(data);
        }
        return rs;
    }


    @Transactional
    public ByteArrayInputStream getListEmployeeAll() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            String txtSearch = null;
            Long active = 0L;
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet;
            sheet = workbook.createSheet("EmployeeInfo");
            Row row = sheet.createRow(0);
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontHeight(16);
            style.setFont(font);
            createCell(row, 0, "ID", style);
            createCell(row, 1, "Username", style);
            createCell(row, 2, "Employee Code", style);
            createCell(row, 3, "Display Name", style);
            createCell(row, 4, "Email", style);
            createCell(row, 5, "IdentifierCreateOnDate", style);
            createCell(row, 6, "Active", style);
            int rowCount = 1;
            CellStyle style2 = workbook.createCellStyle();
            XSSFFont font2 = workbook.createFont();
            font.setFontHeight(14);
            style.setFont(font);
            List<EmployeeInfo> rs = new ArrayList<>();
            String sql = "SELECT rn,\n" +
                    "                                                        EMPLOYEE_ID,USERNAME,\n" +
                    "                                                       EMPLOYEECODE,\n" +
                    "                                                      DISPLAYNAME,           \n" +
                    "                                                        IDENTIFIERCREATEONDATE,\n" +
                    "                                                        email,\n" +
                    "                                                        ISDELETED\n" +
                    "                                                 FROM (\n" +
                    "                                                 SELECT ROW_NUMBER() OVER (\n" +
                    "                                                 ORDER BY EMPLOYEE_ID DESC   \n" +
                    "                                                    ) AS rn,        \n" +
                    "                                                      (select EMPLOYEE_JOB_ID from ERP_HR.HR_EMPLOYEE_JOB where ERP_HR.HR_EMPLOYEE_JOB.EMPLOYEE_ID = a.EMPLOYEE_ID and POST_CODE LIKE '%' and rownum = 1) USERID,\n" +
                    "                                                       EMPLOYEE_ID,USERNAME,\n" +
                    "                                                       email,\n" +
                    "                                                       EMPLOYEECODE,\n" +
                    "                                                      DISPLAYNAME,\n" +
                    "                                                      ISDELETED,\n" +
                    "                                                       TO_CHAR(BIRTHDATE, 'DD/MM/YYYY') as BIRTHDATE,                                               \n" +
                    "                                                       TO_CHAR(IDENTIFIERCREATEONDATE, 'DD/MM/YYYY') as IDENTIFIERCREATEONDATE      \n" +
                    "                                                     FROM  ERP_HR.HR_EMPLOYEE  A  \n" +
                    "                                                  ";
            if (txtSearch != null) {
                sql = sql + "WHERE (UPPER(DISPLAYNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(USERNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(EMPLOYEECODE) LIKE UPPER('%" + txtSearch + "%') or UPPER(IDENTIFIERCREATEONDATE) LIKE UPPER('%" + txtSearch + "%')) ";
            }
            if (active == 0 && txtSearch != null) {
                sql = sql + "and ISDELETED ='N' ";
            }
            if (active == 1 && txtSearch != null) {
                sql = sql + "and ISDELETED ='Y' ";
            }
            if (active == 0 && txtSearch == null) {
                sql = sql + "WHERE ISDELETED ='N' ";
            }
            if (active == 1 && txtSearch == null) {
                sql = sql + "WHERE ISDELETED ='Y' ";
            }
            sql = sql + ") t where rn>=1 and rn <= 1000\n";
            Query query = getSession(sessionFactory).createSQLQuery(sql)
                    .addScalar("EMPLOYEE_ID", new LongType())
                    .addScalar("USERNAME", new StringType())
                    .addScalar("EMPLOYEECODE", new StringType())
                    .addScalar("DISPLAYNAME", new StringType())
                    .addScalar("IDENTIFIERCREATEONDATE", new StringType())
                    .addScalar("EMAIL", new StringType())
                    .addScalar("ISDELETED", new StringType());
            List<Object[]> list = query.getResultList();
            for (Object[] objs : list) {
                Row row2 = sheet.createRow(rowCount++);
                int columnCount = 0;
                createCell(row2, columnCount++, ((Long) objs[0]).toString(), style2);
                createCell(row2, columnCount++, (String) objs[1], style2);
                createCell(row2, columnCount++, (String) objs[2], style2);
                createCell(row2, columnCount++, (String) objs[3], style2);
                createCell(row2, columnCount++, (String) objs[5], style2);
                createCell(row2, columnCount++, (String) objs[4], style2);
                createCell(row2, columnCount++, (String) objs[6], style2);
            }
            workbook.write(out);
            workbook.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public int getTotalEmployee(String txtSearch, Long active) throws Exception {
        String sql = "select count(*) as Total from (SELECT \n" +
                "                                        rn,\n" +
                "                                        USERNAME,\n" +
                "                                        EMPLOYEECODE,\n" +
                "                                        DISPLAYNAME,           \n" +
                "                                        IDENTIFIERCREATEONDATE  \n" +
                "                                 FROM (\n" +
                "                                  SELECT ROW_NUMBER() OVER (\n" +
                "                                  ORDER BY EMPLOYEE_ID DESC   \n" +
                "                                     ) AS rn,         \n" +
                "                                        (select EMPLOYEE_JOB_ID from ERP_HR.HR_EMPLOYEE_JOB where ERP_HR.HR_EMPLOYEE_JOB.EMPLOYEE_ID = a.EMPLOYEE_ID and POST_CODE LIKE '%' and rownum = 1) USERID,\n" +
                "                                        USERNAME,\n" +
                "                                        EMPLOYEECODE,\n" +
                "                                       DISPLAYNAME,\n" +
                "                                        TO_CHAR(BIRTHDATE, 'DD/MM/YYYY') as BIRTHDATE ,\n" +
                "                                   \n" +
                "                                        TO_CHAR(IDENTIFIERCREATEONDATE, 'DD/MM/YYYY') as IDENTIFIERCREATEONDATE       \n" +
                "                                      FROM  ERP_HR.HR_EMPLOYEE  A  \n";
        if (txtSearch != null) {
            sql = sql + "WHERE (UPPER(DISPLAYNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(USERNAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(EMPLOYEECODE) LIKE UPPER('%" + txtSearch + "%') or UPPER(PHONE) LIKE UPPER('%" + txtSearch + "%') or UPPER(email) LIKE UPPER('%" + txtSearch + "%') or UPPER(IDENTIFIERCREATEONDATE) LIKE UPPER('%" + txtSearch + "%')) ";
        }
        if (active == 0 && txtSearch != null) {
            sql = sql + "and ISDELETED ='N' ";
        }
        if (active == 1 && txtSearch != null) {
            sql = sql + "and ISDELETED ='Y' ";
        }
        if (active == 0 && txtSearch == null) {
            sql = sql + "WHERE ISDELETED ='N' ";
        }
        if (active == 1 && txtSearch == null) {
            sql = sql + "WHERE ISDELETED ='Y' ";
        }
        sql = sql + ") t)\n";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("Total", new IntegerType())
                .uniqueResult();
    }
    @Transactional
    public Long insertEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        String salt = EncryptionUtil.salt();
        Date date = null;
        if (!Utils.isNullOrEmpty(employeeInsertDTO.getBirthdate())){
            date=new SimpleDateFormat("dd/MM/yyyy").parse(employeeInsertDTO.getBirthdate());
        }
        BigDecimal rsArr = (BigDecimal) getResultObject(sessionFactory, "ERP_SSO.PKG_SSO.INSERT_HR_EMPLOYEE",
                Arrays.asList(employeeInsertDTO.getUsername(),
                        employeeInsertDTO.getEmployeeCode(),
                        employeeInsertDTO.getFirstName(),
                        employeeInsertDTO.getLastName(),
                        employeeInsertDTO.getEmail(),
                        Utils.getValid84Phone(employeeInsertDTO.getPhone()),
                        date,
                        employeeInsertDTO.getSex(),
                        employeeInsertDTO.getIdentifierNumber(),
                        employeeInsertDTO.getGroupId(),
                        EncryptionUtil.sha256Encode(employeeInsertDTO.getPassword(), salt),
                        EncryptionUtil.sha256Encode(employeeInsertDTO.getPassword(), salt),
                        salt,
                        employeeInsertDTO.getComment(),
                        employeeInsertDTO.getCreateByUser(),
                        employeeInsertDTO.getIsDeleted()), OracleTypes.NUMBER);
        return rsArr.longValue();
    }

    @Transactional
    public Map<String, Object> checkHrEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.CHECK_HR_EMPLOYEE",
                    Arrays.asList(employeeInsertDTO.getEmployeeId(), employeeInsertDTO.getIdentifierNumber(), employeeInsertDTO.getPhone(),
                            employeeInsertDTO.getEmployeeCode(), employeeInsertDTO.getUsername()),
                    new int[]{OracleTypes.CURSOR});
            if (rsArr[0] != null) {
                try (ResultSet rs = (ResultSet) rsArr[0]) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("result", toJsonObject(rs));
                    return result;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            throw throwException(e);
        }
    }

    @Transactional
    public void updateEmployee(EmployeeInsertDTO employeeInsertDTO) throws Exception {
        Date date = null;
        if (!Utils.isNullOrEmpty(employeeInsertDTO.getBirthdate())){
            date=new SimpleDateFormat("dd/MM/yyyy").parse(employeeInsertDTO.getBirthdate());
        }
        getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.UPDATE_HR_EMPLOYEE",
                Arrays.asList(employeeInsertDTO.getEmployeeId(),
                        employeeInsertDTO.getEmployeeCode(),
                        employeeInsertDTO.getFirstName(),
                        employeeInsertDTO.getLastName(),
                        employeeInsertDTO.getEmail(),
                        employeeInsertDTO.getPhone(),
                        date,
                        employeeInsertDTO.getSex(),
                        employeeInsertDTO.getIdentifierNumber(),
                        employeeInsertDTO.getGroupId(),
                        employeeInsertDTO.getComment(),
                        employeeInsertDTO.getCreateByUser(),
                        employeeInsertDTO.getIsDeleted()), OracleTypes.NULL);
    }

    @Transactional
    public void changeStatusEmployee(Long id) {
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery("UPDATE ERP_HR.HR_EMPLOYEE SET ISDELETED = case when ISDELETED = +" + "'Y'" + " then " + "'N'" + " else " + "'Y'" + " end where EMPLOYEE_ID = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }


    @Transactional
    public EmployeeDetailsDTO getDetailsEmployeeById(Long id) {
        EmployeeDetailsDTO data = new EmployeeDetailsDTO();
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(
                        "select FIRSTNAME,LASTNAME,PHONE,EMPLOYEECODE,USERNAME,USERPASSWORD," +
                                "BIRTHDATE,IDENTIFIERNUMBER,EMAIL,employee_id,EMPLOYEECOMMENT,EMPLOYEE_GROUP_ID" +
                                " from ERP_HR.HR_EMPLOYEE where employee_id = ?")
                .addScalar("FIRSTNAME", new StringType())
                .addScalar("LASTNAME", new StringType())
                .addScalar("PHONE", new StringType())
                .addScalar("EMPLOYEECODE", new StringType())
                .addScalar("USERNAME", new StringType())
                .addScalar("USERPASSWORD", new StringType())
                .addScalar("BIRTHDATE", new StringType())
                .addScalar("IDENTIFIERNUMBER", new StringType())
                .addScalar("EMAIL", new StringType())
                .addScalar("employee_id", new LongType())
                .addScalar("EMPLOYEECOMMENT", new StringType())
                .addScalar("EMPLOYEE_GROUP_ID", new LongType())
                .setParameter(1, id);

        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            data.setFirstname((String) objs[0]);
            data.setLastname((String) objs[1]);
            data.setPhone((String) objs[2]);
            data.setEmployeeCode((String) objs[3]);
            data.setUsername((String) objs[4]);
            data.setUserPassword((String) objs[5]);
            data.setBirthdate((String) objs[6]);
            data.setIdentifierNumber((String) objs[7]);
            data.setEmail((String) objs[8]);
            data.setId((Long) objs[9]);
            data.setComment((String) objs[10]);
            data.setGroupId((Long) objs[11]);
        }
        return data;
    }

    @Transactional
    public List<RoleEmployeeInfo> getListRoleOfEmployee(Long id, Long appId) {
        List<RoleEmployeeInfo> rs = new ArrayList<>();
        org.hibernate.Query query = getSession(sessionFactory)
                .createSQLQuery("select APP_ID,USER_ID,ROLE_LIST,NAME,CODE from ERP_SSO.VP_USER_ROLE a\n" +
                        "join ERP_SSO.VP_APP b on (a.APP_ID = b.ID)\n" +
                        "where user_id = ? and app_id = ?")
                .addScalar("APP_ID", new LongType())
                .addScalar("USER_ID", new LongType())
                .addScalar("ROLE_LIST", new StringType())
                .addScalar("NAME", new StringType())
                .addScalar("CODE", new StringType())
                .setParameter(1, id)
                .setParameter(2, appId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleEmployeeInfo data = new RoleEmployeeInfo();
            data.setAppId((Long) objs[0]);
            data.setUserId((Long) objs[1]);
            data.setRoleList((String) objs[2]);
            data.setNameApp((String) objs[3]);
            data.setCodeApp((String) objs[4]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public PagingResponse getRoleInfomation(Long appId, Long pageNumber, Long limit, Long id, String txtSearch) throws Exception {
        List<Object> list = new ArrayList<>();
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.GET_ROLE_EMPLOYEE_PAGING",
                    Arrays.asList(appId, pageNumber, limit, id, txtSearch),
                    new int[]{OracleTypes.CURSOR, OracleTypes.CURSOR});
            PagingResponse output = new PagingResponse();
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                List<Object> lo1 = toListObject(rs);
                output.setMainData(lo1);
                try (ResultSet rs2 = (ResultSet) rsArr[1]) {
                    Object o2 = toJsonObject(rs2);
                    output.setSideData(o2);
                }
            }
            return output;
        } catch (Exception ex) {
            throw throwException(ex);
        }
    }

    @Transactional
    public List<BuuCucEmployeeDTO> getListBuuCucOfEmployee(Long id, String txtSearch) throws VtException {
        List<BuuCucEmployeeDTO> rs = new ArrayList<>();
        String sql = "select a.DN_USERID,a.MA_BUUCUC,c.TEN_BUUCUC,a.MA_CHUCDANH,d.TEN_CHUCDANH from VTP.SUSER_ORA a \n" +
                "join ERP_HR.HR_EMPLOYEE b on (a.DN_USERID = b.EMPLOYEE_ID)\n" +
                "join DM_BUUCUC c on (c.MA_BUUCUC = a.MA_BUUCUC) \n" +
                "join DM_CHUCDANH d on (d.MA_CHUCDANH = a.MA_CHUCDANH)\n" +
                "where a.DN_USERID = ?";
        if (txtSearch != null) {
            sql = sql + " and (UPPER(a.MA_BUUCUC) LIKE UPPER('%" + txtSearch + "%') or UPPER(a.MA_CHUCDANH) LIKE UPPER('%" + txtSearch + "%') or UPPER(c.TEN_BUUCUC) LIKE UPPER('%" + txtSearch + "%') or UPPER(d.TEN_CHUCDANH) LIKE UPPER('%" + txtSearch + "%'))";
        }
        try {
            org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(sql)
                    .addScalar("TEN_BUUCUC", new StringType())
                    .addScalar("TEN_CHUCDANH", new StringType())
                    .addScalar("MA_BUUCUC", new StringType())
                    .addScalar("MA_CHUCDANH", new StringType())
                    .addScalar("DN_USERID", new LongType())
                    .setParameter(1, id);
            List<Object[]> list = query.list();
            for (Object[] objs : list) {
                BuuCucEmployeeDTO data = new BuuCucEmployeeDTO();
                data.setTEN_BUUCUC((String) objs[0]);
                data.setTEN_CHUCDANH((String) objs[1]);
                data.setMA_BUUCUC((String) objs[2]);
                data.setMA_CHUCDANH((String) objs[3]);
                data.setUserId((Long) objs[4]);
                rs.add(data);
            }

        } catch (Exception e) {
            throw new VtException("ID User Không tồn tại trong hệ thống.");
        }
        return rs;
    }

    @Transactional
    public PagingResponse getListBuuCucOfEmployeeByPaging(Long pageNumber, Long limit, Long id, String txtSearch) throws Exception {
        List<Object> list = new ArrayList<>();
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.GET_BUUCUC_EMPLOYEE_PAGING",
                    Arrays.asList(pageNumber, limit, id, txtSearch),
                    new int[]{OracleTypes.CURSOR, OracleTypes.CURSOR});
            PagingResponse output = new PagingResponse();
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                List<Object> lo1 = toListObject(rs);
                output.setMainData(lo1);
                try (ResultSet rs2 = (ResultSet) rsArr[1]) {
                    Object o2 = toJsonObject(rs2);
                    output.setSideData(o2);
                }
            }
            return output;
        } catch (Exception ex) {
            throw throwException(ex);
        }
    }

    @Transactional
    public void insertNewBuuCucEmployee(EmployeeInsertBC em) throws Exception {
        org.hibernate.Query query = getSession(sessionFactory)
                .createSQLQuery("INSERT INTO VTP.SUSER_ORA(ORA_USERID,DN_USERID,IS_DELETE,ACTIVE,IS_VIEW,MA_BUUCUC,MA_CHUCDANH,CREATEDATE)\n" +
                        "VALUES ((SELECT NVL (MAX (ora_userid), 0) + 1  FROM vtp.suser_ora),?,0,?,1,?,?,SYSDATE)");
        query.setParameter(1, em.getUserId());
        query.setParameter(2, em.getActive());
        query.setParameter(3, em.getMA_BUUCUC());
        query.setParameter(4, em.getMA_CHUCDANH());
        query.executeUpdate();
        insertNewBuuCucEmployeeJOB(em);
    }

    @Transactional
    public void insertNewBuuCucEmployeeJOB(EmployeeInsertBC em) throws Exception {
        org.hibernate.Query query = getSession(sessionFactory)
                .createSQLQuery("INSERT INTO ERP_HR.HR_EMPLOYEE_JOB(EMPLOYEE_JOB_ID,AD_ORG_ID,CREATED,CREATEDBY,UPDATED,ISACTIVE,EMPLOYEE_ID,POST_ID,POST_CODE,IS_VIEW,ISSMSVIETNAM)\n" +
                        "VALUES(ERP_HR.EMPLOYEE_JOB_ID_SEQ.NEXTVAL,0,SYSDATE,1,SYSDATE,'Y',?,?,?,1,0)");
        query.setParameter(1, em.getUserId());
        query.setParameter(2, 1);
        query.setParameter(3, em.getMA_BUUCUC());
        query.executeUpdate();
    }

    @Transactional
    public List<HopDongLaoDongInfo> getListHopDongLaoDong() throws VtException {
        List<HopDongLaoDongInfo> rs = new ArrayList<>();
        String sql = "select EMPLOYEE_GROUP_ID,NAME,DESCRIPTION from ERP_HR.HR_EMPLOYEE_GROUP";

        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("EMPLOYEE_GROUP_ID", new LongType())
                .addScalar("NAME", new StringType())
                .addScalar("DESCRIPTION", new StringType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            HopDongLaoDongInfo data = new HopDongLaoDongInfo();
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public void insertRoleForEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception {
        List<RoleEmployeeInfo> listRole = getListRoleOfEmployee(roleInsertEmployeeDTO.getUserId(), roleInsertEmployeeDTO.getAppId());
        String sql = "INSERT INTO ERP_SSO.VP_USER_ROLE(ROLE_LIST,APP_ID,USER_ID,MODIFIED_DATE)\n" +
                "VALUES(?,?,?,SYSDATE)";
        for (RoleEmployeeInfo role : listRole) {
            if (role.getAppId() == roleInsertEmployeeDTO.getAppId() || role.getAppId().equals(roleInsertEmployeeDTO.getAppId())) {
                sql = "UPDATE ERP_SSO.VP_USER_ROLE SET ROLE_LIST = ? where app_id = ? and user_id = ?";
            }
        }

        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(sql);
        query.setParameter(1, roleInsertEmployeeDTO.getRoleList());
        query.setParameter(2, roleInsertEmployeeDTO.getAppId());
        query.setParameter(3, roleInsertEmployeeDTO.getUserId());
        query.executeUpdate();
    }

    @Transactional
    public void updateRoleEmployee(RoleInsertEmployeeDTO roleInsertEmployeeDTO) throws Exception {
        String sql = "UPDATE ERP_SSO.VP_USER_ROLE SET ROLE_LIST = ? where app_id = ? and user_id = ?";
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(sql);
        query.setParameter(1, roleInsertEmployeeDTO.getRoleList());
        query.setParameter(2, roleInsertEmployeeDTO.getAppId());
        query.setParameter(3, roleInsertEmployeeDTO.getUserId());
        query.executeUpdate();
    }

    @Transactional
    public void deleteBuuCucEmployee(EmployeeDelelteBCDTO employeeDelelteBCDTO) throws Exception {
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM VTP.SUSER_ORA where DN_USERID = ? and MA_BUUCUC = ? and MA_CHUCDANH = ?");
        query.setParameter(1, employeeDelelteBCDTO.getUserId());
        query.setParameter(2, employeeDelelteBCDTO.getMa_buucuc());
        query.setParameter(3, employeeDelelteBCDTO.getMa_chucdanh());
        query.executeUpdate();
    }

    @Transactional
    public void deleteRoleOfEmployee(EmployeeDelelteBCDTO employeeDelelteBCDTO) throws Exception {
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM VTP.SUSER_ORA where DN_USERID = ? and MA_BUUCUC = ? and MA_CHUCDANH = ?");
        query.setParameter(1, employeeDelelteBCDTO.getUserId());
        query.setParameter(2, employeeDelelteBCDTO.getMa_buucuc());
        query.setParameter(3, employeeDelelteBCDTO.getMa_chucdanh());
        query.executeUpdate();
    }

    @Transactional
    public void insertLogs(Logs logs) throws Exception {
        String sql = "INSERT INTO ERP_SSO.SSO_LOGS(logId,userName,actionLog,chucNang,timeLog) VALUES(ERP_SSO.SSO_LOGS_SEQ.NEXTVAL+1,?,?,?,SYSDATE)";
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery(sql);
        query.setParameter(1, logs.getUserName());
        query.setParameter(2, logs.getActionLog());
        query.setParameter(3, logs.getChucNang());
        query.executeUpdate();
    }

}
