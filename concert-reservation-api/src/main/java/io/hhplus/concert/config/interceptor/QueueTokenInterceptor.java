package io.hhplus.concert.config.interceptor;

import io.hhplus.concert.application.user.UserQueueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class QueueTokenInterceptor implements HandlerInterceptor {

    private final UserQueueService userQueueService;

    public QueueTokenInterceptor(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String queueIdStr = request.getParameter("queueId");

        if (queueIdStr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            return false;
        }

        try {
            Long queueId = Long.parseLong(queueIdStr);

            if (!userQueueService.isValidQueueToken(queueId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return false;
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid token format");
            return false;
        }

        return true;
    }

}