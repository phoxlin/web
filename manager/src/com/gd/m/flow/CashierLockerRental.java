package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gd.m.Flow;
import com.gd.m.FlowType;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class CashierLockerRental {
	public String xx(JSONObject json, Connection conn, String memId, String cust_name, String gym, String flownum,
			long realAmt, String op_id, String op_name) throws Exception {
		// String id = this.getMemId();
		// JSONObject json = this.getContent();
		String time = (String) json.get("box_end_time");
		String box_id = json.getString("box_id");
		String box_start_time = json.getString("box_start_time");

		String box_end_time = "";

		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Date end_time;
		try {
			end_time = s.parse(time);
		} catch (Exception e) {
			s.applyPattern("yyyy-MM-dd HH:mm:ss");
			end_time = s.parse(time);
		}

		// 使用赠送天数
		int giftCabinetDays = 0;//YpActiveAction.getAndUpdateUserActiCabinet(gym, memId, flownum, conn);
		if (giftCabinetDays > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(end_time);
			cal.add(Calendar.DATE, giftCabinetDays);
			time = s.format(cal.getTime());
		}
		box_end_time = s.format(end_time);
		String buy_type = json.getString("type");
		if ("001".equals(buy_type)) {// 续租
			Entity en = new EntityImpl(conn);

			String sql = "select box_end_time from yp_mem_" + cust_name + " where id=?";
			en.executeQuery(sql, new Object[] { memId });
			box_start_time = s.format(en.getDateValue("box_end_time"));
			sql = "update yp_mem_" + cust_name + " set box_end_time=? where id=?";
			en.executeUpdate(sql, new String[] { time, memId });

			sql = "select id from yp_box where mem_id=?";
			en = new EntityImpl(conn);
			en.executeQuery(sql, new String[] { memId });
			box_id = en.getStringValue("id");
		} else if ("002".equals(buy_type)) {
			String rentboxFee = json.getString("rentboxFee");
			// 出租
			Entity en = new EntityImpl(conn);
			String box_no = "";
			Entity entity = new EntityImpl("yp_box", conn);
			entity.setValue("id", box_id);
			int size = entity.search();
			if (size > 0) {
				box_no = entity.getStringValue("box_no");
			}
			String user_name = json.getString("user_name");

			String sql = "update yp_mem_" + cust_name
					+ " set box_end_time=?,box_start_time=?,has_box='Y',box_num=? where id=?";
			en.executeUpdate(sql, new String[] { time, box_start_time, box_no, memId });
			sql = "update yp_box set is_rent=?,mem_id=?,mem_name=?,state=? where id=?";
			en.executeUpdate(sql, new String[] { "Y", memId, user_name, "002", box_id });

			/**
			 * 添加租柜押金
			 * 
			 */
			Flow flow = new Flow();
			flow.setGym(gym);
			flow.setConn(conn);
			flow.setEmpId(op_id);
			flow.setEmpName(op_name);
			flow.setType(FlowType.租柜押金);
			flow.setGdName("会员租柜押金");
			flow.setMemId(memId);
			flow.setUserName(user_name);
			flow.setT("收银台");
			JSONObject obj = new JSONObject();
			flow.setContent(obj);
			flow.setRealAmt(Long.parseLong(rentboxFee) * 100);
			flow.setCaAmt(Long.parseLong(rentboxFee) * 100);
			flow.setOpId(op_id);
			flow.setOpName(op_name);
			flow.setDataId(box_id);
			flow.setDataTableName("yp_box_rent");
			flow.setState("001");
			//flow.create();

		}
		Entity en = new EntityImpl("yp_box_rent", conn);
		en.setValue("cust_name", cust_name);
		en.setValue("gym", gym);
		en.setValue("fk_box_id", box_id);
		en.setValue("mem_id", memId);
		en.setValue("mem_gym", gym);
		en.setValue("start_time", box_start_time);
		en.setValue("end_time", box_end_time);
		en.setValue("rent_fee", realAmt);
		en.setValue("state", "001");
		en.setValue("flow_num", flownum);
		en.setValue("flow_table_name", "yp_box_rent");
		String id = en.create();

		// 发消息
		JSONObject body = new JSONObject();
		body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
		float f = 0f + realAmt;
		body.put("pay_money", f / 100);
		//MsgToMemAction.SendMsg("mem_rent_box", memId, body, conn, cust_name, gym);
		return id;
	}

	public String xx(Flow fw, Connection conn, String memId, String cust_name, String gym, String flownum, long realAmt,
			String op_id, String op_name) throws Exception {
		// String id = this.getMemId();
		// JSONObject json = this.getContent();
		JSONObject json = fw.getContent();
		String time = (String) json.get("box_end_time");
		String box_id = json.getString("box_id");
		String box_start_time = json.getString("box_start_time");

		String box_end_time = "";

		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		Date end_time;
		try {
			end_time = s.parse(time);
		} catch (Exception e) {
			s.applyPattern("yyyy-MM-dd HH:mm:ss");
			end_time = s.parse(time);
		}

		// 使用赠送天数
		int giftCabinetDays = 0;//YpActiveAction.getAndUpdateUserActiCabinet(gym, memId, flownum, conn);
		if (giftCabinetDays > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(end_time);
			cal.add(Calendar.DATE, giftCabinetDays);
			time = s.format(cal.getTime());
		}
		box_end_time = s.format(end_time);
		String buy_type = json.getString("type");
		if ("001".equals(buy_type)) {// 续租
			Entity en = new EntityImpl(conn);

			String sql = "select box_end_time from yp_mem_" + cust_name + " where id=?";
			en.executeQuery(sql, new Object[] { memId });
			box_start_time = s.format(en.getDateValue("box_end_time"));
			sql = "update yp_mem_" + cust_name + " set box_end_time=? where id=?";
			en.executeUpdate(sql, new String[] { time, memId });

			sql = "select id from yp_box where mem_id=?";
			en = new EntityImpl(conn);
			en.executeQuery(sql, new String[] { memId });
			box_id = en.getStringValue("id");
		} else if ("002".equals(buy_type)) {
			String rentboxFee = json.getString("rentboxFee");
			// 出租
			Entity en = new EntityImpl(conn);
			String box_no = "";
			Entity entity = new EntityImpl("yp_box", conn);
			entity.setValue("id", box_id);
			int size = entity.search();
			if (size > 0) {
				box_no = entity.getStringValue("box_no");
			}
			String user_name = json.getString("user_name");

			String sql = "update yp_mem_" + cust_name
					+ " set box_end_time=?,box_start_time=?,has_box='Y',box_num=? where id=?";
			en.executeUpdate(sql, new String[] { time, box_start_time, box_no, memId });
			sql = "update yp_box set is_rent=?,mem_id=?,mem_name=?,state=? where id=?";
			en.executeUpdate(sql, new String[] { "Y", memId, user_name, "002", box_id });

			/**
			 * 添加租柜押金
			 * 
			 */
			Flow flow = new Flow();
			flow.setGym(gym);
			flow.setPid(fw.getId());// 指定本条押金记录与租柜的关系
			flow.setConn(conn);
			flow.setEmpId(op_id);
			flow.setEmpName(op_name);
			flow.setType(FlowType.租柜押金);
			flow.setGdName("会员租柜押金");
			flow.setMemId(memId);
			flow.setUserName(user_name);
			flow.setT("收银台");
			JSONObject obj = new JSONObject();
			flow.setContent(obj);
			flow.setRealAmt(Long.parseLong(rentboxFee) * 100);
			flow.setCaAmt(Long.parseLong(rentboxFee) * 100);
			flow.setOpId(op_id);
			flow.setOpName(op_name);
			flow.setDataId(box_id);
			flow.setDataTableName("yp_box_rent");
			flow.setState("001");
			String counterFeeType = fw.getCounterFeeType();
			Long fee = Long.parseLong(rentboxFee) * 100;
			if ("cashAmt".equals(counterFeeType)) {
				if (fw.getCardAmtBak() != null) {
					// 余额+现金支付的
					flow.setCashAmt(fee - fw.getCardAmtBak());
					flow.setCardAmt(fw.getCardAmtBak());
				} else {
					flow.setCashAmt(fee);
				}
			} else if ("cardCashAmt".equals(counterFeeType)) {
				if (fw.getCardAmtBak() != null) {
					// 余额+刷卡支付的
					flow.setCardCashAmt(fee - fw.getCardAmtBak());
					flow.setCardAmt(fw.getCardAmtBak());
				} else {
					flow.setCardCashAmt(fee);
				}
			} else if ("aliAmt".equals(counterFeeType)) {
				if (fw.getCardAmtBak() != null) {
					// 余额+支付宝支付的
					flow.setAliAmt(fee - fw.getCardAmtBak());
					flow.setCardAmt(fw.getCardAmtBak());
				} else {
					flow.setAliAmt(fee);

				}
			} else if ("wxAmt".equals(counterFeeType)) {
				if (fw.getCardAmtBak() != null) {
					// 余额+微信支付的
					flow.setCardCashAmt(fee - fw.getCardAmtBak());
					flow.setWxAmt(fw.getCardAmtBak());
				} else {
					flow.setWxAmt(fee);

				}
			} else if ("cardAmt".equals(counterFeeType)) {
				// 余额足额支付押金
				flow.setCardAmt(fee);
			}

			JSONObject content = new JSONObject();
			JSONArray goods = new JSONArray();
			goods.put(new JSONObject().put("name", "租柜押金").put("price", rentboxFee).put("num", 1));
			content.put("goods", goods);
			flow.setContent(content);
			//flow.create();
			// 更新用户余额
			flow.submitPay(cust_name, conn);

		}
		Entity en = new EntityImpl("yp_box_rent", conn);
		en.setValue("cust_name", cust_name);
		en.setValue("gym", gym);
		en.setValue("fk_box_id", box_id);
		en.setValue("mem_id", memId);
		en.setValue("mem_gym", gym);
		en.setValue("start_time", box_start_time);
		en.setValue("end_time", box_end_time);
		en.setValue("rent_fee", realAmt);
		en.setValue("state", "001");
		en.setValue("flow_num", flownum);
		en.setValue("flow_table_name", "yp_box_rent");
		String type = "";//ParamValUtils.getValues(cust_name, gym, "rentBoxFeeType", "rentBoxFeeType", conn);
		if("month".equals(type)){
			en.setValue("unit", 30);
		}else if("quarter".equals(type)){
			en.setValue("unit", 90);
		}else if("year".equals(type)){
			en.setValue("unit", 365);
		}else{
			en.setValue("unit", 1);
		}
		String id = en.create();

		// 发消息
		JSONObject body = new JSONObject();
		body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
		float f = 0f + realAmt;
		body.put("pay_money", f / 100);
		//MsgToMemAction.SendMsg("mem_rent_box", memId, body, conn, cust_name, gym);
		return id;
	}

}
