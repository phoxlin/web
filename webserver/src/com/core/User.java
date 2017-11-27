package com.core;

import com.core.server.Action;
import com.core.server.c.CItem;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.msg.email.MailAccount;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements IUser,Serializable {
    private static final long serialVersionUID = 1L;
    public static String NAME = "__USER_SESSION_NAME";
    protected Map<String, Object> m = new HashMap();
    private String jurisdiction;
    private String user_id;
    private MailAccount mailAccount = null;
    private static MailAccount defaultMailAccount = null;
    private JhOrg org;

    public String getJurisdiction() {
        return this.jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public User(Action act) throws Exception {
        this.m.put("_id", "sid");
        this.m.put("login_name", "system");
    }

    public User(String appId) throws Exception {
        this.m.put("id", "admin");
        this.m.put("login_name", "system");
        this.m.put("appid", appId);
    }

    public User() {
        this.m.put("id", "admin");
        this.m.put("login_name", "system");
    }

    public static String getUserUrl(String userType, User.URLS urlType) throws Exception {
        if(userType != null && userType.length() > 0) {
            String url = Resources.getProperty(userType + "__" + urlType);
            if(url != null && url.length() > 0) {
                return url;
            } else {
                throw new Exception("系统没有配置相关的用户管理路径：【" + userType + "__" + urlType + "】");
            }
        } else {
            throw new Exception("getUserUrl方法的参数userType值为空");
        }
    }

    public List<String> getRole() throws Exception {
        return (List)this.m.get("role");
    }

    public String getXX(String key) throws Exception {
        String id = Utils.getMapStringValue(this.getM(), key.toLowerCase());
        if(id != null && id.length() > 0) {
            return id;
        } else {
            Logger.warn("User没有发现【" + key + "】信息，请检查系统");
            return null;
        }
    }

    public String getLoginName() throws Exception {
        return this.getXX("login_name");
    }

    public Object get(String key) {
        return this.getM().get(key);
    }

    public String getId() throws Exception {
        if(this.m.containsKey("id")) {
            return this.getXX("id");
        } else {
            throw new Exception("当前用户没有用户ID信息");
        }
    }

    public Map<String, Object> getM() {
        if(this.m == null) {
            this.m = new HashMap();
        }

        return this.m;
    }

    public List<String> getCD() {
        return new ArrayList();
    }

    public void validite(String name, String pwd, Action act) throws Exception {
        EntityImpl en = new EntityImpl(act);
        int size = en.executeQuery("select * from sys_user where login_name=? and pwd=?", new String[]{name, Utils.getSha1(pwd)}, 1, 1);
        if(size <= 0) {
            throw new Exception("用户名或者密码错误");
        } else {
            this.getM().putAll((Map)en.getValues().get(0));
        }
    }

    public String getPwd() throws Exception {
        return this.getXX("pwd");
    }

    public void setLoginName(String loginName) {
        this.getM().put("login_name", loginName);
    }

    public void setPwd(String pwd) {
        this.getM().put("pwd", pwd);
    }

    public JhOrg getOrg() {
        return this.org;
    }

    public List<CItem> getUserListBySet(String key, String defaultVal, Connection conn) throws Exception {
        String userImpl = Resources.getProperty("UserImpl");
        IUser impl = null;

        try {
            impl = (IUser)Class.forName(userImpl).newInstance();
            return impl.getUserListBySet(key, defaultVal, conn);
        } catch (Exception var7) {
            throw new Exception("系统没有配置相关的用户管理类：【UserImpl】");
        }
    }

    public void setId(String id) {
        this.user_id = id;
        this.getM().put("id", id);
    }

    public MailAccount getMailAccount() {
        return this.mailAccount;
    }

    public void setMailAccount(MailAccount mailAccount) {
        this.mailAccount = mailAccount;
    }

    public static MailAccount getDefaultMailAccount() {
        if(defaultMailAccount == null) {
            defaultMailAccount = new MailAccount();
            defaultMailAccount.setId("00000000000000000000000000000000");
            defaultMailAccount.setFromAddr("18008066220@163.com");
            defaultMailAccount.setFromName("成都铭硕科技");
            defaultMailAccount.setSmtpHost(Resources.getProperty("mail.smtp.host", "smtp.163.com"));
            defaultMailAccount.setSmtpPort(Resources.getProperty("mail.smtp.port", "25"));
            defaultMailAccount.setSmtpName(Resources.getProperty("mail.smtp.username", "18008066220@163.com"));
            defaultMailAccount.setSmtpPwd(Resources.getProperty("mail.smtp.pwd", "pkf260u3"));
        }

        return defaultMailAccount;
    }

    public static enum URLS {
        VIEW_URL,
        CHOOSE_URL;

        private URLS() {
        }
    }
}
