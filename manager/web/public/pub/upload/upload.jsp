<%@page import="com.jinhua.server.log.Logger"%>
<%@page import="com.jinhua.server.db.DBUtils"%>
<%@page import="com.jinhua.server.tools.Resources"%>
<%@page import="com.jinhua.server.tools.Utils"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.awt.Image"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.util.UUID"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.apache.commons.fileupload.FileItem"%>
<%@ page import="java.util.List"%>
<%@ page
	import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@ page import="org.apache.commons.fileupload.FileItemFactory"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.io.File"%>
<%@ page import="java.sql.*"%>

<%
	String sId = UUID.randomUUID().toString();
	String Sql = null;
	FileItemFactory factory = null;
	ServletFileUpload upload = null;
	List<FileItem> fileItems = null;
	Iterator<FileItem> iter = null;
	FileItem item = null;
	String ext = "txt";
	File file = null;
	String fileName = "";
	int number=1;

	JSONObject obj = new JSONObject();
	obj.put("result", "Y");
	boolean flag = true;
	try {
		request.setCharacterEncoding("UTF-8");
		factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
		// 得到所有的表单域，它们目前都被当作FileItem
		fileItems = upload.parseRequest(request);
		iter = fileItems.iterator();
		String basePath = Resources.getProperty("FileStore", Utils.getWebRootPath());
		String filename = DBUtils.uuid();
		String filepath = Utils.getFilePath(filename);
		File f = new File(basePath + filepath);
		if (!f.exists()) {
			f.mkdirs();
		}
		while (iter.hasNext()) {
			item = (FileItem) iter.next();
			if (!item.isFormField()) {
				fileName = item.getName();
				String[] strs = fileName.split("\\.");
				ext = strs[strs.length - 1];
				ext = ext.toLowerCase();
				file = new File(basePath + filepath+"/" + filename + "." + ext);
				item.write(file);
			} else {
				String x = item.getFieldName();
				String value = item.getString();
				if ("sId".equals(x)) {
					sId = value;
				}else if("number".equals(x)){
					try{
						number=Integer.parseInt(value);
					}catch(Exception e){}
				}
			}
		}
		Utils.saveFile(file, fileName, "-1",  sId);
	} catch (Exception e) {
		obj.put("errMsg", e);
		obj.put("result", "N");
		Logger.error("上传文件出错:" + e);
	} finally {
		try {
			factory = null;
			upload = null;
			fileItems = null;
			iter = null;
			item = null;
		} catch (Exception e) {
		}
		out.println(obj.toString());
	}
%>
