package com.gd.m.flow;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;
/**
 * 购买私教课课程
 * @author 1
 *
 */
public class BuyClass {
	public void xx(String lesson_id, JSONObject obj, Connection conn, String cust_name, String memId, String empId,
			String gym, String flownum,String realAmt) throws Exception {
		String level = obj.getString("level");
		String coach = "";
		if (obj.has("coach_id")) {
			coach = obj.getString("coach_id");
		}
		Entity en = new EntityImpl(conn);
		String sq = "select a.* from yp_private_plan a where  a.id=? ";
		int szie = en.executeQueryWithOutMaxResult(sq, new String[] { lesson_id }, 1, 1);
		if (szie > 0) {
			int lesson_num = en.getIntegerValue("nums");
			int lesson_dead = en.getIntegerValue("lesson_dead");
			/*
			 * 判断是否是收银台购买课程---修改了课程节数，
			 */
			if (obj.has("type")) {
				String type = obj.getString("type");
				if ("cashierBuyClass".equals(type)) {// 说明是收银台购买收费课程
					lesson_num = obj.getInt("num");// 修改为收银台修改后的值
				}
			}
			String lesson_type = en.getStringValue("lesson_type");
			String lesson_name = en.getStringValue("lesson_name");
			if ("001".equals(lesson_type)) {
				if (!"".equals(empId) && !"-1".equals(lesson_type)) {
					//AppUtils.checkUserDefaultPt(false, cust_name, memId, empId, conn);
				}

				// 使用赠送私课数
				int giftSclassDays = 0;//YpActiveAction.getAndUpdateUserActiSclass(gym, memId, lesson_num, flownum, conn);
				lesson_num += giftSclassDays;

					en = new EntityImpl("yp_private_user", conn);
					en.setValue("cust_name", cust_name);
					if (!"".equals(empId)) {
						en.setValue("emp_id", coach);
					} else {
						en.setValue("emp_id", "admin");
					}
					if (lesson_dead > 0) {
						int days = lesson_dead * lesson_num;
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						cal.add(Calendar.DAY_OF_YEAR, days);
						en.setValue("deadline", cal.getTime());// 课程有效期=课程购买节数*课程有效期
					}

					en.setValue("mem_id", memId);
					en.setValue("lesson_id", lesson_id);
					en.setValue("lesson_num", lesson_num);
					en.setValue("gift_num", "0");
					en.setValue("nums", lesson_num);
					en.setValue("is_flow", "N");
					en.setValue("state", "001");
					en.setValue("level", level);
					en.setValue("flow_num", flownum);
					en.setValue("flow_table_name", "YP_FLOW_" + gym);
					en.setValue("buy_time", new Date());
					en.create();
//				}
			} else {
				// 付费团课
//				en = new EntityImpl(conn);
//				String sql = "select * from yp_grp_user where mem_id=? and lesson_id=? and state='001'";
//				int size = en.executeQuery(sql, new String[] { memId, lesson_id });
//				if (size > 0) {
//					int nums = en.getIntegerValue("nums");
//					int ln = en.getIntegerValue("lesson_num");
//					String id = en.getStringValue("id");
//					if (!"".equals(empId)) {
//						sql = "update  yp_grp_user set nums=?,lesson_num=? ";
//						if (lesson_dead > 0) {
//							int days = lesson_dead * lesson_num;
//							Calendar cal = Calendar.getInstance();
//							cal.setTime(new Date());
//							cal.add(Calendar.DAY_OF_YEAR, days);
//							// 课程有效期=课程购买节数*课程有效期
//							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ", Locale.CHINA);
//							sql += ",deadline='" + format.format(cal.getTime()) + "'";
//						}
//						sql += " where id=? and emp_id=?";
//						en.executeUpdate(sql, new String[] { nums + lesson_num + "", ln + lesson_num + "", id, empId });
//					} else {
//						sql = "update  yp_grp_user set nums=?,lesson_num=? where id=?";
//						en.executeUpdate(sql, new String[] { nums + lesson_num + "", ln + lesson_num + "", id });
//					}
//				} else {
					en = new EntityImpl("yp_grp_user", conn);
					en.setValue("cust_name", cust_name);
					if (coach != null && coach.length() > 0) {
						en.setValue("emp_id", coach);
					} else {
						en.setValue("emp_id", "admin");
					}
					if (lesson_dead > 0) {
						int days = lesson_dead * lesson_num;
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						cal.add(Calendar.DAY_OF_YEAR, days);
						en.setValue("deadline", cal.getTime());// 课程有效期=课程购买节数*课程有效期
					}
					en.setValue("mem_id", memId);
					en.setValue("lesson_id", lesson_id);
					en.setValue("lesson_num", lesson_num);
					en.setValue("gift_num", "0");
					en.setValue("nums", lesson_num);
					en.setValue("is_flow", "N");
					en.setValue("flow_table_name", "YP_FLOW_" + gym);
					en.setValue("state", "001");
					en.setValue("flow_num", flownum);
					en.setValue("buy_time", new Date());
					en.create();

				}
//			}
			// 发消息
			
			JSONObject body = new JSONObject();
			body.put("lesson_num", lesson_num);
			body.put("lesson_name", lesson_name);
			body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
			float x = Float.parseFloat(Utils.toPrice(realAmt)) / 100 ; 
			body.put("pay_money", Utils.toPrice(x + ""));
			//MsgToMemAction.SendMsg("mem_buy_lesson",memId, body, conn, cust_name, gym);
		}
	}
}
