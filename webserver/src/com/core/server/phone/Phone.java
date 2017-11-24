package com.core.server.phone;

import com.core.server.log.Logger;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Phone {

    private String phone;
    public static String VERIFY_CODE = "VERIFY_CODE";
    private static Random rand = new Random();



    public Phone(String phone) {
        this.phone = phone;
    }

    public static void main(String[] args) throws Exception {
        Phone p = new Phone("123");

        for(int i = 0; i < 100; ++i) {
            p.createVerifyCode();
        }

    }

    public void verifyCode(String phone, String vCode) throws Exception {
        Jedis rd = null;

        try {
            rd = RedisUtils.getConnection();
            Object e = RedisUtils.getHParam(VERIFY_CODE, phone, rd);
            if(e == null || !vCode.equals(e.toString())) {
                throw new Exception("验证失败");
            }

            Logger.info("验证成功");
        } catch (Exception var8) {
            throw var8;
        } finally {
            RedisUtils.freeConnection(rd);
        }

    }

    public String createVerifyCode() throws Exception {
        if(this.phone != null && this.phone.length() > 0) {
            String str = "0,1,2,3,4,5,6,7,8,9";
            String[] str2 = str.split(",");
            boolean index = false;
            StringBuffer randStr = new StringBuffer();

            for(int rd = 0; rd < 4; ++rd) {
                int var13 = rand.nextInt(str2.length - 1);
                randStr.append(str2[var13]);
            }

            Jedis var14 = null;

            String var8;
            try {
                var14 = RedisUtils.getConnection();
                RedisUtils.setHParam(VERIFY_CODE, this.phone, randStr.toString(), var14, 300);
                Logger.info("验证码：" + randStr);
                var8 = randStr.toString();
            } catch (Exception var11) {
                throw var11;
            } finally {
                RedisUtils.freeConnection(var14);
            }

            return var8;
        } else {
            throw new Exception("请设置发送验证码的手机号码");
        }
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
