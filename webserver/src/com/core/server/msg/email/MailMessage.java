package com.core.server.msg.email;

import com.core.SFile;
import com.core.User;
import com.core.server.db.DBUtils;
import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.msg.Message;
import com.core.server.msg.MessageOpType;
import com.core.server.msg.MessageStatus;
import com.core.server.msg.MessageType;
import com.core.server.tools.MailUtils;
import com.core.server.tools.RedisUtils;
import com.core.server.tools.Utils;

import java.sql.Connection;
import java.util.*;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class MailMessage extends Message {
    private static final long serialVersionUID = 1L;
    private User user;
    private List<Receiver> receivers = new ArrayList();
    private String flag;
    private boolean appx;
    private List<SFile> appxs = new ArrayList();

    public MailMessage() {
        this.setFlag("001");
        this.setMessageType(MessageType.MAIL);
        this.setShowViewURL("public/pub/message/mail_msg/view.jsp");
    }

    public MailMessage getThis() throws Exception {
        RedisUtils.setParam(this.getId(), this);
        return this;
    }

    public void sendMessage() throws Exception {
        if(this.user == null) {
            this.user = new User();
            this.user.setId("00000000000000000000000000000000");
            this.user.setLoginName("admin");
            MailAccount num = User.getDefaultMailAccount();
            this.user.setMailAccount(num);
        }

        long var12 = Utils.getSysParamLongValue("sys_mail#" + this.user.getLoginName(), 1L, 1L);
        String numstr = Utils.leftPadding(Long.valueOf(var12), 10, "0");
        this.setNum(numstr);
        int size = this.getContent().length();
        int f = 0;

        for(int e = this.appxs.size(); f < e; ++f) {
            size = (int)((long)size + ((SFile)this.appxs.get(f)).getFileSize());
        }

        this.setSize((long)size);
        SFile var13 = Utils.saveContent2File(this.getContent(), this.getMessageType() + "_" + Utils.parseData(this.getCreateTime(), "yyyyMMdd_HHmmss") + ".txt", "-1", this.user.getId(), DBUtils.oid());
        this.setHashCode(var13.getHashCode());
        this.setRemark("");

        try {
            MailUtils.sendMail(this.getTitle(), this.receivers, this.appxs, this.getContent(), this.user);
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
                        EntityImpl e = new EntityImpl("sys_mail_msg", conn);
                        e.setValue("msg_num", MailMessage.this.getNum());
                        e.setValue("op_type", MessageOpType.SEND);
                        e.setValue("fk_mail_set_id", MailMessage.this.user.getMailAccount().getId());
                        e.setValue("msg_title", MailMessage.this.getTitle());
                        e.setValue("create_time", MailMessage.this.getCreateTime());
                        e.setValue("msg_size", Long.valueOf(MailMessage.this.getSize()));
                        e.setValue("flag", MailMessage.this.getFlag());
                        e.setValue("hash_code", MailMessage.this.getHashCode());
                        e.setValue("is_appx", MailMessage.this.isAppx()?"Y":"N");
                        e.setValue("status", MailMessage.this.getStatus());
                        e.setValue("remark", MailMessage.this.getRemark());
                        String id = e.create();
                        MailMessage.this.setId(id);
                        Iterator var6 = MailMessage.this.receivers.iterator();

                        EntityImpl sys_appendix;
                        String aId;
                        while(var6.hasNext()) {
                            Receiver list = (Receiver)var6.next();
                            sys_appendix = new EntityImpl("sys_receiver", conn);
                            sys_appendix.setValue("FK_MESSAGE_ID", id);
                            sys_appendix.setValue("RECEIVER", list.getName());
                            sys_appendix.setValue("REC_ADDR", list.getAddr());
                            sys_appendix.setValue("TYPE", list.getReceiverType());
                            aId = sys_appendix.create();
                            list.setId(aId);
                        }

                        if(MailMessage.this.isAppx()) {
                            var6 = MailMessage.this.getAppxs().iterator();

                            while(var6.hasNext()) {
                                SFile list1 = (SFile)var6.next();
                                sys_appendix = new EntityImpl("sys_appendix", conn);
                                sys_appendix.setValue("FK_MESSAGE_ID", id);
                                sys_appendix.setValue("FK_APPX_ID", list1.getId());
                                sys_appendix.setValue("HASH_CODE", list1.getHashCode());
                                aId = sys_appendix.create();
                                list1.setId(aId);
                            }
                        }

                        Object list2 = (Queue)RedisUtils.getHParam("SEND_NEW_MESSAGE", MailMessage.this.user.getLoginName());
                        if(list2 == null) {
                            list2 = new LinkedList();
                        }

                        ((Queue)list2).offer(MailMessage.this.getThis());
                        RedisUtils.setHParam("SEND_NEW_MESSAGE", MailMessage.this.user.getLoginName(), list2);
                    } catch (Exception var12) {
                        Logger.error(var12);
                    } finally {
                        db.freeConnection(conn);
                    }

                }
            });
        }

    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFlag() {
        return this.flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<Receiver> getReceivers() {
        return this.receivers;
    }

    public boolean isAppx() {
        this.appx = this.getAppxs().size() > 0;
        return this.appx;
    }

    public List<SFile> getAppxs() {
        return this.appxs;
    }
}
