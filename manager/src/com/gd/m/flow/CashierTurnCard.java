package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.gd.m.GdUser;
import com.gd.m.card.ICard;
import com.gd.m.card.UserCardUtils;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class CashierTurnCard {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");

	/**
	 * 跨店
	 * 
	 * @param conn
	 * @param json
	 * @param cust_name
	 * @param gym
	 * @param caAmt
	 * @throws Exception
	 */
	public void xx2(Connection conn, JSONObject json, String cust_name, String gym, String caAmt) throws Exception {

		// 发消息
		String name = json.getString("name");
		String from_id = json.getString("from_id");
		String from_phone = json.getString("from_phone");
		String to_phone = json.getString("to_phone");
		String price = json.getString("price");
		String from_name = json.getString("from_name");
		String to_id = json.getString("to_id");
		String yp_type_user_id = json.getString("yp_type_user_id");
		String type_code = json.getString("card");
		String toGym = "";
		if (json.has("to_gym")) {
			toGym = json.getString("to_gym");
		}

		/**
		 * 删除转卡会员的卡
		 * 
		 */
		Entity en = new EntityImpl("yp_type_user", conn);
		en.setTablename("yp_type_user_" + gym);
		en.setValue("id", yp_type_user_id);
		en.delete();
		List<ICard> cards = null;
		try {
			cards = UserCardUtils.getUserCards(cust_name, from_id, conn);
		} catch (Exception e) {
		}
		if (cards == null || cards.size() == 0) {
			// 转卡人没有会员卡了 将转卡人的状态设置为 007
			en = new EntityImpl(conn);
			en.executeUpdate("update yp_mem_" + cust_name + " set state='007' where id=?", new String[] { from_id });
		}

		en = new EntityImpl("yp_type", conn);
		en.setValue("cust_name", cust_name);
		en.setValue("type_code", type_code);
		int size = en.search();
		if (size > 0) {
			int days = en.getIntegerValue("days");// 天数卡天数
			int amt = en.getIntegerValue("amt");// 储值卡金额
			int times = en.getIntegerValue("times"); // 次数卡次数
			int mins = en.getIntegerValue("mins");
			String general_store_level = en.getStringValue("general_store_level"); // 通店组别
			String card_type = en.getStringValue("card_type");
			Entity entity2 = new EntityImpl("yp_type_gym", conn);
			entity2.setValue("cust_name", cust_name);
			entity2.setValue("type_code", type_code);
			int g_size = entity2.search(); // 可见会所

			// 天数卡
			if ("001".equals(card_type)) {
				// 如果没有购买此卡
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				String buy_id = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", to_id, "001", caAmt, conn,
						toGym);
				buy(en, gym, cust_name, type_code, to_id, newDate, times, mins, "001", "admin", "admin", buy_id, conn,
						toGym);
			} else if ("002".equals(card_type)) {
				// 储值卡
				Entity en2 = new EntityImpl(conn);

				String sql = "select a.state,a.deadline,a.times,a.id from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='002' and a.mem_id=? and b.cust_name=?   and a.state='002'";
				size = en2.executeQuery(sql, new String[] { to_id, cust_name });
				// 判断是否已经有购买了的储值卡
				if (size > 0) {
					String cstate = en2.getStringValue("state");
					String type_user_id = en2.getStringValue("id");
					if ("001".equals(cstate)) {
						throw new Exception("该会员已有一张未激活的储值卡,暂时无法继续购买储值卡!");
					}
					// 已经有购买了的储值卡了
					Date deadline = en2.getDateValue("deadline");
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
					en2 = new EntityImpl(conn);
					sql = "update yp_type_user_" + gym + " set deadline=? where id=?";
					en2.executeUpdate(sql, new String[] { sdf.format(newDate), type_user_id });

					// 查询该账户的余额
					en2 = new EntityImpl("yp_mem", conn);
					en2.setTablename("yp_mem_" + cust_name);
					en2.setValue("id", to_id);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;

						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", to_id });
					}
					createBuyCardsRecord(en, gym, cust_name, type_code, "admin", to_id, "001", caAmt, conn, toGym);
				} else {
					Calendar cld = Calendar.getInstance();
					cld.setTime(new Date());
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					String buy_id = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", to_id, "001", caAmt,
							conn, toGym);
					buy(en, gym, cust_name, type_code, to_id, newDate, times, mins, "001", "admin", "admin", buy_id,
							conn, toGym);

					// 查询该账户的余额
					en2 = new EntityImpl("yp_mem", conn);
					en2.setTablename("yp_mem_" + cust_name);
					en2.setValue("id", to_id);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;
						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", to_id });
					}
				}
			} else if ("004".equals(card_type)) {// 时间卡
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				String buy_id = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", to_id, "001", caAmt, conn,
						toGym);
				buy(en, gym, cust_name, type_code, to_id, newDate, times, mins, "001", "admin", "admin", buy_id, conn,
						toGym);
			} else {
				// 如果没有购买此卡
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				String buy_id = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", to_id, "001", caAmt, conn,
						toGym);
				buy(en, gym, cust_name, type_code, to_id, newDate, times, mins, "001", "admin", "admin", buy_id, conn,
						toGym);
			}

		}
		GdUser to_user = new GdUser(cust_name, to_id, false, conn);
		String to_name = to_user.getUserName();

		JSONObject body = new JSONObject();
		body.put("from_name", from_name + "(" + from_phone + ")");
		body.put("to_name", to_name + "(" + to_phone + ")");
		body.put("pay", price);
		body.put("to_id", to_id);
		//MsgToMemAction.SendMsg("mem_trans", from_id, body, conn, cust_name, gym);

	}

	public void xx(Connection conn, JSONObject json, String cust_name, String gym) throws Exception {
		// 发消息
		String name = json.getString("name");
		String from_id = json.getString("from_id");
		String from_phone = json.getString("from_phone");
		String to_phone = json.getString("to_phone");
		String price = json.getString("price");
		String from_name = json.getString("from_name");
		String to_id = json.getString("to_id");
		String yp_type_user_id = json.getString("yp_type_user_id");

		/**
		 * 判断卡种
		 * 
		 */
		Entity en = new EntityImpl(conn);
		String sql = "select b.* from yp_type_user_" + gym
				+ " a,yp_type b where a.type_code = b.type_code and a.id=? and b.cust_name=? ";
		int size = en.executeQuery(sql, new String[] { yp_type_user_id, cust_name });
		if (size > 0) {
			String type_code = en.getStringValue("type_code");
			int days = en.getIntegerValue("days");// 天数卡天数
			int amt = en.getIntegerValue("amt");// 储值卡金额
			int times = en.getIntegerValue("times"); // 次数卡次数
			int mins = en.getIntegerValue("mins");
			String card_type = en.getStringValue("card_type");
			String general_store_level = en.getStringValue("general_store_level"); // 通店组别
			Entity entity2 = new EntityImpl("yp_type_gym", conn);
			entity2.setValue("cust_name", cust_name);
			entity2.setValue("type_code", type_code);
			int g_size = entity2.search(); // 可见会所

			if ("002".equals(card_type)) {
				// 储值卡
				Entity en2 = new EntityImpl(conn);

				sql = "select a.state,a.deadline,a.times from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='002' and a.mem_id=? and b.cust_name=?   and a.state='002'";
				size = en2.executeQuery(sql, new String[] { to_id, cust_name });
				// 判断是否已经有购买了的储值卡
				if (size > 0) {
					String cstate = en2.getStringValue("state");
					if ("001".equals(cstate)) {
						throw new Exception("该会员已有一张未激活的储值卡,暂时无法继续购买储值卡!");
					}
					// 已经有购买了的储值卡了
					Date deadline = en2.getDateValue("deadline");
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

					// 查询该账户的余额
					en2 = new EntityImpl("yp_mem", conn);
					en2.setTablename("yp_mem_" + cust_name);
					en2.setValue("id", from_id);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;

						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", to_id });
					}
					/**
					 * 删除转卡人之前的卡
					 */
					en = new EntityImpl(conn);
					sql = "delete from  yp_type_user_" + gym + "  where id=?";
					en.executeUpdate(sql, new String[] { yp_type_user_id });
				} else {
					Calendar cld = Calendar.getInstance();
					cld.setTime(new Date());
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					en = new EntityImpl(conn);
					sql = "update yp_type_user_" + gym + " set mem_id=? where id=?";
					en.executeUpdate(sql, new String[] { to_id, yp_type_user_id });
					// 查询该账户的余额
					en2 = new EntityImpl("yp_mem", conn);
					en2.setTablename("yp_mem_" + cust_name);
					en2.setValue("id", from_id);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;
						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", to_id });
					}
				}
			} else {
				en = new EntityImpl(conn);
				sql = "update yp_type_user_" + gym + " set mem_id=? where id=?";
				en.executeUpdate(sql, new String[] { to_id, yp_type_user_id });
			}

			GdUser to_user = new GdUser(cust_name, to_id, false, conn);
			String to_name = to_user.getUserName();
			String state = to_user.getXX("state");
			if (!"002".equals(state)) {
				en = new EntityImpl(conn);
				sql = "update yp_mem_" + cust_name + " set state='002' where id=?";
				en.executeUpdate(sql, new String[] { to_id });
			}

			JSONObject body = new JSONObject();
			body.put("from_name", from_name + "(" + from_phone + ")");
			body.put("to_name", to_name + "(" + to_phone + ")");
			body.put("pay", price);
			body.put("to_id", to_id);
			//MsgToMemAction.SendMsg("mem_trans", from_id, body, conn, cust_name, gym);
		} else {
			throw new Exception("未查询到该会员的相关信息，请刷新后重试！");
		}
	}

	private String createBuyCardsRecord(Entity en, String gym, String cust_name, String type_code, String emp_id,
			String mem_id, String active_type, String real_price, Connection conn, String toGym) throws Exception {
		en = new EntityImpl("yp_buy_card_record", conn);
		if (toGym != null && !"".equals(toGym)) {
			en.setTablename("yp_buy_card_record_" + toGym);
		} else {
			en.setTablename("yp_buy_card_record_" + gym);
		}
		en.setValue("cust_name", cust_name);
		en.setValue("type_code", type_code);
		en.setValue("emp_id", emp_id);
		en.setValue("mem_id", mem_id);
		en.setValue("is_flow", "N");
		en.setValue("state", "Y");
		en.setValue("deal_time", new Date());
		en.setValue("active_type", active_type);
		en.setValue("active_time", new Date());
		en.setValue("price", real_price);
		en.setValue("real_price", real_price);
		String id = en.create();
		return id;
	}

	private void buy(Entity en, String gym, String cust_name, String type_code, String user_id, Date deadline,
			int times, int mins, String active_type, String sales_id, String sales_name, String buy_id, Connection conn,
			String toGym) throws Exception {
		en = new EntityImpl("yp_type_user", conn);
		if (toGym != null && !"".equals(toGym)) {
			en.setTablename("yp_type_user_" + toGym);
		} else {
			en.setTablename("yp_type_user_" + gym);
		}
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
}
