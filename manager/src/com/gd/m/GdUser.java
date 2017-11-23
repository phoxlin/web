package com.gd.m;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jinhua.User;
import com.jinhua.UserImpl;
import com.jinhua.server.Action;
import com.jinhua.server.c.CItem;
import com.jinhua.server.c.Codes;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.log.Logger;
import com.jinhua.server.tools.Resources;
import com.jinhua.server.tools.SystemUtils;
import com.jinhua.server.tools.Utils;

public class GdUser extends User {
	private static final long serialVersionUID = 1L;
	private boolean isAdmin = false;
	private boolean isCust = false;
	private String id;
	private String gym;// 所属门店
	private String cust_name;// 客户名称
	private String userName;// 用户名称
	private String mem_no;// 会员卡号
	private String phone;
	private String pwd;

	private String pid;// 主卡id

	private boolean hasBox;// 是否有租赁柜子
	private String boxNo;// 柜号多少
	private Date startBox;// 开始租柜时间
	private Date endBox;// 结束租柜时间

	private String wxOpenId;
	private String wxOpenIdbyApp;
	private String appId;
	private String memId;
	private String memName;
	private String sales_id;
	private String sales_name;
	private String sales_phone;

	private List<String> roles = new ArrayList<>();// 用户拥有的所有的角色代码
	private List<String> roleNames = new ArrayList<>();// 用户拥有的所有的角色名称 和角色代码一一对应
	private Set<String> cds = new HashSet<>();// 用户拥有的所有的模块代码
	private Map<String, Model> cdInfo = new HashMap<>();// 每个模块代码对应得详细的模块信息
	private JSONObject aliPayParam = new JSONObject();
	private JSONObject wechatParam = new JSONObject();

	private List<String> gymList = new ArrayList<>();// 员工可见门店，会员默认一般都是所有门店都可以
	private List<String> gymNameList = new ArrayList<>();// 员工可见门店名称，会员默认一般都是所有门店都可以
	private Cust cust;
	private String wx_open_id_app;
	private Set<UserType> types = new HashSet<>();

	public String getMemId() throws Exception {
		if (memId != null && memId.length() > 0) {
			return this.memId;
		} else {
			return this.getId();
		}
	}

	public JSONObject toJson() {
		JSONObject o = new JSONObject(this.getM());
		if (this.getM().keySet().size() > 0) {
			o.remove("pwd");
			long amt = o.getLong("amt");
			o.put("amtStr", Utils.toPrice(amt));
			long gift_amt = o.getLong("gift_amt");
			o.put("gift_amtStr", Utils.toPrice(gift_amt));

			String state = o.getString("state");
			String stateStr = Codes.note("user_state", state);
			o.put("stateStr", stateStr);
			if (!o.keySet().contains("checkin_state")) {
				o.put("checkin_state_str", "未入场");
				o.put("checkin_state", "001");
			} else {
				String checkin_state = o.getString("checkin_state");
				o.put("checkin_state_str", Codes.note("checkin_state", checkin_state));
			}
			String birDay = Utils.getMapFormatStringValue(this.getM(), "birthday", "yyyy.MM.dd");
			o.put("birthday", birDay);
		}

		return o;
	}

	/**
	 * 微信使用
	 * 
	 * @param cust_name
	 * @param employee
	 * @param wxOpenId
	 * @param conn
	 * @throws Exception
	 */
	public GdUser(String cust_name, boolean employee, String wxOpenId, Connection conn) throws Exception {
		this.cust_name = cust_name;
		this.wxOpenId = wxOpenId;
		if (this.cust_name == null || this.cust_name.length() <= 0) {
			throw new Exception("请设置客户属性");
		}
		init(conn);
	}

	/**
	 * APP使用
	 * 
	 * @param cust_name
	 * @param employee
	 * @param wxOpenId
	 * @param conn
	 * @throws Exception
	 */
	public GdUser(String cust_name, boolean employee, Connection conn, String wxOpenId) throws Exception {
		this.cust_name = cust_name;
		this.wxOpenIdbyApp = wxOpenId;
		if (this.cust_name == null || this.cust_name.length() <= 0) {
			throw new Exception("请设置客户属性");
		}
		init(conn);
	}

	public GdUser(String cust_name, String id, boolean employee, Connection conn) throws Exception {
		this.cust_name = cust_name;
		this.id = id;
		if (this.cust_name == null || this.cust_name.length() <= 0) {
			throw new Exception("请设置客户属性");
		}

		Cust cust = new Cust(cust_name);
		if (employee) {
			this.addUserType(UserType.员工);
		}
		this.setCust(cust);
		init(conn);
	}

	public GdUser(String cust_name, String phone, Connection conn) throws Exception {
		this.cust_name = cust_name;
		this.phone = phone;
		if (this.cust_name == null || this.cust_name.length() <= 0) {
			throw new Exception("请设置客户属性");
		}
		init(conn);
	}

	public static GdUser getUserByPhone(String cust_name, String phone, boolean employee, Connection conn)
			throws Exception {
		GdUser user = new GdUser();
		user.cust_name = cust_name;
		user.phone = phone;
		if (cust_name == null || cust_name.length() <= 0) {
			throw new Exception("请设置客户属性");
		}
		user.init(conn);
		return user;
	}

	public GdUser() {

	}

	public void init(Connection conn) throws Exception {
		// if (this.cust_name == null || this.cust_name.length() <= 0) {
		// throw new Exception("请设置客户属性");
		// }
		if (cust == null) {
			cust = new Cust(cust_name);
		}
		cust_name = cust.getCust_name();
		if (this.is员工()) {
			// 员工登录验证
			// 员工表[YP_EMP]没有分表直接查询即可
			Entity en = new EntityImpl(conn);
			int size = 0;
			if (this.id != null && this.id.length() == 24) {
				size = en.executeQueryWithOutMaxResult("select * from yp_emp a where a.id=?", new String[] { this.id },
						1, 1);
			}
			if (this.wxOpenId != null && this.wxOpenId.length() > 0 && !("null").equals(wxOpenId)) {
				size = en.executeQueryWithOutMaxResult("select * from yp_emp a where a.wx_open_id=?",
						new String[] { this.wxOpenId }, 1, 1);
			}
			if (phone != null && phone.length() > 0 && pwd != null && pwd.length() > 0) {
				size = en.executeQueryWithOutMaxResult(
						"select * from yp_emp a where a.phone=? and a.pwd=? and a.cust_name=? and a.state in ('001','002','003')",
						new String[] { phone, Utils.getMd5(pwd), this.cust_name }, 1, 1);
			}
			if (size > 0) {
				// 判断状态是否正确
				this.id = en.getStringValue("id");

				this.gym = en.getStringValue("gym");
				this.getM().putAll(en.getValues().get(0));
				// 查询角色信息
				size = en.executeQuery(
						"select b.code,b.role_name note ,b.EX,b.MC,b.PT,b.OP from yp_emp_role a,sys_role b where a.role = b.id and b.cust_name=a.cust_name and a.emp_id=? and a.cust_name=?",
						new String[] { id, this.cust_name });
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						String role = en.getStringValue("code", i);
						if (role != null && role.length() > 0) {
							String[] rs = role.split(",");
							for (String r : rs) {
								if (r != null && r.length() > 0) {
									if (!roles.contains(role)) {
										roles.add(role);
										roleNames.add(role);
									}
								}
							}
						}
						if (en.getBooleanValue("ex")) {
							this.addUserType(UserType.主管);
						}
						if (en.getBooleanValue("MC")) {
							this.addUserType(UserType.会稽);
						}
						if (en.getBooleanValue("pt")) {
							this.addUserType(UserType.教练);
						}
						if (en.getBooleanValue("op")) {
							this.addUserType(UserType.运营);
						}
					}
				}
				// 查询可见门店
				size = en.executeQuery("select a.GYM,a.GYM_NAME from yp_emp_gym a where a.cust_name=? and a.emp_id=?",
						new String[] { this.cust_name, id });
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						String gym = en.getStringValue("gym", i);
						String gym_name = en.getStringValue("gym_name", i);
						if (!gymList.contains(gym)) {
							gymList.add(gym);
							gymNameList.add(gym_name);
						}
					}
				}

				String sql = "SELECT DISTINCT\n" + "	c.model_code CODE,\n" + "	c.MODEL_NAME NAME\n" + "FROM\n"
						+ "	yp_emp a,\n" + "	yp_emp_role b,\n" + "	yp_role_model c\n" + "WHERE\n"
						+ "	a.id = b.emp_id\n" + "AND b.role = c. CODE\n" + "AND a.CUST_NAME =?\n"
						+ "AND a.id =?  and c.CUST_NAME=?";
				// ""select d.model_code,d.model_name from yp_emp_role
				// a,sys_role b,yp_emp c,yp_role_model d where a.role=b.id and
				// a.EMP_ID=c.id and d.code=b.id and c.id=?", new String[] {}"
				Entity models = new EntityImpl(conn);
				size = models.executeQuery(sql, new String[] { this.cust_name, this.id, this.cust_name });
				for (int i = 0; i < size; i++) {
					String code = models.getStringValue("code", i);
					Model model = new Model(models.getValues().get(i));
					cdInfo.put(code.toLowerCase(), model);
					cdInfo.put(code, model);
				}
				cds = new HashSet<>(cdInfo.keySet());

				/**
				 * 查询当前用户的支付信息 如果当前健身房有支付信息 就使用自己的 如果没有就使用 上级店铺的
				 * 
				 */
				en = new EntityImpl("yp_param", conn);
				size = en.executeQuery(
						"select id,note from yp_param where code='Alipay' and cust_name = '" + this.cust_name + "'");
				if (size > 0) {
					String note = en.getStringValue("note");
					JSONArray arr = new JSONArray(note);
					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = (JSONObject) arr.get(i);
						if (this.gym.equals(obj.get("gym"))) {
							this.setAliPayParam(obj);
						} else if (this.cust_name.equals(obj.get("gym"))) {
							this.setAliPayParam(obj);
						}
					}
				}
				/**
				 * 微信支付信息
				 * 
				 */
				en = new EntityImpl("yp_param", conn);
				size = en.executeQuery(
						"select id,note from yp_param where code='wechat' and cust_name = '" + this.cust_name + "'");
				if (size > 0) {
					String note = en.getStringValue("note");
					JSONObject obj = new JSONObject(note);
					this.setWechatParam(obj);
				}
				// 查询角色 、、yp_emp_role
				en = new EntityImpl(conn);
				sql = "select b.* from yp_emp_role a,sys_role b where a.role = b.id and  a.emp_id=?";
				size = en.executeQuery(sql, new String[] { this.id });
				if (size > 0) {
					List<Map<String, Object>> r = en.getValues();
					for (Map<String, Object> m : r) {
						String MC = m.get("mc") + "";
						String PT = m.get("pt") + "";
						String EX = m.get("ex") + "";
						String OP = m.get("op") + "";
						if ("Y".equals(MC)) {
							types.add(UserType.会稽);
							types.add(UserType.员工);
						}
						if ("Y".equals(PT)) {
							types.add(UserType.教练);
							types.add(UserType.员工);
						}
						if ("Y".equals(EX)) {
							types.add(UserType.主管);
						}
						if ("Y".equals(OP)) {
							types.add(UserType.运营);
						}
					}
				}
			} else {
				if (this.id != null && this.id.length() == 24) {
					throw new Exception("系统没有查询到相关员工[" + id + "]");
				} else {
					throw new Exception("用户名或者密码错误");
				}
			}
		} else {
			this.addUserType(UserType.会员);
			// 会员登录验证
			// 会员表[yp_mem]按照cust_name进行分表
			Entity en = new EntityImpl(conn);
			int size = 0;
			if (this.id != null && this.id.length() == 24) {
				size = en.executeQueryWithOutMaxResult("select * from yp_mem_" + cust_name + " a where a.id=?",
						new String[] { id }, 1, 1);
			} else if (this.wxOpenId != null && this.wxOpenId.length() > 4) {
				size = en.executeQueryWithOutMaxResult("select * from yp_mem_" + cust_name + " a where a.wx_open_id=?",
						new String[] { this.wxOpenId }, 1, 1);
			} else if (this.wxOpenIdbyApp != null && this.wxOpenIdbyApp.length() > 4) {
				size = en.executeQueryWithOutMaxResult(
						"select * from yp_mem_" + cust_name + " a where a.wx_open_id_app=?",
						new String[] { this.wxOpenIdbyApp }, 1, 1);
			} else if (phone != null && phone.length() > 0 && pwd != null && pwd.length() > 0) {
				size = en.executeQueryWithOutMaxResult(
						"select * from yp_mem_" + cust_name + " a where a.phone=? and a.pwd=?",
						new String[] { phone, Utils.getMd5(pwd) }, 1, 1);
			} else if (phone != null && phone.length() > 0) {
				size = en.executeQueryWithOutMaxResult("select * from yp_mem_" + cust_name + " a where a.phone=? ",
						new String[] { phone }, 1, 1);
			} else if (this.mem_no != null && this.mem_no.length() > 0) {
				size = en.executeQueryWithOutMaxResult("select * from yp_mem_" + cust_name + " a where a.mem_no=? ",
						new String[] { mem_no }, 1, 1);
			} else if ("-1".equals(this.id)) {
				// 散客
				this.userName = "散客";
				this.getM().put("amt", 0l);
				// this.getM().put("", value)
			}
			if (phone != null && phone.length() > 0 && pwd != null && pwd.length() > 0) {
				size = en.executeQueryWithOutMaxResult(
						"select * from yp_mem_" + cust_name + " a where a.phone=? and a.pwd=?",
						new String[] { phone, Utils.getMd5(pwd) }, 1, 1);
			}
			long userDeadline = 0;
			if (size > 0) {
				// 判断状态是否正确
				this.gym = en.getStringValue("gym");
				this.id = en.getStringValue("id");
				Map<String, Object> userInfo = en.getValues().get(0);
				if (userInfo.containsKey("first_start_time")) {
					String first_start_time = en.getFormatStringValue("first_start_time", "yyyy-MM-dd");
					userInfo.put("first_start_time", first_start_time);
				}
				this.getM().putAll(userInfo);
				// 查询有没有会籍
				String sales_id = en.getStringValue("sales_id");
				if (sales_id != null && sales_id.length() > 0) {
					Entity emp = new EntityImpl(conn);
					size = emp.executeQueryWithOutMaxResult("select id,cust_name,emp_name,phone from yp_emp where id=?",
							new String[] { sales_id }, 1, 1);
					if (size > 0) {
						this.getM().put("sales_name", emp.getStringValue("emp_name"));
						this.sales_id = emp.getStringValue("id");
						this.sales_name = emp.getStringValue("cust_name");
						this.sales_phone = emp.getStringValue("phone");
					}
				}

				// 判断是否租柜了
				boolean has_box = en.getBooleanValue("HAS_BOX");
				if (has_box) {
					Date box_end_time = en.getDateValue("BOX_END_TIME");
					if (box_end_time.after(new Date())) {
						this.hasBox = true;
						this.endBox = box_end_time;
						this.startBox = en.getDateValue("BOX_START_TIME");
						this.boxNo = en.getStringValue("BOX_NUM");
					}
					this.getM().put("box_end_time", box_end_time);
				}

				// 查询会员可见会所
				String ugymSql = "select a.gym, a.gym_name from yp_gym a, yp_type_gym b"
						+ " where a.gym=b.gym and b.type_code in " + "(select type_code from yp_type_user_" + gym
						+ " where mem_id=? )";

				Entity userGyms = new EntityImpl(conn);
				int gsize = userGyms.executeQuery(ugymSql, new String[] { id });
				gymList.add(gym);
				//gymList.add(AppUtils.getGymName(gym));
				for (int i = 0; i < gsize; i++) {
					String g = userGyms.getStringValue("gym", i);
					String n = userGyms.getStringValue("gym_name", i);
					this.gymList.add(g);
					this.gymNameList.add(n);
				}

			} else {
				if (this.id != null && this.id.length() == 24) {
					throw new Exception("系统没有查询到相关员工[" + id + "]");
				} else if ("-1".equals(this.id)) {
					// 散客
				} else {
					throw new Exception("用户名或者密码错误");
				}
			}

			/**
			 * 查询当前用户的支付信息 如果当前健身房有支付信息 就使用自己的 如果没有就使用 上级店铺的
			 * 
			 */
			en = new EntityImpl("yp_param", conn);
			size = en.executeQuery(
					"select id,note from yp_param where code='Alipay' and cust_name = '" + this.cust_name + "'");
			if (size > 0) {
				String note = en.getStringValue("note");
				JSONArray arr = new JSONArray(note);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = (JSONObject) arr.get(i);
					if (this.gym.equals(obj.get("gym"))) {
						this.setAliPayParam(obj);
					} else if (this.cust_name.equals(obj.get("gym"))) {
						this.setAliPayParam(obj);
					}
				}
			}
			/**
			 * 微信支付信息
			 * 
			 */
			en = new EntityImpl("yp_param", conn);
			size = en.executeQueryWithOutMaxResult("select id,note from yp_param where code=? and cust_name = ?",
					new String[] { "wechat", this.cust_name }, 1, 1);
			if (size > 0) {
				String note = en.getStringValue("note");
				JSONObject obj = new JSONObject(note);
				this.setWechatParam(obj);
			}

			if (userDeadline <= 0) {
				this.getM().put("deadline", "0000-00-00");
			} else {
				this.getM().put("deadline", Utils.parseData(new Date(userDeadline), "yyyy-MM-dd"));
			}
			// this.getM().put("sales_name", Utils.getListString(sales));

		}

	}

	public String create(Map<String, Object> map, Action act) throws Exception {
		if (this.cust_name == null || this.cust_name.length() <= 0 || this.gym == null || this.gym.length() <= 0) {
			// throw new Exception("请设置客户或者所属门店属性");
			// 没有门店点的暂时 放到 YP_MEM表里面
			map.put("gym", "N/A");
		}
		if (is员工()) {// 员工插入信息
			Entity yp_emp = new EntityImpl("yp_emp", act);
			for (Map.Entry<String, Object> en : map.entrySet()) {
				yp_emp.setValue(en.getKey(), en.getValue());
			}
			yp_emp.setValue("gym", gym);
			yp_emp.setValue("cust_name", cust_name);
			return yp_emp.create();
		} else {// 会员插入信息
			// 会员表[yp_mem]按照cust_name进行分表
			Entity yp_mem = new EntityImpl("yp_mem", act);
			if (this.cust_name != null && !this.cust_name.equals("null") && this.cust_name.length() > 0) {
				yp_mem.setTablename("yp_mem_" + this.cust_name);// 分表设置，实际存储的是当前设置的表名
				yp_mem.setValue("gym", gym);
			} else {
				yp_mem.setValue("gym", "N/A");
			}
			for (Map.Entry<String, Object> en : map.entrySet()) {
				yp_mem.setValue(en.getKey(), en.getValue());
			}
			yp_mem.setValue("is_print", "N");
			this.id = yp_mem.create();
			return this.id;
		}
	}

	public void validiteApp(String wxOpenIdbyApp, Connection conn, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.wxOpenIdbyApp = wxOpenIdbyApp;
		init(conn);
		String state = this.getXX("state");
		if (this.is员工()) {
			Entity en = new EntityImpl(conn);
			try {
				en.executeUpdate("update yp_emp set login_time=? where wx_open_id_app=? and cust_name=?",
						new Object[] { new Date(), this.wxOpenId, this.cust_name });
			} catch (Exception e) {
				Logger.error(e);
			}
			if ("001".equals(state)) {
				throw new Exception("您的账号正在审核中,无法登录!");
			} else if ("003".equals(state)) {
				throw new Exception("您已处于离职状态,无法登录系统!");
			}
		}
		SystemUtils.setSessionUser(this, request, response);
	}

	public void validite(String wx_open_id, Connection conn, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		this.wxOpenId = wx_open_id;
		init(conn);
		String state = this.getXX("state");
		if (this.is员工()) {
			Entity en = new EntityImpl(conn);
			try {
				en.executeUpdate("update yp_emp set login_time=? where wx_open_id=? and cust_name=?",
						new Object[] { new Date(), this.wxOpenId, this.cust_name });
			} catch (Exception e) {
				Logger.error(e);
			}
			if ("001".equals(state)) {
				throw new Exception("您的账号正在审核中,无法登录!");
			} else if ("003".equals(state)) {
				throw new Exception("您已处于离职状态,无法登录系统!");
			}
		}
		SystemUtils.setSessionUser(this, request, response);
	}

	@Override
	public void validite(String name, String pwd, Action act) throws Exception {
		this.phone = name;
		this.pwd = pwd;

		init(act.getConnection());
		String state = this.getXX("state");
		if (this.is员工()) {
			Entity en = new EntityImpl(act);
			try {
				en.executeUpdate("update yp_emp set login_time=? where phone=? and cust_name=?",
						new Object[] { new Date(), this.phone, this.cust_name });
			} catch (Exception e) {
				Logger.error(e);
			}
			if ("001".equals(state)) {
				throw new Exception("您的账号正在审核中,无法登录!");
			} else if ("003".equals(state)) {
				throw new Exception("您已处于离职状态,无法登录系统!");
			}
		}
		SystemUtils.setSessionUser(this, act.request, act.response);
	}

	@Override
	public List<CItem> getUserListBySet(String key, String defaultVal, Connection conn) throws Exception {
		String userImpl = Resources.getProperty("UserImpl");
		UserImpl impl = null;
		try {
			impl = (UserImpl) Class.forName(userImpl).newInstance();
			return impl.getUserListBySet(key, defaultVal, conn);
		} catch (Exception e) {
			throw new Exception("系统没有配置相关的用户管理类：【UserImpl】");
		}
	}

	public long getRemainAmt() {
		if (is会员()) {
			// 会员才需要有余额
			try {
				String amt = this.getXX("amt");
				return Long.parseLong(amt);
			} catch (Exception e) {
				return 0l;
			}
		} else {
			return 0l;
		}

	}

	public String getGym() {
		return gym;
	}

	public void setGym(String gym) {
		this.gym = gym;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Set<String> getCds() {
		return cds;
	}

	public void setCds(Set<String> cds) {
		this.cds = cds;
	}

	public Map<String, Model> getCdInfo() {
		return cdInfo;
	}

	public void setCdInfo(Map<String, Model> cdInfo) {
		this.cdInfo = cdInfo;
	}

	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public List<String> getGymList() {
		return gymList;
	}

	public List<String> getGymNameList() {
		return gymNameList;
	}

	public String getId() throws Exception {
		if (this.id == null) {
			this.id = super.getId();
		}
		return id;
	}

	public String getUserName() throws Exception {
		if (this.userName == null) {
			if (this.getM().containsKey("user_name")) {
				this.userName = this.getXX("user_name");
			} else if (this.getM().containsKey("emp_name")) {
				this.userName = this.getXX("emp_name");
			} else if (this.memName != null && this.memName.length() > 0) {
				this.userName = this.memName;
			}
		}
		return userName;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getMem_no() {
		if (mem_no == null || mem_no.length() <= 0) {
			mem_no = "-";
		}
		return mem_no;
	}

	public void setMem_no(String mem_no) {
		this.mem_no = mem_no;
	}

	public JSONObject getAliPayParam() {
		return aliPayParam;
	}

	public void setAliPayParam(JSONObject aliPayParam) {
		this.aliPayParam = aliPayParam;
	}

	public JSONObject getWechatParam() {
		return wechatParam;
	}

	public void setWechatParam(JSONObject wechatParam) {
		this.wechatParam = wechatParam;
	}

	public void setGymLists(List<String> ids, List<String> names) throws Exception {
		if (ids.size() != names.size()) {
			throw new Exception("数量不对应!");
		} else {
			this.gymList = ids;
			this.gymNameList = names;
		}
	}

	public boolean isCust() {
		return isCust;
	}

	public void setCust(boolean isCust) {
		this.isCust = isCust;
	}

	public String getPhone() {
		if (phone == null) {
			try {
				phone = this.getXX("phone");
			} catch (Exception e) {
			}
		}
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getWxOpenId() throws Exception {
		return this.getXX("wx_open_id");
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean isHasBox() {
		return hasBox;
	}

	public String getBoxNo() {
		return boxNo;
	}

	public Date getStartBox() {
		return startBox;
	}

	public Date getEndBox() {
		return endBox;
	}

	public boolean is会稽() {
		return this.types.contains(UserType.会稽);
	}

	public boolean is教练() {
		return this.types.contains(UserType.教练);
	}

	public boolean is主管() {
		return this.types.contains(UserType.主管);
	}

	public boolean is运营() {
		return this.types.contains(UserType.运营);
	}

	public boolean is会稽经理() {
		return is主管() && is会稽();
	}

	public boolean is教练经理() {
		return is主管() && is教练();
	}

	public boolean is管理员() {
		return this.types.contains(UserType.管理员);
	}

	public boolean is会员() {
		return this.types.contains(UserType.会员);
	}

	public boolean is员工() {
		return this.types.contains(UserType.员工) || this.types.contains(UserType.主管) || this.types.contains(UserType.会稽)
				|| this.types.contains(UserType.教练) || this.types.contains(UserType.运营);
	}

	public Cust getCust() {
		return cust;
	}

	public void setCust(Cust cust) {
		this.cust = cust;
	}

	public void addUserType(UserType type) {
		this.types.add(type);
	}

	@Override
	public List<String> getCD() {
		List<String> li = new ArrayList<>();
		li.addAll(this.getCds());

		return li;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean singleShop() {
		try {
			return !(this.getCust().getShops().size() > 1);
		} catch (Exception e) {
			return true;
		}
	}

	public void setMemId(String memId) {
		this.memId = memId;
	}

	public String getWxOpenIdbyApp() {
		return wxOpenIdbyApp;
	}

	public void setWxOpenIdbyApp(String wxOpenIdbyApp) {
		this.wxOpenIdbyApp = wxOpenIdbyApp;
	}

	public void setWxOpenId(String wxOpenId) {
		this.wxOpenId = wxOpenId;
	}

	public String getWx_open_id_app() {
		return wx_open_id_app;
	}

	public void setWx_open_id_app(String wx_open_id_app) {
		this.wx_open_id_app = wx_open_id_app;
	}

	public String getMemName() {
		return memName;
	}

	public void setMemName(String memName) {
		this.memName = memName;
	}

	public String getSales_id() {
		return sales_id;
	}

	public void setSales_id(String sales_id) {
		this.sales_id = sales_id;
	}

	public String getSales_name() {
		return sales_name;
	}

	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}

	public String getSales_phone() {
		return sales_phone;
	}

	public void setSales_phone(String sales_phone) {
		this.sales_phone = sales_phone;
	}

}
