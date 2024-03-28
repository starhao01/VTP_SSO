package com.viettelpost.core.controller;

import com.viettelpost.core.base.BaseController;
import com.viettelpost.core.base.BaseResponse;
import com.viettelpost.core.base.VtException;
import com.viettelpost.core.controller.request.UserRequest;
import com.viettelpost.core.controller.response.UserResponse;
import com.viettelpost.core.services.RoleService;
import com.viettelpost.core.services.UserService;
import com.viettelpost.core.services.domains.RoleInfo;
import com.viettelpost.core.services.domains.UserInfo;
import com.viettelpost.core.services.dtos.AccountDTO;
import com.viettelpost.core.services.dtos.LoginResponseDto;
import com.viettelpost.core.services.dtos.OfficeDTO;
import com.viettelpost.core.services.dtos.UserTokenDto;
import com.viettelpost.core.utils.Constants;
import com.viettelpost.core.utils.Constants.STATUS;
import com.viettelpost.core.utils.EncryptionUtil;
import com.viettelpost.core.utils.Utils;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.codec.binary.Base64;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.keys.AesKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.HttpURLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.viettelpost.core.base.EncryptionUtil.createJWT;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @ApiOperation(value = "API login tài khoản evtp")
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserRequest userRequest) throws Exception {
        try {
            String username = userRequest.getUsername();
            String password = userRequest.getPassword();
            if (Utils.isNullOrEmpty(username)) {
                throw new VtException(STATUS.E203, "Tài khoản không được để trống");
            }
            if (Utils.isNullOrEmpty(password)) {
                throw new VtException(STATUS.E203, "Mật khẩu không được để trống");
            }
            Map<String, Object> result = userService.getUserInfo(username, userRequest.getAppCode());
            if (result.get("result") == null) {
                return errorApi("Tài khoản này không tồn tại hoặc chưa được gán bưu cục");
            } else {
                Map<String, Object> userInfo = (Map) result.get("result");
                String salt = userInfo.get("passwordsalt") == null ? null : userInfo.get("passwordsalt").toString();
                String passwordformat = userInfo.get("passwordformat") == null ? null : userInfo.get("passwordformat").toString();
                if (Utils.isNullOrEmpty(salt) || Utils.isNullOrEmpty(passwordformat) || !EncryptionUtil.sha256Encode(userRequest.getPassword(), salt).equals(passwordformat)) {
                    return errorApi("Mật khẩu không hợp lệ");
                }
            }
            result.remove("passwordsalt");
            result.remove("passwordformat");
            List<RoleInfo> resultRole = roleService.getAllRoleByUserId(username, 71L);
            String list = "";
            if (resultRole != null) {
                for (RoleInfo role : resultRole) {
                    if (role.getStatus().equals(1L) || role.getStatus() == 1L) {
                        list = list + role.getId() + ",";
                    }
                }
            }
            list = list.replaceAll(",$", "");
            if (list.startsWith(",")) {
                list = list.substring(1, list.length());
            }
            result.put("roleid", list);
            return successApi(new UserResponse(createJWT(result), null, result), "Thành công");
        } catch (Exception e) {
            return errorApi("Lỗi. Hệ thống bận vui lòng thử lại sau!");
        }
    }

    @ApiOperation(value = "API change password")
    @PostMapping("/changePassword")
    public ResponseEntity changePassword(@RequestBody AccountDTO accountDTO) throws Exception {
        Map<String, Object> result = userService.getUserInfo(accountDTO.getUsername(), accountDTO.getAppCode());
        if (result == null || result.get("result") == null) {
            return errorApi("Tài khoản này không tồn tại hoặc chưa được kích hoạt.");
        } else {
            Map<String, Object> userInfo = (Map) result.get("result");
            String salt = userInfo.get("passwordsalt") == null ? null : userInfo.get("passwordsalt").toString();
            String passwordformat = userInfo.get("passwordformat") == null ? null : userInfo.get("passwordformat").toString();
            if (Utils.isNullOrEmpty(salt) || Utils.isNullOrEmpty(passwordformat) || !EncryptionUtil.sha256Encode(accountDTO.getOldPass(), salt).equals(passwordformat)) {
                return errorApi("Mật khẩu không hợp lệ");
            }
            String rs = userService.changePassword(accountDTO.getUsername(), EncryptionUtil.sha256Encode(accountDTO.getNewPass(), salt), salt);
            if ("OK".equals(rs)) {
                return successApi(null, "Thành công");
            } else {
                return errorApi(rs);
            }
        }
    }


    @ApiOperation(value = "API get post by user")
    @GetMapping("/listPostByUsername")
    public ResponseEntity listPostByUsername(@RequestParam("username") String username, @RequestParam(value = "buuCuc", required = false) String buuCuc) throws Exception {
        List<OfficeDTO> list = userService.listPostByUsername(username, buuCuc);
        return successApi(list, "Thành công");
    }

    @ApiOperation(value = "API login thông tin nhân sự")
    @PostMapping("/login-noi-bo")
    public ResponseEntity loginNoiBo(@RequestBody UserRequest userRequest) throws Exception {
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();
        UserTokenDto respons = null;
        if (Utils.isNullOrEmpty(username)) {
            throw new VtException(STATUS.E203, "Tài khoản không được để trống");
        }
        if (Utils.isNullOrEmpty(password)) {
            throw new VtException(STATUS.E203, "Mật khẩu không được để trống");
        }
        String params = "username=" + URLEncoder.encode(username.trim(), "UTF-8") + "&password=" + URLEncoder.encode(password.trim(), "UTF-8")
                + "&token=true&appCode=" + userRequest.getAppCode();
        respons = loginByAPIs(params);
        if(respons==null){
            return errorApi("Tài khoản hoặc mật khẩu không hợp lệ.");
        }
        Map<String, Object> result = userService.getUserInfoNB(respons.getStaffCode(), userRequest.getAppCode());
        if (result.get("result") == null) {
            return errorApi("Tài khoản không tồn tại hoặc chưa được gán bưu cục, vui lòng thử lại.");
        }
        result.remove("passwordsalt");
        result.remove("passwordformat");
        List<RoleInfo> resultRole = roleService.getAllRoleByEmployeeCode(respons.getStaffCode(), 71L);
        String list = "";
        if (resultRole != null) {
            for (RoleInfo role : resultRole) {
                if (role.getStatus().equals(1L) || role.getStatus() == 1L) {
                    list = list + role.getId() + ",";
                }
            }
        }
        list = list.replaceAll(",$", "");
        if (list.startsWith(",")) {
            list = list.substring(1, list.length());
        }
        result.put("roleid", list);
        return successApi(new UserResponse(createJWT(result), null, result), "Thành công");
    }

    private HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    HttpURLConnection getConnect(String url) throws Exception {
        HttpURLConnection conn = null;
        URL realUrl = new URL(null, url);
        if (realUrl.getProtocol().toLowerCase().equals("https")) {
            HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
            https.setHostnameVerifier(DO_NOT_VERIFY);
            conn = https;
        } else {
            conn = (HttpURLConnection) realUrl.openConnection();
        }
        return conn;
    }

    public UserTokenDto loginByAPIs(String params) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);;
        String result = "";
        OutputStreamWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        try {
            conn = getConnect(Constants.ssoUrl);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(params);
            out.flush();
            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else if (conn.getResponseCode() == 401) {
                throw new VtException("Tài khoản hoặc mật khẩu không hợp lệ");
            } else {
                throw new IOException("INTERNAL_SERVER_ERROR");
            }
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                logger.error(ex.getLocalizedMessage(), ex);
            }
        }
        String token = result;
        result = getJsonData(result);
        LoginResponseDto rs = mapper.readValue(result, LoginResponseDto.class);
        UserTokenDto user = new UserTokenDto();
        user.setUserId(Long.valueOf(rs.get1UserId()));
        user.setDeptId(Long.valueOf(rs.get1DeptId()));
        user.setDeptName("");
        user.setEmail(rs.get1Email());
        user.setTelephone(rs.get1PhoneNumber());
        user.setFullName(rs.get1FullName());
        user.setStaffCode(rs.get1StaffCode());
        user.setJti(rs.getJti());
        user.setToken(token);
        return user;
    }

    public static String getJsonData(String input) throws Exception {

        final String signingKey = "ahN47WHSA3-_I7wAcfQ7W2qyTKMeQrbDBYJQoENpGeTs8xLWddVPaMfqgC_e_UboPB9wJluMVC3M8CtoBKt7Ow";
        final String encryptionKey = "rle6pMmf5eWeix5LHm2sil_aP8WWl3IB8RtMWsRw1vs";
        final Key key = new AesKey(signingKey.getBytes(StandardCharsets.UTF_8));
        final JsonWebSignature jws = new JsonWebSignature();

        String result = "";
        jws.setCompactSerialization(input);
        jws.setKey(key);
        if (!jws.verifySignature()) {
            throw new Exception("JWT verification failed");
        }
        final byte[] decodedBytes = Base64.decodeBase64(jws.getEncodedPayload().getBytes(StandardCharsets.UTF_8));
        final String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
        final JsonWebEncryption jwe = new JsonWebEncryption();
        final JsonWebKey jsonWebKey = JsonWebKey.Factory
                .newJwk("\n" + "{\"kty\":\"oct\",\n" + " \"k\":\"" + encryptionKey + "\"\n" + "}");
        jwe.setCompactSerialization(decodedPayload);
        jwe.setKey(new AesKey(jsonWebKey.getKey().getEncoded()));
        result = jwe.getPlaintextString();

        return result;
    }


    @ApiOperation(value = "API send otp")
    @GetMapping("/send-otp")
    public ResponseEntity sendSmsOtp(@RequestParam("phone") String phone) throws Exception {
        Map<String, Object> result = userService.sendOTP(Utils.getValid84Phone(phone));
        if (!"OK".equals(result.get("message"))) {
            return errorApi((String) result.get("message"));
        }
        if (result.get("result") == null) {
            return new ResponseEntity(new BaseResponse(true, "Gửi OTP thất bại. Vui lòng liên hệ CSKH để được hỗ trợ.", ""), HttpStatus.OK);
        }
        return new ResponseEntity(new BaseResponse(false, "Gửi mã xác nhận thành công", result.get("result")), HttpStatus.OK);
    }

    @ApiOperation(value = "API check otp")
    @GetMapping("/check-otp")
    public ResponseEntity checkOtp(@RequestParam("phone") String phone, @RequestParam("otp") String otp, @RequestParam("appCode") String appCode) throws Exception {
        Map<String, Object> result = userService.checkOtp(Utils.getValid84Phone(phone), otp, appCode);
        if (result == null || result.get("result") == null) {
            return errorApi("Tài khoản này không tồn tại hoặc chưa được gán bưu cục");
        }
        List<RoleInfo> resultRole = roleService.getAllRoleByUserId((String) ((HashMap) result.get("result")).get("username"), 71L);
        String list = "";
        if (resultRole != null) {
            for (RoleInfo role : resultRole) {
                if (role.getStatus().equals(1L) || role.getStatus() == 1L) {
                    list = list + role.getId() + ",";
                }
            }
        }
        list = list.replaceAll(",$", "");
        if (list.startsWith(",")) {
            list = list.substring(1, list.length());
        }
        result.put("roleid", list);
        return successApi(new UserResponse(createJWT(result), null, result), "Thành công");
    }


    @ApiOperation(value = "API change password by otp")
    @PostMapping("/changePasswordByOtp")
    public ResponseEntity changePasswordByOto(@RequestBody AccountDTO accountDTO) throws Exception {
        Map<String, Object> result = userService.checkOtp(Utils.getValid84Phone(accountDTO.getPhone()), accountDTO.getOtp(), accountDTO.getAppCode());
        if (result == null) {
            return new ResponseEntity(new BaseResponse(true, "OTP không đúng. Vui lòng kiểm tra lại", ""), HttpStatus.OK);
        }
        String salt = EncryptionUtil.salt();
        String rs = userService.changePasswordByOtp(Utils.getValid84Phone(accountDTO.getPhone()), EncryptionUtil.sha256Encode(accountDTO.getNewPass(), salt), salt);
        if ("OK".equals(rs)) {
            return successApi(null, "Thành công");
        } else {
            return errorApi(rs);
        }
    }

    @ApiOperation(value = "API get token")
    @GetMapping("/get-token")
    public ResponseEntity getToken(@RequestParam("sso") String sso, @RequestParam("code") String code) throws Exception {
        try {
            Map<String, Object> result = userService.getToken(sso, code);
            if (result == null) {
                return new ResponseEntity(new BaseResponse(true, "SSO không đúng. Vui lòng kiểm tra lại", ""), HttpStatus.OK);
            }
            return successApi(new UserResponse(createJWT(result), null, result), "Thành công");
        } catch (Exception e) {
            return errorApi(e.getLocalizedMessage());
        }
    }

    @ApiOperation(value = "API get share code")
    @GetMapping("/get-share-code")
    public ResponseEntity getShareCode(@RequestParam("appCode") String appCode) throws Exception {
        try {
            UserInfo userInfo = getCurrentUser();
            userService.getUserInfo(userInfo.getUsername(), appCode);
            Map<String, Object> result = userService.getShareCode((String) ((HashMap) userInfo.getInfos().get("result")).get("username"), appCode);
            if (result == null) {
                return new ResponseEntity(new BaseResponse(true, "App code không tồn tại. Vui lòng kiểm tra lại", ""), HttpStatus.OK);
            }
            Object object = result.get("result");
            return successApi(new UserResponse(createJWT(result), ((HashMap) object).get("sharecode").toString(), ((HashMap) object).get("domain").toString()), "Thành công");
        } catch (Exception e) {
            return errorApi(e.getLocalizedMessage());
        }
    }
}
