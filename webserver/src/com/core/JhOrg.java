package com.core;

public class JhOrg {
    private String id;
    private String pid;
    private boolean top;
    private String name;
    private String adminName;
    private String adminPwd;
    private String area;
    private String area_name;
    private String addr;
    private String bossName;
    private String bossPhone;
    private String linkman;
    private String linkPhone;
    private String linkman2;
    private String linkPhone2;
    private String appId;

    public JhOrg() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isTop() {
        if(this.pid != null && this.pid.length() > 0 && !this.pid.equals("-1")) {
            this.top = false;
        } else {
            this.top = true;
        }

        return this.top;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPwd() {
        return this.adminPwd;
    }

    public void setAdminPwd(String adminPwd) {
        this.adminPwd = adminPwd;
    }

    public String getArea() {
        return this.area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea_name() {
        return this.area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public String getAddr() {
        return this.addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getBossName() {
        return this.bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getBossPhone() {
        return this.bossPhone;
    }

    public void setBossPhone(String bossPhone) {
        this.bossPhone = bossPhone;
    }

    public String getLinkman() {
        return this.linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getLinkPhone() {
        return this.linkPhone;
    }

    public void setLinkPhone(String linkPhone) {
        this.linkPhone = linkPhone;
    }

    public String getLinkman2() {
        return this.linkman2;
    }

    public void setLinkman2(String linkman2) {
        this.linkman2 = linkman2;
    }

    public String getLinkPhone2() {
        return this.linkPhone2;
    }

    public void setLinkPhone2(String linkPhone2) {
        this.linkPhone2 = linkPhone2;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
