<%@page import="java.io.FileInputStream"%>
<%@page import="java.awt.image.BufferedImage"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.awt.Image"%>
<%@page import="com.tools.BUtils"%>
<%@page import="com.jinhua.server.db.Entity"%>
<%@page import="com.jinhua.server.db.impl.EntityImpl"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.jinhua.server.db.impl.DBM"%>
<%@page import="com.jinhua.server.db.IDB"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.jinhua.server.log.Logger"%>
<%@page import="org.dom4j.Element"%>
<%@page import="java.util.List"%>
<%@page import="org.dom4j.Document"%>
<%@page import="java.io.File"%>
<%@page import="org.dom4j.io.SAXReader"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.HtmlServlet"%>
<%@page import="com.jinhua.server.tools.Resources"%>
<%
	File file = new File(Utils.getRootClassPath() + "/id.xml");
	SAXReader read = new SAXReader();
	Document doc = read.read(file);
	List<Element> items = doc.selectNodes("/root/item");
	Logger.info("共:" + items.size() + "条数据");
	out.print("共:" + items.size() + "条数据");
	IDB db = new DBM();
	Connection conn = null;
	try {
		conn = db.getConnection();
		conn.setAutoCommit(false);
		Entity en = new EntityImpl(conn);
		for (int i = 0; i < items.size(); i++) {
			Element el = items.get(i);
			String id = el.attributeValue("id").trim();
			String pic = el.attributeValue("pic").trim();
			pic = pic.replace("\\", "/");
			int size = en.executeQuery("select * from yp_mem_lr a where a.id=?", new String[] { id });
			if (size > 0) {
				String pic2 = en.getStringValue("pic2");
		//		if (pic2 != null && pic2.length() > 4) {
		//			Logger.info("id:" + id + ",有照片:" + pic2);
		//			out.print("id:" + id + ",有照片:" + pic2);
		//		} else {
					try {
						BufferedImage image = ImageIO.read(new FileInputStream(pic));
						String path = BUtils.cvSaveImage(image, id);
						Logger.info("用户 id:" + id + "上传头像成功,七牛图片地址:" + path);
						out.print("用户 id:" + id + "上传头像成功,七牛图片地址:" + path);
						en.executeUpdate("update yp_mem_lr a set a.pic2=? where a.id=?", new String[] { path, id });
					} catch (Exception ee) {
						Logger.error(ee);
					}
			//	}
			}
			if (i % 100 == 0) {
				conn.commit();
				Logger.info("数据确认一下");
				out.print("数据确认一下");
			}
			Logger.info("完成" + i + "/" + items.size());
		}
		conn.commit();
		out.print("完成工作");
	} catch (Exception e) {
		Logger.error(e);
		out.print(Utils.getErrorStack(e));
	} finally {
		db.freeConnection(conn);
	}
%>