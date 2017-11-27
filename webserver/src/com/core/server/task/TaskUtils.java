package com.core.server.task;

import com.core.server.db.Entity;
import com.core.server.db.impl.EntityImpl;
import com.core.server.tools.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class TaskUtils {
    public static List<Map<String, Object>> getTaskStepHistory(Entity steps) throws Exception {
        ArrayList li = new ArrayList();
        if(steps != null && steps.getResultCount() > 0) {
            String code = "-1";

            for(Map m = null; (m = getNextStep(code, steps)) != null; code = Utils.getMapStringValue(m, "taskcode")) {
                li.add(m);
            }
        }

        return li;
    }

    private static Map<String, Object> getNextStep(String taskcode, Entity steps) throws Exception {
        int i = 0;

        for(int l = steps.getResultCount(); i < l; ++i) {
            String prev_taskcode = steps.getStringValue("prev_taskcode", i);
            if(taskcode.equals(prev_taskcode)) {
                return (Map)steps.getValues().get(i);
            }
        }

        return null;
    }

    public static void recordHistory(Entity entity, String tablename, String dataId, String instanceId, String opType, String userId, Connection conn) throws Exception {
        if(instanceId != null && instanceId.length() > 5 && dataId != null && dataId.length() > 5) {
            if("add".equals(opType)) {
                String en = "";
                String size = entity.toJsonString();
                EntityImpl needSave = new EntityImpl("sys_task_history", conn);
                needSave.setValue("instance_id", instanceId);
                needSave.setValue("legendname", tablename);
                needSave.setValue("oldvalue", en);
                needSave.setValue("newvalue", size);
                needSave.setValue("dataid", dataId);
                needSave.setValue("op_type", "add");
                needSave.setValue("userid", userId);
                needSave.setValue("optime", new Date());
                needSave.create();
            } else {
                String newVal;
                EntityImpl var27;
                if("del".equals(opType)) {
                    var27 = new EntityImpl(tablename, conn);
                    String[] var28 = dataId.split(",");

                    for(int var30 = 0; var30 < var28.length; ++var30) {
                        var27.setValue("id", dataId);
                        int oldVal = var27.search();
                        if(oldVal <= 0) {
                            throw new Exception("记录删除历史数据出错，待删除的数据已经不在了");
                        }

                        newVal = var27.toJsonString();
                        String oldobj = "";
                        EntityImpl oldArray = new EntityImpl("sys_task_history", conn);
                        oldArray.setValue("instance_id", var28[var30]);
                        oldArray.setValue("legendname", tablename);
                        oldArray.setValue("oldvalue", newVal);
                        oldArray.setValue("newvalue", oldobj);
                        oldArray.setValue("dataid", dataId);
                        oldArray.setValue("op_type", "del");
                        oldArray.setValue("userid", userId);
                        oldArray.setValue("optime", new Date());
                        oldArray.create();
                    }
                } else {
                    var27 = new EntityImpl(tablename, conn);
                    var27.setValue("id", dataId);
                    int var29 = var27.search();
                    if(var29 <= 0) {
                        throw new Exception("记录删除历史数据出错，待删除的数据已经不在了");
                    }

                    boolean var31 = false;
                    String var32 = var27.toJsonString();
                    newVal = entity.toJsonString();
                    JSONObject var33 = new JSONObject(var32);
                    JSONArray var34 = var33.getJSONArray("listData");
                    var33.remove("listData");
                    JSONObject newobj = new JSONObject(newVal);
                    JSONArray newArray = newobj.getJSONArray("listData");
                    newobj.remove("listData");
                    JSONObject needSaveOldObj = new JSONObject();
                    JSONObject needSaveNewObj = new JSONObject();

                    try {
                        Map sys_task_history = newArray.getJSONObject(0).toMap();
                        Map oldMap = var34.getJSONObject(0).toMap();
                        Set entrySet = oldMap.entrySet();
                        if("fd_property".equals(tablename)) {
                            needSaveNewObj.put("type", sys_task_history.get("type"));
                            needSaveOldObj.put("type", oldMap.get("type"));
                        }

                        Iterator var22 = entrySet.iterator();

                        while(var22.hasNext()) {
                            Map.Entry entry = (Map.Entry)var22.next();
                            String value = String.valueOf(entry.getValue());
                            String key = (String)entry.getKey();
                            String newVlaue = String.valueOf(sys_task_history.get(key));
                            if(!newVlaue.equals(value) && !"null".equals(newVlaue)) {
                                needSaveNewObj.put(key, newVlaue);
                                needSaveOldObj.put(key, value);
                                var31 = true;
                            }
                        }
                    } catch (Exception var26) {
                        ;
                    }

                    if(var31) {
                        var33.put("listData", (new JSONArray()).put(needSaveOldObj));
                        newobj.put("listData", (new JSONArray()).put(needSaveNewObj));
                        EntityImpl var35 = new EntityImpl("sys_task_history", conn);
                        var35.setValue("instance_id", instanceId);
                        var35.setValue("legendname", tablename);
                        var35.setValue("oldvalue", var33.toString());
                        var35.setValue("newvalue", newobj.toString());
                        var35.setValue("dataid", dataId);
                        var35.setValue("op_type", "edit");
                        var35.setValue("userid", userId);
                        var35.setValue("optime", new Date());
                        var35.create();
                    }
                }
            }
        }

    }
}
