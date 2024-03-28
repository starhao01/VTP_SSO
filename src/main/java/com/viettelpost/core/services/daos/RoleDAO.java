package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.RoleInfo;
import com.viettelpost.core.services.dtos.RoleOfModuleDTO;
import oracle.jdbc.internal.OracleTypes;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RoleDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public RoleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void updateRole(String name, String description, Long status, Long id) {
        updateNgayTacDong(id);
        Query query = getSession(sessionFactory).createSQLQuery("UPDATE ERP_SSO.VP_ROLE SET NAME = ?, DESCRIPTION = ?, STATUS = ? WHERE ID = ?");
        query.setParameter(1, name);
        query.setParameter(2, description);
        query.setParameter(3, status);
        query.setParameter(4, id);
        query.executeUpdate();
    }


    @Transactional
    public void changeStatusRole(Long id) {
        updateNgayTacDong(id);
        Query query = getSession(sessionFactory).createSQLQuery("UPDATE ERP_SSO.VP_ROLE\n" +
                "SET STATUS = case when STATUS = 0 then 1\n" +
                "else 0\n" +
                "end where ID = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }


    @Transactional
    public void deleteRole(Long id) {
        Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM ERP_SSO.VP_ROLE WHERE ID = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }

    //@Transactional
    //public void insertRole(String name, String description, Long status) throws Exception {
      //  getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.INSERT_ROLE", Arrays.asList(name, description, status), OracleTypes.NULL);
   // }

    @Transactional
    public void insertRole(String name, String description, Long status) throws Exception {
        Query query = getSession(sessionFactory).createSQLQuery("INSERT INTO ERP_SSO.VP_ROLE (ID,\n" +
                "                                        NAME,\n" +
                "                                        DESCRIPTION,\n" +
                "                                        STATUS,\n" +
                "                                        CREATE_DATE,NGAYDONGBO)\n" +
                "             VALUES ((SELECT MAX (ID) + 1 INTO vID FROM ERP_SSO.VP_ROLE),\n" +
                "                     ?,\n" +
                "                     ?,\n" +
                "                     ?,\n" +
                "                     SYSDATE,SYSDATE)");
        query.setParameter(1, name);
        query.setParameter(2, description);
        query.setParameter(3, status);
        query.executeUpdate();
    }

    @Transactional
    public List<RoleInfo> getListAllRole() {
        List<RoleInfo> rs = new ArrayList<>();

        Query query = getSession(sessionFactory).createSQLQuery("SELECT id, name, description, status FROM ERP_SSO.VP_ROLE")
                .addScalar("id", new LongType())
                .addScalar("name", new StringType())
                .addScalar("description", new StringType())
                .addScalar("status", new StringType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleInfo data = new RoleInfo();
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public int getTotalRole(String txtSearch) throws Exception {
        String sql = "select count(*) Totalx from (SELECT * FROM (SELECT id, name, description, status, ROW_NUMBER() OVER (ORDER BY id) R FROM ERP_SSO.VP_ROLE ";
        if (txtSearch != null) {
            sql = sql + " where (UPPER(name) like UPPER('%" + txtSearch + "%') or UPPER(DESCRIPTION) LIKE UPPER('%" + txtSearch + "%') ) ";
        }
        sql = sql + " ))";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("Totalx", new IntegerType())
                .uniqueResult();
    }


    @Transactional
    public List<RoleInfo> getAllRole(int RowStart, int RowEnd, String txtSearch) {
        List<RoleInfo> rs = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT id, name, description, status,NGAYDONGBO, ROW_NUMBER() OVER (ORDER BY id) R FROM ERP_SSO.VP_ROLE";
        if (txtSearch != null) {
            sql = sql + " where (UPPER(name) like UPPER('%" + txtSearch + "%') or UPPER(DESCRIPTION) LIKE UPPER('%" + txtSearch + "%') ) ";
        }
        sql = sql + " ) WHERE R >= ? AND R   <= ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("name", new StringType())
                .addScalar("description", new StringType())
                .addScalar("status", new LongType())
                .addScalar("R", new LongType())
                .addScalar("NGAYDONGBO", new StringType());
        query.setParameter(1, RowStart);
        query.setParameter(2, RowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleInfo data = new RoleInfo();
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
            data.setStatus((Long) objs[3]);
            data.setNgaydongbo((String) objs[5]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<RoleInfo> getAllRoleByAppID(Long appId, int RowStart, int RowEnd, String txtSearch) {
        List<RoleInfo> rs = new ArrayList<>();
        String sql = "select DISTINCT(a.VP_ROLE_ID),b.id, b.name, b.description, b.status,a.APP_ID from ERP_SSO.VP_ROLE_MODULE a \n" +
                "left join ERP_SSO.VP_ROLE b on ( a.VP_ROLE_ID = b.ID) where app_id = ? and b.status = 1 ";
        if (txtSearch != null) {
            sql = sql + " and (UPPER(b.name) like UPPER('%" + txtSearch + "%') or UPPER(b.DESCRIPTION) LIKE UPPER('%" + txtSearch + "%') ) ";
        }
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("name", new StringType())
                .addScalar("description", new StringType())
                .addScalar("status", new LongType());
        query.setParameter(1, appId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleInfo data = new RoleInfo();
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
            data.setStatus((Long) objs[3]);
            rs.add(data);
        }
        return rs;
    }


    @Transactional
    public List<RoleInfo> getAllRoleNotGrantByUserId(Long userId, Long appId) {
        List<RoleInfo> listData = new ArrayList<>();
        try {
            ResultSet rs = getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.GET_ROLE_NOT_GRANT_BY_USER", Arrays.asList(userId, appId), oracle.jdbc.OracleTypes.CURSOR);
            while (rs.next()) {
                RoleInfo data = new RoleInfo();
                data.setId(rs.getLong("ID"));
                data.setName(rs.getString("NAME"));
                data.setDescription(rs.getString("DESCRIPTION"));
                listData.add(data);
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
        return listData;
    }


//    @Transactional
//    public List<RoleInfo> getAllRoleByUserId(String userName, Long appId) throws VtException {
//        List<RoleInfo> listData = new ArrayList<>();
//        try {
//            ResultSet rs = getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.GET_ROLE_BY_USER",
//                    Arrays.asList(userName, appId),
//                    oracle.jdbc.OracleTypes.CURSOR);
//            while (rs.next()) {
//                RoleInfo data = new RoleInfo();
//                data.setUserId(rs.getLong("DN_USERID"));
//                data.setDisplayName(rs.getString("DISPLAYNAME"));
//                data.setEmail(rs.getString("EMAIL"));
//                data.setPhone(rs.getString("TELEPHONE"));
//                data.setUsername(rs.getString("USERNAME"));
//                data.setId(rs.getLong("ID"));
//                data.setName(rs.getString("NAME"));
//                data.setDescription(rs.getString("DESCRIPTION"));
//                data.setStatus(rs.getLong("STATUS"));
//                listData.add(data);
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getLocalizedMessage());
//            if (ex instanceof SQLException) {
//                SQLException e = (SQLException) ex;
//                if (e.getMessage().startsWith("ORA-20001: INVALID_USER_EXIST")) {
//                    throw new VtException("Người dùng này không tồn tại trên hệ thống. Vui lòng kiểm tra lại");
//                }
//            }
//        }
//        return listData;
//    }

    @Transactional
    public int isExistRole(Long appId, String username) throws VtException {
        String sql = "SELECT count(*) as total  FROM ERP_SSO.VP_USER_ROLE WHERE APP_ID = ? AND USER_ID = (SELECT DN_USERID FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?))";
        return (int) getSession(sessionFactory)
                .createSQLQuery(sql)
                .addScalar("total", new IntegerType())
                .setParameter(1,appId)
                .setParameter(2,username)
                .uniqueResult();
    }


    @Transactional
    public List<RoleInfo> getAllRoleByUserId(String userName, Long appId) throws VtException {
        List<RoleInfo> rs = new ArrayList<>();
        int check = isExistRole(appId,userName);
        String sql = "SELECT \n" +
                "(SELECT DN_USERID FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) DN_USERID\n" +
                "      , (SELECT FIRSTNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?))||' '||(SELECT LASTNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) DISPLAYNAME\n" +
                "      , (SELECT TELEPHONE FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) TELEPHONE\n" +
                "      , (SELECT USERNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) USERNAME\n" +
                "      , (SELECT EMAIL FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) EMAIL\n" +
                ", NULL ID\n" +
                ", NULL NAME\n" +
                ", NULL DESCRIPTION\n" +
                ", 0 AS STATUS FROM DUAL";
        if(check==1){
             sql = "SELECT\n" +
                    "      (SELECT DN_USERID FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) DN_USERID\n" +
                    "      , (SELECT FIRSTNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?))||' '||(SELECT LASTNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) DISPLAYNAME\n" +
                    "      , (SELECT TELEPHONE FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) TELEPHONE\n" +
                    "      , (SELECT USERNAME FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) USERNAME\n" +
                    "      , (SELECT EMAIL FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?)) EMAIL\n" +
                    "      , ID\n" +
                    "      , NAME\n" +
                    "      , DESCRIPTION\n" +
                    "      ,STATUS \n" +
                    "      FROM ERP_SSO.VP_ROLE WHERE ID IN (SELECT DISTINCT * FROM TABLE (SPLIT(( SELECT MAX(ROLE_LIST) as ROLE_LIST  FROM ERP_SSO.VP_USER_ROLE WHERE APP_ID = ? AND USER_ID = (SELECT DN_USERID FROM VTP.suser_dnn WHERE UPPER(username) = UPPER(?))), ',')))";

        }
           Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("name", new StringType())
                .addScalar("description", new StringType())
                .addScalar("status", new LongType())
                .addScalar("DN_USERID", new LongType())
                .addScalar("DISPLAYNAME", new StringType())
                .addScalar("TELEPHONE", new StringType())
                .addScalar("USERNAME", new StringType())
                .addScalar("EMAIL", new StringType());
        if(check == 1){
            query.setParameter(1, userName);
            query.setParameter(2, userName);
            query.setParameter(3, userName);
            query.setParameter(4, userName);
            query.setParameter(5, userName);
            query.setParameter(6, userName);
            query.setParameter(7, appId);
            query.setParameter(8, userName);
        }else{
            query.setParameter(1, userName);
            query.setParameter(2, userName);
            query.setParameter(3, userName);
            query.setParameter(4, userName);
            query.setParameter(5, userName);
            query.setParameter(6, userName);
        }

        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleInfo data = new RoleInfo();
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
            data.setStatus((Long) objs[3]);
            data.setUserId((Long) objs[4]);
            data.setDisplayName((String) objs[5]);
            data.setPhone((String) objs[6]);
            data.setUsername((String) objs[7]);
            data.setEmail((String) objs[8]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<RoleInfo> getAllRoleByEmployeeCode(String employeeCode, Long appId) throws VtException {
        List<RoleInfo> listData = new ArrayList<>();
        try {
            ResultSet rs = getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.GET_ROLE_BY_USER_CODE",
                    Arrays.asList(employeeCode, appId),
                    oracle.jdbc.OracleTypes.CURSOR);
            while (rs.next()) {
                RoleInfo data = new RoleInfo();
                data.setUserId(rs.getLong("DN_USERID"));
                data.setDisplayName(rs.getString("DISPLAYNAME"));
                data.setEmail(rs.getString("EMAIL"));
                data.setPhone(rs.getString("TELEPHONE"));
                data.setUsername(rs.getString("USERNAME"));
                data.setId(rs.getLong("ID"));
                data.setName(rs.getString("NAME"));
                data.setDescription(rs.getString("DESCRIPTION"));
                data.setStatus(rs.getLong("STATUS"));
                listData.add(data);
            }
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
            if (ex instanceof SQLException) {
                SQLException e = (SQLException) ex;
                if (e.getMessage().startsWith("ORA-20001: INVALID_USER_EXIST")) {
                    throw new VtException("Người dùng này không tồn tại trên hệ thống. Vui lòng kiểm tra lại");
                }
            }
        }
        return listData;
    }

    @Transactional
    public RoleInfo getInfoRoleByRoleId(Long roleId) {
        RoleInfo data = new RoleInfo();
        Query query = getSession(sessionFactory).createSQLQuery("SELECT  id, name, description FROM ERP_SSO.VP_ROLE WHERE id=?")
                .addScalar("id", new LongType())
                .addScalar("name", new StringType())
                .addScalar("description", new StringType());
        query.setParameter(1, roleId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            data.setId((Long) objs[0]);
            data.setName((String) objs[1]);
            data.setDescription((String) objs[2]);
        }
        return data;
    }

    @Transactional
    public void insertModuleToRole(Long appId, Long roleId, Long moduleId) {
        Query query = getSession(sessionFactory).createSQLQuery("INSERT INTO ERP_SSO.VP_ROLE_MODULE(ID,VP_ROLE_ID,VP_MODULE_ID,STATUS,APP_ID,NGAYTACDONG)\n" +
                "VALUES(ERP_WALLET.SEQ_VP_ROLE_MODULE.NEXTVAL,?,?,1,?,SYSDATE)");
        query.setParameter(1, roleId);
        query.setParameter(2, moduleId);
        query.setParameter(3, appId);
        query.executeUpdate();
        updateNgayTacDong(moduleId);
    }

    @Transactional
    public void deleteModuleOfRole(Long appId, Long roleId, Long moduleId) {
        Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM ERP_SSO.VP_ROLE_MODULE WHERE VP_MODULE_ID = ? and VP_ROLE_ID = ? and APP_ID = ?");
        query.setParameter(1, moduleId);
        query.setParameter(2, roleId);
        query.setParameter(3, appId);
        query.executeUpdate();
    }

    @Transactional
    public List<RoleOfModuleDTO> getListAllModuleOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch) {
        List<RoleOfModuleDTO> rs = new ArrayList<>();
        String sql = "select * from (SELECT DISTINCT a.ID,A.APP_ID,a.VP_ROLE_ID,b.NAME ROLE_NAME,a.VP_MODULE_ID,c.CODE,c.NAME MODULE_NAME,c.PARENT_ID,c.TYPE, ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                " where a.VP_ROLE_ID = ? and a.APP_ID = ?";
        if (txtSearch != null) {
            sql = sql + " and (b.NAME LIKE ? or c.CODE LIKE ? or c.NAME like ?)";
        }
        sql = sql + ") where R >= ? and R <= ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("APP_ID", new LongType())
                .addScalar("VP_ROLE_ID", new LongType())
                .addScalar("ROLE_NAME", new StringType())
                .addScalar("VP_MODULE_ID", new LongType())
                .addScalar("CODE", new StringType())
                .addScalar("MODULE_NAME", new StringType())
                .addScalar("PARENT_ID", new LongType())
                .addScalar("TYPE", new LongType());
        query.setParameter(1, roleId);
        query.setParameter(2, appId);
        if (txtSearch != null) {
            query.setParameter(3, "%" + txtSearch + "%");
            query.setParameter(4, "%" + txtSearch + "%");
            query.setParameter(5, "%" + txtSearch + "%");
            query.setParameter(6, rowStart);
            query.setParameter(7, rowEnd);
        } else {
            query.setParameter(3, rowStart);
            query.setParameter(4, rowEnd);
        }

        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleOfModuleDTO data = new RoleOfModuleDTO();
            data.setAppId((Long) objs[0]);
            data.setRoleId((Long) objs[1]);
            data.setRoleName((String) objs[2]);
            data.setModuleId((Long) objs[3]);
            data.setModuleCode((String) objs[4]);
            data.setModuleName((String) objs[5]);
            data.setParentId((Long) objs[6]);
            data.setType((Long) objs[7]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<RoleOfModuleDTO> getListAllModuleNotOfRolePaging(Long roleId, Long appId, int rowStart, int rowEnd, String txtSearch) {
        List<RoleOfModuleDTO> rs = new ArrayList<>();
        String sql = "select * from (SELECT DISTINCT a.ID,A.APP_ID,a.VP_ROLE_ID,b.NAME ROLE_NAME,a.VP_MODULE_ID,c.CODE,c.NAME MODULE_NAME,c.PARENT_ID,c.TYPE,ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                " where VP_ROLE_ID != ? and APP_ID = ? and VP_MODULE_ID not in (SELECT DISTINCT a.VP_MODULE_ID FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                " where VP_ROLE_ID = ? and APP_ID = ?)";
        if (txtSearch != null) {
            sql = sql + " and (b.NAME LIKE ? or c.CODE LIKE ? or c.NAME like ?) ";
        }
        sql = sql + " ) where R >= ? and R < = ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("APP_ID", new LongType())
                .addScalar("VP_ROLE_ID", new LongType())
                .addScalar("ROLE_NAME", new StringType())
                .addScalar("VP_MODULE_ID", new LongType())
                .addScalar("CODE", new StringType())
                .addScalar("MODULE_NAME", new StringType())
                .addScalar("PARENT_ID", new LongType())
                .addScalar("TYPE", new LongType());
        query.setParameter(1, roleId);
        query.setParameter(2, appId);
        query.setParameter(3, roleId);
        query.setParameter(4, appId);
        if (txtSearch != null) {
            query.setParameter(5, "%" + txtSearch + "%");
            query.setParameter(6, "%" + txtSearch + "%");
            query.setParameter(7, "%" + txtSearch + "%");
            query.setParameter(8, rowStart);
            query.setParameter(9, rowEnd);
        } else {
            query.setParameter(5, rowStart);
            query.setParameter(6, rowEnd);
        }

        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            RoleOfModuleDTO data = new RoleOfModuleDTO();
            data.setAppId((Long) objs[0]);
            data.setRoleId((Long) objs[1]);
            data.setRoleName((String) objs[2]);
            data.setModuleId((Long) objs[3]);
            data.setModuleCode((String) objs[4]);
            data.setModuleName((String) objs[5]);
            data.setParentId((Long) objs[6]);
            data.setType((Long) objs[7]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public int getTotalRoleOfModule(Long roleId, Long appId, String txtSearch) throws Exception {
        String sql = "select count(*) Total from (SELECT DISTINCT a.ID,A.APP_ID,a.VP_ROLE_ID,b.NAME ROLE_NAME,a.VP_MODULE_ID,c.CODE,c.NAME MODULE_NAME,c.PARENT_ID,c.TYPE,ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                "where a.VP_ROLE_ID = ? and a.APP_ID = ? ";
        if (txtSearch != null) {
            sql = sql + " and (b.NAME LIKE '%" + txtSearch + "%' or c.CODE LIKE '%" + txtSearch + "%' or c.NAME like '%" + txtSearch + "%')";
        }
        sql = sql + " )";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("total", new IntegerType())
                .setParameter(1, roleId)
                .setParameter(2, appId)
                .uniqueResult();
    }

    @Transactional
    public int getTotalRoleNotOfModule(Long roleId, Long appId, String txtSearch) throws Exception {
        String sql = "select count(*) total from (SELECT DISTINCT a.ID,A.APP_ID,a.VP_ROLE_ID,b.NAME ROLE_NAME,a.VP_MODULE_ID,c.CODE,c.NAME MODULE_NAME,c.PARENT_ID,c.TYPE,ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                " where VP_ROLE_ID != ? and APP_ID = ? and VP_MODULE_ID not in (SELECT DISTINCT a.VP_MODULE_ID FROM ERP_SSO.VP_ROLE_MODULE a \n" +
                "join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                "join ERP_SSO.VP_MODULE c on (c.ID = a.VP_MODULE_ID)\n" +
                " where VP_ROLE_ID = ? and APP_ID = ?) ";
        if (txtSearch != null) {
            sql = sql + " and (b.NAME LIKE '%" + txtSearch + "%' or c.CODE LIKE '%" + txtSearch + "%' or c.NAME like '%" + txtSearch + "%')";
        }
        sql = sql + " )";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("total", new IntegerType())
                .setParameter(1, roleId)
                .setParameter(2, appId)
                .setParameter(3, roleId)
                .setParameter(4, appId)
                .uniqueResult();
    }

    @Transactional
    public void updateNgayTacDong(Long roleId) {
        Query query = getSession(sessionFactory).createSQLQuery("  UPDATE ERP_SSO.VP_ROLE SET NGAYDONGBO = SYSDATE where ID = ?");
        query.setParameter(1, roleId);
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
