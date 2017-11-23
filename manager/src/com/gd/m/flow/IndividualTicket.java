package com.gd.m.flow;

import java.sql.Connection;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;

public class IndividualTicket {
	public void xx(Connection conn, JSONObject json, String dataTableName, String flownum, String gym, String cust_name,
			String phone, long caAmt) throws Exception {
		Entity en = new EntityImpl(conn);
		if (json.keySet().contains("isCheckIn")) {
			String isCheckIn = json.get("isCheckIn").toString();
			if (isCheckIn != null && "yes".equals(isCheckIn)) {
				en.executeUpdate("update yp_flow_" + gym + " set state = ? where FLOW_NUM = ?",
						new String[] { "002", flownum });
				String box_num = "-1";
				if (json.has("box_num"))
					box_num = json.get("box_num").toString();
				String box_id = "";
				if (json.has("box_id"))
					box_id = json.get("box_id").toString();
				en = new EntityImpl("yp_checkin", conn);
				en.setTablename("yp_checkin_" + gym);
				en.setValue("gym", gym);
				en.setValue("cust_name", cust_name);
				en.setValue("emp_id", "-1");
				en.setValue("type_code", json.get("card_type"));
				en.setValue("mem_no", "-1");
				en.setValue("phone", phone);
				en.setValue("user_name", "散客");
				en.setValue("checkin_type", "005");
				en.setValue("checkin_time", new Date());
				en.setValue("box_type", "002");
				en.setValue("box_no", box_num);
				en.setValue("STATE", "002");
				en.setValue("flow_num", flownum);
				en.setValue("CHECKIN_PRICE", caAmt);
				en.setValue("CHECKOUT_PRICE", 0);
				en.setValue("CA_PRICE", caAmt);
				en.setValue("flow_table_name", "yp_flow_" + gym);
				en.create();
				String sql = "update yp_box set is_rent='Y',state='002',mem_id='-1',mem_name='散客' where id=?";
				en.executeUpdate(sql, new String[] { box_id });
			} else {
				en.executeUpdate("update yp_flow_" + gym + " set state = ? where FLOW_NUM = ?",
						new String[] { "001", flownum });
			}
		} else {
			en.executeUpdate("update yp_flow_" + gym + " set state = ? where FLOW_NUM = ?",
					new String[] { "001", flownum });
		}
	}

}
