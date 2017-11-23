package com.gd.m.flow;

import java.sql.Connection;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class AccountRecharge {
	public void xx(Connection conn, int price, String cust_name, String gym, String memId) throws Exception {
		// int price = (int) this.getCaAmt();

		Entity en = new EntityImpl("yp_mem", conn);
		String sql = "select amt from yp_mem_" + cust_name + " where id=?";
		int size = en.executeQueryWithOutMaxResult(sql, new String[] { memId }, 1, 1);
		if (size > 0) {
			int amt = en.getIntegerValue("amt");
			amt = amt + price;
			sql = "update yp_mem_" + cust_name + " set amt=? where id=?";
			en.executeUpdate(sql, new String[] { amt + "", memId });

			// 发消息
			JSONObject body = new JSONObject();
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
			body.put("fee", price/100);
			//MsgToMemAction.SendMsg("mem_recharge", memId, body, conn, cust_name, gym);
		}
	}

}
