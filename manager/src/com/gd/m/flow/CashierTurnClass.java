package com.gd.m.flow;

import java.sql.Connection;
import java.util.Date;

import org.json.JSONObject;

import com.gd.m.GdUser;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class CashierTurnClass {
	public void xx(JSONObject json, Connection conn, String cust_name) throws Exception {
		String from_id = json.getString("from_id");
		String from_name = json.getString("from_name");
		String from_phone = json.getString("from_phone");
		String to_phone = json.getString("to_phone");
		String changeClassFee = json.getString("price");
		String to_id = json.getString("to_id");
		String class_id = json.getString("class_id");
		String class_type = json.getString("class_type");
		/**
		 * 私教课
		 */
		String lesson_id = "";
		if ("s".equals(class_type)) {
			Entity en = new EntityImpl(conn);
			String sql = "select id from yp_private_user  where id=?";
			int size = en.executeQuery(sql, new String[] { class_id });
			if (size > 0) {
				en.executeUpdate("update  yp_private_user set mem_id=? where  id=? ", new String[] { to_id, class_id });
			}
		} else if ("g".equals(class_type)) {// 付费团课
			Entity en = new EntityImpl(conn);
			String sql = "select id from yp_grp_user  where id=?";
			int size = en.executeQuery(sql, new String[] { class_id });
			if (size > 0) {
				en.executeUpdate("update  yp_grp_user  set mem_id=? where  id=? ", new String[] { to_id, class_id });
			}
		}

		if (lesson_id != null && lesson_id.length() > 0) {
			// 查询课程信息
			Entity lesson = new EntityImpl("yp_private_plan", conn);
			lesson.setValue("id", lesson_id);
			int size = lesson.search();
			if (size > 0) {
				GdUser from = new GdUser(cust_name, from_id, false, conn);
				GdUser to = new GdUser(cust_name, to_id, false, conn);
				String lesson_name = lesson.getStringValue("lesson_name");
				// 发消息
				JSONObject body = new JSONObject();
				body.put("from_name", from_name + "(" + from_phone + ")");
				body.put("to_name", to.getUserName() + "(" + to_phone + ")");
				body.put("to_id", to_id);
				body.put("fee", changeClassFee);
				body.put("lesson_name", lesson_name);
				body.put("time", Utils.parseData(new Date(), "yyyy-MM-dd HH:mm"));

				//MsgToMemAction.SendMsg("mem_trans_lesson", from_id, body, conn, cust_name, from.getGym());
			}
		}

	}

}
