package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.controller.request.ModuleRequest;
import com.viettelpost.core.services.ModuleService;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.domains.ModuleInfo;
import com.viettelpost.core.services.dtos.ModuleInforDTO;
import oracle.jdbc.internal.OracleTypes;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ModuleDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public ModuleDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<ModuleInfo> listModuleByRole(String roleList, Long appId) {
        List<ModuleInfo> listModule = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery("SELECT distinct A.ID id, \n" +
                        "        A.TYPE type, \n" +
                        "        A.PARENT_ID parentId,\n" +
                        "        A.CODE code,\n" +
                        "        A.NAME name, \n" +
                        "        A.STATUS status,\n" +
                        "        A.URL url, \n" +
                        "        A.DESCRIPTION description, \n" +
                        "        A.POSITION position, \n" +
                        "        A.ICON icon, \n" +
                        "        A.APPID appId \n" +
                        "        FROM ERP_SSO.VP_MODULE A, ERP_SSO.VP_ROLE_MODULE B where A.ID = B.VP_MODULE_ID AND  APPID= ?  AND  \n" +
                        "                                           VP_ROLE_ID IN (SELECT DISTINCT * FROM TABLE (SPLIT(?, ','))) ORDER BY A.TYPE, A.PARENT_ID, A.POSITION ASC")
                .addScalar("id", new LongType())
                .addScalar("code", new StringType())
                .addScalar("name", new StringType())
                .addScalar("type", new LongType())
                .addScalar("url", new StringType())
                .addScalar("status", new LongType())
                .addScalar("icon", new StringType())
                .addScalar("position", new LongType())
                .addScalar("parentId", new LongType());
        query.setParameter(1, appId);
        query.setParameter(2, roleList);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInfo data = new ModuleInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setType((Long) objs[3]);
            data.setUrl((String) objs[4]);
            data.setStatus((Long) objs[5]);
            data.setIcon((String) objs[6]);
            data.setPosition((Long) objs[7]);
            data.setParentId((Long) objs[8]);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public List<ModuleInfo> getChilrdModuleById(Long ModuleId, Long appId) {
        List<ModuleInfo> listModule = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery(" select a.id id, a.type type, a.parent_id parentId, a.code code,\n" +
                        "                 a.name name, a.status status, a.url url,\n" +
                        "                       a.description description, a.position position, a.icon icon,\n" +
                        "                (SELECT LISTAGG(B.NAME, ',') WITHIN GROUP (ORDER BY B.NAME) FROM ERP_SSO.VP_ROLE_MODULE C, ERP_SSO.VP_ROLE B  WHERE VP_MODULE_ID = a.id AND C.VP_ROLE_ID = B.ID) role\n" +
                        "                  FROM ERP_SSO.vp_module a \n" +
                        "                Connect By Prior a.Id = a.parent_id\n" +
                        "                Start  With a.parent_id = 0")
                .addScalar("id", new LongType())
                .addScalar("code", new StringType())
                .addScalar("name", new StringType())
                .addScalar("type", new LongType())
                .addScalar("url", new StringType())
                .addScalar("status", new LongType())
                .addScalar("icon", new StringType())
                .addScalar("position", new LongType())
                .addScalar("parentId", new LongType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInfo data = new ModuleInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setType((Long) objs[3]);
            data.setUrl((String) objs[4]);
            data.setStatus((Long) objs[5]);
            data.setIcon((String) objs[6]);
            data.setPosition((Long) objs[7]);
            data.setParentId((Long) objs[8]);
            listModule.add(data);
        }
        return listModule;
    }


    @Transactional
    public List<ModuleInfo> getListModuleByAppID(Long appId, String txtSearch) {
        List<ModuleInfo> listModule = new ArrayList<>();
        String sql = "select a.id id, a.type type, a.parent_id parentId, a.code code,\n" +
                "                                         a.name name, a.status status, a.url url,\n" +
                "                                              a.description description, a.position position, a.icon icon,\n" +
                "                                       (SELECT LISTAGG(B.NAME, ',') WITHIN GROUP (ORDER BY B.NAME) FROM ERP_SSO.VP_ROLE_MODULE C, ERP_SSO.VP_ROLE B  WHERE VP_MODULE_ID = a.id AND C.VP_ROLE_ID = B.ID) role\n" +
                "                                          FROM ERP_SSO.vp_module a where a.APPID = ?";
        if (txtSearch != null) {
            sql = sql + " AND (UPPER(CODE) LIKE UPPER('%" + txtSearch + "%') OR UPPER(NAME) LIKE UPPER('%" + txtSearch + "%'))";
        }
        sql = sql + " Connect By Prior a.Id = a.parent_id\n" +
                "                                        Start  With a.parent_id = 0 order by parent_id desc";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("code", new StringType())
                .addScalar("name", new StringType())
                .addScalar("type", new LongType())
                .addScalar("url", new StringType())
                .addScalar("status", new LongType())
                .addScalar("icon", new StringType())
                .addScalar("position", new LongType())
                .addScalar("parentId", new LongType())
                .addScalar("description", new StringType());
        query.setParameter(1, appId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInfo data = new ModuleInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setType((Long) objs[3]);
            data.setUrl((String) objs[4]);
            data.setStatus((Long) objs[5]);
            data.setIcon((String) objs[6]);
            data.setPosition((Long) objs[7]);
            data.setParentId((Long) objs[8]);
            data.setDescription((String) objs[9]);
            List<ModuleInforDTO> result = getModuleInfo((Long) objs[0],appId);
            String rs = "";
            for (int i = 0; i < result.size(); i++) {
                if(result.get(i).getName()==null){
                    continue;
                }
                if(i==result.size()-1){
                    rs= rs + "[" +(result.get(i).getName()) +"]";
                }else {
                    rs= rs + "[" + (result.get(i).getName() +"]" +", ");
                }
            }
            rs = rs.trim();
            rs = rs.replaceAll(",$", "");
            data.setListRoles(rs);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public int getTotalModule(Long roleId, String txtSearch) throws Exception {
        String sql = "select count(*) total from(SELECT a.ID id, a.TYPE type, a.PARENT_ID parentId,a.CODE code,a.NAME name, a.STATUS status,\n" +
                "  a.URL url, a.DESCRIPTION description, a.POSITION position, a.ICON icon, a.APPID appId,b.NAME APPNAME,b.CODE APPCODE, ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_MODULE a\n" +
                " join ERP_SSO.VP_APP b on ( a.APPID = b.ID)  \n" +
                " where a.ID IN(SELECT VP_MODULE_ID FROM ERP_SSO.VP_ROLE_MODULE WHERE VP_ROLE_ID = ?) ";
        if (txtSearch != null) {
            sql = sql + " and (UPPER(a.CODE) LIKE UPPER('%" + txtSearch + "%') or UPPER(a.NAME) LIKE UPPER('%" + txtSearch + "%') or UPPER(a.URL) LIKE UPPER('%" + txtSearch + "%') or UPPER(a.DESCRIPTION) LIKE UPPER('%" + txtSearch + "%') or b.NAME LIKE UPPER('%" + txtSearch + "%') or b.CODE LIKE UPPER('%" + txtSearch + "%'))";
        }
        sql = sql + " )";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("total", new IntegerType())
                .setParameter(1, roleId)
                .uniqueResult();
    }

    @Transactional
    public List<ModuleInfo> listModuleByRolePaging(Long roleId, int rowStart, int rowEnd, String txtSearch) {
        String sql = "select distinct *  from(SELECT a.ID id, a.TYPE type, a.PARENT_ID parentId,a.CODE code,a.NAME name, a.STATUS status,\n" +
                "  a.URL url, a.DESCRIPTION description, a.POSITION position, a.ICON icon, a.APPID appId,b.NAME APPNAME,b.CODE APPCODE,(SELECT NGAYTACDONG FROM ERP_SSO.VP_ROLE_MODULE WHERE VP_MODULE_ID =  a.ID AND VP_ROLE_ID = ?) NGAYTACDONG, ROW_NUMBER() OVER (ORDER BY a.ID) R FROM ERP_SSO.VP_MODULE a\n" +
                " join ERP_SSO.VP_APP b on ( a.APPID = b.ID) \n" +
                " where a.ID IN(SELECT VP_MODULE_ID FROM ERP_SSO.VP_ROLE_MODULE WHERE VP_ROLE_ID = ?) ";
        if (txtSearch != null) {
            sql = sql + " and (UPPER(a.CODE) LIKE UPPER('%" + txtSearch + "%') or UPPER(a.NAME) LIKE '%" + txtSearch + "%' or UPPER(a.URL) LIKE '%" + txtSearch + "%' or UPPER(a.DESCRIPTION) LIKE '%" + txtSearch + "%' or b.NAME LIKE '%" + txtSearch + "%' or b.CODE LIKE '%" + txtSearch + "%')";
        }
        sql = sql + " ) where R>= ? and R<=?";
        List<ModuleInfo> listModule = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("code", new StringType())
                .addScalar("name", new StringType())
                .addScalar("type", new LongType())
                .addScalar("url", new StringType())
                .addScalar("status", new LongType())
                .addScalar("icon", new StringType())
                .addScalar("position", new LongType())
                .addScalar("parentId", new LongType())
                .addScalar("APPNAME", new StringType())
                .addScalar("APPCODE", new StringType())
                .addScalar("NGAYTACDONG", new StringType())
                .addScalar("APPID", new LongType());
        query.setParameter(1, roleId);
        query.setParameter(2, roleId);
        query.setParameter(3, rowStart);
        query.setParameter(4, rowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInfo data = new ModuleInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setType((Long) objs[3]);
            data.setUrl((String) objs[4]);
            data.setStatus((Long) objs[5]);
            data.setIcon((String) objs[6]);
            data.setPosition((Long) objs[7]);
            data.setParentId((Long) objs[8]);
            data.setAppName((String) objs[9]);
            data.setAppCode((String) objs[10]);
            data.setDate((String) objs[11]);
            data.setAppId((Long) objs[12]);
            listModule.add(data);
        }
        return listModule;
    }



    @Transactional
    public List<ModuleInfo> getListModuleByAppIDPaging(int start, int end, Long appId, String txtSearch) {
        List<ModuleInfo> listModule = new ArrayList<>();
        String sql = "select * from ( select a.id id, a.type type, a.parent_id parentId, a.code code, a.name name, a.status status, a.url url,      \n" +
                "            a.description description, a.position position,ROW_NUMBER() OVER (ORDER BY a.id) R, a.icon icon,\n" +
                "               (SELECT LISTAGG(B.NAME, ',') WITHIN GROUP (ORDER BY B.NAME)\n" +
                "                FROM ERP_SSO.VP_ROLE_MODULE C, ERP_SSO.VP_ROLE B  \n" +
                "                WHERE VP_MODULE_ID = a.id AND C.VP_ROLE_ID = B.ID) role                                                       \n" +
                "                FROM ERP_SSO.vp_module \n" +
                "                a where a.APPID = ?";
        if (txtSearch != null) {
            sql = sql + " AND (UPPER(CODE) LIKE UPPER('%" + txtSearch + "%') OR UPPER(NAME) LIKE UPPER('%" + txtSearch + "%'))";
        }
        sql = sql + " Connect By Prior a.Id = a.parent_id\n" +
                "                                        Start  With a.parent_id = 0 order by parent_id desc";
        sql = sql + " ) where R>= ? and R <=?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("id", new LongType())
                .addScalar("code", new StringType())
                .addScalar("name", new StringType())
                .addScalar("type", new LongType())
                .addScalar("url", new StringType())
                .addScalar("status", new LongType())
                .addScalar("icon", new StringType())
                .addScalar("position", new LongType())
                .addScalar("parentId", new LongType())
                .addScalar("description", new StringType());
        query.setParameter(1, appId);
        query.setParameter(2, start);
        query.setParameter(3, end);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInfo data = new ModuleInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setType((Long) objs[3]);
            data.setUrl((String) objs[4]);
            data.setStatus((Long) objs[5]);
            data.setIcon((String) objs[6]);
            data.setPosition((Long) objs[7]);
            data.setParentId((Long) objs[8]);
            data.setDescription((String) objs[9]);
            List<ModuleInforDTO> result = getModuleInfo((Long) objs[0],appId);
            String rs = "";
            for (int i = 0; i < result.size(); i++) {
                if(result.get(i).getName()==null){
                    continue;
                }
                    if(i==result.size()){
                        rs= rs + (result.get(i).getName());
                    }else {
                        rs= rs + (result.get(i).getName()+",");
                    }
            }
            rs = rs.replaceAll(",$", "");
            data.setListRoles(rs);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public void deleteModuleByRoleId(Long roleId, Long appId) {
        Query query = getSession(sessionFactory).createSQLQuery(" DELETE FROM ERP_SSO.VP_ROLE_MODULE WHERE vp_role_id=? AND app_id=?");
        query.setParameter(1, roleId);
        query.setParameter(2, appId);
        query.executeUpdate();
    }

    @Transactional
    public void insertModule(ModuleRequest moduleRequest, Long App_ID) {
        Query query = getSession(sessionFactory).createSQLQuery("INSERT INTO ERP_SSO.VP_MODULE(ID,NAME,CODE,TYPE,PARENT_ID,URL,POSITION,ICON,DESCRIPTION,APPID) VALUES(ERP_WALLET.SEQ_VP_MODULE.NEXTVAL,?,?,?,?,?,?,?,?,?)");
        query.setParameter(1, moduleRequest.getName());
        query.setParameter(2, moduleRequest.getCode());
        query.setParameter(3, moduleRequest.getType());
        query.setParameter(4, moduleRequest.getParent_id());
        query.setParameter(5, moduleRequest.getUrl());
        query.setParameter(6, moduleRequest.getPosition());
        query.setParameter(7, moduleRequest.getIcon());
        query.setParameter(8, moduleRequest.getDescription());
        query.setParameter(9, App_ID);
        query.executeUpdate();
    }


//    @Transactional
//    public List<ModuleInfo> deleteModuleById(Long moduleId) {
//        List<ModuleInfo> dataList = new ArrayList<>();
//        try {
//            ResultSet rs = getResultSet(sessionFactory, "ERP_SSO.PKG_SSO.DELETE_ALL_ROLE_MODULE",
//                    Arrays.asList(moduleId), OracleTypes.CURSOR);
//            if (rs.next()) {
//                ModuleInfo data = new ModuleInfo();
//                data.setId(rs.getLong("id"));
//                data.setType(rs.getLong("type"));
//                data.setCode(rs.getString("code"));
//                data.setIcon(rs.getString("icon"));
//                data.setName(rs.getString("name"));
//                data.setParentId(rs.getLong("parent_id"));
//                data.setUrl(rs.getString("url"));
//                data.setPosition(rs.getLong("position"));
//                data.setStatus(rs.getLong("status"));
//                data.setAppId(rs.getLong("appId"));
//                dataList.add(data);
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getLocalizedMessage());
//        }
//        return dataList;
//    }

    @Transactional
    public List<ModuleInfo> deleteModuleById(Long moduleId) {
        List<ModuleInfo> dataList = new ArrayList<>();
        try{
            getSession(sessionFactory).beginTransaction();
            deleteChildren(moduleId);
            getSession(sessionFactory).getTransaction().commit();
            String selectModuleSql = "SELECT * FROM ERP_SSO.VP_MODULE";
            NativeQuery<Object[]> selectModuleQuery = getSession(sessionFactory).createNativeQuery(selectModuleSql);
            List<Object[]> resultList = selectModuleQuery.list();
            for (Object[] objs : resultList) {
                 ModuleInfo data = new ModuleInfo();
                data.setId((Long) objs[0]);
                data.setType((Long) objs[1]);
                data.setCode((String) objs[2]);
                data.setIcon((String) objs[3]);
                data.setName((String) objs[4]);
                data.setParentId((Long) objs[5]);
                data.setUrl((String) objs[6]);
                data.setPosition((Long) objs[7]);
                data.setStatus((Long) objs[8]);
                data.setAppId((Long) objs[9]);
                dataList.add(data);
            }
        }catch (Exception ex){
            logger.error(ex.getLocalizedMessage());
        }
        return dataList;
    }

    public void deleteChildren(Long parentId) {

            String deleteModuleHql = "DELETE FROM ERP_SSO.VP_MODULE WHERE id = ?";
            String deleteRoleModuleHql = "DELETE FROM ERP_SSO.VP_ROLE_MODULE WHERE VP_MODULE_ID = ?";
            String selectIdHql = "SELECT id FROM VP_MODULE WHERE parentId = ?";

            int deleteModuleCount = getSession(sessionFactory).createSQLQuery(deleteModuleHql)
                    .setParameter("parentId", parentId)
                    .executeUpdate();
            int deleteRoleModuleCount = getSession(sessionFactory).createSQLQuery(deleteRoleModuleHql)
                    .setParameter("parentId", parentId)
                    .executeUpdate();

            List<Integer> childIds = getSession(sessionFactory).createSQLQuery(selectIdHql)
                    .setParameter("parentId", parentId)
                    .getResultList();

            for (int childId : childIds) {
                deleteChildren(Long.parseLong(childId+""));
            }
    }




@Transactional
    public void updateModule(ModuleRequest moduleInfo) {
        org.hibernate.Query query = getSession(sessionFactory).createSQLQuery("UPDATE ERP_SSO.VP_MODULE SET NAME = ?, CODE = ?, Type = ?, PARENT_ID = ?, URL = ?, POSITION = ?, ICON = ?, DESCRIPTION = ? where ID = ?");
        query.setParameter(1, moduleInfo.getName());
        query.setParameter(2, moduleInfo.getCode());
        query.setParameter(3, moduleInfo.getType());
        query.setParameter(4, moduleInfo.getParent_id());
        query.setParameter(5, moduleInfo.getUrl());
        query.setParameter(6, moduleInfo.getPosition());
        query.setParameter(7, moduleInfo.getIcon());
        query.setParameter(8, moduleInfo.getDescription());
        query.setParameter(9, moduleInfo.getId());
        query.executeUpdate();
    }

    @Transactional
    public List<ModuleInforDTO> getModuleInfo(Long ModuleId, Long appId) {
        List<ModuleInforDTO> listModule = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery("select b.ID,b.NAME from ERP_SSO.VP_ROLE_MODULE a\n" +
                        "left join ERP_SSO.VP_ROLE b on (a.VP_ROLE_ID = b.ID)\n" +
                        "where VP_MODULE_ID = ? and APP_ID = ?")
                .addScalar("ID", new LongType())
                .addScalar("NAME", new StringType())
                .setParameter(1,ModuleId)
                .setParameter(2,appId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ModuleInforDTO data = new ModuleInforDTO();
            data.setCode((Long) objs[0]);
            data.setName((String) objs[1]);
            listModule.add(data);
        }
        return listModule;
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
