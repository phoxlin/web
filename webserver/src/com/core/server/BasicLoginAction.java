package com.core.server;

import com.core.User;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.tools.SystemUtils;


public class BasicLoginAction extends BasicAction {

    @Route(value = "/BasicUserLogin", conn = false, m = {HttpMethod.POST}, type = ContentType.JSON)
    public void userLogin() throws Exception {
        String name = this.request.getParameter("name");
        String pwd = this.request.getParameter("pwd");
        this.L.warn("框架默认的登录页面请用用户名:admin密码,admin登录,正式项目必须重新写自己的登录页面");
        if("admin".equals(name) && "admin".equals(pwd)) {
            User user = new User();
            user.setLoginName("admin");
            user.setPwd("admin");
            SystemUtils.setSessionUser(user, this.request, this.response);
        } else {
            throw new Exception("错误的用户名或者密码");
        }
    }
}
