package com.gd.m;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gd.m.card.UserCardUtils;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.tools.Utils;

/**
 * 客户及可见会所类
 * 
 * @author terry
 *
 */
public class Cust implements Serializable {
	private static final long serialVersionUID = 1L;
	private String cust_name;
	private List<Shop> shops = new ArrayList<>();
	private String userId;
	private UserType type;
	private Shop gym;/* 所属门店 */

	/**
	 * 会员或者员工可见会所
	 * 
	 * @param cust_name
	 * @param userId
	 * @param emplyee
	 * @throws Exception
	 */
	public Cust(String cust_name, String userId, UserType type, Connection conn) throws Exception {
		this.type = type;
		this.cust_name = cust_name;
		this.userId = userId;
		/**
		 * 员工可见会所
		 */
		Entity en = new EntityImpl(conn);

		boolean isEmp = UserCardUtils.isMobileNO(userId);
		if (isEmp) {
			String sql = "select  a.cust_name,a.gym,a.gym_name from yp_emp_gym a ,yp_emp b where a.emp_id=b.id and b.phone=? and b.cust_name=?";
			int size = en.executeQuery(sql, new String[] { userId, cust_name });
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					String cn = en.getStringValue("cust_name", i);
					String g = en.getStringValue("gym", i);
					String gn = en.getStringValue("gym_name", i);

					sql = "select area_code,gym_level,admin_login from yp_gym where gym =?";
					Entity e = new EntityImpl(conn);
					int esize = e.executeQuery(sql, new String[] { g });
					if (esize <= 0) {
						continue;
					}
					String area_code = e.getStringValue("area_code");
					String gym_level = e.getStringValue("gym_level");
					String admin_login = e.getStringValue("admin_login");
					String admin_pwd = e.getStringValue("admin_pwd");
					List<String> gl = Utils.getStringList(gym_level);
					Shop shop = new Shop();
					shop.setCust_name(cn);
					shop.setGym(g);
					shop.setGym_name(gn);
					shop.setArea_code(area_code);
					shop.setGym_level(gl);
					shop.setAdmin_login(admin_login);
					shop.setAdmin_pwd(admin_pwd);
					shops.add(shop);
				}
			}
		} else {
			int size = en.executeQuery("select a.area_code,a.cust_name,a.gym_level,a.admin_login,a.admin_pwd,a.gym,a.gym_name,b.gym gym2 from yp_gym a ,yp_gym b where a.cust_name=b.cust_name and b.admin_login=?", new String[] { userId });
			if (size > 0) {
				this.cust_name = en.getStringValue("cust_name");
				for (int i = 0; i < size; i++) {
					String area_code = en.getStringValue("area_code", i);
					String gym_level = en.getStringValue("gym_level", i);
					String admin_login = en.getStringValue("admin_login", i);
					String admin_pwd = en.getStringValue("admin_pwd", i);
					String cn = en.getStringValue("cust_name", i);
					String gn = en.getStringValue("gym", i);
					String gym_name = en.getStringValue("gym_name", i);
					String gym2 = en.getStringValue("gym2", i);

					List<String> gl = Utils.getStringList(gym_level);
					Shop shop = new Shop();
					shop.setCust_name(cn);
					shop.setGym(gn);
					shop.setGym_name(gym_name);
					shop.setArea_code(area_code);
					shop.setGym_level(gl);
					shop.setAdmin_login(admin_login);
					shop.setAdmin_pwd(admin_pwd);
					if (gym2.equals(cust_name)) {
						// 表示是总店的管理员
						shops.add(shop);
					} else {
						if (gym2.equals(gn)) {
							shops.add(shop);
						}
					}
				}

			}
		}
	}

	/**
	 * 
	 * @param cust_name
	 * @param card_code
	 */
	public Cust(String cust_name) {
		this.cust_name = cust_name;
	}

	/**
	 * 可见会所区域
	 * 
	 * @return
	 */
	public List<String> getAreas() {
		Set<String> as = new HashSet<>();
		for (int i = 0, l = shops.size(); i < l; i++) {
			Shop s = this.shops.get(i);
			as.add(s.getArea_code());
		}

		List<String> temps = new ArrayList<>();
		temps.addAll(as);
		Collections.sort(temps);
		return temps;
	}

	public List<Shop> getShopByArea(String area_code) {
		List<Shop> ps = new ArrayList<>();
		for (int i = 0, l = shops.size(); i < l; i++) {
			Shop s = this.shops.get(i);
			if (area_code.equals(s.getArea_code())) {
				ps.add(s);
			}
		}
		return ps;
	}

	public List<Shop> getShopByLevel(String level) {
		List<Shop> ps = new ArrayList<>();
		for (int i = 0, l = shops.size(); i < l; i++) {
			Shop s = this.shops.get(i);
			if (level.equals(s.getGym_level())) {
				ps.add(s);
			}
		}
		return ps;
	}

	public Shop getFirstShop() throws Exception {
		if (this.shops.size() <= 0) {
			throw new Exception("你没有一家可见的会所的权限");
		}
		return shops.get(0);
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public List<Shop> getShops() throws Exception {
		return shops;
	}

	public void addShop(Shop s) {
		this.shops.add(s);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Shop getGym() {
		return gym;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

}
