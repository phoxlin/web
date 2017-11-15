<%@page import="com.gd.utils.HtmlUtils"%>
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
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	IDB db = new DBM();
	Connection conn = null;
	try {
		conn = db.getConnection();
		conn.setAutoCommit(false);
		Entity en = new EntityImpl(conn);
		Entity en2 = new EntityImpl(conn);

		int size = en.executeQuery(
				"select id,pic2 from yp_mem_lr where pic2 like 'http://oixty02vf.bkt.clouddn.com%'");

		if (size > 0) {
			for (int i = 0; i < size; i++) {
				String id = en.getStringValue("id", i);
				String pic2 = en.getStringValue("pic2", i);
				File sf = new File("d:/temps/" + System.currentTimeMillis() + ".jpg");
				HtmlUtils.download(pic2, sf);
				if (sf.exists()) {
					try {
						BufferedImage image = ImageIO.read(new FileInputStream(sf));
						String path = BUtils.cvSaveImage(image, id);
						Logger.info("用户 id:" + id + "上传头像成功,七牛图片地址:" + path);
						out.print("用户 id:" + id + "上传头像成功,七牛图片地址:" + path);
						en2.executeUpdate("update yp_mem_lr a set a.pic2=? where a.id=?",
								new String[] { path, id });
						sf.delete();
					} catch (Exception ee) {
						Logger.error(ee);
					}
					if (i % 100 == 0) {
						conn.commit();
						Logger.info("数据确认一下");
						out.print("数据确认一下");
					}
					Logger.info("完成" + i + "/" + size);
				}else{
					Logger.info("下载失败："+pic2);
				}
				
			}

			conn.commit();
			out.print("完成工作");

		}

	} catch (Exception e) {
		Logger.error(e);
		out.print(Utils.getErrorStack(e));
	} finally {
		db.freeConnection(conn);
	}
%>