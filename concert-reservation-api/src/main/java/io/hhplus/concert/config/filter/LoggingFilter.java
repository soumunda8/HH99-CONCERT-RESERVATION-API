package io.hhplus.concert.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpServletResponse);

        logRequest(wrappedRequest); // 요청 시작 로그

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
            logger.info("Request processing completed successfully for URL: {}", wrappedRequest.getRequestURI()); // 최종 성공 로그
        } catch (Exception ex) {
            logger.error("Error during request processing for URL: {}", wrappedRequest.getRequestURI(), ex); // 예외 발생 시 오류 로그
            throw ex;
        } finally {
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        logger.info("Starting request: {} {}", request.getMethod(), request.getRequestURI()); // 최초 요청 로그
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        logger.info("Response status: {}", response.getStatus()); // 최종 응답 상태 로그
    }

    @Override
    public void destroy() {
    }

}