package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.services.domains.AppInfo;
import com.viettelpost.core.services.domains.Logs;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class AppDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public AppDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<AppInfo> getAllAppInfoByUserID(Long user_id) {
        List<AppInfo> rs = new ArrayList<>();

        Query query = getSession(sessionFactory).createSQLQuery("select ro.APP_ID, ap.code, ap.desription, ap.name from ERP_SSO.VP_USER_ROLE ro inner join ERP_SSO.VP_APP ap on (ro.APP_ID = ap.ID) where status = 1 and user_id =?").addScalar("APP_ID", new LongType()).addScalar("CODE", new StringType()).addScalar("NAME", new StringType()).addScalar("DESRIPTION", new StringType());
        query.setParameter(1, user_id);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            AppInfo data = new AppInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setDesription((String) objs[3]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<AppInfo> getAllApp() {
        List<AppInfo> rs = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery(" SELECT ID APP_ID, CODE, NAME, DESRIPTION FROM ERP_SSO.VP_APP order by APP_ID").addScalar("APP_ID", new LongType()).addScalar("CODE", new StringType()).addScalar("NAME", new StringType()).addScalar("DESRIPTION", new StringType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            AppInfo data = new AppInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setDesription((String) objs[3]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<AppInfo> getAllAppByRole(long roleId) {
        List<AppInfo> rs = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery("            select distinct A.APP_ID, B.NAME, B.CODE, B.DESRIPTION from ERP_SSO.VP_ROLE_MODULE A left join ERP_SSO.VP_APP B on (A.APP_ID = B.ID) where VP_ROLE_ID = ? order by APP_ID")
                .addScalar("APP_ID", new LongType())
                .addScalar("CODE", new StringType())
                .addScalar("NAME", new StringType())
                .addScalar("DESRIPTION", new StringType())
                .setParameter(1,roleId);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            AppInfo data = new AppInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setDesription((String) objs[3]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public List<AppInfo> getAllAppPaging(int rowStart, int rowEnd, String txtSearch) {
        List<AppInfo> rs = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT ID APP_ID, CODE, NAME, DESRIPTION, ROW_NUMBER() OVER (ORDER By ID DESC) R FROM ERP_SSO.VP_APP ";
        if (txtSearch != null) {
            sql = sql + " where (UPPER(NAME) like UPPER('%" + txtSearch + "%') or UPPER(CODE) Like UPPER('%" + txtSearch + "%'))";
        }
        sql = sql + ") where R>= ? and R<= ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql).addScalar("APP_ID", new LongType()).addScalar("CODE", new StringType()).addScalar("NAME", new StringType()).addScalar("DESRIPTION", new StringType()).setParameter(1, rowStart).setParameter(2, rowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            AppInfo data = new AppInfo();
            data.setId((Long) objs[0]);
            data.setCode((String) objs[1]);
            data.setName((String) objs[2]);
            data.setDesription((String) objs[3]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public int getTotalApp(String txtSearch) throws Exception {
        String sql = "select count(*) total from (SELECT * FROM (SELECT ID APP_ID, CODE, NAME, DESRIPTION, ROW_NUMBER() OVER (ORDER By ID) R FROM ERP_SSO.VP_APP ";
        if (txtSearch != null) {
            sql = sql + " where (UPPER(NAME) like UPPER('%" + txtSearch + "%') or UPPER(CODE) Like UPPER('%" + txtSearch + "%'))";
        }
        sql = sql + "))";
        return (int) getSession(sessionFactory).createSQLQuery(sql).addScalar("total", new IntegerType()).uniqueResult();
    }


    @Transactional
    public int getNewAppID() throws Exception {
        return (int) getSession(sessionFactory).createSQLQuery("select ID as APP_ID from ERP_SSO.VP_APP where ROWNUM <= 1 order by ID desc").addScalar("APP_ID", new IntegerType()).uniqueResult() + 1;
    }

    @Transactional
    public void insertApp(String name, String desription, String Code) throws Exception {
        Query query = getSession(sessionFactory).createSQLQuery("INSERT INTO ERP_SSO.VP_APP(ID, NAME, DESRIPTION, STATUS, CREATEDATE, CODE) VALUES (?, ?, ?, 1, SYSDATE,?)");
        query.setParameter(1, getNewAppID());
        query.setParameter(2, name);
        query.setParameter(3, desription);
        query.setParameter(4, Code);
        query.executeUpdate();
    }

    @Transactional
    public void deleteApp(Long id) {
        Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM ERP_SSO.VP_APP where id = ?");
        query.setParameter(1, id);
        query.executeUpdate();
    }


    @Transactional
    public void updateApp(String name, String description, String Code, Long id) {
        Query query = getSession(sessionFactory).createSQLQuery("UPDATE ERP_SSO.VP_APP SET NAME = ?, DESRIPTION = ?, CODE = ? WHERE ID = ?");
        query.setParameter(1, name);
        query.setParameter(2, description);
        query.setParameter(3, Code);
        query.setParameter(4, id);
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
