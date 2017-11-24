package com.core.server.tools;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class UI {
    public static String chooseUser(String name, String defaultValue, boolean required, String otherset, int number, String type) throws Exception {
        String userType = "";
        String userRole = "";
        if(otherset != null && otherset.length() > 0) {
            String[] s = otherset.split("-");
            if(s.length > 0) {
                userType = s[0];
            }

            if(s.length > 1) {
                userRole = s[1];
            }
        }

        StringBuilder var27 = new StringBuilder();
        String viewUrl = User.getUserUrl(userType, URLS.VIEW_URL);
        String chooseUrl = User.getUserUrl(userType, URLS.CHOOSE_URL);
        if(defaultValue != null && defaultValue.length() > 0) {
            Connection sb = null;
            DBM db = new DBM();

            try {
                sb = db.getConnection();
                sb.setAutoCommit(true);
                String e = Resources.getProperty("UserImpl");
                UserImpl impl = null;
                List users = null;

                int i;
                CItem ci;
                String id;
                String name1;
                try {
                    impl = (UserImpl)Class.forName(e).newInstance();
                    users = impl.getUserListBySet(userType, defaultValue, sb);
                    if(users != null && users.size() > 0) {
                        for(i = 0; i < users.size(); ++i) {
                            ci = (CItem)users.get(i);
                            id = ci.getCode();
                            name1 = ci.getNote();
                            var27.append(" <a id=\'" + id + "_1\' href=\'" + viewUrl + "?id=" + id + "\'>" + name1 + "</a>");
                            var27.append(" <img id=\'" + id + "_2\' style=\'cursor:pointer\' src=\'public/jQuery/easyui/css/images/tabs_close.gif\' onclick=\"delChooseUser(\'" + id + "\',\'" + name + "\')\" >");
                        }
                    }
                } catch (Exception var24) {
                    Logger.warn("系统没有配置相关的用户管理类：【UserImpl】");
                }

                if(users != null && users.size() > 0) {
                    for(i = 0; i < users.size(); ++i) {
                        ci = (CItem)users.get(i);
                        id = ci.getCode();
                        name1 = ci.getNote();
                        var27.append(" <a id=\'" + id + "_1\' href=\'" + viewUrl + "?id=" + id + "\'>" + name1 + "</a>");
                        var27.append(" <img id=\'" + id + "_2\' style=\'cursor:pointer\' src=\'public/jQuery/easyui/css/images/tabs_close.gif\' onclick=\"delChooseUser(\'" + id + "\',\'" + name + "\')\" >");
                    }
                }
            } catch (Exception var25) {
                throw var25;
            } finally {
                db.freeConnection(sb);
            }
        } else {
            defaultValue = "";
        }

        StringBuilder var28 = new StringBuilder();
        if(!"detail".equalsIgnoreCase(type)) {
            var28.append("<input type=\"button\" value=\"选择\" onclick=\"openChooseUsers(\'" + name + "\',\'" + chooseUrl + "\',\'" + userRole + "\'," + number + ")\">");
        }

        var28.append("<input class=\"easyui-validatebox\" style=\"width:0px;height:0px;border:0px;\" readonly=\"readonly\" type=\"text\" name=\"" + name + "\"  id=\"" + name + "\"  data-options=\"required:" + required + "\" value=\'" + defaultValue + "\'/>");
        var28.append("<span id=\"desc_" + name + "\" style=\"display: inline;\">");
        var28.append(var27);
        var28.append("</span>");
        return var28.toString();
    }

    public static String createUploadQiNiuFile(String name, String defaultValue, boolean required, String exts, int number, boolean isPic, String type) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<input id=\"" + name + "\" name=\"" + name + "\" type=\"hidden\" value=\"" + defaultValue + "\">");
        sb.append("<a href=\"javascript:uploadFile(\'" + name + "\',\'" + exts + "\'," + number + ");\" class=\"btn btn-xs btn-default btn-block\" style=\"width: 100px;\">上传文件</a>");
        sb.append("<div id=\"_" + name + "\" name=\"_" + name + "\"><a href=\'" + defaultValue + "\' target=\'_blank\'>");
        if(isPic) {
            sb.append("<img src=\'" + defaultValue + "?imageView2/1/w/50/h/50\'>");
        }

        sb.append("</a></div>");
        return sb.toString();
    }

    public static String createUploadFile(String name, String defaultValue, boolean required, String exts, int number, boolean isPic, String type) throws Exception {
        StringBuilder s = new StringBuilder();
        if(defaultValue != null && defaultValue.length() > 0) {
            Connection sb = null;
            DBM db = new DBM();

            try {
                sb = db.getConnection();
                sb.setAutoCommit(true);
                EntityImpl e = new EntityImpl(sb);
                int size = e.executeQuery("select a.id,a.filename from sys_file a where a.id in (\'" + defaultValue.replace(",", "\',\'") + "\')");

                for(int i = 0; i < size; ++i) {
                    String id = e.getStringValue("id", i);
                    String name1 = e.getStringValue("filename", i);
                    s.append(" <a id=\'" + id + "_1\' href=\'public/pub/upload/down.jsp?id=" + id + "\'>" + name1 + "</a>");
                    s.append(" <img id=\'" + id + "_2\' style=\'cursor:pointer\' src=\'public/jQuery/easyui/css/images/tabs_close.gif\' onclick=\"deleteFile(\'" + id + "\',\'" + name + "\')\" >");
                }
            } catch (Exception var18) {
                throw var18;
            } finally {
                db.freeConnection(sb);
            }
        } else {
            defaultValue = "";
        }

        StringBuilder var20 = new StringBuilder();
        if(!"detail".equalsIgnoreCase(type)) {
            if(isPic) {
                var20.append("<input type=\"button\" value=\"上传图片\" onclick=\"uploadFiles(\'" + name + "\',\'" + exts + "\'," + number + ",true)\">");
            } else {
                var20.append("<input type=\"button\" value=\"上传文件\" onclick=\"uploadFiles(\'" + name + "\',\'" + exts + "\'," + number + ",false)\">");
            }
        }

        var20.append("<input class=\"easyui-validatebox\" style=\"width:0px;height:0px;border:0px;\" readonly=\"readonly\" type=\"text\" name=\"" + name + "\"  id=\"" + name + "\"  data-options=\"required:" + required + "\" value=\'" + defaultValue + "\'/>");
        var20.append("<span id=\"desc_" + name + "\" style=\"display: inline;\">");
        var20.append(s);
        var20.append("</span>");
        return var20.toString();
    }

    public static String createEditor(String name, String defaultValue, boolean required, UI_Op uiOp) {
        boolean custom = false;
        String[] items = new String[0];
        if(uiOp.getProps() != null && uiOp.getProps().length() > 0) {
            custom = true;
            String sb = uiOp.getProps();
            items = sb.split(",");
        }

        StringBuilder sb1 = new StringBuilder();
        sb1.append("<textarea id=\"textarea_" + name + "\" name=\"textarea_" + name + "\" style=\"" + uiOp.getStyle() + ";visibility:hidden;\">" + defaultValue + "</textarea>\r\n");
        sb1.append("<input type=\"" + (required?"text":"hidden") + "\" style=\"width:0px;height:0px;\" readonly=\"readonly\" name=\"" + name + "\" id=\"" + name + "\" class=\"easyui-validatebox\" data-options=\"required:" + required + "\" />\r\n");
        sb1.append("<script type=\"text/javascript\">\r\n");
        sb1.append(" var " + name + "_editor;" + "KindEditor.ready(function(K) {" + name + "_editor = K.create(\'textarea[name=\"textarea_" + name + "\"]\', " + "{uploadJson : \'public/js/kindeditor/upload.jsp\',\tallowImageUpload : true,items : [\'" + (custom?Utils.getListString(items, "\',\'"):"fontname\', \'fontsize\', \'forecolor\', \'hilitecolor\', \'bold\', \'underline\',\'removeformat\',\'justifyleft\', \'justifycenter\', \'justifyright\', \'insertorderedlist\',\'insertunorderedlist\', \'emoticons\', \'image\', \'link\',\'table\', \'source") + "\']" + ",afterChange : function() {\t$(\'#" + name + "\').val(this.html());}});});\r\n");
        sb1.append("</script>\r\n");
        return sb1.toString();
    }

    public static String createDateTimeBox(String name, String defaultValue, boolean required, String style) {
        return required?"<input id=\"" + name + "\" name=\"" + name + "\" type=\"text\"  editable=false class=\"easyui-datetimebox\" style=\"" + style + "\" required=\"required\" value=\"" + defaultValue + "\"></input>":"<input id=\"" + name + "\" name=\"" + name + "\" type=\"text\"  editable=false class=\"easyui-datetimebox\" style=\"" + style + "\" value=\"" + defaultValue + "\"></input>";
    }

    public static String createDateTimeBox(String id, String name, String defaultValue, boolean required, String style) {
        return required?"<input id=\"" + id + "\" name=\"" + name + "\" type=\"text\"   editable=false class=\"easyui-datetimebox\" style=\"" + style + "\" required=\"required\" value=\"" + defaultValue + "\"></input>":"<input id=\"" + id + "\" name=\"" + name + "\" type=\"text\" editable=false class=\"easyui-datetimebox\" style=\"" + style + "\" value=\"" + defaultValue + "\"></input>";
    }

    public static String createDateBox(String name, String defaultValue, boolean required, String style) {
        return required?"<input id=\"" + name + "\" name=\"" + name + "\" type=\"text\" class=\"easyui-datebox\" editable=false style=\"" + style + "\" required=\"required\"  value=\"" + defaultValue + "\"></input>":"<input id=\"" + name + "\" name=\"" + name + "\" type=\"text\" class=\"easyui-datebox\" editable=false style=\"" + style + "\"   value=\"" + defaultValue + "\"></input>";
    }

    public static String createDateBox(String id, String name, String defaultValue, boolean required, String style) {
        return required?"<input id=\"" + id + "\" name=\"" + name + "\" type=\"text\" class=\"easyui-datebox\"  style=\"" + style + "\" required=\"required\" value=\"" + defaultValue + "\"></input>":"<input id=\"" + id + "\" name=\"" + name + "\" type=\"text\" class=\"easyui-datebox\"  style=\"" + style + "\" value=\"" + defaultValue + "\"></input>";
    }

    public static String createCombinedTree(String name, String mainSql, String leafSql, boolean required, boolean multiple, boolean linked, boolean leafOnly, String defaultValue, String style) {
        if(style == null) {
            style = "";
        }

        StringBuilder sb = new StringBuilder("<input  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" " + (multiple?"multiple":"") + " data-options=\"");
        sb.append("animate:true,cascadeCheck:" + linked + ",onlyLeafCheck:" + leafOnly + ",");
        if(leafOnly) {
            sb.append("onBeforeSelect: function(rec){var leaf=$(\'#" + name + "\').tree(\'isLeaf\',rec.target); if(!leaf){return false;}},");
        }

        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        if(defaultValue == null) {
            defaultValue = "";
        }

        String[] temps = defaultValue.split(",");
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(mainSql);
            List list = e.getValues();
            Object leafs = null;

            try {
                e.executeQuery(leafSql, 0, 0);
                leafs = e.getValues();
            } catch (Exception var37) {
                leafs = new ArrayList();
            }

            if(list.size() > 0) {
                int i;
                int l;
                Map leaf;
                String parent_id;
                int k;
                int m;
                if(((Map)list.get(0)).keySet().size() == 2) {
                    i = 0;

                    for(l = 0; l < list.size(); ++l) {
                        leaf = (Map)list.get(l);
                        parent_id = String.valueOf(leaf.get("code"));
                        String fond = String.valueOf(leaf.get("note"));
                        if(hasChildren(parent_id, (List)leafs)) {
                            if(i != 0) {
                                sb.append(",");
                            }

                            ++i;
                            sb.append("{");
                            sb.append("id:\'" + parent_id + "\',text:\'" + fond + "\'");
                            if(Utils.contains(temps, parent_id)) {
                                sb.append(",\'checked\':true");
                            }

                            sb.append(",children:[");
                            k = 0;
                            m = 0;

                            for(int li = ((List)leafs).size(); m < li; ++m) {
                                Map id = (Map)((List)leafs).get(m);
                                String parent_id1 = Utils.getMapStringValue(id, "parent_id");
                                String code2 = Utils.getMapStringValue(id, "code");
                                String note2 = Utils.getMapStringValue(id, "note");
                                if(parent_id.equals(parent_id1)) {
                                    if(k != 0) {
                                        sb.append(",");
                                    }

                                    sb.append("{");
                                    sb.append("id:\'" + code2 + "\',text:\'" + note2 + "\'");
                                    if(Utils.contains(temps, code2)) {
                                        sb.append(",\'checked\':true");
                                    }

                                    sb.append("}");
                                    ++k;
                                }
                            }

                            sb.append("]");
                            sb.append("}");
                        }
                    }
                } else {
                    list = treeDataTrim(list);
                    if(((List)leafs).size() > 0) {
                        i = 0;

                        for(l = ((List)leafs).size(); i < l; ++i) {
                            leaf = (Map)((List)leafs).get(i);
                            parent_id = Utils.getMapStringValue(leaf, "parent_id");
                            boolean var40 = false;
                            k = 0;

                            for(m = list.size(); k < m; ++k) {
                                Map var41 = (Map)list.get(k);
                                String var42 = Utils.getMapStringValue(var41, "id");
                                if(var42.equals(parent_id)) {
                                    var40 = true;
                                    break;
                                }
                            }

                            if(var40) {
                                leaf.put("id", "" + UUID.randomUUID());
                                list.add(leaf);
                            }
                        }
                    }

                    parserTreeData(temps, sb, "-1", list, false);
                }
            }
        } catch (Exception var38) {
            Logger.error(var38);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var36) {
                ;
            }

        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createCombinedTreePage(String name, String mainSql, String leafSql, boolean multiple, boolean linked, boolean leafOnly, String defaultValue, String style) {
        if(style == null) {
            style = "";
        }

        StringBuilder sb = new StringBuilder("<ul  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-tree\"  data-options=\"");
        sb.append((multiple?"checkbox:true":"checkbox:false") + ", animate:true,cascadeCheck:" + linked + ",onlyLeafCheck:" + leafOnly + ",");
        if(leafOnly) {
            sb.append("onBeforeSelect: function(rec){var leaf=$(\'#" + name + "\').tree(\'isLeaf\',rec.target); if(!leaf){return false;}},");
        }

        sb.append("data:[");
        if(defaultValue == null) {
            defaultValue = "";
        }

        String[] temps = defaultValue.split(",");
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(mainSql);
            List list = e.getValues();
            Object leafs = null;

            try {
                e.executeQuery(leafSql);
                leafs = e.getValues();
            } catch (Exception var36) {
                leafs = new ArrayList();
            }

            if(list.size() > 0) {
                int i;
                int l;
                Map leaf;
                String parent_id;
                int k;
                int m;
                String code;
                if(((Map)list.get(0)).keySet().size() == 2) {
                    i = 0;

                    for(l = 0; l < list.size(); ++l) {
                        leaf = (Map)list.get(l);
                        parent_id = String.valueOf(leaf.get("code"));
                        String fond = String.valueOf(leaf.get("note"));
                        if(hasChildren(parent_id, (List)leafs)) {
                            if(i != 0) {
                                sb.append(",");
                            }

                            ++i;
                            sb.append("{");
                            sb.append("id:\'" + parent_id + "\',text:\'" + fond + "\'");
                            if(Utils.contains(temps, parent_id)) {
                                sb.append(",\'checked\':true");
                            }

                            sb.append(",children:[");
                            k = 0;
                            m = 0;

                            for(int li = ((List)leafs).size(); m < li; ++m) {
                                Map id = (Map)((List)leafs).get(m);
                                code = Utils.getMapStringValue(id, "parent_id");
                                String code2 = Utils.getMapStringValue(id, "code");
                                String note2 = Utils.getMapStringValue(id, "note");
                                if(parent_id.equals(code)) {
                                    if(k != 0) {
                                        sb.append(",");
                                    }

                                    sb.append("{");
                                    sb.append("id:\'" + code2 + "\',text:\'" + note2 + "\'");
                                    if(Utils.contains(temps, code2)) {
                                        sb.append(",\'checked\':true");
                                    }

                                    sb.append("}");
                                    ++k;
                                }
                            }

                            sb.append("]");
                            sb.append("}");
                        }
                    }
                } else {
                    list = treeDataTrim(list);
                    if(((List)leafs).size() > 0) {
                        i = 0;

                        for(l = ((List)leafs).size(); i < l; ++i) {
                            leaf = (Map)((List)leafs).get(i);
                            parent_id = Utils.getMapStringValue(leaf, "parent_id");
                            boolean var39 = false;
                            k = 0;

                            for(m = list.size(); k < m; ++k) {
                                Map var40 = (Map)list.get(k);
                                String var41 = Utils.getMapStringValue(var40, "id");
                                code = Utils.getMapStringValue(var40, "code");
                                if(code.equals(parent_id)) {
                                    var39 = true;
                                    leaf.put("parent_id", var41);
                                    break;
                                }
                            }

                            if(var39) {
                                leaf.put("id", "" + UUID.randomUUID());
                                list.add(leaf);
                            }
                        }
                    }

                    parserTreeData(temps, sb, "-1", list, true);
                }
            }
        } catch (Exception var37) {
            Logger.error(var37);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var35) {
                ;
            }

        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static List<Map<String, Object>> treeDataTrim(List<Map<String, Object>> list) {
        ArrayList val = new ArrayList();
        int i = 0;

        for(int l = list.size(); i < l; ++i) {
            Map li = (Map)list.get(i);
            String oldId = Utils.getMapStringValue(li, "id");
            String parent_id = Utils.getMapStringValue(li, "parent_id");
            if(!"-1".equals(parent_id)) {
                boolean fond = false;

                for(int k = 0; k < l; ++k) {
                    Map m = (Map)list.get(k);
                    String id = Utils.getMapStringValue(m, "id");
                    if(!oldId.equals(id) && id.equals(parent_id)) {
                        fond = true;
                        break;
                    }
                }

                if(!fond) {
                    li.put("parent_id", "-1");
                }
            }

            val.add(li);
        }

        return val;
    }

    private static void parserTreeData(String[] defaultValues, StringBuilder sb, String pId, List<Map<String, Object>> list, boolean page) {
        int k = 0;

        for(int i = 0; i < list.size(); ++i) {
            Map m = (Map)list.get(i);
            String code = String.valueOf(m.get("code"));
            String note = String.valueOf(m.get("note"));
            String parent = String.valueOf(m.get("parent_id"));
            String sort = String.valueOf(m.get("sort"));
            String id = String.valueOf(m.get("id"));
            if(pId.equals(parent)) {
                if(k != 0) {
                    sb.append(",");
                }

                ++k;
                sb.append("{");
                String newId = code;
                if(page) {
                    newId = id + "__" + code + "__" + note + "__" + sort;
                }

                sb.append("id:\'" + newId + "\',text:\'" + note + "\'");
                if(Utils.contains(defaultValues, code)) {
                    sb.append(",checked:true");
                }

                if(hasChildren(id, list)) {
                    sb.append(",children:[");
                    parserTreeData(defaultValues, sb, id, list, page);
                    sb.append("]");
                }

                sb.append("}");
            }
        }

    }

    public static String createLeafTreeBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<select  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        sb.append("animate:true,cascadeCheck:false,onlyLeafCheck:true,");
        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(sql);
            List list = e.getValues();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var19) {
            Logger.error(var19);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var18) {
                ;
            }

        }

        sb.append("]\" ></select>");
        return sb.toString();
    }

    public static String createMultiLeafTreeBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<select  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" multiple data-options=\"");
        sb.append("animate:true,cascadeCheck:false,onlyLeafCheck:true,");
        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(sql);
            List list = e.getValues();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var19) {
            Logger.error(var19);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var18) {
                ;
            }

        }

        sb.append("]\" ></select>");
        return sb.toString();
    }

    public static String createTree(String name, String type, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        if(defaultValue == null || defaultValue.equals("-1")) {
            defaultValue = "";
        }

        StringBuilder sb = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" value=\"" + defaultValue + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        try {
            Code e = Codes.code(type);
            if(e == null) {
                throw new Exception("系统找不到对应的码表:" + type + ",如果你确定系统有,请试着初始化系统");
            }

            List list = e.toListMap();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(sb, "-1", list);
            }
        } catch (Exception var8) {
            Logger.error(var8);
        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createTreeBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        if(defaultValue == null || defaultValue.equals("-1")) {
            defaultValue = "";
        }

        StringBuilder sb = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" value=\"" + defaultValue + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(sql);
            List list = e.getValues();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(sb, "-1", list);
            }
        } catch (Exception var18) {
            Logger.error(var18);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var17) {
                ;
            }

        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createTreeByData(String name, String binddata, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        if(defaultValue == null || defaultValue.equals("-1")) {
            defaultValue = "";
        }

        StringBuilder sb = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" value=\"" + defaultValue + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        if(required) {
            sb.append("required:true,data:[");
        } else {
            sb.append("required:false,data:[");
        }

        ArrayList list = new ArrayList();
        if(binddata != null && binddata.length() > 0) {
            String[] li = binddata.split(",");
            int temp = 0;
            String[] var12 = li;
            int var11 = li.length;

            for(int var10 = 0; var10 < var11; ++var10) {
                String l = var12[var10];
                ++temp;
                if(l != null && l.length() > 0) {
                    String[] xx = l.split(":");
                    if(xx.length == 4) {
                        HashMap m = new HashMap();
                        m.put("id", xx[0]);
                        m.put("parent_id", xx[1]);
                        m.put("code", xx[2]);
                        m.put("note", xx[3]);
                        m.put("sort", Integer.valueOf(temp));
                        list.add(m);
                    }
                }
            }
        }

        if(list.size() > 0) {
            List var15 = treeDataTrim(list);
            parserTreeData(sb, "-1", var15);
        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createLinkedTree(String name, String type, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        if(required) {
            sb.append("required:true,multiple:true,data:[");
        } else {
            sb.append("required:false,multiple:true,data:[");
        }

        try {
            Code e = Codes.code(type);
            if(e == null) {
                throw new Exception("系统找不到对应的码表:" + type + ",如果你确定系统有,请试着初始化系统");
            }

            List list = e.toListMap();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var9) {
            Logger.error(var9);
        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createLinkedTreeBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" data-options=\"");
        if(required) {
            sb.append("required:true,multiple:true,data:[");
        } else {
            sb.append("required:false,multiple:true,data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(sql);
            List list = e.getValues();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var19) {
            Logger.error(var19);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var18) {
                ;
            }

        }

        sb.append("]\" />");
        return sb.toString();
    }

    public static String createMultiTree(String name, String type, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<select  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" multiple data-options=\"");
        if(required) {
            sb.append("required:true,cascadeCheck:false,data:[");
        } else {
            sb.append("required:false,cascadeCheck:false,data:[");
        }

        try {
            Code e = Codes.code(type);
            if(e == null) {
                throw new Exception("系统找不到对应的码表:" + type + ",如果你确定系统有,请试着初始化系统");
            }

            List list = e.toListMap();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var9) {
            Logger.error(var9);
        }

        sb.append("]\" ></select>");
        return sb.toString();
    }

    public static String createMultiTreeByData(String name, String data, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<select  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" multiple data-options=\"");
        if(required) {
            sb.append("required:true,cascadeCheck:false,data:[");
        } else {
            sb.append("required:false,cascadeCheck:false,data:[");
        }

        String[] datas = data.split(",");
        int size = datas.length;
        if(size > 0) {
            ArrayList list = new ArrayList();

            for(int i = 0; i < size; ++i) {
                String d = datas[i];
                String[] ds = d.split(":");
                String id = d;
                String pid = "-1";
                String code = d;
                String value = d;
                String sort = String.valueOf(i);
                if(ds.length == 4) {
                    id = ds[0];
                    pid = ds[1];
                    code = ds[2];
                    value = ds[3];
                }

                HashMap m = new HashMap();
                m.put("id", id);
                m.put("parent_id", pid);
                m.put("code", code);
                m.put("note", value);
                m.put("sort", sort);
                list.add(m);
            }

            if(list.size() > 0) {
                List var19 = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", var19, false);
            }
        }

        sb.append("]\" ></select>");
        return sb.toString();
    }

    public static String createMultiTreeBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && !defaultValue.equals("-1") && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder sb = new StringBuilder("<select  id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combotree\" multiple data-options=\"");
        if(required) {
            sb.append("required:true,cascadeCheck:false,data:[");
        } else {
            sb.append("required:false,cascadeCheck:false,data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            e.executeQuery(sql);
            List list = e.getValues();
            if(list.size() > 0) {
                list = treeDataTrim(list);
                parserTreeData(temps, sb, "-1", list, false);
            }
        } catch (Exception var19) {
            Logger.error(var19);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var18) {
                ;
            }

        }

        sb.append("]\" ></select>");
        return sb.toString();
    }

    public static String createSelectByData(String name, String data, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String data_op = null;
        if(required) {
            data_op = "editable:false,multiple:false,validType:\'myRequired[1]\'";
        } else {
            data_op = "editable:false,multiple:false,validType:\'myRequired[0]\'";
        }

        Element el = DocumentHelper.createElement("select");
        el.addAttribute("id", name);
        el.addAttribute("name", name);
        el.addAttribute("class", "easyui-combobox form-control");
        el.addAttribute("style", style);
        el.addAttribute("data-options", data_op);

        try {
            String[] e = data.split(",");
            int size = e.length;
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    String d = e[i];
                    String[] ds = d.split(":");
                    String code = d;
                    String value = d;
                    if(ds.length == 2) {
                        code = ds[0];
                        value = ds[1];
                    }

                    Element op = el.addElement("option");
                    op.addAttribute("value", code);
                    op.setText(value);
                    if(code.equals(defaultValue)) {
                        op.addAttribute("selected", "selected");
                    }
                }
            }
        } catch (Exception var15) {
            Logger.error(var15);
        }

        return el.asXML();
    }

    public static String createSelect(String name, String type, String defaultValue, boolean required, String ops) {
        JSONObject obj = null;

        try {
            obj = new JSONObject(ops);
        } catch (Exception var21) {
            ;
        }

        String style = "";
        boolean edit = false;

        try {
            style = obj.getString("style");
        } catch (Exception var20) {
            ;
        }

        try {
            edit = obj.getBoolean("edit");
        } catch (Exception var19) {
            ;
        }

        String data_op = null;
        if(required) {
            data_op = "editable:" + edit + ",multiple:false,validType:\'myRequired[1]\'";
        } else {
            data_op = "editable:" + edit + ",multiple:false,validType:\'myRequired[0]\'";
        }

        Element el = DocumentHelper.createElement("select");
        el.addAttribute("id", name);
        el.addAttribute("name", name);
        el.addAttribute("class", "easyui-combobox");
        el.addAttribute("style", style);
        el.addAttribute("data-options", data_op);

        try {
            Code e = Codes.code(type);
            if(e == null) {
                throw new Exception("系统找不到对应的码表:" + type + ",如果你确定系统有,请试着初始化系统");
            }

            List list = e.getItems();
            int size = list.size();
            Element op = el.addElement("option");
            op.addAttribute("value", "");
            op.setText("请选择");
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    CItem info = (CItem)list.get(i);
                    String code = info.getCode();
                    String note = info.getNote();
                    Element op2 = el.addElement("option");
                    op2.addAttribute("value", code);
                    op2.setText(note);
                    if(code.equals(defaultValue)) {
                        op2.addAttribute("selected", "selected");
                    }
                }
            }
        } catch (Exception var22) {
            Logger.error(var22);
        }

        return el.asXML();
    }

    public static void main(String[] args) throws JSONException {
        JSONObject o = new JSONObject();
        o.put("keyHandler", "{up: function(){}}");
        Logger.info(o.toString());
    }

    public static String createSelectBySql(String name, String sql, String defaultValue, boolean required, String ops) {
        JSONObject obj = null;

        try {
            obj = new JSONObject(ops);
        } catch (Exception var38) {
            ;
        }

        String style = "";
        boolean edit = false;
        String keyHandler = "";
        String onChange = "";

        try {
            style = obj.getString("style");
        } catch (Exception var37) {
            ;
        }

        try {
            onChange = obj.getString("onChange");
        } catch (Exception var36) {
            ;
        }

        try {
            keyHandler = obj.getString("keyHandler");
        } catch (Exception var35) {
            ;
        }

        try {
            edit = obj.getBoolean("edit");
        } catch (Exception var34) {
            ;
        }

        String data_op = null;
        if(required) {
            data_op = "editable:" + edit + ",multiple:false,validType:\'myRequired[1]\'";
        } else {
            data_op = "editable:" + edit + ",multiple:false,validType:\'myRequired[0]\'";
        }

        if(keyHandler != null && keyHandler.length() > 0) {
            data_op = data_op + ",delay:500,keyHandler:" + keyHandler;
        }

        if(onChange != null && onChange.length() > 0) {
            data_op = data_op + ",onChange:" + onChange;
        }

        Element el = DocumentHelper.createElement("select");
        el.addAttribute("id", name);
        el.addAttribute("name", name);
        el.addAttribute("class", "easyui-combobox");
        el.addAttribute("style", style);
        el.addAttribute("data-options", data_op);
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            Element op2 = el.addElement("option");
            op2.addAttribute("value", "");
            if(edit) {
                op2.setText("");
            } else {
                op2.setText("请选择");
            }

            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    String code = e.getStringValue("code", i);
                    String note = e.getStringValue("note", i);
                    Element op = el.addElement("option");
                    op.addAttribute("value", code);
                    op.setText(note);
                    if(code.equals(defaultValue)) {
                        op.addAttribute("selected", "selected");
                    }
                }
            }
        } catch (Exception var39) {
            Logger.error(var39);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var33) {
                ;
            }

        }

        return el.asXML();
    }

    public static String createMultiSelectByData(String name, String data, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder data_op = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combobox\" data-options=\"");
        if(required) {
            data_op.append("required:true,editable:\'false\',multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        } else {
            data_op.append("required:false,editable:\'false\',multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        }

        try {
            String[] e = data.split(",");
            int size = e.length;
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    String d = e[i];
                    String[] ds = d.split(":");
                    String code = d;
                    String value = d;
                    if(ds.length == 2) {
                        code = ds[0];
                        value = ds[1];
                    }

                    if(i != 0) {
                        data_op.append(",");
                    }

                    data_op.append("{");
                    data_op.append("value:\'" + code + "\',");
                    data_op.append("note:\'" + value + "\'");
                    if(temps != null && Utils.contains(temps, code)) {
                        data_op.append(",selected:\'true\'");
                    }

                    data_op.append("}");
                }
            }
        } catch (Exception var14) {
            Logger.error(var14);
        }

        data_op.append("]\" />");
        return data_op.toString();
    }

    public static String createMultiSelect(String name, String type, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder data_op = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combobox\" data-options=\"");
        if(required) {
            data_op.append("required:true,editable:false,multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        } else {
            data_op.append("required:false,editable:false,multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        }

        try {
            Code e = Codes.code(type);
            if(e == null) {
                throw new Exception("系统找不到对应的码表:" + type + ",如果你确定系统有,请试着初始化系统");
            }

            List list = e.getItems();
            int size = list.size();
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    CItem info = (CItem)list.get(i);
                    String code = info.getCode();
                    String note = info.getNote();
                    if(i != 0) {
                        data_op.append(",");
                    }

                    data_op.append("{");
                    data_op.append("value:\'" + code + "\',");
                    data_op.append("note:\'" + note + "\'");
                    if(temps != null && Utils.contains(temps, code)) {
                        data_op.append(",selected:\'true\'");
                    }

                    data_op.append("}");
                }
            }
        } catch (Exception var14) {
            Logger.error(var14);
        }

        data_op.append("]\" />");
        return data_op.toString();
    }

    public static String createMultiSelectBySql(String name, String sql, String defaultValue, boolean required, String style) {
        if(style == null) {
            style = "";
        }

        String[] temps = null;
        if(defaultValue != null && defaultValue.trim().length() > 0) {
            temps = defaultValue.split(",");
        }

        StringBuilder data_op = new StringBuilder("<input id=\"" + name + "\" name=\"" + name + "\" style=\"" + style + "\" class=\"easyui-combobox\" data-options=\"");
        if(required) {
            data_op.append("required:true,editable:false,multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        } else {
            data_op.append("required:false,editable:false,multiple:\'true\',valueField:\'value\',textField:\'note\',data:[");
        }

        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(true);
            EntityImpl e = new EntityImpl(conn);
            int size = e.executeQuery(sql);
            if(size > 0) {
                for(int i = 0; i < size; ++i) {
                    String code = e.getStringValue("code", i);
                    String note = e.getStringValue("note", i);
                    if(i != 0) {
                        data_op.append(",");
                    }

                    data_op.append("{");
                    data_op.append("value:\'" + code + "\',");
                    data_op.append("note:\'" + note + "\'");
                    if(temps != null && Utils.contains(temps, code)) {
                        data_op.append(",selected:\'true\'");
                    }

                    data_op.append("}");
                }
            }
        } catch (Exception var22) {
            Logger.error(var22);
        } finally {
            try {
                db.freeConnection(conn);
            } catch (Exception var21) {
                ;
            }

        }

        data_op.append("]\" />");
        return data_op.toString();
    }

    private static boolean hasChildren(String id, List<Map<String, Object>> list) {
        int i = 0;

        for(int l = list.size(); i < l; ++i) {
            Map m = (Map)list.get(i);
            String pid = String.valueOf(m.get("parent_id"));
            if(id.equals(pid)) {
                return true;
            }
        }

        return false;
    }

    public static void parserTreeData(StringBuilder sb, String pId, List<Map<String, Object>> list) {
        int k = 0;

        for(int i = 0; i < list.size(); ++i) {
            Map m = (Map)list.get(i);
            String code = String.valueOf(m.get("code"));
            String node = String.valueOf(m.get("note"));
            String parent = String.valueOf(m.get("parent_id"));
            String id = String.valueOf(m.get("id"));
            if(pId.equals(parent)) {
                if(k != 0) {
                    sb.append(",");
                }

                ++k;
                sb.append("{");
                sb.append("id:\'" + code + "\',text:\'" + node + "\'");
                if(hasChildren(id, list)) {
                    sb.append(",children:[");
                    parserTreeData(sb, id, list);
                    sb.append("]");
                }

                sb.append("}");
            }
        }

    }
}
