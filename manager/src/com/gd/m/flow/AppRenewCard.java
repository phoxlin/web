package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

/**
 * APP续卡
 * 
 * @author 1
 *
 */
public class AppRenewCard {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");

	public String xx(String card_id, Connection conn, String cust_name, String gym, String memId, String caAmt,
			String realAmt, String flownum) throws Exception {
		String recordId = "";
		Entity en = new EntityImpl("yp_type", conn);
		en.setValue("id", card_id);
		int size = en.search();
		Date d = new Date();
		String type_name = "";
		if (size > 0) {
			int days = en.getIntegerValue("days");// 天数卡天数
			int amt = en.getIntegerValue("amt");// 储值卡金额
			int times = en.getIntegerValue("times");
			int mins = en.getIntegerValue("mins");
			int type_fee = en.getIntegerValue("type_fee");
			String card_type = en.getStringValue("card_type");
			String type_code = en.getStringValue("type_code");

			if ("001".equals(card_type)) {
				// 天数卡
				en = new EntityImpl(conn);
				String sql = "select a.state,a.id,a.deadline,a.times from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='001' and b.cust_name=?  and a.mem_id=?";
				size = en.executeQuery(sql, new String[] { cust_name, memId });
				if (size > 0) {
					String id = en.getStringValue("id");
					Date deadline = en.getDateValue("deadline");
					int oldTimes = en.getIntegerValue("times");
					Calendar cld = Calendar.getInstance();
					if (deadline.after(new Date())) {
						cld.setTime(deadline);
					}
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					en = new EntityImpl(conn);
					sql = "update yp_type_user_" + gym + " set times=? ,deadline=?, type_code=? where id=?";
					en.executeUpdate(sql, new String[] { oldTimes + times + "", sdf.format(newDate), type_code, id });
					recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", memId, "001", "", caAmt, conn,flownum);
					d = newDate;
				}
			} else if ("002".equals(card_type)) {
				// 储值卡
				en = new EntityImpl(conn);
				String sql = "select a.state,a.deadline,a.times,a.id from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='002' and b.cust_name=?  and a.mem_id=?";
				size = en.executeQuery(sql, new String[] { cust_name, memId });
				// 判断是否已经有购买了的储值卡
				if (size > 0) {
					// 已经有购买了的储值卡了
					String id = en.getStringValue("id");
					Date deadline = en.getDateValue("deadline");
					int oldTimes = en.getIntegerValue("times");
					if (deadline == null) {
						deadline = new Date();
					}
					Calendar cld = Calendar.getInstance();
					if (deadline.after(new Date())) {
						cld.setTime(deadline);
					}
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					en = new EntityImpl(conn);
					sql = "update yp_type_user_" + gym + " set times=?, deadline=? ,type_code=? where id=?";
					en.executeUpdate(sql, new String[] { oldTimes + times + "", sdf.format(newDate), type_code, id });

					// 查询该账户的余额
					en = new EntityImpl("yp_mem", conn);
					en.setTablename("yp_mem_" + cust_name);
					en.setValue("id", memId);
					size = en.search();

					d = newDate;
					recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", memId, "001", "", caAmt, conn,flownum);
				}
			} else if ("004".equals(card_type)) {// 时间卡
				en = new EntityImpl(conn);
				String sql = "select a.id,a.mins from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='004' and b.cust_name=?  and a.mem_id=?";
				size = en.executeQuery(sql, new String[] { cust_name, memId });
				// 判断是否已经有购买了的时间卡
				if (size > 0) {
					String id = en.getStringValue("id");
					int oldmins = en.getIntegerValue("mins");
					Date deadline = en.getDateValue("deadline");
					if (deadline == null) {
						deadline = new Date();
					}
					Calendar cld = Calendar.getInstance();
					if (deadline.after(new Date())) {
						cld.setTime(deadline);
					}
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					sql = "update yp_type_user_" + gym + " set mins=?, deadline=? ,type_code=? where id=?";
					en.executeUpdate(sql, new String[] { oldmins + mins + "", sdf.format(newDate), type_code, id });
					d = newDate;
					recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", memId, "001", "", caAmt, conn,flownum);
				}

			} else {
				// 次数卡
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", memId, "001", "", caAmt,
						conn,flownum);
				buy(en, gym, cust_name, type_code, memId, newDate, times, mins, "001", "admin", "", recordId, conn);
				d = newDate;
			}
			// 发送消息
			JSONObject body = new JSONObject();
			body.put("deadline", Utils.parseData(d, "yyyy-MM-dd"));
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
			body.put("type_name", type_name);
			body.put("pay_money", realAmt);
			//MsgToMemAction.SendMsg("mem_renew", memId, body, conn, cust_name, gym);
		}
		return recordId;
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
			String mem_id, String active_type, String active_time, String real_price, Connection conn, String flownum)
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
		en.setValue("flow_num", flownum);
		String id = en.create();
		return id;
	}

}
