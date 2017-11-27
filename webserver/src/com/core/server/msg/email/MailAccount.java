package com.core.server.msg.email;

import java.io.Serializable;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class MailAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String fromName;
    private String fromAddr;
    private String pop3host;
    private String pop3Port;
    private String pop3name;
    private String pop3pwd;
    private String smtpHost;
    private String smtpPort;
    private String smtpName;
    private String smtpPwd;
    private String signature;

    public MailAccount() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignature() {
        if(this.signature == null) {
            this.signature = "";
        }

        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFromName() {
        return this.fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAddr() {
        return this.fromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.fromAddr = fromAddr;
    }

    public String getPop3host() {
        return this.pop3host;
    }

    public void setPop3host(String pop3host) {
        this.pop3host = pop3host;
    }

    public String getPop3Port() {
        return this.pop3Port;
    }

    public void setPop3Port(String pop3Port) {
        this.pop3Port = pop3Port;
    }

    public String getPop3name() {
        return this.pop3name;
    }

    public void setPop3name(String pop3name) {
        this.pop3name = pop3name;
    }

    public String getPop3pwd() {
        return this.pop3pwd;
    }

    public void setPop3pwd(String pop3pwd) {
        this.pop3pwd = pop3pwd;
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return this.smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpName() {
        return this.smtpName;
    }

    public void setSmtpName(String smtpName) {
        this.smtpName = smtpName;
    }

    public String getSmtpPwd() {
        return this.smtpPwd;
    }

    public void setSmtpPwd(String smtpPwd) {
        this.smtpPwd = smtpPwd;
    }
}
