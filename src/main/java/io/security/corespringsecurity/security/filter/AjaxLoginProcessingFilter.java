package io.security.corespringsecurity.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.security.model.AjaxLoginUser;
import io.security.corespringsecurity.security.token.AjaxAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public AjaxLoginProcessingFilter(String loginUrl, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(loginUrl));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if (!isAjax(request)) {
            throw new IllegalArgumentException("Authentication is not supported");
        }

        AjaxLoginUser ajaxLoginUser = objectMapper.readValue(request.getInputStream(), AjaxLoginUser.class);

        if (!StringUtils.hasText(ajaxLoginUser.getUsername()) || !StringUtils.hasText(ajaxLoginUser.getPassword())) {
            throw new IllegalArgumentException("Username or Password is Empty");
        }

        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(ajaxLoginUser.getUsername(), ajaxLoginUser.getPassword());

        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

    }
}
