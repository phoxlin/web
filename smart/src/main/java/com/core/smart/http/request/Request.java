package com.core.smart.http.request;

/**
 * 封装请求信息
 * Created by Administrator on 2017/11/9.
 */
public class Request {
    private String requestMethod;
    private String requestPath;

    public Request(String requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (requestMethod != null ? !requestMethod.equals(request.requestMethod) : request.requestMethod != null)
            return false;
        if (requestPath != null ? !requestPath.equals(request.requestPath) : request.requestPath != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = requestMethod != null ? requestMethod.hashCode() : 0;
        result = 31 * result + (requestPath != null ? requestPath.hashCode() : 0);
        return result;
    }
}
