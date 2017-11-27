package com.core.server.wx;

import com.core.User;
import com.core.server.BasicAction;
import com.core.server.Route;
import com.core.server.flow.Flow;
import com.core.server.log.Logger;
import com.core.server.m.ContentType;
import com.core.server.m.HttpMethod;
import com.core.server.tools.Resources;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class WxAction extends BasicAction{
    @Route(value = "/jh_wx_pay", conn = true, m = {HttpMethod.POST}, type = ContentType.JSON)
    public void wechatPay() throws Exception {
        String out_trade_no = this.getParameter("out_trade_no");
        String subject = this.getParameter("subject");
        String total_fee = this.getParameter("total_fee");
        String wx_open_id = this.getParameter("wx_open_id");
        if(out_trade_no == null || out_trade_no.length() <= 4) {
            Flow appId = new Flow();
            out_trade_no = appId.getFlownum();
        }

        String appId1 = this.request.getParameter("appId");
        String appsecret = this.request.getParameter("appsecret");
        String key = this.request.getParameter("key");
        String mchId = this.request.getParameter("MchId");
        String businessClass = this.getParameter("businessClass");
        if(appId1 == null || appId1.length() <= 0) {
            String user = this.getParameter("appIdParamName");
            String pay = this.getParameter("appsecretParamName");
            String total_feeFloat = this.getParameter("keyParamName");
            String wxPay = this.getParameter("MchIdParamName");
            appId1 = Resources.getProperty(user);
            appsecret = Resources.getProperty(pay);
            key = Resources.getProperty(total_feeFloat);
            mchId = Resources.getProperty(wxPay);
        }

        User user1 = this.getSessionUser();
        if(wx_open_id != null && wx_open_id.length() > 0) {
            WXPayToPay pay1 = new WXPayToPay();
            Float total_feeFloat1 = Float.valueOf(0.0F);

            try {
                total_feeFloat1 = Float.valueOf(Float.parseFloat(total_fee) * 100.0F);
            } catch (Exception var19) {
                ;
            }

            IWxPay wxPay1 = (IWxPay)Class.forName(businessClass).newInstance();
            HashMap data = new HashMap();
            String payData = this.request.getParameter("payData");

            JSONObject xx;
            try {
                xx = new JSONObject(payData);
                Iterator var18 = xx.keySet().iterator();

                while(var18.hasNext()) {
                    String key1 = (String)var18.next();
                    data.put(key1, xx.get(key1));
                }
            } catch (Exception var20) {
                Logger.warn(var20);
            }

            wxPay1.createBusinessData(out_trade_no, subject, data, total_feeFloat1.longValue(), user1.getId(), this.getConnection());
            xx = pay1.Topay(total_feeFloat1, subject, wx_open_id, out_trade_no, appId1, appsecret, mchId, key);
            this.obj.put("data", xx);
            this.obj.put("out_trade_no", out_trade_no);
            this.obj.put("businessClass", businessClass);
        } else {
            throw new Exception("您的微信号还没有和系统绑定，暂不能使用微信支付,请绑定后再试");
        }
    }

    @Route(value = "/jh_wx_pay_confirm", conn = true, m = {HttpMethod.POST}, type = ContentType.JSON)
    public void wechatPayConfirm() throws Exception {
        String out_trade_no = this.getParameter("out_trade_no");
        String businessClass = this.getParameter("businessClass");
        IWxPay wxPay = (IWxPay)Class.forName(businessClass).newInstance();
        wxPay.updateBusinessData(out_trade_no, this.getConnection());
    }
}
