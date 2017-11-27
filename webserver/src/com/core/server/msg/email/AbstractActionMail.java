package com.core.server.msg.email;

import com.core.server.ActionMail;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class AbstractActionMail extends ActionMail {
    public String getTitle() {
        StringBuilder sb = new StringBuilder();
        if(this.getUser() != null) {
            try {
                sb.append("用户【");
                sb.append(this.getUser().getId());
                sb.append("-");
                sb.append(this.getUser().getLoginName());
                sb.append("】");
            } catch (Exception var3) {
                ;
            }
        }

        sb.append("执行系统--超过【");
        sb.append(this.getEs());
        sb.append("】");
        return sb.toString();
    }

    public String getContent() {
        StringBuilder sb = new StringBuilder("执行系统超过【" + this.getEs() + "ms】,怀疑有性能缺陷请检查：<br/>");
        sb.append("当前服务器:" + this.request.getServletContext().getServerInfo() + "<br/>");
        sb.append("当前服务器端口:" + this.request.getLocalPort() + "<br/>");

        for(int i = 0; i < this.getContentList().size(); ++i) {
            sb.append((String)this.getContentList().get(i));
            sb.append("<br/>");
        }

        return sb.toString();
    }

    public boolean isSend() {
        return true;
    }
}
