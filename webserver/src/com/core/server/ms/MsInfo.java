package com.core.server.ms;

import com.core.enuts.ColumnType;
import com.core.server.c.DType;
import com.core.server.log.Logger;
import com.core.server.qm.QmInfo;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class MsInfo {
    private static Map<String, MsInfo> msInfos = new HashMap();
    public static Pattern pattern = Pattern.compile("#\\{user.([\\w]*)\\}");
    public static Pattern paramsPattern = Pattern.compile("_\\{p.([\\w]*)\\}");
    private QmInfo qmInfo = null;
    private String title;
    private String name;
    private String orderby;
    private String desc;
    private User user;
    private String sql;
    private String collectionName;
    private String description;
    private QmColumns columns;
    private String condition;
    private PageLegends pageLegends;
    private QmToolbars toolbars;
    private File file;
    private Document doc;
    private List<String> jsFuns;
    private List<String> onceJsFuns;
    private List<String> jsFunctions = new ArrayList();
    private String skin;
    private DType dType;
    private boolean paging;
    private boolean refresh;
    private boolean exportExcel = true;
    private String hookClass;
    private boolean hookReplace = false;
    private int curPage;
    private int pageSize;
    private int totalSize;
    private String editPage;
    private File editPageFile;
    private Action act;
    public static String[] allCodes = new String[]{"codetable", "treecodetable", "csql", "tsql", "sql"};
    public static String[] hasValuedCodes = new String[]{"codetable", "treecodetable"};
    public static String[] sqlCodes = new String[]{"csql", "tsql", "sql"};
    public static Map<String, Code> queryedData = new HashMap();

    public MsInfo() {
    }

    public JSONArray toJson() throws Exception {
        return this.pageLegends.toJson(this.act, this.dType);
    }

    public JSONArray toJson(String _id) throws Exception {
        return this.pageLegends.toJson(this.act, this.dType, _id);
    }

    public static MsInfo initialize(String name, Action act) throws Exception {
        MsInfo qm = (MsInfo)msInfos.get(name);
        if(qm == null || Resources.DEVELOPMENT) {
            qm = new MsInfo();
            qm.setName(name);
            File file = null;

            String pageSize;
            try {
                File read = new File(NettyUtils.getRootContent() + "/configures/ms");
                file = getFile(read, name, "xml");
                if(file != null) {
                    qm.file = file;
                } else {
                    File doc = new File(NettyUtils.getRootContent() + "/configures/qm");
                    file = getFile(doc, name, "cfg");
                    if(file != null) {
                        qm.setdType(DType.OLDQM);
                        qm.qmInfo = QmInfo.initialize(new FileInputStream(file), name);
                        qm.qmInfo.ms = qm;
                        msInfos.put(name, qm);
                        return qm;
                    }

                    File table = getFile(new File(NettyUtils.getRootContent() + "/configures/database/mgo"), name, "xml");
                    if(table == null) {
                        throw new Exception("找不到对应的ms或者qm配置的文件");
                    }

                    String[] title = Utils.getFileNameWithoutExt(table.getName()).split("-");
                    pageSize = "";
                    if(title.length > 1) {
                        pageSize = "-" + title[1];
                    } else {
                        pageSize = "";
                    }

                    file = new File(NettyUtils.getRootContent() + "/configures/ms/" + name + pageSize + ".xml");
                    file = createMsInfoFile(table, file);
                    qm.file = file;
                }
            } catch (Exception var69) {
                Logger.error(var69);
            }

            SAXReader var70 = new SAXReader();
            Document var71 = var70.read(file);
            qm.doc = var71;
            Element var72 = var71.getRootElement();
            String var73 = var72.attributeValue("title");
            pageSize = var72.attributeValue("pageSize");
            String description = var72.attributeValue("description");
            String mode = var72.attributeValue("skin");
            String dType = var72.attributeValue("type");
            String collectionName = var72.attributeValue("collectionName");
            String sort1 = var72.attributeValue("sort");
            String editPage = var72.attributeValue("editpage");
            if(editPage != null && editPage.length() > 0) {
                File condition = new File(Utils.getWebRootPath() + editPage);
                if(condition.exists()) {
                    qm.editPage = editPage;
                    qm.editPageFile = condition;
                }
            }

            if(mode != null && mode.trim().length() > 0) {
                qm.setSkin(mode);
            } else {
                qm.setSkin("default");
            }

            if("mysql".equalsIgnoreCase(dType)) {
                qm.setdType(DType.NORMAL);
            } else if("data".equalsIgnoreCase(dType)) {
                qm.setdType(DType.DATA);
            } else if("xml".equalsIgnoreCase(dType)) {
                qm.setdType(DType.XML);
            } else {
                qm.setdType(DType.MGO);
            }

            qm.setOrderby(sort1);
            qm.setDesc("desc");
            if(collectionName != null) {
                qm.setCollectionName(collectionName.trim());
            }

            String var74 = var72.attributeValue("condition");

            try {
                qm.condition = var74;
            } catch (Exception var68) {
                qm.condition = "[]";
            }

            boolean paging = !"false".equalsIgnoreCase(var72.attributeValue("paging"));
            boolean refresh = !"false".equalsIgnoreCase(var72.attributeValue("refresh"));
            boolean exportExcel = !"false".equalsIgnoreCase(var72.attributeValue("exportExcel"));
            String sql = var72.attributeValue("sql");
            if(sql != null && sql.length() > 0) {
                qm.setSql(sql);
            }

            qm.setPaging(paging);
            qm.setRefresh(refresh);
            qm.setExportExcel(exportExcel);
            if(var73 != null && var73.length() > 0) {
                qm.setTitle(var73.trim());
            } else {
                qm.setTitle(qm.getName());
            }

            if(description != null && description.length() > 0) {
                qm.setDescription(description.trim());
            } else {
                qm.setDescription(qm.getName());
            }

            try {
                qm.setPageSize(Integer.parseInt(pageSize.trim()));
            } catch (Exception var67) {
                qm.setPageSize(20);
            }

            List toolbarlist = var72.selectNodes("./toolbars/toolbar");
            int funs;
            String _class;
            String l;
            String el;
            String method;
            String pl;
            String ps;
            String field;
            String display;
            String columnType;
            String controlType;
            String bindType;
            String bindData;
            String height;
            if(toolbarlist != null && toolbarlist.size() > 0) {
                QmToolbars columns = new QmToolbars();
                int legends = 0;

                for(funs = toolbarlist.size(); legends < funs; ++legends) {
                    Element oncefuns = (Element)toolbarlist.get(legends);
                    String javaFun = oncefuns.attributeValue("class");
                    _class = oncefuns.attributeValue("text");
                    l = oncefuns.attributeValue("js");
                    el = oncefuns.attributeValue("name");
                    method = oncefuns.attributeValue("align");
                    pl = oncefuns.attributeValue("role");
                    ps = oncefuns.attributeValue("visible_code");
                    QmToolbar inputs = new QmToolbar();
                    if(javaFun != null && javaFun.trim().length() > 0) {
                        inputs.set_class(javaFun.trim());
                    }

                    if(_class != null && _class.trim().length() > 0) {
                        inputs.setText(_class.trim());
                    }

                    if(l != null && l.trim().length() > 0) {
                        inputs.setJs(l.trim());
                    }

                    if(el != null && el.trim().length() > 0) {
                        inputs.setName(el.trim());
                    }

                    if("left".equalsIgnoreCase(method)) {
                        inputs.setAlign(QmAlign.LEFT);
                    } else if("right".equalsIgnoreCase(method)) {
                        inputs.setAlign(QmAlign.RIGHT);
                    } else {
                        inputs.setAlign(QmAlign.CENTER);
                    }

                    String[] i;
                    if(pl != null && pl.trim().length() > 0) {
                        i = pl.trim().split(",");
                        inputs.setRoles(i);
                    }

                    if(ps != null && ps.trim().length() > 0) {
                        i = ps.trim().split(",");
                        inputs.setVisibleCodes(i);
                    }

                    List var98 = oncefuns.selectNodes("./toolbar");
                    if(var98 != null && var98.size() > 0) {
                        inputs.getSubItems().clear();

                        QmToolbar width;
                        for(Iterator cl = var98.iterator(); cl.hasNext(); inputs.getSubItems().add(width)) {
                            Element l1 = (Element)cl.next();
                            field = l1.attributeValue("class");
                            display = l1.attributeValue("text");
                            columnType = l1.attributeValue("js");
                            controlType = l1.attributeValue("name");
                            bindType = l1.attributeValue("align");
                            bindData = l1.attributeValue("role");
                            height = l1.attributeValue("visible_code");
                            width = new QmToolbar();
                            if(field != null && field.trim().length() > 0) {
                                width.set_class(field.trim());
                            }

                            if(display != null && display.trim().length() > 0) {
                                width.setText(display.trim());
                            }

                            if(columnType != null && columnType.trim().length() > 0) {
                                width.setJs(columnType.trim());
                            }

                            if(controlType != null && controlType.trim().length() > 0) {
                                width.setName(controlType.trim());
                            }

                            if("left".equalsIgnoreCase(bindType)) {
                                width.setAlign(QmAlign.LEFT);
                            } else if("right".equalsIgnoreCase(bindType)) {
                                width.setAlign(QmAlign.RIGHT);
                            } else {
                                width.setAlign(QmAlign.CENTER);
                            }

                            String[] hidden;
                            if(bindData != null && bindData.trim().length() > 0) {
                                hidden = bindData.trim().split(",");
                                width.setRoles(hidden);
                            }

                            if(height != null && height.trim().length() > 0) {
                                hidden = height.trim().split(",");
                                width.setVisibleCodes(hidden);
                            }
                        }
                    }

                    columns.addQmToolbar(inputs);
                }

                qm.setToolbars(columns);
            }

            List var75 = var72.selectNodes("./columns/column");
            Element var83;
            if(var75 != null && var75.size() > 0) {
                QmColumns var76 = new QmColumns();
                var76.setMsInfo(qm);
                qm.setColumns(var76);
                funs = 0;

                for(int var79 = var75.size(); funs < var79; ++funs) {
                    var83 = (Element)var75.get(funs);
                    _class = var83.attributeValue("code");
                    l = var83.attributeValue("display");
                    el = var83.attributeValue("type");
                    method = var83.attributeValue("width");
                    pl = var83.attributeValue("format");
                    ps = var83.attributeValue("reminder");
                    String var96 = var83.attributeValue("align");
                    String var99 = var83.attributeValue("bindType");
                    String var100 = var83.attributeValue("bindData");
                    String var103 = var83.attributeValue("fieldName");
                    boolean var105 = "true".equalsIgnoreCase(var83.attributeValue("show"));
                    boolean var106 = "true".equalsIgnoreCase(var83.attributeValue("send"));
                    boolean var107 = !"false".equalsIgnoreCase(var83.attributeValue("sort"));
                    boolean var108 = "true".equalsIgnoreCase(var83.attributeValue("ignore"));
                    QmColumn var109 = new QmColumn();
                    if(_class != null && _class.length() > 0) {
                        var109.setCode(_class.trim());
                        if(var103 != null && var103.length() > 0) {
                            var109.setFieldName(var103);
                        } else {
                            var109.setFieldName(var109.getCode());
                        }
                    }

                    var109.setIgnore(var108);
                    var109.setSort(var107);
                    if(var99 != null) {
                        var109.setBindtype(var99);
                        if(var100 != null && var100.length() > 0) {
                            var109.setBinddata(var100);
                        }
                    } else {
                        var109.setBindtype("no");
                    }

                    if(l != null && l.length() > 0) {
                        var109.setDisplay(l.trim());
                    } else {
                        var109.setDisplay(var109.getCode());
                    }

                    if("long".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.LONG);
                    } else if("int".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.INT);
                    } else if("date".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.DATE);
                    } else if("float".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.FLOAT);
                    } else if("datetime".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.DATETIME);
                    } else if("text".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.TEXT);
                    } else if("LONGTEXT".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.LONGTEXT);
                    } else if("MEDIUMTEXT".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.MEDIUMTEXT);
                    } else if("CLOB".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.CLOB);
                    } else if("BLOB".equalsIgnoreCase(el)) {
                        var109.setType(ColumnType.BLOB);
                    } else {
                        var109.setType(ColumnType.STRING);
                    }

                    if("left".equalsIgnoreCase(var96)) {
                        var109.setAlign(QmAlign.LEFT);
                    } else if("right".equalsIgnoreCase(var96)) {
                        var109.setAlign(QmAlign.RIGHT);
                    } else {
                        var109.setAlign(QmAlign.CENTER);
                    }

                    if(pl != null && pl.length() > 0) {
                        var109.setFormat(pl.trim());
                    }

                    try {
                        var109.setWidth(Integer.parseInt(method.trim()));
                    } catch (Exception var66) {
                        var109.setWidth(100);
                    }

                    if(ps != null && ps.length() > 0) {
                        var109.setReminder(ps.trim());
                    } else {
                        var109.setReminder("点击排序");
                    }

                    var109.setSend(var106);
                    var109.setShow(var105);
                    var76.addColumn(var109);
                }
            }

            List var77 = var72.selectNodes("./page/legends/legend");
            if(var77 != null && var77.size() > 0) {
                PageLegends var78 = new PageLegends();
                var78.setMsInfo(qm);
                qm.setPageLegends(var78);
                String var81 = var72.valueOf("./page/@columnnumber");

                try {
                    qm.getPageLegends().setColumnNumber(Integer.parseInt(var81));
                } catch (Exception var65) {
                    ;
                }

                HashSet var85 = new HashSet();
                Iterator var90 = var77.iterator();

                while(var90.hasNext()) {
                    Element var88 = (Element)var90.next();
                    el = var88.attributeValue("name");
                    method = var88.attributeValue("title");
                    PageLegend var94 = new PageLegend();
                    var94.setMsInfo(qm);
                    var94.setTitle(method);
                    var94.setName(el);
                    var78.addLegend(var94);
                    PageColumns var95 = new PageColumns();
                    var94.setPageColumns(var95);
                    var95.setMsInfo(qm);
                    List var97 = var88.selectNodes("./input");
                    int var101 = 0;

                    for(int var102 = var97.size(); var101 < var102; ++var101) {
                        Element var104 = (Element)var97.get(var101);
                        field = var104.attributeValue("name");
                        display = var104.attributeValue("display");
                        columnType = var104.attributeValue("columnType");
                        controlType = var104.attributeValue("controlType");
                        bindType = var104.attributeValue("bindType");
                        bindData = var104.attributeValue("bindData");
                        height = var104.attributeValue("height");
                        String var110 = var104.attributeValue("width");
                        String var111 = var104.attributeValue("hidden");
                        String readonly = var104.attributeValue("readonly");
                        String unique = var104.attributeValue("unique");
                        String oneLine = var104.attributeValue("oneLine");
                        String nullable = var104.attributeValue("nullable");
                        String spanColNum = var104.attributeValue("spanColNum");
                        String defaultValue = var104.attributeValue("defaultValue");
                        String min = var104.attributeValue("min");
                        String max = var104.attributeValue("max");
                        String precision = var104.attributeValue("precision");
                        String sort = var104.attributeValue("sort");
                        PageColumn pc = new PageColumn();
                        String[] params = new String[]{"name", "display", "columnType", "controlType", "bindType", "bindData", "height", "width", "hidden", "readonly", "unique", "oneLine", "nullable", "spanColNum", "defaultValue", "min", "max", "precision", "sort"};
                        List li = var104.attributes();

                        int s;
                        for(s = 0; s < li.size(); ++s) {
                            Attribute aa = (Attribute)li.get(s);
                            if(!Utils.contains(params, aa.getName())) {
                                pc.getM().put(aa.getName().toLowerCase(), aa.getValue());
                            }
                        }

                        if(field == null || field.length() <= 0) {
                            throw new Exception("MS【" + qm.getName() + "】没有配置 Page Column的field属性");
                        }

                        if(var85.contains(field)) {
                            throw new Exception("MS【" + qm.getName() + "】Page Column的field属性【" + field + "】重复了");
                        }

                        pc.setName(field);
                        var85.add(field);

                        try {
                            pc.setMin(Integer.parseInt(min));
                        } catch (Exception var64) {
                            ;
                        }

                        try {
                            pc.setMax(Integer.parseInt(max));
                        } catch (Exception var63) {
                            ;
                        }

                        try {
                            pc.setPrecision(Integer.parseInt(precision));
                        } catch (Exception var62) {
                            ;
                        }

                        if(display != null && display.length() > 0) {
                            pc.setDisplay(display);
                        } else {
                            pc.setDisplay(field.toUpperCase());
                        }

                        pc.setBindType(bindType);
                        pc.setBindData(bindData);
                        if("long".equalsIgnoreCase(columnType)) {
                            pc.setColumnType(ColumnType.LONG);
                        } else if("int".equalsIgnoreCase(columnType)) {
                            pc.setColumnType(ColumnType.INT);
                        } else if("date".equalsIgnoreCase(columnType)) {
                            pc.setColumnType(ColumnType.DATE);
                        } else if("float".equalsIgnoreCase(columnType)) {
                            pc.setColumnType(ColumnType.FLOAT);
                        } else if("datetime".equalsIgnoreCase(columnType)) {
                            pc.setColumnType(ColumnType.DATETIME);
                        } else {
                            pc.setColumnType(ColumnType.STRING);
                        }

                        try {
                            pc.setHeight(height);
                        } catch (Exception var61) {
                            pc.setHeight("20px");
                        }

                        pc.setWidth(var110);
                        pc.setHidden(Utils.isTrue(var111));
                        pc.setReadonly(Utils.isTrue(readonly));
                        pc.setUnique(Utils.isTrue(unique));
                        pc.setOneLine(Utils.isTrue(oneLine));
                        pc.setNullable(Utils.isTrue(nullable));
                        pc.setControlType(controlType);

                        try {
                            pc.setSpanColNum(Integer.parseInt(spanColNum));
                        } catch (Exception var60) {
                            pc.setSpanColNum(1);
                        }

                        if(defaultValue != null && defaultValue.length() > 0) {
                            pc.setDefaultValue(defaultValue);
                        }

                        s = 1;
                        if(sort != null && sort.length() > 0) {
                            s = Integer.parseInt(sort);
                        }

                        pc.setSort(s);
                        var95.addColumn(pc);
                    }
                }
            } else {
                qm.getColumns().initPageLegends();
            }

            List var80 = var72.selectNodes("./jsFuns/fun");
            int var89;
            if(var80 != null && var80.size() > 0) {
                ArrayList var82 = new ArrayList();
                qm.setJsFuns(var82);
                int var86 = 0;

                for(var89 = var80.size(); var86 < var89; ++var86) {
                    Element var91 = (Element)var80.get(var86);
                    el = var91.attributeValue("method");
                    if(el != null && el.trim().length() > 0) {
                        var82.add(el);
                    }
                }
            }

            List var84 = var72.selectNodes("./onceJsFuns/fun");
            if(var84 != null && var84.size() > 0) {
                ArrayList var87 = new ArrayList();
                qm.setOnceJsFuns(var87);
                var89 = 0;

                for(int var92 = var84.size(); var89 < var92; ++var89) {
                    Element var93 = (Element)var84.get(var89);
                    method = var93.attributeValue("method");
                    if(method != null && method.trim().length() > 0) {
                        var87.add(method);
                    }
                }
            }

            var83 = (Element)var72.selectSingleNode("./javaFun");
            if(var83 != null) {
                _class = var83.attributeValue("class");
                if(_class != null && _class.trim().length() > 0) {
                    qm.setHookClass(_class.trim());
                    qm.hookReplace = Utils.isTrue(var83.attributeValue("replaceAll"));
                }
            }

            if(qm.getdType() == DType.NORMAL) {
                qm.qmInfo = new QmInfo();
                qm.qmInfo.ms = qm;
            }

            msInfos.put(name, qm);
        }

        qm.createXML();
        qm.setAct(act);
        return qm;
    }

    private static File createMsInfoFile(File dbFile, File file) throws Exception {
        SAXReader read = new SAXReader();
        Document doc = read.read(dbFile);
        Element table = doc.getRootElement();
        String name = Utils.getFileNameWithoutExt(file.getName());
        name = name.split("-")[0];
        Element toolbars = DocumentHelper.createElement("toolbars");
        Element toolbar = toolbars.addElement("toolbar");
        toolbar.addAttribute("class", "icon-detail");
        toolbar.addAttribute("text", "详细信息");
        toolbar.addAttribute("js", "detail");
        toolbar.addAttribute("name", "detail");
        toolbar.addAttribute("name", name + "_detail");
        toolbar.addAttribute("type", "button");
        toolbar.addAttribute("align", "align");
        toolbar.addAttribute("com_js", "");
        toolbar.addAttribute("role", "");
        toolbar.addAttribute("visible_code", name + "_detail");
        toolbar = toolbars.addElement("toolbar");
        toolbar.addAttribute("class", "icon-add");
        toolbar.addAttribute("text", "添加");
        toolbar.addAttribute("js", "add");
        toolbar.addAttribute("name", name + "_add");
        toolbar.addAttribute("type", "button");
        toolbar.addAttribute("align", "align");
        toolbar.addAttribute("com_js", "");
        toolbar.addAttribute("role", "");
        toolbar.addAttribute("visible_code", name + "_add");
        toolbar = toolbars.addElement("toolbar");
        toolbar.addAttribute("class", "icon-edit");
        toolbar.addAttribute("text", "修改");
        toolbar.addAttribute("js", "edit");
        toolbar.addAttribute("name", name + "_edit");
        toolbar.addAttribute("type", "button");
        toolbar.addAttribute("align", "align");
        toolbar.addAttribute("com_js", "");
        toolbar.addAttribute("role", "");
        toolbar.addAttribute("visible_code", name + "_edit");
        toolbar = toolbars.addElement("toolbar");
        toolbar.addAttribute("class", "icon-del");
        toolbar.addAttribute("text", "删除");
        toolbar.addAttribute("js", "del");
        toolbar.addAttribute("name", name + "_del");
        toolbar.addAttribute("type", "button");
        toolbar.addAttribute("align", "align");
        toolbar.addAttribute("com_js", "");
        toolbar.addAttribute("role", "");
        toolbar.addAttribute("visible_code", name + "_del");
        List fitemlist = table.selectNodes("./item");
        Element cols = DocumentHelper.createElement("columns");
        Iterator title = fitemlist.iterator();

        Element filedoc;
        String t;
        while(title.hasNext()) {
            filedoc = (Element)title.next();
            t = filedoc.attributeValue("code").toLowerCase();
            List writer = filedoc.selectNodes("./item");
            String bindType;
            String bindData;
            String format1;
            if(writer != null && writer.size() > 0) {
                Iterator format3 = writer.iterator();

                while(format3.hasNext()) {
                    Element ex1 = (Element)format3.next();
                    bindType = ex1.attributeValue("code").toLowerCase();
                    bindData = ex1.attributeValue("name");
                    String column2 = ex1.attributeValue("bindType");
                    format1 = ex1.attributeValue("bindData");
                    String type = ex1.attributeValue("type");
                    Element column1 = cols.addElement("column");
                    column1.addAttribute("code", t + "." + bindType);
                    column1.addAttribute("display", bindData);
                    column1.addAttribute("width", "100");
                    column1.addAttribute("bindType", column2);
                    column1.addAttribute("bindData", format1);
                    String format2 = "";
                    if("datetime".equals(type)) {
                        format2 = "yyyy-MM-dd HH:mm:ss";
                    } else if("date".equals(type)) {
                        format2 = "yyyy-MM-dd";
                    }

                    column1.attributeValue("type", type);
                    column1.addAttribute("format", format2);
                    column1.addAttribute("show", "true");
                    column1.addAttribute("send", "true");
                    column1.addAttribute("sort", "true");
                }
            } else {
                String ex = filedoc.attributeValue("name");
                String format = filedoc.attributeValue("type");
                bindType = filedoc.attributeValue("bindType");
                bindData = filedoc.attributeValue("bindData");
                Element column = cols.addElement("column");
                column.addAttribute("code", t);
                column.addAttribute("display", ex);
                column.addAttribute("width", "100");
                column.addAttribute("bindType", bindType);
                column.addAttribute("bindData", bindData);
                format1 = "";
                if("datetime".equals(format)) {
                    format1 = "yyyy-MM-dd HH:mm:ss";
                } else if("date".equals(format)) {
                    format1 = "yyyy-MM-dd";
                }

                column.attributeValue("type", format);
                column.addAttribute("format", format1);
                if(!t.equalsIgnoreCase("id") && !t.equalsIgnoreCase("_id")) {
                    column.addAttribute("show", "true");
                } else {
                    column.addAttribute("show", "false");
                }

                column.addAttribute("send", "true");
                column.addAttribute("sort", "true");
            }
        }

        new SAXReader();
        filedoc = DocumentHelper.createElement("table");
        String[] title1 = Utils.getFileNameWithoutExt(dbFile.getName()).split("-");
        t = "";
        if(title1.length > 1) {
            t = title1[1];
        } else {
            t = title1[0].toUpperCase();
        }

        filedoc.addAttribute("title", t);
        filedoc.addAttribute("collectionName", name.split("-")[0]);
        filedoc.addAttribute("condition", "[]");
        filedoc.addAttribute("sort", "{}");
        filedoc.addAttribute("type", "mgo");
        filedoc.add(toolbars);
        filedoc.add(cols);
        XMLWriter writer1 = null;

        try {
            FileOutputStream ex2 = new FileOutputStream(file);
            OutputFormat format4 = OutputFormat.createPrettyPrint();
            writer1 = new XMLWriter(ex2, format4);
            writer1.write(filedoc);
        } catch (Exception var29) {
            Logger.error(var29);
        } finally {
            if(writer1 != null) {
                try {
                    writer1.close();
                } catch (IOException var28) {
                    ;
                }
            }

        }

        return file;
    }

    private void createXML() {
        XMLWriter writer = null;

        try {
            FileOutputStream ex = new FileOutputStream(this.getFile());
            OutputFormat format = OutputFormat.createPrettyPrint();
            writer = new XMLWriter(ex, format);
            writer.write(this.doc);
        } catch (Exception var12) {
            Logger.error(var12);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException var11) {
                    ;
                }
            }

        }

    }

    public static void main(String[] args) throws Exception {
        initialize("fd_pay_receive_detail", (Action)null);
    }

    public JSONArray getHead() {
        JSONArray ths = new JSONArray();

        for(int i = 0; i < this.getColumns().getCols().size(); ++i) {
            QmColumn qc = (QmColumn)this.getColumns().getCols().get(i);
            JSONObject th = new JSONObject();
            th.put("code", qc.getCode());
            th.put("display", qc.getDisplay());
            th.put("fieldName", qc.getFieldName());
            th.put("width", qc.getWidth());
            th.put("align", qc.getAlign());
            th.put("show", qc.isShow());
            th.put("ignore", qc.isIgnore());
            th.put("send", qc.isSend());
            th.put("sort", qc.isSort());
            th.put("bindType", qc.getBindtype());
            th.put("bindData", qc.getBinddata());
            th.put("type", qc.getType());
            JSONObject qm_th;
            if(qc.getCode().equals("id")) {
                qm_th = new JSONObject();
                qm_th.put("code", "_id");
                qm_th.put("display", qc.getDisplay());
                qm_th.put("fieldName", qc.getFieldName());
                qm_th.put("width", qc.getWidth());
                qm_th.put("align", qc.getAlign());
                qm_th.put("show", qc.isShow());
                qm_th.put("ignore", qc.isIgnore());
                qm_th.put("send", qc.isSend());
                qm_th.put("sort", qc.isSort());
                qm_th.put("bindType", qc.getBindtype());
                qm_th.put("bindData", qc.getBinddata());
                qm_th.put("type", "hidden");
                ths.put(qm_th);
            }

            ths.put(th);
            if(Utils.contains(allCodes, qc.getBindtype())) {
                qm_th = new JSONObject();
                qm_th.put("code", qc.getCode() + "__qm_code");
                qm_th.put("display", qc.getDisplay());
                qm_th.put("fieldName", qc.getFieldName() + "__qm_code");
                qm_th.put("width", "0");
                qm_th.put("align", qc.getAlign());
                qm_th.put("show", false);
                qm_th.put("ignore", false);
                qm_th.put("send", true);
                qm_th.put("sort", 1);
                qm_th.put("bindType", "");
                qm_th.put("bindData", "");
                ths.put(qm_th);
            }
        }

        return ths;
    }

    public JSONArray getToobars() {
        JSONArray ths = new JSONArray();

        for(int i = 0; i < this.getToolbars().getToolbars().size(); ++i) {
            QmToolbar toolBar = (QmToolbar)this.getToolbars().getToolbars().get(i);
            JSONObject bar = new JSONObject();
            bar.put("_class", toolBar.get_class());
            bar.put("text", toolBar.getText());
            bar.put("js", toolBar.getJs());
            bar.put("name", toolBar.getName());
            bar.put("align", toolBar.getAlign());
            bar.put("visibleCodes", toolBar.getVisibleCodes());
            JSONArray li = new JSONArray();
            if(toolBar.getSubItems().size() > 0) {
                Iterator var7 = toolBar.getSubItems().iterator();

                while(var7.hasNext()) {
                    QmToolbar su = (QmToolbar)var7.next();
                    li.put(su.toJson());
                }
            }

            bar.put("li", li);
            ths.put(bar);
        }

        return ths;
    }

    public String getOrderby() {
        return this.orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSql() {
        return this.sql == null && this.qmInfo != null?this.qmInfo.getSql():this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QmColumns getColumns() {
        return this.columns == null && this.qmInfo != null?this.qmInfo.getColumns():this.columns;
    }

    public void setColumns(QmColumns columns) {
        this.columns = columns;
    }

    public QmToolbars getToolbars() {
        if(this.toolbars == null && this.getQmInfo() != null) {
            return this.getQmInfo().getToolbars();
        } else {
            if(this.toolbars == null) {
                this.toolbars = new QmToolbars();
            }

            return this.toolbars;
        }
    }

    public void setToolbars(QmToolbars toolbars) {
        this.toolbars = toolbars;
    }

    public List<String> getJsFuns() {
        return this.jsFuns;
    }

    public void setJsFuns(List<String> jsFuns) {
        this.jsFuns = jsFuns;
    }

    public List<String> getOnceJsFuns() {
        return this.onceJsFuns;
    }

    public void setOnceJsFuns(List<String> onceJsFuns) {
        this.onceJsFuns = onceJsFuns;
    }

    public List<String> getJsFunctions() {
        return this.jsFunctions;
    }

    public void setJsFunctions(List<String> jsFunctions) {
        this.jsFunctions = jsFunctions;
    }

    public String getSkin() {
        return this.skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public DType getdType() {
        return this.dType;
    }

    public void setdType(DType dType) {
        this.dType = dType;
    }

    public boolean isPaging() {
        return this.paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public boolean isRefresh() {
        return this.refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isExportExcel() {
        return this.exportExcel;
    }

    public void setExportExcel(boolean exportExcel) {
        this.exportExcel = exportExcel;
    }

    public String getHookClass() {
        return this.hookClass;
    }

    public void setHookClass(String hookClass) {
        this.hookClass = hookClass;
    }

    public int getCurPage() {
        return this.curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public long getTotalPages(Long total, int pagesize) {
        if(pagesize <= 0) {
            pagesize = 20;
        }

        return total.longValue() % (long)pagesize == 0L?total.longValue() / (long)pagesize:total.longValue() / (long)pagesize + 1L;
    }

    public Action getAct() {
        return this.act;
    }

    public void setAct(Action act) {
        this.act = act;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCollectionName() {
        return this.collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public PageLegends getPageLegends() {
        return this.pageLegends;
    }

    public void setPageLegends(PageLegends pageLegends) {
        this.pageLegends = pageLegends;
    }

    public File getFile() {
        return this.file;
    }

    public Document getDoc() {
        return this.doc;
    }

    public static File getFile(File baseDirectory, String filename, String ext) throws Exception {
        FileScaner scaner = new FileScaner(baseDirectory, filename, ext);
        return scaner.getFile();
    }

    public static void getFiles(File baseDirectory, String ext, List<File> li) throws Exception {
        if(baseDirectory.isDirectory()) {
            File[] list = baseDirectory.listFiles();
            File[] var7 = list;
            int var6 = list.length;

            for(int var5 = 0; var5 < var6; ++var5) {
                File f = var7[var5];
                if(f.isDirectory()) {
                    getFiles(f, ext, li);
                } else {
                    String name = f.getName();
                    String tempExt = Utils.getExt(name);
                    if(ext.equals(tempExt)) {
                        li.add(f);
                    }
                }
            }
        }

    }

    public List<Map<String, Object>> trimRows(List<Map<String, Object>> values, Action act) {
        ArrayList li = new ArrayList();
        int i = 0;

        for(int l = values.size(); i < l; ++i) {
            Map m = (Map)values.get(i);
            HashMap mm = new HashMap();
            mm.putAll(m);
            li.add(mm);
            Iterator var9 = m.entrySet().iterator();

            while(var9.hasNext()) {
                Map.Entry en = (Map.Entry)var9.next();
                String key = (String)en.getKey();
                Object val = en.getValue();
                QmColumn col = null;
                if(this.columns != null) {
                    col = this.columns.getColumn(key);
                } else {
                    col = this.qmInfo.getColumns().getColumn(key);
                }

                if(col != null) {
                    String vv;
                    if(Utils.contains(allCodes, col.getBindtype())) {
                        if(col.getBinddata() != null && col.getBinddata().length() > 0) {
                            Code var21 = null;
                            if(Utils.contains(hasValuedCodes, col.getBindtype())) {
                                try {
                                    var21 = Codes.code(col.getBinddata());
                                } catch (Exception var20) {
                                    act.L.error(var20);
                                }
                            } else if(Utils.contains(sqlCodes, col.getBindtype())) {
                                String var22 = col.getBinddata();
                                var21 = (Code)queryedData.get(var22);
                                if(var21 == null) {
                                    Matcher var23 = pattern.matcher(var22);

                                    while(var23.find()) {
                                        vv = var23.group(1);

                                        try {
                                            String uVal = BeanUtils.getProperty(act.getSessionUser(), vv);
                                            var22 = var22.replace("#{user." + vv + "}", uVal);
                                        } catch (Exception var19) {
                                            ;
                                        }
                                    }

                                    var21 = Codes.sql(var22);
                                    queryedData.put(var22, var21);
                                }
                            }

                            mm.put(key + "__qm_code", val);
                            if(var21 != null) {
                                mm.put(key, var21.getNote(String.valueOf(val)));
                            } else {
                                mm.put(key, "");
                            }
                        }
                    } else if(col.getFormat() != null && col.getFormat().length() > 0) {
                        String val2 = null;
                        if(col.getType() != ColumnType.DATE && col.getType() != ColumnType.DATETIME) {
                            val2 = String.format(col.getFormat(), new Object[]{val});
                            mm.put(key, val2);
                        } else {
                            Date temp = col.getDateValue(m);
                            SimpleDateFormat sdf = new SimpleDateFormat(col.getFormat());
                            if(val != null) {
                                try {
                                    vv = sdf.format(temp);
                                    mm.put(key, vv);
                                } catch (Exception var18) {
                                    ;
                                }
                            } else {
                                mm.put(key, val);
                            }
                        }
                    } else if("id".equals(col.getCode())) {
                        mm.put("_id", val);
                    }
                }
            }
        }

        return li;
    }

    public String getCondition() {
        return this.condition;
    }

    public QmInfo getQmInfo() {
        return this.qmInfo;
    }

    public int getTableWidth() {
        int w = 0;

        for(int i = 0; i < this.getHead().length(); ++i) {
            JSONObject o = this.getHead().getJSONObject(i);
            if(o.getBoolean("show")) {
                w += o.getInt("width");
            }
        }

        return w;
    }

    public void createExcel(List<Map<String, Object>> list, WritableSheet sheet) throws Exception {
        int k = 0;
        ArrayList head = new ArrayList();
        int i = 0;

        int l;
        for(l = this.getColumns().getCols().size(); i < l; ++i) {
            QmColumn v = (QmColumn)this.getColumns().getCols().get(i);
            if(v.isShow()) {
                head.add(v.getCode());
                Label j = new Label(k, 0, v.getDisplay());
                WritableFont s = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
                WritableCellFormat cf = new WritableCellFormat(s);
                j.setCellFormat(cf);
                sheet.addCell(j);
                ++k;
            }
        }

        i = 0;

        for(l = list.size(); i < l; ++i) {
            Map var13 = (Map)list.get(i);
            int var14 = 0;

            for(Iterator var16 = head.iterator(); var16.hasNext(); ++var14) {
                String var15 = (String)var16.next();
                QmColumn qm = this.getColumns().getColumn(var15);
                Label label = new Label(var14, i + 1, Utils.getMapStringValue(var13, qm.getCode()));
                sheet.addCell(label);
            }
        }

    }

    public boolean isHookReplace() {
        return this.hookReplace;
    }

    public String getEditPage() {
        return this.editPage == null && this.getQmInfo() != null?this.getQmInfo().getEditPage():this.editPage;
    }

    public File getEditPageFile() {
        return (this.editPageFile == null || !this.editPageFile.exists()) && this.getQmInfo() != null?this.getQmInfo().getEditPageFile():this.editPageFile;
    }

    public void setEditPageFile(File editPageFile) {
        this.editPageFile = editPageFile;
    }
}
