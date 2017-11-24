package com.core.server.c;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Code {
    private List<CItem> items = new ArrayList();
    private String type;
    private String note;
    private CType cType;
    private String remark;

    public Code() {
    }

    public boolean containKey(String code) {
        Iterator var3 = this.items.iterator();

        while(var3.hasNext()) {
            CItem item = (CItem)var3.next();
            if(code.equals(item.getCode())) {
                return true;
            }
        }

        return false;
    }

    public CItem getItem(String code) {
        int i = 0;

        for(int l = this.items.size(); i < l; ++i) {
            CItem ci = (CItem)this.items.get(i);
            if(ci.getCode().equals(code)) {
                return ci;
            }
        }

        return null;
    }

    public CItem getItemByNote(String note) {
        int i = 0;

        for(int l = this.items.size(); i < l; ++i) {
            CItem ci = (CItem)this.items.get(i);
            if(ci.getNote().equals(note)) {
                return ci;
            }
        }

        return null;
    }

    public String getCode(String note) {
        int i = 0;

        for(int l = this.items.size(); i < l; ++i) {
            CItem ci = (CItem)this.items.get(i);
            if(ci.getNote().equals(note)) {
                return ci.getCode();
            }
        }

        return "";
    }

    public String getNote(String code) {
        int i = 0;

        for(int l = this.items.size(); i < l; ++i) {
            CItem ci = (CItem)this.items.get(i);
            if(ci.getCode().equals(code)) {
                return ci.getNote();
            }
        }

        return "";
    }

    public List<CItem> getItems() {
        return this.items;
    }

    public void addItem(CItem item) {
        if(item.getSort() > 0) {
            this.items.add(item);
        } else {
            item.setSort(this.getNextItemSort());
            this.items.add(item);
        }

    }

    private int getNextItemSort() {
        int maxIndex = 0;
        Iterator var3 = this.getItems().iterator();

        while(var3.hasNext()) {
            CItem c = (CItem)var3.next();
            if(c.getSort() > maxIndex) {
                maxIndex = c.getSort();
            }
        }

        return maxIndex + 1;
    }

    public String getType() {
        if(this.type == null) {
            this.type = "";
        }

        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        if(this.note == null) {
            this.note = "";
        }

        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CType getcType() {
        return this.cType;
    }

    public void setcType(CType cType) {
        this.cType = cType;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<Map<String, Object>> toListMap() {
        ArrayList li = new ArrayList();
        Iterator var3 = this.getItems().iterator();

        while(var3.hasNext()) {
            CItem c = (CItem)var3.next();
            HashMap m = new HashMap();
            m.put("id", c.getId());
            m.put("parent_id", c.getPid());
            m.put("code", c.getCode());
            m.put("note", c.getNote());
            li.add(m);
        }

        return li;
    }

    public JSONArray toJsonArray(boolean tree) {
        JSONArray li = new JSONArray();
        Iterator var4 = this.getItems().iterator();

        while(var4.hasNext()) {
            CItem c = (CItem)var4.next();
            JSONObject m = new JSONObject();
            if(tree) {
                m.put("id", c.getId());
                m.put("parent_id", c.getPid());
            }

            m.put("code", c.getCode());
            m.put("note", c.getNote());
            li.put(m);
        }

        return li;
    }

    public JSONArray toJsonArray() {
        JSONArray li = new JSONArray();
        Iterator var3 = this.getItems().iterator();

        while(var3.hasNext()) {
            CItem c = (CItem)var3.next();
            JSONObject m = new JSONObject();
            m.put("id", c.getId());
            m.put("parent_id", c.getPid());
            m.put("code", c.getCode());
            m.put("note", c.getNote());
            li.put(m);
        }

        return li;
    }

    public List<Map<String, Object>> toListMapWithBlank() {
        HashMap map = new HashMap();
        map.put("id", DBUtils.uuid());
        map.put("parent_id", "-1");
        map.put("code", "");
        map.put("note", "请选择");
        List li = this.toListMap();
        li.add(0, map);
        return li;
    }

    public Map<String, String> toMap() {
        HashMap m = new HashMap();
        Iterator var3 = this.getItems().iterator();

        while(var3.hasNext()) {
            CItem c = (CItem)var3.next();
            m.put(c.getCode(), c.getNote());
        }

        return m;
    }

    public String toString(boolean tree) {
        return this.toJsonArray(tree).toString();
    }
}
