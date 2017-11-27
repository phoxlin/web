package com.core.server.wxtools;

import com.core.server.log.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class HtmlUtils {
    public static final int WORD_HTML = 8;
    public static final int WORD_TXT = 7;
    public static final int EXCEL_HTML = 44;

    public HtmlUtils() {
    }

    public static void main(String[] args) throws Exception {
    }

    public static String post(String url, Map<String, String> params, String charset) throws Exception {
        PostMethod postMethod = new PostMethod(url);
        HttpClient httpClient = new HttpClient();
        postMethod.getParams().setParameter("http.protocol.content-charset", charset);
        NameValuePair[] ps = new NameValuePair[params.size()];
        int i = 0;

        Map.Entry reader;
        NameValuePair simcard;
        for(Iterator e = params.entrySet().iterator(); e.hasNext(); ps[i++] = simcard) {
            reader = (Map.Entry)e.next();
            simcard = new NameValuePair((String)reader.getKey(), (String)reader.getValue());
        }

        postMethod.setRequestBody(ps);
        Logger.debug(params);
        reader = null;

        String var10;
        try {
            int var22 = httpClient.executeMethod(postMethod);
            if(var22 != 200) {
                throw new Exception("连接失败");
            }

            var10 = postMethod.getResponseBodyAsString();
        } catch (Exception var20) {
            throw var20;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException var19) {
                    ;
                }

                reader = null;
            }

            if(httpClient != null) {
                try {
                    httpClient = null;
                } catch (Exception var18) {
                    ;
                }
            }

        }

        return var10;
    }

    public static String get(String url, String refer, String charset) {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet e = new HttpGet(url);
            if(refer != null && refer.length() > 0) {
                e.setHeader("Referer", refer);
            }

            CloseableHttpResponse response = httpclient.execute(e);

            try {
                HttpEntity entity = response.getEntity();
                if(entity != null) {
                    String var8 = EntityUtils.toString(entity);
                    return var8;
                }
            } finally {
                try {
                    response.close();
                } catch (Exception var30) {
                    ;
                }

            }
        } catch (Exception var32) {
            Logger.error(var32);
        } finally {
            try {
                httpclient.close();
            } catch (Exception var29) {
                ;
            }

        }

        return "";
    }

    public static String get(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String var6;
        try {
            HttpGet e = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(e);

            try {
                HttpEntity entity = response.getEntity();
                if(entity == null) {
                    return "";
                }

                var6 = EntityUtils.toString(entity);
            } finally {
                try {
                    response.close();
                } catch (Exception var28) {
                    ;
                }

            }
        } catch (Exception var30) {
            Logger.error(var30);
            return "";
        } finally {
            try {
                httpclient.close();
            } catch (Exception var27) {
                ;
            }

        }

        return var6;
    }

    public static void parserHtml(String content) throws ParserException {
        Parser parser = Parser.createParser(content, "UTF-8");
        HtmlPage html = new HtmlPage(parser);
        parser.visitAllNodesWith(html);
        TableTag[] tables = html.getTables();
        if(tables != null && tables.length > 0) {
            int k = 0;

            for(int m = tables.length; k < m; ++k) {
                TableTag table = tables[k];
                TableRow[] rows = table.getRows();
                if(rows != null && rows.length > 0) {
                    Logger.info("表格：" + (k + 1));
                    TableRow[] var11 = rows;
                    int var10 = rows.length;

                    for(int var9 = 0; var9 < var10; ++var9) {
                        TableRow row = var11[var9];
                        Logger.info(trimHtmlTxt(row.toPlainTextString()));
                    }
                }
            }
        }

    }

    public static String trimHtmlTxt(String str) {
        if(str != null && str.length() > 0) {
            str = str.replace("&nbsp;", "");
            return str.trim();
        } else {
            return "";
        }
    }

    public static String html2String(String html) throws Exception {
        if(html != null && html.length() > 0) {
            if(!html.contains("<body>")) {
                html = "<body>" + html + "</body>";
            }

            Parser parser = Parser.createParser(html, "UTF-8");
            HtmlPage html2 = new HtmlPage(parser);
            parser.visitAllNodesWith(html2);
            NodeList list = html2.getBody();
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < list.size(); ++i) {
                Node node1 = list.elementAt(i);
                if(node1 instanceof TextNode) {
                    TextNode node = (TextNode)node1;
                    sb.append(node.toPlainTextString());
                } else {
                    TagNode var8 = (TagNode)list.elementAt(i);
                    sb.append(var8.toPlainTextString());
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }
}
