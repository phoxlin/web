package com.gd.m.flow;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

public class CardUpdate {

	public String xx(JSONObject con,Connection conn,String cust_name,long remainAmt,String memId,String flownun,String gym,String mem_id,String real_price) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String recordId="";
		String type_code = con.getString("type_code");// 要升级卡的type_code
		String typ_user_id = con.getString("typ_user_id");// 之前购买卡的(yp_type_user)id
		String userGym = con.getString("userGym");// 之前购买卡的(yp_type_user)id
		Entity card = new EntityImpl("yp_type", conn);
		card.setValue("cust_name", cust_name);
		card.setValue("type_code", type_code);
		//拿到升级卡的名字
		Entity entity = new EntityImpl("yp_type",conn);
		entity.executeQuery("select type_name from yp_type where type_code=? and gym=?", new String[]{type_code,userGym});
		String newCardName = entity.getStringValue("type_name");
		String oldCardName = "";
		int size = card.search();
		if (size > 0) {
			String card_type = card.getStringValue("card_type");
			/**
			 * 之前的卡
			 */
			String sql = "select * from yp_type_user_" + userGym + " where id=?";
			Entity oldCard = new EntityImpl(conn);
			oldCard.executeQuery(sql, new String[] { typ_user_id });
			//拿到老卡的名字
			String oldTypeCode = oldCard.getStringValue("type_code");
			entity.executeQuery("select type_name from yp_type where type_code=? and gym=?", new String[]{oldTypeCode,userGym});
			oldCardName = entity.getStringValue("type_name");
			if ("001".equals(card_type)) {
				Date buy_time = oldCard.getDateValue("deadline");
				int newDays = card.getIntegerValue("days");
				Date newEnd = Utils.dateAddDay(buy_time, newDays);
				Entity en = new EntityImpl(conn);
				sql = "update yp_type_user_" + userGym + " set deadline=?, type_code=? where id=?";
				en.executeUpdate(sql, new Object[] { newEnd, type_code, typ_user_id });
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", mem_id, "002",sdf.format(new Date()), real_price, conn, flownun);
			} else if ("002".equals(card_type)) {
				long newAmt = card.getLongValue("amt");
				long remain_amt = remainAmt + newAmt;
				sql = "update yp_mem_" + cust_name + " set amt=? where id=?";
				Entity en = new EntityImpl(conn);
				en.executeUpdate(sql, new Object[] { remain_amt, memId });
				sql = "update yp_type_user_" + userGym + " set type_code=? where id=?";
				en.executeUpdate(sql, new Object[] { type_code, typ_user_id });
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", mem_id, "002",sdf.format(new Date()), real_price, conn, flownun);
			} else if ("003".equals(card_type)) {
				int newTimes = card.getIntegerValue("times");
				int odTimes = oldCard.getIntegerValue("times");
				Entity en = new EntityImpl(conn);
				sql = "update yp_type_user_" + userGym + " set times=?,type_code=? where id=?";
				en.executeUpdate(sql, new Object[] { newTimes + odTimes, type_code, typ_user_id });
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", mem_id, "002",sdf.format(new Date()), real_price, conn, flownun);
			} else if ("004".equals(card_type)) {
				int newMins = card.getIntegerValue("mins");
				int odMins = oldCard.getIntegerValue("mins");
				Entity en = new EntityImpl(conn);
				sql = "update yp_type_user_" + userGym + " set mins=?,type_code=? where id=?";
				en.executeUpdate(sql, new Object[] { newMins + odMins, type_code, typ_user_id });
				recordId = createBuyCardsRecord(en, gym, cust_name, type_code, "admin", mem_id, "002",sdf.format(new Date()), real_price, conn, flownun);
			}
		} else {
			throw new Exception("未查询到该卡种信息!");
		}
		
		// 发送消息
		JSONObject body = new JSONObject();
		body.put("pay_time", Utils.parseData(new Date(), "yyyy-MM-dd"));
		body.put("newCardName", newCardName);
		body.put("oldCardName", oldCardName);
		//MsgToMemAction.SendMsg("mem_upgrade_block", memId, body, conn, cust_name, gym);
		return recordId;
	}

	private String createBuyCardsRecord(Entity en, String gym, String cust_name, String type_code, String emp_id,
			String mem_id, String active_type, String active_time, String real_price, Connection conn, String flowNum)
			throws Exception {
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
		en.setValue("price", real_price);
		en.setValue("real_price", real_price);
		en.setValue("flow_num", flowNum);
		String id = en.create();
		return id;
	}
}
