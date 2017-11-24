package com.core.server.tools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class NettyUtils {
    private static Map<String, Boolean> fileInfos = new HashMap();
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private static String WebContent = Utils.getRootClassPath() + "/../WebContent";
    private static String WebContentPath;
    private static String RootContent;
    public static final String regx = ".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*";

    static {
        WebContentPath = (new File(WebContent)).getAbsolutePath();
        RootContent = Utils.getRootClassPath() + "..";
    }

    public NettyUtils() {
    }

    public static String trimUrl(String url) {
        if(!url.startsWith("/")) {
            return null;
        } else {
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (UnsupportedEncodingException var4) {
                try {
                    url = URLDecoder.decode(url, "ISO-8859-1");
                } catch (UnsupportedEncodingException var3) {
                    throw new Error();
                }
            }

            return url;
        }
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        String ext = Utils.getExt(file.getName());
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        String contentType = mimeTypesMap.getContentType(file.getPath());
        if("css".equals(ext)) {
            contentType = "text/css";
        } else if("js".equals(ext)) {
            contentType = "text/javascript";
        } else if("shtml".equals(ext)) {
            contentType = "text/html";
        } else if("xml".equals(ext)) {
            contentType = "text/xsl";
        }

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    public static void staticFileFlush(String url, ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        File f = new File(getWebContent() + url);
        String fpath = f.getAbsolutePath();
        String ext = Utils.getExt(f.getName());
        if("html".equals(ext)) {
            boolean e;
            if(!fileInfos.containsKey(fpath)) {
                e = hasExpr(f);
                fileInfos.put(fpath, Boolean.valueOf(e));
            }

            e = Utils.getMapBooleanValue(fileInfos, fpath);
            if(e) {
                String content = getStaticFileContent(f, ctx, e);
                f = new File(getWebContent() + "/temp/" + url);

                try {
                    Utils.saveContent2File(f, content);
                } catch (Exception var11) {
                    writeError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, var11.getMessage());
                }
            }
        }

        try {
            String e1 = f.getAbsolutePath();
            if(e1.startsWith(WebContentPath)) {
                responeFile(ctx, request, f);
            } else {
                throw new Exception("不能访问对应系统文件:" + f.getName());
            }
        } catch (FileNotFoundException var9) {
            writeError(ctx, HttpResponseStatus.NOT_FOUND, var9.getMessage());
        } catch (Exception var10) {
            writeError(ctx, HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION, var10.getMessage());
        }
    }

    private static void responeFile(ChannelHandlerContext ctx, FullHttpRequest request, File file) throws IOException {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException var8) {
            writeError(ctx, HttpResponseStatus.NOT_FOUND, Utils.getErrorStack(var8));
            return;
        }

        Long fileLength = Long.valueOf(raf.length());
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength.longValue());
        setContentTypeHeader(response, file);
        ctx.write(response);
        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        if(ctx.pipeline().get(SslHandler.class) == null) {
            sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0L, fileLength.longValue()), ctx.newProgressivePromise());
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            sendFileFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(raf, 0L, fileLength.longValue(), fileLength.intValue())), ctx.newProgressivePromise());
            lastContentFuture = sendFileFuture;
        }

        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
            }

            public void operationComplete(ChannelProgressiveFuture future) {
            }
        });
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }

    private static boolean hasExpr(File f) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            String line = null;
            Pattern pattern = Pattern.compile(".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*");

            while((line = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()) {
                    return true;
                }
            }
        } catch (Exception var14) {
            ;
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException var13) {
                    ;
                }
            }

            br = null;
        }

        return false;
    }

    private static String getStaticFileContent(File f, ChannelHandlerContext ctx, boolean hasExper) {
        String ext = Utils.getExt(f.getName());
        String text = null;
        BufferedReader br;
        InputStreamReader isr;
        String oldStr;
        if(hasExper) {
            br = null;

            String var16;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                StringBuilder var52 = new StringBuilder();
                isr = null;
                Pattern var53 = Pattern.compile(".*(<!-- +#include +file *= *[\"\']([\\w\\-./]*)[\"\'] *-->).*");

                String var54;
                while((var54 = br.readLine()) != null) {
                    Matcher var55 = var53.matcher(var54);

                    while(var55.find()) {
                        int groupsize = var55.groupCount();
                        if(groupsize > 0) {
                            oldStr = null;
                            String filename = null;

                            for(int f1 = 1; f1 <= groupsize; ++f1) {
                                if(f1 == 1) {
                                    oldStr = var54.substring(var55.start(f1), var55.end(f1));
                                } else if(f1 == 2) {
                                    filename = var54.substring(var55.start(f1), var55.end(f1));
                                }
                            }

                            if(oldStr != null && filename != null) {
                                filename = filename.trim();
                                if(filename.length() > 0) {
                                    if(filename.startsWith("/")) {
                                        filename = filename.substring(1);
                                    }

                                    File var56 = new File(getWebContent() + "/" + filename);
                                    System.out.println(var56.getAbsolutePath());
                                    if(var56.exists()) {
                                        boolean hasE = hasExpr(var56);
                                        var54 = var54.replace(oldStr, getStaticFileContent(var56, ctx, hasE));
                                    } else {
                                        writeError(ctx, HttpResponseStatus.NOT_FOUND, var56.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }

                    var52.append(var54 + "\r\n");
                }

                text = var52.toString();
                var16 = text;
                return var16;
            } catch (Exception var48) {
                writeError(ctx, HttpResponseStatus.NOT_FOUND, var48.getMessage());
                var16 = text;
            } finally {
                if(br != null) {
                    try {
                        br.close();
                        br = null;
                    } catch (Exception var44) {
                        ;
                    }
                }

            }

            return var16;
        } else if(!"html".equals(ext)) {
            writeError(ctx, HttpResponseStatus.NOT_ACCEPTABLE, "html里面只能包含【html】，发现未知的文件类型:【" + ext + "】");
            return text;
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
            } catch (Exception var50) {
                writeError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, var50.getMessage());
            } finally {
                if(fs != null) {
                    try {
                        fs.close();
                    } catch (IOException var47) {
                        ;
                    }

                    fs = null;
                }

                if(isr != null) {
                    try {
                        isr.close();
                    } catch (IOException var46) {
                        ;
                    }

                    isr = null;
                }

                if(br != null) {
                    try {
                        br.close();
                    } catch (IOException var45) {
                        ;
                    }

                    br = null;
                }

            }

            return "";
        }
    }

    public static void writeError(ChannelHandlerContext ctx, HttpResponseStatus status, String err) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n" + err, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void writeResponse(ChannelHandlerContext ctx, HttpRequest request, StringBuilder responseContent, JSONObject obj, boolean keepAlive, ContentType contentType) throws IOException {
        ByteBuf buf = null;
        DefaultFullHttpResponse response = null;
        if(contentType == ContentType.JSON) {
            buf = Unpooled.copiedBuffer(obj.toString(), CharsetUtil.UTF_8);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        } else if(contentType == ContentType.HTML) {
            buf = Unpooled.copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        } else if(contentType == ContentType.XML) {
            Logger.warn("ContentType 等于 XML的情况暂时没有实现");
        } else if(contentType == ContentType.JPG) {
            ByteArrayOutputStream cookies = null;

            try {
                cookies = new ByteArrayOutputStream();
                Utils.createImg(responseContent.toString(), cookies);
                buf = Unpooled.copiedBuffer(cookies.toByteArray());
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/jpeg; charset=UTF-8");
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
            } catch (Exception var18) {
                Logger.error(var18);
            } finally {
                try {
                    if(cookies != null) {
                        cookies.close();
                    }
                } catch (Exception var17) {
                    ;
                }

                cookies = null;
            }
        }

        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        responseContent.setLength(0);
        if(!keepAlive) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        }

        String value = request.headers().get(HttpHeaderNames.COOKIE);
        Set cookies1;
        if(value == null) {
            cookies1 = Collections.emptySet();
        } else {
            cookies1 = ServerCookieDecoder.STRICT.decode(value);
        }

        cookies1.isEmpty();
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if(keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

    }

    public static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static String getWebContent() {
        return WebContent;
    }

    public static String getRootContent() {
        return RootContent;
    }
}
