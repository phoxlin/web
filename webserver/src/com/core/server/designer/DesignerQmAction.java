package com.core.server.designer;

import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.db.Column;
import com.core.server.db.impl.EntityImpl;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.tools.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class DesignerQmAction extends BasicAction {
    @Route(
            value = "/designer-initGridData",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void initGridData() throws Exception {
        String tablename = this.request.getParameter("loadingTableName");
        JSONArray li = new JSONArray();
        EntityImpl en = new EntityImpl(tablename, this);
        List columns = en.getCols();
        Iterator var6 = columns.iterator();

        while(var6.hasNext()) {
            Column col = (Column)var6.next();
            li.put(col.toJsonConf(this.getSessionUser()));
        }

        this.obj.put("li", li);
    }

    @Route(
            value = "/designer-init-tasklist",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void initDBList() throws Exception {
        EntityImpl sys_task_designer = new EntityImpl(this);
        int size = sys_task_designer.executeQuery("select id,isFirstTask,task_code,task_name from sys_task_designer order by isFirstTask,task_code");
        this.obj.put("size", size);
        JSONArray li = new JSONArray();

        for(int i = 0; i < size; ++i) {
            String _id = sys_task_designer.getStringValue("id", i);
            String isFirstTask = sys_task_designer.getBooleanValue("isFirstTask", i)?"Y":"N";
            String task_code = sys_task_designer.getStringValue("task_code", i);
            String task_name = sys_task_designer.getStringValue("task_name", i);
            JSONObject o = new JSONObject();
            o.put("_id", _id);
            o.put("isFirstTask", isFirstTask);
            o.put("task_code", task_code);
            o.put("task_name", task_name);
            li.put(o);
        }

        this.obj.put("db", li);
    }

    @Route(
            value = "/designer-load-taskset",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void loadDbSet() throws Exception {
        String _id = this.request.getParameter("_id");
        EntityImpl sys_task_designer = new EntityImpl("sys_task_designer", this);
        sys_task_designer.setValue("id", _id);
        int size = sys_task_designer.search();
        if(size > 0) {
            String isFirstTask = sys_task_designer.getBooleanValue("isFirstTask")?"Y":"N";
            String isTask = sys_task_designer.getBooleanValue("isTask")?"Y":"N";
            String task_name = sys_task_designer.getStringValue("task_name");
            String task_code = sys_task_designer.getStringValue("task_code");
            String layoutdata = sys_task_designer.getStringValue("layoutdata");
            this.obj.put("_id", _id);
            this.obj.put("isFirstTask", isFirstTask);
            this.obj.put("isTask", isTask);
            this.obj.put("task_name", task_name);
            this.obj.put("task_code", task_code);
            this.obj.put("layoutdata", layoutdata);
        }

    }

    @Route(
            value = "/designer-save-taskset",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void saveDbSet() throws Exception {
        String isFirstTask = this.request.getParameter("isFirstTask");
        String isTask = this.request.getParameter("isTask");
        String task_name = this.request.getParameter("task_name");
        String task_code = this.request.getParameter("task_code");
        String layoutdata = this.request.getParameter("layoutdata");
        EntityImpl sys_task_designer = new EntityImpl(this);
        int size = sys_task_designer.executeQuery("select * from sys_task_designer where task_code=? order by isFirstTask,task_code", new String[]{task_code});
        if(size > 0) {
            String sys_task_designer1 = sys_task_designer.getStringValue("id");
            sys_task_designer.executeUpdate("update sys_task_designer set isFirstTask=?,isTask=?,task_name=?,layoutdata=? where id=?", new String[]{isFirstTask, isTask, task_name, layoutdata, sys_task_designer1});
        } else {
            EntityImpl sys_task_designer11 = new EntityImpl("sys_task_designer", this);
            sys_task_designer11.setValue("task_code", task_code);
            sys_task_designer11.setValue("task_name", task_name);
            sys_task_designer11.setValue("isTask", isTask);
            sys_task_designer11.setValue("isFirstTask", isFirstTask);
            sys_task_designer11.setValue("layoutdata", layoutdata);
            sys_task_designer11.setValue("createtime", new Date());
            sys_task_designer11.create();
        }

    }

    @Route(
            value = "/designer-create-index",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void createIndex() throws Exception {
        boolean isTask = Utils.isTrue(this.request.getParameter("isTask"));
        boolean isFirstTask = Utils.isTrue(this.request.getParameter("isFirstTask"));
        String task_name = this.request.getParameter("task_name");
        String task_code = this.request.getParameter("task_code");
        String mainContent = this.request.getParameter("mainContent");
        Map map = DesQmUtils.parseQmContent(mainContent);
        map.put("isTask", Boolean.valueOf(isTask));
        map.put("isFirstTask", Boolean.valueOf(isFirstTask));
        TaskDesignerUtils.saveTaskCfg(task_code, task_name, map, isFirstTask);
        TaskDesignerUtils.saveIndex(task_code, task_name);
    }

    @Route(
            value = "/designer-create-edit",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void createEdit() throws Exception {
        String task_code = this.request.getParameter("task_code");
        TaskDesignerUtils.saveEdit(task_code);
    }

    @Route(
            value = "/designer-preview-index",
            conn = false,
            m = {HttpMethod.POST},
            type = ContentType.JSON,
            realSlience = true
    )
    public void previewIndex() throws Exception {
        boolean isTask = Utils.isTrue(this.request.getParameter("isTask"));
        boolean isFirstTask = Utils.isTrue(this.request.getParameter("isFirstTask"));
        String mainContent = this.request.getParameter("mainContent");
        Map map = DesQmUtils.parseQmContent(mainContent);
        map.put("isTask", Boolean.valueOf(isTask));
        map.put("isFirstTask", Boolean.valueOf(isFirstTask));
        TaskDesignerUtils.saveTaskCfg("demo", "演示", map, true);
        TaskDesignerUtils.saveIndex("demo", "演示");
        TaskDesignerUtils.saveEdit("demo");
    }
}
