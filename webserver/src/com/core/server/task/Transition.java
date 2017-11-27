package com.core.server.task;

import com.core.Assignee;
import org.json.JSONObject;

import java.sql.Connection;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class Transition {
    private Assignee assignee;
    private String name;
    private String to;
    private String condition;

    public Transition() {
    }

    public JSONObject toJson(Connection conn) throws Exception {
        JSONObject o = this.toJson();
        o.put("assigneeVals", this.assignee.toJson(conn));
        return o;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("name", this.getName());
        o.put("to", this.getTo());
        o.put("condition", this.getCondition());
        o.put("assignee", this.assignee.toJson());
        return o;
    }

    public Assignee getAssignee() {
        return this.assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return this.to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
