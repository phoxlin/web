package com.core.server.c;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CItem implements Comparator<CItem> {
    private String id;
    private String pid;
    private String code;
    private String note;
    private int sort;
    private Map<String, String> m = new HashMap();

    public CItem(String id, String pid, String code, String note, int sort) {
        this.id = id;
        this.pid = pid;
        this.code = code;
        this.note = note;
        this.sort = sort;
    }

    public CItem(String id, String pid, String code, String note) {
        this.id = id;
        this.pid = pid;
        this.code = code;
        this.note = note;
        this.sort = -1;
    }

    public CItem() {
    }

    public String toString() {
        return "id:" + this.id + ",pid:" + this.pid + ",code:" + this.code + ",note:" + this.note + ",sort:" + this.sort;
    }

    public String getValue(String key) {
        key = key.toLowerCase();
        return this.m.containsKey(key)?(String)this.m.get(key):"";
    }

    public int hashCode() {
        int hc = 0;
        if(this.id != null) {
            hc += this.id.hashCode();
        }

        if(this.pid != null) {
            hc += this.pid.hashCode();
        }

        if(this.code != null) {
            hc += this.code.hashCode();
        }

        if(this.note != null) {
            hc += this.note.hashCode();
        }

        hc += this.sort;
        return hc;
    }

    public int compare(CItem o1, CItem o2) {
        return o1.sort == o2.sort?0:(o1.sort > o2.sort?1:-1);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        if(this.pid == null || this.pid.length() <= 0) {
            this.pid = "-1";
        }

        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCode() {
        if(this.code == null) {
            this.code = "";
        }

        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setM(Map<String, String> m) {
        this.m = m;
    }

    public Map<String, String> getM() {
        return this.m;
    }
}
