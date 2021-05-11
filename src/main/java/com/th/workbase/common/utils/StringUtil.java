package com.th.workbase.common.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

public class StringUtil {


    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.equals("");
    }

    public static String join(Object[] arr, String separator) {
        StringBuffer sbBuffer = new StringBuffer(512);
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sbBuffer.append(separator);
            }
            sbBuffer.append(arr[i]);
        }
        return sbBuffer.toString();
    }

    public static String join(int[] arr, String separator) {
        return join(arr, separator, false);
    }

    public static String join(int[] arr, String separator, boolean isHex) {
        StringBuffer sbBuffer = new StringBuffer(512);
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sbBuffer.append(separator);
            }
            if (isHex) {
                sbBuffer.append(Integer.toHexString(arr[i]));
            } else {
                sbBuffer.append(arr[i]);
            }

        }
        return sbBuffer.toString();
    }

    public static String join(byte[] arr, String separator) {
        StringBuffer sbBuffer = new StringBuffer(512);
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sbBuffer.append(separator);
            }
            sbBuffer.append(arr[i]);
        }
        return sbBuffer.toString();
    }

    public static String join(List<String> arr, String separator) {
        return join(arr.toArray(), separator);
    }

    /**
     * 字符串分隔，返回字符串数组，此方法为避免后面有空字串时，分隔长度不对的问题
     *
     * @param str      源字符串
     * @param interval 分隔
     * @return 字符串数组
     */
    public static String[] split(String str, String interval) {
        String[] strings = str.replace(interval, " " + interval + " ").split(interval);
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
        }
        return strings;
    }

    public static String Concat(String... strings) {
        StringBuffer strBuffer = new StringBuffer(256);
        for (String str : strings) {
            strBuffer.append(str);
        }
        return strBuffer.toString();
    }

    public static String Concat(Object... objects) {
        StringBuffer strBuffer = new StringBuffer(256);
        for (Object o : objects) {
            strBuffer.append(String.valueOf(o));
        }
        return strBuffer.toString();
    }

    /**
     * 模仿C#格式化字符串
     *
     * @param str
     * @param args
     * @return
     */
    public static String format(String str, String... args) {
        for (int i = 0; i < args.length; i++) {
            str = str.replace("{" + i + "}", args[i]);
        }
        return str;
    }

    public static String paddingLeft(int intVal, int width) {
        return String.format("%1$0" + width + "d", intVal);
    }

    public static String paddingLeft(String str, int width) {
        StringBuffer sb = new StringBuffer(width);
        for (int i = 0; i < width - str.length(); i++) {
            sb.append("0");
        }
        sb.append(str);
        return sb.toString();

    }

    /**
     * 左补空格
     *
     * @param str   原
     * @param str1  补
     * @param width 总长度
     * @return
     */
    public static String paddingLeft(String str, String str1, int width) {
        int strLen = str.length();
        StringBuffer sb = new StringBuffer();
        while (strLen + str.length() < width) {

            sb.append(str1);
            strLen = sb.length();
        }

        return sb.append(str).toString();

    }

    public static String encryptSha1(String str) {
        return encrypt(str, "SHA-1");
    }

    public static String encrypt(String strSrc, String encName) {
        // parameter strSrc is a string will be encrypted,
        // parameter encName is the algorithm name will be used.
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    private static String bytes2Hex(byte[] bin) {
        StringBuffer buf = new StringBuffer(512);
        for (int i = 0; i < bin.length; ++i) {
            int x = bin[i] & 0xFF, h = x >>> 4, l = x & 0x0F;
            buf.append((char) (h + ((h < 10) ? '0' : 'a' - 10)));
            buf.append((char) (l + ((l < 10) ? '0' : 'a' - 10)));
        }
        return buf.toString();
    }

    public static String getFormatDate(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date getDateFromFormat(String dateFormat) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateFormat);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date getDatetimeFromFormat(String datetime) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date getDatetimeFrom14(String datetime) throws Exception {
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(datetime);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取今天的日期，没有时间
     *
     * @return
     * @throws ParseException
     */
    public static Date getCurrentDate() throws ParseException {
        return getDate(new Date());
    }

    // 获得当天0点时间
    public static long getTimesmorning() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    // 获得当天24点时间
    public static long getTimesnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获得日期时间中的日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateString = sdf.format(date);
        return sdf.parse(dateString);
    }

    /**
     * 获得文件扩展名，带 .，如ABC.FSN 返回 .FSN
     *
     * @param fileName 文件
     * @return 扩展名
     */
    public static String getExtensionName(String fileName) {
        int pos = fileName.lastIndexOf('.');
        if (pos > 0) {
            return fileName.substring(pos).toLowerCase();
        } else {
            return "";

        }
    }

    public static String fomatDatetime(Date d) {
        return String.format("%tF %tT", d, d);
    }

    public static String fomatDate(Date d) {
        return String.format("%tF", d);
    }

    public static String fomatTime(Date d) {
        return String.format("%tT", d);
    }

    public static String fomatMillionSecond(Date d) {
        return String.format("%tF %tT.%tL", d, d, d);
    }

    /**
     * 创建日期2011-4-25上午10:12:38 修改日期 作者：dh *TODO 使用Base64加密算法加密字符串 return
     */
    public static String encodeStr(String plainText) {
        byte[] b = plainText.getBytes();
        Base64 base64 = new Base64();
        b = base64.encode(b);
        String s = new String(b);
        return s;
    }

    /**
     * 创建日期2011-4-25上午10:15:11 修改日期 作者：dh *TODO 使用Base64加密 return
     */
    public static String decodeStr(String encodeStr) {
        byte[] b = encodeStr.getBytes();
        Base64 base64 = new Base64();
        b = base64.decode(b);
        String s = new String(b);
        return s;
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     *
     * @param smdate 日期1
     * @param bdate  日期2
     * @return 毫秒差值
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 将字符串变成日期
     *
     * @param dateString
     * @param dateFormat
     * @return
     */
    public static Date getParaDate(String dateString, String dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat).parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串翻转
     *
     * @param str 要翻转的字符串
     * @return 翻转后的字符串
     */
    public static String StringOverturn(String str) {
        StringBuffer sBuffer = new StringBuffer(str).reverse();
        return sBuffer.toString();
    }

    /**
     * 字符串左补齐
     *
     * @param c   要填充的字符
     * @param l   填充后字符串的总长度
     * @param tmp 要格式化的字符串
     */
    public static String flushLeft(String c, long l, String tmp) {
        String str = "";
        String cs = "";
        if (tmp.length() > l)
            str = tmp;
        else
            for (int i = 0; i < l - tmp.length(); i++)
                cs = cs + c;
        str = cs + tmp;
        return str;
    }

    /**
     * 路径结合，自动去除连接中多余的分隔符/或\\ 后最的结尾是否带分隔符/或\\ ，如果最后输入的路径后面有分隔就带，最后输入的路径后面不带则不带 2015-3-3 lbf
     *
     * @param paths 多个路径
     * @return 结合后的结果
     */
    public static String combinePaths(String... paths) {
        if (paths == null || paths.length == 0) {
            return "";
        }
        if (paths.length == 1) {
            return paths[0];
        }
        StringBuffer buffer = new StringBuffer(256);
        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == null || paths[i].isEmpty()) {
                continue;
            }
            if (buffer.length() > 0) {
                buffer.append("/");
            }
            if (i > 0) {
                if (paths[i].charAt(0) == '/' || paths[i].charAt(0) == '\\') {
                    paths[i] = paths[i].substring(1);
                }
            }
            if (i != paths.length - 1) {
                if (paths[i].charAt(paths[i].length() - 1) == '/' || paths[i].charAt(paths[i].length() - 1) == '\\') {

                    paths[i] = paths[i].substring(0, paths[i].length() - 1);
                }
            }
            buffer.append(paths[i]);
        }
        return buffer.toString();
    }

    /**
     * 加日期
     *
     * @param d    日期
     * @param days 增加的天数
     * @return 结果日期
     */
    public static Date addDays(Date d, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 生成文件并写入内容
     *
     * @param path   文件路径
     * @param name   文件名称（带扩展名）
     * @param unCode 输出文件编码格式
     * @param txt    内容列表
     * @author hut
     */
    public static void saveInfoToFile(String path, String name, String unCode, List<String> txt) {
        BufferedWriter writer = null;
        File a = new File(path + "/" + name + ".temp");
        try {
            if (!a.exists()) {
                a.createNewFile();
            }
            FileOutputStream writerStream = new FileOutputStream(a);
            writer = new BufferedWriter(new OutputStreamWriter(writerStream, unCode));
            for (String context : txt) {
                writer.write(context);
                writer.newLine();// 换行
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                a.renameTo(new File(path + "/" + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成文件并写入property内容
     *
     * @param path   文件路径
     * @param name   文件名称（带扩展名）
     * @param unCode 输出文件编码格式
     * @param p      内容列表
     * @author hut
     */
    public static void saveInfoToProFile(String path, String name, String unCode, Properties p) {
        File a = new File(path + "/" + name + ".temp");
        OutputStream os = null;
        OutputStreamWriter osw = null;
        try {
            if (!a.exists()) {
                a.createNewFile();
            }
            os = new FileOutputStream(a);
            osw = new OutputStreamWriter(os, "utf-8");
            p.store(os, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 将临时文件改为正式文件
            a.renameTo(new File(path + "/" + name));
        }
    }

    public static String turn(String str) {

        try {
            return new String(str.trim().getBytes("ISO-8859-1"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获得输入日期前或后多少天的日期 正数向后，负数向前
     *
     * @param day   格式yyyyMMdd
     * @param count
     * @author hut
     */
    public static String getDay(String day, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt = sdf.parse(day, new ParsePosition(0));
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DATE, count);// 你要加减的日期
        Date dt1 = rightNow.getTime();
        String reStr = StringUtil.getFormatDate("yyyyMMdd", dt1);
        return reStr;
    }

    /**
     * 获得输入日期前或后多少天的日期 正数向后，负数向前
     *
     * @param day   格式yyyyMMdd
     * @param count
     * @author hut
     */
    public static String getDay_yyyy(String day, int count) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = sdf.parse(day, new ParsePosition(0));
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(dt);
        rightNow.add(Calendar.DATE, count);// 你要加减的日期
        Date dt1 = rightNow.getTime();
        String reStr = StringUtil.getFormatDate("yyyy-MM-dd", dt1);
        return reStr;
    }

    /**
     * 删除文件夹以及文件夹下的文件
     *
     * @param folderPath
     * @author hut
     */
    public static void deleteFolder(String folderPath) throws Exception {
        try {
            File file = new File(folderPath);
            if (file.isDirectory()) {
                File[] array = file.listFiles();
                if (array != null && array.length > 0) {
                    for (File child : array) {
                        deleteFolder(child.getAbsolutePath());
                    }
                }
                file.delete();
            } else {
                file.delete();
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * 将文件转成byte数组
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] getContent(String filePath) throws IOException {
        File file = new File(filePath);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            return null;
        }
        FileInputStream fi = new FileInputStream(file);
        byte[] buffer = new byte[(int) fileSize];
        int offset = 0;
        int numRead = 0;
        while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset != buffer.length) {
            fi.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        fi.close();
        return buffer;
    }

    /**
     * @param index
     * @return
     */
    public static String getExcelColumnLabel(int index) {
        String rs = "";
        do {
            index--;
            rs = ((char) (index % 26 + (int) 'A')) + rs;
            index = (int) ((index - index % 26) / 26);
        } while (index > 0);
        return rs;
    }

    public static byte[] int2byteArray(int num) {
        byte[] result = new byte[4];
        result[3] = (byte) (num >> 24);// 取最高8位放到0下标
        result[2] = (byte) (num >> 16);// 取次高8为放到1下标
        result[1] = (byte) (num >> 8); // 取次低8位放到2下标
        result[0] = (byte) (num); // 取最低8位放到3下标
        return result;
    }

    public static byte[] short2byteArray(int num) {
        byte[] result = new byte[2];
        result[1] = (byte) (num >> 8); // 取次低8位放到2下标
        result[0] = (byte) (num); // 取最低8位放到3下标
        return result;
    }

    public static byte short1byteArray(int num) {
        byte result;
        result = (byte) (num); // 取最低8位放到3下标
        return result;
    }


    /**
     * null 转换成空字符串
     *
     * @param arg
     * @return
     */
    public static String coverNullToEmpty(String arg) {
        if (arg == null) {
            return "";
        } else {
            return arg;
        }
    }

    /**
     * 读取txt文件
     *
     * @param path
     * @return
     */
    public static String readtxt(String path) {
        String result = "";
        if (isNullOrEmpty(path)) {
            return "path is null";
        }
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return "file not find";
        }
        FileInputStream stream = null;
        InputStreamReader reader = null;
        BufferedReader br = null;
        try {
            stream = new FileInputStream(file);
            reader = new InputStreamReader(stream);
            br = new BufferedReader(reader);
            String s = null;
            while ((s = br.readLine()) != null) {
                result = result + s.replace(" ", "&nbsp;") + "</br>";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 将null转成空字符串
     *
     * @param arg
     * @return
     */
    public static String converNullToEmpty(String arg) {
        if (arg == null || "null".equals(arg)) {
            return "";
        } else {
            return arg;
        }
    }

    /**
     * 获得本地ip
     *
     * @return
     */
    public static String getLocalIp() {
        Enumeration allNetInterfaces;
        String ipStr = "";
        InetAddress ip = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        if (netInterface.getName().contains("eth") && !netInterface.getDisplayName().contains("VMware")
                                && !"127.0.0.1".equals(ip.getHostAddress())
                                && !"localhost".equals(ip.getHostAddress())) {
                            ipStr = ip.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipStr;
    }

    /**
     * 通过request获得ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ip;
    }

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public static String urlEncode(String data) {
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String appendURLParams(String url, String params) {
        if (isNotEmpty(params)) {
            if (url.indexOf('?') >= 0) {
                return url + '&' + params;
            } else {
                return url + '?' + params;
            }
        } else {
            return url;
        }
    }

    public static void writeJSON(HttpServletResponse response, JSONObject jo) throws IOException {
        if (jo == null) {
            jo = new JSONObject();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jo.toJSONString());
    }

    public static String getMonthStr(String arg) {
        if ("1".equals(arg) || "01".equals(arg)) {
            return "一月";
        } else if ("2".equals(arg) || "02".equals(arg)) {
            return "二月";
        } else if ("3".equals(arg) || "03".equals(arg)) {
            return "三月";
        } else if ("4".equals(arg) || "04".equals(arg)) {
            return "四月";
        } else if ("5".equals(arg) || "05".equals(arg)) {
            return "五月";
        } else if ("6".equals(arg) || "06".equals(arg)) {
            return "六月";
        } else if ("7".equals(arg) || "07".equals(arg)) {
            return "七月";
        } else if ("8".equals(arg) || "08".equals(arg)) {
            return "八月";
        } else if ("9".equals(arg) || "09".equals(arg)) {
            return "九月";
        } else if ("10".equals(arg)) {
            return "十月";
        } else if ("11".equals(arg)) {
            return "十一月";
        } else if ("12".equals(arg)) {
            return "十二月";
        } else {
            return arg;
        }
    }
}
