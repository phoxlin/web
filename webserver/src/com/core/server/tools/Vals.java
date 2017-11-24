package com.core.server.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Vals {
    public static List<String> getListStringValue(List<Map<String, Object>> vals, String name) {
        ArrayList li = new ArrayList();
        if(vals != null && vals.size() > 0) {
            int i = 0;

            for(int l = vals.size(); i < l; ++i) {
                Map m = (Map)vals.get(i);
                String val = Utils.getMapStringValue(m, name);
                li.add(val);
            }
        }

        return li;
    }

    public static List<Long> getListLongValue(List<Map<String, Object>> vals, String name) {
        ArrayList li = new ArrayList();
        if(vals != null && vals.size() > 0) {
            int i = 0;

            for(int l = vals.size(); i < l; ++i) {
                Map m = (Map)vals.get(i);
                Long val = Utils.getMapLongValue(m, name);
                li.add(val);
            }
        }

        return li;
    }

    public static List<Integer> getListIntegerValue(List<Map<String, Object>> vals, String name) {
        ArrayList li = new ArrayList();
        if(vals != null && vals.size() > 0) {
            int i = 0;

            for(int l = vals.size(); i < l; ++i) {
                Map m = (Map)vals.get(i);
                Integer val = Integer.valueOf(Utils.getMapIntegerValue(m, name));
                li.add(val);
            }
        }

        return li;
    }
}
