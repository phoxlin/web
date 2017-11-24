package com.core.server.db.xml;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import org.apache.commons.collections4.map.LinkedMap;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Data {
    private OrderedMap<String, Object> map = new LinkedMap();
    private String name;

    public Data() {
    }

    public Data(String tablename, Element data) {
        this.name = tablename;
        List li = data.attributes();
        int i = 0;

        for(int l = li.size(); i < l; ++i) {
            Attribute at = (Attribute)li.get(i);
            this.map.put(at.getName().toLowerCase(), at.getValue());
        }

    }

    public List<String> keys() {
        ArrayList li = new ArrayList();
        OrderedMapIterator iterator = this.map.mapIterator();

        while(iterator.hasNext()) {
            String code = (String)iterator.next();
            li.add(code);
        }

        return li;
    }

    public String getStringValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                return String.valueOf(obj);
            }
        }

        return "";
    }

    public Long getLongValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        Long temp = Long.valueOf(0L);
        param = param.toLowerCase();
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                try {
                    temp = Long.valueOf(Long.parseLong(String.valueOf(obj)));
                } catch (Exception var5) {
                    throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to long");
                }
            }
        }

        return temp;
    }

    public boolean getBooleanValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                try {
                    return Utils.isTrue(obj);
                } catch (Exception var4) {
                    throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to Boolean");
                }
            }
        }

        return false;
    }

    public Integer getIntegerValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Integer temp = Integer.valueOf(0);
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                try {
                    temp = Integer.valueOf(Integer.parseInt(String.valueOf(obj)));
                } catch (Exception var5) {
                    throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to integer");
                }
            }
        }

        return temp;
    }

    public Float getFloatValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Float temp = Float.valueOf(0.0F);
        if(this.map != null) {
            Object obj = this.map.get(param);
            String objStr = String.valueOf(obj);
            if(obj != null) {
                try {
                    temp = Float.valueOf(Float.parseFloat(objStr));
                } catch (Exception var6) {
                    if(!objStr.matches("\\d*,?\\d*\\.?\\d*")) {
                        throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to float");
                    }

                    objStr = objStr.replace(",", "");
                    temp = Float.valueOf(Float.parseFloat(objStr));
                }
            }
        }

        return temp;
    }

    public Double getDoubleValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Double temp = Double.valueOf(0.0D);
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                try {
                    temp = Double.valueOf(Double.parseDouble(String.valueOf(obj)));
                } catch (Exception var5) {
                    throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to double");
                }
            }
        }

        return temp;
    }

    public Date getDateValue(String param) throws Exception {
        if(param == null) {
            param = "";
        }

        param = param.toLowerCase();
        Date temp = new Date(System.currentTimeMillis());
        if(this.map != null) {
            Object obj = this.map.get(param);
            if(obj != null) {
                try {
                    SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        temp = (Date)e.parse(String.valueOf(obj));
                    } catch (Exception var10) {
                        e = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        try {
                            temp = (Date)e.parse(String.valueOf(obj));
                        } catch (Exception var9) {
                            e = new SimpleDateFormat("yyyyMMdd");

                            try {
                                temp = (Date)e.parse(String.valueOf(obj));
                            } catch (Exception var8) {
                                throw var8;
                            }
                        }
                    }
                } catch (Exception var11) {
                    throw new Exception("Get Xml Data[" + this.name + "] result value error: can\'t convert result value [" + obj + "]to date");
                }
            }
        }

        return temp;
    }

    public String getFormatStringValue(String param, String format) throws Exception {
        try {
            Date e = this.getDateValue(param);
            SimpleDateFormat sf = new SimpleDateFormat(format);
            return sf.format(e);
        } catch (Exception var5) {
            return "";
        }
    }

    public JSONObject toJson() {
        return new JSONObject(this.map);
    }

    public OrderedMap<String, Object> getMap() {
        return this.map;
    }
}
