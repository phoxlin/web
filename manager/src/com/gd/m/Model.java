package com.gd.m;

import java.io.Serializable;
import java.util.Map;

public class Model implements Serializable{
	private static final long serialVersionUID = 1L;
	private String code;
	private String name;
	private String icon;
	private String mobilePage;
	private String padPage;
	private String bgPage;

	public Model() {
		super();
	}
	
	public Model(Map<String,Object> model) {
		this.code = model.get("code").toString();
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMobilePage() {
		return mobilePage;
	}

	public void setMobilePage(String mobilePage) {
		this.mobilePage = mobilePage;
	}

	public String getPadPage() {
		return padPage;
	}

	public void setPadPage(String padPage) {
		this.padPage = padPage;
	}

	public String getBgPage() {
		return bgPage;
	}

	public void setBgPage(String bgPage) {
		this.bgPage = bgPage;
	}

}
