package com.core;

import com.core.server.db.impl.EntityImpl;
import com.core.server.tools.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Assignee {
    private List<String> ids = new ArrayList();
    private List<String> roles = new ArrayList();


    public List<String> getIds() {
        return this.ids;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public void addId(String id) {
        this.ids.add(id);
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    public JSONArray toJson(Connection conn) throws Exception {
        JSONArray li = new JSONArray();
        EntityImpl en = new EntityImpl(conn);
        int size;
        int i;
        String id;
        String user_name;
        JSONObject o;
        if(this.ids.size() > 0) {
            size = en.executeQuery("select a.id,a.user_name from sys_user a where a.id in (" + Utils.getListString("?", this.ids.size()) + ")", this.ids.toArray());

            for(i = 0; i < size; ++i) {
                id = en.getStringValue("id", i);
                user_name = en.getStringValue("user_name", i);
                o = new JSONObject();
                o.put("code", id);
                o.put("note", user_name);
                li.put(o);
            }
        }

        if(this.roles.size() > 0) {
            size = en.executeQuery("select a.id,a.user_name from sys_user a ,sys_user_role b where b.fk_user_id=a.id and b.fk_role_code in(" + Utils.getListString("?", this.roles.size()) + ")", this.roles.toArray());

            for(i = 0; i < size; ++i) {
                id = en.getStringValue("id", i);
                user_name = en.getStringValue("user_name", i);
                o = new JSONObject();
                o.put("code", id);
                o.put("note", user_name);
                li.put(o);
            }
        }

        if(li.length() <= 0) {
            size = en.executeQuery("select a.id,a.user_name from sys_user a");

            for(i = 0; i < size; ++i) {
                id = en.getStringValue("id", i);
                user_name = en.getStringValue("user_name", i);
                o = new JSONObject();
                o.put("code", id);
                o.put("note", user_name);
                li.put(o);
            }
        }

        return li;
    }

    public JSONObject toJson(){
        JSONObject o = new JSONObject();
        JSONArray ids = new JSONArray();
        JSONArray roles = new JSONArray();
        Iterator it = this.ids.iterator();

        String role;
        while(it.hasNext()) {
            role = (String)it.next();
            ids.put(role);
        }

        it = this.roles.iterator();

        while(it.hasNext()) {
            role = (String)it.next();
            roles.put(role);
        }

        o.put("ids", ids);
        o.put("roles", roles);
        return o;
    }
}
