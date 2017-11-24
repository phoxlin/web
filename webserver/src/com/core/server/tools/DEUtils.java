package com.core.server.tools;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class DEUtils {
    public static Document json2doc(JSONObject o) {
        Document doc = new Document();
        if(o != null && o.keySet().size() > 0) {
            Set keys = o.keySet();
            Iterator var4 = keys.iterator();

            while(var4.hasNext()) {
                String str = (String)var4.next();
                Object oo = o.get(str);
                if(oo instanceof JSONObject) {
                    JSONObject var13 = (JSONObject)oo;
                    doc.append(str, json2doc(var13));
                } else if(!(oo instanceof JSONArray)) {
                    doc.append(str, oo);
                } else {
                    ArrayList ar = new ArrayList();
                    ArrayList alist = new ArrayList();
                    boolean notJson = false;
                    JSONArray li = (JSONArray)oo;

                    for(int i = 0; i < li.length(); ++i) {
                        try {
                            JSONObject e = li.getJSONObject(i);
                            ar.add(json2doc(e));
                        } catch (Exception var12) {
                            if(i == 0) {
                                notJson = true;
                            }

                            alist.add(li.getString(i));
                        }
                    }

                    if(notJson) {
                        doc.append(str, alist);
                    } else {
                        doc.append(str, ar);
                    }
                }
            }

            return doc;
        } else {
            return doc;
        }
    }
}
