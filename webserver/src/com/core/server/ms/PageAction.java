package com.core.server.ms;

import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PageAction extends BasicAction {
    @Route(
            value = "/common_query",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void common_query() throws Exception {
        String type = this.getParameter("type");
        String name = this.getParameter("name");
        String paramsStr = this.getParameter("params");
        String startpage = this.getParameter("startpage");
        String pagesize = this.getParameter("pagesize");
        String orderby = this.getParameter("orderby");
        String desc = this.getParameter("desc");
        String filterStr = this.getParameter("filter");
        this.obj.put("name", name);
        this.obj.put("orderby", orderby);
        HashMap p = new HashMap();

        try {
            JSONObject filter = new JSONObject(paramsStr);
            Iterator page = filter.keySet().iterator();

            while(page.hasNext()) {
                String start = (String)page.next();
                Object names = filter.get(start);
                if(!(names instanceof JSONArray)) {
                    if(names instanceof JSONObject) {
                        this.L.error("应该不可能有jsonObject类型的参数:" + names);
                    } else {
                        p.put(start, filter.getString(start));
                    }
                } else {
                    JSONArray taskcode = (JSONArray)names;
                    ArrayList legendcode = new ArrayList();

                    for(int task = 0; task < taskcode.length(); ++task) {
                        legendcode.add(taskcode.getString(task));
                    }

                    p.put(start, legendcode);
                }
            }
        } catch (Exception var21) {
            this.L.error(var21);
        }

        JSONArray var22 = null;

        try {
            var22 = new JSONArray(filterStr);
        } catch (Exception var20) {
            ;
        }

        int var23 = 1;

        try {
            var23 = Integer.parseInt(startpage);
        } catch (Exception var19) {
            ;
        }

        int var24 = 20;

        try {
            var24 = Integer.parseInt(pagesize);
        } catch (Exception var18) {
            ;
        }

        if("qm".equals(type)) {
            this.qmQuery(name, var23, var24, orderby, desc, p, var22, false);
        } else {
            if(!"task".equals(type)) {
                throw new Exception("不清楚的通用查询类型【" + type + "】,目前只支持【ms、qm、task】三种");
            }

            String[] var25 = name.split("___");
            if(var25.length != 2) {
                throw new Exception("task类型的通用查询的name需要用格式【taskname___cqname】");
            }

            String var26 = var25[0];
            String var27 = var25[1];
            TaskInfo var28 = new TaskInfo(var26, this.getSessionUser());
            TaskGridLegend grid = var28.getGridLegend(var27);
            if(grid.isRdb()) {
                grid.query(name, var23, var24, orderby, desc, p, var22, false, this);
            } else {
                grid.query(name, var23, var24, orderby, desc, p, var22, false, this);
            }

            this.obj.put("cds", this.getSessionUser().getCD());
        }

    }

    @Route(
            value = "/common_query-expertExcel",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.Excel
    )
    public void common_query_expert() throws Exception {
        String type = this.getParameter("type");
        String name = this.getParameter("name");
        String paramsStr = this.getParameter("params");
        String startpage = this.getParameter("startpage");
        String pagesize = this.getParameter("pagesize");
        String orderby = this.getParameter("orderby");
        String desc = this.getParameter("desc");
        String filterStr = this.getParameter("filter");
        HashMap p = new HashMap();

        try {
            JSONObject filter = new JSONObject(paramsStr);
            Iterator page = filter.keySet().iterator();

            while(page.hasNext()) {
                String start = (String)page.next();
                Object names = filter.get(start);
                if(!(names instanceof JSONArray)) {
                    if(names instanceof JSONObject) {
                        this.L.error("应该不可能有jsonObject类型的参数:" + names);
                    } else {
                        p.put(start, filter.getString(start));
                    }
                } else {
                    JSONArray taskcode = (JSONArray)names;
                    ArrayList legendcode = new ArrayList();

                    for(int task = 0; task < taskcode.length(); ++task) {
                        legendcode.add(taskcode.getString(task));
                    }

                    p.put(start, legendcode);
                }
            }
        } catch (Exception var21) {
            this.L.error(var21);
        }

        JSONArray var22 = null;

        try {
            var22 = new JSONArray(filterStr);
        } catch (Exception var20) {
            ;
        }

        int var23 = 1;

        try {
            var23 = Integer.parseInt(startpage);
        } catch (Exception var19) {
            ;
        }

        int var24 = 20;

        try {
            var24 = Integer.parseInt(pagesize);
        } catch (Exception var18) {
            ;
        }

        if("qm".equals(type)) {
            this.qmQuery(name, var23, var24, orderby, desc, p, var22, true);
        } else {
            if(!"task".equals(type)) {
                throw new Exception("不清楚的通用查询类型【" + type + "】,目前只支持【ms、qm】两种");
            }

            String[] var25 = name.split("___");
            if(var25.length != 2) {
                throw new Exception("task类型的通用查询的name需要用格式【taskname___cqname】");
            }

            String var26 = var25[0];
            String var27 = var25[1];
            TaskInfo var28 = new TaskInfo(var26, this.getSessionUser());
            TaskGridLegend grid = var28.getGridLegend(var27);
            if(grid.isRdb()) {
                grid.query(name, var23, var24, orderby, desc, p, var22, true, this);
            } else {
                grid.query(name, var23, var24, orderby, desc, p, var22, false, this);
            }
        }

    }

    public static void parserFilter(JSONArray filter, String name, Map<String, Object> p, boolean export, User user, Connection conn, Action act) throws Exception {
        if(filter != null && filter.length() > 0) {
            JSONArray searchs = new JSONArray();

            for(int k = 1; k < 10; ++k) {
                ArrayList temps = new ArrayList();
                JSONArray row = new JSONArray();

                int i;
                JSONObject f;
                for(i = 0; i < filter.length(); ++i) {
                    f = filter.getJSONObject(i);
                    int key = f.getInt("rownum");
                    if(key == k) {
                        temps.add(f);
                    }
                }

                for(i = 0; i < temps.size(); ++i) {
                    f = (JSONObject)temps.get(i);
                    String var32 = f.getString("columnname");
                    String compare = f.getString("compare");
                    String value = "";

                    try {
                        value = f.getString("columnvalue");
                    } catch (Exception var31) {
                        ;
                    }

                    String label = f.getString("label");
                    String type = f.getString("type");
                    String format = null;

                    try {
                        format = f.getString("format");
                    } catch (Exception var30) {
                        ;
                    }

                    String bindType = null;

                    try {
                        bindType = f.getString("bindType");
                    } catch (Exception var29) {
                        ;
                    }

                    String bindData = null;

                    try {
                        bindData = f.getString("bindData");
                    } catch (Exception var28) {
                        ;
                    }

                    JSONObject o = new JSONObject();
                    o.put("name", name + "_" + var32 + "_search_" + i);
                    o.put("compare", compare);
                    o.put("label", label);
                    o.put("value", value != null && value.length() > 0?value:"");
                    if(bindType != null && bindType.length() > 0 && bindData != null && bindData.length() > 0) {
                        if("codetable".equalsIgnoreCase(bindType)) {
                            o.put("type", "code");
                            if(bindData != null && bindData.length() > 0) {
                                Code sql = Codes.code(bindData);
                                sql.toListMap();
                                o.put("bindData", sql.toJsonArray());
                            }
                        } else if(!"csql".equalsIgnoreCase(bindType) && !"sql".equalsIgnoreCase(bindType)) {
                            if((format == null || format.length() <= 0) && !"date".equals(type) && !"datetime".equals(type)) {
                                o.put("type", "text");
                            } else {
                                o.put("type", "date");
                                o.put("label", label);
                            }
                        } else {
                            o.put("type", "code");
                            if(bindData != null && bindData.length() > 0) {
                                String var33 = bindData;
                                Matcher matcher = MsInfo.pattern.matcher(bindData);

                                String code;
                                String val;
                                while(matcher.find()) {
                                    code = matcher.group(1);

                                    try {
                                        val = BeanUtils.getProperty(user, code);
                                        var33 = var33.replace("_{user." + code + "}", val);
                                    } catch (Exception var27) {
                                        ;
                                    }
                                }

                                matcher = MsInfo.paramsPattern.matcher(var33);

                                while(matcher.find()) {
                                    code = matcher.group(1);

                                    try {
                                        val = Utils.getMapStringValue(p, code);
                                        var33 = var33.replace("_{p." + code + "}", val);
                                    } catch (Exception var26) {
                                        ;
                                    }
                                }

                                Code var34 = Codes.sql(var33, conn);
                                var34.toListMap();
                                o.put("bindData", var34.toJsonArray());
                            }
                        }

                        row.put(o);
                    } else {
                        o.put("type", "text");
                        row.put(o);
                    }
                }

                if(row.length() > 0) {
                    searchs.put(row);
                }
            }

            if(!export) {
                act.obj.put("searchs", searchs);
            }
        }

    }

    private void qmQuery(String name, int start, int page, String orderby, String desc, Map<String, Object> p, JSONArray filter, boolean export) throws Exception {
        MsInfo qm = MsInfo.initialize(name, this);
        qm.setAct(this);
        this.obj.put("cq_type", qm.getdType());
        this.obj.put("editpage", qm.getEditPage());
        if(qm.getdType() == DType.NORMAL || qm.getdType() == DType.OLDQM) {
            qm.setCurPage(start);
            qm.setPageSize(page);
            qm.getQmInfo().setConn(this.getConnection());
            if(orderby != null && orderby.length() > 0 && desc != null && !desc.equals("n")) {
                qm.setOrderby(orderby);
                qm.setDesc(desc);
            }

            String nSql = Utils.getMapStringValue(p, "sql");
            if(nSql != null && nSql.length() > 0) {
                qm.setSql(nSql);
            }

            Pattern pattern = Pattern.compile("_\\{user.([\\w]*)\\}");
            Matcher matcher = pattern.matcher(qm.getSql());

            String ps;
            while(matcher.find()) {
                ps = matcher.group(1);

                try {
                    User q = this.getSessionUser();
                    String vals = q.getXX(ps);
                    qm.setSql(qm.getSql().replace("_{user." + ps + "}", vals));
                } catch (Exception var19) {
                    ;
                }
            }

            pattern = Pattern.compile("_\\{p.([\\w]*)\\}");
            matcher = pattern.matcher(qm.getSql());

            while(matcher.find()) {
                ps = matcher.group(1);

                try {
                    qm.setSql(qm.getSql().replace("_{p." + ps + "}", Utils.getMapStringValue(p, ps)));
                } catch (Exception var18) {
                    ;
                }
            }

            List ps1 = Utils.getMapListValue(p, "sqlPs");
            QmInfo q1 = qm.getQmInfo();
            if(qm.getdType() == DType.OLDQM) {
                qm.setTitle(qm.getQmInfo().getTitle());
            } else if(qm.getdType() == DType.NORMAL || qm.getdType() == DType.MGO) {
                q1.setCurPage(qm.getCurPage());
                q1.setPageSize(qm.getPageSize());
                q1.setSql(qm.getSql());
                q1.setColumns(qm.getColumns());
                q1.setPaging(qm.isPaging());
            }

            List vals1 = q1.queryData(ps1, filter, p, q1.getColumns(), this.getSessionUser(), orderby, desc, this.getConnection());
            if(!export) {
                this.obj.put("rows", vals1);
                this.obj.put("total", q1.getTotalSize());
                this.obj.put("title", qm.getTitle());
                this.obj.put("head", qm.getHead());
                this.obj.put("tableWidth", qm.getTableWidth());
                this.obj.put("toolbars", qm.getToobars());
                this.obj.put("pagesize", qm.getPageSize());
                parserFilter(filter, name, p, export, this.getSessionUser(), this.getConnection(), this);
            } else {
                this.response.reset();
                this.response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                this.response.setHeader("Content-Disposition", "attachment; filename=" + new String(qm.getTitle().getBytes("GBK"), "ISO-8859-1") + ".xls");
                this.response.setHeader("progma", "no-cache");
                this.response.setHeader("Cache-Control", "no-cache");
                this.response.setDateHeader("Expires", 0L);
                WritableWorkbook workbook = Workbook.createWorkbook(this.response.getOutputStream());
                WritableSheet sheet = workbook.createSheet("通用导出", 0);
                qm.createExcel(vals1, sheet);
                workbook.write();
                workbook.close();
                this.response.flushBuffer();
            }
        }

    }

    @Route(
            value = "/qmLoad-openDetailDialog/<name>/<_id>",
            conn = true,
            m = {HttpMethod.GET},
            type = ContentType.Forward
    )
    public void qmOpenDetailDialog(String name, String _id) throws Exception {
        MsInfo qm = MsInfo.initialize(name, this);
        if(qm.getQmInfo().getEditPageFile() != null && qm.getEditPageFile().exists()) {
            EntityImpl entity = new EntityImpl(name, this);
            entity.setValue("id", _id);
            entity.search();
            this.request.setAttribute(name, entity);
            this.obj.put("nextpage", qm.getEditPage());
        } else {
            this.L.error("Qm通用查询【" + name + "】没有配置【editPage】属性");
        }

    }
}
