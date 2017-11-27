package com.core.server.task;

import com.core.SFile;
import com.core.enuts.ColumnType;
import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.db.Column;
import com.core.server.db.Entity;
import com.core.server.db.impl.EntityImpl;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskAction extends BasicAction {
    @Route(
            value = "/taskview-doForward",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewDoForward() throws Exception {
        String instance_id = this.getParameter("instance_id");
        String instance_no = this.getParameter("instance_no");
        String step_id = this.getParameter("step_id");
        String taskcode = this.getParameter("taskcode");
        String remark = this.getParameter("forward_remark");
        String to = this.getParameter("traned2Name");
        String userinfo = this.getParameter("userinfo");
        EntityImpl sys_task_step2 = new EntityImpl("sys_task_step", this);
        sys_task_step2.setValue("id", step_id);
        sys_task_step2.setValue("state", "transfered");
        sys_task_step2.setValue("next_taskcode", to);
        sys_task_step2.setValue("remark", remark);
        sys_task_step2.update();
        TaskInfo toTask = new TaskInfo(to, this.getSessionUser());
        EntityImpl check = new EntityImpl(this);
        check.executeQuery("select count(id) num from sys_task_step a where a.instance_id=? and a.taskcode=?", new String[]{instance_id, to});
        if(check.getIntegerValue("num").intValue() <= 0) {
            EntityImpl sys_task_step = new EntityImpl("sys_task_step", this);
            sys_task_step.setValue("instance_no", instance_no);
            sys_task_step.setValue("instance_id", instance_id);
            sys_task_step.setValue("taskcode", to);
            sys_task_step.setValue("taskname", toTask.getName());
            sys_task_step.setValue("prev_taskcode", taskcode);
            sys_task_step.setValue("next_taskcode", "");
            sys_task_step.setValue("userid", userinfo);
            sys_task_step.setValue("op_time", new Date());
            sys_task_step.setValue("state", "waitview");
            String id = sys_task_step.create();
            EntityImpl en = new EntityImpl(this);
            en.executeUpdate("update sys_task_instance set CURRENT_STEP_CODE=?,CURRENT_STEP_ID=? where id=?", new Object[]{to, id, instance_id});
        }

    }

    @Route(
            value = "/taskview-save",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewSave() throws Exception {
        String task_instrance_id = this.getParameter("instance_id");
        String instance_no = this.getParameter("instance_no");
        String task_step_id = this.getParameter("task_step_id");
        String task_state = this.getParameter("task_state");
        String taskcode = this.getParameter("taskcode");
        this.obj.put("instance_no", instance_no);
        this.obj.put("instance_id", task_instrance_id);
        this.obj.put("state", task_state);
        this.obj.put("step_id", task_step_id);
        this.obj.put("taskcode", taskcode);
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        HashSet tables = new HashSet();
        Iterator var9 = task.getLegends().iterator();

        while(var9.hasNext()) {
            TaskNormalLegend en = (TaskNormalLegend)var9.next();
            if(!en.getType().equals("data-grid")) {
                Iterator str = en.getRows().iterator();

                while(str.hasNext()) {
                    TaskRow entity = (TaskRow)str.next();
                    Iterator var13 = entity.getCols().iterator();

                    while(var13.hasNext()) {
                        Column col = (Column)var13.next();
                        tables.add(col.getInput_tablename());
                    }
                }
            }
        }

        if(tables.size() > 0) {
            var9 = tables.iterator();

            while(var9.hasNext()) {
                String en1 = (String)var9.next();
                Entity entity1 = this.getEntityFromPage(en1);
                if(entity1 != null) {
                    String str1 = this.request.getParameter("_");
                    if("change".equalsIgnoreCase(str1)) {
                        TaskUtils.recordHistory(entity1, en1, entity1.getStringValue("id"), task_instrance_id, "edit", this.getSessionUser().getId(), this.getConnection());
                    }

                    entity1.update();
                }
            }
        }

        EntityImpl en2 = new EntityImpl(this);
        en2.executeUpdate("update sys_task_instance set CURRENT_STEP_CODE=?,CURRENT_STEP_ID=? where id=?", new Object[]{taskcode, task_step_id, task_instrance_id});
    }

    @Route(
            value = "/taskview-finish",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewFinish() throws Exception {
        String task_instrance_id = this.getParameter("instance_id");
        String instance_no = this.getParameter("instance_no");
        String task_step_id = this.getParameter("task_step_id");
        String task_state = this.getParameter("task_state");
        String taskcode = this.getParameter("taskcode");
        this.obj.put("instance_no", instance_no);
        this.obj.put("instance_id", task_instrance_id);
        this.obj.put("state", task_state);
        this.obj.put("step_id", task_step_id);
        this.obj.put("taskcode", taskcode);
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        HashSet tables = new HashSet();
        Iterator date = task.getLegends().iterator();

        while(date.hasNext()) {
            TaskNormalLegend sf = (TaskNormalLegend)date.next();
            if(!sf.getType().equals("data-grid")) {
                Iterator sql = sf.getRows().iterator();

                while(sql.hasNext()) {
                    TaskRow sys_task_step2 = (TaskRow)sql.next();
                    Iterator var13 = sys_task_step2.getCols().iterator();

                    while(var13.hasNext()) {
                        Column jdbc_type = (Column)var13.next();
                        tables.add(jdbc_type.getInput_tablename());
                    }
                }
            }
        }

        if(tables.size() > 0) {
            date = tables.iterator();

            while(date.hasNext()) {
                String sf1 = (String)date.next();
                Entity sys_task_step21 = this.getEntityFromPage(sf1);
                sys_task_step21.setValue("instance_id", task_instrance_id);
                sys_task_step21.save();
            }

            EntityImpl sf2 = new EntityImpl(this);

            try {
                sf2.executeUpdate("delete from sys_task_draft where step_id =?", new String[]{task_step_id});
            } catch (Exception var14) {
                ;
            }
        }

        SimpleDateFormat sf3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1 = new Date();
        EntityImpl sys_task_step22 = new EntityImpl("sys_task_step", this);
        String sql1 = "update sys_task_step set state=? where  instance_id=?";
        sys_task_step22.executeUpdate(sql1, new String[]{"closed", task_instrance_id});
        String jdbc_type1 = Resources.getProperty("JDBC_TYPE", "oracle");
        if(jdbc_type1 != null && jdbc_type1.length() > 0 && "oracle".equalsIgnoreCase(jdbc_type1)) {
            sql1 = "update sys_task_step set state=?,op_time=to_date(?,\'yyyy-mm-dd hh:mi:ss\') where  instance_id=? and PREV_TASKCODE=\'-1\'";
        } else {
            sql1 = "update sys_task_step set state=?,op_time=? where instance_id=? and PREV_TASKCODE=\'-1\'";
        }

        sys_task_step22.executeUpdate(sql1, new Object[]{"closed", sf3.format(date1), task_instrance_id});
        sys_task_step22.executeUpdate("update sys_task_instance set state=?,CURRENT_STEP_CODE=?,CURRENT_STEP_ID=? where id=?", new Object[]{"closed", taskcode, task_step_id, task_instrance_id});
    }

    @Route(
            value = "/taskview-forward",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewForward() throws Exception {
        String task_instrance_id = this.getParameter("instance_id");
        String instance_no = this.getParameter("instance_no");
        String task_step_id = this.getParameter("task_step_id");
        String task_state = this.getParameter("task_state");
        String taskcode = this.getParameter("taskcode");
        this.obj.put("instance_no", instance_no);
        this.obj.put("instance_id", task_instrance_id);
        this.obj.put("state", task_state);
        this.obj.put("step_id", task_step_id);
        this.obj.put("taskcode", taskcode);
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        HashSet tables = new HashSet();
        Iterator trans = task.getLegends().iterator();

        Iterator entity;
        while(trans.hasNext()) {
            TaskNormalLegend savedTables = (TaskNormalLegend)trans.next();
            if(!savedTables.getType().equals("data-grid")) {
                entity = savedTables.getRows().iterator();

                while(entity.hasNext()) {
                    TaskRow tran = (TaskRow)entity.next();
                    Iterator o = tran.getCols().iterator();

                    while(o.hasNext()) {
                        Column id = (Column)o.next();
                        tables.add(id.getInput_tablename());
                    }
                }
            }
        }

        JSONArray savedTables1 = new JSONArray();
        if(tables.size() > 0) {
            Iterator tran1 = tables.iterator();

            while(tran1.hasNext()) {
                String trans1 = (String)tran1.next();
                Entity entity1 = this.getEntityFromPage(trans1);
                if(entity1 != null) {
                    entity1.setValue("instance_id", task_instrance_id);
                    String id1 = entity1.save();
                    if(id1 != null) {
                        JSONObject o1 = new JSONObject();
                        o1.put("table_name", trans1);
                        o1.put("table_id", id1);
                        savedTables1.put(o1);
                    }
                }
            }

            EntityImpl trans2 = new EntityImpl(this);

            try {
                trans2.executeUpdate("delete from sys_task_draft where step_id =?", new String[]{task_step_id});
            } catch (Exception var14) {
                ;
            }
        }

        this.obj.put("savedTables", savedTables1);
        this.obj.put("userId", this.getSessionUser().getUser_id());
        JSONArray trans3 = new JSONArray();
        entity = task.getDecisions().iterator();

        while(entity.hasNext()) {
            Transition tran2 = (Transition)entity.next();
            trans3.put(tran2.toJson(this.getConnection()));
        }

        this.obj.put("trans", trans3);
    }

    @Route(
            value = "/taskview-saveDraft",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewSaveDraft() throws Exception {
        String task_instrance_id = this.getParameter("instance_id");
        String task_step_id = this.getParameter("task_step_id");
        String taskcode = this.getParameter("taskcode");
        HashSet fields = new HashSet();
        HashMap datainfo = new HashMap();
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        Iterator name = task.getLegends().iterator();

        while(name.hasNext()) {
            TaskNormalLegend sys_task_step2 = (TaskNormalLegend)name.next();
            if(!sys_task_step2.getType().equals("data-grid")) {
                Iterator value = sys_task_step2.getRows().iterator();

                while(value.hasNext()) {
                    TaskRow row = (TaskRow)value.next();
                    Iterator sys_task_draft = row.getCols().iterator();

                    while(sys_task_draft.hasNext()) {
                        Column type = (Column)sys_task_draft.next();
                        String name1 = type.getInput_tablename() + "__" + type.getName();
                        String value1 = this.request.getParameter(name1);
                        String type1 = type.getControlType();
                        if(value1 != null && value1.length() > 0) {
                            fields.add(name1);
                            datainfo.put(name1, value1);
                            datainfo.put(name1 + "__type", type1);
                        }
                    }
                }
            }
        }

        EntityImpl sys_task_step21;
        if(fields.size() > 0) {
            sys_task_step21 = new EntityImpl(this);

            try {
                sys_task_step21.executeUpdate("delete from sys_task_draft where step_id =?", new String[]{task_step_id});
            } catch (Exception var16) {
                ;
            }

            Iterator row1 = fields.iterator();

            while(row1.hasNext()) {
                String name2 = (String)row1.next();
                String value2 = (String)datainfo.get(name2);
                String type2 = (String)datainfo.get(name2 + "__type");
                EntityImpl sys_task_draft1 = new EntityImpl("sys_task_draft", this);
                sys_task_draft1.setValue("instance_id", task_instrance_id);
                sys_task_draft1.setValue("step_id", task_step_id);
                sys_task_draft1.setValue("taskcode", taskcode);
                sys_task_draft1.setValue("fieldname", name2);
                sys_task_draft1.setValue("fieldtype", type2);
                sys_task_draft1.setValue("fieldvalue", value2);
                sys_task_draft1.create();
            }
        }

        sys_task_step21 = new EntityImpl("sys_task_step", this);
        sys_task_step21.setValue("id", task_step_id);
        sys_task_step21.setValue("state", "drafted");
        sys_task_step21.update();
    }

    @Route(
            value = "/taskview-index",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskviewIndex() throws Exception {
        String taskcode = this.request.getParameter("taskcode");
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        String sid = this.request.getParameter("sId");
        task.setStepId(sid);
        JSONObject json = task.toJson(this.getSessionUser());
        if(sid != null && sid.length() == 24) {
            EntityImpl sys_task_step = new EntityImpl("sys_task_step", this);
            sys_task_step.setValue("id", sid);
            JSONObject data = new JSONObject();
            JSONObject types = new JSONObject();
            if(sys_task_step.search() <= 0) {
                throw new Exception("系统找不到当前步骤的sid请检查，");
            }

            String instance_no = sys_task_step.getStringValue("instance_no");
            String instance_id = sys_task_step.getStringValue("instance_id");
            String state = sys_task_step.getStringValue("state");
            String userid = sys_task_step.getStringValue("userid");
            if(!this.getSessionUser().getId().equals(userid)) {
                this.L.warn("你不是当前任务指定的操作人");
            }

            json.put("instance_no", instance_no);
            json.put("instance_id", instance_id);
            json.put("state", state);
            json.put("sessionUID", this.getSessionUser().getId());
            json.put("userid", userid);
            json.put("userType", this.getSessionUser().getJurisdiction());
            EntityImpl steps = new EntityImpl(this);
            steps.executeQuery("select a.id,a.taskcode,a.state,a.taskname,a.op_time,a.prev_taskcode,a.next_taskcode from sys_task_step a where a.instance_id=?", new String[]{instance_id});
            List li = TaskUtils.getTaskStepHistory(steps);
            String curState = "";
            Iterator size = li.iterator();

            Iterator entity;
            String sql;
            label119:
            while(size.hasNext()) {
                Map sys_task_draft = (Map)size.next();
                entity = sys_task_draft.keySet().iterator();

                while(entity.hasNext()) {
                    String map = (String)entity.next();
                    if("taskcode".equals(map)) {
                        sql = sys_task_draft.get(map).toString();
                        if(sql.equals(taskcode)) {
                            curState = sys_task_draft.get("state").toString();
                            break label119;
                        }
                    }
                }
            }

            json.put("steps", li);
            json.put("curState", curState);
            EntityImpl var36 = new EntityImpl(this);
            int var37 = var36.executeQuery("select fieldname,fieldtype,fieldvalue from sys_task_draft where step_id =?", new String[]{sid});
            String sze;
            if(var37 > 0) {
                for(int var38 = 0; var38 < var37; ++var38) {
                    String var40 = var36.getStringValue("fieldname", var38);
                    sql = var36.getStringValue("fieldtype", var38);
                    sze = var36.getStringValue("fieldvalue", var38);
                    if("old-upload".equals(sql)) {
                        SFile sf = new SFile(sze);
                        HashMap en = new HashMap();
                        en.put("id", sze);
                        en.put("name", sf.getFileName());
                        data.put(var40, en);
                    } else {
                        data.put(var40, sze);
                    }

                    types.put(var40, sql);
                }
            }

            entity = task.getNormalLegends().iterator();

            while(entity.hasNext()) {
                TaskNormalLegend var39 = (TaskNormalLegend)entity.next();
                Map var43 = var39.getFieldColumns();
                Iterator var45 = var39.getTableNames().iterator();

                while(var45.hasNext()) {
                    sze = (String)var45.next();
                    EntityImpl var46 = new EntityImpl(this);
                    int size2 = var46.executeQuery("select * from " + sze + " where instance_id=?", new String[]{instance_id}, 1, 1);
                    if(size2 > 0) {
                        List list = (List)var43.get(sze);
                        int i = 0;

                        for(int l = list.size(); i < l; ++i) {
                            Column c = (Column)list.get(i);
                            String val = "";
                            if(c.getType() != ColumnType.DATE && c.getType() != ColumnType.DATETIME) {
                                val = var46.getStringValue(c.getName());
                            } else {
                                val = var46.getFormatStringValue(c.getName(), c.getFormat());
                            }

                            if(val != null && val.length() > 0) {
                                data.put(sze + "__" + c.getName(), val);
                                types.put(sze + "__" + c.getName(), c.getControlType());
                            }

                            if(!"upload".equals(c.getControlType()) && "old-upload".equals(c.getControlType()) && val != null && val.length() > 0) {
                                String[] vals = val.split(",");
                                if(vals.length > 0) {
                                    HashMap rmap = new HashMap();
                                    String[] var34 = vals;
                                    int var33 = vals.length;

                                    for(int var32 = 0; var32 < var33; ++var32) {
                                        String v = var34[var32];
                                        if(v != null && v.length() > 0) {
                                            SFile sf1 = new SFile(v);
                                            rmap.put("id", v);
                                            rmap.put("name", sf1.getFileName());
                                        }
                                    }

                                    data.put(sze + "__" + c.getName(), rmap);
                                }
                            }
                        }
                    }
                }
            }

            json.put("types", types);
            json.put("data", data);
            Map var41 = (Map)li.get(0);
            if("CA".equals(var41.get("taskname"))) {
                EntityImpl var42 = new EntityImpl(this);
                sql = "select * from sys_task_instance where id=?";
                int var44 = var42.executeQuery(sql, new String[]{instance_id});
                if(var44 > 0) {
                    json.put("CaState", var42.getStringValue("state"));
                }
            }
        }

        json.put("userType", this.getSessionUser().getJurisdiction());
        this.obj.put("task", json);
    }

    @Route(
            value = "/task-start-up",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskstartup() throws Exception {
        String taskcode = this.request.getParameter("taskcode");
        TaskInfo task = new TaskInfo(taskcode, this.getSessionUser());
        long num = Utils.getSysParamLongValue(taskcode, 0L, 1L);
        String no = Utils.leftPadding(Long.valueOf(num), 5, "0");
        EntityImpl sys_task_instance = new EntityImpl("sys_task_instance", this);
        sys_task_instance.setValue("instance_no", no);
        sys_task_instance.setValue("taskcode", taskcode);
        sys_task_instance.setValue("taskname", task.getName());
        sys_task_instance.setValue("userid", this.getSessionUser().getUser_id());
        sys_task_instance.setValue("createtime", new Date());
        sys_task_instance.setValue("state", "waitview");
        String instance_id = sys_task_instance.create();
        EntityImpl sys_task_step = new EntityImpl("sys_task_step", this);
        sys_task_step.setValue("instance_no", no);
        sys_task_step.setValue("instance_id", instance_id);
        sys_task_step.setValue("taskcode", taskcode);
        sys_task_step.setValue("taskname", task.getName());
        sys_task_step.setValue("prev_taskcode", "-1");
        sys_task_step.setValue("next_taskcode", "");
        sys_task_step.setValue("userid", this.getSessionUser().getUser_id());
        sys_task_step.setValue("op_time", new Date());
        sys_task_step.setValue("state", "waitview");
        String sid = sys_task_step.create();
        sys_task_step.executeUpdate("update sys_task_instance set CURRENT_STEP_CODE=?,CURRENT_STEP_ID=? where id=?", new Object[]{taskcode, sid, instance_id});
    }

    @Route(
            value = "/task-cq-del",
            conn = true,
            m = {HttpMethod.POST}
    )
    public void taskCqDel() throws Exception {
        String entity = this.request.getParameter("entity");
        String id = this.getParameter("id");
        String instance_id = this.getParameter("instance_id");
        if(id != null && id.length() > 0) {
            EntityImpl en = new EntityImpl(this);
            String[] ids = id.split(",");
            if(ids.length > 1) {
                ArrayList ts = new ArrayList();

                for(int i = 0; i < ids.length; ++i) {
                    ts.add("?");
                    if("change".equalsIgnoreCase(this.request.getParameter("_")) && instance_id != null && instance_id.length() > 0 && "change".equalsIgnoreCase(this.request.getParameter("_"))) {
                        TaskUtils.recordHistory((Entity)null, entity, ids[i], instance_id, "del", this.getSessionUser().getId(), this.getConnection());
                    }
                }

                en.executeUpdate("delete from " + entity + " where id in(" + Utils.getListString(ts) + ")", ids);
            } else {
                if(instance_id != null && instance_id.length() > 0 && "change".equalsIgnoreCase(this.request.getParameter("_"))) {
                    TaskUtils.recordHistory((Entity)null, entity, id, instance_id, "del", this.getSessionUser().getId(), this.getConnection());
                }

                en.executeUpdate("delete from " + entity + " where id=?", new String[]{id});
            }

        } else {
            throw new Exception("至少选择一条数据进行操作");
        }
    }

    @Route(
            value = "/task-cq-detail",
            conn = true,
            m = {HttpMethod.GET},
            type = ContentType.Forward
    )
    public void taskCqDetail() throws Exception {
        String entity = this.request.getParameter("entity");
        String type = this.request.getParameter("type");
        String id = this.getParameter("id");
        if(id != null && id.length() > 0) {
            String[] ids = id.split(",");
            if(ids.length > 1) {
                throw new Exception("只能选择一条记录进行查看");
            } else {
                EntityImpl en = new EntityImpl(entity, this);
                en.setValue("id", id);
                int size = en.search();
                if(size > 0) {
                    this.request.setAttribute(entity, en);
                    this.request.setAttribute("type", type);
                } else {
                    throw new Exception("没有找到对应的数据信息");
                }
            }
        } else {
            throw new Exception("至少选择一条数据进行操作");
        }
    }

    @Route(
            value = "/task-cq-save",
            conn = true,
            m = {HttpMethod.POST},
            type = ContentType.JSON
    )
    public void taskCqSave() throws Exception {
        String m = this.request.getParameter("m");
        String e = this.request.getParameter("e");
        Entity entity = this.getEntityFromPage(e);
        Entity en = this.getEntityFromPage(e);
        String instance_id = entity.getStringValue("instance_id");
        if("add".equals(m)) {
            String id = entity.create();
            if("change".equalsIgnoreCase(this.request.getParameter("_"))) {
                TaskUtils.recordHistory(en, e, id, instance_id, "add", this.getSessionUser().getId(), this.getConnection());
            }
        } else {
            if("change".equalsIgnoreCase(this.request.getParameter("_"))) {
                TaskUtils.recordHistory(entity, e, entity.getStringValue("id"), instance_id, "edit", this.getSessionUser().getId(), this.getConnection());
            }

            entity.update();
        }

    }
}
