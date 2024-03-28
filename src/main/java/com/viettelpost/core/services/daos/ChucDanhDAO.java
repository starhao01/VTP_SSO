package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.services.domains.Logs;
import com.viettelpost.core.services.dtos.ChucDanhDTO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChucDanhDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;

    public ChucDanhDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<ChucDanhDTO> getAllChucDanh() {
        List<ChucDanhDTO> listModule = new ArrayList<>();
        Query query = getSession(sessionFactory).createSQLQuery("Select D.MA_CHUCDANH, D.TEN_CHUCDANH, D.SU_DUNG from VTP.DM_CHUCDANH D")
                .addScalar("MA_CHUCDANH", new StringType())
                .addScalar("TEN_CHUCDANH", new StringType())
                .addScalar("SU_DUNG", new IntegerType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ChucDanhDTO data = new ChucDanhDTO();
            data.setMA_CHUCDANH((String) objs[0]);
            data.setTEN_CHUCDANH((String) objs[1]);
            data.setSU_DUNG((int) objs[2]);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public List<ChucDanhDTO> getChucDanhByName(String txtSearch) {
        List<ChucDanhDTO> listModule = new ArrayList<>();
        String sql = "Select D.MA_CHUCDANH, D.TEN_CHUCDANH, D.SU_DUNG from VTP.DM_CHUCDANH D";
        if (txtSearch != null) {
            sql = sql + " where (UPPER(D.MA_CHUCDANH) LIKE UPPER('%" + txtSearch + "%') or UPPER(D.TEN_CHUCDANH) LIKE UPPER('%" + txtSearch + "%')) ";
        }
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("MA_CHUCDANH", new StringType())
                .addScalar("TEN_CHUCDANH", new StringType())
                .addScalar("SU_DUNG", new IntegerType());
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ChucDanhDTO data = new ChucDanhDTO();
            data.setMA_CHUCDANH((String) objs[0]);
            data.setTEN_CHUCDANH((String) objs[1]);
            data.setSU_DUNG((int) objs[2]);
            listModule.add(data);
        }
        return listModule;
    }


    @Transactional
    public List<ChucDanhDTO> getAllChucDanhByPaging(int rowStart, int rowEnd, String txtSearch) {
        List<ChucDanhDTO> listModule = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT MA_CHUCDANH, TEN_CHUCDANH, SU_DUNG,NGAY_NHAP_MAY, ROW_NUMBER() OVER (ORDER BY MA_CHUCDANH) R FROM VTP.DM_CHUCDANH";
        if (txtSearch != null) {
            sql = sql + " where ( UPPER(MA_CHUCDANH) LIKE UPPER('%" + txtSearch.toUpperCase().trim() + "%') or UPPER(TEN_CHUCDANH) LIKE UPPER('%" + txtSearch.toUpperCase().trim() + "%') ) ";
        }
        sql = sql + ") WHERE R >= ? AND R   <= ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("MA_CHUCDANH", new StringType())
                .addScalar("TEN_CHUCDANH", new StringType())
                .addScalar("SU_DUNG", new IntegerType())
                .addScalar("NGAY_NHAP_MAY", new StringType())
                .setParameter(1, rowStart)
                .setParameter(2, rowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ChucDanhDTO data = new ChucDanhDTO();
            data.setMA_CHUCDANH((String) objs[0]);
            data.setTEN_CHUCDANH((String) objs[1]);
            data.setSU_DUNG((int) objs[2]);
            data.setNGAY_NHAP_MAY((String) objs[3]);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public List<ChucDanhDTO> getAllChucDanhHoatDongByPaging(int rowStart, int rowEnd, String txtSearch) {
        List<ChucDanhDTO> listModule = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT MA_CHUCDANH, TEN_CHUCDANH, SU_DUNG,NGAY_NHAP_MAY, ROW_NUMBER() OVER (ORDER BY MA_CHUCDANH) R FROM VTP.DM_CHUCDANH where SU_DUNG = 1";
        if (txtSearch != null) {
            sql = sql + " and ( UPPER(MA_CHUCDANH) LIKE UPPER('%" + txtSearch.toUpperCase().trim() + "%') or UPPER(TEN_CHUCDANH) LIKE UPPER('%" + txtSearch.toUpperCase().trim() + "%') ) ";
        }
        sql = sql + ") WHERE R >= ? AND R   <= ?";
        Query query = getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("MA_CHUCDANH", new StringType())
                .addScalar("TEN_CHUCDANH", new StringType())
                .addScalar("SU_DUNG", new IntegerType())
                .addScalar("NGAY_NHAP_MAY", new StringType())
                .setParameter(1, rowStart)
                .setParameter(2, rowEnd);
        List<Object[]> list = query.list();
        for (Object[] objs : list) {
            ChucDanhDTO data = new ChucDanhDTO();
            data.setMA_CHUCDANH((String) objs[0]);
            data.setTEN_CHUCDANH((String) objs[1]);
            data.setSU_DUNG((int) objs[2]);
            data.setNGAY_NHAP_MAY((String) objs[3]);
            listModule.add(data);
        }
        return listModule;
    }

    @Transactional
    public void deleteChucDanh(String MA_CHUCDANH) {
        Query query = getSession(sessionFactory).createSQLQuery("DELETE FROM VTP.DM_CHUCDANH WHERE MA_CHUCDANH = ?");
        query.setParameter(1, MA_CHUCDANH);
        query.executeUpdate();
    }

    @Transactional
    public void insertChucDAnh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE) {
        Query query = getSession(sessionFactory).createSQLQuery("INSERT INTO DM_CHUCDANH(MA_CHUCDANH, TEN_CHUCDANH, SU_DUNG, NGAY_NHAP_MAY) VALUES (?,?,?, SYSDATE)");
        query.setParameter(1, MA_CHUCDANH);
        query.setParameter(2, TEN_CHUCDANH);
        query.setParameter(3, ACTIVE);
        query.executeUpdate();
    }

    @Transactional
    public void updateChucDanh(String MA_CHUCDANH, String TEN_CHUCDANH, Long ACTIVE) {
        updateNgayTacDong(MA_CHUCDANH);
        Query query = getSession(sessionFactory).createSQLQuery("UPDATE DM_CHUCDANH set TEN_CHUCDANH = ?, SU_DUNG = ? where MA_CHUCDANH = ?");
        query.setParameter(1, TEN_CHUCDANH);
        query.setParameter(2, ACTIVE);
        query.setParameter(3, MA_CHUCDANH);
        query.executeUpdate();
    }

    @Transactional
    public void changeStatusChucDanh(String MA_CHUCDANH) {
        updateNgayTacDong(MA_CHUCDANH);
        Query query = getSession(sessionFactory).createSQLQuery("UPDATE DM_CHUCDANH\n" +
                "SET SU_DUNG = case when SU_DUNG = 0 then 1\n" +
                "else 0\n" +
                "end where MA_CHUCDANH = ?");
        query.setParameter(1, MA_CHUCDANH);
        query.executeUpdate();
    }

    @Transactional
    public void updateNgayTacDong(String MA_CHUCDANH) {
        Query query = getSession(sessionFactory).createSQLQuery("  UPDATE VTP.DM_CHUCDANH SET NGAY_NHAP_MAY = SYSDATE where MA_CHUCDANH = ?");
        query.setParameter(1, MA_CHUCDANH);
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
