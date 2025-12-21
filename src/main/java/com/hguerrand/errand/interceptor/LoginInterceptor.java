// src/main/java/com/hguerrand/errand/interceptor/LoginInterceptor.java
package com.hguerrand.errand.interceptor;

import com.hguerrand.errand.vo.MemberVO;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        MemberVO loginMember =
                (session == null) ? null : (MemberVO) session.getAttribute("loginMember");

        if (loginMember == null) {
            response.sendRedirect(
                    request.getContextPath() + "/auth/login?error=loginRequired"
            );
            return false;
        }

        return true;
    }
}
