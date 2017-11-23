package com.yepao.manager;

import com.gd.m.Model;
import com.jinhua.server.BasicAction;
import com.jinhua.server.Route;
import com.jinhua.server.db.Entity;
import com.jinhua.server.db.impl.EntityImpl;
import com.jinhua.server.m.ContentType;
import com.jinhua.server.m.HttpMethod;
import com.jinhua.server.tools.SystemUtils;
import com.jinhua.server.tools.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登陆
 * Created by Administrator on 2017/11/15.
 */
public class LoginAction extends BasicAction {
    /**
     * /ws-login-backend
     */
    @Route(value = "/ws-login-backend", conn = true, mdb = false, m = HttpMethod.POST, type = ContentType.JSON)
    public void loginBackend() throws Exception {
        String cust_name = request.getParameter("cust_name");
        String name = this.getParameter("name");
        String pwd = this.getParameter("pwd");
        if (name == null || name.length() <= 0 || pwd == null || pwd.length() <= 0 || "-1".equals(name)) {
            throw new Exception("用户名或者密码为空");
        }



        /*// 查询健身房总部登录管理员账号
        Entity en = new EntityImpl("yp_gym", this);
        en.setValue("admin_login", name);
        en.setValue("admin_pwd", Utils.getMd5(pwd));
        int size = en.search();
        List<String> gids = new ArrayList<>();
        List<String> gnames = new ArrayList<>();

            // 查询系统模块
            String sql = "select a.*, a.model_code code,\n" + "       a.model_icon icon,\n"
                    + "       a.model_name name from yp_model a\n";

            Entity models = new EntityImpl("yp_model", this);
            size = models.executeQuery(sql);
            Map<String, Model> cdInfo = new HashMap<>();

            // 查询所有模块的子功能
            for (int i = 0; i < size; i++) {

                Model model = new Model(models.getValues().get(i));
                cdInfo.put(models.getStringValue("code", i), model);
                cdInfo.put(models.getStringValue("code", i).toLowerCase(), model);

                Map<String, Object> item = models.getValues().get(i);
                String task = item.get("model_task") + "";
                String legend = item.get("model_legend") + "";
                if (task != null && !"null".equals(task) && legend != null && !"null".equals(legend)) {

                }

            }


            // 查询支付宝支付配置信息
            size = en.executeQuery("select id,note from yp_param where code='Alipay' and cust_name = 'yepao'");
            if (size > 0) {
                String note = en.getStringValue("note");
                JSONArray arr = new JSONArray(note);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);

                }
            }

            // 查询配置微信支付信息
            en = new EntityImpl("yp_param", this);
            size = en.executeQuery("select id,note from yp_param where code='wechat' and cust_name = 'yepao'");
            if (size > 0) {
                String note = en.getStringValue("note");
                JSONObject obj = new JSONObject(note);
            }
            SystemUtils.setSessionUser(null, request, response);*/


    }

}
