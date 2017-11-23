package com.gd.m.card;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gd.m.GdUser;
import com.jinhua.server.tools.Utils;

/**
 * 天数卡
 * 
 * @author terry
 *
 */
public class DayCard extends ICard {

	public DayCard() {

	}

	/**
	 * 只用来初始化card的基本信息
	 * 
	 * @param m
	 */
	public DayCard(Map<String, Object> m, Map<String, Set<String>> codeGym, Map<String, Set<String>> levelGym, Map<String, String> gymInfo) {
		this.setCardId(Utils.getMapStringValue(m, "id"));
		this.setCust_name(Utils.getMapStringValue(m, "cust_name"));
		this.setGym(Utils.getMapStringValue(m, "gym"));
		this.setCardType(MemCardType.天数卡);
		this.setType_code(Utils.getMapStringValue(m, "type_code"));
		this.setType_name(Utils.getMapStringValue(m, "type_name"));
		this.setDays(Utils.getMapIntegerValue(m, "days"));
		try {
			this.setState(ICard.getCardState(Utils.getMapStringValue(m, "state")));
		} catch (Exception e) {
			this.setState(MemCardState.激活);
		}
		this.setType_fee(Utils.getMapLongValue(m, "type_fee"));
		this.setRemark(Utils.getMapStringValue(m, "remark"));
		this.setGeneralStoreLevel(Utils.getMapStringValue(m, "general_store_level"));
		this.setConsume_rank(Utils.getMapIntegerValue(m, "consume_rank"));

		try {
			if (this.getGeneralStoreLevel() != null && this.getGeneralStoreLevel().length() > 0) {
				Set<String> temps = levelGym.get(this.getGeneralStoreLevel());
				if (temps != null) {
					getGyms().addAll(temps);
				}
			}
		} catch (Exception e) {
		}
		try {
			Set<String> temps = codeGym.get(this.getType_code());
			if (temps != null) {
				getGyms().addAll(temps);
			}
		} catch (Exception e) {
		}
		setGymInfos(gymInfo);
	}

	@Override
	public void autoCheckin(Connection conn, GdUser emp, HttpServletRequest request, JSONObject obj) throws Exception {
		//
	}

	@Override
	public void checkOut(Connection conn, JSONObject obj) throws Exception {

	}

}
