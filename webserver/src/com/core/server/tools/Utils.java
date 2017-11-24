package com.core.server.tools;

import com.core.SFile;
import com.core.server.db.DBUtils;
import com.core.server.log.Logger;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.Security;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Utils {
    public static Executor execute = Executors.newFixedThreadPool(Resources.getIntProperty("SYS_THREAD_POOL_NUM", 5));
    public static final String ISO_STRING = "ISO-8859-1";
    public static final String GBK = "GB18030";
    public static final String UTF8 = "UTF-8";
    private static DecimalFormat df = new DecimalFormat("0.##");
    private static String goodStrRegEx = "[\\u4e00-\\u9fa5\\w~!@#$%\\^&\\*\\(\\)\\+=\\-_,，，.，。“”！<>《》\\{\\}| （）】【，。！]*";
    private static final char[] cnNumbers = new char[]{'〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
    private static final char[] series = new char[]{' ', '十', '百', '仟', '万', '拾', '百', '仟', '亿'};
    private static final String csvFlagRegx = "\\G(?:^|,)(?:\"([^\"]*+(?:\"\"[^\"]*+)*+)\"|([^\",]*+))";
    private static final String[] hexs = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "\'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "@", "[", "\",", "~", "。", "“", "”", "【", "】", "，", "；", "]", "^", "_", "`", "{", "|", "}", "《", "》", "？", "￥"};
    private static Configuration freemarkerCfg = new Configuration();
    private static Pattern phonePattern = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
    private static int DEFAULT_WIDTH;
    private static int UNIT_WIDTH = 15;
    private static String basePath = null;



    public static List<String> split(String str, String[] flag) {
        if(str != null && str.trim().length() > 0 && flag != null && flag.length > 0) {
            String ff = flag[0];

            for(int i = 1; i < flag.length; ++i) {
                String f = flag[i];
                str = str.replace(f, ff);
            }

            return split(str, (String)ff);
        } else {
            return new ArrayList();
        }
    }

    private static List<String> split(String str, String flag) {
        ArrayList li = new ArrayList();
        if(str != null && str.length() > 0) {
            String[] temps = str.split(flag);
            String[] var7 = temps;
            int var6 = temps.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                String t = var7[var5];
                if(t != null && t.length() > 0) {
                    li.add(t);
                }
            }
        }

        return li;
    }

    public static boolean isNull(Object str) {
        return str == null?true:str.toString().equalsIgnoreCase("null") || str.toString().length() <= 0;
    }

    public static boolean beforeTime(Date src, Date desc) throws Exception {
        String srcStr = parseData(src, "HH:mm");
        String descStr = parseData(desc, "HH:mm");
        srcStr = "2000-01-01 " + srcStr + ":00";
        descStr = "2000-01-01 " + descStr + ":00";
        Date srcDate = parse2Date(srcStr, "yyyy-MM-dd HH:mm:ss");
        Date descDate = parse2Date(descStr, "yyyy-MM-dd HH:mm:ss");
        return srcDate.before(descDate);
    }

    public static String UTF2Str(String utfString) {
        StringBuilder sb = new StringBuilder();
        boolean i = true;
        int pos = 0;

        int i1;
        while((i1 = utfString.indexOf("\\u", pos)) != -1) {
            sb.append(utfString.substring(pos, i1));
            if(i1 + 5 < utfString.length()) {
                pos = i1 + 6;
                sb.append((char)Integer.parseInt(utfString.substring(i1 + 2, i1 + 6), 16));
            }
        }

        sb.append(utfString.substring(pos));
        return sb.toString();
    }

    public static boolean beforeOrEqualsTime(Date src, Date desc) throws Exception {
        String srcStr = parseData(src, "HH:mm");
        String descStr = parseData(desc, "HH:mm");
        if(srcStr.equals(descStr)) {
            return true;
        } else {
            srcStr = "2000-01-01 " + srcStr + ":00";
            descStr = "2000-01-01 " + descStr + ":00";
            Date srcDate = parse2Date(srcStr, "yyyy-MM-dd HH:mm:ss");
            Date descDate = parse2Date(descStr, "yyyy-MM-dd HH:mm:ss");
            return srcDate.before(descDate);
        }
    }

    public static boolean afterTime(Date src, Date desc) throws Exception {
        return beforeTime(desc, src);
    }

    public static boolean afterOrEqualsTime(Date src, Date desc) throws Exception {
        return beforeOrEqualsTime(desc, src);
    }

    public static boolean equelTime(Date src, Date desc) {
        String srcStr = parseData(src, "HH:mm");
        String descStr = parseData(desc, "HH:mm");
        return srcStr.equals(descStr);
    }

    public static boolean isMobile(String str) {
        Matcher m = phonePattern.matcher(str);
        return m.matches();
    }

    public static String int2hex(int i) {
        return i >= 0 && i <= 62?hexs[i]:"-";
    }

    public static int dateCompareToDate(Date d1, Date d2) throws Exception {
        if(d1 != null && d2 != null) {
            String d1s = parseData(d1, "yyyy-MM-dd");
            String d2s = parseData(d2, "yyyy-MM-dd");
            Date date1 = parse2Date(d1s + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            Date date2 = parse2Date(d2s + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
            return date1.compareTo(date2);
        } else {
            throw new Exception("时间比较不能为空");
        }
    }

    public static int datetimeCompareToDatetime(Date d1, Date d2) throws Exception {
        if(d1 != null && d2 != null) {
            return d1.compareTo(d2);
        } else {
            throw new Exception("时间比较不能为空");
        }
    }

    public static Date dateAddDay(Date time, int days) {
        Calendar cd = Calendar.getInstance();
        cd.setTime(time);
        cd.add(5, days);
        return cd.getTime();
    }

    public static String toPrice(String str) {
        try {
            BigDecimal e = new BigDecimal(str);
            return String.valueOf(e.setScale(2, 4).doubleValue());
        } catch (Exception var2) {
            return "0";
        }
    }

    public static String toPriceFromLongStr(String str) {
        try {
            BigDecimal e = new BigDecimal(str);
            return toPrice(e.longValue());
        } catch (Exception var2) {
            return "0";
        }
    }

    public static long toPriceLong(String str) {
        try {
            BigDecimal e = new BigDecimal(str);
            return e.multiply(new BigDecimal(100)).longValue();
        } catch (Exception var2) {
            return 0L;
        }
    }

    public static String toPrice(long val) {
        StringBuilder sb = new StringBuilder();
        long prefix = val / 100L;
        sb.append(prefix);
        sb.append(".");
        long fen = Math.abs(val % 100L);
        if(fen < 10L) {
            sb.append("0");
        }

        sb.append(fen);
        return sb.toString();
    }

    public static List<String> getStringList(String ids) {
        ArrayList li = new ArrayList();
        String[] ss = ids.split(",");
        int i = 0;

        for(int l = ss.length; i < l; ++i) {
            String s1 = ss[i];
            if(s1 != null && s1.length() > 0) {
                String[] s1s = s1.split(";");
                String[] var10 = s1s;
                int var9 = s1s.length;

                for(int var8 = 0; var8 < var9; ++var8) {
                    String ts1 = var10[var8];
                    if(ts1 != null && ts1.length() > 0) {
                        String[] ts1s = ts1.split("，");
                        String[] var15 = ts1s;
                        int var14 = ts1s.length;

                        for(int var13 = 0; var13 < var14; ++var13) {
                            String ts1s1 = var15[var13];
                            if(ts1s1 != null && ts1s1.length() > 0) {
                                li.add(ts1s1);
                            }
                        }
                    }
                }
            }
        }

        return li;
    }

    public static String codeString(File file) throws Exception {
        BufferedInputStream bin = null;

        String var5;
        try {
            bin = new BufferedInputStream(new FileInputStream(file));
            int p = (bin.read() << 8) + bin.read();
            String code = null;
            switch(p) {
                case 61371:
                    code = "UTF-8";
                    break;
                case 65279:
                    code = "UTF-16BE";
                    break;
                case 65534:
                    code = "Unicode";
                    break;
                default:
                    code = "GBK";
            }

            var5 = code;
        } finally {
            if(bin != null) {
                try {
                    bin.close();
                } catch (Exception var10) {
                    ;
                }
            }

        }

        return var5;
    }

    public static void createImg(String str, OutputStream os) throws Exception {
        Qrcode qrcode = new Qrcode();
        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');
        qrcode.setQrcodeVersion(12);
        Object buff = null;
        byte[] var9 = str.getBytes("utf-8");
        boolean[][] bRect = qrcode.calQrcode(var9);
        DEFAULT_WIDTH = bRect.length * UNIT_WIDTH;
        BufferedImage bi = new BufferedImage(DEFAULT_WIDTH, DEFAULT_WIDTH, 1);
        Graphics2D g = bi.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, DEFAULT_WIDTH, DEFAULT_WIDTH);
        g.setColor(Color.black);
        if(var9.length > 0 && var9.length < 300) {
            for(int i = 0; i < bRect.length; ++i) {
                for(int j = 0; j < bRect.length; ++j) {
                    if(bRect[j][i]) {
                        g.fillRect(j * UNIT_WIDTH, i * UNIT_WIDTH, UNIT_WIDTH - 1, UNIT_WIDTH - 1);
                    }
                }
            }

            g.dispose();
            bi.flush();
            ImageIO.write(bi, "jpg", os);
        } else {
            throw new Exception("太长了");
        }
    }

    public static List<String> getFileContentList(File file) {
        ArrayList list = new ArrayList();
        if(file.exists()) {
            BufferedReader br = null;

            try {
                String e = codeString(file);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), e));
                String line = null;

                while((line = br.readLine()) != null) {
                    list.add(line);
                }
            } catch (Exception var13) {
                Logger.error(var13);
            } finally {
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException var12) {
                        ;
                    }
                }

            }
        }

        return list;
    }

    public static List<String> getCsvFields(String line) throws Exception {
        Matcher main = Pattern.compile("\\G(?:^|,)(?:\"([^\"]*+(?:\"\"[^\"]*+)*+)\"|([^\",]*+))").matcher(line);
        Matcher mquote = Pattern.compile("\"\"").matcher("");

        ArrayList fields;
        String field;
        for(fields = new ArrayList(); main.find(); fields.add(field)) {
            if(main.start(2) >= 0) {
                field = main.group(2);
            } else {
                field = mquote.reset(main.group(1)).replaceAll("\"");
            }
        }

        return fields;
    }

    public static SFile saveFileFromUrl(String url, String ext) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        SFile var10;
        try {
            HttpGet e = new HttpGet(url);
            response = httpclient.execute(e);
            HttpEntity entity = response.getEntity();
            if(entity == null) {
                throw new Exception("下载文件失败");
            }

            String content = EntityUtils.toString(entity);

            try {
                JSONObject sf1 = new JSONObject(content);
                String errmsg = sf1.getString("errmsg");
                throw new Exception(errmsg);
            } catch (Exception var21) {
                Logger.error(var21);
                SFile sf = saveContent2File(content, DBUtils.uuid() + "." + ext.toLowerCase(), "-1", "-1", DBUtils.uuid());
                var10 = sf;
            }
        } catch (Exception var22) {
            throw var22;
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
            } catch (Exception var20) {
                ;
            }

            try {
                if(httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception var19) {
                ;
            }

        }

        return var10;
    }

    public static Date parse2Date(String obj, String format) throws Exception {
        if(obj != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            try {
                return sdf.parse(String.valueOf(obj));
            } catch (Exception var4) {
                throw new Exception("Can\'t convert result value [" + obj + "]to date");
            }
        } else {
            return null;
        }
    }

    public static Date parse2Date(Object obj) throws Exception {
        new Date();
        if(obj != null) {
            Date temp;
            if(obj instanceof Date) {
                temp = (Date)obj;
            } else if(obj instanceof Date) {
                temp = new Date(((Date)obj).getTime());
            } else if(obj instanceof Timestamp) {
                temp = new Date(((Timestamp)obj).getTime());
            } else if(obj instanceof DATE) {
                temp = new Date(((DATE)obj).dateValue().getTime());
            } else if(obj instanceof TIMESTAMP) {
                temp = new Date(((TIMESTAMP)obj).dateValue().getTime());
            } else {
                try {
                    DateFormat e = DateFormat.getDateInstance();

                    try {
                        temp = e.parse(String.valueOf(obj));
                    } catch (Exception var9) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            temp = sdf.parse(String.valueOf(obj));
                        } catch (Exception var8) {
                            sdf = new SimpleDateFormat("yyyyMMdd");

                            try {
                                temp = sdf.parse(String.valueOf(obj));
                            } catch (Exception var7) {
                                throw var7;
                            }
                        }
                    }
                } catch (Exception var10) {
                    throw new Exception("Can\'t convert result value [" + obj + "]to date");
                }
            }

            return temp;
        } else {
            return null;
        }
    }

    public static List<?> trimList(List<?> list) {
        if(list == null) {
            return new ArrayList();
        } else {
            HashSet set = new HashSet();
            set.addAll(list);
            ArrayList list2 = new ArrayList();
            list2.addAll(set);
            return list2;
        }
    }

    public static String toXml(Document el, XmlShowType type) throws Exception {
        if(el == null) {
            return "";
        } else {
            StringWriter str = new StringWriter();
            OutputFormat s;
            XMLWriter writer;
            if(type == XmlShowType.Pretty) {
                s = OutputFormat.createPrettyPrint();
                s.setEncoding("UTF-8");
                writer = new XMLWriter(str, s);
                writer.write(el);
                writer.close();
            } else if(type == XmlShowType.Compact) {
                s = OutputFormat.createCompactFormat();
                s.setEncoding("UTF-8");
                writer = new XMLWriter(str, s);
                writer.write(el);
                writer.close();
            } else if(type == XmlShowType.Oneline) {
                String s1 = el.asXML();
                s1 = s1.replace("\n", "");
                return s1;
            }

            return str.toString();
        }
    }

    public static String toXml(Element el, XmlShowType type) throws Exception {
        if(el == null) {
            return "";
        } else {
            StringWriter str = new StringWriter();
            OutputFormat s;
            XMLWriter writer;
            if(type == XmlShowType.Pretty) {
                s = OutputFormat.createPrettyPrint();
                s.setEncoding("UTF-8");
                writer = new XMLWriter(str, s);
                writer.write(el);
                writer.close();
            } else {
                if(type != XmlShowType.Compact) {
                    if(type == XmlShowType.Oneline) {
                        String s1 = el.asXML();
                        s1 = s1.replace("\n", "");
                        return s1;
                    }

                    return el.asXML();
                }

                s = OutputFormat.createCompactFormat();
                s.setEncoding("UTF-8");
                writer = new XMLWriter(str, s);
                writer.write(el);
                writer.close();
            }

            return str.toString();
        }
    }

    public static byte[] convertObj2Btyes(Object obj) throws Exception {
        Object bytes = null;
        if(obj == null) {
            return (byte[])bytes;
        } else {
            ByteArrayOutputStream bo = null;
            ObjectOutputStream oo = null;

            byte[] bytes1;
            try {
                bo = new ByteArrayOutputStream();
                oo = new ObjectOutputStream(bo);
                oo.writeObject(obj);
                bytes1 = bo.toByteArray();
            } catch (Exception var15) {
                throw var15;
            } finally {
                if(bo != null) {
                    try {
                        bo.close();
                    } catch (IOException var14) {
                        ;
                    }
                }

                if(oo != null) {
                    try {
                        oo.close();
                    } catch (IOException var13) {
                        ;
                    }
                }

            }

            return bytes1;
        }
    }

    public static boolean contains(String compareStrings, Object target) {
        if(compareStrings != null && compareStrings.length() > 0) {
            String[] strs = compareStrings.split(",");
            return contains((Object[])strs, target);
        } else {
            return false;
        }
    }

    public static boolean isTrue(Object val) {
        return "true".equalsIgnoreCase(String.valueOf(val)) || "y".equalsIgnoreCase(String.valueOf(val)) || "yes".equalsIgnoreCase(String.valueOf(val)) || "ok".equalsIgnoreCase(String.valueOf(val)) || "on".equalsIgnoreCase(String.valueOf(val)) || "1".equals(String.valueOf(val)) || "checked".equals(String.valueOf(val));
    }

    public static boolean isFalse(Object val) {
        return val != null && val.toString().length() > 0 && !"false".equalsIgnoreCase(String.valueOf(val)) && !"N".equalsIgnoreCase(String.valueOf(val)) && !"off".equalsIgnoreCase(String.valueOf(val)) && !"no".equalsIgnoreCase(String.valueOf(val)) && !"not".equalsIgnoreCase(String.valueOf(val)) && !"0".equals(String.valueOf(val));
    }

    public static Object convertBytes2Obj(byte[] bytes) throws Exception {
        if(bytes == null) {
            return null;
        } else {
            Object obj = null;
            ByteArrayInputStream bi = null;
            ObjectInputStream oi = null;

            try {
                bi = new ByteArrayInputStream(bytes);
                oi = new ObjectInputStream(bi);
                obj = oi.readObject();
            } catch (Exception var8) {
                throw var8;
            } finally {
                if(bi != null) {
                    bi.close();
                }

                if(oi != null) {
                    oi.close();
                }

            }

            return obj;
        }
    }

    public static String getBase64(byte[] bytes) {
        return (new BASE64Encoder()).encode(bytes);
    }

    public static byte[] getFromBase64(String s) throws Exception {
        if(s == null) {
            return null;
        } else {
            BASE64Decoder decoder = new BASE64Decoder();
            return decoder.decodeBuffer(s);
        }
    }

    public static String getFileHtmlContent(File file) {
        return getFileContent(file, "<br/>");
    }

    public static String getFileContent(File file) {
        return getFileContent(file, "\r\n");
    }

    public static float getFloat(float data, int num) {
        if(num < 0) {
            num = 0;
        }

        BigDecimal b = new BigDecimal((double)data);
        float f1 = b.setScale(num, 4).floatValue();
        return f1;
    }

    public static SFile saveFile(File file, String pid, String userId, String sessionId) throws Exception {
        return saveFile(file, pid, userId, sessionId, true);
    }

    public static SFile saveFile(File file, String pid, String userId, String sessionId, boolean deleteOrgFile) throws Exception {
        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        String fileId = DBUtils.uuid();
        String ext = getExt(file.getName());
        String filename = file.getName();
        String bodyHashCode = getSha1((File)file);
        ext = ext.toLowerCase();
        String filepath = getFilePath(bodyHashCode);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        File file2 = new File(basePath + filepath + "/" + bodyHashCode + "." + ext);
        Logger.info("========saveFile: fileName->" + file2.getAbsolutePath());
        if(!file2.exists()) {
            FileUtils.copyFile(file, file2);
        }

        if(file2.exists()) {
            if(deleteOrgFile) {
                file.delete();
            }

            return saveFile(bodyHashCode, filename, "-1", userId, sessionId, 0, fileId);
        } else {
            throw new Exception("Copy File to System Filestore Failed");
        }
    }

    public static SFile saveFile(String hashcode, String filename, String pid, String userId, String sessionId, String fileId) throws Exception {
        return saveFile(hashcode, filename, pid, userId, sessionId, 0, fileId);
    }

    public static SFile saveFile(File file, String filename, String pid, String userId, String sessionId) throws Exception {
        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        String fileId = getFileNameWithoutExt(file.getName());
        String ext = getExt(file.getName());
        String bodyHashCode = getSha1((File)file);
        ext = ext.toLowerCase();
        String filepath = getFilePath(bodyHashCode);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        File file2 = new File(basePath + filepath + "/" + bodyHashCode + "." + ext);
        if(!file2.exists()) {
            FileUtils.copyFile(file, file2);
        }

        if(file2.exists()) {
            file.delete();
            return saveFile(bodyHashCode, filename, "-1", userId, sessionId, 0, fileId);
        } else {
            throw new Exception("Copy File to System Filestore Failed");
        }
    }

    public static SFile saveFile(String hashcode, String filename, String pid, String userId, String sessionId) throws Exception {
        return saveFile(hashcode, filename, pid, userId, sessionId, 0, DBUtils.uuid());
    }

    public static SFile saveFile(String hashcode, String filename, String pid, String userId, final String sessionId, int number, String fileId) throws Exception {
        final ArrayList fileIds = new ArrayList();
        String filepath = getFilePath(hashcode);
        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        String ext = getExt(filename);
        File file = new File(basePath + filepath + "/" + hashcode + "." + ext);
        if(file != null && file.exists()) {
            String width = Resources.getProperty("SMB");
            if(width != null && width.length() > 5) {
                SmbFile height = new SmbFile(width + "//" + filepath.replace("/", "//"));
                if(!height.exists()) {
                    height.mkdirs();
                }

                SmbFileOutputStream pic = null;
                BufferedInputStream fileSize = null;
                BufferedOutputStream out = null;

                try {
                    pic = new SmbFileOutputStream(width + "//" + filepath.replace("/", "//") + "//" + hashcode + "." + ext);
                    fileSize = new BufferedInputStream(new FileInputStream(file));
                    out = new BufferedOutputStream(pic);
                    byte[] fileName2 = new byte[1024];

                    int now;
                    while((now = fileSize.read(fileName2)) != -1) {
                        out.write(fileName2, 0, now);
                    }

                    out.flush();
                    out.flush();
                } catch (Exception var49) {
                    Logger.error(var49);
                } finally {
                    if(pic != null) {
                        pic.close();
                    }

                    if(fileSize != null) {
                        fileSize.close();
                    }

                    if(out != null) {
                        out.close();
                    }

                }
            }

            int var51 = 0;
            int var52 = 0;
            boolean var53 = isPicFile(file);
            if(var53) {
                BufferedImage var54 = ImageIO.read(file);
                var51 = (int)((double)var54.getWidth((ImageObserver)null));
                var52 = (int)((double)var54.getHeight((ImageObserver)null));
            }

            long var55 = file.length();
            Date var56 = new Date();
            Jedis jd = null;
            final SFile sf = new SFile();
            sf.setId(fileId);
            sf.setExt(ext);
            sf.setFileName(filename);
            sf.setRename(filename);
            sf.setFileSize(var55);
            sf.setFileStringSize(getFileStringSize(var55));
            sf.setFileType(SFileType.FILE);
            sf.setHashCode(hashcode);
            sf.setPic(var53);
            sf.setPid(pid);
            sf.setHashId(DBUtils.uuid());
            sf.setCreateTime(var56);
            sf.setWidth(var51);
            sf.setHeight(var52);
            sf.setUserId(userId);

            SFile var29;
            try {
                jd = RedisUtils.getConnection();
                int e = 0;
                String hasHashCode;
                if(userId != null && userId.length() >= 5) {
                    if(jd.hexists(userId + "__" + pid, filename).booleanValue()) {
                        hasHashCode = jd.hget(userId + "__" + pid, filename);

                        try {
                            e = Integer.parseInt(hasHashCode);
                            if(e < 0) {
                                e = 0;
                            }
                        } catch (Exception var45) {
                            ;
                        }
                    }

                    jd.hset(userId + "__" + pid, filename, String.valueOf(e + 1));
                } else {
                    if(jd.hexists(sessionId, filename).booleanValue()) {
                        hasHashCode = jd.hget(sessionId, filename);

                        try {
                            e = Integer.parseInt(hasHashCode);
                            if(e < 0) {
                                e = 0;
                            }
                        } catch (Exception var46) {
                            ;
                        }
                    }

                    jd.hset(sessionId, filename, String.valueOf(e + 1));
                    jd.expire(sessionId, 300);
                }

                if(e > 0) {
                    sf.setRename(sf.getFileNameWithoutExt() + "(" + (e + 1) + ")." + sf.getExt());
                }

                if(number > 0) {
                    byte[] var57 = (sessionId + "__fileIdList").getBytes();
                    if(jd.exists(var57).booleanValue()) {
                        byte[] lock = jd.get(var57);
                        Queue fe = (Queue)convertBytes2Obj(lock);
                        fe.offer(sf.getId());
                        if(fe.size() > number) {
                            for(int pf = 0; pf < fe.size() - number; ++pf) {
                                String id = (String)fe.poll();
                                if(id != null) {
                                    fileIds.add(id);
                                }
                            }
                        }

                        jd.set(var57, convertObj2Btyes(fe));
                    } else {
                        LinkedList var59 = new LinkedList();
                        var59.offer(sf.getId());
                        jd.set(var57, convertObj2Btyes(var59));
                    }
                }

                final boolean var58 = jd.hexists(SystemUtils.PROJECT_NAME, sf.getHashCode()).booleanValue();
                if(var58) {
                    String var60 = jd.hget(SystemUtils.PROJECT_NAME, sf.getHashCode());
                    SFile var62 = SFile.createFileByJson(var60);
                    if(!var62.exists()) {
                        File var63 = var62.getFile().getParentFile();
                        if(!var63.exists()) {
                            var63.mkdirs();
                        }

                        FileUtils.copyFile(file, var62.getFile());
                    }

                    sf.setHashId(var62.getHashId());
                } else {
                    jd.hset(SystemUtils.PROJECT_NAME, sf.getHashCode(), sf.toString());
                }

                jd.hset(SystemUtils.PROJECT_NAME, "fileId_" + sf.getId(), sf.getHashCode());
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##" + sf.getId(), "s");
                jd.hset("param_type", SystemUtils.PROJECT_NAME + "##" + sf.getHashCode(), "s");
                final Object var61 = new Object();
                execute.execute(new Runnable() {
                    public void run() {
                        DBM db = new DBM();
                        Connection conn = null;

                        try {
                            conn = db.getConnection();
                            conn.setAutoCommit(true);
                            EntityImpl e = new EntityImpl(conn);
                            String parsedDate;
                            if(!var58) {
                                parsedDate = "insert into sys_hash(id,hash_code,filename,file_size,extension)VALUES(\'" + sf.getHashId() + "\',\'" + sf.getHashCode() + "\',\'" + sf.getFileName() + "\'," + sf.getFileSize() + ",\'" + sf.getExt() + "\')";
                                e.executeUpdate(parsedDate);
                            }

                            if(fileIds.size() > 0) {
                                Iterator fileSql = fileIds.iterator();

                                while(fileSql.hasNext()) {
                                    parsedDate = (String)fileSql.next();

                                    try {
                                        e.executeUpdate("delete from sys_file where id=\'" + parsedDate + "\'");
                                    } catch (Exception var15) {
                                        ;
                                    }
                                }
                            }

                            parsedDate = "\'" + Utils.parseData(sf.getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "\'";
                            if(DBUtils.getRDBType() == DBType.Oracle) {
                                parsedDate = " TO_DATE(\'" + Utils.parseData(sf.getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "\',\'YYYY-MM-DD hh24:MI:SS\') ";
                            }

                            String fileSql1 = "insert into sys_file(id,pid,fk_user_id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + sf.getId() + "\', \'" + sf.getPid() + "\',\'" + sf.getUserId() + "\',\'" + sf.getFileName() + "\',\'" + sf.getRename() + "\', " + sf.getFileSize() + ", \'" + sf.getExt() + "\', \'" + sf.getHashCode() + "\', \'" + sessionId + "\', " + parsedDate + ", \'" + (sf.isPic()?"Y":"N") + "\', " + sf.getWidth() + ", " + sf.getHeight() + ")";
                            if(sf.getUserId() == null) {
                                fileSql1 = "insert into sys_file(id,pid,fk_user_id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + sf.getId() + "\', \'" + sf.getPid() + "\',null,\'" + sf.getFileName() + "\',\'" + sf.getRename() + "\', " + sf.getFileSize() + ", \'" + sf.getExt() + "\', \'" + sf.getHashCode() + "\', \'" + sessionId + "\'," + parsedDate + ", \'" + (sf.isPic()?"Y":"N") + "\', " + sf.getWidth() + ", " + sf.getHeight() + ")";
                            }

                            e.executeUpdate(fileSql1);

                            try {
                                Object var6 = var61;
                                synchronized(var61) {
                                    var61.notify();
                                }
                            } catch (Exception var14) {
                                ;
                            }
                        } catch (Exception var16) {
                            Logger.error(var16);
                        } finally {
                            db.freeConnection(conn);
                        }

                    }
                });
                if(fileIds.size() > 0) {
                    synchronized(var61) {
                        var61.wait(5000L);
                    }
                }

                var29 = sf;
            } catch (Exception var47) {
                throw var47;
            } finally {
                RedisUtils.freeConnection(jd);
            }

            return var29;
        } else {
            throw new Exception("文件为空，不能保存到系统,文件路径:" + file.getAbsolutePath());
        }
    }

    public static SFile saveContent2File(String content, String filename, String pid, String userId, String sessionId) throws Exception {
        if(content == null) {
            content = "";
        }

        String hashId = DBUtils.uuid();
        String ext = getExt(filename);
        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        String filepath = getFilePath(hashId);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        File file = new File(basePath + filepath + "/" + hashId + "." + ext);
        BufferedWriter bw = null;
        OutputStreamWriter writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            bw = new BufferedWriter(writer);
            bw.write(content);
            bw.flush();
        } catch (Exception var17) {
            throw var17;
        } finally {
            if(bw != null) {
                bw.close();
            }

        }

        return saveFile(file, pid, userId, sessionId);
    }

    public static void saveContent2File(File file, String content) throws Exception {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
        }

        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(content);
            bw.flush();
        } catch (Exception var7) {
            throw var7;
        } finally {
            if(bw != null) {
                bw.close();
                bw = null;
            }

        }

    }

    public static String getFileContent(File file, String lineFlag) {
        if(file.exists()) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;

            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                String line = null;

                while((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append(lineFlag);
                }

                String var6 = sb.toString();
                return var6;
            } catch (Exception var14) {
                return "";
            } finally {
                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException var13) {
                        ;
                    }
                }

            }
        } else {
            return "";
        }
    }

    public static SFile zip(String uuid, List<File> files, String zipFileName, String baseDir, Connection conn) throws Exception {
        if(baseDir == null || baseDir.length() <= 0) {
            baseDir = "";
        }

        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        String filepath = getFilePath(uuid);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        String[] strs = zipFileName.split("\\.");
        String ext = strs[strs.length - 1];
        ext = ext.toLowerCase();
        File file = new File(basePath + filepath + "/" + uuid + "." + ext);
        ZipOutputStream zos = null;

        try {
            zos = new ZipOutputStream(new FileOutputStream(file));
            int e = 0;

            for(int l = files.size(); e < l; ++e) {
                File inFile = (File)files.get(e);
                zipFile(inFile, zos, baseDir);
            }
        } catch (Exception var19) {
            throw var19;
        } finally {
            if(zos != null) {
                zos.close();
            }

        }

        return saveFile((String)uuid, zipFileName, "-1", (String)null, DBUtils.uuid());
    }

    public static List<SFile> unzip(File zipFile, Connection conn) throws Exception {
        String zipfilename = zipFile.getName();
        String ext2 = getExt(zipfilename);
        if(!ext2.equals("zip") && !ext2.equals("jar") && !ext2.equals("war")) {
            throw new Exception("不正确的压缩文件后缀名:" + ext2 + ",只支持：[zip,jar,war]");
        } else {
            ArrayList list = new ArrayList();
            InputStream inputStream = null;
            ZipFile zipFile2 = null;

            try {
                zipFile2 = new ZipFile(zipFile, "GB18030");
                String ioe = DBUtils.uuid();
                Enumeration entries = zipFile2.getEntries();

                while(entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    String zipFileName = new String(entry.getName());
                    if(entry.isDirectory()) {
                        SFile filename1 = new SFile();
                        filename1.setCreateTime(new Date());
                        filename1.setFileName(zipFileName);
                        filename1.setFileType(SFileType.FOLDER);
                        filename1.setRename(zipFileName);
                        list.add(filename1);
                    } else {
                        String filename = UUID.randomUUID().toString().replace("-", "");
                        String filepath = getFilePath(filename);
                        String basePath = Resources.getProperty("FileStore", getWebRootPath());
                        File f = new File(basePath + filepath);
                        if(!f.exists()) {
                            f.mkdirs();
                        }

                        String[] strs = zipFileName.split("\\.");
                        String ext = strs[strs.length - 1];
                        ext = ext.toLowerCase();
                        File file = new File(basePath + filepath + "/" + filename + "." + ext);
                        inputStream = zipFile2.getInputStream(entry);
                        FileOutputStream fileOut = null;

                        try {
                            fileOut = new FileOutputStream(file);
                            boolean fw = false;
                            byte[] buf = new byte[1024];

                            int fw1;
                            while((fw1 = inputStream.read(buf)) > 0) {
                                fileOut.write(buf, 0, fw1);
                            }
                        } catch (Exception var32) {
                            throw var32;
                        } finally {
                            if(fileOut != null) {
                                fileOut.close();
                                fileOut = null;
                            }

                        }

                        SFile fw2 = saveFile((String)filename, zipFileName, "-1", (String)null, ioe);
                        list.add(fw2);
                    }
                }

                ArrayList var23 = list;
                return var23;
            } catch (IOException var34) {
                throw var34;
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }

                if(zipFile2 != null) {
                    zipFile2.close();
                    zipFile2 = null;
                }

            }
        }
    }

    private static void zipFile(File inFile, ZipOutputStream zos, String baseDir) throws IOException {
        if(baseDir != null && !baseDir.endsWith("/")) {
            baseDir = baseDir + "/";
        } else if(baseDir != null && !baseDir.equals("/")) {
            if(!baseDir.endsWith("/")) {
                baseDir = baseDir + "/";
            }
        } else {
            baseDir = "";
        }

        File[] entryName;
        int e;
        if(inFile.isDirectory()) {
            entryName = inFile.listFiles();
            File[] BUFFER = entryName;
            e = entryName.length;

            for(int bis = 0; bis < e; ++bis) {
                File entry = BUFFER[bis];
                zipFile(entry, zos, baseDir + inFile.getName());
            }
        } else {
            entryName = null;
            String var14;
            if(baseDir.length() <= 0) {
                var14 = inFile.getName();
            } else {
                var14 = baseDir + inFile.getName();
            }

            java.util.zip.ZipEntry var15 = new java.util.zip.ZipEntry(var14);
            zos.putNextEntry(var15);
            BufferedInputStream var16 = null;

            try {
                var16 = new BufferedInputStream(new FileInputStream(inFile));
                short var17 = 1024;
                byte[] data = new byte[var17];

                while((e = var16.read(data, 0, var17)) != -1) {
                    zos.write(data, 0, e);
                }
            } catch (Exception var12) {
                throw var12;
            } finally {
                if(var16 != null) {
                    var16.close();
                    var16 = null;
                }

            }
        }

    }

    public static SFile downloadFile(String fileUrl, String filename) throws Exception {
        boolean byteread = false;
        URL url = null;
        URLConnection conn = null;
        InputStream inStream = null;
        FileOutputStream fs = null;
        String hashId = DBUtils.uuid();
        String ext = getExt(filename);
        String basePath = Resources.getProperty("FileStore", getWebRootPath());
        String filepath = getFilePath(hashId);
        File f = new File(basePath + filepath);
        if(!f.exists()) {
            f.mkdirs();
        }

        File file = new File(basePath + filepath + "/" + hashId + "." + ext);

        try {
            url = new URL(fileUrl);
            conn = url.openConnection();
            inStream = conn.getInputStream();
            fs = new FileOutputStream(file);
            byte[] e = new byte[2048];

            int byteread1;
            while((byteread1 = inStream.read(e)) != -1) {
                fs.write(e, 0, byteread1);
            }
        } catch (Exception var24) {
            throw var24;
        } finally {
            url = null;
            conn = null;
            if(inStream != null) {
                try {
                    inStream.close();
                } catch (Exception var23) {
                    ;
                }

                inStream = null;
            }

            if(fs != null) {
                try {
                    fs.close();
                } catch (Exception var22) {
                    ;
                }

                fs = null;
            }

        }

        return saveFile((File)file, filename, "-1", (String)null, hashId);
    }

    public static SFile getUploadFileInfo(String fileId) throws Exception {
        Jedis jd = null;

        SFile var7;
        try {
            jd = RedisUtils.getConnection();
            boolean hasHashCode = jd.hexists(SystemUtils.PROJECT_NAME, "fileId_" + fileId).booleanValue();
            if(!hasHashCode) {
                throw new Exception("系统没有找到对应的上传文件");
            }

            String hashcode = jd.hget(SystemUtils.PROJECT_NAME, "fileId_" + fileId);
            String filestr = jd.hget(SystemUtils.PROJECT_NAME, hashcode);
            SFile sf = SFile.createFileByJson(filestr);
            var7 = sf;
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var7;
    }

    public static SFile getUploadFileInfoByHashCode(String hashcode) throws Exception {
        Jedis jd = null;

        SFile var6;
        try {
            jd = RedisUtils.getConnection();
            boolean hasHashCode = jd.hexists(SystemUtils.PROJECT_NAME, hashcode).booleanValue();
            if(!hasHashCode) {
                throw new Exception("系统没有找到对应的上传文件");
            }

            String filestr = jd.hget(SystemUtils.PROJECT_NAME, hashcode);
            SFile sf = SFile.createFileByJson(filestr);
            var6 = sf;
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var6;
    }

    public static byte[] image2Bytes(String imagePath, String ext) throws Exception {
        BufferedImage bu = ImageIO.read(new File(imagePath));
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(bu, ext, imageStream);
        imageStream.flush();
        byte[] tagInfo = imageStream.toByteArray();
        return tagInfo;
    }

    public static boolean isPicFile(File file) {
        try {
            ImageInputStream e = ImageIO.createImageInputStream(file);
            Iterator iter = ImageIO.getImageReaders(e);
            if(!iter.hasNext()) {
                return false;
            } else {
                e.close();
                return true;
            }
        } catch (IOException var3) {
            return false;
        }
    }

    public static String getFileNameWithoutExt(String file_name) {
        String value = "";

        try {
            if(file_name != null && file_name.contains(".")) {
                int e = file_name.lastIndexOf(".");
                value = file_name.substring(0, e);
            }
        } catch (Exception var3) {
            Logger.warn(getErrorStack((Exception)var3));
        }

        return value;
    }

    public static String getExt(String fileName) {
        String extName = "";

        try {
            String[] temps = fileName.split("\\.");
            return temps[temps.length - 1];
        } catch (Exception var3) {
            return extName.toLowerCase();
        }
    }

    public static String getCNNumberStr(String original) {
        String integerPart = "";
        String floatPart = "";
        if(original.contains(".")) {
            int sb = original.indexOf(".");
            integerPart = original.substring(0, sb);
            floatPart = original.substring(sb + 1);
        } else {
            integerPart = original;
        }

        StringBuffer var6 = new StringBuffer();

        int i;
        int number;
        for(i = 0; i < integerPart.length(); ++i) {
            number = getNumber(integerPart.charAt(i));
            var6.append(cnNumbers[number]);
            var6.append(series[integerPart.length() - 1 - i]);
        }

        if(floatPart.length() > 0) {
            var6.append("点");

            for(i = 0; i < floatPart.length(); ++i) {
                number = getNumber(floatPart.charAt(i));
                var6.append(cnNumbers[number]);
            }
        }

        return var6.toString().trim();
    }

    private static int getNumber(char c) {
        String str = String.valueOf(c);
        return Integer.parseInt(str);
    }

    public static String getFilePathFromHashId(String id, String ext) {
        String filepath = getFilePath(id);
        return SFile.basePath + filepath + "/" + id + "." + ext;
    }

    public static String getFilePath(String uuid) {
        StringBuilder sb = new StringBuilder();
        sb.append("uploadfile/" + SystemUtils.PROJECT_NAME + "/");
        sb.append(uuid.substring(0, 2));
        sb.append("/");
        sb.append(uuid.substring(2, 4));
        sb.append("/");
        sb.append(uuid.substring(4, 6));
        sb.append("/");
        sb.append(uuid.substring(6, 8));
        sb.append("/");
        sb.append(uuid.substring(8, 10));
        sb.append("/");
        sb.append(uuid.substring(10));
        return sb.toString();
    }

    public static String getStringTime(long seconds) {
        if(seconds < 1L) {
            return "--:--:--";
        } else {
            long days = seconds / 60L / 60L / 24L;
            long rest = seconds - days * 60L * 60L * 24L;
            long hours = rest / 60L / 60L;
            rest -= hours * 60L * 60L;
            long minutes = rest / 60L;
            long sec = rest - minutes * 60L;
            if(days > 0L) {
                hours += days * 24L;
            }

            StringBuilder sb = new StringBuilder();
            if(hours < 10L) {
                sb.append("0");
            }

            sb.append(hours);
            sb.append(":");
            if(minutes < 10L) {
                sb.append("0");
            }

            sb.append(minutes);
            sb.append(":");
            if(sec < 10L) {
                sb.append("0");
            }

            sb.append(sec);
            return sb.toString();
        }
    }

    public static String getFileStringSize(long size) {
        float k = 1024.0F;
        float m = k * k;
        float g = m * k;
        float t = g * k;
        float fsize = (float)size;
        return fsize >= t?new String(df.format((double)(fsize / t)) + " TB"):(fsize >= g?new String(df.format((double)(fsize / g)) + " GB"):(fsize >= m?new String(df.format((double)(fsize / m)) + " MB"):(fsize >= k?new String(df.format((double)(fsize / k)) + " KB"):new String(size + " B"))));
    }

    public static String getErrorStack(Throwable e) {
        String error = "";
        if(e != null) {
            ByteArrayOutputStream baos = null;
            PrintStream ps = null;

            String var6;
            try {
                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                e.printStackTrace(ps);
                var6 = baos.toString();
            } catch (Exception var14) {
                error = e.toString();
                return error;
            } finally {
                try {
                    if(baos != null) {
                        baos.close();
                        baos = null;
                    }
                } catch (IOException var13) {
                    ;
                }

                if(ps != null) {
                    ps.close();
                    ps = null;
                }

            }

            return var6;
        } else {
            return error;
        }
    }

    public static String getErrorStack(Exception e) {
        String error = null;
        if(e != null) {
            ByteArrayOutputStream baos = null;
            PrintStream ps = null;

            try {
                baos = new ByteArrayOutputStream();
                ps = new PrintStream(baos);
                e.printStackTrace(ps);
                error = baos.toString();
            } catch (Exception var13) {
                error = e.toString();
            } finally {
                if(baos != null) {
                    try {
                        baos.close();
                    } catch (IOException var12) {
                        ;
                    }

                    baos = null;
                }

                if(ps != null) {
                    ps.close();
                }

                ps = null;
            }
        }

        return error;
    }

    public static String parseData(Date date, String format) {
        try {
            SimpleDateFormat e = new SimpleDateFormat(format);
            return e.format(date);
        } catch (Exception var3) {
            return "0000-00-00";
        }
    }

    public static String getSha1(String message) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] b = md.digest(message.getBytes("utf-8"));
        StringBuffer StrBuff = new StringBuffer();

        for(int i = 0; i < b.length; ++i) {
            if(Integer.toHexString(255 & b[i]).length() == 1) {
                StrBuff.append("0").append(Integer.toHexString(255 & b[i]));
            } else {
                StrBuff.append(Integer.toHexString(255 & b[i]));
            }
        }

        return StrBuff.toString();
    }

    public static String getMD4(String message) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = MessageDigest.getInstance("md4");
        byte[] b = md.digest(message.getBytes());
        return new String(Hex.encode(b));
    }

    public static String getMD4(File file) throws Exception {
        FileInputStream fis = null;

        try {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest ex = MessageDigest.getInstance("md4");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            boolean length = true;

            int length1;
            while((length1 = fis.read(buffer)) != -1) {
                ex.update(buffer, 0, length1);
            }

            byte[] b = ex.digest();
            String var7 = new String(Hex.encode(b));
            return var7;
        } catch (Exception var14) {
            throw var14;
        } finally {
            try {
                fis.close();
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }
    }

    public static String getSha1(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            byte[] e = new byte[2048];
            boolean length = true;

            int length1;
            while((length1 = fis.read(e)) != -1) {
                md.update(e, 0, length1);
            }

            byte[] b = md.digest();
            String var7 = byteToHexStringSingle(b);
            return var7;
        } catch (Exception var10) {
            throw var10;
        } finally {
            if(fis != null) {
                fis.close();
            }

        }
    }

    private static String byteToHexStringSingle(byte[] byteArray) {
        StringBuffer md5StrBuff = new StringBuffer();

        for(int i = 0; i < byteArray.length; ++i) {
            if(Integer.toHexString(255 & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
            }
        }

        return md5StrBuff.toString();
    }

    public static String getMd5(String temp) throws Exception {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(temp.getBytes());
        byte[] tmp = md.digest();
        char[] str = new char[32];
        int k = 0;

        for(int i = 0; i < 16; ++i) {
            byte byte0 = tmp[i];
            str[k++] = hexDigits[byte0 >>> 4 & 15];
            str[k++] = hexDigits[byte0 & 15];
        }

        return new String(str);
    }

    public static String getMapStringValue(Map<String, ?> data, String key, String defaultValue) {
        if(data != null) {
            Object o = data.get(key);
            if(o == null) {
                return defaultValue;
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                return o instanceof Float?df.format(Float.valueOf(o.toString())):(o instanceof Double?df.format(Double.valueOf(o.toString())):o.toString());
            }
        } else {
            return "";
        }
    }

    public static List<Object> getMapListValue(Map<String, ?> data, String key) {
        if(data != null) {
            Object o = data.get(key);
            if(o != null) {
                if(o instanceof List) {
                    return (List)o;
                }

                if(o instanceof JSONArray) {
                    ArrayList li = new ArrayList();
                    JSONArray as = (JSONArray)o;

                    for(int i = 0; i < as.length(); ++i) {
                        li.add(as.get(i));
                    }

                    return li;
                }
            }
        }

        return new ArrayList();
    }

    public static String getMapStringValue(Map<String, ?> data, String key) {
        if(data != null) {
            Object o = data.get(key);
            if(o == null) {
                return "";
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                return o instanceof Float?df.format(Float.valueOf(o.toString())):(o instanceof Double?df.format(Double.valueOf(o.toString())):o.toString());
            }
        } else {
            return "";
        }
    }

    public static String getMapFormatStringValue(Map<String, ?> data, String key, String format) {
        if(data != null) {
            Object o = data.get(key);
            if(o == null) {
                return "";
            } else {
                try {
                    Date e = getMapDateValue(data, key);
                    return parseData(e, format);
                } catch (Exception var5) {
                    return "";
                }
            }
        } else {
            return "";
        }
    }

    public static int getMapIntegerValue(Map<String, ?> data, String key) {
        if(data != null) {
            Object o = data.get(key);
            if(o == null) {
                return 0;
            } else {
                try {
                    Float e = Float.valueOf(Float.parseFloat(o.toString().trim()));
                    return e.intValue();
                } catch (Exception var4) {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    public static Date getMapDateValue(Map<String, ?> map, String key) {
        new Date(System.currentTimeMillis());
        if(map == null) {
            return null;
        } else {
            Object obj = map.get(key);
            if(obj != null) {
                Date temp;
                if(obj instanceof Date) {
                    temp = (Date)obj;
                } else if(obj instanceof Date) {
                    temp = new Date(((Date)obj).getTime());
                } else if(obj instanceof Timestamp) {
                    temp = new Date(((Timestamp)obj).getTime());
                } else if(obj instanceof DATE) {
                    temp = new Date(((DATE)obj).dateValue().getTime());
                } else if(obj instanceof TIMESTAMP) {
                    try {
                        temp = new Date(((TIMESTAMP)obj).dateValue().getTime());
                    } catch (Exception var10) {
                        return null;
                    }
                } else {
                    try {
                        SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            temp = e.parse(String.valueOf(obj));
                        } catch (Exception var8) {
                            e = new SimpleDateFormat("yyyyMMdd");

                            try {
                                temp = e.parse(String.valueOf(obj));
                            } catch (Exception var7) {
                                throw var7;
                            }
                        }
                    } catch (Exception var9) {
                        return null;
                    }
                }

                return temp;
            } else {
                return null;
            }
        }
    }

    public static float getMapFloatValue(Map<String, ?> data, String key) {
        if(data != null) {
            Object o = data.get(key);
            if(o == null) {
                return 0.0F;
            } else {
                try {
                    float e = Float.parseFloat(o.toString().trim());
                    return e;
                } catch (Exception var4) {
                    return 0.0F;
                }
            }
        } else {
            return 0.0F;
        }
    }

    public static Long getMapLongValue(Map<String, Object> data, String name) {
        if(data != null) {
            Object o = data.get(name);
            if(o == null) {
                return Long.valueOf(0L);
            } else {
                try {
                    Double e = Double.valueOf(Double.parseDouble(o.toString().trim()));
                    return Long.valueOf(e.longValue());
                } catch (Exception var4) {
                    return Long.valueOf(0L);
                }
            }
        } else {
            return Long.valueOf(0L);
        }
    }

    public static Object getMapValue(Map<String, ?> data, String key) {
        return data != null?data.get(key):null;
    }

    public static boolean getMapBooleanValue(Map<String, ?> data, String key) {
        boolean ok = false;
        if(data != null) {
            Object o = data.get(key);
            if(!(o instanceof Boolean)) {
                return isTrue(o);
            }

            ok = ((Boolean)o).booleanValue();
        }

        return ok;
    }

    public static String getWebRootPath2() {
        String path = Resources.getProperty("workspace");
        return path != null && path.length() > 0?path:getWebRootPath();
    }

    public static String getWebRootPath() {
        String path = getRootClassPath();

        try {
            int index = path.lastIndexOf("/");
            path = path.substring(0, index);
            index = path.lastIndexOf("/");
            path = path.substring(0, index);
            index = path.lastIndexOf("/");
            path = path.substring(0, index);
            path = path + "/";
        } catch (Exception var2) {
            ;
        }

        return path;
    }

    public static String getRootClassPath() {
        if(basePath == null) {
            String classpath = "";

            try {
                URL paths = Utils.class.getClass().getResource("/application.properties");
                if(paths != null) {
                    classpath = paths.toURI().toString();
                } else {
                    classpath = Utils.class.getClassLoader().getResource("").toURI().toString();
                }

                classpath = classpath.replace("\\", "/");
            } catch (URISyntaxException var4) {
                ;
            }

            if(classpath.endsWith(".jar!/application.properties")) {
                int paths1 = classpath.lastIndexOf("/");
                classpath = classpath.substring(0, paths1);
                paths1 = classpath.lastIndexOf("/");
                classpath = classpath.substring(0, paths1);
            } else if(classpath.endsWith("application.properties")) {
                classpath = classpath.substring(0, classpath.length() - 22);
            }

            String[] paths2 = classpath.split("file:/");
            classpath = paths2[paths2.length - 1];
            if(!classpath.contains(":")) {
                classpath = "/" + classpath;
            }

            try {
                basePath = (new File(classpath)).getAbsolutePath();
            } catch (Exception var3) {
                ;
            }

            if(!basePath.endsWith("/")) {
                basePath = basePath + "/";
            }
        }

        basePath = basePath.replace("\\", "/");
        return basePath;
    }

    public static boolean contains(Object[] objs, Object obj) {
        if(objs != null && objs.length > 0) {
            int i = 0;

            for(int l = objs.length; i < l; ++i) {
                Object temp = objs[i];
                if(temp != null && temp.equals(obj)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean contains(Collection<?> objs, Object obj) {
        if(objs != null && objs.size() > 0) {
            Iterator var3 = objs.iterator();

            Object temp;
            do {
                if(!var3.hasNext()) {
                    return false;
                }

                temp = var3.next();
            } while(temp == null || !temp.equals(obj));

            return true;
        } else {
            return false;
        }
    }

    public static String getListString(String str, int size) {
        return getListString(str, ",", size);
    }

    public static String getListString(String str, String flat, int size) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < size; ++i) {
            sb.append(str);
            sb.append(flat);
        }

        return sb.substring(0, sb.length() - flat.length());
    }

    public static String getListString(Object[] objs) {
        return getListString((Object[])objs, ",");
    }

    public static String getListString(Object[] objs, String flat) {
        if(objs != null && objs.length > 0) {
            if(flat == null || flat.length() <= 0) {
                flat = ",";
            }

            StringBuilder sb = new StringBuilder();
            int i = 0;

            for(int l = objs.length; i < l; ++i) {
                sb.append(objs[i]);
                sb.append(flat);
            }

            return sb.substring(0, sb.length() - flat.length());
        } else {
            return "";
        }
    }

    public static String getListString(Collection<?> objs) {
        return getListString((Collection)objs, ",");
    }

    public static String getListString(Collection<?> objs, String flat) {
        if(objs != null && objs.size() > 0) {
            if(flat == null || flat.length() <= 0) {
                flat = ",";
            }

            StringBuilder sb = new StringBuilder();
            Iterator iterator = objs.iterator();

            while(iterator.hasNext()) {
                Object obj = iterator.next();
                sb.append(obj);
                sb.append(flat);
            }

            return sb.substring(0, sb.length() - flat.length());
        } else {
            return "";
        }
    }

    public static JSONArray parseList2Json(List<Map<String, ?>> list) throws JSONException {
        JSONArray array = new JSONArray();
        if(list != null) {
            for(int i = 0; i < list.size(); ++i) {
                Map map = (Map)list.get(i);
                JSONObject object = parseMap2Json(map);
                array.put(object);
            }
        }

        return array;
    }

    public static JSONObject parseMap2Json(Map<String, ?> map) throws JSONException {
        JSONObject object = new JSONObject();

        Object key;
        Object v;
        for(Iterator var3 = map.keySet().iterator(); var3.hasNext(); object.put((String)key, v)) {
            key = var3.next();
            v = map.get(key);
            if(v == null) {
                v = "";
            }
        }

        return object;
    }

    public static String leftPadding(Object str, int length, String charVal) throws Exception {
        StringBuilder temp = new StringBuilder();
        if(str != null) {
            temp.append(str);
        }

        if(charVal != null && charVal.length() > 0) {
            if(charVal.length() > 1) {
                throw new Exception("charVal 长度不对，填充字符应该只有且只有一个字符长度");
            }
        } else {
            charVal = " ";
        }

        if(temp.length() < length) {
            while(temp.length() < length) {
                temp.insert(0, charVal);
            }
        }

        return temp.toString();
    }

    public static String rightPadding(Object str, int length, String charVal) throws Exception {
        String temp = "";
        if(str != null) {
            temp = String.valueOf(str);
        }

        if(charVal != null && charVal.length() > 0) {
            if(charVal.length() > 1) {
                throw new Exception("charVal 长度不对，填充字符应该只有且只有一个字符长度");
            }
        } else {
            charVal = " ";
        }

        if(temp.length() < length) {
            while(temp.length() < length) {
                temp = temp + charVal;
            }
        }

        return temp;
    }

    public static long getSysParamLongValue(final String name, final long defaultValue, final long adjust) throws Exception {
        final String value = "" + defaultValue;
        Jedis jd = null;

        long var14;
        try {
            jd = RedisUtils.getConnection();
            final boolean exsit = jd.hexists(SystemUtils.PROJECT_NAME, name).booleanValue();
            if(exsit) {
                value = jd.hget(SystemUtils.PROJECT_NAME, name);
            }

            execute.execute(new Runnable() {
                public void run() {
                    long xx = 0L;

                    try {
                        xx = Long.parseLong(value);
                    } catch (Exception var13) {
                        ;
                    }

                    DBM db = new DBM();
                    Connection conn = null;

                    try {
                        conn = db.getConnection();
                        conn.setAutoCommit(true);
                        EntityImpl e;
                        if(exsit) {
                            e = new EntityImpl(conn);
                            e.executeQuery("select count(*) num from sys_param_info a where a.param_name=\'" + name + "\'");
                            if(e.getIntegerValue("num").intValue() > 0) {
                                e.executeUpdate("update sys_param_info set param_value=\'" + (xx + adjust) + "\' where param_name=\'" + name + "\'");
                            } else {
                                EntityImpl sys_param_info2 = new EntityImpl("sys_param_info", conn);
                                sys_param_info2.setValue("param_name", name);
                                sys_param_info2.setValue("param_value", Long.valueOf(xx + adjust));
                                sys_param_info2.setValue("param_type", "01");
                                sys_param_info2.create();
                            }
                        } else {
                            e = new EntityImpl("sys_param_info", conn);
                            e.setValue("param_name", name);
                            e.setValue("param_value", Long.valueOf(defaultValue + adjust));
                            e.setValue("param_type", "01");
                            e.create();
                        }
                    } catch (Exception var11) {
                        Logger.error(var11);
                    } finally {
                        db.freeConnection(conn);
                    }

                }
            });
            long tempXX = 0L;

            try {
                tempXX = Long.parseLong(value);
            } catch (Exception var18) {
                ;
            }

            long xx = tempXX + adjust;
            jd.hset(SystemUtils.PROJECT_NAME, name, String.valueOf(xx));
            var14 = xx;
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var14;
    }

    public static String getSysParamValue(final String name, final String defaultValue) throws Exception {
        Jedis jd = null;

        String var5;
        try {
            if(defaultValue == null) {
                throw new Exception("defaultValue不能为空");
            }

            jd = RedisUtils.getConnection();
            if(!jd.hexists(SystemUtils.PROJECT_NAME, name).booleanValue()) {
                jd.hset(SystemUtils.PROJECT_NAME, name, defaultValue);
                execute.execute(new Runnable() {
                    public void run() {
                        DBM db = new DBM();
                        Connection conn = null;

                        try {
                            conn = db.getConnection();
                            conn.setAutoCommit(true);
                            SFile e = null;
                            if(defaultValue.length() > 1000) {
                                e = Utils.saveContent2File(defaultValue, DBUtils.uuid() + ".txt", "-1", "sys", DBUtils.uuid());
                            }

                            EntityImpl sys_param_info = new EntityImpl("sys_param_info", conn);
                            sys_param_info.setValue("param_name", name);
                            if(e != null) {
                                sys_param_info.setValue("param_type", "02");
                                sys_param_info.setValue("param_value", e.getId());
                            } else {
                                sys_param_info.setValue("param_type", "01");
                                sys_param_info.setValue("param_value", defaultValue);
                            }

                            sys_param_info.create();
                        } catch (Exception var8) {
                            Logger.error(var8);
                        } finally {
                            db.freeConnection(conn);
                        }

                    }
                });
                var5 = defaultValue;
                return var5;
            }

            var5 = jd.hget(SystemUtils.PROJECT_NAME, name);
        } catch (Exception var8) {
            throw var8;
        } finally {
            RedisUtils.freeConnection(jd);
        }

        return var5;
    }

    public static void setSysParamValue(final String name, final String value) throws Exception {
        Jedis jd = null;

        try {
            if(value == null) {
                throw new Exception("value不能为空");
            }

            jd = RedisUtils.getConnection();
            final boolean e = jd.hexists(SystemUtils.PROJECT_NAME, name).booleanValue();
            execute.execute(new Runnable() {
                public void run() {
                    DBM db = new DBM();
                    Connection conn = null;

                    try {
                        conn = db.getConnection();
                        conn.setAutoCommit(true);
                        SFile ex = null;
                        if(value.length() > 1000) {
                            ex = Utils.saveContent2File(value, DBUtils.uuid() + ".txt", "-1", "sys", DBUtils.uuid());
                        }

                        EntityImpl sys_param_info;
                        if(e) {
                            sys_param_info = new EntityImpl(conn);
                            int size = sys_param_info.executeQuery("select id from sys_param_info a where a.param_name=\'" + name + "\'");
                            EntityImpl sys_param_info2;
                            if(size > 0) {
                                sys_param_info2 = new EntityImpl("sys_param_info", conn);
                                sys_param_info2.setValue("id", sys_param_info.getStringValue("id"));
                                if(ex != null) {
                                    sys_param_info2.setValue("param_type", "02");
                                    sys_param_info2.setValue("param_value", ex.getId());
                                } else {
                                    sys_param_info2.setValue("param_type", "01");
                                    sys_param_info2.setValue("param_value", value);
                                }

                                sys_param_info2.update();
                            } else {
                                sys_param_info2 = new EntityImpl("sys_param_info", conn);
                                sys_param_info2.setValue("param_name", name);
                                if(ex != null) {
                                    sys_param_info2.setValue("param_type", "02");
                                    sys_param_info2.setValue("param_value", ex.getId());
                                } else {
                                    sys_param_info2.setValue("param_type", "01");
                                    sys_param_info2.setValue("param_value", value);
                                }

                                sys_param_info2.create();
                            }
                        } else {
                            sys_param_info = new EntityImpl("sys_param_info", conn);
                            sys_param_info.setValue("param_name", name);
                            if(ex != null) {
                                sys_param_info.setValue("param_type", "02");
                                sys_param_info.setValue("param_value", ex.getId());
                            } else {
                                sys_param_info.setValue("param_type", "01");
                                sys_param_info.setValue("param_value", value);
                            }

                            sys_param_info.create();
                        }
                    } catch (Exception var10) {
                        Logger.error(var10);
                    } finally {
                        db.freeConnection(conn);
                    }

                }
            });
            jd.hset(SystemUtils.PROJECT_NAME, name, value);
        } catch (Exception var7) {
            throw var7;
        } finally {
            RedisUtils.freeConnection(jd);
        }

    }

    public static Template getTemplate(String path) throws Exception {
        freemarkerCfg.setDefaultEncoding("UTF-8");
        File f = new File(path);
        if(f.exists()) {
            freemarkerCfg.setDirectoryForTemplateLoading(f.getParentFile());
            return freemarkerCfg.getTemplate(f.getName());
        } else {
            throw new Exception("找对应的freemarker模板文件【" + path + "】");
        }
    }

    public static String getFreeMarkerString(Template tpl, Object data) throws Exception {
        String requestXML = null;
        StringWriter writer = null;

        String var6;
        try {
            writer = new StringWriter();
            tpl.process(data, writer);
            writer.flush();
            requestXML = writer.getBuffer().toString();
            var6 = requestXML;
        } catch (Exception var9) {
            throw var9;
        } finally {
            if(writer != null) {
                writer.close();
            }

        }

        return var6;
    }

    public static void trimPic(File input, File output) throws Exception {
        FileOutputStream out = null;

        try {
            BufferedImage ex = ImageIO.read(input);
            if(ex.getWidth((ImageObserver)null) == -1) {
                throw new Exception("图片没有准备好:" + input.getAbsolutePath());
            }

            int newWidth = (int)((double)ex.getWidth((ImageObserver)null));
            int newHeight = (int)((double)ex.getHeight((ImageObserver)null));
            if(newWidth > 2000 || newHeight > 2000) {
                double tag = 0.1D;
                if(newWidth > newHeight) {
                    tag = (double)newWidth / 2000.0D;
                } else {
                    tag = (double)newHeight / 2000.0D;
                }

                newWidth = (int)((double)newWidth / tag);
                newHeight = (int)((double)newHeight / tag);
            }

            BufferedImage tag1 = new BufferedImage(newWidth, newHeight, 1);
            tag1.getGraphics().drawImage(ex.getScaledInstance(newWidth, newHeight, 4), 0, 0, (ImageObserver)null);
            out = new FileOutputStream(output);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag1);
        } catch (IOException var11) {
            throw var11;
        } finally {
            if(out != null) {
                out.close();
            }

        }

    }

    public static void trimPic(File input, File output, int width, int height) throws Exception {
        FileOutputStream out = null;

        try {
            BufferedImage ex = ImageIO.read(input);
            if(ex.getWidth((ImageObserver)null) == -1) {
                throw new Exception("图片没有准备好:" + input.getAbsolutePath());
            }

            int newWidth = (int)((double)ex.getWidth((ImageObserver)null));
            int newHeight = (int)((double)ex.getHeight((ImageObserver)null));
            double tag;
            if(newWidth > width) {
                tag = 0.1D;
                tag = (double)newWidth / (double)width;
                newWidth = (int)((double)newWidth / tag);
            }

            if(newHeight > height) {
                tag = 0.1D;
                tag = (double)newHeight / (double)height;
                newHeight = (int)((double)newHeight / tag);
            }

            BufferedImage tag1 = new BufferedImage(newWidth, newHeight, 1);
            tag1.getGraphics().drawImage(ex.getScaledInstance(newWidth, newHeight, 4), 0, 0, (ImageObserver)null);
            out = new FileOutputStream(output);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(tag1);
        } catch (IOException var13) {
            throw var13;
        } finally {
            if(out != null) {
                out.close();
            }

        }

    }

    public static String encodeStr(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static boolean isGoodStr(String str) {
        return Pattern.matches(goodStrRegEx, str);
    }

    public static String getPinYinHeadChar(String str) {
        String convert = "";

        for(int j = 0; j < str.length(); ++j) {
            char word = str.charAt(j);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if(pinyinArray != null) {
                convert = convert + pinyinArray[0].charAt(0);
            } else {
                convert = convert + word;
            }
        }

        return convert;
    }
}
