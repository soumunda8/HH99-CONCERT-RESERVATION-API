package io.hhplus.concert.domain.concert;

import org.springframework.context.ApplicationEvent;

public class PaymentCompletedEvent  extends ApplicationEvent {

    private final String userId;

    public PaymentCompletedEvent(Object source, String userId) {
        super(source);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}