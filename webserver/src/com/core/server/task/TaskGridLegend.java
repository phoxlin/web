package com.core.server.task;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.server.db.Column;
import com.core.server.log.Logger;
import com.core.server.ms.PageAction;
import com.core.server.ms.QmAlign;
import com.core.server.ms.QmToolbar;
import com.core.server.ms.QmToolbars;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskGridLegend extends TaskNormalLegend{
    private int show_col_num = 2;
    private int titleWidth = 90;
    private int spaceWidth = 40;
    private int inputWidth = 170;
    private int winWidth = 700;
    private int winHeight = 500;
    private String hookClass = "";
    private Columns columns = new Columns();
    private QmToolbars toolbars = new QmToolbars();
    private String qmInfoImplClz = Resources.getProperty("qmImplClz", "com.jinhua.server.task.BasicQmInfo");

    public List<String> toFilterIndexs() {
        ArrayList li = new ArrayList();
        ArrayList query = new ArrayList();
        Iterator rownum = this.columns.getCols().iterator();

        while(rownum.hasNext()) {
            Column colnum = (Column)rownum.next();
            if(colnum.getSearch_compare() != null && colnum.getSearch_compare().length() > 0) {
                query.add(colnum);
            }
        }

        if(query.size() > 0) {
            int var9 = 1;
            int var10 = 1;

            for(int i = 0; i < query.size(); ++i) {
                if((var9 - 1) % 4 == 0) {
                    ++var10;
                }

                Column c = (Column)query.get(i);
                JSONObject o = new JSONObject();
                o.put("rownum", var10);
                o.put("colnum", var9++);
                o.put("label", c.getComment());
                o.put("columnname", c.getName());
                if(c.getBindType() != null && !c.getBindType().equals("no")) {
                    o.put("compare", "=");
                    o.put("type", "text");
                    o.put("bindType", c.getBindType());
                    o.put("bindData", c.getBindData());
                } else if(c.getType() != ColumnType.DATE && c.getType() != ColumnType.DATETIME) {
                    o.put("compare", c.getSearch_compare());
                    o.put("type", "text");
                } else {
                    o.put("label", c.getComment() + " 从");
                    o.put("compare", ">=");
                    o.put("type", "date");
                }

                if(c.getType() != ColumnType.DATE && c.getType() != ColumnType.DATETIME) {
                    if(i == query.size() - 1) {
                        li.add(o.toString());
                    } else {
                        li.add(o.toString() + ",");
                    }
                } else {
                    if((var9 - 1) % 4 == 0) {
                        ++var10;
                    }

                    JSONObject t = new JSONObject();
                    t.put("rownum", var10);
                    t.put("colnum", var9++);
                    t.put("label", "到");
                    t.put("columnname", c.getName());
                    t.put("compare", "<=");
                    t.put("type", "date");
                    li.add(o.toString() + ",");
                    if(i == query.size() - 1) {
                        li.add(t.toString());
                    } else {
                        li.add(t.toString() + ",");
                    }
                }
            }
        }

        return li;
    }

    public void query(String name, int start, int page, String orderby, String desc, Map<String, Object> p,
                      JSONArray filter, boolean export, PageAction act) throws Exception {
        QmInfo q = (QmInfo)Class.forName(this.qmInfoImplClz).newInstance();
        q.setSql(this.getInitSql());
        q.setCurPage(start);
        q.setPageSize(page);
        q.setPaging(true);
        q.setConn(act.getConnection());
        q.setColumns(this.columns);
        q.setToolbars(this.toolbars);
        q.setHookClass(this.hookClass);
        if(orderby != null && orderby.length() > 0 && desc != null && !desc.equals("n")) {
            q.setOrder(orderby);
            q.setDesc(desc);
        }

        String nSql = Utils.getMapStringValue(p, "sql");
        if(nSql != null && nSql.length() > 0) {
            q.setSql(nSql);
        }

        Pattern pattern = Pattern.compile("#\\{user.([\\w]*)\\}");
        Matcher matcher = pattern.matcher(q.getSql());

        String ps;
        while(matcher.find()) {
            ps = matcher.group(1);

            try {
                User vals = act.getSessionUser();
                String workbook = vals.getXX(ps);
                q.setSql(q.getSql().replace("#{user." + ps + "}", workbook));
            } catch (Exception var23) {
                ;
            }
        }

        pattern = Pattern.compile("#\\{p.([\\w]*)\\}");
        matcher = pattern.matcher(q.getSql());

        while(matcher.find()) {
            ps = matcher.group(1);

            try {
                q.setSql(q.getSql().replace("#{p." + ps + "}", Utils.getMapStringValue(p, ps)));
            } catch (Exception var22) {
                ;
            }
        }

        Object var24 = new ArrayList();

        try {
            var24 = Utils.getMapListValue(p, "sqlPs");
        } catch (Exception var21) {
            Logger.error(var21);
        }

        List var25 = q.queryData(name, (List)var24, filter, p, q.getColumns(), act.getSessionUser(), orderby, desc, act, export);
        if(q.isPaging()) {
            if(!export) {
                act.obj.put("curpage", q.getCurPage());
            }

            long var26 = q.getTotalPages(q.getTotalSize(), q.getPageSize());
            if(!export) {
                act.obj.put("totalpage", var26);
            }

            ArrayList pages = new ArrayList();

            int ps2;
            for(ps2 = q.getCurPage(); ps2 >= 1; --ps2) {
                if(!pages.contains(Integer.valueOf(ps2))) {
                    pages.add(Integer.valueOf(ps2));
                }

                if(pages.size() >= 5) {
                    break;
                }
            }

            if((long)q.getCurPage() != var26) {
                for(ps2 = q.getCurPage(); (long)ps2 <= var26; ++ps2) {
                    if(!pages.contains(Integer.valueOf(ps2))) {
                        pages.add(Integer.valueOf(ps2));
                    }

                    if(pages.size() >= 10) {
                        break;
                    }
                }
            }

            Collections.sort(pages);
            JSONArray var28 = new JSONArray();

            for(int i = 0; i < pages.size(); ++i) {
                var28.put(pages.get(i));
            }

            if(!export) {
                act.obj.put("pages", var28);
            }
        }

        if(!export) {
            act.obj.put("rows", var25);
            act.obj.put("total", q.getTotalSize());
            act.obj.put("title", this.getName());
            act.obj.put("head", q.getHead());
            act.obj.put("tableWidth", q.getTableWidth());
            act.obj.put("toolbars", q.getToobars());
            act.obj.put("pagesize", q.getPageSize());
            PageAction.parserFilter(filter, name, p, export, act.getSessionUser(), act.getConnection(), act);
        } else {
            act.response.reset();
            act.response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            act.response.setHeader("Content-Disposition", "attachment; filename=" + new String(this.getName().getBytes("GBK"), "ISO-8859-1") + ".xls");
            act.response.setHeader("progma", "no-cache");
            act.response.setHeader("Cache-Control", "no-cache");
            act.response.setDateHeader("Expires", 0L);
            WritableWorkbook var27 = Workbook.createWorkbook(act.response.getOutputStream());
            WritableSheet sheet = var27.createSheet("通用导出", 0);
            q.createExcel(var25, sheet);
            var27.write();
            var27.close();
            act.response.flushBuffer();
        }

    }

    public JSONObject toJson(User user) {
        JSONObject o = super.toJson(user);
        JSONArray cols = new JSONArray();

        for(int toolbars = 0; toolbars < this.columns.getCols().size(); ++toolbars) {
            Column b = (Column)this.columns.getCols().get(toolbars);
            cols.put(b.toJsonConf(user));
        }

        o.put("cols", cols);
        JSONArray var7 = new JSONArray();
        Iterator var6 = this.toolbars.getToolbars().iterator();

        while(var6.hasNext()) {
            QmToolbar var8 = (QmToolbar)var6.next();
            var7.put(var8.toJson());
        }

        o.put("toolbars", var7);
        return o;
    }

    public void addToolbar(QmToolbar bar) {
        this.toolbars.addQmToolbar(bar);
    }

    public TaskGridLegend(Element ele) {
        super(ele);

        try {
            this.show_col_num = Integer.parseInt(ele.attributeValue("column_show_num"));
        } catch (Exception var30) {
            ;
        }

        try {
            this.titleWidth = Integer.parseInt(ele.attributeValue("titleWidth"));
        } catch (Exception var29) {
            ;
        }

        try {
            this.spaceWidth = Integer.parseInt(ele.attributeValue("spaceWidth"));
        } catch (Exception var28) {
            ;
        }

        try {
            this.inputWidth = Integer.parseInt(ele.attributeValue("inputWidth"));
        } catch (Exception var27) {
            ;
        }

        try {
            this.winWidth = Integer.parseInt(ele.attributeValue("winWidth"));
        } catch (Exception var26) {
            ;
        }

        try {
            this.winHeight = Integer.parseInt(ele.attributeValue("winHeight"));
        } catch (Exception var25) {
            ;
        }

        Element hook = (Element)ele.selectSingleNode("./javaFun");
        if(hook != null) {
            String colList = hook.attributeValue("class");
            if(colList != null && !"".equals(colList)) {
                this.hookClass = colList;
            }
        }

        List colList1 = ele.selectNodes(".//column");
        if(colList1 != null && colList1.size() > 0) {
            Iterator t = colList1.iterator();

            while(t.hasNext()) {
                Element toolbars = (Element)t.next();
                this.addCol(TaskDesignerUtils.createColumn(toolbars));
            }
        }

        List toolbars1 = ele.selectNodes("./toolbars/toolbar");
        QmToolbar b;
        if(toolbars1 != null && toolbars1.size() > 0) {
            for(Iterator var6 = toolbars1.iterator(); var6.hasNext(); this.addToolbar(b)) {
                Element t1 = (Element)var6.next();
                String clzz = t1.attributeValue("class");
                String text = t1.attributeValue("text");
                String js = t1.attributeValue("js");
                String name = t1.attributeValue("name");
                String visible_codde = t1.attributeValue("visible_code").toLowerCase();
                b = new QmToolbar();
                b.set_class(clzz);
                b.setText(text);
                b.setJs(js);
                b.setName(name);
                b.setVisibleCodes(visible_codde.split(","));
                List items = t1.selectNodes("./toolbar");
                if(items != null && items.size() > 0) {
                    b.getSubItems().clear();

                    QmToolbar st;
                    for(Iterator var15 = items.iterator(); var15.hasNext(); b.getSubItems().add(st)) {
                        Element item = (Element)var15.next();
                        String _imag = item.attributeValue("class");
                        String _tex = item.attributeValue("text");
                        String _j = item.attributeValue("js");
                        String _nam = item.attributeValue("name");
                        String _alig = item.attributeValue("align");
                        String _rol = item.attributeValue("role");
                        String _visible_cod = item.attributeValue("visible_code");
                        st = new QmToolbar();
                        if(_imag != null && _imag.trim().length() > 0) {
                            st.set_class(_imag.trim());
                        }

                        if(_tex != null && _tex.trim().length() > 0) {
                            st.setText(_tex.trim());
                        }

                        if(_j != null && _j.trim().length() > 0) {
                            st.setJs(_j.trim());
                        }

                        if(_nam != null && _nam.trim().length() > 0) {
                            st.setName(_nam.trim());
                        }

                        if("left".equalsIgnoreCase(_alig)) {
                            st.setAlign(QmAlign.LEFT);
                        } else if("right".equalsIgnoreCase(_alig)) {
                            st.setAlign(QmAlign.RIGHT);
                        } else {
                            st.setAlign(QmAlign.CENTER);
                        }

                        String[] codes;
                        if(_rol != null && _rol.trim().length() > 0) {
                            codes = _rol.trim().split(",");
                            st.setRoles(codes);
                        }

                        if(_visible_cod != null && _visible_cod.trim().length() > 0) {
                            codes = _visible_cod.trim().split(",");
                            st.setVisibleCodes(codes);
                        }
                    }
                }
            }
        }

    }

    public void addCol(Column column) {
        this.columns.getCols().add(column);
    }

    public QmToolbars getToolbars() {
        return this.toolbars;
    }

    public List<TaskRow> getRows() throws Exception {
        throw new Exception("Grid Legend里面没有TaskRow");
    }

    public void addRow(TaskRow row) throws Exception {
        throw new Exception("Grid Legend里面没有TaskRow");
    }

    public Columns getColumns() {
        return this.columns;
    }

    public int getShow_col_num() {
        return this.show_col_num;
    }

    public void setShow_col_num(int show_col_num) {
        this.show_col_num = show_col_num;
    }

    public int getTitleWidth() {
        return this.titleWidth;
    }

    public void setTitleWidth(int titleWidth) {
        this.titleWidth = titleWidth;
    }

    public int getSpaceWidth() {
        return this.spaceWidth;
    }

    public void setSpaceWidth(int spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public int getInputWidth() {
        return this.inputWidth;
    }

    public void setInputWidth(int inputWidth) {
        this.inputWidth = inputWidth;
    }

    public int getWinWidth() {
        return this.winWidth;
    }

    public void setWinWidth(int winWidth) {
        this.winWidth = winWidth;
    }

    public int getWinHeight() {
        return this.winHeight;
    }

    public void setWinHeight(int winHeight) {
        this.winHeight = winHeight;
    }
}
