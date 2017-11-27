package com.core.server.task;

import com.core.User;
import com.core.server.db.Column;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskRow {
    private List<Column> cols = new ArrayList();

    public TaskRow() {
    }

    public void addCol(Column column) {
        this.cols.add(column);
    }

    public List<Column> getCols() {
        return this.cols;
    }

    public JSONObject toJson(User user) {
        JSONObject o = new JSONObject();
        JSONArray cols = new JSONArray();

        for(int i = 0; i < this.cols.size(); ++i) {
            Column c = (Column)this.cols.get(i);
            cols.put(c.toJsonConf(user));
        }

        o.put("cols", cols);
        return o;
    }
}
