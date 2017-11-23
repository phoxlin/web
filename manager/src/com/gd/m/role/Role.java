package com.gd.m.role;

public class Role {
	private String cust_name;
	private String id;
	private String code;
	private String roleName;
	private boolean ex;
	private boolean mc;
	private boolean pt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isEx() {
		return ex;
	}

	public void setEx(boolean ex) {
		this.ex = ex;
	}

	public boolean isMc() {
		return mc;
	}

	public void setMc(boolean mc) {
		this.mc = mc;
	}

	public boolean isPt() {
		return pt;
	}

	public void setPt(boolean pt) {
		this.pt = pt;
	}

	public String getCust_name() {
		return cust_name;
	}

	public void setCust_name(String cust_name) {
		this.cust_name = cust_name;
	}

}
