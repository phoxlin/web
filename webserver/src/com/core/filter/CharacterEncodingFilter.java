package com.core.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        if(response instanceof HttpServletResponse) {
            ((HttpServletResponse)response).setHeader("Cache-Control", "no-cache");
            ((HttpServletResponse)response).setHeader("Pragma", "no-cache");
            ((HttpServletResponse)response).setDateHeader("Expires", -1L);
        }

        filterChain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
