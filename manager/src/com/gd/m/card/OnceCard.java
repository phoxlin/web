package com.gd.m.card;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gd.m.Flow;
import com.gd.m.GdUser;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

/**
 * 单次入场卷
 * 
 * 
 * @author terry
 *
 */
public class OnceCard extends ICard {

	public OnceCard() {
		/*
		 */
	}

	public OnceCard(Connection conn, String cust_name, String gym, String cardno) throws Exception {
		this.setCust_name(cust_name);
		this.setGym(gym);
		this.setCardno(cardno);
		Entity en = new EntityImpl(conn);
		int size = en.executeQuery("select * from  yp_fit_" + cust_name + " where card_no=? and gym=?", new String[] { cardno, gym });
		if (size > 0) {
			this.setSales_id(en.getStringValue("emp_id"));
			this.setCheckinState(ICard.getCheckinState(en.getStringValue("state")));
			this.setDeadline(en.getDateValue("deadline"));
			this.setType_code(en.getStringValue("type_code"));
			this.setMemId(en.getStringValue("mem_id"));
			this.setBuyDate(en.getDateValue("buy_time"));
		} else {
			throw new Exception("系统查询不到对应的单次入场卷【" + cardno + "】");
		}
	}

	@Override
	public void checkOut(Connection conn, JSONObject obj) throws Exception {
		Entity en = new EntityImpl(conn);
		String sql = "update yp_fit_" + this.getCust_name() + " set state='004' where gym=? and card_no=?";
		en.executeUpdate(sql, new String[] { this.getGym(), this.getCardno() });
		sql = "update yp_checkin_" + getGym() + " set state='004', checkout_time=? where mem_no=? and cust_name=? and gym=? ";
		en.executeUpdate(sql, new Object[] { new Date(), this.getCardno(), this.getCust_name(), this.getGym() });
		try {
			sql = "update yp_box set is_rent='N',state='001' where mem_id=? and cust_name=? and gym=? ";
			en.executeUpdate(sql, new String[] { this.getCardno(), this.getCust_name(), this.getGym() });
		} catch (Exception e) {
		}
		obj.put("flag", "CHECKOUT");
	}

	@Override
	public void autoCheckin(Connection conn, GdUser emp, HttpServletRequest request, JSONObject obj) throws Exception {
		// 14位的卡号表示 散客卷入场
		boolean isNeedKey = Utils.isTrue(request.getParameter("isNeedKey"));// 是否需要手牌
		String key = request.getParameter("key");// 是否传了手牌号
		if (key != null && key.length() > 0) {
			obj.put("key", key);
		}

		Entity en = null;
		List<String> list = new ArrayList<>();

		en = new EntityImpl(conn);
		Date deadline = this.getDeadline();
		if (deadline.before(new Date())) {
			throw new Exception("入场失败-单次入场券:【" + this.getCardno() + "】已经过期");
		}
		if (this.getCheckinState() == CheckinState.场内) {
			// 已经入场，现在出场
			checkOut(conn, obj);
			return;

		} else if (this.getCheckinState() == CheckinState.出场 || this.getCheckinState() == CheckinState.自动离场) {
			throw new Exception("入场失败-单次入场券:【" + this.getCardno() + "】已经使用了");
		}
		if (isNeedKey) {
			if (key != null && key.length() > 0) {
				int szie = en.executeQuery("select * from yp_box where cust_name=? and gym=? and box_no=?", new String[] { getCust_name(), getGym(), key });
				if (szie == 0) {
					// throw new Exception("当前健身房没有柜号为:【" + key +
					// "】的储物柜");
				} else {
					String is_rent = en.getStringValue("is_rent");
					String state = en.getStringValue("state");
					if ("Y".equals(is_rent) || "002".equals(state)) {
						throw new Exception("当前健身房柜号为:【" + key + "】的储物柜已被使用");
					}
				}
			} else {
				obj.put("flag", "NEED_KEY");// 前台输入手牌号
				return;
			}
		}
		Entity yp_checkin = new EntityImpl("yp_checkin", conn);
		yp_checkin.setTablename("yp_checkin_" + getGym());
		yp_checkin.setValue("gym", getGym());
		yp_checkin.setValue("cust_name", getCust_name());
		yp_checkin.setValue("emp_id", emp.getId());
		yp_checkin.setValue("type_code", "005");
		yp_checkin.setValue("mem_no", this.getCardno());
		yp_checkin.setValue("phone", "0000");
		yp_checkin.setValue("user_name", "散客");
		yp_checkin.setValue("checkin_type", "005");
		yp_checkin.setValue("checkin_time", new Date());
		yp_checkin.setValue("box_type", "002");
		if (key != null && key.length() > 0) {
			yp_checkin.setValue("box_no", key);
		} else {
			yp_checkin.setValue("box_no", "-1");
		}
		Flow flow = new Flow();
		yp_checkin.setValue("STATE", "002");
		yp_checkin.setValue("flow_num", flow.getFlownum());
		yp_checkin.setValue("CHECKIN_PRICE", 0);
		yp_checkin.setValue("CHECKOUT_PRICE", 0);
		yp_checkin.setValue("CA_PRICE", 0);
		yp_checkin.setValue("flow_table_name", "yp_flow_" + getGym());
		yp_checkin.create();
		list.add("入场成功-【" + getCardno() + "】入场成功");
		String sql = "update yp_fit_" + getCust_name() + " set state='002' where card_no=?";
		en.executeUpdate(sql, new String[] { getCardno() });
		if (key != null && key.length() > 0) {
			sql = "update yp_box set is_rent='Y',state='002',mem_id=? where box_no=? and gym=? and cust_name=?";
			en.executeUpdate(sql, new String[] { getCardno(), key, getGym(), getCust_name() });
		}
		obj.put("flag", "CHECKIN");
	}

}
