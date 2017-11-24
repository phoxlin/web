package com.core.server;

import com.core.User;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;

public class BasicAction extends Action {
    private User sessionUser;

    public BasicAction() {
    }

    @Route(
            conn = false,
            type = ContentType.HTML
    )
    public void defaultGetPost(String[] params) throws Exception {
        this.out.println("Hello World!!ddd");
    }

    @Route(
            conn = true,
            type = ContentType.HTML
    )
    public void defaultPut(String[] params) {
        this.out.println("Hello World!!");
    }

    @Route(
            conn = true,
            type = ContentType.HTML
    )
    public void defaultDelete(String[] params) {
        this.out.println("Hello World!!");
    }

    public User getSessionUser() throws Exception {
        if(this.sessionUser == null) {
            this.sessionUser = SystemUtils.getSessionUser(this.request, this.response);
        }

        return this.sessionUser;
    }

    @Route(
            value = "/QR",
            conn = false,
            m = {HttpMethod.GET},
            type = ContentType.JPG
    )
    public void showJpg() throws Exception {
        this.response.setContentType("image/jpeg; charset=UTF-8");
        String str = this.getParameter("s");
        if(str != null && str.length() > 0) {
            try {
                ZxingUtils.createQr(str, this.response.getOutputStream());
            } catch (Exception var3) {
                this.L.error(var3);
            }
        }

    }

    @Route(
            value = "/GetValidate",
            conn = false,
            m = {HttpMethod.GET},
            type = ContentType.JPG
    )
    public void getValidate() throws Exception {
        this.response.setContentType("image/jpeg");
        this.response.setHeader("Pragma", "No-cache");
        this.response.setHeader("Cache-Control", "no-cache");
        this.response.setDateHeader("Expire", 0L);
        RandomValidateCode randomValidateCode = new RandomValidateCode();
        randomValidateCode.getRandcode(this.request, this.response);
    }
}
