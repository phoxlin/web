package com.gd.m;

import java.io.Serializable;
import java.util.Date/**/;
import java.util.List;

public class Shop implements Serializable {

	private static final long serialVersionUID = 1L;
	private String cust_name/* 客户 */;
	private String gym/* 会所代码 */;
	private String gym_name/* 会所名称 */;
	private String gym_type/* 会所类型 */;

	private List<String> gym_level/* 通店组别 */;
	private String admin_login/* 管理员登录名 */;
	private String admin_pwd/* 管理员密码 */;
	private String area_code/* 行政区划 */;
	private String addr/* 详细地址 */;

	private String link_man/* 联系人 */;
	private String link_phone/* 联系电话 */;
	private String link_man2/* 联系人2 */;
	private String link_phon2/* 联系电话2 */;
	private Date create_time/* 创建时间 */;
	private Date deadline/* 截止日期 */;
	private Date renew_time/* 上次续费时间 */;
	private String sales_name/* 销售员 */;
	private String state/* 状态 */;
	private String remark/* 备注 */;
	private String logo/**/;
	private String addr_points/* 导航坐标 */;
	private long gym_msg_fee/* 会所短信价钱 */;
	private long yp_msg_fee/* 也跑短信价钱 */;
	private String sign_type/* 签名类型 */;
	private String signature/* 会所签名 */;
	private long msg_count/* 剩余短信数 */;
	private String checkin_type/* 入场方式 */;
	private long card_fee/* 会员卡保底金 */;
	private long checkin_fee/* 当日入场押金 */;
	private Date act_start_time/* 营业时间从 */;
	private Date act_end_time/* 营业时间到 */;

	@Override
	public String toString() {
		return this.gym_name + "(" + this.gym + ")";
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

	public String getGym() {
		return gym;
	}

	public void setGym(String gym) {
		this.gym = gym;
	}

	public String getGym_name() {
		return gym_name;
	}

	public void setGym_name(String gym_name) {
		this.gym_name = gym_name;
	}

	public String getGym_type() {
		return gym_type;
	}

	public void setGym_type(String gym_type) {
		this.gym_type = gym_type;
	}

	public List<String> getGym_level() {
		return gym_level;
	}

	public void setGym_level(List<String> gym_level) {
		this.gym_level = gym_level;
	}

	public String getAdmin_login() {
		return admin_login;
	}

	public void setAdmin_login(String admin_login) {
		this.admin_login = admin_login;
	}

	public String getAdmin_pwd() {
		return admin_pwd;
	}

	public void setAdmin_pwd(String admin_pwd) {
		this.admin_pwd = admin_pwd;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getLink_man() {
		return link_man;
	}

	public void setLink_man(String link_man) {
		this.link_man = link_man;
	}

	public String getLink_phone() {
		return link_phone;
	}

	public void setLink_phone(String link_phone) {
		this.link_phone = link_phone;
	}

	public String getLink_man2() {
		return link_man2;
	}

	public void setLink_man2(String link_man2) {
		this.link_man2 = link_man2;
	}

	public String getLink_phon2() {
		return link_phon2;
	}

	public void setLink_phon2(String link_phon2) {
		this.link_phon2 = link_phon2;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getRenew_time() {
		return renew_time;
	}

	public void setRenew_time(Date renew_time) {
		this.renew_time = renew_time;
	}

	public String getSales_name() {
		return sales_name;
	}

	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getAddr_points() {
		return addr_points;
	}

	public void setAddr_points(String addr_points) {
		this.addr_points = addr_points;
	}

	public long getGym_msg_fee() {
		return gym_msg_fee;
	}

	public void setGym_msg_fee(long gym_msg_fee) {
		this.gym_msg_fee = gym_msg_fee;
	}

	public long getYp_msg_fee() {
		return yp_msg_fee;
	}

	public void setYp_msg_fee(long yp_msg_fee) {
		this.yp_msg_fee = yp_msg_fee;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public long getMsg_count() {
		return msg_count;
	}

	public void setMsg_count(long msg_count) {
		this.msg_count = msg_count;
	}

	public String getCheckin_type() {
		return checkin_type;
	}

	public void setCheckin_type(String checkin_type) {
		this.checkin_type = checkin_type;
	}

	public long getCard_fee() {
		return card_fee;
	}

	public void setCard_fee(long card_fee) {
		this.card_fee = card_fee;
	}

	public long getCheckin_fee() {
		return checkin_fee;
	}

	public void setCheckin_fee(long checkin_fee) {
		this.checkin_fee = checkin_fee;
	}

	public Date getAct_start_time() {
		return act_start_time;
	}

	public void setAct_start_time(Date act_start_time) {
		this.act_start_time = act_start_time;
	}

	public Date getAct_end_time() {
		return act_end_time;
	}

	public void setAct_end_time(Date act_end_time) {
		this.act_end_time = act_end_time;
	}

}
