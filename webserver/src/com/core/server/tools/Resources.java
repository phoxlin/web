package com.core.server.tools;

import com.core.enuts.OSType;
import com.core.server.log.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Resources {
    public static boolean DEVELOPMENT = true;
    public static boolean DBLog = false;
    private static String os = System.getProperties().getProperty("os.name");
    private static String os_arch = System.getProperties().getProperty("os.arch");
    private static Map<String, String> dataInfo = new HashMap();
    private static String[] propsFile = new String[]{"application.properties"};
    private static String prefix = "linux_";
    private static boolean inited = false;
    private static Object ResourceLock = new Object();
    public static String baseHttp = getProperty("base", "http://localhost:8000/43gdfl/");

    public Resources() {
    }

    public static void init() {
        Object var0 = ResourceLock;
        synchronized(ResourceLock) {
            if(!DEVELOPMENT) {
                inited = true;
            }

            OSType osType = getOSType();
            if(osType == OSType.WIN) {
                prefix = "windows_";
            } else if(osType == OSType.MAC) {
                prefix = "mac_";
            } else {
                prefix = "linux_";
            }

            try {
                if(propsFile != null && propsFile.length > 0) {
                    int value = 0;

                    for(int dblog = propsFile.length; value < dblog; ++value) {
                        Properties props = new Properties();
                        FileInputStream fi = new FileInputStream(new File(Utils.getRootClassPath() + propsFile[value]));
                        props.load(fi);
                        Iterator var7 = props.keySet().iterator();

                        while(var7.hasNext()) {
                            Object key = var7.next();
                            String val = props.getProperty(key.toString());
                            val = new String(val.getBytes("ISO-8859-1"), "UTF-8");
                            dataInfo.put(key.toString().toLowerCase(), val);
                        }

                        if(fi != null) {
                            fi.close();
                        }
                    }
                }
            } catch (Exception var9) {
                Logger.error(var9);
            }

            String var11 = (String)dataInfo.get(prefix + "development");
            if(var11 == null || var11.length() <= 0) {
                var11 = (String)dataInfo.get("development");
            }

            DEVELOPMENT = Utils.isTrue(var11);
            String var12 = (String)dataInfo.get((prefix + "DBLog").toLowerCase());
            if(var12 == null || var12.length() <= 0) {
                var12 = (String)dataInfo.get("DBLog".toLowerCase());
            }

            DBLog = Utils.isTrue(var12);
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException var13) {
            var13.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }

        }

        return sb.toString();
    }

    public static void main(String[] args) {
        List temps = getListProperty("test.test");
        Iterator var3 = temps.iterator();

        while(var3.hasNext()) {
            String t = (String)var3.next();
            System.out.println(t);
        }

    }

    private static String getPropertyByOS(String key) {
        if(!inited) {
            init();
        }

        key = key.toLowerCase();
        String value = (String)dataInfo.get(prefix + key);
        if(value == null || value.length() <= 0) {
            value = (String)dataInfo.get(key);
        }

        return value;
    }

    public static String getPropertyByOS(String key, String defaultValue) {
        if(!inited) {
            init();
        }

        key = key.toLowerCase();
        String value = (String)dataInfo.get(prefix + key);
        if(value == null || value.length() <= 0) {
            value = (String)dataInfo.get(key);
            if(value == null || value.length() <= 0) {
                value = defaultValue;
            }
        }

        return value;
    }

    public static int getIntProperty(String key) {
        Double d = Double.valueOf(Double.parseDouble(getProperty(key)));
        return d.intValue();
    }

    public static List<String> getListProperty(String key) {
        String pattern = key + "\\.(.*)";
        Pattern r = Pattern.compile(pattern);
        HashMap info = new HashMap();
        ArrayList keys = new ArrayList();
        Iterator k = dataInfo.keySet().iterator();

        while(k.hasNext()) {
            String temps = (String)k.next();

            try {
                Matcher m = r.matcher(temps);
                if(m.find()) {
                    String key2 = m.group(1);
                    String v = getProperty(temps);
                    info.put(key2, v);
                    keys.add(key2);
                }
            } catch (Exception var10) {
                ;
            }
        }

        Collections.sort(keys);
        ArrayList temps1 = new ArrayList();
        Iterator m1 = keys.iterator();

        while(m1.hasNext()) {
            String k1 = (String)m1.next();
            temps1.add((String)info.get(k1));
        }

        return temps1;
    }

    public static List<String> getListProperty(String key, String[] defaultList) {
        List temps = getListProperty(key);
        if(temps.size() <= 0 && defaultList != null && defaultList.length > 0) {
            int i = 0;

            for(int l = defaultList.length; i < l; ++i) {
                temps.add(defaultList[i]);
            }
        }

        return temps;
    }

    public static List<String> getListProperty(String key, List<String> defaultList) {
        List temps = getListProperty(key);
        return temps.size() <= 0?defaultList:temps;
    }

    public static long getLongProperty(String key) {
        Double d = Double.valueOf(Double.parseDouble(getProperty(key)));
        return d.longValue();
    }

    public static JSONObject getJsonObjectProperty(String key) {
        return new JSONObject(getProperty(key));
    }

    public static JSONArray getJsonArrayProperty(String key) {
        return new JSONArray(getProperty(key));
    }

    public static float getFloatProperty(String key) {
        return Float.parseFloat(getProperty(key));
    }

    public static boolean getBooleanProperty(String key) {
        String ob = getProperty(key);
        return Utils.isTrue(ob);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String val = getProperty(key);
        if(val != null && val.length() > 0) {
            Double d = Double.valueOf(Double.parseDouble(getProperty(key)));
            return d.intValue();
        } else {
            return defaultValue;
        }
    }

    public static long getLongProperty(String key, long defaultValue) {
        String val = getProperty(key);
        if(val != null && val.length() > 0) {
            Double d = Double.valueOf(Double.parseDouble(getProperty(key)));
            return d.longValue();
        } else {
            return defaultValue;
        }
    }

    public static JSONObject getJsonObjectProperty(String key, JSONObject defaultValue) {
        String val = getProperty(key);
        return val != null && val.length() > 0?new JSONObject(getProperty(key)):defaultValue;
    }

    public static JSONArray getJsonArrayProperty(String key, JSONArray defaultValue) {
        String val = getProperty(key);
        return val != null && val.length() > 0?new JSONArray(getProperty(key)):defaultValue;
    }

    public static float getFloatProperty(String key, float defaultValue) {
        String val = getProperty(key);
        return val != null && val.length() > 0?Float.parseFloat(getProperty(key)):defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String val = getProperty(key);
        return val != null && val.length() > 0?"true".equalsIgnoreCase(val):defaultValue;
    }

    public static String getProperty(String key) {
        return getPropertyByOS(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return getPropertyByOS(key, defaultValue);
    }

    public static OSType getOSType() {
        OSType type = OSType.WIN;
        if(os == null) {
            os = "";
        }

        if(os.startsWith("Windows")) {
            type = OSType.WIN;
        } else if(os.startsWith("Mac")) {
            type = OSType.MAC;
        } else {
            type = OSType.LIUNX;
        }

        return type;
    }

    public static boolean isX64() {
        return os_arch.contains("64");
    }
}
