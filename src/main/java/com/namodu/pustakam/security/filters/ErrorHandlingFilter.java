//package com.namodu.pustakam.security.filters;
//
//import com.namodu.pustakam.exception.NoPrivilegeException;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * Created by dsanem on 11/18/16.
// */
//@Component
//public class ErrorHandlingFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        try {
//            chain.doFilter(request, response);
//        } catch (NoPrivilegeException npe) {
//            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            httpServletResponse.getWriter().append("{\"message\":\"" + npe.getMessage() + "\"}");
//            httpServletResponse.setContentType("application/json");
//        } catch (Exception ex) {
//
////            request.setAttribute("errorMessage", ex);
////            request.getRequestDispatcher("/WEB-INF/views/jsp/error.jsp")
////                    .forward(request, response);
//
//            httpServletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
//            httpServletResponse.setContentType("application/json");
//            httpServletResponse.setHeader("Location", "http://localhost:8080/home");
//        }
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
