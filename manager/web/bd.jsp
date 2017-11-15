<%@page import="java.util.Calendar"%>
<%@page import="javafx.scene.control.Alert"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.jinhua.server.log.Logger"%>
<%@page import="org.dom4j.Element"%>
<%@page import="java.util.List"%>
<%@page import="org.dom4j.Document"%>
<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="java.io.File"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.HtmlServlet"%>
<%@page import="com.jinhua.server.tools.Resources"%>
<%
	File file = new File(Utils.getRootClassPath() + "bd.xml");
	String aa = "";
	String bb = "";
	SAXReader reader = new SAXReader();
	Document doc = reader.read(file);
	List<Element> items = doc.selectNodes("/root/item");
	IDB db = new DBM();
	Connection conn = null;
	try {
		conn = db.getConnection();
		conn.setAutoCommit(false);

		for (int i = 0, l = items.size(); i < l; i++) {
			Element el = items.get(i);

			String name = el.attributeValue("name");
			String phone = el.attributeValue("phone");
			String card = el.attributeValue("card");
			String start = el.attributeValue("start");
			String daystr = el.attributeValue("day");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String userId = "";
			String type_codes = "";
			Date startTime = null;
			int day = Integer.parseInt(daystr);

			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(start));
			cal.add(Calendar.DAY_OF_YEAR, day);
			Date zz = cal.getTime();

			Entity yp_mem_baodi = new EntityImpl(conn);
			int size = yp_mem_baodi.executeQuery(
					"select id from yp_mem_baodi where phone = ? and gym = 'baodi001' ",
					new String[] { phone });
			if (size <= 0) {
				Logger.warn("line:" + i + ",phone:" + phone + ",查询不到会员信息");
				continue;
			}
			userId = yp_mem_baodi.getStringValue("id");

			Entity ss = new EntityImpl(conn);
			size = ss.executeQuery("select type_code from yp_type where type_name = ? and gym = 'baodi001' ",
					new String[] { card });
			if (size <= 0) {
				Logger.warn("line:" + i + ",type_name:" + card + ",查询不到会员卡信息");
				continue;
			}
			type_codes = ss.getStringValue("type_code");

			Entity dd = new EntityImpl(conn);
			size = dd.executeQuery("SELECT * FROM yp_buy_card_record_baodi001 where mem_id = ? ",
					new String[] { userId });
			if (size <= 0) {
				Logger.warn("line:" + i + ",mem_id:" + userId + ",查询不到购卡信息");
				continue;
			}

			Entity xx = new EntityImpl(conn);
			size = xx.executeQuery("select * from yp_type_user_baodi001 where mem_id = ? and gym = 'baodi001' ",
					new String[] { userId });
			if (size <= 0) {
				Logger.warn("line:" + i + ",mem_id:" + userId + ",查询不到会员信息");
				continue;
			}

			Entity cc = new EntityImpl("yp_type_user", conn);
			cc.setTablename("yp_type_user_baodi001");
// 			cc.executeUpdate("update yp_type_user_baodi001 set type_code='" + type_codes + "' , act_time='"
// 					+ start + "', deadline='" + sdf.format(zz) + "' where mem_id = '" + userId + "'");
		}

	} catch (Exception e) {
		Logger.error(e);
	} finally {
		db.freeConnection(conn);
	}
%>
