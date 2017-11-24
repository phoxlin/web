package com.core.server.qm;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.enuts.DBType;
import com.core.server.db.DBUtils;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.ms.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QmInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    private String editPage;
    private String paramsStr;
    private String name;
    private String title;
    private User user;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String sql;
    private String description;
    private QmColumns columns;
    private QmToolbars toolbars;
    private List<String> jsFuns;
    private List<String> onceJsFuns;
    private List<String> jsFunctions = new ArrayList();
    private String mode;
    private boolean paging;
    private boolean refresh;
    private boolean exportExcel = true;
    private boolean calculate = false;
    private String hookClass;
    private int curPage;
    private int pageSize;
    private int totalSize;
    private int totalPages;
    private String order;
    private String desc;
    private StringBuilder calculateStr = new StringBuilder();
    private List<SearchRow> searchs;
    private Connection conn;
    public MsInfo ms;
    private File editPageFile;

    public QmInfo() {
    }

    public List<Map<String, Object>> queryData(List<Object> ps, JSONArray filter, Map<String, Object> p, QmColumns columns, User user, String order, String desc, Connection conn) throws Exception {
        EntityImpl en = new EntityImpl(conn);
        Map m = this.parseSql2(ps, filter, p, columns, user, order, desc, conn);
        String parsedSql = "" + m.get("sql");
        Object[] params = (Object[])m.get("params");
        int start = (this.curPage - 1) * this.pageSize + 1;
        int end = this.curPage * this.pageSize;
        new ArrayList();
        if(start <= 0) {
            start = 1;
        }

        List li;
        if(this.paging && this.curPage >= 0) {
            en.executeQueryWithMaxResult(parsedSql, params, start, end);
            li = en.getValues();
        } else {
            en.executeQuery(parsedSql, params);
            li = en.getValues();
        }

        this.totalSize = en.getMaxResultCount();
        int temp = this.totalSize / this.pageSize;
        this.totalPages = this.totalSize % this.pageSize > 0?temp + 1:(temp > 0?temp:1);
        if(this.curPage > this.totalPages) {
            this.curPage = this.totalPages;
        }

        if(this.totalSize > 0 && li.size() <= 0) {
            this.curPage = this.totalPages;
            return this.queryData(ps, filter, p, columns, user, order, desc, conn);
        } else {
            if(this.hookClass != null && this.hookClass.length() > 0) {
                try {
                    QmHook e = (QmHook)Class.forName(this.hookClass.trim()).newInstance();
                    e.setRawResultSet(li);
                    e.setConn(this.getConn());
                    e.setPagesize(this.getPageSize());
                    e.setCurrentPage(this.getCurPage());
                    e.setPaging(true);
                    e.setUser(this.getUser());
                    li = this.ms.trimRows(e.getTrimedSet(), this.ms.getAct());
                } catch (Exception var18) {
                    Logger.warn("Qm hook class:" + this.hookClass.trim() + " processed with error:" + Utils.getErrorStack(var18));
                }
            } else {
                li = this.ms.trimRows(li, this.ms.getAct());
            }

            return li;
        }
    }

    public Map<String, Object> parseSql2(List<Object> ps, JSONArray filter, Map<String, Object> p, QmColumns columns, User user, String order, String desc, Connection conn) throws Exception {
        HashMap m = new HashMap();
        ArrayList list = new ArrayList();
        list.addAll(ps);
        StringBuilder sb = new StringBuilder();
        boolean changed = false;

        for(int i = 0; i < filter.length(); ++i) {
            JSONObject ss = filter.getJSONObject(i);
            String code = ss.getString("columnname");
            String val = "";

            try {
                val = ss.getString("columnvalue");
            } catch (Exception var20) {
                ;
            }

            String compare = "like";

            try {
                compare = ss.getString("compare");
            } catch (Exception var19) {
                ;
            }

            ColumnType type = columns.getColumnType(code);
            if(val != null && val.length() > 0) {
                if(!changed) {
                    sb.append("select * from (" + this.sql + ") a where ");
                }

                changed = true;
                if(type != ColumnType.DATE && type != ColumnType.DATETIME) {
                    if(type != ColumnType.FLOAT && type != ColumnType.INT && type != ColumnType.LONG) {
                        if("like".equalsIgnoreCase(compare)) {
                            sb.append(code);
                            sb.append(" " + compare + " ?");
                            list.add("%" + val + "%");
                        } else {
                            sb.append(code);
                            sb.append(" " + compare + " ?");
                            list.add(val);
                        }
                    } else {
                        sb.append(code);
                        sb.append(" " + compare);
                        sb.append("?");
                        list.add(val);
                    }
                } else {
                    if(DBUtils.getRDBType() == DBType.Oracle) {
                        sb.append("to_date(to_char(" + code + ",\'yyyy-mm-dd\'),\'yyyy-mm-dd\')");
                        sb.append(" " + compare);
                        sb.append(" to_date(");
                        sb.append("?");
                        sb.append(",\'yyyy-mm-dd\')");
                    } else {
                        sb.append("str_to_date(DATE_FORMAT(" + code + ",\'%Y-%m-%d\'),\'%Y-%m-%d\') ");
                        sb.append(" " + compare + " ?");
                    }

                    list.add(val);
                }

                sb.append(" and ");
            }
        }

        if(changed) {
            this.sql = sb.substring(0, sb.length() - 5);
        }

        if(order != null && !order.equals("null") && order.length() > 0 && !desc.equals("none")) {
            this.sql = "select * from(" + this.sql + ") a order by " + order + " " + desc;
        }

        m.put("sql", this.sql.toString());
        m.put("params", list.toArray());
        return m;
    }

    public static QmInfo initialize(InputStream is, String name) throws Exception {
        QmInfo qm = new QmInfo();
        qm.setName(name);
        SAXReader read = new SAXReader();
        Document doc = read.read(is);
        Element table = doc.getRootElement();
        String title = table.attributeValue("title");
        String pageSize = table.attributeValue("pageSize");
        String description = table.attributeValue("description");
        String editPage = table.attributeValue("editpage");
        if(editPage != null && editPage.length() > 0) {
            File mode = new File(Utils.getWebRootPath() + editPage);
            if(mode.exists()) {
                qm.editPage = editPage;
                qm.editPageFile = mode;
            }
        }

        String var40 = table.attributeValue("mode");
        if(var40 != null && var40.trim().length() > 0) {
            qm.setMode(var40);
        } else {
            qm.setMode("default");
        }

        boolean paging = !"false".equalsIgnoreCase(table.attributeValue("paging"));
        boolean refresh = !"false".equalsIgnoreCase(table.attributeValue("refresh"));
        boolean exportExcel = !"false".equalsIgnoreCase(table.attributeValue("exportExcel"));
        boolean calculate = "true".equalsIgnoreCase(table.attributeValue("calculate"));
        String sql = table.attributeValue("sql");
        if(sql != null && sql.length() > 0) {
            qm.setSql(sql);
        }

        qm.setPaging(paging);
        qm.setRefresh(refresh);
        qm.setExportExcel(exportExcel);
        qm.setCalculate(calculate);
        if(title != null && title.length() > 0) {
            qm.setTitle(title.trim());
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
        } catch (Exception var39) {
            qm.setPageSize(20);
        }

        List toolbarlist = table.selectNodes("./toolbars/toolbar");
        int oncefuns;
        Element javaFun;
        String _class;
        String l;
        String el;
        String method;
        String width;
        String format;
        String reminder;
        if(toolbarlist != null && toolbarlist.size() > 0) {
            QmToolbars columns = new QmToolbars();
            qm.setToolbars(columns);
            int funs = 0;

            for(oncefuns = toolbarlist.size(); funs < oncefuns; ++funs) {
                javaFun = (Element)toolbarlist.get(funs);
                _class = javaFun.attributeValue("class");
                l = javaFun.attributeValue("text");
                el = javaFun.attributeValue("js");
                method = javaFun.attributeValue("name");
                width = javaFun.attributeValue("align");
                format = javaFun.attributeValue("role");
                reminder = javaFun.attributeValue("visible_code");
                QmToolbar align = new QmToolbar();
                columns.addQmToolbar(align);
                if(_class != null && _class.trim().length() > 0) {
                    align.set_class(_class.trim());
                }

                if(l != null && l.trim().length() > 0) {
                    align.setText(l.trim());
                }

                if(el != null && el.trim().length() > 0) {
                    align.setJs(el.trim());
                }

                if(method != null && method.trim().length() > 0) {
                    align.setName(method.trim());
                }

                if("left".equalsIgnoreCase(width)) {
                    align.setAlign(QmAlign.LEFT);
                } else if("right".equalsIgnoreCase(width)) {
                    align.setAlign(QmAlign.RIGHT);
                } else {
                    align.setAlign(QmAlign.CENTER);
                }

                String[] bindType;
                if(format != null && format.trim().length() > 0) {
                    bindType = format.trim().split(",");
                    align.setRoles(bindType);
                }

                if(reminder != null && reminder.trim().length() > 0) {
                    bindType = reminder.trim().split(",");
                    align.setVisibleCodes(bindType);
                }
            }
        }

        List var41 = table.selectNodes("./columns/column");
        int var45;
        if(var41 != null && var41.size() > 0) {
            QmColumns var42 = new QmColumns();
            qm.setColumns(var42);
            oncefuns = 0;

            for(var45 = var41.size(); oncefuns < var45; ++oncefuns) {
                Element var47 = (Element)var41.get(oncefuns);
                l = var47.attributeValue("code");
                el = var47.attributeValue("display");
                method = var47.attributeValue("type");
                width = var47.attributeValue("width");
                format = var47.attributeValue("format");
                reminder = var47.attributeValue("reminder");
                String var53 = var47.attributeValue("align");
                String var54 = var47.attributeValue("bindType");
                String bindData = var47.attributeValue("bindData");
                String fieldName = var47.attributeValue("fieldName");
                boolean show = "true".equalsIgnoreCase(var47.attributeValue("show"));
                boolean send = "true".equalsIgnoreCase(var47.attributeValue("send"));
                boolean sort = !"false".equalsIgnoreCase(var47.attributeValue("sort"));
                boolean ignore = "true".equalsIgnoreCase(var47.attributeValue("ignore"));
                QmColumn c = new QmColumn();
                if(l != null && l.length() > 0) {
                    c.setCode(l.trim());
                    if(fieldName != null && fieldName.length() > 0) {
                        c.setFieldName(fieldName);
                    } else {
                        c.setFieldName(c.getCode());
                    }
                }

                c.setIgnore(ignore);
                c.setSort(sort);
                if(var54 != null) {
                    c.setBindtype(var54);
                    if(bindData != null && bindData.length() > 0) {
                        c.setBinddata(bindData);
                    }
                } else {
                    c.setBindtype("no");
                }

                if(el != null && el.length() > 0) {
                    c.setDisplay(el.trim());
                } else {
                    c.setDisplay(c.getCode());
                }

                if("long".equalsIgnoreCase(method)) {
                    c.setType(ColumnType.LONG);
                } else if("int".equalsIgnoreCase(method)) {
                    c.setType(ColumnType.INT);
                } else if("date".equalsIgnoreCase(method)) {
                    c.setType(ColumnType.DATE);
                } else if("float".equalsIgnoreCase(method)) {
                    c.setType(ColumnType.FLOAT);
                } else if("datetime".equalsIgnoreCase(method)) {
                    c.setType(ColumnType.DATETIME);
                } else {
                    c.setType(ColumnType.STRING);
                }

                if("left".equalsIgnoreCase(var53)) {
                    c.setAlign(QmAlign.LEFT);
                } else if("right".equalsIgnoreCase(var53)) {
                    c.setAlign(QmAlign.RIGHT);
                } else {
                    c.setAlign(QmAlign.CENTER);
                }

                if(format != null && format.length() > 0) {
                    c.setFormat(format.trim());
                }

                try {
                    c.setWidth(Integer.parseInt(width.trim()));
                } catch (Exception var38) {
                    c.setWidth(100);
                }

                if(reminder != null && reminder.length() > 0) {
                    c.setReminder(reminder.trim());
                } else {
                    c.setReminder("点击排序");
                }

                c.setSend(send);
                c.setShow(show);
                var42.addColumn(c);
            }
        }

        List var43 = table.selectNodes("./jsFuns/fun");
        int var49;
        if(var43 != null && var43.size() > 0) {
            ArrayList var44 = new ArrayList();
            qm.setJsFuns(var44);
            var45 = 0;

            for(var49 = var43.size(); var45 < var49; ++var45) {
                Element var50 = (Element)var43.get(var45);
                el = var50.attributeValue("method");
                if(el != null && el.trim().length() > 0) {
                    var44.add(el);
                }
            }
        }

        List var46 = table.selectNodes("./onceJsFuns/fun");
        if(var46 != null && var46.size() > 0) {
            ArrayList var48 = new ArrayList();
            qm.setOnceJsFuns(var48);
            var49 = 0;

            for(int var51 = var46.size(); var49 < var51; ++var49) {
                Element var52 = (Element)var46.get(var49);
                method = var52.attributeValue("method");
                if(method != null && method.trim().length() > 0) {
                    var48.add(method);
                }
            }
        }

        javaFun = (Element)table.selectSingleNode("./javaFun");
        if(javaFun != null) {
            _class = javaFun.attributeValue("class");
            if(_class != null && _class.trim().length() > 0) {
                qm.setHookClass(_class.trim());
            }
        }

        return qm;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public QmColumns getColumns() {
        return this.columns;
    }

    public void setColumns(QmColumns columns) {
        this.columns = columns;
    }

    public int getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
        int temp = this.totalSize / this.pageSize;
        this.totalPages = this.totalSize % this.pageSize > 0?temp + 1:(temp > 0?temp:1);
    }

    public int getCurPage() {
        return this.curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<String> getJsFuns() {
        return this.jsFuns;
    }

    public void setJsFuns(List<String> jsFuns) {
        this.jsFuns = jsFuns;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public QmToolbars getToolbars() {
        if(this.toolbars == null) {
            this.toolbars = new QmToolbars();
        }

        return this.toolbars;
    }

    public void setToolbars(QmToolbars toolbars) {
        this.toolbars = toolbars;
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

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<SearchRow> getRows() {
        if(this.searchs == null) {
            this.searchs = new ArrayList();
        }

        return this.searchs;
    }

    public void setRows(List<SearchRow> rows) {
        this.searchs = rows;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getJsFunctions() {
        return this.jsFunctions;
    }

    public Connection getConn() {
        return this.conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isExportExcel() {
        return this.exportExcel;
    }

    public void setExportExcel(boolean exportExcel) {
        this.exportExcel = exportExcel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCalculate() {
        return this.calculate;
    }

    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }

    public String getHookClass() {
        return this.hookClass;
    }

    public void setHookClass(String hookClass) {
        this.hookClass = hookClass;
    }

    public User getUser() {
        return this.user;
    }

    public StringBuilder getCalculateStr() {
        return this.calculateStr;
    }

    public List<String> getOnceJsFuns() {
        return this.onceJsFuns;
    }

    public void setOnceJsFuns(List<String> onceJsFuns) {
        this.onceJsFuns = onceJsFuns;
    }

    public void setCalculateStr(StringBuilder calculateStr) {
        this.calculateStr = calculateStr;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getParamsStr() {
        return this.paramsStr;
    }

    public void setParamsStr(String paramsStr) {
        this.paramsStr = paramsStr;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
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
            ths.put(th);
            if(Utils.contains(MsInfo.allCodes, qc.getBindtype())) {
                JSONObject qm_th = new JSONObject();
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
            bar.put("roles", toolBar.getRoles());
            bar.put("visibleCodes", toolBar.getVisibleCodes());
            ths.put(bar);
        }

        return ths;
    }

    public String getEditPage() {
        return this.editPage;
    }

    public void setEditPage(String editPage) {
        this.editPage = editPage;
    }

    public File getEditPageFile() {
        return this.editPageFile;
    }

    public void setEditPageFile(File editPageFile) {
        this.editPageFile = editPageFile;
    }
}
