package com.core.server.task;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.server.Action;
import com.core.server.c.Code;
import com.core.server.db.Column;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.ms.*;
import com.core.server.tools.Utils;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public abstract class QmInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    public static String[] allCodes = new String[]{"codetable", "treecodetable", "csql", "tsql", "sql", "mgo", "cmgo", "tmgo"};
    public static String[] hasValuedCodes = new String[]{"codetable", "treecodetable"};
    public static String[] sqlCodes = new String[]{"csql", "tsql", "sql"};
    public static String[] mgoCodes = new String[]{"mgo", "cmgo", "tmgo"};
    public static Map<String, Code> queryedData = new HashMap();
    public static Map<String, Long> queryedDataTime = new HashMap();
    private String editPage;
    private String paramsStr;
    private String name;
    private String title;
    private User user;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String sql;
    private String description;
    private Columns columns;
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
    private File editPageFile;



    public List<Map<String, Object>> queryData(String taskcode, List<Object> ps,
                                               JSONArray filter, Map<String, Object> p, Columns columns,
                                               User user, String order, String desc,
                                               Action act, boolean export) throws Exception {
        this.columns = columns;
        EntityImpl en = new EntityImpl(this.conn);
        Map m = this.parseSql2(taskcode, ps, filter, p, columns, user, order, desc, this.conn);
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
            return this.queryData(taskcode, ps, filter, p, columns, user, order, desc, act, export);
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
                    e.setP(p);
                    li = this.trimRows(e.getTrimedSet(), act, export);
                } catch (Exception var20) {
                    Logger.warn("Qm hook class:" + this.hookClass.trim() + " processed with error:"
                            + Utils.getErrorStack(var20));
                }
            } else {
                li = this.trimRows(li, act, export);
            }

            return li;
        }
    }

    public List<Map<String, Object>> trimRows(List<Map<String, Object>> values, Action act, boolean export) throws Exception {
        ArrayList li = new ArrayList();
        int i = 0;

        for(int l = values.size(); i < l; ++i) {
            Map m = (Map)values.get(i);
            HashMap mm = new HashMap();
            mm.putAll(m);
            li.add(mm);
            Iterator var10 = m.entrySet().iterator();

            while(var10.hasNext()) {
                Map.Entry en = (Map.Entry)var10.next();
                String key = (String)en.getKey();
                Object val = en.getValue();
                Column col = null;
                if(this.columns != null) {
                    col = this.columns.getColumn(key);
                }

                if(col != null) {
                    Code code = col.getCode(act.getSessionUser(), act.L);
                    if(code != null) {
                        if(col.getBindData() != null && col.getBindData().length() > 0) {
                            mm.put(key + "__qm_code", val);
                            mm.put(key, code.getNote(String.valueOf(val)));
                        }
                    } else if(col.getFormat() != null && col.getFormat().length() > 0) {
                        String val2 = null;
                        if(col.getType() != ColumnType.DATE && col.getType() != ColumnType.DATETIME) {
                            if(export) {
                                if(!"longPrice2".equalsIgnoreCase(col.getFormat()) && !"longPrice".equalsIgnoreCase(col.getFormat())) {
                                    val2 = String.format(col.getFormat(), new Object[]{val});
                                } else {
                                    Long var22 = Long.valueOf(0L);

                                    try {
                                        var22 = Long.valueOf(Long.parseLong(val.toString()));
                                    } catch (Exception var20) {
                                        ;
                                    }

                                    val2 = Utils.toPrice(var22.longValue());
                                }
                            } else if(!"longPrice2".equalsIgnoreCase(col.getFormat())) {
                                val2 = String.format(col.getFormat(), new Object[]{val});
                            } else {
                                long var23 = 0L;

                                try {
                                    Double var24 = Double.valueOf(Double.parseDouble(val.toString()));
                                    var23 = var24.longValue();
                                } catch (Exception var19) {
                                    ;
                                }

                                val2 = Utils.toPrice(var23);
                            }

                            mm.put(key, val2);
                        } else {
                            java.sql.Date v = col.getDateValue(m);
                            SimpleDateFormat sdf = new SimpleDateFormat(col.getFormat());
                            if(val != null) {
                                try {
                                    String temp = sdf.format(v);
                                    mm.put(key, temp);
                                } catch (Exception var21) {
                                    ;
                                }
                            } else {
                                mm.put(key, val);
                            }
                        }
                    } else if("id".equals(col.getName())) {
                        mm.put("_id", val);
                    }
                }
            }
        }

        return li;
    }

    public abstract Map<String, Object> parseSql2(String var1, List<Object> var2, JSONArray var3, Map<String, Object> var4, Columns var5, User var6, String var7, String var8, Connection var9) throws Exception;

    public String getTableAliase(Map<String, Object> p) {
        String tableAliase = "";
        String aliase = Utils.getMapStringValue(p, "aliase");
        if(aliase.trim().length() > 0) {
            tableAliase = aliase.trim() + ".";
        }

        return tableAliase;
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

    public Columns getColumns() {
        return this.columns;
    }

    public void setColumns(Columns columns) {
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

    public long getTotalPages(int total, int pagesize) {
        if(pagesize <= 0) {
            pagesize = 20;
        }

        return total % pagesize == 0?(long)(total / pagesize):(long)(total / pagesize + 1);
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
            Column qc = (Column)this.getColumns().getCols().get(i);
            JSONObject th = new JSONObject();
            th.put("code", qc.getName());
            th.put("display", qc.getComment());
            th.put("fieldName", qc.getField());
            th.put("width", qc.getWidth());
            th.put("show", qc.isShow());
            th.put("ignore", qc.isIgnore());
            th.put("send", true);
            th.put("sort", qc.isSort());
            th.put("bindType", qc.getBindType());
            th.put("bindData", qc.getBindData());
            ths.put(th);
            if(Utils.contains(MsInfo.allCodes, qc.getBindType())) {
                JSONObject qm_th = new JSONObject();
                qm_th.put("code", qc.getName() + "__qm_code");
                qm_th.put("display", qc.getComment());
                qm_th.put("fieldName", qc.getField() + "__qm_code");
                qm_th.put("width", "0");
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
            Column v = (Column)this.getColumns().getCols().get(i);
            if(v.isShow()) {
                head.add(v.getName());
                Label j = new Label(k, 0, v.getComment());
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
                Column qm = this.getColumns().getColumn(var15);
                Label label = new Label(var14, i + 1, Utils.getMapStringValue(var13, qm.getName()));
                sheet.addCell(label);
            }
        }

    }

    public JSONArray getToobars() {
        JSONArray ths = new JSONArray();

        for(int i = 0; i < this.getToolbars().getToolbars().size(); ++i) {
            QmToolbar toolBar = (QmToolbar)this.getToolbars().getToolbars().get(i);
            JSONObject o = toolBar.toJson();
            JSONArray li = new JSONArray();
            if(toolBar.getSubItems().size() > 0) {
                Iterator var7 = toolBar.getSubItems().iterator();

                while(var7.hasNext()) {
                    QmToolbar q = (QmToolbar)var7.next();
                    li.put(q.toJson());
                }
            }

            o.put("li", li);
            ths.put(o);
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
