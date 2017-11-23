package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.gd.m.Flow;
import com.gd.m.FlowType;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

/**
 * 购买会员卡
 * 
 * @author 1
 *
 */
public class buyCard {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");

	public String xx(JSONObject con, Connection conn, String cust_name, String memId, String gym, String flownum,
			String opId, String opName, String dataId, String caAmt, String realAmt) throws Exception {
		String recordId = "";
		String counterFee = con.getString("counterFee");
		if (con.has("pre_fee")) {
			JSONObject pre_fees = con.getJSONObject("pre_fee");
			Entity pre_fee = new EntityImpl("yp_prefee", conn);
			pre_fee.setTablename("yp_prefee_" + cust_name);
			for (String pre_id : pre_fees.keySet()) {
				pre_fee.setValue("id", pre_id);
				pre_fee.setValue("state", "Y");
				pre_fee.setValue("use_time", new Date());
				pre_fee.update();
			}
		}

		// 会籍ID
		String sales_id = "admin";
		String sales_name = "";
		if (con.keySet().contains("sales_id")) {

			sales_id = con.get("sales_id").toString();
			if (sales_id != null && sales_id.length() == 24) {
				sales_name = "";//AppUtils.getEmpNameById(sales_id, conn);
				Entity u = new EntityImpl("yp_mem", conn);
				u.setTablename("yp_mem_" + cust_name);
				u.setValue("id", memId);
				u.setValue("sales_id", sales_id);
				u.update();
			}
		}
		String remark = "";
		if (con.has("remark")) {
			remark = con.getString("remark");
		}
		String active_type = "001";
		if (con.has("active_type")) {
			active_type = con.getString("active_type");
		}
		String active_time = "";
		if (con.has("active_time")) {
			active_time = con.getString("active_time");
		}

		int gift_days = 0;
		if (con.has("acti_id") && con.get("acti_id") != null) {
			String acti_id = con.getString("acti_id");
			Entity act = new EntityImpl("yp_active", conn);
			String sq = "select * from yp_active where id=?";
			int asize = act.executeQuery(sq, new String[] { acti_id });
			if (asize > 0) {
				String act_name = act.getStringValue("act_name");
				float dis = act.getFloatValue("dis");
				long dis_amt = act.getLongValue("dis_amt");
				gift_days = act.getIntegerValue("days");
				int box_days = act.getIntegerValue("box_days");
				int lesson_num = act.getIntegerValue("lession_num");
				int lesson_limit = act.getIntegerValue("over_lession_num");
				String gift_id = act.getStringValue("gift_id");

				Entity acti = new EntityImpl("yp_active", conn);
				int remain_gift_num = act.getIntegerValue("remain_gift_num");
				if (remain_gift_num <= 0) {
					throw new Exception("该活动礼包已赠送完毕,无法参与该活动!");
				} else {
					String s = "update yp_active set remain_gift_num=? where id=?";
					acti.executeUpdate(s, new Object[] { remain_gift_num - 1, acti_id });
				}

				Entity user_acti = new EntityImpl("yp_user_acti", conn);
				user_acti.setValue("cust_name", cust_name);
				user_acti.setValue("gym", gym);
				user_acti.setValue("mem_id", memId);
				user_acti.setValue("act_id", acti_id);
				user_acti.setValue("act_name", act_name);
				user_acti.setValue("dis", dis);
				user_acti.setValue("dis_amt", dis_amt);
				user_acti.setValue("gift_days", gift_days);
				user_acti.setValue("gift_days_used", "Y");
				user_acti.setValue("gift_cabinet", box_days);
				if (box_days == 0) {
					user_acti.setValue("gift_cabinet_used", "Y");
				} else {
					user_acti.setValue("gift_cabinet_used", "N");
				}
				user_acti.setValue("gift_sclass", lesson_num);
				user_acti.setValue("gift_sclass_limit", lesson_limit);
				if (lesson_num == 0) {
					user_acti.setValue("gift_sclass_used", "Y");
				} else {
					user_acti.setValue("gift_sclass_used", "N");
				}
				user_acti.setValue("gift_id", gift_id);
				user_acti.setValue("flow_num", flownum);
				user_acti.setValue("type_code", con.getString("type_code"));
				user_acti.create();

				// 赠送商品
				// 查询礼包的商品信息
				Entity gift = new EntityImpl(conn);
				String gsql = "select a.num gift_num,b.good_name,b.store_id, b.id goods_id, b.good_num from yp_gift_goods a, yp_goods b where"
						+ " a.gift_id=? and a.goods_id = b.id";
				int size = gift.executeQuery(gsql, new String[] { gift_id });

				List<String> infos = new ArrayList<>();
				Entity goods = new EntityImpl("yp_goods", conn);
				for (int i = 0; i < size; i++) {
					String name = gift.getStringValue("good_name", i);
					int gift_num = gift.getIntegerValue("gift_num", i);
					int store_num = gift.getIntegerValue("good_num", i);

					if (gift_num > store_num) {
						infos.add("商品【" + name + "】数量不足,无法进行礼包赠送!");
					} else {
						String goods_id = gift.getStringValue("goods_id", i);
						goods.setValue("id", goods_id);
						goods.setValue("good_num", store_num - gift_num);
						goods.update();

						String store_id = gift.getStringValue("store_id", i);
						Entity rec = new EntityImpl("yp_store_rec", conn);
						rec.setValue("store_id", store_id);
						rec.setValue("good_id", goods_id);
						rec.setValue("good_num", 0 - gift_num);
						rec.setValue("op_id", opId);
						rec.setValue("cust_name", cust_name);
						rec.setValue("gym", gym);
						rec.setValue("op_name", opName);
						rec.setValue("op_time", new Date());
						rec.setValue("store_num", store_num - gift_num);
						rec.create();
					}
				}
				if (infos.size() > 0) {
					throw new Exception(infos.toString());
				}
			}
		}

		// 处理会员卡
		Entity en = new EntityImpl("yp_type", conn);
		en.setValue("id", dataId);
		int size = en.search();
		if (size > 0) {
			String type_code = en.getStringValue("type_code");
			int days = en.getIntegerValue("days");// 天数卡天数
			int amt = en.getIntegerValue("amt");// 储值卡金额
			int times = en.getIntegerValue("times"); // 次数卡次数
			int mins = en.getIntegerValue("mins");
			int type_fee = en.getIntegerValue("type_fee");
			String general_store_level = en.getStringValue("general_store_level"); // 通店组别
			Entity entity2 = new EntityImpl("yp_type_gym", conn);
			entity2.setValue("cust_name", cust_name);
			entity2.setValue("type_code", type_code);
			int g_size = entity2.search(); // 可见会所

			String card_type = en.getStringValue("card_type");

			days += gift_days; // 加上赠送天数;

			if ("001".equals(active_type)) {
				Entity u = new EntityImpl("yp_mem", conn);
				// u.setTablename("yp_mem_" + cust_name);
				// u.setValue("id", memId);
				// u.setValue("state", "001");
				// u.setValue("remark", remark);
				// u.update();
				String sql = "update yp_mem_" + cust_name + " set state=?";
				if (remark != null && remark.length() > 0) {
					sql += " ,remark=concat(remark,' " + remark + "')";
				}
				sql += " where id=?";
				u.executeUpdate(sql, new String[] { "001", memId });
			} else {
				Entity u = new EntityImpl("yp_mem", conn);
				u.setTablename("yp_mem_" + cust_name);
				u.setValue("id", memId);
				u.search();

				String estate = u.getStringValue("state");
				if ("003".equals(estate) || "004".equals(estate) || "008".equals(estate)) {
					u.setValue("state", "002");
					String _remark = u.getStringValue("remark");
					if (_remark != null && _remark.length() > 0) {
						remark = _remark + " " + remark;
					}
					u.setValue("remark", remark);
					u.update();
				}
			}

			Date d = new Date();
			if ("001".equals(card_type)) {
				// 天数卡
				Calendar cld = Calendar.getInstance();
				if (active_time != null && active_time.length() > 0 && "003".equals(active_type)) {
					cld.setTime(Utils.parse2Date(active_time));
				} else {
					cld.setTime(new Date());
				}
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, sales_id, memId, active_type,
						active_time, caAmt, realAmt, conn, flownum);
				buy(en, gym, cust_name, type_code, memId, newDate, times, mins, active_type, active_time, sales_id,
						sales_name, recordId, conn, remark);
				d = newDate;
			} else if ("002".equals(card_type)) {
				// 储值卡
				Entity en2 = new EntityImpl(conn);

				String sql = "select a.state,a.deadline,a.times,a.id from yp_type_user_" + gym
						+ " a,yp_type b where a.type_code = b.type_code and b.card_type='002' and a.mem_id=? and b.cust_name=?   and a.state='002'";
				size = en2.executeQuery(sql, new String[] { memId, cust_name });
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
					en2.setValue("id", memId);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;

						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", memId });
					}
					recordId = createBuyCardsRecord(en, gym, cust_name, type_code, sales_id, memId, "001", "", caAmt,
							realAmt, conn, flownum);

					d = newDate;
				} else {
					Calendar cld = Calendar.getInstance();
					cld.setTime(new Date());
					cld.add(Calendar.DAY_OF_YEAR, days);
					// 得到续费后的日期
					Date newDate = cld.getTime();
					recordId = createBuyCardsRecord(en, gym, cust_name, type_code, sales_id, memId, active_type,
							active_time, caAmt, realAmt, conn, flownum);
					buy(en, gym, cust_name, type_code, memId, newDate, times, mins, active_type, active_time, sales_id,
							sales_name, recordId, conn, remark);
					d = newDate;

					// 查询该账户的余额
					en2 = new EntityImpl("yp_mem", conn);
					en2.setTablename("yp_mem_" + cust_name);
					en2.setValue("id", memId);
					size = en2.search();
					if (size > 0) {
						int userAmt = en2.getIntegerValue("amt");
						int newAmt = userAmt + amt * 100;
						sql = "update yp_mem_" + cust_name + " set amt=? where id =?";
						en2.executeUpdate(sql, new String[] { newAmt + "", memId });
					}
				}
			} else if ("004".equals(card_type)) {// 时间卡
				Calendar cld = Calendar.getInstance();
				cld.setTime(new Date());
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, sales_id, memId, active_type,
						active_time, caAmt, realAmt, conn, flownum);
				buy(en, gym, cust_name, type_code, memId, newDate, times, mins, active_type, active_time, sales_id,
						sales_name, recordId, conn, remark);
				d = newDate;
			} else {
				Calendar cld = Calendar.getInstance();
				if (active_time != null && active_time.length() > 0 && "003".equals(active_type)) {
					cld.setTime(Utils.parse2Date(active_time));
				} else {
					cld.setTime(new Date());
				}
				cld.add(Calendar.DAY_OF_YEAR, days);
				// 得到续费后的日期
				Date newDate = cld.getTime();
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, sales_id, memId, active_type,
						active_time, caAmt, realAmt, conn, flownum);
				buy(en, gym, cust_name, type_code, memId, newDate, times, mins, active_type, active_time, sales_id,
						sales_name, recordId, conn, remark);
				d = newDate;
			}
			/**
			 * 购买会员卡 后激活会员
			 */

			// 发送消息
			Entity entity = new EntityImpl("yp_type", conn);
			String type_name = "";
			size = entity.executeQuery("select * from yp_type where cust_name=? and type_code=? and card_type=?",
					new String[] { cust_name, type_code, card_type });
			if (size > 0) {
				type_name = entity.getStringValue("type_name");
			}
			JSONObject body = new JSONObject();
			body.put("deadline", Utils.parseData(d, "yyyy-MM-dd"));
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
			body.put("type_name", type_name);
			float x = Float.parseFloat(Utils.toPrice(realAmt)) / 100;
			body.put("pay_money", Utils.toPrice(x + ""));
			//MsgToMemAction.SendMsg("mem_buy_card", memId, body, conn, cust_name, gym);

			/**
			 * 
			 */
			if (counterFee != null && counterFee.length() > 0 && !"0.00".equals(counterFee) && !"0".equals(counterFee)
					&& !"null".equals(counterFee)) {
				Entity u = new EntityImpl("yp_mem", conn);
				u.setTablename("yp_mem_" + cust_name);
				u.setValue("id", memId);
				u.search();
				Flow flow = new Flow();
				flow.setGym(gym);
				flow.setT("收银台");
				flow.setConn(conn);
				flow.setEmpId(opId);
				flow.setEmpName(opName);
				flow.setType(FlowType.收银台发卡押金收费);
				flow.setGdName("收银台发卡押金收费");
				flow.setMemId(memId);
				flow.setContent(new JSONObject());
				flow.setUserName(u.getStringValue("user_name"));
				flow.setPhone(u.getStringValue("phone"));
				Double parseDouble = Double.parseDouble(counterFee);
				flow.setRealAmt(parseDouble.longValue());
				flow.setCashAmt(parseDouble.longValue());
				flow.setOpId(opId);
				flow.setOpName(opName);
				flow.setState("002");
				//flow.create();
			}
		}
		return recordId;
	}

	private String createBuyCardsRecord(Entity en, String gym, String cust_name, String type_code, String emp_id,
			String mem_id, String active_type, String active_time, String ca_price, String real_price, Connection conn,
			String flowNum) throws Exception {
		if ("".equals(emp_id)) {
			emp_id = "admin";
		}
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
		en.setValue("price", ca_price);
		en.setValue("real_price", real_price);
		en.setValue("flow_num", flowNum);
		String id = en.create();
		return id;
	}

	private void buy(Entity en, String gym, String cust_name, String type_code, String user_id, Date deadline,
			int times, int mins, String active_type, String active_time, String sales_id, String sales_name,
			String buy_id, Connection conn, String remark) throws Exception {
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
		en.setValue("remark", remark);
		if ("001".equals(active_type)) {// 001 立即激活
			en.setValue("act_time", new Date());
			en.setValue("deadline", deadline);
			en.setValue("state", "002");
		} else if ("003".equals(active_type) && active_time != null && active_time.length() > 0) {
			// 指定日期开卡，选择开卡日期在今天之前
			Date act_time = Utils.parse2Date(active_time, "yyyy-MM-dd");
			Date n = Utils.parse2Date(Utils.parseData(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
			if (act_time.getTime() <= n.getTime()) {
				en.setValue("act_time", Utils.parse2Date(active_time));
				en.setValue("deadline", deadline);
				en.setValue("state", "002");
			} else {
				en.setValue("state", "001");
			}
		} else {
			en.setValue("state", "001");
		}
		en.create();

	}

}
