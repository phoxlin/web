package com.core.server.tools;

import com.core.SFile;
import com.core.User;
import com.core.enuts.DBType;
import com.core.server.db.DBUtils;
import com.core.server.db.impl.EntityImpl;
import com.core.server.msg.email.Address;
import com.core.server.msg.email.MailAccount;
import com.core.server.msg.email.Receiver;

import javax.imageio.ImageIO;
import javax.mail.Session;
import javax.mail.Store;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

public final class MailUtils {
    public static Store checkPOP3Server(Properties props, String username, String pwd) throws Exception {
        props.put("mail.pop3.auth", Boolean.valueOf(Resources.getBooleanProperty("mail.pop3.auth", true)));
        String port = props.getProperty("mail.pop3.port");
        if(port == null || port.length() <= 0) {
            props.put("mail.pop3.port", "110");
        }

        DefaultAuthenticator authenticator = new DefaultAuthenticator(username, pwd);
        Session session = Session.getInstance(props, authenticator);
        Store store = session.getStore("pop3");
        store.connect();
        return store;
    }

    public static void sendMail(String title, List<Receiver> receivers, List<SFile> appx, String content, User user) throws Exception {
        MailAccount info = user.getMailAccount();
        if(info == null) {
            throw new Exception("当前用户：" + user.getLoginName() + ",没有设置EMail相关信息");
        } else {
            HtmlEmail email = new HtmlEmail();
            email.setCharset("UTF-8");
            email.setSubject(title);
            email.setHostName(info.getSmtpHost());
            email.setSmtpPort(Integer.parseInt(info.getSmtpPort()));
            if(Resources.getBooleanProperty("mail.smtp.auth", true)) {
                email.setAuthenticator(new DefaultAuthenticator(info.getSmtpName(), info.getSmtpPwd()));
            }

            Address from = new Address(info.getFromName(), info.getFromAddr());
            email.setFrom(from.getEmail(), from.getPersonal());
            Iterator l = receivers.iterator();

            while(l.hasNext()) {
                Receiver i = (Receiver)l.next();
                switch(i.getReceiverType().ordinal()) {
                    case 1:
                        email.addTo(i.getAddr(), i.getName());
                        break;
                    case 2:
                        email.addCc(i.getAddr(), i.getName());
                        break;
                    case 3:
                        email.addBcc(i.getAddr(), i.getName());
                }
            }

            email.setHtmlMsg(content);
            if(appx != null && appx.size() > 0) {
                int var12 = 0;

                for(int var13 = appx.size(); var12 < var13; ++var12) {
                    SFile app = (SFile)appx.get(var12);
                    EmailAttachment attachment = new EmailAttachment();
                    attachment.setPath(app.getPath());
                    attachment.setDisposition("attachment");
                    attachment.setDescription(app.getFileName());
                    attachment.setName(MimeUtility.encodeText(app.getFileName()));
                    email.attach(attachment);
                }
            }

            email.send();
        }
    }

    public static void sendMail(Properties props, String subject, List<Address> to, List<Address> cc, List<Address> bcc, List<SFile> appx, String content, User user, MailAccount info, Connection conn) throws Exception {
        if(content == null || content.length() <= 0) {
            content = "";
        }

        long num = Utils.getSysParamLongValue("sys_message#" + user.getLoginName(), 1L, 1L);
        String numstr = Utils.leftPadding(Long.valueOf(num), 10, "0");
        String id = DBUtils.oid();
        String nowStr = DBUtils.formartDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        String hashId = DBUtils.uuid();
        String fk_fileId = DBUtils.uuid();
        String bodyHashCode = "";
        Statement st = conn.createStatement();

        String username;
        String pwd;
        int l;
        try {
            String from = nowStr + ".txt";
            String email = "txt";
            username = Resources.getProperty("FileStore", Utils.getWebRootPath());
            pwd = Utils.getFilePath(hashId);
            File size = new File(username + pwd);
            if(!size.exists()) {
                size.mkdirs();
            }

            File file = new File(username + pwd + "/" + hashId + "." + email);
            BufferedWriter msgSql = null;

            try {
                msgSql = new BufferedWriter(new FileWriter(file));
                msgSql.write(content);
                msgSql.flush();
            } catch (Exception var39) {
                ;
            } finally {
                if(msgSql != null) {
                    msgSql.close();
                }

            }

            bodyHashCode = Utils.getSha1(file);
            String i = "N";
            l = 0;
            int app = 0;
            boolean attachment = Utils.isPicFile(file);
            if(attachment) {
                i = "Y";
                BufferedImage fileSize = ImageIO.read(file);
                l = (int)((double)fileSize.getWidth((ImageObserver)null));
                app = (int)((double)fileSize.getHeight((ImageObserver)null));
            }

            long var52 = file.length();
            EntityImpl en = new EntityImpl(conn);
            en.executeQuery("select id  from sys_hash a where a.hash_code=\'" + bodyHashCode + "\'");
            if(en.getResultCount() <= 0) {
                String fileName2 = "insert into sys_hash(id,hash_code,filename,file_size,extension)VALUES(\'" + hashId + "\',\'" + bodyHashCode + "\',\'" + from + "\'," + var52 + ",\'" + email + "\')";
                st.addBatch(fileName2);
            } else {
                hashId = en.getStringValue("id");
                file.delete();
            }

            Date now = new Date();
            String fileSql = "insert into sys_file(id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + fk_fileId + "\', \'" + from + "\',\'" + from + "\', " + var52 + ", \'" + email + "\', \'" + bodyHashCode + "\', \'" + UUID.randomUUID().toString().replace("-", "") + "\', \'" + Utils.parseData(now, "yyyy-MM-dd HH:mm:ss") + "\', \'" + i + "\', " + l + ", " + app + ")";
            if(DBUtils.getDBType() == DBType.Oracle) {
                fileSql = "insert into sys_file(id, filename, re_name, file_size, extension, hash_code, session_id, create_time, is_pic, width, height) VALUES ( \'" + fk_fileId + "\', \'" + from + "\',\'" + from + "\', " + var52 + ", \'" + email + "\', \'" + bodyHashCode + "\', \'" + UUID.randomUUID().toString().replace("-", "") + "\', to_date(\'" + nowStr + "\',\'YYYY-MM-DD hh24:MI:SS\'), \'" + i + "\', " + l + ", " + app + ")";
            }

            st.addBatch(fileSql);
        } catch (Exception var41) {
            ;
        }

        Address var42 = new Address(info.getFromName(), info.getFromAddr());
        HtmlEmail var43 = new HtmlEmail();
        var43.setCharset("UTF-8");
        var43.setHostName(props.getProperty("mail.smtp.host"));
        var43.setSmtpPort(Integer.parseInt(props.getProperty("mail.smtp.port", "25")));
        username = props.getProperty("mail.smtp.username");
        pwd = props.getProperty("mail.smtp.pwd");
        if(Resources.getBooleanProperty("mail.smtp.auth", true)) {
            var43.setAuthenticator(new DefaultAuthenticator(username, pwd));
        }

        long var44 = (long)content.length();
        int var47;
        if(appx != null && ((List)appx).size() > 0) {
            int var45 = 0;

            for(var47 = ((List)appx).size(); var45 < var47; ++var45) {
                SFile var48 = (SFile)((List)appx).get(var45);
                String var49 = "insert into SYS_APPENDIX(id,FK_MESSAGE_ID,FK_APPX_ID,HASH_CODE) VALUES (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var48.getId() + "\',\'" + var48.getHashCode() + "\')";
                st.addBatch(var49);
                var44 += var48.getFileSize();
            }
        } else {
            appx = new ArrayList();
        }

        var43.setFrom(var42.getEmail(), var42.getPersonal());
        String var46 = "INSERT INTO sys_message(id,msg_num,msg_type,op_type,fk_sender_id,sender,send_addr,msg_title,msg_summary,create_time,msg_size,fk_set_id,flag,content,is_file_content,hash_code,is_appx,status,hash_id,fk_file_id) VALUES (\'" + id + "\',\'" + numstr + "\',\'002\',\'002\',\'\',\'" + var42.getPersonal() + "\',\'" + var42.getEmail() + "\',\'" + subject + "\',\'" + subject + "\',\'" + nowStr + "\'," + var44 + ",\'" + info.getId() + "\',\'001\',\'\',\'Y\',\'" + bodyHashCode + "\',\'" + (((List)appx).size() > 0?"Y":"N") + "\',\'001\',\'" + hashId + "\',\'" + fk_fileId + "\')";
        if(DBUtils.getDBType() == DBType.Oracle) {
            var46 = "INSERT INTO sys_message(id,msg_num,msg_type,op_type,fk_sender_id,sender,send_addr,msg_title,msg_summary,create_time,msg_size,fk_set_id,flag,content,is_file_content,hash_code,is_appx,status,hash_id,fk_file_id) VALUES (\'" + id + "\',\'" + numstr + "\',\'002\',\'002\',\'\',\'" + var42.getPersonal() + "\',\'" + var42.getEmail() + "\',\'" + subject + "\',\'" + subject + "\',to_date(\'" + nowStr + "\',\'YYYY-MM-DD hh24:MI:SS\')," + var44 + ",\'" + info.getId() + "\',\'001\',\'\',\'Y\',\'" + bodyHashCode + "\',\'" + (((List)appx).size() > 0?"Y":"N") + "\',\'001\',\'" + hashId + "\',\'" + fk_fileId + "\')";
        }

        st.addBatch(var46);
        Address var50;
        String var51;
        if(to != null && to.size() > 0) {
            var47 = 0;

            for(l = to.size(); var47 < l; ++var47) {
                var50 = (Address)to.get(var47);
                var43.addTo(var50.getEmail(), var50.getPersonal());
                var51 = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var50.getPersonal() + "\',\'" + var50.getEmail() + "\',\'TO\')";
                st.addBatch(var51);
            }
        }

        if(cc != null && cc.size() > 0) {
            var47 = 0;

            for(l = cc.size(); var47 < l; ++var47) {
                var50 = (Address)cc.get(var47);
                var43.addCc(var50.getEmail(), var50.getPersonal());
                var51 = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var50.getPersonal() + "\',\'" + var50.getEmail() + "\',\'CC\')";
                st.addBatch(var51);
            }
        }

        if(bcc != null && bcc.size() > 0) {
            var47 = 0;

            for(l = bcc.size(); var47 < l; ++var47) {
                var50 = (Address)bcc.get(var47);
                var43.addBcc(var50.getEmail(), var50.getPersonal());
                var51 = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var50.getPersonal() + "\',\'" + var50.getEmail() + "\',\'BCC\')";
                st.addBatch(var51);
            }
        }

        if(subject == null) {
            subject = "";
        }

        var43.setSubject(subject);
        var43.setHtmlMsg(content);
        if(appx != null && ((List)appx).size() > 0) {
            var47 = 0;

            for(l = ((List)appx).size(); var47 < l; ++var47) {
                SFile var53 = (SFile)((List)appx).get(var47);
                EmailAttachment var54 = new EmailAttachment();
                var54.setPath(var53.getPath());
                var54.setDisposition("attachment");
                var54.setDescription(var53.getFileName());
                var54.setName(MimeUtility.encodeText(var53.getFileName()));
                var43.attach(var54);
            }
        }

        st.executeBatch();
        var43.send();
    }

    public static int receiveMail(Store store, Connection conn, User user, String setId) throws Exception {
        int total = 0;
        Folder folder = store.getFolder("INBOX");
        folder.open(1);
        if(folder instanceof POP3Folder) {
            POP3Folder inbox = (POP3Folder)folder;
            Message[] message = inbox.getMessages();
            Statement st = conn.createStatement();
            EntityImpl en = new EntityImpl(conn);

            for(int i = 0; i < message.length; ++i) {
                MimeMessage msg = (MimeMessage)message[i];
                String id = ReceivedMail.trimMessageId(inbox.getUID(msg));
                ReceivedMail mail = new ReceivedMail(id, msg, conn, st);
                int size = en.executeQuery("select count(*) num from sys_message a where id=\'" + id + "\'");
                if(size != 1 || en.getIntegerValue("num").intValue() <= 0) {
                    long num = Utils.getSysParamLongValue("sys_message#" + user.getLoginName(), 1L, 1L);
                    String numstr = Utils.leftPadding(Long.valueOf(num), 10, "0");
                    mail.getBody();
                    List appx = mail.getAppx();
                    Address from = mail.getFromAddr();
                    String msgSql = "INSERT INTO sys_message(id,msg_num,msg_type,op_type,fk_sender_id,sender,send_addr,msg_title,msg_summary,create_time,msg_size,fk_set_id,flag,content,is_file_content,hash_code,is_appx,status,hash_id,fk_file_id) VALUES (\'" + id + "\',\'" + numstr + "\',\'002\',\'001\',\'\',\'" + from.getPersonal() + "\',\'" + from.getEmail() + "\',\'" + mail.getSubject() + "\',\'" + mail.getSubject() + "\',\'" + mail.getSendDateStr() + "\'," + mail.getSize() + ",\'" + setId + "\',\'001\',\'\',\'Y\',\'" + mail.getBodyHashCode() + "\',\'" + (appx.size() > 0?"Y":"N") + "\',\'001\',\'" + mail.getHashId() + "\',\'" + mail.getFk_fileId() + "\')";
                    st.addBatch(msgSql);
                    String type = "TO";
                    List tos = mail.getTo();
                    int bccs;
                    if(tos.size() > 0) {
                        int ccs = 0;

                        for(bccs = tos.size(); ccs < bccs; ++ccs) {
                            Address k = (Address)tos.get(ccs);
                            String l = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + k.getPersonal() + "\',\'" + k.getEmail() + "\',\'" + type + "\')";
                            st.addBatch(l);
                        }
                    }

                    type = "CC";
                    List var29 = mail.getCc();
                    int var31;
                    if(var29.size() > 0) {
                        bccs = 0;

                        for(var31 = var29.size(); bccs < var31; ++bccs) {
                            Address var32 = (Address)var29.get(bccs);
                            String a = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var32.getPersonal() + "\',\'" + var32.getEmail() + "\',\'" + type + "\')";
                            st.addBatch(a);
                        }
                    }

                    type = "BCC";
                    List var30 = mail.getBcc();
                    if(var30.size() > 0) {
                        var31 = 0;

                        for(int var33 = var30.size(); var31 < var33; ++var31) {
                            Address var34 = (Address)var30.get(var31);
                            String sql = "insert into SYS_RECEIVER (id,FK_MESSAGE_ID,RECEIVER,REC_ADDR,TYPE) values (\'" + DBUtils.uuid() + "\',\'" + id + "\',\'" + var34.getPersonal() + "\',\'" + var34.getEmail() + "\',\'" + type + "\')";
                            st.addBatch(sql);
                        }
                    }

                    if(i % 20 == 0) {
                        st.executeBatch();
                    }

                    ++total;
                }
            }

            st.executeBatch();
        }

        return total;
    }

    public static int receiveMail2(Store store, Connection conn, User user, String setId) throws Exception {
        int total = 0;
        Folder folder = store.getFolder("INBOX");
        folder.open(1);
        if(folder instanceof POP3Folder) {
            POP3Folder inbox = (POP3Folder)folder;
            Message[] message = inbox.getMessages();

            for(int i = 0; i < message.length; ++i) {
                MimeMessage msg = (MimeMessage)message[i];
                String id = ReceivedMail.trimMessageId(inbox.getUID(msg));
                Logger.info(id);
                ++total;
            }
        }

        return total;
    }

    public static void checkSTMPServer(Properties props, String username, String pwd) throws Exception {
        HtmlEmail email = new HtmlEmail();
        String fromAddr = props.getProperty("mail.fromAddr");
        if(fromAddr == null || fromAddr.length() <= 0) {
            fromAddr = username;
        }

        email.setHostName(props.getProperty("mail.smtp.host"));
        email.setSmtpPort(Integer.parseInt(props.getProperty("mail.smtp.port", "25")));
        email.setAuthenticator(new DefaultAuthenticator(username, pwd));
        email.setFrom(fromAddr);
        email.addTo(fromAddr);
        email.setSubject("SMTP 服务器验证（自动）");
        email.send();
    }

    public static void main(String[] args) throws Exception {
        Logger.info("oik");
        MailMessage msg = new MailMessage();
        msg.setContent("付了款的手机费了多少咖啡机都是");
        msg.setTitle("测试。。。。");
        msg.getReceivers().add(new Receiver("郭勇灵", "251294371@qq.com", ReceiverType.TO));
        msg.getReceivers().add(new Receiver("Terry", "ybbcelvo@163.com", ReceiverType.CC));
        msg.sendMessage();
    }
}
