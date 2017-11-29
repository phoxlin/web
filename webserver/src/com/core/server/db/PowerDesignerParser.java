package com.core.server.db;

import com.core.server.log.Logger;
import com.core.server.task.TaskDesignerUtils;
import com.core.server.tools.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class PowerDesignerParser {
    private Map<String, List<String>> tableInfos = new HashMap();
    private Map<String, String> dataInfos = new HashMap();
    private int labelWidth;
    private int inputWidth;
    private int spaceWidth;
    private int column = 0;
    private String path;
    private String outputDir;

    public PowerDesignerParser(String path, int labelWidth, int inputWidth, int spaceWidth, int column) throws Exception {
        this.path = path;
        this.labelWidth = labelWidth;
        this.inputWidth = inputWidth;
        this.spaceWidth = spaceWidth;
        this.column = column;
        Logger.info("Initializing PDM file:" + path);
        this.init();
        Logger.info("Initialized ......");
        Logger.info("Creating XML file ......");
        this.createXml();
        this.createCfg();
        this.createNewHtml();
        this.createHtml();
        Logger.info("Finished ......");
    }

    public PowerDesignerParser() {
    }

    public PowerDesignerParser(String path, String outputDir) throws Exception {
        this.path = path;
        this.outputDir = outputDir;
        if(outputDir == null || outputDir.length() <= 0 || "null".equalsIgnoreCase(outputDir)) {
            this.outputDir = Utils.getWebRootPath2();
        }

        Logger.info("Initializing PDM file:" + path);
        this.init();
        Logger.info("Initialized ......");
        Logger.info("Creating XML file ......");
        this.createXml();
        Logger.info("Finished ......");
    }

    private void createCfg() throws Exception {
        String tablecode;
        File var31;
        for(Iterator var2 = this.tableInfos.entrySet().iterator(); var2.hasNext(); Logger.info("Created cfg file:" + var31.getParent() + "/cfgs/" + tablecode + ".cfg")) {
            Map.Entry en = (Map.Entry)var2.next();
            String tableid = (String)en.getKey();
            List columnIds = (List)en.getValue();
            Document root = DocumentHelper.createDocument();
            Element table = root.addElement("table");
            tablecode = ((String)this.dataInfos.get(tableid + "tablecode")).toLowerCase();
            String tablename = (String)this.dataInfos.get(tableid + "tablename");
            table.addAttribute("title", tablename);
            table.addAttribute("pageSize", "20");
            table.addAttribute("description", tablename);
            table.addAttribute("sql", "select * from " + tablecode);
            table.addAttribute("mode", "default");
            table.addAttribute("refresh", "true");
            table.addAttribute("paging", "true");
            table.addAttribute("exportExcel", "true");
            Element toolbars = table.addElement("toolbars");
            Element detail = toolbars.addElement("toolbar");
            detail.addAttribute("class", "icon-detail");
            detail.addAttribute("text", "详细信息");
            detail.addAttribute("js", "detail");
            detail.addAttribute("name", tablecode + "_detail");
            detail.addAttribute("align", "left");
            detail.addAttribute("type", "button");
            detail.addAttribute("com_js", "");
            detail.addAttribute("role", "");
            detail.addAttribute("visible_code", tablecode + "_detail");
            Element add = toolbars.addElement("toolbar");
            add.addAttribute("class", "icon-add");
            add.addAttribute("text", "添加");
            add.addAttribute("js", "add");
            add.addAttribute("name", tablecode + "_add");
            add.addAttribute("align", "left");
            add.addAttribute("type", "button");
            add.addAttribute("com_js", "");
            add.addAttribute("role", "");
            add.addAttribute("visible_code", tablecode + "_add");
            Element edit = toolbars.addElement("toolbar");
            edit.addAttribute("class", "icon-edit");
            edit.addAttribute("text", "修改");
            edit.addAttribute("js", "edit");
            edit.addAttribute("name", tablecode + "_edit");
            edit.addAttribute("align", "left");
            edit.addAttribute("type", "button");
            edit.addAttribute("com_js", "");
            edit.addAttribute("role", "");
            edit.addAttribute("visible_code", tablecode + "_edit");
            Element del = toolbars.addElement("toolbar");
            del.addAttribute("class", "icon-del");
            del.addAttribute("text", "删除");
            del.addAttribute("js", "del");
            del.addAttribute("name", tablecode + "_del");
            del.addAttribute("align", "left");
            del.addAttribute("type", "button");
            del.addAttribute("com_js", "");
            del.addAttribute("role", "");
            del.addAttribute("visible_code", tablecode + "_del");
            Element jsFuns;
            String e;
            if(columnIds != null && columnIds.size() > 0) {
                jsFuns = table.addElement("columns");
                Iterator file = columnIds.iterator();

                while(file.hasNext()) {
                    String format = (String)file.next();
                    Element filepath = jsFuns.addElement("column");
                    String writer = ((String)this.dataInfos.get(format + "colcode")).toLowerCase();
                    String bw = (String)this.dataInfos.get(format + "comment");
                    String br = (String)this.dataInfos.get(format + "primarykey");
                    e = (String)this.dataInfos.get(format + "datatype");
                    filepath.addAttribute("code", writer);
                    filepath.addAttribute("display", bw);
                    filepath.addAttribute("width", "100");
                    filepath.addAttribute("bindType", "");
                    filepath.addAttribute("bindData", "");
                    filepath.addAttribute("type", e);
                    filepath.addAttribute("format", "");
                    if("Y".equalsIgnoreCase(br)) {
                        filepath.addAttribute("show", "false");
                    } else {
                        filepath.addAttribute("show", "true");
                    }

                    filepath.addAttribute("send", "true");
                    filepath.addAttribute("sort", "true");
                    filepath.addAttribute("reminder", "点击排序");
                    filepath.addAttribute("align", "center");
                }
            }

            jsFuns = table.addElement("jsFuns");
            jsFuns.addElement("fun").addAttribute("method", "");
            OutputFormat var30 = OutputFormat.createPrettyPrint();
            var31 = new File(this.path);
            File var32 = new File(var31.getParent() + "/cfgs/");
            if(!var32.exists()) {
                var32.mkdirs();
            }

            XMLWriter var33 = new XMLWriter(new FileWriter(var31.getParent() + "/cfgs/" + tablecode + ".cfg"), var30);
            var33.write(root);
            var33.close();
            BufferedWriter var34 = null;
            BufferedReader var35 = null;

            try {
                var35 = new BufferedReader(new FileReader(var31.getParent() + "/cfgs/" + tablecode + ".cfg"));
                e = null;
                ArrayList contents = new ArrayList();

                while((e = var35.readLine()) != null) {
                    contents.add(e);
                }

                var35.close();
                contents.add(1, "");
                contents.add(1, "<!-- =========================================================================== -->");
                contents.add(1, "<!-- column->sort 为 true 表示这列可以点击排序， false 表示不可以排序 -->");
                contents.add(1, "<!-- column->send 为 true 表示发送这列值到grid， false 表示不发送 -->");
                contents.add(1, "<!-- column->show 为 true 表示显示这列 ，false 表示不显示 -->");
                contents.add(1, "<!-- column->reminder 不设置默认为：点击排序 -->");
                contents.add(1, "<!-- column->bindType 包括[codetable(一般码表),treecodetable(树形码表),sql] -->");
                contents.add(1, "<!-- column->align 包括[left,right,center(默认)] -->");
                contents.add(1, "<!-- =========================================================================== -->");
                contents.add(1, "");
                contents.add(1, "<!-- =========================================================================== -->");
                contents.add(1, "<!-- toolbar->align 包括[left,right] -->");
                contents.add(1, "<!-- =========================================================================== -->");
                contents.add(1, "");
                var34 = new BufferedWriter(new FileWriter(var31.getParent() + "/cfgs/" + tablecode + ".cfg"));
                int i = 0;

                for(int l = contents.size(); i < l; ++i) {
                    var34.write((String)contents.get(i));
                    var34.newLine();
                }
            } catch (Exception var28) {
                var28.printStackTrace();
            } finally {
                if(var35 != null) {
                    var35.close();
                }

                if(var34 != null) {
                    var34.close();
                }

            }
        }

    }

    private void createNewHtml() throws IOException {
        String tablecode;
        File var36;
        for(Iterator var2 = this.tableInfos.entrySet().iterator(); var2.hasNext(); Logger.info("Created html file:" + var36.getParent() + "/newhtml/" + tablecode + ".html")) {
            Map.Entry en = (Map.Entry)var2.next();
            String tableid = (String)en.getKey();
            List columnIds = (List)en.getValue();
            tablecode = ((String)this.dataInfos.get(tableid + "tablecode")).toLowerCase();
            Element root = DocumentHelper.createElement("html");
            Element form = root.addElement("form").addAttribute("class", "l-form").addAttribute("id", tablecode + "FormObj").addAttribute("method", "post");
            Element view = root.addElement("div").addAttribute("id", tablecode + "DivObj");
            String e;
            if(columnIds != null && columnIds.size() > 0) {
                Element format = form.addElement("ul");
                Element file = view.addElement("ul");
                int filepath = 0;
                int writer = 0;

                for(int bw = columnIds.size(); writer < bw; ++writer) {
                    String br = (String)columnIds.get(writer);
                    e = ((String)this.dataInfos.get(br + "colcode")).toLowerCase();
                    String contents = ((String)this.dataInfos.get(br + "datatype")).toLowerCase();
                    String i = ((String)this.dataInfos.get(br + "length")).toLowerCase();
                    String l = (String)this.dataInfos.get(br + "mandatory");
                    String primarykey = (String)this.dataInfos.get(br + "primarykey");
                    String comment = (String)this.dataInfos.get(br + "comment");
                    String bindType = (String)this.dataInfos.get(br + "bindType");
                    String bindValue = (String)this.dataInfos.get(br + "bindValue");
                    Logger.info(bindType);
                    if(filepath % this.column == 0 && filepath != 0) {
                        format = form.addElement("ul");
                        file = view.addElement("ul");
                    }

                    Element hiddenInput;
                    if(!"Y".equalsIgnoreCase(primarykey)) {
                        ++filepath;
                        hiddenInput = format.addElement("li");
                        Element tr2_td1 = file.addElement("li");
                        hiddenInput.addAttribute("style", "width: " + this.labelWidth + "px; text-align: left;");
                        if("Y".equalsIgnoreCase(l)) {
                            hiddenInput.setText(comment + "(*)：");
                        } else {
                            hiddenInput.setText(comment + "：");
                        }

                        tr2_td1.addAttribute("style", "width: " + this.labelWidth + "px; text-align: left;");
                        Element valli = format.addElement("li").addAttribute("style", "width: " + this.inputWidth + "px; text-align: left;");
                        Element valDiv = valli.addElement("div").addAttribute("class", "l-text").addAttribute("style", "width: " + (this.inputWidth - 2) + "px;");
                        Element div;
                        StringBuilder sb;
                        if("long".equalsIgnoreCase(contents)) {
                            div = valDiv.addElement("input").addAttribute("class", "easyui-numberbox");
                            div.addAttribute("style", "width: " + (this.inputWidth - 6) + "px;");
                            div.addAttribute("type", "text").addAttribute("name", tablecode + "__" + e).addAttribute("id", tablecode + "__" + e);
                            sb = new StringBuilder();
                            sb.append("precision:0,");
                            if("Y".equalsIgnoreCase(l)) {
                                sb.append("required:true");
                            } else {
                                sb.append("required:false");
                            }

                            div.addAttribute("data-options", sb.toString()).addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                        } else if("float".equalsIgnoreCase(contents)) {
                            div = valDiv.addElement("input").addAttribute("class", "easyui-numberbox");
                            div.addAttribute("style", "width: " + (this.inputWidth - 6) + "px;");
                            div.addAttribute("type", "text").addAttribute("name", tablecode + "__" + e).addAttribute("id", tablecode + "__" + e);
                            sb = new StringBuilder();
                            sb.append("precision:2,");
                            if("Y".equalsIgnoreCase(l)) {
                                sb.append("required:true");
                            } else {
                                sb.append("required:false");
                            }

                            div.addAttribute("data-options", sb.toString()).addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                        } else {
                            StringBuilder var44;
                            if("datetime".equalsIgnoreCase(contents)) {
                                var44 = new StringBuilder();
                                var44.append("<%=UI.createDateTimeBox(\"" + tablecode + "__" + e);
                                var44.append("\",");
                                var44.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"width:" + (this.inputWidth - 4) + "px\") %>");
                                valDiv.setText(var44.toString());
                            } else if("date".equalsIgnoreCase(contents)) {
                                var44 = new StringBuilder();
                                var44.append("<%=UI.createDateBox(\"" + tablecode + "__" + e);
                                var44.append("\",");
                                var44.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"width:" + (this.inputWidth - 4) + "px\") %>");
                                valDiv.setText(var44.toString());
                            } else if("tree".equalsIgnoreCase(bindType)) {
                                var44 = new StringBuilder();
                                var44.append("<%=UI.createTree(\"" + tablecode + "__" + e + "\"" + "," + "\"" + bindValue + "\"");
                                var44.append(",");
                                var44.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"width:" + (this.inputWidth - 4) + "px\") %>");
                                valDiv.setText(var44.toString());
                            } else if("code".equalsIgnoreCase(bindType)) {
                                var44 = new StringBuilder();
                                var44.append("<%=UI.createSelect(\"" + tablecode + "__" + e + "\"" + "," + "\"" + bindValue + "\"");
                                var44.append(",");
                                var44.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"{\'style\':\'width:" + (this.inputWidth - 4) + "px\'}\") %>");
                                valDiv.setText(var44.toString());
                            } else if(!"treesql".equalsIgnoreCase(bindType) && !"codesql".equalsIgnoreCase(bindType)) {
                                div = valDiv.addElement("input").addAttribute("class", "easyui-validatebox");
                                div.addAttribute("style", "width: " + (this.inputWidth - 6) + "px;");
                                div.addAttribute("type", "text").addAttribute("name", tablecode + "__" + e).addAttribute("id", tablecode + "__" + e);
                                sb = new StringBuilder();
                                if("Y".equalsIgnoreCase(l)) {
                                    sb.append("required:true");
                                } else {
                                    sb.append("required:false");
                                }

                                sb.append(",validType:\'length[0," + i + "]\'");
                                div.addAttribute("data-options", sb.toString());
                                div.addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                                StringBuilder editor;
                                if(!"20".equals(i) && !"1".equalsIgnoreCase(i)) {
                                    if(Integer.parseInt(i) >= 100) {
                                        editor = new StringBuilder();
                                        editor.append("<%--");
                                        editor.append("<%=UI.createEditor(\"" + tablecode + "__" + e + "\"");
                                        editor.append(",");
                                        editor.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",new UI_Op(\"width:" + (this.inputWidth - 6) + "px\",\"\")) %>");
                                        editor.append("--%>");
                                        valDiv.addComment(editor.toString());
                                    }
                                } else {
                                    editor = new StringBuilder();
                                    editor.append("<%--");
                                    editor.append("<%=UI.createSelect(\"" + tablecode + "__" + e + "\"" + "," + "\"PUB_Cxxx\"");
                                    editor.append(",");
                                    editor.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"{\'style\':\'width:" + (this.inputWidth - 4) + "px\'}\") %>");
                                    editor.append("--%>");
                                    valDiv.addComment(editor.toString());
                                    StringBuilder combotree = new StringBuilder();
                                    combotree.append("<%--");
                                    combotree.append("<%=UI.createTree(\"" + tablecode + "__" + e + "\"" + "," + "\"PUB_Txxx\"");
                                    combotree.append(",");
                                    combotree.append("has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "," + ("Y".equalsIgnoreCase(l)?"true":"false") + ",\"width:" + (this.inputWidth - 4) + "px\") %>");
                                    combotree.append("--%>");
                                    valDiv.addComment(combotree.toString());
                                }

                                valDiv.addElement("div").addAttribute("class", "l-text-l").setText("");
                                valDiv.addElement("div").addAttribute("class", "l-text-r").setText("");
                            }
                        }

                        tr2_td1.addElement("label").addAttribute("style", "width:" + (this.labelWidth - 2) + "px").setText(comment + "：");
                        div = file.addElement("li").addAttribute("style", "width:" + this.inputWidth + "px,text-align: left;").addElement("div").addAttribute("class", "l-text").addAttribute("style", "width:" + (this.inputWidth - 2) + "px");
                        div.addText("<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                        format.addElement("li").addAttribute("style", "width: " + this.spaceWidth + "px;").setText("");
                        file.addElement("li").addAttribute("style", "width: " + this.spaceWidth + "px;").setText("");
                    } else {
                        hiddenInput = DocumentHelper.createElement("input");
                        hiddenInput.addAttribute("id", tablecode + "__" + e).addAttribute("name", tablecode + "__" + e).addAttribute("type", "hidden").addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                        form.elements().add(0, hiddenInput);
                    }
                }
            }

            OutputFormat var35 = OutputFormat.createPrettyPrint();
            var36 = new File(this.path);
            File var37 = new File(var36.getParent() + "/newhtml/");
            if(!var37.exists()) {
                var37.mkdirs();
            }

            XMLWriter var38 = new XMLWriter(new FileWriter(var36.getParent() + "/newhtml/" + tablecode + ".html"), var35);
            var38.write(root);
            var38.close();
            BufferedWriter var39 = null;
            BufferedReader var40 = null;

            try {
                var40 = new BufferedReader(new FileReader(var36.getParent() + "/newhtml/" + tablecode + ".html"));
                e = null;
                ArrayList var41 = new ArrayList();

                while((e = var40.readLine()) != null) {
                    e = e.replaceAll("&lt;", "<");
                    e = e.replaceAll("&gt;", ">");
                    e = e.replaceAll("&quot;", "\"");
                    e = e.replaceAll("\"<%=", "\'<%=");
                    e = e.replaceAll("%>\"", "%>\'");
                    e = e.replaceAll("<!--<%--", "<%--");
                    e = e.replaceAll("--%>-->", "--%>");
                    e = e.replaceAll("--%>-->", "--%>");
                    if(e.contains("<%=UI")) {
                        var41.add(e.replaceAll("<%=UI", "\r\n<%=UI"));
                    } else {
                        var41.add(e);
                    }
                }

                var40.close();
                var41.add(1, "%>");
                var41.add(1, "  boolean has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "=" + tablecode + "!=null&&" + tablecode + ".getResultCount()>0;");
                var41.add(1, "  Entity " + tablecode + "=(Entity)request.getAttribute(\"" + tablecode + "\");");
                var41.add(1, "<% ");
                var39 = new BufferedWriter(new FileWriter(var36.getParent() + "/newhtml/" + tablecode + ".html"));
                int var42 = 0;

                for(int var43 = var41.size(); var42 < var43; ++var42) {
                    var39.write((String)var41.get(var42));
                    var39.newLine();
                }
            } catch (Exception var33) {
                var33.printStackTrace();
            } finally {
                if(var40 != null) {
                    var40.close();
                }

                if(var39 != null) {
                    var39.close();
                }

            }
        }

    }

    private void createHtml() throws Exception {
        String tablecode;
        File var32;
        for(Iterator var2 = this.tableInfos.entrySet().iterator(); var2.hasNext(); Logger.info("Created html file:" + var32.getParent() + "/html/" + tablecode + ".html")) {
            Map.Entry en = (Map.Entry)var2.next();
            String tableid = (String)en.getKey();
            List columnIds = (List)en.getValue();
            tablecode = ((String)this.dataInfos.get(tableid + "tablecode")).toLowerCase();
            Element root = DocumentHelper.createElement("html");
            Element html = root.addElement("form").addAttribute("id", tablecode + "FormObj").addAttribute("method", "post");
            Element table = html.addElement("table").addAttribute("class", "table_detail");
            Element table2 = root.addElement("table").addAttribute("class", "table_detail");
            String e;
            if(columnIds != null && columnIds.size() > 0) {
                Element format = table.addElement("tr");
                Element file = table2.addElement("tr");
                format.addAttribute("class", "d_tr");
                file.addAttribute("class", "d_tr");
                int filepath = 0;
                int writer = 0;

                for(int bw = columnIds.size(); writer < bw; ++writer) {
                    String br = (String)columnIds.get(writer);
                    e = ((String)this.dataInfos.get(br + "colcode")).toLowerCase();
                    String contents = ((String)this.dataInfos.get(br + "datatype")).toLowerCase();
                    String i = ((String)this.dataInfos.get(br + "length")).toLowerCase();
                    String l = (String)this.dataInfos.get(br + "mandatory");
                    String primarykey = (String)this.dataInfos.get(br + "primarykey");
                    String comment = (String)this.dataInfos.get(br + "comment");
                    if(filepath % 2 == 0 && filepath != 0) {
                        format = table.addElement("tr");
                        format.addAttribute("class", "d_tr");
                        file = table2.addElement("tr");
                        file.addAttribute("class", "d_tr");
                    }

                    if(!"Y".equalsIgnoreCase(primarykey)) {
                        ++filepath;
                        Element tr_td1 = format.addElement("td");
                        Element tr2_td1 = file.addElement("td");
                        tr_td1.addElement("label").addAttribute("for", tablecode + "__" + e).setText(comment + "：");
                        Element input = tr_td1.addElement("input").addAttribute("class", "easyui-validatebox");
                        input.addAttribute("style", "width: 122px;");
                        input.addAttribute("type", "text").addAttribute("name", tablecode + "__" + e).addAttribute("id", tablecode + "__" + e);
                        StringBuilder sb = new StringBuilder();
                        if("Y".equalsIgnoreCase(l)) {
                            sb.append("required:true");
                        } else {
                            sb.append("required:false");
                        }

                        if("long".equalsIgnoreCase(contents)) {
                            sb.append(",validType:\'long\'");
                        } else if("float".equalsIgnoreCase(contents)) {
                            sb.append(",validType:\'float[2]\'");
                        } else {
                            sb.append(",validType:\'length[0," + i + "]\'");
                        }

                        input.addAttribute("data-options", sb.toString());
                        input.addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                        tr2_td1.addElement("label").addAttribute("for", tablecode + "__" + e).setText(comment);
                        Element div = tr2_td1.addElement("div").addAttribute("class", "d_input_view");
                        div.addText("<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                    } else {
                        html.addElement("input").addAttribute("id", tablecode + "__" + e).addAttribute("name", tablecode + "__" + e).addAttribute("type", "hidden").addAttribute("value", "<%=has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "?" + tablecode + ".getStringValue(\"" + e.toLowerCase() + "\"):\"\"" + "%>");
                    }
                }
            }

            OutputFormat var31 = OutputFormat.createPrettyPrint();
            var32 = new File(this.path);
            File var33 = new File(var32.getParent() + "/html/");
            if(!var33.exists()) {
                var33.mkdirs();
            }

            XMLWriter var34 = new XMLWriter(new FileWriter(var32.getParent() + "/html/" + tablecode + ".html"), var31);
            var34.write(root);
            var34.close();
            BufferedWriter var35 = null;
            BufferedReader var36 = null;

            try {
                var36 = new BufferedReader(new FileReader(var32.getParent() + "/html/" + tablecode + ".html"));
                e = null;
                ArrayList var37 = new ArrayList();

                while((e = var36.readLine()) != null) {
                    e = e.replaceAll("&lt;", "<");
                    e = e.replaceAll("&gt;", ">");
                    e = e.replaceAll("&quot;", "\"");
                    e = e.replaceAll("\"<%=", "\'<%=");
                    e = e.replaceAll("%>\"", "%>\'");
                    var37.add(e);
                }

                var36.close();
                var37.add(1, "%>");
                var37.add(1, "  boolean has" + tablecode.substring(0, 1).toUpperCase() + tablecode.substring(1) + "=" + tablecode + "!=null&&" + tablecode + ".getResultCount()>0;");
                var37.add(1, "  Entity " + tablecode + "=(Entity)request.getAttribute(\"" + tablecode + "\");");
                var37.add(1, "<% ");
                var35 = new BufferedWriter(new FileWriter(var32.getParent() + "/html/" + tablecode + ".html"));
                int var38 = 0;

                for(int var39 = var37.size(); var38 < var39; ++var38) {
                    var35.write((String)var37.get(var38));
                    var35.newLine();
                }
            } catch (Exception var29) {
                var29.printStackTrace();
            } finally {
                if(var36 != null) {
                    var36.close();
                }

                if(var35 != null) {
                    var35.close();
                }

            }
        }

    }

    private void createXml() throws Exception {
        Iterator var2 = this.tableInfos.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry en = (Map.Entry)var2.next();
            String tableid = (String)en.getKey();
            List columnIds = (List)en.getValue();
            Document root = DocumentHelper.createDocument();
            Element table = root.addElement("table");
            String tablecode = ((String)this.dataInfos.get(tableid + "tablecode")).toLowerCase();
            String tablename = ((String)this.dataInfos.get(tableid + "tablename")).toLowerCase();
            table.addAttribute("name", tablecode);
            table.addAttribute("comment", tablename);
            if(columnIds != null && columnIds.size() > 0) {
                Iterator filepath = columnIds.iterator();

                while(filepath.hasNext()) {
                    String format = (String)filepath.next();
                    Element writer = table.addElement("column");
                    String code = ((String)this.dataInfos.get(format + "colcode")).toLowerCase();
                    String datatype = ((String)this.dataInfos.get(format + "datatype")).toLowerCase();
                    String length = ((String)this.dataInfos.get(format + "length")).toLowerCase();
                    String mandatory = (String)this.dataInfos.get(format + "mandatory");
                    String comment = (String)this.dataInfos.get(format + "comment");
                    String primarykey = (String)this.dataInfos.get(format + "primarykey");
                    String autoC = (String)this.dataInfos.get(format + "autoC");
                    writer.addAttribute("name", code);
                    writer.addAttribute("field", code);
                    writer.addAttribute("type", datatype);
                    if("date".equalsIgnoreCase(datatype)) {
                        writer.addAttribute("format", "yyyy-MM-dd");
                    } else if("datetime".equalsIgnoreCase(datatype)) {
                        writer.addAttribute("format", "yyyy-MM-dd HH:mm:ss");
                    }

                    writer.addAttribute("comment", comment);
                    if(!length.equalsIgnoreCase("0")) {
                        writer.addAttribute("length", length);
                    }

                    writer.addAttribute("nullable", "Y".equalsIgnoreCase(mandatory)?"false":"true");
                    writer.addAttribute("controlType", TaskDesignerUtils.columnType2ControlType(datatype));
                    writer.addAttribute("input_tablename", tablecode);
                    writer.addAttribute("width", "100");
                    writer.addAttribute("min", "0");
                    writer.addAttribute("max", "10000");
                    if(datatype.equalsIgnoreCase("float")) {
                        writer.addAttribute("decamial", "2");
                    } else {
                        writer.addAttribute("decamial", "0");
                    }

                    writer.addAttribute("otherset", "");
                    writer.addAttribute("placeholder", comment);
                    writer.addAttribute("search_compare", "");
                    if("Y".equalsIgnoreCase(primarykey)) {
                        writer.addAttribute("iskey", "true");
                        if("Y".equalsIgnoreCase(autoC)) {
                            writer.addAttribute("isAutoGenerate", "true");
                        } else {
                            writer.addAttribute("isAutoGenerate", "false");
                        }
                    }
                }
            }

            OutputFormat format1 = OutputFormat.createPrettyPrint();
            File filepath1 = new File(this.outputDir + "xml/");
            if(!filepath1.exists()) {
                filepath1.mkdirs();
            }

            XMLWriter writer1 = new XMLWriter(new FileWriter(this.outputDir + "xml/" + tablecode + "-" + tablename + ".xml"), format1);
            writer1.write(root);
            writer1.close();
            Logger.info("Created xml file:" + this.outputDir + "xml/" + tablecode + ".xml");
        }

    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void init() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new File(this.path));
        List tables = doc.selectNodes("//c:Tables/o:Table");
        int i = 0;

        for(int l = tables.size(); i < l; ++i) {
            Element table = (Element)tables.get(i);
            String tableid = table.valueOf("./a:ObjectID/text()");
            String tableCode = table.valueOf("./a:Code/text()");
            String tableName = table.valueOf("./a:Name/text()");
            this.dataInfos.put(tableid + "tablecode", tableCode);
            if(tableName == null || tableName.length() <= 0) {
                tableName = tableCode;
            }

            this.dataInfos.put(tableid + "tablename", tableName);
            List cols = table.selectNodes("./c:Columns/o:Column");
            Iterator pCol = cols.iterator();

            String colid;
            while(pCol.hasNext()) {
                Element primaryKeyRef = (Element)pCol.next();
                colid = primaryKeyRef.valueOf("./a:ObjectID/text()");
                String colname = primaryKeyRef.valueOf("./a:Name/text()");
                String colcode = primaryKeyRef.valueOf("./a:Code/text()");
                String datatype = primaryKeyRef.valueOf("./a:DataType/text()");
                String length = primaryKeyRef.valueOf("./a:Length/text()");
                String mandatory = primaryKeyRef.valueOf("./a:Mandatory/text()");
                String comment = primaryKeyRef.valueOf("./a:Comment/text()");
                String refid = primaryKeyRef.attributeValue("Id");
                String bindType = primaryKeyRef.valueOf("./a:Format/text()");
                String bindValue = primaryKeyRef.valueOf("./a:PhysicalOptions/text()");
                List columns = (List)this.tableInfos.get(tableid);
                if(columns == null) {
                    columns = new ArrayList();
                    this.tableInfos.put(tableid, columns);
                }

                ((List)columns).add(colid);
                if(colname == null || colname.length() <= 0) {
                    colname = colcode;
                }

                if(comment == null || comment.length() <= 0) {
                    comment = colname;
                }

                if("1".equals(mandatory)) {
                    mandatory = "Y";
                } else {
                    mandatory = "N";
                }

                if(length == null || length.length() <= 0) {
                    length = "0";
                }

                datatype = datatype.split("\\(")[0];
                if("TEXT".equalsIgnoreCase(datatype)) {
                    length = "65000";
                } else if("MEDIUMTEXT".equalsIgnoreCase(datatype)) {
                    length = "15000000";
                } else if("LONGTEXT".equalsIgnoreCase(datatype)) {
                    length = "4200000000";
                }

                datatype = TaskDesignerUtils.dbtype2ColumnType(datatype).toString().toLowerCase();
                this.dataInfos.put(colid + "colname", colname);
                this.dataInfos.put(colid + "colcode", colcode);
                this.dataInfos.put(colid + "datatype", datatype);
                this.dataInfos.put(colid + "length", length);
                this.dataInfos.put(colid + "mandatory", mandatory);
                this.dataInfos.put(colid + "comment", comment);
                this.dataInfos.put(colid + "refid", refid);
                if(bindType != null && bindType.trim().length() > 0 && bindValue != null && bindValue.trim().length() > 0) {
                    this.dataInfos.put(colid + "bindType", bindType.trim());
                    this.dataInfos.put(colid + "bindValue", bindValue.trim());
                }
            }

            try {
                String var25 = table.valueOf("./c:PrimaryKey/o:Key/@Ref");
                if(var25 != null) {
                    String var26 = table.valueOf("./c:Keys/o:Key[@Id=\'" + var25 + "\']/c:Key.Columns/o:Column/@Ref");
                    if(var26 != null) {
                        colid = table.valueOf("./c:Columns/o:Column[@Id=\'" + var26 + "\']/a:ObjectID/text()");
                        this.dataInfos.put(colid + "primarykey", "Y");
                        this.dataInfos.put(colid + "autoC", "Y");
                    }
                }
            } catch (Exception var24) {
                ;
            }
        }

    }

    public Map<String, List<String>> getTableInfos() {
        return this.tableInfos;
    }

    public Map<String, String> getDataInfos() {
        return this.dataInfos;
    }
}
