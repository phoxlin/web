package com.core.server.task;

import com.core.User;
import com.core.enuts.ColumnType;
import com.core.enuts.DBType;
import com.core.server.db.DBUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class BasicQmInfo extends QmInfo implements Serializable {
    private static final long serialVersionUID = 1L;



    public Map<String, Object> parseSql2(String taskcode, List<Object> ps, JSONArray filter,
                                         Map<String, Object> p, Columns columns, User user,
                                         String order, String desc, Connection conn)
            throws Exception
    {
        String sql = this.getSql();
        String tableAliase = this.getTableAliase(p);
        HashMap m = new HashMap();
        ArrayList list = new ArrayList();
        list.addAll(ps);
        StringBuilder sb = new StringBuilder();
        boolean changed = false;
        String ignore = "N";

        for(int i = 0; i < filter.length(); ++i) {
            JSONObject ss = filter.getJSONObject(i);
            if(ss.has("ignore")) {
                ignore = ss.getString("ignore");
            } else {
                ignore = "N";
            }

            String code = ss.getString("columnname");
            String val = "";

            try {
                val = ss.getString("columnvalue");
            } catch (Exception var24) {
                ;
            }

            String compare = "like";

            try {
                compare = ss.getString("compare");
            } catch (Exception var23) {
                ;
            }

            ColumnType type = columns.getColumnType(code);
            if(val != null && val.length() > 0 && "N".equals(ignore)) {
                if(!changed) {
                    sb.append(sql);
                    if(!sql.toLowerCase().contains("where")) {
                        sb.append(" where ");
                    } else {
                        sb.append(" and ");
                    }
                }

                changed = true;
                if(type != ColumnType.DATE && type != ColumnType.DATETIME) {
                    if(type != ColumnType.FLOAT && type != ColumnType.INT && type != ColumnType.LONG) {
                        if("like".equalsIgnoreCase(compare)) {
                            sb.append(tableAliase + code);
                            sb.append(" " + compare + " ?");
                            list.add(val + "%");
                        } else {
                            sb.append(tableAliase + code);
                            sb.append(" " + compare + " ?");
                            list.add(val);
                        }
                    } else {
                        sb.append(tableAliase + code);
                        sb.append(" " + compare);
                        sb.append("?");
                        list.add(val);
                    }
                } else {
                    if(DBUtils.getRDBType() == DBType.Oracle) {
                        sb.append("to_date(to_char(" + tableAliase + code + ",\'yyyy-mm-dd\'),\'yyyy-mm-dd\')");
                        sb.append(" " + compare);
                        sb.append(" to_date(");
                        sb.append("?");
                        sb.append(",\'yyyy-mm-dd\')");
                    } else {
                        sb.append("str_to_date(DATE_FORMAT(" + tableAliase + code + ",\'%Y-%m-%d\'),\'%Y-%m-%d\') ");
                        sb.append(" " + compare + " ?");
                    }

                    list.add(val);
                }

                sb.append(" and ");
            }
        }

        if(changed) {
            sql = sb.substring(0, sb.length() - 5);
        }

        if(order != null && !order.equals("null") && order.length() > 0 && !desc.equals("none")) {
            sql = "select * from(" + sql + ") a order by " + order + " " + desc;
        }

        m.put("sql", sql.toString());
        m.put("params", list.toArray());
        return m;
    }
}
