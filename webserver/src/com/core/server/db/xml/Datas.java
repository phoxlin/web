package com.core.server.db.xml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Datas {
    public static Map<String, File> dataFiles = new HashMap();
    private String tablename;
    private File file;
    private List<Data> list = new ArrayList();

    public Datas() {
    }

    public static void main(String[] args) throws Exception {
        HashMap data = new HashMap();
        data.put("id", DBUtils.oid());
        data.put("name", "abc");
        insert("test", data);
    }

    public static void insert(String tablename, Map<String, String> data) throws Exception {
        String[] names = tablename.split("-");
        String name = names[0];
        String note = names[names.length - 1].toUpperCase();
        File file = (File)dataFiles.get(tablename);
        File writer;
        if(file == null) {
            writer = new File(NettyUtils.getRootContent() + "/configures/database/xml");
            file = MsInfo.getFile(writer, name, "xml");
            if(file != null) {
                dataFiles.put(name, file);
            }
        }

        XMLWriter writer1 = null;
        Document root = null;
        Element datas = null;

        try {
            if(file == null) {
                file = new File(NettyUtils.getWebContent() + "/WEB-INF/configures/database/xml/" + name + "-" + note + ".xml");
                root = DocumentHelper.createDocument();
                datas = root.addElement("datas");
            } else {
                SAXReader e = new SAXReader();
                root = e.read(file);
                datas = root.getRootElement();
            }

            Element e1 = DocumentHelper.createElement("data");
            Iterator var11 = data.entrySet().iterator();

            while(var11.hasNext()) {
                Map.Entry format = (Map.Entry)var11.next();
                e1.addAttribute(((String)format.getKey()).toLowerCase(), (String)format.getValue());
            }

            datas.add(e1);
            OutputFormat format1 = OutputFormat.createPrettyPrint();
            format1.setEncoding("UTF-8");
            writer1 = new XMLWriter(new FileOutputStream(file), format1);
            writer1.write(root);
        } catch (Exception var15) {
            throw new Exception("insert XML Data into document【" + file.getAbsolutePath() + "】错误：" + Utils.getErrorStack(var15));
        } finally {
            if(writer1 != null) {
                writer1.close();
                writer = null;
            }

        }
    }

    public static Data findMax(String tablename, String xpath, String max) throws Exception {
        Datas li = find(tablename, xpath);
        if(li != null && li.list.size() > 0) {
            Data d = null;
            int m = 0;

            for(int i = 0; i < li.list.size(); ++i) {
                Data data = (Data)li.list.get(i);
                int md = data.getIntegerValue(max).intValue();
                if(md > m || i == 0) {
                    m = md;
                    d = data;
                }
            }

            return d;
        } else {
            return null;
        }
    }

    public static Data findMin(String tablename, String xpath, String min) throws Exception {
        Datas li = find(tablename, xpath);
        if(li != null && li.list.size() > 0) {
            Data d = null;
            int m = 0;

            for(int i = 0; i < li.list.size(); ++i) {
                Data data = (Data)li.list.get(i);
                int md = data.getIntegerValue(min).intValue();
                if(md < m || i == 0) {
                    m = md;
                    d = data;
                }
            }

            return d;
        } else {
            return null;
        }
    }

    public static Data findOne(String tablename, String key, String comp, String val) throws Exception {
        return findOne(tablename, "@" + key + comp + "\'" + val + "\'");
    }

    public static Data findOne(String tablename, String xpath) throws Exception {
        File file = (File)dataFiles.get(tablename);
        if(file == null) {
            File read = new File(NettyUtils.getRootContent() + "/configures/database/xml");
            file = MsInfo.getFile(read, tablename, "xml");
            if(file != null) {
                dataFiles.put(tablename, file);
            }
        }

        if(file == null) {
            throw new Exception("没有找到相关的xml数据信息文件");
        } else {
            SAXReader read1 = new SAXReader();
            Document doc = read1.read(file);
            Element data = (Element)doc.selectSingleNode("/datas/data[" + xpath + "]");
            return data != null?new Data(tablename, data):null;
        }
    }

    public static Datas find(String tablename, String xpath) throws Exception {
        File file = (File)dataFiles.get(tablename);
        if(file == null) {
            File read = new File(NettyUtils.getRootContent() + "/configures/database/xml");
            file = MsInfo.getFile(read, tablename, "xml");
            if(file != null) {
                dataFiles.put(tablename, file);
            }
        }

        if(file == null) {
            throw new Exception("没有找到相关的xml数据信息文件");
        } else {
            SAXReader read1 = new SAXReader();
            Document doc = read1.read(file);
            List datas = doc.selectNodes("/datas/data[" + xpath + "]");
            if(datas == null) {
                return null;
            } else {
                Datas li = new Datas();
                li.setTablename(tablename);
                Iterator var8 = datas.iterator();

                while(var8.hasNext()) {
                    Element e = (Element)var8.next();
                    li.addData(new Data(tablename, e));
                }

                return li;
            }
        }
    }

    public static Datas find(String tablename, String key, String comp, String val) throws Exception {
        return find(tablename, "@" + key + comp + "\'" + val + "\'");
    }

    public String getStringValue(String param) throws Exception {
        return this.getStringValue(param, 0);
    }

    public String getStringValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getStringValue(param);
        }
    }

    public Long getLongValue(String param) throws Exception {
        return this.getLongValue(param, 0);
    }

    public Long getLongValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getLongValue(param);
        }
    }

    public int getIntegerValue(String param) throws Exception {
        return this.getIntegerValue(param, 0);
    }

    public int getIntegerValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getIntegerValue(param).intValue();
        }
    }

    public boolean getBooleanValue(String param) throws Exception {
        return this.getBooleanValue(param, 0);
    }

    public boolean getBooleanValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getBooleanValue(param);
        }
    }

    public Float getFloatValue(String param) throws Exception {
        return this.getFloatValue(param, 0);
    }

    public Float getFloatValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getFloatValue(param);
        }
    }

    public Double getDoubleValue(String param) throws Exception {
        return this.getDoubleValue(param, 0);
    }

    public Double getDoubleValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getDoubleValue(param);
        }
    }

    public java.sql.Date getDateValue(String param) throws Exception {
        return this.getDateValue(param, 0);
    }

    public java.sql.Date getDateValue(String param, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getDateValue(param);
        }
    }

    public String getFormatStringValue(String param, String format) throws Exception {
        return this.getFormatStringValue(param, format, 0);
    }

    public String getFormatStringValue(String param, String format, int i) throws Exception {
        if(i >= this.list.size()) {
            throw new Exception("Get Xml Data[" + this.tablename + "] result value error:invalid result row num:" + i + ",data total result row num is :" + this.list.size());
        } else {
            return ((Data)this.list.get(i)).getFormatStringValue(param, format);
        }
    }

    public void addData(Data data) {
        this.list.add(data);
    }

    public String getTablename() {
        return this.tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Data> getList() {
        return this.list;
    }
}
