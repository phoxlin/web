package com.core.server.task;

import com.core.Assignee;
import com.core.User;
import com.core.enuts.ColumnType;
import com.core.server.db.Column;
import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.ms.MsInfo;
import com.core.server.tools.Utils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskInfo {
    private File baseFile;
    private String id;
    private String pid;
    private String stepId;
    private String instraceId;
    private String code;
    private String name;
    private int loadOnStartup;
    private boolean isTask;
    private boolean isFirstTask;
    private User user;
    private int left;
    private int right;
    private List<TaskNormalLegend> legends;
    private List<Transition> decisions;
    private JSONObject json;

    public TaskInfo(String taskcode, String stepId, User user) throws Exception {
        this(taskcode, user);
        this.stepId = stepId;
        Connection conn = null;
        DBM db = new DBM();

        try {
            conn = db.getConnection();
            conn.setAutoCommit(false);
            this.toJson(user);
            if(stepId != null && stepId.length() == 24) {
                EntityImpl e = new EntityImpl("sys_task_step", conn);
                e.setValue("id", stepId);
                JSONObject data = new JSONObject();
                JSONObject types = new JSONObject();
                if(e.search() <= 0) {
                    throw new Exception("系统找不到当前步骤的sid请检查，");
                }

                String instance_no = e.getStringValue("instance_no");
                String instance_id = e.getStringValue("instance_id");
                this.setInstraceId(instance_id);
                String state = e.getStringValue("state");
                String userid = e.getStringValue("userid");
                this.json.put("instance_no", instance_no);
                this.json.put("instance_id", instance_id);
                this.json.put("state", state);
                this.json.put("userid", userid);
                EntityImpl steps = new EntityImpl(conn);
                steps.executeQuery("select a.id,a.taskcode,a.taskname,a.op_time,a.prev_taskcode,a.next_taskcode from sys_task_step a where a.instance_id=?", new String[]{instance_id});
                List li = TaskUtils.getTaskStepHistory(steps);
                this.json.put("steps", li);
                EntityImpl sys_task_draft = new EntityImpl(conn);
                int size = sys_task_draft.executeQuery("select fieldname,fieldtype,fieldvalue from sys_task_draft where step_id =?", new String[]{stepId});
                String tablename;
                if(size > 0) {
                    for(int le = 0; le < size; ++le) {
                        String fieldname = sys_task_draft.getStringValue("fieldname", le);
                        String map = sys_task_draft.getStringValue("fieldtype", le);
                        tablename = sys_task_draft.getStringValue("fieldvalue", le);
                        data.put(fieldname, tablename);
                        types.put(fieldname, map);
                    }
                }

                Iterator var34 = this.getNormalLegends().iterator();

                while(var34.hasNext()) {
                    TaskNormalLegend var33 = (TaskNormalLegend)var34.next();
                    Map var35 = var33.getFieldColumns();
                    Iterator var21 = var33.getTableNames().iterator();

                    while(var21.hasNext()) {
                        tablename = (String)var21.next();
                        EntityImpl en = new EntityImpl(conn);
                        en.executeQuery("select * from " + tablename + " where instance_id=?", new String[]{instance_id}, 1, 1);
                        List list = (List)var35.get(tablename);
                        int i = 0;

                        for(int l = list.size(); i < l; ++i) {
                            Column c = (Column)list.get(i);
                            String val = "";
                            if(c.getType() != ColumnType.DATE && c.getType() != ColumnType.DATETIME) {
                                val = en.getStringValue(c.getName(), i);
                            } else {
                                val = en.getFormatStringValue(c.getName(), c.getFormat(), i);
                            }

                            if(val != null && val.length() > 0) {
                                data.put(tablename + "__" + c.getName(), val);
                                types.put(tablename + "__" + c.getName(), c.getControlType());
                            }
                        }
                    }
                }

                this.json.put("types", types);
                this.json.put("data", data);
            }

            conn.commit();
        } catch (Exception var31) {
            throw var31;
        } finally {
            db.freeConnection(conn);
        }

    }

    public String getLegendFieldValue(String tablename, String fieldname, User user) throws Exception {
        if(this.json == null) {
            this.toJson(user);
        }

        if(this.json.keySet().contains("data")) {
            JSONObject data = this.json.getJSONObject("data");
            if(data.keySet().contains(tablename + "__" + fieldname)) {
                String val = data.getString(tablename + "__" + fieldname);
                return val;
            }
        }

        return "";
    }

    public TaskInfo(String taskcode, User user) throws Exception {
        this.baseFile = new File(Utils.getWebRootPath() + "WEB-INF/configures/task");
        this.left = 0;
        this.right = 12;
        this.legends = new ArrayList();
        this.decisions = new ArrayList();
        this.user = user;
        File file = MsInfo.getFile(this.baseFile, taskcode, "xml");
        if(file != null && file.exists()) {
            SAXReader read = new SAXReader();
            Document doc = read.read(file);
            Element task = (Element)doc.selectSingleNode("/root/task");
            String code = task.attributeValue("code");
            String taskname = task.attributeValue("name");
            String loadOnStartup = task.attributeValue("load-on-startup");
            String isTask = task.attributeValue("isTask");
            String isFirstTask = task.attributeValue("isFirstTask");

            try {
                this.loadOnStartup = Integer.parseInt(loadOnStartup);
            } catch (Exception var34) {
                ;
            }

            int transitions;
            try {
                int legends = Integer.parseInt(task.attributeValue("left_col_number"));
                transitions = Integer.parseInt(task.attributeValue("right_col_number"));
                if(legends >= 0 && transitions >= 0 && legends + transitions == 12) {
                    this.left = legends;
                    this.right = transitions;
                }
            } catch (Exception var33) {
                ;
            }

            this.code = code;
            this.name = taskname;
            this.isTask = Utils.isTrue(isTask);
            this.isFirstTask = Utils.isTrue(isFirstTask);
            List var35 = doc.selectNodes("/root/task/content/legend");
            transitions = 0;

            String name;
            for(int transition = var35.size(); transitions < transition; ++transitions) {
                Element legend = (Element)var35.get(transitions);
                name = legend.attributeValue("type");
                if("data-grid".equals(name)) {
                    TaskGridLegend to = new TaskGridLegend(legend);
                    this.addLegend(to);
                } else if("legend".equals(name)) {
                    TaskNormalLegend var39 = new TaskNormalLegend(legend);
                    this.addLegend(var39);
                }
            }

            List var36 = doc.selectNodes("/root/task/decision/transition");
            if(var36 != null && var36.size() > 0) {
                Iterator var38 = var36.iterator();

                while(var38.hasNext()) {
                    Element var37 = (Element)var38.next();
                    name = var37.attributeValue("name");
                    String var40 = var37.attributeValue("to");
                    String assigneeUsers = var37.attributeValue("assignee-users");
                    String assigneeGroups = var37.attributeValue("assignee-groups");
                    String condition = var37.attributeValue("condition");
                    Transition tran = new Transition();
                    tran.setName(name);
                    tran.setTo(var40);
                    tran.setCondition(condition);
                    Assignee assignee = new Assignee();
                    String[] ids;
                    String role;
                    int var25;
                    int var26;
                    String[] var27;
                    String[] _ids;
                    String _id;
                    int var30;
                    int var31;
                    String[] var32;
                    if(assigneeGroups != null && assigneeGroups.length() > 0) {
                        ids = assigneeGroups.split(";");
                        var27 = ids;
                        var26 = ids.length;

                        for(var25 = 0; var25 < var26; ++var25) {
                            role = var27[var25];
                            _ids = role.split(",");
                            var32 = _ids;
                            var31 = _ids.length;

                            for(var30 = 0; var30 < var31; ++var30) {
                                _id = var32[var30];
                                if(_id != null && _id.length() > 0) {
                                    assignee.addRole(_id);
                                }
                            }
                        }
                    }

                    if(assigneeUsers != null && assigneeUsers.length() > 0) {
                        ids = assigneeUsers.split(";");
                        var27 = ids;
                        var26 = ids.length;

                        for(var25 = 0; var25 < var26; ++var25) {
                            role = var27[var25];
                            _ids = role.split(",");
                            var32 = _ids;
                            var31 = _ids.length;

                            for(var30 = 0; var30 < var31; ++var30) {
                                _id = var32[var30];
                                if(_id != null && _id.length() > 0) {
                                    assignee.addId(_id);
                                }
                            }
                        }
                    }

                    tran.setAssignee(assignee);
                    this.decisions.add(tran);
                }
            }

        } else {
            throw new Exception("系统没有找到对应的Task[" + taskcode + "]配置文件");
        }
    }

    public TaskInfo getHistoryTask(String taskcode) throws Exception {
        if(this.stepId != null && this.stepId.length() > 0) {
            Connection conn = null;
            DBM db = new DBM();

            TaskInfo var9;
            try {
                conn = db.getConnection();
                conn.setAutoCommit(false);
                EntityImpl e = new EntityImpl("sys_task_step", conn);
                e.setValue("id", this.stepId);
                if(e.search() <= 0) {
                    throw new Exception("系统找不到对应的步骤id【" + this.stepId + "】请检查");
                }

                String instance_id = e.getStringValue("instance_id");
                int size = e.executeQuery("select a.id from sys_task_step a where a.instance_id=? and a.taskcode=? order by a.OP_TIME desc", new String[]{instance_id, taskcode});
                conn.commit();
                if(size <= 0) {
                    throw new Exception("流程历史里面找不到对应的task【" + taskcode + "】");
                }

                String sid = e.getStringValue("id");
                var9 = new TaskInfo(taskcode, sid, (User)null);
            } catch (Exception var12) {
                throw var12;
            } finally {
                db.freeConnection(conn);
            }

            return var9;
        } else {
            throw new Exception("没有设置步骤id");
        }
    }

    public List<TaskNormalLegend> getNormalLegends() {
        ArrayList li = new ArrayList();
        Iterator var3 = this.legends.iterator();

        while(var3.hasNext()) {
            TaskNormalLegend le = (TaskNormalLegend)var3.next();
            if(!le.getType().equals("data-grid")) {
                li.add(le);
            }
        }

        return li;
    }

    public TaskNormalLegend getNormalLegend(String legendcode) throws Exception {
        if(legendcode != null && legendcode.length() <= 0) {
            throw new Exception("NormalLegend Code不能为空");
        } else {
            Iterator var3 = this.legends.iterator();

            TaskNormalLegend le;
            do {
                if(!var3.hasNext()) {
                    throw new Exception("NormalLegend Code【" + legendcode + "】在task【" + this.code + "】中找不到");
                }

                le = (TaskNormalLegend)var3.next();
            } while(!legendcode.equals(le.getCode()) || le.getType().equals("data-grid"));

            return le;
        }
    }

    public TaskGridLegend getGridLegend(String legendcode) throws Exception {
        if(legendcode != null && legendcode.length() <= 0) {
            throw new Exception("GridLegend Code不能为空");
        } else {
            Iterator var3 = this.legends.iterator();

            TaskNormalLegend le;
            do {
                if(!var3.hasNext()) {
                    throw new Exception("GridLegend Code【" + legendcode + "】在task【" + this.code + "】中找不到");
                }

                le = (TaskNormalLegend)var3.next();
            } while(!legendcode.equals(le.getCode()) || !le.getType().equals("data-grid"));

            return (TaskGridLegend)le;
        }
    }

    public void addLegend(TaskNormalLegend legend) {
        this.legends.add(legend);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getInstraceId() {
        return this.instraceId;
    }

    public void setInstraceId(String instraceId) {
        this.instraceId = instraceId;
    }

    public List<TaskNormalLegend> getLegends() {
        return this.legends;
    }

    public List<Transition> getDecisions() {
        return this.decisions;
    }

    public void addTransition(Transition transition) {
        this.decisions.add(transition);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoadOnStartup() {
        return this.loadOnStartup;
    }

    public void setLoadOnStartup(int loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    public boolean isTask() {
        return this.isTask;
    }

    public void setTask(boolean isTask) {
        this.isTask = isTask;
    }

    public boolean isFirstTask() {
        return this.isFirstTask;
    }

    public void setFirstTask(boolean isFirstTask) {
        this.isFirstTask = isFirstTask;
    }

    public String getStepId() {
        return this.stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public JSONObject toJson(User user) {
        this.json = new JSONObject();
        this.json.put("id", this.getId());
        this.json.put("pid", this.getPid());
        this.json.put("code", this.code);
        this.json.put("name", this.name);
        this.json.put("loadOnStartup", this.loadOnStartup);
        this.json.put("isTask", this.isTask?"Y":"N");
        this.json.put("isFirstTask", this.isFirstTask?"Y":"N");
        this.json.put("instraceId", this.getInstraceId());
        this.json.put("step_id", this.getStepId());
        JSONArray legends = new JSONArray();
        Iterator le = this.legends.iterator();

        while(le.hasNext()) {
            TaskNormalLegend decisions = (TaskNormalLegend)le.next();
            legends.put(decisions.toJson(user));
        }

        this.json.put("legends", legends);
        JSONArray decisions1 = new JSONArray();
        Iterator var5 = this.decisions.iterator();

        while(var5.hasNext()) {
            Transition le1 = (Transition)var5.next();
            decisions1.put(le1.toJson());
        }

        this.json.put("decisions", decisions1);
        this.json.put("next_step_count", this.decisions.size());
        return this.json;
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }

    public User getUser() {
        return this.user;
    }
}
