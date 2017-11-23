package com.gd.m.flow;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;

public class SalesDeal {

	public void xx(JSONObject con, Connection conn, String cust_name, String gym, String deal_id, String memId,
			String empId, String empName, long caAmt) throws Exception {
		if (con.has("pre_fee")) {
			JSONObject pre_fees = con.getJSONObject("pre_fee");
			Entity pre_fee = new EntityImpl("yp_prefee", conn);
			pre_fee.setTablename("yp_prefee_" + cust_name);
			for (String pre_id : pre_fees.keySet()) {
				pre_fee.setValue("id", pre_id);
				pre_fee.setValue("state", "Y");
				pre_fee.update();
			}
		}

		/**
		 * 完成成交 1.成交状态 3.会员状态(激活类型) 3.会籍完成任务
		 */

		Entity deal = new EntityImpl("yp_buy_card_record", conn);
		deal.setTablename("yp_buy_card_record_" + gym);
		deal.setValue("id", deal_id);
		int size = deal.search();
		if (size > 0) {
			String active_type = deal.getStringValue("active_type");

			String emp_id = deal.getStringValue("emp_id");
			Entity emp = new EntityImpl("yp_emp", conn);
			emp.setValue("id", emp_id);
			emp.search();

			String mem_id = deal.getStringValue("mem_id");
			String type_code = deal.getStringValue("type_code");

			Entity type = new EntityImpl("yp_type", conn);
			type.setValue("cust_name", cust_name);
			type.setValue("type_code", type_code);
			type.search();

			deal.setValue("id", deal_id);
			deal.setValue("state", "Y"); // 成交付款完成
			deal.update();

			int days = type.getIntegerValue("days");// 天数卡天数
			int amt = type.getIntegerValue("amt");// 储值卡金额
			int times = type.getIntegerValue("times");
			int mins = type.getIntegerValue("mins");
			String card_type = type.getStringValue("card_type");
			if ("001".equals(card_type)) {
				// 天数卡
				// 激活
				Entity card = new EntityImpl("yp_type_user", conn);
				card.setTablename("yp_type_user_" + gym);
				card.setValue("cust_name", cust_name);
				card.setValue("gym", gym);
				card.setValue("type_code", type_code);
				card.setValue("mem_id", mem_id);
				card.setValue("buy_time", new Date());
				card.setValue("is_give", "N");
				card.setValue("is_print", "N");
				card.setValue("sales_name", emp.getStringValue("emp_name"));
				card.setValue("sales_id", emp_id);

				if ("001".equals(active_type)) {
					Entity u = new EntityImpl("yp_mem", conn);
					u.setTablename("yp_mem_" + cust_name);
					u.setValue("id", mem_id);
					u.setValue("state", "001");
					u.update();

					int card_days = type.getIntegerValue("days");
					Calendar c = Calendar.getInstance();
					c.add(Calendar.DATE, card_days);
					Date deadline = c.getTime();
					// 添加会员卡
					card.setValue("act_time", new Date());
					card.setValue("deadline", deadline);
					card.setValue("state", "002");
				} else {
					card.setValue("state", "001");
				}
				card.create();
			} else if ("002".equals(card_type)) {
				// 储值卡
				Entity en = new EntityImpl(conn);
				String sql = "select a.state from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='002' and b.cust_name=?  and a.mem_id=?";
				size = en.executeQuery(sql, new String[] { cust_name, memId });
				// 判断是否已经有购买了的储值卡
				if (size > 0) {
					// 已经有购买了的储值卡了
					// 查询该账户的余额
					Entity entity = new EntityImpl("yp_mem", conn);
					entity.setTablename("yp_mem_" + cust_name);
					entity.setValue("id", memId);
					size = entity.search();
					int userAmt = entity.getIntegerValue("amt");
					int newAmt = userAmt + amt * 100;
					sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
					entity.executeUpdate(sql, new Object[] { newAmt, memId });
				} else {
					// 激活
					Entity card = new EntityImpl("yp_type_user", conn);
					card.setTablename("yp_type_user_" + gym);
					card.setValue("cust_name", cust_name);
					card.setValue("gym", gym);
					card.setValue("type_code", type_code);
					card.setValue("mem_id", mem_id);
					card.setValue("buy_time", new Date());
					card.setValue("is_give", "N");
					card.setValue("is_print", "N");
					card.setValue("sales_name", emp.getStringValue("emp_name"));
					card.setValue("sales_id", emp_id);
					if ("001".equals(active_type)) {
						Entity u = new EntityImpl("yp_mem", conn);
						u.setTablename("yp_mem_" + cust_name);
						u.setValue("id", mem_id);
						u.setValue("state", "001");
						u.update();

						int card_days = type.getIntegerValue("days");
						Calendar c = Calendar.getInstance();
						c.add(Calendar.DATE, card_days);
						Date deadline = c.getTime();
						// 添加会员卡
						card.setValue("act_time", new Date());
						card.setValue("deadline", deadline);
						card.setValue("state", "002");
						Entity entity = new EntityImpl("yp_mem", conn);
						entity.setTablename("yp_mem_" + cust_name);
						entity.setValue("id", memId);
						size = entity.search();
						int userAmt = entity.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;
						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						u.executeUpdate(sql, new Object[] { newAmt, memId });
					} else {
						card.setValue("state", "001");
					}
					card.create();
				}
			} else if ("004".equals(card_type)) {// 时间卡
				Entity en = new EntityImpl(conn);
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				String buy_id = createBuyCardsRecord(en, gym, cust_name, type_code, empId, memId, active_type,
						deal.getStringValue("active_time"), caAmt + "", conn);
				buy(en, gym, cust_name, type_code, memId, newDate, times, mins, active_type, empId, empName, buy_id,
						conn);
			} else {
				// 激活
				int card_days = type.getIntegerValue("days");
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, card_days);
				Date deadline = c.getTime();
				Entity card = new EntityImpl("yp_type_user", conn);
				card.setTablename("yp_type_user_" + gym);
				card.setValue("cust_name", cust_name);
				card.setValue("gym", gym);
				card.setValue("type_code", type_code);
				card.setValue("mem_id", mem_id);
				card.setValue("times", times);
				card.setValue("buy_time", new Date());
				card.setValue("is_give", "N");
				card.setValue("is_print", "N");
				card.setValue("sales_name", emp.getStringValue("emp_name"));
				card.setValue("sales_id", emp_id);
				if ("001".equals(active_type)) {
					Entity u = new EntityImpl("yp_mem", conn);
					u.setTablename("yp_mem_" + cust_name);
					u.setValue("id", mem_id);
					u.setValue("state", "001");
					u.update();

					// 添加会员卡
					card.setValue("act_time", new Date());
					card.setValue("deadline", deadline);
					card.setValue("state", "002");
				} else {
					card.setValue("state", "001");
				}
				card.create();
			}

			// 完成任务
			Entity todo = new EntityImpl("yp_todo", conn);
			todo.setTablename("yp_todo_" + gym);
			todo.setValue("emp_id", emp_id);
			todo.setValue("mem_id", mem_id);
			todo.setValue("state", "001");
			size = todo.search();
			if (size > 0) {
				String id = todo.getStringValue("id");
				todo.setValue("id", id);
				todo.setValue("state", "002");
				todo.update();
			}
		} else {
			throw new Exception("未查询到该次购卡成交!");
		}
	}

	private void buy(Entity en, String gym, String cust_name, String type_code, String user_id, Date deadline,
			int times, int mins, String active_type, String sales_id, String sales_name, String buy_id, Connection conn)
			throws Exception {
		en = new EntityImpl("yp_type_user", conn);
		en.setTablename("yp_type_user_" + gym);
		en.setValue("cust_name", cust_name);
		en.setValue("gym", gym);
		en.setValue("type_code", type_code);
		en.setValue("mem_id", user_id);
		en.setValue("buy_time", new Date());
		en.setValue("is_give", "N");
		en.setValue("is_print", "N");
		en.setValue("mins", mins);
		en.setValue("sales_id", sales_id);
		en.setValue("sales_name", sales_name);
		en.setValue("times", times);
		en.setValue("buy_id", buy_id);
		if ("001".equals(active_type)) {// 001 立即激活
			en.setValue("act_time", new Date());
			en.setValue("deadline", deadline);
			en.setValue("state", "002");
		} else {
			en.setValue("state", "001");
		}
		en.create();

	}

	private String createBuyCardsRecord(Entity en, String gym, String cust_name, String type_code, String emp_id,
			String mem_id, String active_type, String active_time, String real_price, Connection conn)
			throws Exception {
		en = new EntityImpl("yp_buy_card_record", conn);
		en.setTablename("yp_buy_card_record_" + gym);
		en.setValue("cust_name", cust_name);
		en.setValue("type_code", type_code);
		en.setValue("emp_id", emp_id);
		en.setValue("mem_id", mem_id);
		en.setValue("is_flow", "N");
		en.setValue("state", "Y");
		en.setValue("deal_time", new Date());
		en.setValue("active_type", active_type);
		en.setValue("active_time", active_time);
		en.setValue("price", real_price);
		en.setValue("real_price", real_price);
		String id = en.create();
		return id;
	}
}
