package com.core.server.msg;

import com.core.server.db.impl.DBM;
import com.core.server.db.impl.EntityImpl;
import com.core.server.log.Logger;
import com.core.server.tools.RedisUtils;
import com.core.server.tools.Utils;

import java.sql.Connection;
import java.util.Date;

/**
 * Created by chen_lin on 2017/11/27.
 */
public abstract class Message {
    private static final long serialVersionUID = 1L;
    public static final String SEND_NEW_MESSAGE = "SEND_NEW_MESSAGE";
    private MessageType messageType;
    private String id;
    private String num;
    private String title;
    private Date createTime = new Date();
    private long size;
    private String content;
    private String hashCode;
    private MessageStatus status;
    private String showViewURL;
    private String remark;

    public Message() {
        this.status = MessageStatus.NEW_MESSAGE;
    }

    public abstract void sendMessage() throws Exception;

    public void updateStatus(MessageStatus status) throws Exception {
        this.setStatus(status);
        RedisUtils.setParam(this.id, this);
        Utils.execute.execute(new Runnable() {
            public void run() {
                DBM db = new DBM();
                Connection conn = null;

                try {
                    conn = db.getConnection();
                    conn.setAutoCommit(true);
                    EntityImpl e = null;
                    switch(Message.this.getMessageType().ordinal()) {
                        case 1:
                            e = new EntityImpl("sys_mail_msg", conn);
                            break;
                        case 2:
                            e = new EntityImpl("sys_phone_msg", conn);
                            break;
                        case 3:
                            e = new EntityImpl("sys_paper_msg", conn);
                            break;
                        case 4:
                        case 5:
                        default:
                            Logger.error("系统对 类型为：" + Message.this.getMessageType() + ", 的信息没有实现更新状态操作.");
                            break;
                        case 6:
                            e = new EntityImpl("sys_qq_msg", conn);
                    }

                    if(e != null) {
                        e.setValue("id", Message.this.getId());
                        e.setValue("status", Message.this.getStatus());
                        e.update();
                    }
                } catch (Exception var7) {
                    Logger.error(var7);
                } finally {
                    db.freeConnection(conn);
                }

            }
        });
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSummary() {
        return this.content.length() > 30?this.content.substring(0, 28):this.content;
    }

    public String getTitle() {
        if(this.title == null) {
            this.title = "<无标题>";
        }

        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public long getSize() {
        return this.size;
    }

    public String getContent() {
        if(this.content == null) {
            this.content = "";
        }

        return this.content;
    }

    public void setContent(String content) throws Exception {
        this.content = content;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public MessageStatus getStatus() {
        return this.status;
    }

    public String getHashCode() {
        return this.hashCode;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getShowViewURL() {
        return this.showViewURL;
    }

    public void setShowViewURL(String showViewURL) {
        this.showViewURL = showViewURL;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
