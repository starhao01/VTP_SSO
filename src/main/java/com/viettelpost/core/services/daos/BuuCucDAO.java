package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.BuuCucDTO;
import com.viettelpost.core.services.dtos.OfficeDTO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
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
public class BuuCucDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public BuuCucDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Transactional
    public List<BuuCucDTO> getListBuuCuc(int rowStart, int rowEnd, String txtSearch) {
        String sql = "select * from (SELECT MA_BUUCUC,TEN_BUUCUC,SU_DUNG,ROW_NUMBER() OVER (ORDER BY ID_BUUCUC) R FROM VTP.DM_BUUCUC where SU_DUNG = 1 ";
        if (txtSearch != null) {
            sql = sql + " AND (UPPER(TEN_BUUCUC) LIKE UPPER('%" + txtSearch + "%') or UPPER(MA_BUUCUC) LIKE UPPER('%" + txtSearch + "%')) ";
        }
        sql = sql + ") WHERE R >= ? AND R   <= ?";
        List<BuuCucDTO> rs = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("MA_BUUCUC", new StringType())
                .addScalar("TEN_BUUCUC", new StringType())
                .addScalar("SU_DUNG", new IntegerType())
                .setParameter(1, rowStart)
                .setParameter(2, rowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            BuuCucDTO data = new BuuCucDTO();
            data.setMA_BUUCUC((String) objs[0]);
            data.setTEN_BUUCUC((String) objs[1]);
            data.setSu_dung((Integer) objs[2]);
            rs.add(data);
        }
        return rs;
    }

    @Transactional
    public int getTotalBuuCuc(String txtSearch) throws Exception {
        String sql = "select count(*) Total from (SELECT MA_BUUCUC,TEN_BUUCUC,ROW_NUMBER() OVER (ORDER BY ID_BUUCUC) R FROM VTP.DM_BUUCUC where SU_DUNG = 1";
        if (txtSearch != null) {
            sql = sql + " and (UPPER(TEN_BUUCUC) LIKE UPPER('%" + txtSearch + "%') or UPPER(MA_BUUCUC) LIKE UPPER('%" + txtSearch + "%')) ";
        }
        sql = sql + " )";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("Total", new IntegerType())
                .uniqueResult();
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

    @Transactional
    public List<OfficeDTO> listPostByUsername(int rowStart, int rowEnd,String username) {
        Query query = getSession(sessionFactory).createSQLQuery("select * from (SELECT USERID userId, ID_BUUCUC postId, A.MA_BUUCUC code, DEPT_CODE org, TEN_BUUCUC name, A.MA_TINH maTinh,B.MA_CHUCDANH maChucDanh, \n" +
                        "                        CASE WHEN A.VUNG = 9 THEN 'HNI'\n" +
                        "                                            WHEN A.VUNG = 10 THEN 'HCM'\n" +
                        "                                            WHEN A.VUNG = 11 THEN 'LOG'\n" +
                        "                                        ELSE 'Vùng '||A.VUNG END vung, (SELECT PROVINCE_ID FROM ERP_CUS.CUS_PROVINCE WHERE VALUE = A.MA_TINH) idTinh,\n" +
                        "                                        ROW_NUMBER() OVER (ORDER BY IS_VIEW,A.ID_BUUCUC) R \n" +
                        "                         FROM DM_BUUCUC A, SUSERS B WHERE UPPER(USERNAME) = UPPER(?) AND A.MA_BUUCUC = B.MA_BUUCUC AND A.SU_DUNG = 1) where R >= ?  and R <= ?")
                .addScalar("userId", new LongType())
                .addScalar("postId", new LongType())
                .addScalar("code", new StringType())
                .addScalar("org", new StringType())
                .addScalar("name", new StringType())
                .addScalar("maTinh", new StringType())
                .addScalar("vung", new StringType())
                .addScalar("idTinh", new LongType())
                .addScalar("maChucDanh",new StringType())
                .setResultTransformer(Transformers.aliasToBean(OfficeDTO.class));
        query.setParameter(1, username.toUpperCase());
        query.setParameter(2, rowStart);
        query.setParameter(3, rowEnd);
        return query.list();
    }

    @Transactional
    public int getTotalPost(String username) throws Exception {
        String sql = "select count(*) as Total from (SELECT USERID userId, ID_BUUCUC postId, A.MA_BUUCUC code, DEPT_CODE org, TEN_BUUCUC name, A.MA_TINH maTinh,B.MA_CHUCDANH maChucDanh, \n" +
                "                        CASE WHEN A.VUNG = 9 THEN 'HNI'\n" +
                "                                            WHEN A.VUNG = 10 THEN 'HCM'\n" +
                "                                            WHEN A.VUNG = 11 THEN 'LOG'\n" +
                "                                        ELSE 'Vùng '||A.VUNG END vung, (SELECT PROVINCE_ID FROM ERP_CUS.CUS_PROVINCE WHERE VALUE = A.MA_TINH) idTinh,\n" +
                "                                        ROW_NUMBER() OVER (ORDER BY A.ID_BUUCUC) R \n" +
                "                         FROM DM_BUUCUC A, SUSERS B WHERE UPPER(USERNAME) = UPPER(?) AND A.MA_BUUCUC = B.MA_BUUCUC AND A.SU_DUNG = 1)";
        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("Total", new IntegerType())
                .setParameter(1,username)
                .uniqueResult();
    }
}
