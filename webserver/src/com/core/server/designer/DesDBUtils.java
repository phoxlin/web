package com.core.server.designer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.*;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DesDBUtils {
    public static String html2Xml(String htmlContent) {
        StringBuilder sb = new StringBuilder();
        String[] lines = htmlContent.split("<input ");
        sb.append(lines[0]);

        for(int i = 1; i < lines.length; ++i) {
            String line = "<input " + lines[i];
            int index = line.indexOf(">");
            String head = line.substring(0, index) + " />";
            String tail = line.substring(index + 1);
            sb.append(head);
            sb.append(tail);
        }

        return sb.toString();
    }

    public static List<Map<String, String>> parseColumnLayoutData(String layoutdata) throws Exception {
        layoutdata = html2Xml(layoutdata);
        Document doc = DocumentHelper.parseText(layoutdata);
        ArrayList list = new ArrayList();
        List inputs = doc.selectNodes("/div/div/div/div/div/div//input");
        HashMap m = null;

        String e;
        String value;
        String name;
        for(Iterator index = inputs.iterator(); index.hasNext(); m.put(e, value)) {
            Element selects = (Element)index.next();
            e = selects.attributeValue("name");
            value = selects.attributeValue("value");
            if("name".equals(e)) {
                if(m != null) {
                    list.add(m);
                }

                m = new HashMap();
            }

            name = selects.attributeValue("checked");
            String os = selects.attributeValue("type");
            if("checkbox".equals(os)) {
                if("checked".equals(name)) {
                    value = "on";
                } else {
                    value = "off";
                }
            }
        }

        if(m != null) {
            list.add(m);
        }

        List var15 = doc.selectNodes("/div/div/div/div/div/div//select");
        int var16 = -1;
        Iterator var18 = var15.iterator();

        while(var18.hasNext()) {
            Element var17 = (Element)var18.next();
            name = var17.attributeValue("name");
            List var19 = var17.elements();
            String value1 = "";
            Iterator var13 = var19.iterator();

            while(var13.hasNext()) {
                Element map = (Element)var13.next();
                String selected = map.attributeValue("selected");
                if(selected != null && selected.length() > 0) {
                    value1 = map.attributeValue("value");
                    if(value1 == null) {
                        value1 = map.getTextTrim();
                    }
                }
            }

            if("controlType".equals(name)) {
                ++var16;
            }

            Map var20 = (Map)list.get(var16);
            var20.put(name, value1);
        }

        return list;
    }
}
