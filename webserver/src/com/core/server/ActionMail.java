package com.core.server;

import com.core.User;
import com.core.server.log.Logger;
import com.core.server.msg.email.MailMessage;
import com.core.server.msg.email.Receiver;
import com.core.server.msg.email.ReceiverType;
import com.core.server.tools.Resources;
import com.core.server.tools.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ActionMail {
    private static List<String> tos = Resources.getListProperty("action.performance.receiver.to");
    private static List<String> ccs = Resources.getListProperty("action.performance.receiver.cc");
    private static List<String> bccs = Resources.getListProperty("action.performance.receiver.bcc");
    public static String actionMailClass = Resources.getProperty("action.performance.check.class", "com.jinhua.server.msg.email.AbstractActionMail");
    public static long 性能提醒时间 = Resources.getLongProperty("action.performance.check.time", 500L);
    private List<String> contentList = new ArrayList();
    private long start;
    private long end;
    private long es;
    private User user;
    public HttpServletRequest request;
    public HttpServletResponse response;


    public void send() {
        (new Thread(new Runnable() {
            public void run() {
                MailMessage msg = new MailMessage();

                try {
                    msg.setContent(ActionMail.this.getContent());
                    msg.setTitle(ActionMail.this.getTitle());
                    if(ActionMail.tos.size() <= 0) {
                        ActionMail.tos.add("郭勇灵--251294371@qq.com");
                    }

                    Iterator var3 = ActionMail.tos.iterator();

                    String e;
                    List res;
                    String name;
                    String addr;
                    while(var3.hasNext()) {
                        e = (String)var3.next();
                        if(!Utils.isNull(e)) {
                            res = Utils.split(e, new String[]{"--", "__", ","});
                            name = (String)res.get(0);
                            addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                            msg.getReceivers().add(new Receiver(name, addr, ReceiverType.TO));
                        }
                    }

                    if(ActionMail.ccs.size() > 0) {
                        var3 = ActionMail.ccs.iterator();

                        while(var3.hasNext()) {
                            e = (String)var3.next();
                            if(!Utils.isNull(e)) {
                                res = Utils.split(e, new String[]{"--", "__", ","});
                                name = (String)res.get(0);
                                addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                                msg.getReceivers().add(new Receiver(name, addr, ReceiverType.CC));
                            }
                        }
                    }

                    if(ActionMail.bccs.size() > 0) {
                        var3 = ActionMail.bccs.iterator();

                        while(var3.hasNext()) {
                            e = (String)var3.next();
                            if(!Utils.isNull(e)) {
                                res = Utils.split(e, new String[]{"--", "__", ","});
                                name = (String)res.get(0);
                                addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                                msg.getReceivers().add(new Receiver(name, addr, ReceiverType.BCC));
                            }
                        }
                    }

                    if(ActionMail.this.isSend()) {
                        msg.sendMessage();
                    } else {
                        Logger.debug("=====mail=========");
                        Logger.debug("   subject:" + ActionMail.this.getTitle());
                        var3 = ActionMail.tos.iterator();

                        while(var3.hasNext()) {
                            e = (String)var3.next();
                            if(!Utils.isNull(e)) {
                                res = Utils.split(e, new String[]{"--", "__", ","});
                                name = (String)res.get(0);
                                addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                                Logger.debug("   to:" + name + " - " + addr);
                            }
                        }

                        if(ActionMail.ccs.size() > 0) {
                            var3 = ActionMail.ccs.iterator();

                            while(var3.hasNext()) {
                                e = (String)var3.next();
                                if(!Utils.isNull(e)) {
                                    res = Utils.split(e, new String[]{"--", "__", ","});
                                    name = (String)res.get(0);
                                    addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                                    Logger.debug("   cc:" + name + " - " + addr);
                                }
                            }
                        }

                        if(ActionMail.bccs.size() > 0) {
                            var3 = ActionMail.bccs.iterator();

                            while(var3.hasNext()) {
                                e = (String)var3.next();
                                if(!Utils.isNull(e)) {
                                    res = Utils.split(e, new String[]{"--", "__", ","});
                                    name = (String)res.get(0);
                                    addr = res.size() > 1?(String)res.get(1):(String)res.get(0);
                                    Logger.debug("   bcc:" + name + " - " + addr);
                                }
                            }
                        }

                        Logger.debug("=====content:======");
                        Logger.debug(ActionMail.this.getContent());
                    }
                } catch (Exception var7) {
                    Logger.error(var7);
                }

            }
        })).start();
    }

    public abstract boolean isSend();

    public List<String> getContentList() {
        return this.contentList;
    }

    public long getEs() {
        return this.es;
    }

    public User getUser() {
        return this.user;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setEs(long se) {
        this.es = se;
    }

    public abstract String getTitle();

    public abstract String getContent();

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
