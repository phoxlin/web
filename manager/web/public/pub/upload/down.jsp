<%@page import="com.jinhua.SFile"%>
<%@page import="com.jinhua.server.tools.Resources"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="com.jinhua.server.log.Logger"%>
<%@page import="com.jinhua.User"%>
<%@page import="com.jinhua.server.tools.SystemUtils"%>
<%@page import="java.io.File"%>
<%@page import="redis.clients.jedis.Jedis"%>
<%@page import="redis.clients.jedis.Jedis"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.net.URLEncoder"%>
<%@page language="java" contentType="application/force-download"
	pageEncoding="UTF-8"%>
<%
	String fid = request.getParameter("id");
	String hc = request.getParameter("hc");
	String userId = request.getParameter("userid");
	String start = request.getParameter("start");
	User user = (User) SystemUtils.getSessionUser(request, response);

	//添加教育 平台的下载记录

	if (userId == null) {
		if (user != null) {
			userId = user.getId();
		} else {
			userId = "2c90e68e31c74cae0131ccdc07590007";
		}
	}
	long st = 0;
	try {
		st = Integer.parseInt(start);
	} catch (Exception e) {
		//	Logger.error("Invalid start parameter:[" + start + "] for download file id:" + fid);
	}
	Logger.info("Start parameter:[" + st + "] for download file id:" + fid);

	String filedownload = null;
	String filedisplay = "";
	String filedisplay2 = "";
	Logger.info("Downloading file id:" + fid);
	boolean ok = true;
	String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
	//jd.hset(SystemUtils.PROJECT_NAME_BYTE, file.getId().getBytes(), Utils.convertObj2Btyes(file));
	SFile sf = null;
	try {
		if (hc != null && hc.length() > 0) {
			sf = SFile.createSFileByHashCode(hc);
		} else if (fid != null && fid.length() > 0) {
			sf = SFile.createSFileById(fid);
		} else {
			ok = false;
		}
		filedisplay2 = sf.getFileName();
		filedisplay2 = filedisplay2.replace(" ", "");
		filedisplay = new String(filedisplay2.getBytes("GBK"), "ISO8859-1");

		String filepath = Utils.getFilePath(sf.getHashCode());
		filedownload = basePath + filepath + "/" + sf.getHashCode() + "." + sf.getExt();
		String filedownload2 = filedownload;

		String pic = request.getParameter("pic");
		if ("Y".equals(pic)) {
			int width = 60;
			int height = 40;
			try {
				width = Integer.parseInt(request.getParameter("w"));
				height = Integer.parseInt(request.getParameter("h"));
				filedownload2 = basePath + filepath + "/" + sf.getHashCode() + "." + sf.getExt() + "." + width + "_" + height;
			} catch (Exception e) {
			}
			if (!new File(filedownload2).exists()) {
				//PicUtils.compressPic(new File(filedownload), new File(filedownload2), width, height);
			}
			filedownload = filedownload2;
		}

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if (sf != null) {
			ok = true;
		} else {
			if ("-1".equals(fid) || "null".equals(fid) || fid == null || fid.equals("")) {
				ok = false;
			} else {
				ok = false;
			}
		}
	}

	if (ok) {
		response.reset();
		response.setContentType("application/force-download");
		response.addHeader("Content-Disposition", "attachment;filename=" + filedisplay);
		java.io.OutputStream outp = null;
		BufferedInputStream in = null;
		try {
			outp = response.getOutputStream();
			in = new BufferedInputStream(new FileInputStream(filedownload));
			in.skip(st);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) > 0) {
				outp.write(b, 0, i);
			}
			outp.flush();
			Logger.info("Downloaded file id:" + fid + ",filename:" + filedisplay2);
		} catch (Exception e) {
			Logger.error("Download file id:" + fid + ",filename:" + filedisplay2 + ", failed:" + Utils.getErrorStack(e));
		} finally {
			if (in != null) {
				in.close();
				in = null;
			}
		}
	} else {
		Logger.error("Download file id:" + fid + ",filename:" + filedisplay2 + ", failed");
	}
	out.clear();
	out = pageContext.pushBody();
%>
