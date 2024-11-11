package io.hhplus.concert.config.interceptor;

import io.hhplus.concert.application.user.UserQueueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class QueueTokenInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(QueueTokenInterceptor.class);

    private final UserQueueService userQueueService;

    public QueueTokenInterceptor(UserQueueService userQueueService) {
        this.userQueueService = userQueueService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getParameter("userId");

        if (userId == null) {
            logger.warn("Request missing token parameter. Rejecting access.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing token");
            return false;
        }

        try {
            //Long queueId = Long.parseLong(queueIdStr);

            if (!userQueueService.isUserInQueue(userId)) {
                logger.warn("Invalid or expired token for userId: {}", userId);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return false;
            }

        } catch (NumberFormatException e) {
            logger.warn("Invalid token format: {}", userId);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid token format");
            return false;
        }

        return true;
    }

}