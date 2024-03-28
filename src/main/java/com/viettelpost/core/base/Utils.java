package com.viettelpost.core.base;

import com.viettelpost.core.services.domains.PhoneInfo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Utils {
    static List<PhoneInfo> listPhone = Arrays.asList(
            new PhoneInfo("0162", "032"),
            new PhoneInfo("0163", "033"),
            new PhoneInfo("0164", "034"),
            new PhoneInfo("0165", "035"),
            new PhoneInfo("0166", "036"),
            new PhoneInfo("0167", "037"),
            new PhoneInfo("0168", "038"),
            new PhoneInfo("0169", "039"),

            new PhoneInfo("0120", "070"),
            new PhoneInfo("0121", "079"),
            new PhoneInfo("0122", "077"),
            new PhoneInfo("0126", "076"),
            new PhoneInfo("0128", "078"),

            new PhoneInfo("0123", "083"),
            new PhoneInfo("0124", "084"),
            new PhoneInfo("0125", "085"),
            new PhoneInfo("0127", "081"),
            new PhoneInfo("0129", "082"),

            new PhoneInfo("0186", "056"),
            new PhoneInfo("0188", "058"),

            new PhoneInfo("0199", "059")
    );

    public static Date dateToDateFormat(String date, String strFormat) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(strFormat);
            java.util.Date parsed = format.parse(date);
            java.sql.Date sql = new java.sql.Date(parsed.getTime());
            return sql;
        } catch (Exception ex) {

        }
        return null;
    }

    public String sendVTManNotify(String endpointURL, String token, String params) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpointURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(500);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("token", token);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(params.getBytes("UTF-8"));
            os.flush();
            BufferedReader br;
            if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream()), "UTF-8"));
            }
            StringBuilder output = new StringBuilder();
            String tmp;
            while ((tmp = br.readLine()) != null) {
                if (!tmp.isEmpty()) {
                    output.append(tmp);
                }
            }
            return output.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static boolean isNullOrEmpty(Object input) {
        if (input instanceof String) {
            return input == null || ((String) input).trim().isEmpty();
        }

        if (input instanceof List) {
            return input == null || ((List) input).isEmpty();
        }
        return input == null;
    }

    public static String[] validate84Phone(String phone) {
        phone = phone.trim();
        if (phone.startsWith("+")) {
            phone = phone.substring(1);
        }
        if (!Utils.isNumber(phone)) {
            return new String[]{};
        }
        if (phone.startsWith("84") && phone.length() > 9) {
            phone = phone.substring(2);
        }
        if (phone.startsWith("000")) {
            phone = phone.substring(3);
        }
        if (phone.startsWith("00")) {
            phone = phone.substring(2);
        }
        if (phone.startsWith("0")) {
            phone = phone.substring(1);
        }
        if (phone.length() > 10) {
            return new String[]{};
        }
        if ((phone.startsWith("3") || phone.startsWith("5") || phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9")) && phone.length() != 9) {
            return new String[]{};
        }
        if (phone.length() >= 9 && !phone.startsWith("18") && !phone.startsWith("19")) {
            phone = "84" + phone;
        } else {
            return new String[]{phone, phone};
        }
        for (PhoneInfo phoneData : listPhone) {
            if (phoneData.equals(new PhoneInfo(phone.substring(0, 4), null))) {
                return new String[]{phoneData.getNewPhone() + phone.substring(4), phoneData.getOldPhone() + phone.substring(4)};
            }
            if (phoneData.equals(new PhoneInfo(null, phone.substring(0, 3)))) {
                return new String[]{phoneData.getNewPhone() + phone.substring(3), phoneData.getOldPhone() + phone.substring(3)};
            }
        }
        return new String[]{phone, phone};
    }

    public static String getValid84Phone(String phone) {
        if (!Utils.isNumber(phone)) {
            return null;
        }
        String[] arr = validate84Phone(phone);
        String str = null;
        if (arr.length > 0 && !isNullOrEmpty(arr[0])) {
            str = arr[0];
            return str;
        }
        return null;
    }

    public static boolean isNumber(String input) {
        try {
            Long.valueOf(input);
            return true;
        } catch (Exception e) {
            //ignored
        }
        return false;
    }
}
