package com.th.workbase.common.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    private static String strRead(InputStream inputStream, String charset) {
        StringBuffer strBuffer = new StringBuffer();
        try {
            String line = "";
            InputStreamReader fileIn = new InputStreamReader(inputStream, charset);
            BufferedReader reader = new BufferedReader(fileIn);
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strBuffer.toString();
    }

    private static String buildX_www_form_urlencoded(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            if (StringUtil.isEmpty(entry.getValue())) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=');
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    public static String post(String url, Map<String, String> params) {
        if (url.substring(0, 8).equals("https://")) {
            try {
                return postHttpsURLContentNoCatch(url, "POST", params, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String result = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            String postData = buildX_www_form_urlencoded(params);
            byte[] entity = postData.toString().getBytes("UTF-8");
            URL _url = new URL(url);
            conn = (HttpURLConnection) _url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);// 允许输出数据
            conn.setDoInput(true);
            conn.connect();
            OutputStream outStream = conn.getOutputStream();
            outStream.write(entity);
            outStream.flush();
            outStream.close();
            if (conn.getResponseCode() == 200) {
                is = conn.getInputStream();
                result = strRead(is, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    public static String postHttpsURLContentNoCatch(String url, String method, Map<String, String> params, String encoding)
            throws GeneralSecurityException, IOException {
        StringBuilder buffer = new StringBuilder();
        HttpsURLConnection conn = null;
        try {
            TrustManager[] tm = {new EmptyX509TrustManager()};
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL _url = new URL(url);
            conn = (HttpsURLConnection) _url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);// 允许输出数据
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(method);
            conn.connect();
            String postData = buildX_www_form_urlencoded(params);
            if (StringUtil.isNotEmpty(postData)) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes(encoding));
                outputStream.close();
            }
            // 将返回的输入流转换成字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encoding);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            return buffer.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static class EmptyX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
