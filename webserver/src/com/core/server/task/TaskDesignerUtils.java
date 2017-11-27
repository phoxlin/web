package com.core.server.task;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.enuts.XmlShowType;
import com.core.server.db.Column;
import com.core.server.log.Logger;
import com.core.server.tools.Utils;
import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskDesignerUtils {
    private static String[] columnAttrs = new String[]{"name", "code", "width", "length", "nullable", "list", "sort", "ignore", "edit", "hidden", "readonly", "line", "controlType", "input_tablename", "placeholder", "format", "decamial", "min", "max", "bindtype", "binddata", "defaultValue", "otherset", "col_class", "col_style", "cols", "labelCols", "fieldCols", "inputHeight", "inputSpanCol", "search_compare"};


    public static void saveIndex(String taskcode, String taskname) throws Exception {
        if(taskcode != null && taskcode.length() > 0) {
            File taskIndex = new File(Utils.getWebRootPath() + "pages/" + taskcode + "/index.jsp");
            File descIndexFile = new File(Utils.getWebRootPath2() + "pages/" + taskcode + "/index.jsp");
            if("demo".equals(taskcode)) {
                taskIndex = new File(Utils.getWebRootPath() + "public/designer/demo/index.jsp");
                descIndexFile = new File(Utils.getWebRootPath2() + "public/designer/demo/index.jsp");
            }

            createTaskIndex(taskcode, taskIndex, descIndexFile);
        } else {
            throw new Exception("taskcode不能为空");
        }
    }

    public static void saveEdit(String taskcode) throws Exception {
        if(taskcode != null && taskcode.length() > 0) {
            File taskFolderFile = new File(Utils.getWebRootPath() + "pages/" + taskcode);
            File descTaskFolderFile = new File(Utils.getWebRootPath2() + "pages/" + taskcode);
            if("demo".equals(taskcode)) {
                taskFolderFile = new File(Utils.getWebRootPath() + "public/designer/demo");
                descTaskFolderFile = new File(Utils.getWebRootPath2() + "public/designer/demo");
            }

            createTaskEdit(taskcode, taskFolderFile, descTaskFolderFile);
        } else {
            throw new Exception("taskcode不能为空");
        }
    }

    private static void createTaskEdit(String taskcode, File taskFolderFile, File descTaskFolderFile) throws Exception {
        TaskInfo task = new TaskInfo(taskcode, (User)null);
        List legends = task.getLegends();
        if(legends != null && legends.size() > 0) {
            Iterator var6 = legends.iterator();

            while(var6.hasNext()) {
                TaskNormalLegend le = (TaskNormalLegend)var6.next();
                if(le.getType().equals("data-grid")) {
                    TaskGridLegend grid = (TaskGridLegend)le;
                    String entity = grid.getEntity();
                    String hasEntity = "has" + entity.substring(0, 1).toUpperCase() + entity.substring(1);
                    String grid_code = grid.getCode();
                    Columns columns = grid.getColumns();
                    String formName = grid_code + "FormObj";
                    int show_col_num = grid.getShow_col_num();
                    int title_width = grid.getTitleWidth();
                    int space_width = grid.getSpaceWidth();
                    int e_width = grid.getInputWidth();
                    File edit = new File(taskFolderFile.getAbsolutePath() + "/" + grid_code + "_edit.jsp");
                    File descEdit = new File(descTaskFolderFile.getAbsolutePath() + "/" + grid_code + "_edit.jsp");
                    ArrayList readonlyFun = new ArrayList();
                    ArrayList editFun = new ArrayList();
                    ArrayList edits = new ArrayList();
                    edits.add("<%@page import=\"com.jinhua.server.db.Entity\"%>");
                    edits.add("<%@page import=\"com.jinhua.server.tools.UI\"%>");
                    edits.add("<%@page import=\"com.jinhua.server.tools.UI_Op\"%>");
                    edits.add("<%@page import=\"com.jinhua.server.tools.Utils\"%>");
                    edits.add("<%@page import=\"com.jinhua.server.tools.SystemUtils\"%>");
                    edits.add("<%@page import=\"com.jinhua.User\"%>");
                    edits.add("<%@page import=\"java.util.Date\"%>");
                    edits.add("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>");
                    edits.add("<%");
                    edits.add("");
                    edits.add("\tUser user=SystemUtils.getSessionUser(request, response);");
                    edits.add("\tif(user==null){ request.getRequestDispatcher(\"/\").forward(request, response);}");
                    edits.add("");
                    edits.add("\tEntity " + entity + "=(Entity)request.getAttribute(\"" + entity + "\");");
                    edits.add("\tboolean " + hasEntity + "=" + entity + "!=null&&" + entity + ".getResultCount()>0;");
                    edits.add("%>");
                    edits.add("<!DOCTYPE HTML>");
                    edits.add("<html>");
                    edits.add(" <head>");
                    edits.add("  <jsp:include page=\"/public/edit_base.jsp\" />");
                    edits.add("  <script type=\"text/javascript\">");
                    edits.add("    var entity = \"" + entity + "\";");
                    edits.add("    var form_id = \"" + formName + "\";");
                    edits.add("    var lockId=new UUID();");
                    edits.add("    $(document).ready(function() {");
                    edits.add("");
                    edits.add("    //insert js");
                    edits.add("");
                    edits.add("    });");
                    edits.add("  </script>");
                    if(!"demo".equals(taskcode)) {
                        edits.add("  <script type=\"text/javascript\" charset=\"utf-8\" src=\"pages/" + taskcode + "/" + grid.getCode() + ".js\"></script>");
                    } else {
                        edits.add("  <script type=\"text/javascript\" charset=\"utf-8\" src=\"public/designer/demo/" + grid.getCode() + ".js\"></script>");
                    }

                    edits.add(" </head>");
                    edits.add("<body>");
                    edits.add("\t  <form class=\"l-form\" id=\"" + formName + "\" method=\"post\">");
                    Iterator closed = columns.getCols().iterator();

                    while(closed.hasNext()) {
                        Column k = (Column)closed.next();
                        if(k.isHidden()) {
                            edits.add("\t    <input id=\"" + entity + "__" + k.getName() + "\" name=\"" + entity + "__" + k.getName() + "\" type=\"hidden\" value=\'<%=" + hasEntity + "?" + entity + ".getStringValue(\"" + k.getName() + "\"):\"\"%>\'/>");
                        }
                    }

                    int var40 = 0;
                    boolean var42 = true;

                    int insertIndex;
                    String n;
                    for(insertIndex = 0; insertIndex < columns.getCols().size(); ++insertIndex) {
                        Column m = (Column)columns.getCols().get(insertIndex);
                        if(!m.isHidden() && m.isEdit() && !m.isIgnore()) {
                            n = m.getDefaultValue();
                            String display_type = m.getControlType();
                            String fieldname = m.getName();
                            String bindData = m.getBindData();
                            int length = m.getLength();
                            int e_height = m.getInputHeight();
                            if(display_type.equals("textarea")) {
                                if(e_height < 40) {
                                    e_height = 40;
                                }
                            } else if(display_type.equals("editor")) {
                                if(e_height < 150) {
                                    e_height = 150;
                                }
                            } else {
                                e_height = 0;
                            }

                            String db_field_comment = m.getComment();
                            boolean nullable = !m.isNullable();
                            boolean is_readonly = m.isReadonly();
                            boolean is_single_row = m.isLine();
                            boolean is_edit = m.isEdit();
                            int span_col_num = m.getInputSpanCol();
                            if(span_col_num < 1) {
                                span_col_num = 1;
                            }

                            if(span_col_num > 3) {
                                span_col_num = 3;
                            }

                            if(span_col_num > show_col_num) {
                                span_col_num = show_col_num;
                            }

                            int spanColLine;
                            if(is_single_row) {
                                spanColLine = (title_width + e_width + space_width) * show_col_num - title_width - space_width;
                                if(var42) {
                                    edits.add("\t    <ul>");
                                    edits.add("\t      <li style=\"width: " + title_width + "px; text-align: left;\">" + db_field_comment + (nullable?"":"(*)") + "：</li>");
                                    edits.add("       <li style=\"width: " + spanColLine + "px; text-align: left;\">");
                                    edits.add("\t        <div class=\"l-text\" style=\"width: " + (spanColLine - 2) + "px;" + (e_height > 0?"height: " + (e_height + 10) + "px;":"") + "\">");
                                    edits.add("\t          " + createUIString(display_type, entity, fieldname, nullable, hasEntity, is_readonly, is_edit, bindData, n, spanColLine - 6, e_height, length, editFun, readonlyFun, m));
                                    edits.add("\t          <div class=\"l-text-l\"></div>");
                                    edits.add("\t          <div class=\"l-text-r\"></div>");
                                    edits.add("\t        </div>");
                                    edits.add("\t      </li>");
                                    edits.add("\t      <li style=\"width: " + space_width + "px;\"></li>");
                                    edits.add("\t    </ul>");
                                } else {
                                    edits.add("\t    </ul>");
                                    edits.add("\t    <ul>");
                                    edits.add("\t      <li style=\"width: " + title_width + "px; text-align: left;\">" + db_field_comment + (nullable?"":"(*)") + "：</li>");
                                    edits.add("       <li style=\"width: " + spanColLine + "px; text-align: left;\">");
                                    edits.add("\t        <div class=\"l-text\" style=\"width: " + (spanColLine - 2) + "px;" + (e_height > 0?"height: " + (e_height + 10) + "px;":"") + "\">");
                                    edits.add("\t          " + createUIString(display_type, entity, fieldname, nullable, hasEntity, is_readonly, is_edit, bindData, n, spanColLine - 6, e_height, length, editFun, readonlyFun, m));
                                    edits.add("\t          <div class=\"l-text-l\"></div>");
                                    edits.add("\t          <div class=\"l-text-r\"></div>");
                                    edits.add("\t        </div>");
                                    edits.add("\t      </li>");
                                    edits.add("\t      <li style=\"width: " + space_width + "px;\"></li>");
                                    edits.add("\t    </ul>");
                                }

                                var42 = true;
                                var40 = 0;
                            } else {
                                spanColLine = (title_width + e_width + space_width) * span_col_num - title_width - space_width;
                                if(var42) {
                                    var42 = false;
                                    boolean var41 = false;
                                    var40 = span_col_num;
                                    edits.add("\t    <ul>");
                                    edits.add("\t      <li style=\"width: " + title_width + "px; text-align: left;\">" + db_field_comment + (nullable?"":"(*)") + "：</li>");
                                    edits.add("       <li style=\"width: " + spanColLine + "px; text-align: left;\">");
                                    edits.add("\t        <div class=\"l-text\" style=\"width: " + (spanColLine - 2) + "px;" + (e_height > 0?"height: " + (e_height + 10) + "px;":"") + "\">");
                                    edits.add("\t          " + createUIString(display_type, entity, fieldname, nullable, hasEntity, is_readonly, is_edit, bindData, n, spanColLine - 6, e_height, length, editFun, readonlyFun, m));
                                    edits.add("\t          <div class=\"l-text-l\"></div>");
                                    edits.add("\t          <div class=\"l-text-r\"></div>");
                                    edits.add("\t        </div>");
                                    edits.add("\t      </li>");
                                    edits.add("\t      <li style=\"width: " + space_width + "px;\"></li>");
                                    if(span_col_num >= show_col_num) {
                                        edits.add("\t    </ul>");
                                        var42 = true;
                                    }
                                } else {
                                    var40 %= show_col_num;
                                    edits.add("\t      <li style=\"width: " + title_width + "px; text-align: left;\">" + db_field_comment + (nullable?"":"(*)") + "：</li>");
                                    edits.add("       <li style=\"width: " + spanColLine + "px; text-align: left;\">");
                                    edits.add("\t        <div class=\"l-text\" style=\"width: " + (spanColLine - 2) + "px;" + (e_height > 0?"height: " + (e_height + 10) + "px;":"") + "\">");
                                    edits.add("\t          " + createUIString(display_type, entity, fieldname, nullable, hasEntity, is_readonly, is_edit, bindData, n, spanColLine - 6, e_height, length, editFun, readonlyFun, m));
                                    edits.add("\t          <div class=\"l-text-l\"></div>");
                                    edits.add("\t          <div class=\"l-text-r\"></div>");
                                    edits.add("\t        </div>");
                                    edits.add("\t      </li>");
                                    edits.add("\t      <li style=\"width: " + space_width + "px;\"></li>");
                                    var40 += span_col_num;
                                    if(var40 >= show_col_num) {
                                        edits.add("\t    </ul>");
                                        var42 = true;
                                        var40 = 0;
                                    }
                                }
                            }
                        }
                    }

                    if(!var42) {
                        edits.add("\t    </ul>");
                    }

                    edits.add("\t  </form>");
                    edits.add(" </body>");
                    edits.add("</html>");
                    if(readonlyFun.size() > 0 || editFun.size() > 0) {
                        insertIndex = 0;

                        int var43;
                        for(var43 = 0; var43 < edits.size(); ++var43) {
                            n = (String)edits.get(var43);
                            if(n.trim().startsWith("//insert js")) {
                                insertIndex = var43 + 1;
                                break;
                            }
                        }

                        var43 = 0;

                        int var44;
                        for(var44 = readonlyFun.size(); var43 < var44; ++var43) {
                            edits.add(insertIndex++, "      " + (String)readonlyFun.get(var43));
                            edits.add(insertIndex++, "");
                        }

                        if(editFun.size() > 0) {
                            edits.add(insertIndex++, "      <% if(\"edit\".equals(request.getParameter(\"type\"))){ %>");
                            edits.add(insertIndex++, "");
                            var43 = 0;

                            for(var44 = editFun.size(); var43 < var44; ++var43) {
                                edits.add(insertIndex++, "        " + (String)editFun.get(var43));
                                edits.add(insertIndex++, "");
                            }

                            edits.add(insertIndex++, "       <%");
                            edits.add(insertIndex++, "");
                            edits.add(insertIndex++, "      } %>");
                            edits.add(insertIndex++, "");
                        }
                    }

                    Utils.saveContent2File(edit, Utils.getListString(edits, "\r\n"));
                    Logger.info("create file:" + edit.getAbsolutePath());
                    if(!"demo".equals(taskcode)) {
                        FileUtils.copyFile(edit, descEdit);
                        Logger.info("copyed to :" + descEdit.getAbsolutePath());
                    }
                }
            }
        }

    }

    private static void createTaskIndex(String taskcode, File index, File descIndex) throws Exception {
        File parentIndex = index.getParentFile();
        File parentDesc = descIndex.getParentFile();
        if(!parentIndex.exists()) {
            parentIndex.mkdirs();
        }

        if(!parentDesc.exists()) {
            parentDesc.mkdirs();
        }

        TaskInfo task = new TaskInfo(taskcode, (User)null);
        ArrayList indexs = new ArrayList();
        indexs.add("<%@page import=\"com.jinhua.server.tools.SystemUtils\"%>");
        indexs.add("<%@page import=\"com.jinhua.User\"%>");
        indexs.add("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\" pageEncoding=\"UTF-8\"%>");
        indexs.add("<%");
        indexs.add("\tUser user=SystemUtils.getSessionUser(request, response);");
        indexs.add("\tif(user==null){ request.getRequestDispatcher(\"/\").forward(request, response);}");
        indexs.add("");
        indexs.add("\tString taskcode = \"" + task.getCode() + "\";");
        indexs.add("\tString taskname = \"" + task.getName() + "\";");
        indexs.add("\tString sId = request.getParameter(\"sid\");");
        indexs.add("");
        indexs.add("%>");
        indexs.add("<!DOCTYPE html>");
        indexs.add("<html>");
        indexs.add("<head>");
        indexs.add("<title><%=taskname%></title>");
        indexs.add("<jsp:include page=\"/public/base.jsp\" />");
        indexs.add("<script type=\"text/javascript\">");
        indexs.add("");
        indexs.add("");
        indexs.add("//data-grid配置开始");
        List legends = task.getLegends();
        int gridIndex = 0;
        TaskNormalLegend le;
        Iterator var10;
        TaskGridLegend grid;
        if(legends != null && legends.size() > 0) {
            var10 = legends.iterator();

            while(var10.hasNext()) {
                le = (TaskNormalLegend)var10.next();
                if(le.getType().equals("data-grid")) {
                    grid = (TaskGridLegend)le;
                    String qmname = taskcode + "___" + le.getCode();
                    StringBuilder var10001 = new StringBuilder("///////////////////////////////////////////(");
                    ++gridIndex;
                    indexs.add(var10001.append(gridIndex).append(").").append(qmname).append("开始///////////////////////////////////////////").toString());
                    indexs.add("\t//搜索配置");
                    indexs.add("\tvar " + qmname + "_filter=[");
                    indexs.addAll(grid.toFilterIndexs());
                    indexs.add("\t\t\t\t      \t ];");
                    indexs.add("\t//编辑页面弹框标题配置");
                    indexs.add("\tvar " + qmname + "_dialog_title=\'" + grid.getName() + "\';");
                    indexs.add("\t//编辑页面弹框宽度配置");
                    indexs.add("\tvar " + qmname + "_dialog_width=" + grid.getWinWidth() + ";");
                    indexs.add("\t//编辑页面弹框高度配置");
                    indexs.add("\tvar " + qmname + "_dialog_height=" + grid.getWinHeight() + ";");
                    indexs.add("\t//IndexGrid数据加载提示配置");
                    indexs.add("\tvar " + qmname + "_loading=true;");
                    indexs.add("\t//编辑页面弹框宽度配置");
                    indexs.add("\tvar " + qmname + "_entity=\"" + grid.getEntity() + "\";");
                    indexs.add("\t//编辑页面路径配置");
                    if(!"demo".equals(taskcode)) {
                        indexs.add("\tvar " + qmname + "_nextpage=\"pages/" + taskcode + "/" + grid.getCode() + "_edit.jsp\";");
                    } else {
                        indexs.add("\tvar " + qmname + "_nextpage=\"public/designer/demo/" + grid.getCode() + "_edit.jsp\";");
                    }

                    indexs.add("///////////////////////////////////////////(" + gridIndex + ")." + qmname + "结束///////////////////////////////////////////");
                    indexs.add("");
                }
            }
        }

        indexs.add("//data-grid配置结束");
        indexs.add("");
        indexs.add("</script>");
        if(!"demo".equals(taskcode)) {
            indexs.add("<script type=\"text/javascript\" charset=\"utf-8\" src=\"pages/" + taskcode + "/index.js\"></script>");
            (new File(parentIndex.getAbsolutePath() + "/index.js")).createNewFile();
            (new File(parentDesc.getAbsolutePath() + "/index.js")).createNewFile();
        } else {
            indexs.add("<script type=\"text/javascript\" charset=\"utf-8\" src=\"public/designer/demo/index.js\"></script>");
            (new File(parentIndex.getAbsolutePath() + "/index.js")).createNewFile();
        }

        if(legends != null && legends.size() > 0) {
            var10 = legends.iterator();

            while(var10.hasNext()) {
                le = (TaskNormalLegend)var10.next();
                if(le.getType().equals("data-grid")) {
                    grid = (TaskGridLegend)le;
                    if(!"demo".equals(taskcode)) {
                        indexs.add("<script type=\"text/javascript\" charset=\"utf-8\" src=\"pages/" + taskcode + "/" + grid.getCode() + ".js\"></script>");
                        (new File(parentIndex.getAbsolutePath() + "/" + grid.getCode() + ".js")).createNewFile();
                        (new File(parentDesc.getAbsolutePath() + "/" + grid.getCode() + ".js")).createNewFile();
                    } else {
                        indexs.add("<script type=\"text/javascript\" charset=\"utf-8\" src=\"public/designer/demo/" + grid.getCode() + ".js\"></script>");
                        (new File(parentIndex.getAbsolutePath() + "/" + grid.getCode() + ".js")).createNewFile();
                    }
                }
            }
        }

        indexs.add("");
        indexs.add("<script type=\"text/javascript\">");
        indexs.add("\t$(document).ready(function() {");
        indexs.add("\t\tshowTaskView(\'<%=taskcode%>\',\'<%=sId%>\',\'" + (task.isTask()?"Y":"N") + "\');");
        indexs.add("\t});");
        indexs.add("</script>");
        indexs.add("</head>");
        indexs.add("<body>");
        indexs.add("\t<div id=\"wrapper\">");
        indexs.add("\t\t<jsp:include page=\"/public/menus.jsp\" />");
        indexs.add("\t\t<div id=\"page-wrapper\">");
        indexs.add("\t\t\t<div class=\"container-fluid\">");
        indexs.add("\t\t\t\t<div class=\"row\">");
        indexs.add("\t\t\t\t\t<div class=\"col-lg-12\">");
        indexs.add(" \t\t\t\t\t\t<h1 class=\"page-header\"><%=taskname%></h1>");
        indexs.add("\t\t\t\t\t</div>");
        indexs.add("\t\t\t\t</div>");
        indexs.add("\t\t\t\t<div class=\"row\">");
        if(task.getLeft() > 0) {
            indexs.add("\t\t\t\t\t<div class=\"col-lg-" + task.getLeft() + "\">");
            indexs.add("\t\t\t\t\t\t<div id=\"<%=taskcode%>_jh_process_leftpage\"> </div>");
            indexs.add("\t\t\t\t\t</div>");
        }

        indexs.add("\t\t\t\t\t<div class=\"col-lg-" + task.getRight() + "\">");
        indexs.add("\t\t\t\t\t\t<div id=\"<%=taskcode%>_jh_process_page\"> </div>");
        indexs.add("\t\t\t\t\t</div>");
        indexs.add("\t\t\t\t</div>");
        indexs.add("\t\t\t</div>");
        indexs.add("\t\t</div>");
        indexs.add("\t</div>");
        indexs.add("</body>");
        indexs.add("</html>");
        Utils.saveContent2File(index, Utils.getListString(indexs, "\r\n"));
        Logger.info("create file:" + index.getAbsolutePath());
        if(!"demo".equals(taskcode)) {
            FileUtils.copyFile(index, descIndex);
            Logger.info("copyed to :" + descIndex.getAbsolutePath());
        }

    }

    public static void saveTaskCfg(String taskcode, String taskname, Map<String, Object> map, boolean first) throws Exception {
        File taskCfg = new File(Utils.getWebRootPath() + "WEB-INF/configures/task/" + taskcode + "-" + taskname + ".xml");
        File descFile = new File(Utils.getWebRootPath2() + "WEB-INF/configures/task/" + taskcode + "-" + taskname + ".xml");
        Document doc = DocumentHelper.createDocument();
        Element task = doc.addElement("root").addElement("task");
        task.addAttribute("code", taskcode);
        task.addAttribute("name", taskname);
        task.addAttribute("load-on-startup", first?"0":"1");
        task.addAttribute("isTask", Utils.getMapBooleanValue(map, "isTask")?"Y":"N");
        task.addAttribute("isFirstTask", Utils.getMapBooleanValue(map, "isFirstTask")?"Y":"N");
        int left_col_number = 0;
        int right_col_number = 12;

        try {
            if(map.containsKey("left_col_number")) {
                left_col_number = Utils.getMapIntegerValue(map, "left_col_number");
            }

            if(map.containsKey("right_col_number")) {
                right_col_number = Utils.getMapIntegerValue(map, "right_col_number");
            }
        } catch (Exception var45) {
            ;
        }

        task.addAttribute("left_col_number", String.valueOf(left_col_number));
        task.addAttribute("right_col_number", String.valueOf(right_col_number));
        Element decision = task.addElement("decision");
        Object decis = map.get("decision");
        Map ct;
        String type;
        String rdb;
        String autoLoadingData;
        String legendTableName;
        if(decis != null) {
            List content = (List)decis;
            if(content.size() > 0) {
                int contentObj = 0;

                for(int contents = content.size(); contentObj < contents; ++contentObj) {
                    ct = (Map)content.get(contentObj);
                    String decision_name = Utils.getMapStringValue(ct, "decision_name");
                    type = Utils.getMapStringValue(ct, "toName");
                    rdb = Utils.getMapStringValue(ct, "assignee-users");
                    autoLoadingData = Utils.getMapStringValue(ct, "assignee-groups");
                    legendTableName = Utils.getMapStringValue(ct, "condition");
                    if(legendTableName == null || legendTableName.length() <= 0) {
                        legendTableName = "default";
                    }

                    Element legendDisplay = decision.addElement("transition");
                    legendDisplay.addAttribute("name", decision_name);
                    legendDisplay.addAttribute("to", type);
                    legendDisplay.addAttribute("assignee-users", rdb);
                    legendDisplay.addAttribute("assignee-groups", autoLoadingData);
                    legendDisplay.addAttribute("condition", legendTableName);
                }
            }
        }

        Element var46 = task.addElement("content");
        Object var47 = map.get("task_content");
        if(var47 != null) {
            List var48 = (List)var47;
            if(var48.size() > 0) {
                Iterator var49 = var48.iterator();

                while(var49.hasNext()) {
                    ct = (Map)var49.next();
                    type = Utils.getMapStringValue(ct, "type");
                    rdb = "Y";
                    String entity;
                    String var50;
                    if("grid".equals(type)) {
                        autoLoadingData = Utils.getMapStringValue(ct, "gridName");
                        legendTableName = Utils.getMapStringValue(ct, "gridCode");
                        var50 = Utils.getMapBooleanValue(ct, "autoLoadingData")?"Y":"N";
                        entity = Utils.getMapStringValue(ct, "initSqlValue");
                        String var51 = Utils.getMapStringValue(ct, "entity");
                        String var52 = Utils.getMapStringValue(ct, "column_show_num");
                        String var53 = Utils.getMapStringValue(ct, "spaceWidth");
                        String var54 = Utils.getMapStringValue(ct, "titleWidth");
                        String var55 = Utils.getMapStringValue(ct, "inputWidth");
                        String var56 = Utils.getMapStringValue(ct, "winWidth");
                        String var57 = Utils.getMapStringValue(ct, "winHeight");
                        List bars = null;

                        try {
                            bars = (List)ct.get("bars");
                        } catch (Exception var44) {
                            ;
                        }

                        List cts = null;

                        try {
                            cts = (List)ct.get("contents");
                        } catch (Exception var43) {
                            ;
                        }

                        Element grid = var46.addElement("legend");
                        grid.addAttribute("type", "data-grid");
                        grid.addAttribute("code", legendTableName);
                        grid.addAttribute("name", autoLoadingData);
                        grid.addAttribute("rdb", rdb);
                        grid.addAttribute("autoLoadingData", var50);
                        grid.addAttribute("initSql", entity);
                        grid.addAttribute("entity", var51);
                        grid.addAttribute("column_show_num", var52);
                        grid.addAttribute("spaceWidth", var53);
                        grid.addAttribute("titleWidth", var54);
                        grid.addAttribute("inputWidth", var55);
                        grid.addAttribute("winWidth", var56);
                        grid.addAttribute("winHeight", var57);
                        Element toolbars = grid.addElement("toolbars");
                        if(bars != null && bars.size() > 0) {
                            Iterator m1 = bars.iterator();

                            while(m1.hasNext()) {
                                Map columns = (Map)m1.next();
                                String toolbar_display = Utils.getMapStringValue(columns, "toolbar_display");
                                String toolbar_class = Utils.getMapStringValue(columns, "toolbar_class");
                                String toolbar_js = Utils.getMapStringValue(columns, "toolbar_js");
                                String toolbar_name = Utils.getMapStringValue(columns, "toolbar_name");
                                Element toolbar = toolbars.addElement("toolbar");
                                toolbar.addAttribute("class", toolbar_class);
                                toolbar.addAttribute("text", toolbar_display);
                                toolbar.addAttribute("js", toolbar_js);
                                toolbar.addAttribute("name", toolbar_name);
                                toolbar.addAttribute("visible_code", taskcode + "_" + toolbar_name);
                            }
                        }

                        Element var58 = grid.addElement("columns");
                        if(cts != null && cts.size() > 0) {
                            Iterator var60 = cts.iterator();

                            while(var60.hasNext()) {
                                Map var59 = (Map)var60.next();
                                var58.add(createColumn((Map)var59));
                            }
                        }
                    } else {
                        autoLoadingData = Utils.getMapBooleanValue(ct, "autoLoadingData")?"Y":"N";
                        legendTableName = Utils.getMapStringValue(ct, "legendTableName");
                        var50 = Utils.getMapStringValue(ct, "legendDisplay");
                        entity = Utils.getMapStringValue(ct, "entity");
                        List rows = null;

                        try {
                            rows = (List)ct.get("rows");
                        } catch (Exception var42) {
                            ;
                        }

                        Element legend = var46.addElement("legend");
                        legend.addAttribute("type", "legend");
                        legend.addAttribute("code", legendTableName);
                        legend.addAttribute("name", var50);
                        legend.addAttribute("rdb", rdb);
                        legend.addAttribute("autoLoadingData", autoLoadingData);
                        legend.addAttribute("entity", entity);
                        if(rows != null && rows.size() > 0) {
                            Iterator titleWidth = rows.iterator();

                            while(titleWidth.hasNext()) {
                                List row = (List)titleWidth.next();
                                if(row != null && row.size() > 0) {
                                    Element rowElement = legend.addElement("row");
                                    Iterator winHeight = row.iterator();

                                    while(winHeight.hasNext()) {
                                        Map m = (Map)winHeight.next();
                                        rowElement.add(createColumn((Map)m));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Utils.saveContent2File(taskCfg, Utils.toXml(doc, XmlShowType.Pretty));
        Logger.info("Created xml file:" + taskCfg.getAbsolutePath());
        if(!"demo".equals(taskcode)) {
            try {
                FileUtils.copyFile(taskCfg, descFile);
                Logger.info("Copyed xml file to:" + descFile.getAbsolutePath());
            } catch (Exception var41) {
                ;
            }
        }

    }

    public static Column createColumn(Element ele) {
        String name = ele.attributeValue("display");
        String code = ele.attributeValue("code");
        String width = ele.attributeValue("width");
        String length = ele.attributeValue("length");
        String nullable = ele.attributeValue("nullable");
        String list = ele.attributeValue("show");
        String sort = ele.attributeValue("sort");
        String ignore = ele.attributeValue("ignore");
        String edit = ele.attributeValue("edit");
        String hidden = ele.attributeValue("hidden");
        String readonly = ele.attributeValue("readonly");
        String line = ele.attributeValue("line");
        String controlType = ele.attributeValue("controlType");
        String input_tablename = ele.attributeValue("tablename");
        String placeholder = ele.attributeValue("placeholder");
        String format = ele.attributeValue("format");
        String decamial = ele.attributeValue("decamial");
        String min = ele.attributeValue("min");
        String max = ele.attributeValue("max");
        String bindtype = ele.attributeValue("bindType");
        String binddata = ele.attributeValue("bindData");
        String defaultValue = ele.attributeValue("defaultValue");
        String otherset = ele.attributeValue("otherset");
        String col_class = ele.attributeValue("col_class");
        String col_style = ele.attributeValue("col_style");
        String cols = ele.attributeValue("cols");
        String labelCols = ele.attributeValue("labelCols");
        String fieldCols = ele.attributeValue("fieldCols");
        String inputHeight = ele.attributeValue("inputHeight");
        String inputSpanCol = ele.attributeValue("inputSpanCol");
        String search_compare = ele.attributeValue("search_compare");
        Column col = new Column();
        List li = ele.attributes();

        for(int a = 0; a < li.size(); ++a) {
            Attribute aa = (Attribute)li.get(a);
            if(!Utils.contains(columnAttrs, aa.getName().toLowerCase())) {
                col.getMap().put(aa.getName(), aa.getValue());
            }
        }

        if("id".equalsIgnoreCase(code)) {
            col.setAutoGenerate(true);
            col.setKey(true);
        }

        col.setName(code);
        col.setComment(name);
        col.setWidth(width);
        col.setLength(length);
        col.setNullable(Utils.isTrue(nullable));
        col.setShow(Utils.isTrue(list));
        col.setSort(Utils.isTrue(sort));
        col.setIgnore(Utils.isTrue(ignore));
        col.setEdit(Utils.isTrue(edit));
        col.setHidden(Utils.isTrue(hidden));
        col.setReadonly(Utils.isTrue(readonly));
        col.setLine(Utils.isTrue(line));
        col.setQuery(true);
        col.setControlType(controlType);
        col.setType(parseControlType2ColumnType(controlType));
        col.setInput_tablename(input_tablename);
        col.setPlaceholder(placeholder);
        col.setFormat(format);
        col.setDecamial(decamial);
        col.setMin(min);
        col.setMax(max);
        col.setBindType(bindtype);
        col.setBindData(binddata);
        col.setDefaultValue(defaultValue);
        col.setOtherset(otherset);
        col.setSearch_compare(search_compare);
        col.setCol_class(col_class);
        col.setCol_style(col_style);

        try {
            col.setCols(Integer.parseInt(cols));
        } catch (Exception var40) {
            ;
        }

        try {
            col.setLabelCols(Integer.parseInt(labelCols));
        } catch (Exception var39) {
            ;
        }

        try {
            col.setFieldCols(Integer.parseInt(fieldCols));
        } catch (Exception var38) {
            ;
        }

        try {
            col.setInputHeight(Integer.parseInt(inputHeight));
        } catch (Exception var37) {
            ;
        }

        try {
            col.setInputSpanCol(Integer.parseInt(inputSpanCol));
        } catch (Exception var36) {
            ;
        }

        return col;
    }

    private static Element createColumn(Map<String, Object> m) {
        String name = Utils.getMapStringValue(m, "name");
        String code = Utils.getMapStringValue(m, "code");
        String width = Utils.getMapStringValue(m, "width");
        String length = Utils.getMapStringValue(m, "length");
        String nullable = Utils.getMapBooleanValue(m, "nullable")?"Y":"N";
        String list = Utils.getMapBooleanValue(m, "list")?"Y":"N";
        String sort = Utils.getMapBooleanValue(m, "sort")?"Y":"N";
        String ignore = Utils.getMapBooleanValue(m, "ignore")?"Y":"N";
        String edit = Utils.getMapBooleanValue(m, "edit")?"Y":"N";
        String hidden = Utils.getMapBooleanValue(m, "hidden")?"Y":"N";
        String readonly = Utils.getMapBooleanValue(m, "readonly")?"Y":"N";
        String line = Utils.getMapBooleanValue(m, "line")?"Y":"N";
        String query = "Y";
        String controlType = Utils.getMapStringValue(m, "controlType");
        String input_tablename = Utils.getMapStringValue(m, "input_tablename");
        String placeholder = Utils.getMapStringValue(m, "placeholder");
        String format = Utils.getMapStringValue(m, "format");
        String decamial = Utils.getMapStringValue(m, "decamial");
        String min = Utils.getMapStringValue(m, "min");
        String max = Utils.getMapStringValue(m, "max");
        String bindtype = Utils.getMapStringValue(m, "bindtype");
        String binddata = Utils.getMapStringValue(m, "binddata");
        String defaultValue = Utils.getMapStringValue(m, "defaultValue");
        String otherset = Utils.getMapStringValue(m, "otherset");
        String col_class = Utils.getMapStringValue(m, "col_class");
        String col_style = Utils.getMapStringValue(m, "col_style");
        String cols = Utils.getMapStringValue(m, "cols");
        String labelCols = Utils.getMapStringValue(m, "labelCols");
        String fieldCols = Utils.getMapStringValue(m, "fieldCols");
        String InputHeight = Utils.getMapStringValue(m, "InputHeight");
        String InputSpanCol = Utils.getMapStringValue(m, "InputSpanCol");
        String search_compare = Utils.getMapStringValue(m, "search_compare");
        int intCols = 6;

        try {
            intCols = Integer.parseInt(cols);
        } catch (Exception var43) {
            ;
        }

        int intlabelCols = 6;

        try {
            intlabelCols = Integer.parseInt(labelCols);
        } catch (Exception var42) {
            ;
        }

        int intfieldCols = 6;

        try {
            intfieldCols = Integer.parseInt(fieldCols);
        } catch (Exception var41) {
            ;
        }

        int inputHeight = 50;

        try {
            inputHeight = Integer.parseInt(InputHeight);
        } catch (Exception var40) {
            ;
        }

        int inputSpanCol = 1;

        try {
            inputSpanCol = Integer.parseInt(InputSpanCol);
        } catch (Exception var39) {
            ;
        }

        Element col = DocumentHelper.createElement("column");
        col.addAttribute("code", code);
        col.addAttribute("display", name);
        col.addAttribute("width", width);
        col.addAttribute("length", length);
        col.addAttribute("format", format);
        col.addAttribute("show", list);
        col.addAttribute("send", "Y");
        col.addAttribute("sort", sort);
        col.addAttribute("nullable", nullable);
        col.addAttribute("ignore", ignore);
        col.addAttribute("edit", edit);
        col.addAttribute("hidden", hidden);
        col.addAttribute("readonly", readonly);
        col.addAttribute("line", line);
        col.addAttribute("query", query);
        col.addAttribute("placeholder", placeholder);
        col.addAttribute("bindType", parseBindType(bindtype));
        col.addAttribute("bindData", binddata);
        col.addAttribute("controlType", parseControlType(controlType));
        col.addAttribute("defaultValue", defaultValue);
        col.addAttribute("tablename", input_tablename);
        col.addAttribute("decamial", decamial);
        col.addAttribute("min", min);
        col.addAttribute("max", max);
        col.addAttribute("otherset", otherset);
        col.addAttribute("col_class", col_class);
        col.addAttribute("col_style", col_style);
        col.addAttribute("cols", String.valueOf(intCols));
        col.addAttribute("labelCols", String.valueOf(intlabelCols));
        col.addAttribute("fieldCols", String.valueOf(intfieldCols));
        col.addAttribute("inputHeight", String.valueOf(inputHeight));
        col.addAttribute("inputSpanCol", String.valueOf(inputSpanCol));
        col.addAttribute("search_compare", search_compare);
        return col;
    }

    public static String parseBindType(String bindType) {
        String bindtype = "no";
        if("一般码表".equals(bindType)) {
            bindtype = "codetable";
        } else if("树形码表".equals(bindType)) {
            bindtype = "treecodetable";
        } else if("SQL".equals(bindType)) {
            bindtype = "sql";
        } else if("MGO".equals(bindType)) {
            bindtype = "mgo";
        } else {
            bindtype = "no";
        }

        return bindtype;
    }

    public static ColumnType parseControlType2ColumnType(String controlType) {
        ColumnType type = ColumnType.STRING;
        if("password".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("easyui-numberbox".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("easyui-datebox".equals(controlType)) {
            type = ColumnType.DATE;
        } else if("easyui-datetimebox".equals(controlType)) {
            type = ColumnType.DATETIME;
        } else if("easyui-combobox".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("easyui-combotree".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("niu-upload".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("old-upload".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("editor".equals(controlType)) {
            type = ColumnType.LONGTEXT;
        } else if("textarea".equals(controlType)) {
            type = ColumnType.TEXT;
        } else if("easyui-timespinner".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("easyui-numberspinner".equals(controlType)) {
            type = ColumnType.STRING;
        } else if("chooseUser".equals(controlType)) {
            type = ColumnType.STRING;
        }

        return type;
    }

    public static ColumnType dbtype2ColumnType(String datatype) {
        ColumnType type = ColumnType.STRING;
        if("TEXT".equalsIgnoreCase(datatype)) {
            type = ColumnType.TEXT;
        } else if("MEDIUMTEXT".equalsIgnoreCase(datatype)) {
            type = ColumnType.MEDIUMTEXT;
        } else if("LONGTEXT".equalsIgnoreCase(datatype)) {
            type = ColumnType.LONGTEXT;
        } else if("DATE".equalsIgnoreCase(datatype)) {
            type = ColumnType.DATE;
        } else if("datetime".equalsIgnoreCase(datatype)) {
            type = ColumnType.DATETIME;
        } else if("TIMESTAMP".equalsIgnoreCase(datatype)) {
            type = ColumnType.DATETIME;
        } else if("NUMBER".equalsIgnoreCase(datatype)) {
            type = ColumnType.FLOAT;
        } else if("FLOAT".equalsIgnoreCase(datatype)) {
            type = ColumnType.FLOAT;
        } else if("LONG".equalsIgnoreCase(datatype)) {
            type = ColumnType.LONG;
        } else if("INT".equalsIgnoreCase(datatype)) {
            type = ColumnType.LONG;
        } else if("INTEGER".equalsIgnoreCase(datatype)) {
            type = ColumnType.LONG;
        } else if("bigint".equalsIgnoreCase(datatype)) {
            type = ColumnType.LONG;
        } else if("clob".equalsIgnoreCase(datatype)) {
            type = ColumnType.CLOB;
        } else if("blob".equalsIgnoreCase(datatype)) {
            type = ColumnType.BLOB;
        }

        return type;
    }

    public static String columnType2ControlType(String columnType) throws Exception {
        String type = "easyui-validatebox";
        if(columnType.equalsIgnoreCase("" + ColumnType.BLOB)) {
            throw new Exception("一般最好不要设置BLOB数据类型");
        } else {
            if(!columnType.equalsIgnoreCase("" + ColumnType.CLOB) && !columnType.equalsIgnoreCase("" + ColumnType.LONGTEXT) && !columnType.equalsIgnoreCase("" + ColumnType.MEDIUMTEXT)) {
                if(columnType.equalsIgnoreCase("" + ColumnType.TEXT)) {
                    type = "textarea";
                } else if(columnType.equalsIgnoreCase("" + ColumnType.DATE)) {
                    type = "easyui-datebox";
                } else if(columnType.equalsIgnoreCase("" + ColumnType.DATETIME)) {
                    type = "easyui-datetimebox";
                } else if(!columnType.equalsIgnoreCase("" + ColumnType.FLOAT) && !columnType.equalsIgnoreCase("" + ColumnType.INT) && !columnType.equalsIgnoreCase("" + ColumnType.LONG)) {
                    if(columnType.equalsIgnoreCase("" + ColumnType.EXT)) {
                        throw new Exception("忘了EXT是什么数据类型了");
                    }
                } else {
                    type = "easyui-numberbox";
                }
            } else {
                type = "editor";
            }

            return type;
        }
    }

    public static String parseControlType(String controlType) {
        String type = "easyui-validatebox";
        if("password".equals(controlType)) {
            type = "password";
        } else if("numberbox".equals(controlType)) {
            type = "easyui-numberbox";
        } else if("datebox".equals(controlType)) {
            type = "easyui-datebox";
        } else if("datetimebox".equals(controlType)) {
            type = "easyui-datetimebox";
        } else if("select".equals(controlType)) {
            type = "easyui-combobox";
        } else if("tree".equals(controlType)) {
            type = "easyui-combotree";
        } else if("upload".equals(controlType)) {
            type = "niu-upload";
        } else if("uploadFile".equals(controlType)) {
            type = "old-upload";
        } else if("editor".equals(controlType)) {
            type = "editor";
        } else if("area_text".equals(controlType)) {
            type = "textarea";
        } else if("timespinner".equals(controlType)) {
            type = "easyui-timespinner";
        } else if("numberspinner".equals(controlType)) {
            type = "easyui-numberspinner";
        } else if("chooseUser".equals(controlType)) {
            type = "chooseUser";
        }

        return type;
    }

    public static String createUIString(String display_type, String tablecode, String fieldname, boolean nullable, String hasTable, boolean is_readonly, boolean is_edit, String bindData, String default_value, int width, int height, int length, List<String> editFun, List<String> readonlyFun, Column col) {
        String name = tablecode + "__" + fieldname;
        if(default_value == null) {
            default_value = "";
        }

        if(is_readonly) {
            is_edit = true;
        }

        if("chooseUser".equals(display_type)) {
            return "<%=UI.chooseUser(\"" + name + "\", " + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\" , " + (!nullable && !is_readonly) + ", \"" + col.getOtherset() + "\", 1,\"add\") %>";
        } else if("old-upload".equals(display_type)) {
            return "<%=UI.createUploadFile(\"" + name + "\", " + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\" , " + (!nullable && !is_readonly) + ", \"*\", 1, false,\"add\") %>";
        } else if("editor".equals(display_type)) {
            return "<%=UI.createEditor(\"" + name + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",new UI_Op(\"width:99%;height:" + height + "px;\",\"\")) %>";
        } else if("textarea".equals(display_type)) {
            if(!is_edit) {
                editFun.add("document.getElementById(\"" + name + "\").disabled=true;");
            }

            return "<textarea id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-validatebox\" " + (is_readonly?"disabled=\"disabled\"":"") + " style=\"width: 99%;height: " + height + "px;\"  data-options=\"required:" + (!nullable && !is_readonly) + ",validType:\'length[0," + (length <= 0?10:length) + "]\'\" ><%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%></textarea>";
        } else if("easyui-datetimebox".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').datetimebox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').datetimebox({\'disabled\':\'true\'});");
            }

            return "<%=UI.createDateTimeBox(\"" + name + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px;\")%>";
        } else if("easyui-datebox".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').datebox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').datebox({\'disabled\':\'true\'});");
            }

            return "<%=UI.createDateBox(\"" + name + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px;\")%>";
        } else if("easyui-combotree".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createTree(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("multiTree".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createMultiTree(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("linkedTree".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createLinkedTree(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("treeBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createTreeBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("multiTreeBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createMultiTreeBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("multiLeafTreeBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createMultiLeafTreeBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("linkedTreeBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createLinkedTreeBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("leafTreeBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combotree({\'disabled\':\'true\'});");
            }

            return "<%=UI.createLeafTreeBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("easyui-combobox".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            return "sql".equalsIgnoreCase(col.getBindType())?"<%=UI.createSelectBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"{\'style\':\'width:" + width + "px\'}\") %>":"<%=UI.createSelect(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"{\'style\':\'width:" + width + "px\'}\") %>";
        } else if("multiSelect".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            return "<%=UI.createMultiSelect(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("selectBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            return "<%=UI.createSelectBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"{\'style\':\'width:" + width + "px\'}\") %>";
        } else if("multiSelectBySql".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').combobox({\'disabled\':\'true\'});");
            }

            return "<%=UI.createMultiSelectBySql(\"" + name + "\",\"" + bindData + "\"," + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"," + (!nullable && !is_readonly) + ",\"width:" + width + "px\") %>";
        } else if("easyui-numberbox".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').numberbox({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').numberbox({\'disabled\':\'true\'});");
            }

            return "<input id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-numberbox\" style=\"width: " + width + "px;\" type=\"text\" data-options=\"precision:" + col.getDecamial() + "," + (!nullable && !is_readonly?"required:true":"required:false") + "\" value=\'<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\'/>";
        } else if("easyui-numberspinner".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').numberspinner({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').numberspinner({\'disabled\':\'true\'});");
            }

            return "<input id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-numberspinner\" style=\"width: " + width + "px;\" type=\"text\" data-options=\"precision:" + col.getDecamial() + "," + (!nullable && !is_readonly?"required:true":"required:false") + "\" value=\'<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\'/>";
        } else if("easyui-timespinner".equals(display_type)) {
            if(is_readonly) {
                readonlyFun.add("$(\'#" + name + "\').timespinner({\'disabled\':\'true\'});");
            }

            if(!is_edit) {
                editFun.add("$(\'#" + name + "\').timespinner({\'disabled\':\'true\'});");
            }

            return "<input id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-timespinner\" style=\"width: " + width + "px;\" type=\"text\" data-options=\"" + (!nullable && !is_readonly?"required:true":"required:false") + "\" value=\'<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\'/>";
        } else if("niu-upload".equals(display_type)) {
            return "<input id=\"" + name + "\" name=\"" + name + "\" type=\"hidden\" value=\"<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\"><a href=\"javascript:uploadFile(\'" + name + "\',\'\',\'\');\" class=\"btn btn-xs btn-default btn-block\" style=\"width: 100px;\">上传文件</a><div id=\"_" + name + "\" name=\"_" + name + "\"><a href=\'" + default_value + "\' target=\'_blank\'><img src=\'" + default_value + "?imageView2/1/w/50/h/50\'></a></div>";
        } else if("password".equals(display_type)) {
            if(!is_edit) {
                editFun.add("document.getElementById(\"" + name + "\").disabled=true;");
            }

            return "<input id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-validatebox\" " + (is_readonly?"disabled=\"disabled\"":"") + " style=\"width: " + width + "px;\" type=\"password\" data-options=\"" + (!nullable && !is_readonly?"required:true":"required:false") + ",validType:\'length[0," + (length <= 0?10:length) + "]\'\" value=\'<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\'/>";
        } else {
            if(!is_edit) {
                editFun.add("document.getElementById(\"" + name + "\").disabled=true;");
            }

            return "<input id=\"" + name + "\" name=\"" + name + "\" class=\"easyui-validatebox\" " + (is_readonly?"disabled=\"disabled\"":"") + " style=\"width: " + width + "px;\" type=\"text\" data-options=\"" + (!nullable && !is_readonly?"required:true":"required:false") + ",validType:\'length[0," + (length <= 0?10:length) + "]\'\" value=\'<%=" + hasTable + "?" + tablecode + ".getStringValue(\"" + fieldname + "\"):\"" + default_value + "\"%>\'/>";
        }
    }
}
