package com.core.server.c;

import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.ms.MsInfo;
import com.core.server.tools.NettyUtils;
import com.core.server.tools.Utils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.sql.Connection;
import java.util.*;

public class Codes {
    private static Map<String, CType> codeTypes = new HashMap();
    private static List<Code> codes = new ArrayList();

    static {
        try {
            init();
        } catch (Exception var1) {
            Logger.error(var1);
        }

    }

    public Codes() {
    }

    public static void main(String[] args) {
        Logger.info(note("DA_001", "N"));
        Logger.info(code("DA_001", "是"));
    }

    public static Code code(String type) throws Exception {
        int i = 0;

        for(int l = codes.size(); i < l; ++i) {
            Code c = (Code)codes.get(i);
            if(c.getType().equals(type)) {
                return c;
            }
        }

        throw new Exception("系统找不到对应的码表【" + type + "】,如果你确定系统有,请试着初始化系统");
    }

    public static void init(Connection conn) throws Exception {
        ArrayList files = new ArrayList();

        File f;
        try {
            f = new File(NettyUtils.getRootContent() + "/configures/database/data/codetable");
            MsInfo.getFiles(f, "xml", files);
        } catch (Exception var7) {
            ;
        }

        Iterator var3;
        SAXReader read;
        Document doc;
        if(files.size() > 0) {
            var3 = files.iterator();

            while(var3.hasNext()) {
                f = (File)var3.next();
                read = new SAXReader();
                doc = read.read(f);
                initcodedata(doc);
            }
        }

        files.clear();

        try {
            f = new File(NettyUtils.getRootContent() + "/configures/database/data/treecodetable");
            MsInfo.getFiles(f, "xml", files);
        } catch (Exception var6) {
            ;
        }

        if(files.size() > 0) {
            var3 = files.iterator();

            while(var3.hasNext()) {
                f = (File)var3.next();
                read = new SAXReader();
                doc = read.read(f);
                inittreedata(doc);
            }
        }

        if(conn != null) {
            initcodesql(conn);
            inittreesql(conn);
        }

    }

    public static void init() throws Exception {
        init((Connection)null);
    }

    private static void inittreesql(Connection conn) throws Exception {
        EntityImpl en = new EntityImpl(conn);
        EntityImpl en2 = new EntityImpl(conn);
        int size2 = en2.executeQuery("select a.type,a.note from sys_treecode_index a order by a.type");
        int size = en.executeQuery("select a.type,a.code,a.note,a.sort from sys_treecode_values a order by a.type,a.sort");
        HashMap m = new HashMap();
        HashMap mm = new HashMap();

        int i;
        String type;
        String note;
        for(i = 0; i < size; ++i) {
            type = en.getStringValue("id", i);
            note = en.getStringValue("pid", i);
            String code = en.getStringValue("type", i);
            String map = en.getStringValue("code", i);
            String er = en.getStringValue("note", i);
            String sort = en.getStringValue("sort", i);
            Object c = (Map)m.get(code);
            if(c == null) {
                c = new HashMap();
                m.put(code, c);
            }

            ((Map)c).put(map, er);
            mm.put(code + map + "#####id", type);
            mm.put(code + map + "#####pid", note);
            mm.put(code + map + "#####sort", sort);
        }

        for(i = 0; i < size2; ++i) {
            type = en2.getStringValue("type", i);
            note = en2.getStringValue("note", i);
            if(type != null && !codeTypes.containsKey(type)) {
                codeTypes.put(type, CType.TREE_TABLE);
                Code var22 = new Code();
                var22.setcType(CType.TREE_TABLE);
                var22.setType(type);
                var22.setNote(note);
                var22.setRemark(note);
                codes.add(var22);
                Map var23 = (Map)m.get(type);
                CItem ci;
                if(var23 != null && var23.size() > 0) {
                    for(Iterator var25 = var23.entrySet().iterator(); var25.hasNext(); var22.addItem(ci)) {
                        Map.Entry var24 = (Map.Entry)var25.next();
                        String var26 = (String)var24.getKey();
                        String n = (String)var24.getValue();
                        String id = (String)mm.get(type + var26 + "#####id");
                        String pid = (String)mm.get(type + var26 + "#####pid");
                        String sort1 = (String)mm.get(type + var26 + "#####sort");
                        ci = new CItem();
                        ci.setId(id);
                        ci.setPid(pid);
                        ci.setCode(var26);
                        ci.setNote(n);

                        try {
                            ci.setSort(Integer.parseInt(sort1));
                        } catch (Exception var21) {
                            ci.setSort(1);
                        }
                    }
                }
            }
        }

    }

    private static void initcodesql(Connection conn) throws Exception {
        EntityImpl en = new EntityImpl(conn);
        EntityImpl en2 = new EntityImpl(conn);
        int size2 = en2.executeQuery("select a.type,a.note from sys_code_index a order by a.type");
        int size = en.executeQuery("select a.type,a.code,a.note,a.sort from sys_code_values a order by a.type,a.sort");
        HashMap m = new HashMap();
        HashMap mm = new HashMap();

        int i;
        String type;
        String note;
        for(i = 0; i < size; ++i) {
            type = en.getStringValue("type", i);
            note = en.getStringValue("code", i);
            String code = en.getStringValue("note", i);
            String map = en.getStringValue("sort", i);
            Object er = (Map)m.get(type);
            if(er == null) {
                er = new HashMap();
                m.put(type, er);
            }

            ((Map)er).put(note, code);
            mm.put(type + note + "#####sort", map);
        }

        for(i = 0; i < size2; ++i) {
            type = en2.getStringValue("type", i);
            note = en2.getStringValue("note", i);
            if(type != null && !codeTypes.containsKey(type)) {
                codeTypes.put(type, CType.CODE_TABLE);
                Code var20 = new Code();
                var20.setcType(CType.CODE_TABLE);
                var20.setType(type);
                var20.setNote(note);
                var20.setRemark(note);
                codes.add(var20);
                Map var21 = (Map)m.get(type);
                CItem ci;
                if(var21 != null && var21.size() > 0) {
                    for(Iterator var13 = var21.entrySet().iterator(); var13.hasNext(); var20.addItem(ci)) {
                        Map.Entry var22 = (Map.Entry)var13.next();
                        String c = (String)var22.getKey();
                        String n = (String)var22.getValue();
                        String sort = (String)mm.get(type + c + "#####sort");
                        ci = new CItem();
                        ci.setCode(c);
                        ci.setNote(n);

                        try {
                            ci.setSort(Integer.parseInt(sort));
                        } catch (Exception var19) {
                            ci.setSort(1);
                        }
                    }
                }
            }
        }

    }

    private static void inittreedata(Document doc) {
        List datas = doc.selectNodes("/root/data");
        if(datas != null && datas.size() > 0) {
            int i = 0;

            for(int l = datas.size(); i < l; ++i) {
                Element el = (Element)datas.get(i);
                String type = el.attributeValue("type");
                String note = el.attributeValue("note");
                String remark = el.attributeValue("remark");
                if(type != null && !codeTypes.containsKey(type)) {
                    if(note == null || note.length() <= 0) {
                        note = type;
                    }

                    if(remark == null || remark.length() <= 0) {
                        remark = note;
                    }

                    codeTypes.put(type, CType.TREE_TABLE);
                    Code code = new Code();
                    code.setcType(CType.TREE_TABLE);
                    code.setType(type);
                    code.setNote(note);
                    code.setRemark(remark);
                    codes.add(code);
                    List items = el.selectNodes("./item");
                    if(items != null && items.size() > 0) {
                        HashSet cs = new HashSet();
                        HashSet ids = new HashSet();
                        int j = 0;

                        for(int m = items.size(); j < m; ++j) {
                            Element e = (Element)items.get(j);
                            String id = e.attributeValue("id");
                            String pid = e.attributeValue("pid");
                            String c = e.attributeValue("code");
                            String n = e.attributeValue("note");
                            String sort = e.attributeValue("sort");
                            CItem ci = new CItem();
                            if(id != null && id.length() > 0 && !ids.contains(id)) {
                                ci.setId(id);
                                if(pid == null || pid.length() <= 0) {
                                    pid = "-1";
                                }

                                ci.setPid(pid);
                            } else if(id != null) {
                                Logger.warn("datas.xml中的树形码表【" + type + "】中,发现重复的id【" + id + "】");
                            }

                            if(c != null && c.length() > 0 && !cs.contains(c)) {
                                cs.add(c);
                                ci.setCode(c);
                                ci.setNote(n);

                                try {
                                    ci.setSort(Integer.parseInt(sort));
                                } catch (Exception var25) {
                                    ci.setSort(1);
                                }

                                HashMap map = new HashMap();
                                List li = e.attributes();

                                for(int a = 0; a < li.size(); ++a) {
                                    Attribute aa = (Attribute)li.get(a);
                                    map.put(aa.getName().toLowerCase(), aa.getValue());
                                }

                                ci.setM(map);
                                code.addItem(ci);
                            } else {
                                Logger.warn("datas.xml中的树形码表【" + type + "】中,发现重复的code【" + c + "】");
                            }
                        }
                    }
                }
            }
        } else {
            Logger.warn("初始化 数据码表的树形码表 xpath:/root/data，发现为空");
        }

    }

    private static void initcodedata(Document doc) {
        List datas = doc.selectNodes("/root/data");
        if(datas != null && datas.size() > 0) {
            int i = 0;

            for(int l = datas.size(); i < l; ++i) {
                Element el = (Element)datas.get(i);
                String type = el.attributeValue("type");
                String note = el.attributeValue("note");
                String remark = el.attributeValue("remark");
                if(type != null && !codeTypes.containsKey(type)) {
                    if(note == null || note.length() <= 0) {
                        note = type;
                    }

                    if(remark == null || remark.length() <= 0) {
                        remark = note;
                    }

                    codeTypes.put(type, CType.CODE_TABLE);
                    Code code = new Code();
                    code.setcType(CType.CODE_TABLE);
                    code.setType(type);
                    code.setNote(note);
                    code.setRemark(remark);
                    codes.add(code);
                    List items = el.selectNodes("./item");
                    if(items != null && items.size() > 0) {
                        HashSet cs = new HashSet();
                        int j = 0;

                        for(int m = items.size(); j < m; ++j) {
                            Element e = (Element)items.get(j);
                            String c = e.attributeValue("code");
                            String n = e.attributeValue("note");
                            String sort = e.attributeValue("sort");
                            if(c != null && c.length() > 0 && !cs.contains(c)) {
                                cs.add(c);
                                CItem ci = new CItem();
                                ci.setCode(c);
                                ci.setNote(n);

                                try {
                                    ci.setSort(Integer.parseInt(sort));
                                } catch (Exception var22) {
                                    ci.setSort(1);
                                }

                                HashMap map = new HashMap();
                                List li = e.attributes();

                                for(int a = 0; a < li.size(); ++a) {
                                    Attribute aa = (Attribute)li.get(a);
                                    map.put(aa.getName().toLowerCase(), aa.getValue());
                                }

                                ci.setM(map);
                                code.addItem(ci);
                            } else {
                                Logger.warn("datas.xml中的一般码表【" + type + "】中,发现重复的code【" + c + "】");
                            }
                        }
                    }
                }
            }
        } else {
            Logger.warn("初始化 数据码表的一般码表 xpath:/root/data，发现为空");
        }

    }

    public static CItem item(String type, String code) {
        int i = 0;

        for(int l = codes.size(); i < l; ++i) {
            Code c = (Code)codes.get(i);
            if(c.getType().equals(type)) {
                return c.getItem(code);
            }
        }

        return null;
    }

    public static CItem itemByNote(String type, String note) {
        int i = 0;

        for(int l = codes.size(); i < l; ++i) {
            Code c = (Code)codes.get(i);
            if(c.getType().equals(type)) {
                return c.getItemByNote(note);
            }
        }

        return null;
    }

    public static String note(String type, String code) {
        int i = 0;

        for(int l = codes.size(); i < l; ++i) {
            Code c = (Code)codes.get(i);
            if(c.getType().equals(type)) {
                return c.getNote(code);
            }
        }

        return "";
    }

    public static String code(String type, String note) {
        int i = 0;

        for(int l = codes.size(); i < l; ++i) {
            Code c = (Code)codes.get(i);
            if(c.getType().equals(type)) {
                return c.getCode(note);
            }
        }

        return "";
    }

    public static Code sql(String sql, Connection conn) {
        Code code = new Code();

        try {
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    Map m = (Map)e.getValues().get(i);
                    String id = String.valueOf(m.get("id"));
                    String pid = String.valueOf(m.get("parent_id"));
                    String code1 = String.valueOf(m.get("code"));
                    String note = String.valueOf(m.get("note"));
                    CItem ci = new CItem();
                    ci.setId(id);
                    ci.setPid(pid);
                    ci.setCode(code1);
                    ci.setNote(note);
                    code.addItem(ci);
                }
            }
        } catch (Exception var12) {
            Logger.error(var12);
        }

        return code;
    }

    public static Code sql(String sql) {
        Code code = new Code();
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    Map m = (Map)e.getValues().get(i);
                    String code1 = String.valueOf(m.get("code"));
                    String note = String.valueOf(m.get("note"));
                    CItem ci = new CItem();
                    ci.setId(Utils.getMapStringValue(m, "id"));
                    ci.setPid(Utils.getMapStringValue(m, "parent_id"));
                    ci.setSort(Utils.getMapIntegerValue(m, "sort"));
                    ci.setCode(code1);
                    ci.setNote(note);
                    code.addItem(ci);
                }
            }

            conn.commit();
        } catch (Exception var19) {
            Logger.error(var19);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var18) {
                ;
            }

        }

        return code;
    }

    public static String sqlNote(String sql, String code) {
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            String e = sqlNote(sql, code, conn);
            conn.commit();
            String var6 = e;
            return var6;
        } catch (Exception var14) {
            Logger.error(var14);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var13) {
                ;
            }

        }

        return "";
    }

    public static String sqlCode(String sql, String note) {
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            String e = sqlCode(sql, note, conn);
            conn.commit();
            String var6 = e;
            return var6;
        } catch (Exception var14) {
            Logger.error(var14);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var13) {
                ;
            }

        }

        return "";
    }

    public static String sqlNote(String sql, String code, Connection conn) {
        try {
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    Map m = (Map)e.getValues().get(i);
                    String code1 = String.valueOf(m.get("code"));
                    String node = String.valueOf(m.get("note"));
                    if(code.equals(code1)) {
                        return node;
                    }
                }
            }
        } catch (Exception var9) {
            Logger.error(var9);
        }

        return "";
    }

    public static String sqlCode(String sql, String note, Connection conn) {
        try {
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    Map m = (Map)e.getValues().get(i);
                    String code1 = String.valueOf(m.get("code"));
                    String node1 = String.valueOf(m.get("note"));
                    if(note.equals(node1)) {
                        return code1;
                    }
                }
            }
        } catch (Exception var9) {
            Logger.error(var9);
        }

        return "";
    }

    public static CType getTableType(String type) {
        CType ttype = (CType)codeTypes.get(type);
        if(ttype == null) {
            ttype = CType.NONE;
        }

        return ttype;
    }
}
