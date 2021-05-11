package com.th.workbase.common.utils;

/**
 * @Date 2021-03-17-8:27
 * @Author tangJ
 * @Description 依据两点的经纬度计算之间的距离
 * @Version 1.0
 */
public class CalculateDistanceUtil {
    private static double EARTH_RADIUS = 6378.137;// 单位千米

    private static double getRadian(double degree) {
        return degree * Math.PI / 180.0;
    }

    /**
     * 依据经纬度计算两点之间的距离
     *
     * @param lat1 1点的纬度
     * @param lng1 1点的经度
     * @param lat2 2点的纬度
     * @param lng2 2点的经度
     * @return 距离 单位 米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = getRadian(lat1);
        double radLat2 = getRadian(lat2);
        double a = radLat1 - radLat2;// 两点纬度差
        double b = getRadian(lng1) - getRadian(lng2);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s * 1000;
    }
}
