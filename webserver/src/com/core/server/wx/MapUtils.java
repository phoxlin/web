package com.core.server.wx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class MapUtils {
    private static double EARTH_RADIUS = 6378.137D;


    private static double rad(double d) {
        return d * 3.141592653589793D / 180.0D;
    }

    public static long getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
        Double lat1 = Double.valueOf(Double.parseDouble(lat1Str));
        Double lng1 = Double.valueOf(Double.parseDouble(lng1Str));
        Double lat2 = Double.valueOf(Double.parseDouble(lat2Str));
        Double lng2 = Double.valueOf(Double.parseDouble(lng2Str));
        double radLat1 = rad(lat1.doubleValue());
        double radLat2 = rad(lat2.doubleValue());
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1.doubleValue()) - rad(lng2.doubleValue());
        double distance = 2.0D * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2.0D), 2.0D) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(mdifference / 2.0D), 2.0D)));
        distance *= EARTH_RADIUS;
        distance = (double)(Math.round(distance * 10000.0D) / 10L);
        Double x = new Double(distance);
        return x.longValue();
    }

    public static Map<String, String> getAround(String latStr, String lngStr, String raidus) {
        HashMap map = new HashMap();
        Double latitude = Double.valueOf(Double.parseDouble(latStr));
        Double longitude = Double.valueOf(Double.parseDouble(lngStr));
        Double degree = Double.valueOf(111293.63611111112D);
        double raidusMile = Double.parseDouble(raidus);
        Double mpdLng = Double.valueOf(Double.parseDouble(String.valueOf(degree.doubleValue() * Math.cos(latitude.doubleValue() * 0.017453292519943295D)).replace("-", "")));
        Double dpmLng = Double.valueOf(1.0D / mpdLng.doubleValue());
        Double radiusLng = Double.valueOf(dpmLng.doubleValue() * raidusMile);
        Double minLat = Double.valueOf(longitude.doubleValue() - radiusLng.doubleValue());
        Double maxLat = Double.valueOf(longitude.doubleValue() + radiusLng.doubleValue());
        Double dpmLat = Double.valueOf(1.0D / degree.doubleValue());
        Double radiusLat = Double.valueOf(dpmLat.doubleValue() * raidusMile);
        Double minLng = Double.valueOf(latitude.doubleValue() - radiusLat.doubleValue());
        Double maxLng = Double.valueOf(latitude.doubleValue() + radiusLat.doubleValue());
        map.put("minLat", "" + minLat);
        map.put("maxLat", "" + maxLat);
        map.put("minLng", "" + minLng);
        map.put("maxLng", "" + maxLng);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(getDistance("30.57248", "104.05801", "30.57188", "104.05861"));
        System.out.println(getAround("117.11811", "36.68484", "13000"));
    }
}
