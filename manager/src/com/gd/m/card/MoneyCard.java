package com.gd.m.card;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.gd.m.GdUser;

/**
 * 储值卡
 * 
 * @author terry
 *
 */
public class MoneyCard extends ICard {

	@Override
	public void autoCheckin(Connection conn, GdUser emp, HttpServletRequest request, JSONObject obj) throws Exception {

	}

	@Override
	public void checkOut(Connection conn, JSONObject obj) throws Exception {

	}

}
