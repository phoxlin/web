package com.core.server;

import com.core.server.log.Logger;
import com.core.server.tools.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlServlet extends HttpServlet {
    public static Map<String, Boolean> fileInfos = new HashMap();
    private static String WebContentPath = null;
    public static final String includeRegx = ".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*";
    public static Map<String, String> fileBuffer = new HashMap();
    private static final long serialVersionUID = 1L;
    public static final String exprRegx = ".*(<!--#\\{([\\w.]*)\\}-->).*";

    public HtmlServlet() {
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(WebContentPath == null) {
            WebContentPath = Utils.getWebRootPath();
            WebContentPath = WebContentPath.substring(0, WebContentPath.length() - 1);
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try {
            this.staticFileFlush(request.getServletPath(), request, response);
        } catch (Exception var4) {
            throw new ServletException(var4);
        }
    }

    public void staticFileFlush(String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(!url.toLowerCase().endsWith(".jsp")) {
            File f = new File(WebContentPath + url);
            String fpath = f.getAbsolutePath();
            boolean containsFile = fileBuffer.containsKey(fpath);
            if(!Resources.DEVELOPMENT && containsFile) {
                this.responeFile((String)((String)fileBuffer.get(fpath)), response);
            } else {
                boolean hasExper;
                if(!fileInfos.containsKey(fpath)) {
                    hasExper = hasExpr(f);
                    fileInfos.put(fpath, Boolean.valueOf(hasExper));
                }

                hasExper = Utils.getMapBooleanValue(fileInfos, fpath);
                if(hasExper) {
                    String content = getStaticFileContent(f, hasExper, request);
                    f = new File(WebContentPath + "/temp/" + url);
                    Utils.saveContent2File(f, content);
                    fileBuffer.put(fpath, content);
                } else {
                    fileBuffer.put(fpath, Utils.getFileContent(f));
                }

                this.responeFile((File)f, response);
            }

        }
    }

    public static String checkExpr(String line, HttpServletRequest request) {
        Pattern exprPattern = Pattern.compile(".*(<!--#\\{([\\w.]*)\\}-->).*");
        Matcher exprMatcher = exprPattern.matcher(line);
        if(exprMatcher.find()) {
            int groupsize = exprMatcher.groupCount();
            if(groupsize > 0) {
                String oldStr = null;
                String expr = null;

                for(int i = 1; i <= groupsize; ++i) {
                    if(i == 1) {
                        oldStr = line.substring(exprMatcher.start(i), exprMatcher.end(i));
                    } else if(i == 2) {
                        expr = line.substring(exprMatcher.start(i), exprMatcher.end(i));
                    }
                }

                line = line.replace(oldStr, calcExpr(expr, request));
            }
        }

        exprMatcher = exprPattern.matcher(line);
        return exprMatcher.find()?checkExpr(line, request):line;
    }

    public static void main(String[] args) {
        String line = "<base href=\"<!--#{request.serverPort1}-->//:<!--#{request.contextPath}-->\">";
        line = checkExpr(line, (HttpServletRequest)null);
        System.out.println(line);
    }

    private static String getStaticFileContent(File f, boolean hasExper, HttpServletRequest request) throws Exception {
        String ext = Utils.getExt(f.getName());
        String text = null;
        BufferedReader br;
        InputStreamReader isr;
        String oldStr;
        if(hasExper) {
            br = null;

            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                StringBuilder var47 = new StringBuilder();
                isr = null;

                String var49;
                for(Pattern var48 = Pattern.compile(".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*"); (var49 = br.readLine()) != null; var47.append(var49 + "\r\n")) {
                    Matcher var50 = var48.matcher(var49);
                    if(var50.find()) {
                        int groupsize = var50.groupCount();
                        if(groupsize > 0) {
                            oldStr = null;
                            String filename = null;

                            for(int f1 = 1; f1 <= groupsize; ++f1) {
                                if(f1 == 1) {
                                    oldStr = var49.substring(var50.start(f1), var50.end(f1));
                                } else if(f1 == 2) {
                                    filename = var49.substring(var50.start(f1), var50.end(f1));
                                }
                            }

                            if(oldStr != null && filename != null) {
                                filename = filename.trim();
                                if(filename.length() > 0) {
                                    if(filename.startsWith("/")) {
                                        filename = filename.substring(1);
                                    }

                                    File var51 = new File(Utils.getWebRootPath() + filename);
                                    if(!var51.exists()) {
                                        throw new Exception("系统找不到文件：" + var51.getAbsolutePath());
                                    }

                                    boolean hasE = hasExpr(var51);
                                    var49 = var49.replace(oldStr, getStaticFileContent(var51, hasE, request));
                                }
                            }
                        }
                    } else {
                        var49 = checkExpr(var49, request);
                    }
                }

                text = var47.toString();
                String var16 = text;
                return var16;
            } catch (Exception var43) {
                throw var43;
            } finally {
                if(br != null) {
                    try {
                        br.close();
                        br = null;
                    } catch (Exception var42) {
                        ;
                    }
                }

            }
        } else if(!"html".equals(ext)) {
            throw new Exception("html里面只能包含【html】，发现未知的文件类型:【" + ext + "】");
        } else {
            br = null;
            FileInputStream fs = null;
            isr = null;

            try {
                fs = new FileInputStream(f);
                isr = new InputStreamReader(fs, "UTF-8");
                br = new BufferedReader(isr);
                StringBuilder e = new StringBuilder();
                String line = null;

                while((line = br.readLine()) != null) {
                    e.append(line + "\r\n");
                }

                text = e.toString();
                oldStr = text;
                return oldStr;
            } catch (Exception var45) {
                throw var45;
            } finally {
                if(fs != null) {
                    try {
                        fs.close();
                    } catch (IOException var41) {
                        ;
                    }

                    fs = null;
                }

                if(isr != null) {
                    try {
                        isr.close();
                    } catch (IOException var40) {
                        ;
                    }

                    isr = null;
                }

                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException var39) {
                        ;
                    }

                    br = null;
                }

            }
        }
    }

    private static String calcExpr(String expr, HttpServletRequest request) {
        if(expr.startsWith("request.")) {
            if(request == null) {
                return expr;
            }

            try {
                return BeanUtils.getSimpleProperty(request, expr.substring(8));
            } catch (Exception var3) {
                Logger.error(var3);
            }
        } else {
            Logger.warn("html文件里面的表达式暂时不支持你的写法【" + expr + "】");
        }

        return "";
    }

    private void responeFile(File file, HttpServletResponse response) throws IOException {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String e = null;

            while((e = reader.readLine()) != null) {
                response.getWriter().println(e);
            }
        } catch (Exception var12) {
            throw var12;
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException var11) {
                    ;
                }
            }

        }

    }

    private void responeFile(String fileContent, HttpServletResponse response) throws IOException {
        try {
            response.getWriter().println(fileContent);
        } catch (Exception var4) {
            throw var4;
        }
    }

    private static boolean hasExpr(File f) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String line = null;
            Pattern pattern = Pattern.compile(".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*");
            Pattern exprPattern = Pattern.compile(".*(<!--#\\{([\\w.]*)\\}-->).*");

            Matcher matcher;
            do {
                if((line = br.readLine()) == null) {
                    return false;
                }

                matcher = pattern.matcher(line);
                if(matcher.find()) {
                    return true;
                }

                matcher = exprPattern.matcher(line);
            } while(!matcher.find());

            return true;
        } catch (Exception var16) {
            return false;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException var15) {
                    ;
                }

                br = null;
            }

        }
    }
}
