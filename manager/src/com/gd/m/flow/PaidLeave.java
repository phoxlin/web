package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class PaidLeave {
	public void xx(JSONObject content, Connection conn, String cust_name, String gym, String memId, long realAmt) throws Exception {
		// JSONObject content = this.getContent();

		EntityImpl entity = new EntityImpl("yp_leave", conn);
		String leave_time_from = content.getString("leave_time_from");
		String leave_time_to = content.getString("leave_time_to");
		String leave_reason = content.getString("leave_reason");

		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		entity.setValue("cust_name", cust_name);
		entity.setValue("mem_id", memId);
		entity.setValue("state", "001");
		entity.setValue("gym", gym);
		entity.setValue("content", leave_reason);
		entity.setValue("start_time", date.parse(leave_time_from));
		entity.setValue("end_time", date.parse(leave_time_to));
		entity.setValue("leave_type", "付费");
		entity.setValue("flow_num", "1");
		entity.setValue("flow_table_name", "付费");
		entity.setValue("leave_type", "pay");
		entity.setValue("create_time", new Date());
		entity.create();
		
		/**
		 * 判断请假开始时间是否为今天 如果是今天 请假信息立即生效 否则 就在Task里面处理请假
		 */
		leave_time_from = leave_time_from.split(" ")[0];
		String now = Utils.parseData(new Date(), "yyyy-MM-dd");

		if (leave_time_from.equals(now)) {
			entity = new EntityImpl("yp_mem", conn);
			entity.setTablename("yp_mem_" + cust_name);
			entity.setValue("id", memId);
			entity.setValue("state", "005");
			entity.update();
			// 查询该会员的所有会员卡
			entity = new EntityImpl(conn);
			String sql = "update yp_type_user_" + gym + " set state='004' where mem_id=? and state=?";
			entity.executeUpdate(sql, new String[] { memId ,"002"});
		}
		// 发送消息
		JSONObject body = new JSONObject();
		body.put("start_time", leave_time_from);
		body.put("end_time", leave_time_to);
		body.put("fee", Utils.toPrice(realAmt));
		//MsgToMemAction.SendMsg("mem_fee_holiday", memId, body, conn, cust_name, gym);
	}

}
