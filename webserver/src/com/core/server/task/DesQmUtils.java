package com.core.server.task;

import com.core.server.designer.DesDBUtils;
import com.core.server.tools.Utils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class DesQmUtils {
    public static Map<String, Object> parseQmContent(String content) throws Exception {
        HashMap mm = new HashMap();
        content = DesDBUtils.html2Xml(content);
        content = "<div>" + content + "</div>";

        try {
            Document e = DocumentHelper.parseText(content);
            int left = 0;
            int right = 12;

            try {
                int decisList = Integer.parseInt(e.valueOf(".//input[@name=\'left_col_number\']/@value"));
                int m = Integer.parseInt(e.valueOf(".//input[@name=\'right_col_number\']/@value"));
                if(decisList >= 0 && m >= 0 && decisList + m == 12) {
                    left = decisList;
                    right = m;
                }
            } catch (Exception var12) {
                ;
            }

            mm.put("left_col_number", Integer.valueOf(left));
            mm.put("right_col_number", Integer.valueOf(right));
            List decisList1 = e.selectNodes("/div/div[@id=\'decision_content\']//input");
            HashMap m1 = null;
            ArrayList decis = new ArrayList();

            String div;
            String value;
            for(Iterator divList = decisList1.iterator(); divList.hasNext(); m1.put(div, value)) {
                Element contents = (Element)divList.next();
                div = contents.attributeValue("name");
                value = contents.attributeValue("value");
                if("decision_name".equals(div)) {
                    if(m1 != null) {
                        decis.add(m1);
                    }

                    m1 = new HashMap();
                }
            }

            if(m1 != null) {
                decis.add(m1);
            }

            mm.put("decision", decis);
            ArrayList contents1 = new ArrayList();
            List divList1 = e.selectNodes("/div/div[@id=\'task_content\']/div");
            Iterator value1 = divList1.iterator();

            while(value1.hasNext()) {
                Element div1 = (Element)value1.next();
                contents1.add(parseElement(div1));
            }

            mm.put("task_content", contents1);
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return mm;
    }

    private static Map<String, Object> parseElement(Element div) throws Exception {
        Element input = (Element)div.selectSingleNode(".//input[@name=\'start\']");
        String type = input.attributeValue("value");
        return "grid".equalsIgnoreCase(type)?parseGrid(div):parseLegend(div);
    }

    private static Map<String, Object> parseGrid(Element div) throws Exception {
        HashMap m = new HashMap();
        String initSqlValue = div.valueOf(".//input[@name=\'initSqlinput\']/@value");
        boolean rdb = Utils.isTrue(div.valueOf(".//input[@name=\'gridtype\']/@checked"));
        boolean autoLoadingData = Utils.isTrue(div.valueOf(".//input[@name=\'autoLoadingData\']/@checked"));
        String gridName = div.valueOf(".//input[@name=\'gridName\']/@value");
        String gridCode = div.valueOf(".//input[@name=\'gridCode\']/@value");
        String loadingTableName = div.valueOf(".//input[@name=\'loadingTableName\']/@value");
        String column_show_num = div.valueOf(".//input[@name=\'column_show_num\']/@value");
        String spaceWidth = div.valueOf(".//input[@name=\'spaceWidth\']/@value");
        String titleWidth = div.valueOf(".//input[@name=\'titleWidth\']/@value");
        String inputWidth = div.valueOf(".//input[@name=\'inputWidth\']/@value");
        String winWidth = div.valueOf(".//input[@name=\'win_width\']/@value");
        String winHeight = div.valueOf(".//input[@name=\'win_height\']/@value");
        ArrayList bars = new ArrayList();
        ArrayList contents = new ArrayList();
        m.put("type", "grid");
        m.put("initSqlValue", initSqlValue);
        m.put("rdb", Boolean.valueOf(rdb));
        m.put("entity", loadingTableName);
        m.put("autoLoadingData", Boolean.valueOf(autoLoadingData));
        m.put("gridName", gridName);
        m.put("gridCode", gridCode);
        m.put("column_show_num", column_show_num);
        m.put("spaceWidth", spaceWidth);
        m.put("titleWidth", titleWidth);
        m.put("inputWidth", inputWidth);
        m.put("winWidth", winWidth);
        m.put("winHeight", winHeight);
        m.put("bars", bars);
        m.put("contents", contents);
        List toolsElement = div.selectNodes(".//div[@data-type=\'toolbarContent\']/div/div");
        Iterator ele = toolsElement.iterator();

        while(ele.hasNext()) {
            Element gridContentElement = (Element)ele.next();
            Map barMap = parseToolbarEle(gridContentElement);
            bars.add(barMap);
        }

        List gridContentElement1 = div.selectNodes(".//div[@data-type=\'gridContent\']/div/div");
        Iterator barMap1 = gridContentElement1.iterator();

        while(barMap1.hasNext()) {
            Element ele1 = (Element)barMap1.next();
            Map inputMap = parseInputEle(ele1);
            contents.add(inputMap);
        }

        return m;
    }

    private static Map<String, Object> parseInputEle(Element div) throws Exception {
        HashMap m = new HashMap();
        String name = div.valueOf(".//input[@name=\'name\']/@value");
        String code = div.valueOf(".//input[@name=\'code\']/@value");
        String controlType = getSelectValue(div.selectSingleNode(".//select[@name=\'controlType\']"));
        String width = div.valueOf(".//input[@name=\'width\']/@value");
        String length = div.valueOf(".//input[@name=\'length\']/@value");
        boolean nullable = Utils.isTrue(div.valueOf(".//input[@name=\'nullable\']/@checked"));
        boolean list = Utils.isTrue(div.valueOf(".//input[@name=\'list\']/@checked"));
        boolean sort = Utils.isTrue(div.valueOf(".//input[@name=\'sort\']/@checked"));
        boolean ignore = Utils.isTrue(div.valueOf(".//input[@name=\'ignore\']/@checked"));
        boolean edit = Utils.isTrue(div.valueOf(".//input[@name=\'edit\']/@checked"));
        boolean hidden = Utils.isTrue(div.valueOf(".//input[@name=\'hidden\']/@checked"));
        boolean readonly = Utils.isTrue(div.valueOf(".//input[@name=\'readonly\']/@checked"));
        boolean line = Utils.isTrue(div.valueOf(".//input[@name=\'line\']/@checked"));
        boolean query = Utils.isTrue(div.valueOf(".//input[@name=\'query\']/@checked"));
        String input_tablename = div.valueOf(".//input[@name=\'input_tablename\']/@value");
        String placeholder = div.valueOf(".//input[@name=\'placeholder\']/@value");
        String format = div.valueOf(".//input[@name=\'format\']/@value");
        String decamial = getSelectValue(div.selectSingleNode(".//select[@name=\'decamial\']"));
        String min = div.valueOf(".//input[@name=\'min\']/@value");
        String max = div.valueOf(".//input[@name=\'max\']/@value");
        String bindtype = getSelectValue(div.selectSingleNode(".//select[@name=\'bindtype\']"));
        String binddata = div.valueOf(".//input[@name=\'binddata\']/@value");
        String defaultValue = div.valueOf(".//input[@name=\'defaultValue\']/@value");
        String otherset = div.valueOf(".//input[@name=\'otherset\']/@value");
        String col_class = div.valueOf(".//input[@name=\'col_class\']/@value");
        String col_style = div.valueOf(".//input[@name=\'col_style\']/@value");
        String cols = div.valueOf(".//input[@name=\'cols\']/@value");
        String labelCols = div.valueOf(".//input[@name=\'labelCols\']/@value");
        String fieldCols = div.valueOf(".//input[@name=\'fieldCols\']/@value");
        String InputHeight = div.valueOf(".//input[@name=\'InputHeight\']/@value");
        String InputSpanCol = div.valueOf(".//input[@name=\'InputSpanCol\']/@value");
        String search_compare = getSelectValue(div.selectSingleNode(".//select[@name=\'search_compare\']"));
        m.put("name", name);
        m.put("code", code);
        m.put("controlType", controlType);
        m.put("width", width);
        m.put("length", length);
        m.put("nullable", Boolean.valueOf(nullable));
        m.put("list", Boolean.valueOf(list));
        m.put("sort", Boolean.valueOf(sort));
        m.put("ignore", Boolean.valueOf(ignore));
        m.put("edit", Boolean.valueOf(edit));
        m.put("hidden", Boolean.valueOf(hidden));
        m.put("readonly", Boolean.valueOf(readonly));
        m.put("line", Boolean.valueOf(line));
        m.put("query", Boolean.valueOf(query));
        m.put("input_tablename", input_tablename);
        m.put("placeholder", placeholder);
        m.put("format", format);
        m.put("decamial", decamial);
        m.put("min", min);
        m.put("max", max);
        m.put("bindtype", bindtype);
        m.put("binddata", binddata);
        m.put("defaultValue", defaultValue);
        m.put("col_class", col_class);
        m.put("col_style", col_style);
        m.put("cols", cols);
        m.put("labelCols", labelCols);
        m.put("fieldCols", fieldCols);
        m.put("InputHeight", InputHeight);
        m.put("InputSpanCol", InputSpanCol);
        m.put("search_compare", search_compare);
        m.put("otherset", otherset);
        return m;
    }

    public static String getSelectValue(Node node) {
        Element el = (Element)node;
        List ops = el.selectNodes("./option");
        Iterator var4 = ops.iterator();

        Element op;
        String selected;
        do {
            if(!var4.hasNext()) {
                return "";
            }

            op = (Element)var4.next();
            selected = op.attributeValue("selected");
        } while(selected == null || selected.length() <= 0);

        String val = op.attributeValue("value");
        if(val == null) {
            val = op.getTextTrim();
        }

        return val;
    }

    private static Map<String, String> parseToolbarEle(Element div) throws Exception {
        HashMap m = new HashMap();
        String toolbar_display = div.valueOf(".//input[@name=\'toolbar_display\']/@value");
        String toolbar_class = div.valueOf(".//input[@name=\'toolbar_class\']/@value");
        String toolbar_js = div.valueOf(".//input[@name=\'toolbar_js\']/@value");
        String toolbar_name = div.valueOf(".//input[@name=\'toolbar_name\']/@value");
        m.put("toolbar_display", toolbar_display);
        m.put("toolbar_class", toolbar_class);
        m.put("toolbar_js", toolbar_js);
        m.put("toolbar_name", toolbar_name);
        return m;
    }

    private static Map<String, Object> parseLegend(Element div) throws Exception {
        HashMap m = new HashMap();
        boolean rdb = Utils.isTrue(div.valueOf(".//input[@name=\'gridtype\']/@checked"));
        boolean autoLoadingData = Utils.isTrue(div.valueOf(".//input[@name=\'autoLoadingData\']/@checked"));
        String legendTableName = div.valueOf(".//input[@name=\'legendTableName\']/@value");
        String legendDisplay = div.valueOf(".//input[@name=\'legendDisplay\']/@value");
        String loadingTableName = div.valueOf(".//input[@name=\'loadingTableName\']/@value");
        List rowContentElement = div.selectNodes(".//div[@data-type=\'rowContent\']");
        ArrayList rows = new ArrayList();
        Iterator var10 = rowContentElement.iterator();

        while(var10.hasNext()) {
            Element ele = (Element)var10.next();
            List row = parseRowEle(ele);
            rows.add(row);
        }

        m.put("type", "legend");
        m.put("rdb", Boolean.valueOf(rdb));
        m.put("entity", loadingTableName);
        m.put("autoLoadingData", Boolean.valueOf(autoLoadingData));
        m.put("legendTableName", legendTableName);
        m.put("legendDisplay", legendDisplay);
        m.put("rows", rows);
        return m;
    }

    private static List<Map<String, Object>> parseRowEle(Element div) throws Exception {
        ArrayList list = new ArrayList();
        List gridContentElement = div.selectNodes("./div/div");
        Iterator var4 = gridContentElement.iterator();

        while(var4.hasNext()) {
            Element ele = (Element)var4.next();
            Map inputMap = parseInputEle(ele);
            list.add(inputMap);
        }

        return list;
    }
}
