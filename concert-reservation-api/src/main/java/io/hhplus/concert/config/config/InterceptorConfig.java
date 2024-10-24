package io.hhplus.concert.config.config;

import io.hhplus.concert.config.interceptor.QueueTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final QueueTokenInterceptor queueTokenInterceptor;

    public InterceptorConfig(QueueTokenInterceptor queueTokenInterceptor) {
        this.queueTokenInterceptor = queueTokenInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(queueTokenInterceptor)
                .addPathPatterns("/api/queue/**");
    }

}
