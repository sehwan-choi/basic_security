package io.security.corespringsecurity.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.security.corespringsecurity.security.excpetion.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

// 로그인은 하였지만 해당 리소스에 접근 권한이 없는경우
@RequiredArgsConstructor
public class AjaxAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    private final MessageSource messageSource;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println("AjaxAccessDeniedHandler::");

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            String message = messageSource.getMessage("no.permission", new Object[] {request.getRequestURI()}, request.getLocale());
            objectMapper.writeValue(outputStream, new ErrorResponse(LocalDateTime.now() , request.getRequestURI(), message));
            outputStream.flush();
        }
    }
}
