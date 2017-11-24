package com.core.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 */
public class RouteMapping {
    private Map<String, RouteObj> rp = new HashMap();

    public RouteMapping() {
    }

    public RouteObj getRouteObj(HttpMethod method, String key, int parsSize) {
        String[] keys = null;
        if(key.startsWith("/")) {
            keys = key.split("/");
            key = keys[1];
        } else {
            keys = key.split("/");
            key = keys[0];
        }

        RouteObj ro = (RouteObj)this.rp.get(key + method + parsSize);
        if(ro != null) {
            return ro;
        } else {
            Logger.error("没有找到:" + key + method + parsSize);
            return null;
        }
    }

    public void addRoute(RouteObj route) {
        HttpMethod[] ms = route.getM();
        if(ms != null && ms.length > 0) {
            HttpMethod[] var6 = ms;
            int var5 = ms.length;

            for(int var4 = 0; var4 < var5; ++var4) {
                HttpMethod m = var6[var4];
                this.rp.put(route.getUrl() + m + route.getParamNum(), route);
                if(route.getAliase() != null && route.getAliase().length() > 0 && !route.getUrl().equals(route.getAliase())) {
                    this.rp.put(route.getAliase() + m + route.getParamNum(), route);
                }
            }
        }

    }

    public static void main(String[] args) {
        String url = "/mm";
        String[] paths = url.split("/");
        System.out.println(paths.length);
    }
}
