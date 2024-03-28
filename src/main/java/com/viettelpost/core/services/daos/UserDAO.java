package com.viettelpost.core.services.daos;

import com.viettelpost.core.base.BaseDAO;
import com.viettelpost.core.base.VtException;
import com.viettelpost.core.services.dtos.AppDto;
import com.viettelpost.core.services.dtos.OfficeDTO;
import com.viettelpost.core.services.dtos.UserTokenDto;
import oracle.jdbc.internal.OracleTypes;
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserDAO extends BaseDAO {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Qualifier("coreFactory")
    SessionFactory sessionFactory;


    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public List<OfficeDTO> listPostByUsername(String username, String buuCuc) {
        Query query = getSession(sessionFactory).createSQLQuery("SELECT USERID userId, ID_BUUCUC postId, A.MA_BUUCUC code, DEPT_CODE org, TEN_BUUCUC name, A.MA_TINH maTinh,B.MA_CHUCDANH maChucDanh, " +
                        "CASE WHEN A.VUNG = 9 THEN 'HNI'\n" +
                        "                    WHEN A.VUNG = 10 THEN 'HCM'\n" +
                        "                    WHEN A.VUNG = 11 THEN 'LOG'\n" +
                        "                ELSE 'Vùng '||A.VUNG END vung," +
                        "A.VUNG idVung,C.ORGNAME orgName,C.ORG_ID OrgId, (SELECT PROVINCE_ID FROM ERP_CUS.CUS_PROVINCE WHERE VALUE = A.MA_TINH) idTinh " +
                        " FROM DM_BUUCUC A, SUSERS B,ERP_HR.HR_ORGANIZATION C WHERE UPPER(USERNAME) = ? " +
                        "AND A.MA_BUUCUC = B.MA_BUUCUC AND A.DEPT_CODE = C.ORGCODE" +
                        " AND A.SU_DUNG = 1 AND (? IS NULL OR B.MA_BUUCUC = ?)" +
                        "ORDER BY IS_VIEW, A.MA_BUUCUC")
                .addScalar("userId", new LongType())
                .addScalar("postId", new LongType())
                .addScalar("code", new StringType())
                .addScalar("org", new StringType())
                .addScalar("name", new StringType())
                .addScalar("maTinh", new StringType())
                .addScalar("vung", new StringType())
                .addScalar("idTinh", new LongType())
                .addScalar("maChucDanh", new StringType())
                .addScalar("idVung", new LongType())
                .addScalar("orgName", new StringType())
                .addScalar("OrgId", new LongType())

                .setResultTransformer(Transformers.aliasToBean(OfficeDTO.class));
        query.setParameter(1, username.toUpperCase());
        query.setParameter(2, buuCuc);
        query.setParameter(3, buuCuc);
        return query.list();
    }

    @Transactional
    public Map<String, Object> getUserInfo(String username, String appId) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.USER_LOGIN",
                    Arrays.asList(username, appId),
                    new int[]{OracleTypes.CURSOR});
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                Map<String, Object> result = new HashMap<>();
                if (rsArr[0] != null) {
                    result.put("result", toJsonObject(rs));
                    return result;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @Transactional
    public Map<String, Object> getUserInfoNB(String employeeCode, String appId) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.USER_LOGIN_NB",
                    Arrays.asList(employeeCode, appId),
                    new int[]{OracleTypes.CURSOR});
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                Map<String, Object> result = new HashMap<>();
                if (rsArr[0] != null) {
                    result.put("result", toJsonObject(rs));
                    return result;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @Transactional
    public String changePassword(String username, String newPass, String newPassSalt) throws Exception {
        String resultSet = null;
        try {
            resultSet = (String) getResultObject(sessionFactory, "ERP_SSO.PKG_SSO.CHANGE_PASSWORD",
                    Arrays.asList(username, newPass, newPassSalt), OracleTypes.VARCHAR);
        } catch (Exception e) {

        }
        return resultSet;
    }

    public void sendOtpViaSms(String phone) throws Exception {
        Object[] objs = getListResult(sessionFactory, "VTP.EVTP_OTP.SEND_OTP_VTMAN_VIA_SMS", Arrays.asList(phone),
                new int[]{OracleTypes.VARCHAR});
        String msg = (String) objs[0];
        if (msg != null && !msg.isEmpty()) {
            throw new VtException(200, msg);
        }
    }

    public Long getTruongBuuCuc(String maBC) throws Exception {
        return (Long) getSession(sessionFactory).createSQLQuery("SELECT USERID FROM SUSERS WHERE MA_CHUCDANH = ? AND MA_BUUCUC = ?")
                .addScalar("USERID", new LongType())
                .setParameter(1, "VTN0055").setParameter(2, maBC)
                .uniqueResult();
    }

    @Transactional
    public Map<String, Object> sendOTP(String phone) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.SEND_OTP_VTMAN",
                    Arrays.asList(phone),
                    new int[]{OracleTypes.CURSOR, OracleTypes.VARCHAR});
            Map<String, Object> result = new HashMap<>();
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                if (rs != null) {
                    result.put("result", toJsonObject(rs));
                }
                result.put("message", rsArr[1]);
            }
            return result;
        } catch (Exception e) {
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }


    @Transactional
    public String checkOTP(String phone, String otp) throws Exception {
        try {
            String url = getUrl();
            if (url != null && url.equals("jdbc:oracle:thin:@10.60.117.73:1521:evtp")) {
                return "OK";
            }
            getResultSet(sessionFactory, "EVTP_OTP.CHECK_OTP_VTMAN", Arrays.asList(phone, otp), OracleTypes.NULL);
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("INVALID_OTP")) {
                return "OTP " + otp + " không đúng. Vui lòng kiểm tra lại";
            } else {
                throwException(e);
            }
            return "Lỗi. Hệ thống bận vui lòng thử lại sau!";
        }
        return "OK";
    }

    @Transactional
    public Map<String, Object> checkOtp(String phone, String otp, String appCode) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.CHECK_OTP_VTMAN",
                    Arrays.asList(phone, otp, appCode),
                    new int[]{OracleTypes.CURSOR});
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                Map<String, Object> result = new HashMap<>();
                if (rsArr[0] != null) {
                    result.put("result", toJsonObject(rs));
                    return result;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            if (e.getLocalizedMessage().contains("INVALID_OTP")) {
                throw new VtException("OTP " + otp + " không đúng. Vui lòng kiểm tra lại");
            } else {
                logger.error(e.getLocalizedMessage(), e);
            }
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @Transactional
    public String changePasswordByOtp(String phone, String newPass, String newPassSalt) throws Exception {
        String resultSet = null;
        try {
            resultSet = (String) getResultObject(sessionFactory, "ERP_SSO.PKG_SSO.CHANGE_PASSWORD_BY_OTP_VTMAN",
                    Arrays.asList(phone, newPass, newPassSalt), OracleTypes.VARCHAR);
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage(), e);
        }
        return resultSet;
    }


    @Transactional
    public AppDto checkAppByToken(String code, String ip, String token) throws Exception {
        try {
            ResultSet rs = getResultSet(sessionFactory, "ERP_SSO.PKG_SSO_NEW.LGIN", Arrays.asList(code, token), OracleTypes.CURSOR);
            if (rs.next()) {
                UserTokenDto user = new UserTokenDto();
                user.setDeptName(rs.getString("DEPT_NAME"));
                user.setDeptId(rs.getLong("DEPT_ID"));
                user.setFullName(rs.getString("FULL_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setTelephone(rs.getString("TELEPHONE"));
                user.setEmail(rs.getString("EMAIL"));
                user.setUserId(rs.getLong("USER_ID"));
                user.setStaffCode(rs.getString("STAFF_CODE"));
                user.setUsername(rs.getString("USERNAME"));
                if (user.getUserId() != null && user.getUserId().longValue() < 0) {
                    user.setUserId(-user.getUserId());
                }
                AppDto app = new AppDto(code, rs.getString("DOMAIN"), rs.getString("TOKEN"));
                app.setUser(user);
                return app;
            }
        } catch (Exception ex) {
            logCheckApp(ex);
        }
        return null;
    }

    void logCheckApp(Exception ex) throws Exception {
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("INVALID_APP_CODE")) {
                throw new VtException("Ứng dụng chưa được khai báo. Vui lòng sao chép đường dẫn gửi về qtht@viettelpost.com.vn để được hỗ trợ!");
            }
            if (ex.getMessage().contains("INVALID_APP_STATE")) {
                throw new VtException("Ứng dụng đã bị khóa. Vui lòng sao chép đường dẫn gửi về qtht@viettelpost.com.vn để được hỗ trợ!");
            }
        }
    }

    private String getUrl() {
        try {
            Connection connection = getConnection(sessionFactory);
            DatabaseMetaData metaData = connection.getMetaData();
            return metaData.getURL();
        } catch (Exception ex) {

        }
        return null;
    }

    @Transactional
    public Map<String, Object> getToken(String sso, String code) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.GET_TOKEN",
                    Arrays.asList(sso, code),
                    new int[]{OracleTypes.CURSOR});
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                Map<String, Object> result = new HashMap<>();
                result.put("result", toJsonObject(rs));
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @Transactional
    public Map<String, Object> getShareCode(String username, String appCode) throws Exception {
        try {
            Object[] rsArr = getListResult(sessionFactory, "ERP_SSO.PKG_SSO.GET_SHARE_CODE",
                    Arrays.asList(username, appCode),
                    new int[]{OracleTypes.CURSOR});
            try (ResultSet rs = (ResultSet) rsArr[0]) {
                Map<String, Object> result = new HashMap<>();
                result.put("result", toJsonObject(rs));
                return result;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            throw new VtException("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @Transactional
    public int getNewestOTP(String txtSearch) throws Exception {
        String sql = "select OTP_CHAR from VTP.VTMAN_OTP where ROWNUM = 1 order by OTP_DATE desc";

        return (int) getSession(sessionFactory).createSQLQuery(sql)
                .addScalar("total", new IntegerType())
                .uniqueResult();
    }
}
