package com.gd.m.flow;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class BuyGclass {
	public void xx(JSONObject obj, Connection conn, String dataId, String memId, String empId, String cust_name,
			String gym, String flownum, long realAmt) throws Exception {
		Entity ent = new EntityImpl(conn);
		String sq = "select a.* from yp_private_plan a where  a.id=? ";
		int szie = ent.executeQueryWithOutMaxResult(sq, new String[] { dataId }, 1, 1);
		if (szie == 0) {
			throw new Exception("未查询到相关课程的信息");
		}
		String num = obj.get("num") + "";
		int lesson_num = 1;
		if (num != null && !"null".equals(num)) {
			lesson_num = Integer.parseInt(num);
		} else {
			lesson_num = ent.getIntegerValue("nums");
		}
		String lesson_name = ent.getStringValue("lesson_name");
		int lesson_dead = ent.getIntegerValue("lesson_dead");

		Object keep = obj.get("keeper_phone");
		EntityImpl en = new EntityImpl("yp_grp_user", conn);
		if (lesson_dead > 0) {
			int days = lesson_dead * lesson_num;
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, days);
			en.setValue("deadline", cal.getTime());// 课程有效期=课程购买节数*课程有效期
		}
		en.setValue("cust_name", cust_name);
		if (obj.has("coach_id") && obj.getString("coach_id") != null && obj.getString("coach_id").length() > 0) {
			en.setValue("emp_id", obj.getString("coach_id"));
		} else {
			en.setValue("emp_id", "admin");
		}
		en.setValue("mem_id", memId);
		en.setValue("lesson_id", dataId);
		en.setValue("lesson_num", lesson_num);
		en.setValue("gift_num", "0");
		en.setValue("nums", lesson_num);
		if (keep != null) {
			en.setValue("keeper_phone", keep.toString());
		}
		en.setValue("is_flow", "N");
		en.setValue("flow_table_name", "YP_FLOW_" + gym);
		en.setValue("state", "001");
		en.setValue("flow_num", flownum);
		en.setValue("buy_time", new Date());
		en.create();
		// 发消息
		JSONObject body = new JSONObject();
		body.put("lesson_num", lesson_num);
		body.put("lesson_name", lesson_name);
		body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
		body.put("pay_money", Utils.toPrice(realAmt));
		//MsgToMemAction.SendMsg("mem_buy_lesson", memId, body, conn, cust_name, gym);
	}

}
