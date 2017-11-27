package com.core.server.msg.phone;

import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.msg.Message;
import com.core.server.msg.MessageStatus;
import com.core.server.msg.MessageType;
import com.core.server.tools.PhoneMsgUtils;
import com.core.server.tools.RedisUtils;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class PhoneMessage extends Message {
    private static final long serialVersionUID = 1L;
    private String phoneNumbers;
    private String type;
    private String notifyUsers;

    public PhoneMessage() {
        this.setMessageType(MessageType.PHONE_MSG);
        this.setShowViewURL("public/pub/message/phone_msg/view.jsp");
    }

    public PhoneMessage getThis() throws Exception {
        RedisUtils.setParam(this.getId(), this);
        return this;
    }

    public void sendMessage(boolean send) throws Exception {
        long num = Utils.getSysParamLongValue("sys_phone_msg", 1L, 1L);
        String numstr = Utils.leftPadding(Long.valueOf(num), 10, "0");
        this.setNum(numstr);
        int size = this.getContent().length();
        this.setSize((long)size);
        this.setRemark("");

        try {
            if(this.phoneNumbers == null || this.phoneNumbers.length() <= 5) {
                throw new Exception("没有设置接收短信的手机号码");
            }

            if(send) {
                PhoneMsgUtils.sendMsg(this.getContent(), this.phoneNumbers);
            } else if(!Resources.DEVELOPMENT) {
                PhoneMsgUtils.sendMsg(this.getContent(), this.phoneNumbers);
            } else {
                Logger.info("PhoneMsgUtils sended a Phone Msg to :" + this.getPhoneNumbers());
            }

            this.setStatus(MessageStatus.NEW_MESSAGE);
        } catch (Exception var10) {
            this.setRemark(Utils.getErrorStack(var10));
            this.setStatus(MessageStatus.SEND_ERROR);
            throw var10;
        } finally {
            Utils.execute.execute(new Runnable() {
                public void run() {
                    DBM db = new DBM();
                    Connection conn = null;

                    try {
                        conn = db.getConnection();
                        conn.setAutoCommit(true);
                        EntityImpl e = new EntityImpl("sys_phone_msg", conn);
                        e.setValue("msg_num", PhoneMessage.this.getNum());
                        e.setValue("phones", PhoneMessage.this.getPhoneNumbers());
                        e.setValue("content", PhoneMessage.this.getContent());
                        e.setValue("create_time", PhoneMessage.this.getCreateTime());
                        e.setValue("msg_size", Long.valueOf(PhoneMessage.this.getSize()));
                        e.setValue("status", PhoneMessage.this.getStatus());
                        e.setValue("remark", PhoneMessage.this.getRemark());
                        e.setValue("platform", PhoneMsgUtils.platform);
                        e.setValue("plat_url", PhoneMsgUtils.url);
                        e.setValue("plat_login_name", PhoneMsgUtils.name);
                        e.setValue("plat_pwd", PhoneMsgUtils.pwd2);
                        String id = e.create();
                        PhoneMessage.this.setId(id);
                        if(PhoneMessage.this.getNotifyUsers() != null && PhoneMessage.this.getNotifyUsers().trim().length() > 0) {
                            String[] users = PhoneMessage.this.getNotifyUsers().split(",");
                            String[] var9 = users;
                            int var8 = users.length;

                            for(int var7 = 0; var7 < var8; ++var7) {
                                String user = var9[var7];
                                Object list = (Queue) RedisUtils.getHParam("SEND_NEW_MESSAGE", user);
                                if(list == null) {
                                    list = new LinkedList();
                                }

                                ((Queue)list).offer(PhoneMessage.this.getThis());
                                RedisUtils.setHParam("SEND_NEW_MESSAGE", user, list);
                            }
                        }
                    } catch (Exception var14) {
                        Logger.error(var14);
                    } finally {
                        db.freeConnection(conn);
                    }

                }
            });
        }

    }

    public String getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getNotifyUsers() {
        return this.notifyUsers;
    }

    public void setNotifyUsers(String notifyUsers) {
        this.notifyUsers = notifyUsers;
    }

    public void sendMessage() throws Exception {
        this.sendMessage(false);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
